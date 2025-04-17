package cn.iocoder.yudao.module.medical.controller.admin.constitution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "管理后台 - 体质评估问卷创建 Request VO")
public class ConstitutionQuestionnaireCreateReqVO {

    @Schema(description = "问卷标题", required = true, example = "中医体质评估问卷")
    @NotEmpty(message = "问卷标题不能为空")
    private String title;

    @Schema(description = "问卷描述", example = "根据《中医体质分类与判定》标准")
    private String description;

    @Schema(description = "问卷版本", required = true, example = "1.0")
    @NotEmpty(message = "问卷版本不能为空")
    private String version;

    @Schema(description = "问卷状态（0-停用 1-启用）", required = true, example = "1")
    @NotNull(message = "问卷状态不能为空")
    private Integer status;
}