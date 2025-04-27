package cn.iocoder.yudao.module.ai.controller.admin.prompttemplate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * AI 提示词模板渲染 Request DTO
 */
@Data
public class PromptTemplateRenderReqVO {

    @Schema(description = "模板编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "模板编号不能为空")
    private Long templateId;

    @Schema(description = "渲染参数")
    private Map<String, Object> parameters;
}