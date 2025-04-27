package cn.iocoder.yudao.module.ai.enums.prompt;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI 提示词模板领域枚举
 */
@AllArgsConstructor
@Getter
public enum AiPromptTemplateDomainEnum {

    GENERAL(0, "通用"),
    MEDICAL(1, "医疗"),
    EDUCATION(2, "教育"),
    LEGAL(3, "法律"),
    FINANCE(4, "金融"),
    CUSTOMER_SERVICE(5, "客服");

    /**
     * 领域类型
     */
    private final Integer domain;

    /**
     * 领域名称
     */
    private final String name;
}