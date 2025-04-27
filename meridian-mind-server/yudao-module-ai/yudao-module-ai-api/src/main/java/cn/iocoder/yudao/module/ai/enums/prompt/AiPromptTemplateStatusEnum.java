package cn.iocoder.yudao.module.ai.enums.prompt;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI 提示词模板状态枚举
 */
@AllArgsConstructor
@Getter
public enum AiPromptTemplateStatusEnum {

    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    /**
     * 状态
     */
    private final Integer status;

    /**
     * 状态名
     */
    private final String name;
}