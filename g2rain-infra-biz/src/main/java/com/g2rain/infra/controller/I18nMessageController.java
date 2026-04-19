package com.g2rain.infra.controller;

import com.g2rain.common.model.PageData;
import com.g2rain.common.model.PageSelectListDto;
import com.g2rain.common.model.Result;
import com.g2rain.infra.api.I18nMessageApi;
import com.g2rain.infra.dto.I18nMessageDto;
import com.g2rain.infra.dto.I18nMessageSelectDto;
import com.g2rain.infra.service.I18nMessageService;
import com.g2rain.infra.vo.I18nMessageVo;
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
 * 国际化信息表控制器
 * 表名: i18n_message
 *
 * @author G2rain Generator
 */
@RestController
@RequestMapping("/i18n_message")
public class I18nMessageController implements I18nMessageApi {

    @Resource(name = "i18nMessageServiceImpl")
    private I18nMessageService i18nMessageService;

    @Override
    public Result<List<I18nMessageVo>> selectList(I18nMessageSelectDto selectDto) {
        return Result.success(i18nMessageService.selectList(selectDto));
    }

    @Override
    public Result<PageData<I18nMessageVo>> selectPage(PageSelectListDto<I18nMessageSelectDto> selectDto) {
        return Result.successPage(i18nMessageService.selectPage(selectDto));
    }

    /**
     * 新增或更新国际化信息
     *
     * @param dto 国际化信息数据传输对象
     * @return 保存成功后的主键 ID
     */
    @PostMapping("/save")
    @Operation(summary = "新增或更新国际化信息", description = "新增或更新国际化文案信息")
    public Result<Long> save(@RequestBody I18nMessageDto dto) {
        return Result.success(i18nMessageService.save(dto));
    }

    /**
     * 根据主键删除国际化信息
     *
     * @param id 国际化信息主键 ID
     * @return 受影响的记录行数
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除国际化信息记录", description = "根据主键删除国际化信息记录")
    public Result<Integer> delete(@Parameter(description = "国际化信息标识") @PathVariable Long id) {
        return Result.success(i18nMessageService.delete(id));
    }
}
