package cn.iocoder.yudao.module.medical.controller.admin.diagnosis.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 诊断结果 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DiagnosisRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21525")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "问诊记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2740")
    @ExcelProperty("问诊记录ID")
    private Long diagnosticId;

    @Schema(description = "主要证型", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("主要证型")
    private String primarySyndrome;

    @Schema(description = "次要证型(JSON数组)")
    @ExcelProperty("次要证型(JSON数组)")
    private String secondarySyndromes;

    @Schema(description = "八纲辨证(JSON)")
    @ExcelProperty("八纲辨证(JSON)")
    private String eightPrinciples;

    @Schema(description = "诊断解释", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("诊断解释")
    private String diagnosisExplanation;

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