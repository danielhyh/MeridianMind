package cn.iocoder.yudao.module.medical.controller.app.diagnostic.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 APP - 问诊记录新增/修改 Request VO")
@Data
public class AppDiagnosticSaveReqVO {

    @Schema(description = "主键", example = "1024")
    private Long id;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    @NotNull(message = "患者ID不能为空")
    private Long userId;

    @Schema(description = "医生ID", example = "3072")
    private Long doctorId;

    @Schema(description = "问诊时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "问诊时间不能为空")
    private LocalDateTime diagnosticTime;

    @Schema(description = "主诉", requiredMode = Schema.RequiredMode.REQUIRED, example = "头痛、发热")
    @NotNull(message = "主诉不能为空")
    @Size(max = 500, message = "主诉不能超过500个字符")
    private String chiefComplaint;

    @Schema(description = "发病时间", example = "3天前")
    @Size(max = 50, message = "发病时间不能超过50个字符")
    private String onsetTime;

    @Schema(description = "病程发展", example = "逐渐加重")
    @Size(max = 100, message = "病程发展不能超过100个字符")
    private String diseaseCourse;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "状态不能为空")
    private Integer status;

}