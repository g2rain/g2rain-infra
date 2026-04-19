package com.g2rain.infra.api;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.infra.dto.G2rainRaindropSelectDto;
import com.g2rain.infra.vo.G2rainRaindropVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * 全局唯一ID管理表API接口
 * 表名: g2rain_raindrop
 *
 * @author G2rain Generator
 */
@Tag(name = "全局唯一序列", description = "全局唯一 ID 管理相关接口")
public interface G2rainRaindropApi {

    /**
     * 根据条件查询列表
     *
     * @param selectDto 查询条件 DTO
     * @return 数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询全局唯一 ID 记录列表", description = "根据查询条件返回全局唯一 ID 管理记录列表")
    Result<List<G2rainRaindropVo>> selectList(G2rainRaindropSelectDto selectDto);

    /**
     * 根据条件分页查询
     *
     * @param selectDto 查询条件DTO（包含分页参数）
     * @return 分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询全局唯一 ID 记录列表", description = "分页查询全局唯一 ID 管理记录列表")
    Result<PageData<G2rainRaindropVo>> selectPage(PageSelectListDto<G2rainRaindropSelectDto> selectDto);

    /**
     * 获取雪花算法生成的全局唯一 ID
     *
     * <p>
     * 使用雪花算法生成，保证在分布式环境下全局唯一。
     * </p>
     *
     * @return {@link Result} 包含生成的 {@link Long} 类型 ID
     */
    @GetMapping(value = "snowflake")
    @Operation(summary = "获取雪花 ID", description = "使用雪花算法分配全局唯一 ID，适用于分布式场景")
    Result<Long> getSnowflakeId();

    /**
     * 获取业务 ID，可根据业务标签生成
     *
     * <p>
     * 适合业务系统使用，可通过 {@code bizTag} 区分不同业务维度。
     * 不依赖底层实现细节。
     * </p>
     *
     * @param bizTag 可选业务标签
     * @return {@link Result} 包含生成的 {@link Long} 类型业务 ID
     */
    @GetMapping(value = "/business")
    @Operation(summary = "获取业务 ID", description = "按业务标签分配业务维度 ID，bizTag 为空时使用默认策略")
    Result<Long> getBusinessId(@Parameter(description = "可选业务标签，用于区分业务维度") @RequestParam(value = "bizTag", required = false) String bizTag);
}
