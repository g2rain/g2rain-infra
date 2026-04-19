package com.g2rain.infra.components;


import com.g2rain.data.redis.StringRedisHelper;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;


/**
 * <h1>RedisWorkerIdManager</h1>
 *
 * <p>负责分布式环境下 Snowflake 算法的 workerId 分配与管理，保证 workerId 唯一性及租约安全。</p>
 *
 * <h2>功能概述</h2>
 * <ul>
 *   <li>原子抢号：从 Redis pool 随机获取可用 workerId。</li>
 *   <li>懒回收：回收过期租约的 workerId 再次抢占。</li>
 *   <li>心跳续租：定期续租防止 workerId 被抢走。</li>
 *   <li>优雅释放：释放 workerId 并回收到 pool 中。</li>
 * </ul>
 *
 * <h2>设计原则</h2>
 * <ul>
 *   <li>使用 Lua 脚本保证 Redis 操作原子性。</li>
 *   <li>避免 workerId 重复分配，支持高并发环境。</li>
 *   <li>租约 TTL 与心跳间隔合理匹配，保证安全性。</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * RedisWorkerIdManager manager = new RedisWorkerIdManager(redisHelper);
 * long workerId = manager.acquireWorkerId();
 * if (workerId != -1) {
 *     // 成功获取 workerId
 *     boolean renewOk = manager.renewLease(workerId); // 心跳续租
 *     manager.releaseWorkerId(workerId); // 释放
 * }
 * }</pre>
 *
 * @author alpha
 * @since 2025/12/27
 */
@Component
public class RedisWorkerIdManager {

    /**
     * Redis 中存放可用 workerId 的 Set key
     * 在 Redis 集群中，{} 内的内容用作 hash tag，确保包含该 tag 的所有 key 落在同一个 slot，从而保证跨 key 操作的原子性。
     */
    private static final String POOL_KEY = "g2rain:snowflake:{worker}:pool";

    /**
     * workerId 租约 Key 前缀
     * 在 Redis 集群中，{} 内的内容用作 hash tag，确保包含该 tag 的所有 key 落在同一个 slot，从而保证跨 key 操作的原子性。
     */
    private static final String LEASE_PREFIX = "g2rain:snowflake:{worker}:";

    /**
     * 租约 TTL，单位秒
     * <p>
     * 保证心跳周期小于 TTL，避免 workerId 被抢占
     * </p>
     * <p>
     * 设置成39s的意义是 10s续租一次, 最多续租3次。
     * 如果三次都失败, 那么整体消耗时间是37s。
     * 因为考虑到链接超时2s+读写超时5s = 7s
     * 现在是设置了40s是因为最大续租次数:3次
     * </p>
     */
    private static final long LEASE_SECONDS = 40;

    /**
     * 最大 workerId
     */
    private static final int MAX_WORKER_ID = 1023;

    /**
     * Redis 工具，封装常用操作
     */
    private final StringRedisHelper stringRedisHelper;

    /**
     * 当前节点唯一标识，用于租约归属判断
     */
    private final String nodeId = UUID.randomUUID().toString();

    /**
     * 构造函数，注入 Redis 工具
     *
     * @param stringRedisHelper Redis 工具
     */
    public RedisWorkerIdManager(StringRedisHelper stringRedisHelper) {
        this.stringRedisHelper = stringRedisHelper;
    }

    /**
     * 原子抢号方法
     *
     * <p>流程：</p>
     * <ol>
     *   <li>判断 pool 是否存在，不存在则初始化 0~MAX_WORKER_ID。</li>
     *   <li>尝试 SPOP 弹出 workerId 并设置租约。</li>
     *   <li>如果第一次抢号失败，检查过期租约，回收可用 workerId 再抢一次。</li>
     *   <li>返回抢到的 workerId，若仍未获取到则返回 -1。</li>
     * </ol>
     *
     * @return 成功返回 workerId，失败返回 -1
     */
    public Long acquireWorkerId() {
        String lua = """
            -- KEYS[1] = pool key
            -- ARGV[1] = nodeId
            -- ARGV[2] = TTL 秒数
            -- ARGV[3] = 最大 workerId
            -- ARGV[4] = 租约 key 前缀

            local function pop_and_set()
                local id = redis.call('SPOP', KEYS[1])
                if id then
                    redis.call('SET', ARGV[4] .. id, ARGV[1], 'EX', ARGV[2])
                end
                return id
            end

            -- pool 是否存在，不存在初始化
            if redis.call('EXISTS', KEYS[1]) == 0 then
                for i = 0, tonumber(ARGV[3]) do
                    redis.call('SADD', KEYS[1], i)
                end
            end

            -- 第一次抢号
            local workerId = pop_and_set()

            -- 如果第一次抢号失败，回收过期租约再抢一次
            if not workerId then
                for i = 0, tonumber(ARGV[3]) do
                    local leaseKey = ARGV[4] .. i
                    if not redis.call('GET', leaseKey) then
                        redis.call('SADD', KEYS[1], i)
                    end
                end
                workerId = pop_and_set()
            end

            -- 返回 workerId 或 -1
            if not workerId then
                return -1
            else
                return tonumber(workerId)
            end
            """;

        return stringRedisHelper.execute(
            new DefaultRedisScript<>(lua, Long.class),
            List.of(POOL_KEY),
            nodeId,
            String.valueOf(LEASE_SECONDS),
            String.valueOf(MAX_WORKER_ID),
            LEASE_PREFIX
        );
    }

    /**
     * 心跳续租方法
     *
     * <p>仅在当前节点持有租约时续租，保证 workerId 不被抢占。</p>
     *
     * @param workerId 当前节点持有的 workerId
     * @return true 续租成功，false 续租失败
     */
    public boolean renewLease(long workerId) {
        String leaseKey = LEASE_PREFIX + workerId;
        String lua = """
            if redis.call('GET', KEYS[1]) == ARGV[1] then
                return redis.call('EXPIRE', KEYS[1], ARGV[2])
            else
                return 0
            end
            """;

        Long ret = stringRedisHelper.execute(
            new DefaultRedisScript<>(lua, Long.class),
            List.of(leaseKey),
            nodeId,
            String.valueOf(LEASE_SECONDS)
        );
        return ret > 0;
    }

    /**
     * 优雅释放 workerId
     *
     * <p>仅删除当前节点持有的租约，并回收到 pool。</p>
     *
     * @param workerId 当前节点持有的 workerId
     * @return true 释放成功，false 当前节点未持有租约
     */
    public boolean releaseWorkerId(long workerId) {
        String lua = """
            if redis.call('GET', KEYS[1]) == ARGV[1] then
                redis.call('DEL', KEYS[1])
                redis.call('SADD', KEYS[2], ARGV[2])
                return 1
            else
                return 0
            end
            """;

        Long ok = stringRedisHelper.execute(
            new DefaultRedisScript<>(lua, Long.class),
            List.of(LEASE_PREFIX + workerId, POOL_KEY),
            nodeId,
            String.valueOf(workerId)
        );

        return ok > 0;
    }
}
