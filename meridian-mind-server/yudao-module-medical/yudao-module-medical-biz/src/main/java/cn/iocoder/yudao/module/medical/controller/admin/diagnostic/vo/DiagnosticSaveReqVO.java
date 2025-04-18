package cn.iocoder.yudao.module.medical.controller.admin.diagnostic.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 问诊记录新增/修改 Request VO")
@Data
public class DiagnosticSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "8655")
    private Long id;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "12356")
    @NotNull(message = "患者ID不能为空")
    private Long userId;

    @Schema(description = "医生ID（预留）", example = "13194")
    private Long doctorId;

    @Schema(description = "问诊时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "问诊时间不能为空")
    private LocalDateTime diagnosticTime;

    @Schema(description = "主诉", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "主诉不能为空")
    private String chiefComplaint;

    @Schema(description = "发病时间")
    private String onsetTime;

    @Schema(description = "病程发展")
    private String diseaseCourse;

    @Schema(description = "状态：0进行中 1已完成 2已取消", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "状态：0进行中 1已完成 2已取消不能为空")
    private Integer status;

}