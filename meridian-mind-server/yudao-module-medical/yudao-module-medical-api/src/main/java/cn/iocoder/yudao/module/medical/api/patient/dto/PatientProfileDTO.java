package cn.iocoder.yudao.module.medical.api.patient.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 患者健康档案 DTO
 */
@Data
public class PatientProfileDTO implements Serializable {
    
    /**
     * 主键
     */
    private Long id;
    
    /**
     * 会员用户ID
     */
    private Long userId;
    
    /**
     * 身高(cm)
     */
    private BigDecimal height;
    
    /**
     * 体重(kg)
     */
    private BigDecimal weight;
    
    /**
     * 血型
     */
    private String bloodType;
    
    /**
     * 过敏史
     */
    private String allergies;
    
    /**
     * 既往病史
     */
    private String medicalHistory;
    
    /**
     * 家族病史
     */
    private String familyMedicalHistory;
    
    /**
     * 中医体质类型
     */
    private String constitutionType;
}