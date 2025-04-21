package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto;

import lombok.Data;

import java.util.Map;

/**
 * 腹部按诊特征 DTO
 */
@Data
public class AbdomenFeatureDTO {
    /**
     * 腹部按诊部位
     */
    private String abdomenRegion;
    
    /**
     * 腹部按诊部位代码
     */
    private String abdomenRegionCode;
    
    /**
     * 腹部按诊感觉
     */
    private String abdomenSensation;
    
    /**
     * 腹部按诊感觉代码
     */
    private String abdomenSensationCode;
    
    /**
     * 腹部皮温
     */
    private String abdomenTemperature;
    
    /**
     * 腹部皮温代码
     */
    private String abdomenTemperatureCode;
    
    /**
     * 腹部按诊描述
     */
    private String description;
    
    /**
     * 是否有压痛
     */
    private Boolean hasTenderness;
    
    /**
     * 是否有硬结
     */
    private Boolean hasMass;
    
    /**
     * 原始特征数据
     */
    private Map<String, Object> rawFeatures;
}