package com.g2rain.infra.controller;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.infra.api.G2rainRaindropApi;
import com.g2rain.infra.dto.G2rainRaindropDto;
import com.g2rain.infra.dto.G2rainRaindropSelectDto;
import com.g2rain.infra.enums.KeysmithType;
import com.g2rain.infra.service.G2rainRaindropService;
import com.g2rain.infra.vo.G2rainRaindropVo;
import com.g2rain.web.interceptors.annotations.LoginGuard;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 全局唯一ID管理表控制器
 * 表名: g2rain_raindrop
 *
 * @author G2rain Generator
 */
@RestController
@RequestMapping("/g2rain_raindrop")
public class G2rainRaindropController implements G2rainRaindropApi {

    @Resource(name = "g2rainRaindropServiceImpl")
    private G2rainRaindropService g2rainRaindropService;

    @Override
    public Result<List<G2rainRaindropVo>> selectList(G2rainRaindropSelectDto selectDto) {
        return Result.success(g2rainRaindropService.selectList(selectDto));
    }

    @Override
    public Result<PageData<G2rainRaindropVo>> selectPage(PageSelectListDto<G2rainRaindropSelectDto> selectDto) {
        return Result.successPage(g2rainRaindropService.selectPage(selectDto));
    }

    @Override
    @LoginGuard(require = false)
    public Result<Long> getSnowflakeId() {
        return Result.success(g2rainRaindropService.allocate(KeysmithType.SNOWFLAKE, null));
    }

    @Override
    @LoginGuard(require = false)
    public Result<Long> getBusinessId(String bizTag) {
        return Result.success(g2rainRaindropService.allocate(KeysmithType.SEGMENT, bizTag));
    }

    /**
     * 新增或更新全局唯一 ID 管理记录
     *
     * @param dto 数据传输对象
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新全局唯一 ID 记录", description = "新增或更新全局唯一 ID 管理表数据")
    public Result<Long> save(@RequestBody G2rainRaindropDto dto) {
        return Result.success(g2rainRaindropService.save(dto));
    }

    /**
     * 根据主键删除记录
     *
     * @param id 主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除全局唯一 ID 记录", description = "根据主键删除全局唯一 ID 管理记录")
    public Result<Integer> delete(@Parameter(description = "记录标识") @PathVariable Long id) {
        return Result.success(g2rainRaindropService.delete(id));
    }
}
