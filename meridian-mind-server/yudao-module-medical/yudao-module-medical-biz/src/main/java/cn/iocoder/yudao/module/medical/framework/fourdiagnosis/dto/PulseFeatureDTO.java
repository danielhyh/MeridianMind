package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto;

import lombok.Data;

import java.util.Map;

/**
 * 脉象特征 DTO
 */
@Data
public class PulseFeatureDTO {
    /**
     * 脉象类型
     */
    private String pulseType;
    
    /**
     * 脉象类型代码
     */
    private String pulseTypeCode;
    
    /**
     * 脉象强度
     */
    private String pulseStrength;
    
    /**
     * 脉象强度代码
     */
    private String pulseStrengthCode;
    
    /**
     * 脉搏节律
     */
    private String pulseRhythm;
    
    /**
     * 脉搏节律代码
     */
    private String pulseRhythmCode;
    
    /**
     * 脉率（每分钟）
     */
    private Integer pulseRate;
    
    /**
     * 脉象描述
     */
    private String description;
    
    /**
     * 原始特征数据
     */
    private Map<String, Object> rawFeatures;
}