package cn.iocoder.yudao.module.medical.controller.admin.diagnosistreatment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 中医诊断治疗一体化新增/修改 Request VO")
@Data
public class DiagnosisTreatmentSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31905")
    private Long id;

    @Schema(description = "问诊记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "23192")
    @NotNull(message = "问诊记录ID不能为空")
    private Long diagnosticId;

    @Schema(description = "患者ID", example = "17695")
    private Long userId;

    @Schema(description = "主要证型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "主要证型不能为空")
    private String primarySyndrome;

    @Schema(description = "主要证型解释")
    private String primarySyndromeExplanation;

    @Schema(description = "次要证型JSON数组，包含名称和解释")
    private String secondarySyndromes;

    @Schema(description = "八纲辨证JSON", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "八纲辨证JSON不能为空")
    private String eightPrinciples;

    @Schema(description = "症状详情JSON", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "症状详情JSON不能为空")
    private String symptoms;

    @Schema(description = "辨证分析", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "辨证分析不能为空")
    private String syndromeAnalysis;

    @Schema(description = "治疗原则，如如'健脾益气，养心安神'", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "治疗原则，如如'健脾益气，养心安神'\"不能为空")
    private String treatmentPrinciple;

    @Schema(description = "治疗原则解释")
    private String principleExplanation;

    @Schema(description = "方剂JSON，包含名称、组成、用法等", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "方剂JSON，包含名称、组成、用法等不能为空")
    private String prescriptions;

    @Schema(description = "生活调养建议JSON", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "生活调养建议JSON不能为空")
    private String lifestyleAdvice;

    @Schema(description = "食疗推荐JSON")
    private String dietRecipes;

    @Schema(description = "随访管理JSON")
    private String followUp;

    @Schema(description = "AI原始输出", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "AI原始输出不能为空")
    private String aiRawOutput;

    @Schema(description = "置信度(1-100)")
    private Integer confidenceLevel;

}