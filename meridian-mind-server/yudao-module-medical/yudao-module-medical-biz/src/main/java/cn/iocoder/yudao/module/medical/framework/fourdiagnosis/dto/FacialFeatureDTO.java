package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto;

import lombok.Data;

import java.util.Map;

/**
 * 面色特征 DTO
 */
@Data
public class FacialFeatureDTO {

    /**
     * 面色
     */
    private String faceColor;

    /**
     * 色彩饱和度
     */
    private Double colorSaturation;

    /**
     * 色彩亮度
     */
    private Double colorBrightness;

    /**
     * 各区域面色
     */
    private Map<String, Object> regionColors;

    /**
     * 图像URL
     */
    private String imageUrl;

    /**
     * 原始特征数据
     */
    private Map<String, Object> rawFeatures;
    /**
     * 面色代码
     */
    private String faceColorCode;

}