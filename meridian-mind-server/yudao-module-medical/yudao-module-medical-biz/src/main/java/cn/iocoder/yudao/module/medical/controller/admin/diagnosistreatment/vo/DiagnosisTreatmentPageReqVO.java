package cn.iocoder.yudao.module.medical.controller.admin.diagnosistreatment.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 中医诊断治疗一体化分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DiagnosisTreatmentPageReqVO extends PageParam {

    @Schema(description = "问诊记录ID", example = "23192")
    private Long diagnosticId;

    @Schema(description = "患者ID", example = "17695")
    private Long userId;

    @Schema(description = "主要证型")
    private String primarySyndrome;

    @Schema(description = "主要证型解释")
    private String primarySyndromeExplanation;

    @Schema(description = "次要证型JSON数组，包含名称和解释")
    private String secondarySyndromes;

    @Schema(description = "八纲辨证JSON")
    private String eightPrinciples;

    @Schema(description = "症状详情JSON")
    private String symptoms;

    @Schema(description = "辨证分析")
    private String syndromeAnalysis;

    @Schema(description = "治疗原则，如健脾益气，养心安神")
    private String treatmentPrinciple;

    @Schema(description = "治疗原则解释")
    private String principleExplanation;

    @Schema(description = "方剂JSON，包含名称、组成、用法等")
    private String prescriptions;

    @Schema(description = "生活调养建议JSON")
    private String lifestyleAdvice;

    @Schema(description = "食疗推荐JSON")
    private String dietRecipes;

    @Schema(description = "随访管理JSON")
    private String followUp;

    @Schema(description = "AI原始输出")
    private String aiRawOutput;

    @Schema(description = "置信度(1-100)")
    private Integer confidenceLevel;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}