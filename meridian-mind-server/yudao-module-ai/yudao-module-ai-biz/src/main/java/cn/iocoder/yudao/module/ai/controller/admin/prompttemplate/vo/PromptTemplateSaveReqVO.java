package cn.iocoder.yudao.module.ai.controller.admin.prompttemplate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - AI提示词模板新增/修改 Request VO")
@Data
public class PromptTemplateSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "28216")
    private Long id;

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "模板名称不能为空")
    private String name;

    @Schema(description = "模板描述", example = "你猜")
    private String description;

    @Schema(description = "提示词文本", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "提示词文本不能为空")
    private String content;

    @Schema(description = "参数定义(JSON)")
    private String parameters;

    @Schema(description = "领域类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "领域类型不能为空")
    private Integer domain;

    @Schema(description = "状态:0禁用 1启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态:0禁用 1启用不能为空")
    private Integer status;

    @Schema(description = "默认模型编号", example = "518")
    private Long defaultModelId;

    @Schema(description = "默认知识库编号", example = "18915")
    private Long defaultKnowledgeId;

}