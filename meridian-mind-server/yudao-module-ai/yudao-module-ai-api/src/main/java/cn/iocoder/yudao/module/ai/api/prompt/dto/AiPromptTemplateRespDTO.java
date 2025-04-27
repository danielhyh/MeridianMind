package cn.iocoder.yudao.module.ai.api.prompt.dto;

import lombok.Data;

/**
 * AI 提示词模板 Response DTO
 */
@Data
public class AiPromptTemplateRespDTO {

    /**
     * 模板编号
     */
    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 提示词内容
     */
    private String content;

    /**
     * 参数定义，JSON格式
     */
    private String parameters;
    /**
     * 领域类型
     */
    private Integer domain;

    /**
     * 默认模型编号
     */
    private Long defaultModelId;

    /**
     * 默认知识库编号
     */
    private Long defaultKnowledgeId;
}