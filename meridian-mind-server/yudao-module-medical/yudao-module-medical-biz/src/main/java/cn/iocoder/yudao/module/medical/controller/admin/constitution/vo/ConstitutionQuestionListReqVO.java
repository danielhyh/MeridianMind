package cn.iocoder.yudao.module.medical.controller.admin.constitution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "管理后台 - 体质评估问题查询 Request VO")
public class ConstitutionQuestionListReqVO {

    @Schema(description = "问卷ID", required = true, example = "1024")
    @NotNull(message = "问卷ID不能为空")
    private Long questionnaireId;

    @Schema(description = "问题内容", example = "您是否容易疲劳？")
    private String question;

    @Schema(description = "体质类型", example = "qi_deficiency")
    private String constitutionType;
}