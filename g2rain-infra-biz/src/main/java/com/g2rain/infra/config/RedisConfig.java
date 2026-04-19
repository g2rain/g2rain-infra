package com.g2rain.infra.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类，用于自定义 {@link RedisTemplate} 的序列化方式。
 * <p>
 * 默认 RedisTemplate 对象的 key 和 value 序列化方式为：
 * <ul>
 *     <li>Key / HashKey：字符串序列化 ({@link StringRedisSerializer})</li>
 *     <li>Value / HashValue：JSON 序列化 ({@link RedisSerializer})</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * @Autowired
 * private RedisTemplate<String, Object> redisTemplate;
 *
 * // 写入数据
 * redisTemplate.opsForValue().set("user:1", new User(1, "Alice"));
 *
 * // 读取数据
 * User user = (User) redisTemplate.opsForValue().get("user:1");
 * }</pre>
 * </p>
 * <p>
 * 这样可以保证 Redis 中的数据可读性，并且兼容复杂对象存储。
 * <p>
 * 注意：Redis 连接工厂由 Spring Boot 自动配置，连接池配置通过 application.yml 中的
 * spring.data.redis.lettuce.pool.* 属性进行配置。
 * </p>
 *
 * @author alpha
 * @since 2025/10/13
 */
@Slf4j
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    /**
     * 自定义 RedisTemplate Bean。
     * <p>
     * 配置 key 使用字符串序列化，value 使用 JSON 序列化，保证数据存取的一致性和可读性。
     * </p>
     *
     * @param factory Redis 连接工厂，由 Spring Boot 自动注入
     * @return 配置好的 {@link RedisTemplate} 实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 记录 Redis 配置信息
        log.info("Redis 配置 - Host: {}, Port: {}", redisHost, redisPort);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // key 使用字符串序列化
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // value 使用 JSON 序列化
        RedisSerializer<@NonNull Object> jsonSerializer = RedisSerializer.json();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
