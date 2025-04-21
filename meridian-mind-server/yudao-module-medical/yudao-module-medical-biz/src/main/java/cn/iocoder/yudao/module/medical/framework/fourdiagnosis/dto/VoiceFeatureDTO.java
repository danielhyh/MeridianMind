package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto;

import lombok.Data;

import java.util.Map;

/**
 * 语音特征 DTO
 */
@Data
public class VoiceFeatureDTO {

    /**
     * 声音强度
     */
    private String strength;

    /**
     * 音调
     */
    private String tone;

    /**
     * 节奏
     */
    private String rhythm;

    /**
     * 呼吸特点
     */
    private String breathPattern;

    /**
     * 音频URL
     */
    private String audioUrl;

    /**
     * 原始特征数据
     */
    private Map<String, Object> rawFeatures;
    /**
     * 声音强度代码
     */
    private String strengthCode;
    /**
     * 音调代码
     */
    private String toneCode;
    /**
     * 节奏代码
     */
    private String breathPatternCode;
}