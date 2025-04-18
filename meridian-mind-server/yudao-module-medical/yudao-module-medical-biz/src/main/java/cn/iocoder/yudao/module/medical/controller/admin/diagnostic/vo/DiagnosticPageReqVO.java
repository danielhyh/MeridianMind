package cn.iocoder.yudao.module.medical.controller.admin.diagnostic.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 问诊记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DiagnosticPageReqVO extends PageParam {

    @Schema(description = "患者ID", example = "12356")
    private Long userId;

    @Schema(description = "医生ID（预留）", example = "13194")
    private Long doctorId;

    @Schema(description = "问诊时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] diagnosticTime;

    @Schema(description = "主诉")
    private String chiefComplaint;

    @Schema(description = "发病时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private String[] onsetTime;

    @Schema(description = "病程发展")
    private String diseaseCourse;

    @Schema(description = "状态：0进行中 1已完成 2已取消", example = "2")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}