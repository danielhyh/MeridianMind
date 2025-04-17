package cn.iocoder.yudao.module.medical.api.patient.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PatientProfileCreateReqDTO {

    private Long id;

    @NotNull(message = "会员用户ID不能为空")
    private Long userId;

    private BigDecimal height;

    private BigDecimal weight;

    private String bloodType;

    private String allergies;

    private String medicalHistory;

    private String familyMedicalHistory;

    private String constitutionType;

}