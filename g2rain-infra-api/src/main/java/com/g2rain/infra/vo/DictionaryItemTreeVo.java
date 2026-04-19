package com.g2rain.infra.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 树形字典
 *
 * @author jagger
 * @since 2026/4/13-08:48
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "树形字典显示 VO")
public class DictionaryItemTreeVo extends DictionaryItemVo{

    @Schema(description = "子节点列表")
    private List<DictionaryItemTreeVo> children;
}
