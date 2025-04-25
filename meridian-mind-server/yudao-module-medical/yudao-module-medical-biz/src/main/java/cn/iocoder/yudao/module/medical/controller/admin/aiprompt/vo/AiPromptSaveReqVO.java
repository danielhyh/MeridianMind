package cn.iocoder.yudao.module.medical.controller.admin.aiprompt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - AI提示词模板新增/修改 Request VO")
@Data
public class AiPromptSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "26269")
    private Long id;

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "模板名称不能为空")
    private String name;

    @Schema(description = "模板描述", example = "随便")
    private String description;

    @Schema(description = "提示词文本", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "提示词文本不能为空")
    private String promptText;

    @Schema(description = "类别:1诊断 2治疗 3健康管理", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "类别:1诊断 2治疗 3健康管理不能为空")
    private Integer category;

    @Schema(description = "参数定义(JSON)")
    private String parameters;

    @Schema(description = "状态:0禁用 1启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态:0禁用 1启用不能为空")
    private Integer status;

}