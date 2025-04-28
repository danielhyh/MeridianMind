package cn.iocoder.yudao.module.medical.controller.admin.diagnosis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 诊断结果新增/修改 Request VO")
@Data
public class DiagnosisSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21525")
    private Long id;

    @Schema(description = "问诊记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2740")
    @NotNull(message = "问诊记录ID不能为空")
    private Long diagnosticId;

    @Schema(description = "主要证型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "主要证型不能为空")
    private String primarySyndrome;

    @Schema(description = "次要证型(JSON数组)")
    private String secondarySyndromes;

    @Schema(description = "八纲辨证(JSON)")
    private String eightPrinciples;

    @Schema(description = "诊断解释", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "诊断解释不能为空")
    private String diagnosisExplanation;

    @Schema(description = "AI原始输出", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "AI原始输出不能为空")
    private String aiRawOutput;

    @Schema(description = "置信度(1-100)")
    private Integer confidenceLevel;

}