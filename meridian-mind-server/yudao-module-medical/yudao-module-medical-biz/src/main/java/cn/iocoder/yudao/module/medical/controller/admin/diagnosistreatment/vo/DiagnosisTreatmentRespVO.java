package cn.iocoder.yudao.module.medical.controller.admin.diagnosistreatment.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 中医诊断治疗一体化 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DiagnosisTreatmentRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31905")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "问诊记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "23192")
    @ExcelProperty("问诊记录ID")
    private Long diagnosticId;

    @Schema(description = "患者ID", example = "17695")
    @ExcelProperty("患者ID")
    private Long userId;

    @Schema(description = "主要证型", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("主要证型")
    private String primarySyndrome;

    @Schema(description = "主要证型解释")
    @ExcelProperty("主要证型解释")
    private String primarySyndromeExplanation;

    @Schema(description = "次要证型JSON数组，包含名称和解释")
    @ExcelProperty("次要证型JSON数组，包含名称和解释")
    private String secondarySyndromes;

    @Schema(description = "八纲辨证JSON", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("八纲辨证JSON")
    private String eightPrinciples;

    @Schema(description = "症状详情JSON", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("症状详情JSON")
    private String symptoms;

    @Schema(description = "辨证分析", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("辨证分析")
    private String syndromeAnalysis;

    @Schema(description = "治疗原则，如'健脾益气，养心安神'", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("治疗原则，如'健脾益气，养心安神'")
    private String treatmentPrinciple;

    @Schema(description = "治疗原则解释")
    @ExcelProperty("治疗原则解释")
    private String principleExplanation;

    @Schema(description = "方剂JSON，包含名称、组成、用法等", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("方剂JSON，包含名称、组成、用法等")
    private String prescriptions;

    @Schema(description = "生活调养建议JSON", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("生活调养建议JSON")
    private String lifestyleAdvice;

    @Schema(description = "食疗推荐JSON")
    @ExcelProperty("食疗推荐JSON")
    private String dietRecipes;

    @Schema(description = "随访管理JSON")
    @ExcelProperty("随访管理JSON")
    private String followUp;

    @Schema(description = "AI原始输出", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("AI原始输出")
    private String aiRawOutput;

    @Schema(description = "置信度(1-100)")
    @ExcelProperty("置信度(1-100)")
    private Integer confidenceLevel;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}