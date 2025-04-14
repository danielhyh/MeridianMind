package cn.iocoder.yudao.module.medical.controller.app.patient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 患者管理新增/修改 Request VO")
@Data
public class AppPatientProfileSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "32617")
    private Long id;

    @Schema(description = "会员用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "28025")
    @NotNull(message = "会员用户ID不能为空")
    private Long memberUserId;

    @Schema(description = "身高(cm)")
    private BigDecimal height;

    @Schema(description = "体重(kg)")
    private BigDecimal weight;

    @Schema(description = "血型", example = "1")
    private String bloodType;

    @Schema(description = "过敏史")
    private String allergies;

    @Schema(description = "既往病史")
    private String medicalHistory;

    @Schema(description = "家族病史")
    private String familyMedicalHistory;

    @Schema(description = "中医体质类型", example = "2")
    private String constitutionType;

}