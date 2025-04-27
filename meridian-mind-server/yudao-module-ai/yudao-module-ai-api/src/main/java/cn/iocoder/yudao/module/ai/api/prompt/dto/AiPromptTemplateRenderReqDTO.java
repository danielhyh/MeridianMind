package cn.iocoder.yudao.module.ai.api.prompt.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * AI 提示词模板渲染 Request DTO
 */
@Data
public class AiPromptTemplateRenderReqDTO {

    /**
     * 模板编号
     */
    @NotNull(message = "模板编号不能为空")
    private Long templateId;

    /**
     * 渲染参数
     */
    private Map<String, Object> parameters;
}