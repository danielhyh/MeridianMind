package cn.iocoder.yudao.module.medical.controller.admin.diagnosis.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 诊断结果分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DiagnosisPageReqVO extends PageParam {

    @Schema(description = "问诊记录ID", example = "2740")
    private Long diagnosticId;

    @Schema(description = "主要证型")
    private String primarySyndrome;

    @Schema(description = "次要证型(JSON数组)")
    private String secondarySyndromes;

    @Schema(description = "八纲辨证(JSON)")
    private String eightPrinciples;

    @Schema(description = "诊断解释")
    private String diagnosisExplanation;

    @Schema(description = "AI原始输出")
    private String aiRawOutput;

    @Schema(description = "置信度(1-100)")
    private Integer confidenceLevel;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}