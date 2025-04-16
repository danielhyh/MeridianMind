package cn.iocoder.yudao.module.medical.controller.app.diagnostic.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 问诊记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AppDiagnosticRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "2926")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "29521")
    @ExcelProperty("患者ID")
    private Long userId;

    @Schema(description = "医生ID（预留）", example = "10572")
    @ExcelProperty("医生ID（预留）")
    private Long doctorId;

    @Schema(description = "问诊时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("问诊时间")
    private LocalDateTime diagnosticTime;

    @Schema(description = "主诉", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("主诉")
    private String chiefComplaint;

    @Schema(description = "现病史")
    @ExcelProperty("现病史")
    private String illnessHistory;

    @Schema(description = "既往史")
    @ExcelProperty("既往史")
    private String medicalHistory;

    @Schema(description = "状态：0进行中 1已完成 2已取消", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("状态：0进行中 1已完成 2已取消")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}