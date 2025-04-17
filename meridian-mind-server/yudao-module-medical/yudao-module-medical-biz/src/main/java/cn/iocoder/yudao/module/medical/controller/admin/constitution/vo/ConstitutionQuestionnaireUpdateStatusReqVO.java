package cn.iocoder.yudao.module.medical.controller.admin.constitution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "管理后台 - 体质评估问卷更新 Request VO")
public class ConstitutionQuestionnaireUpdateStatusReqVO {

    @Schema(description = "问卷ID", required = true, example = "1024")
    @NotNull(message = "问卷ID不能为空")
    private Long id;

    @Schema(description = "问卷状态（0-停用 1-启用）", required = true, example = "1")
    @NotNull(message = "问卷状态不能为空")
    private Integer status;
}