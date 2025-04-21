package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto;

import lombok.Data;

import java.util.Map;

/**
 * 舌象特征 DTO
 */
@Data
public class TongueFeatureDTO {

    /**
     * 舌质颜色
     */
    private String tongueColor;

    /**
     * 舌质颜色代码
     */
    private String tongueColorCode;

    /**
     * 舌苔特征
     */
    private String tongueCoating;

    /**
     * 舌苔特征代码
     */
    private String tongueCoatingCode;

    /**
     * 舌形特征
     */
    private String tongueShape;

    /**
     * 是否有裂纹
     */
    private Boolean hasCrack;

    /**
     * 是否有齿痕
     */
    private Boolean hasToothMark;

    /**
     * 湿润度
     */
    private Double moisture;

    /**
     * 图像URL
     */
    private String imageUrl;

    /**
     * 原始特征数据
     */
    private Map<String, Object> rawFeatures;
}