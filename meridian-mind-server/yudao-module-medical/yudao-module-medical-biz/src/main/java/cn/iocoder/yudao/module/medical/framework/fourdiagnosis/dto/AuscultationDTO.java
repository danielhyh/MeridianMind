package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto;

import lombok.Data;

@Data
public class AuscultationDTO {
    /**
     * 声特征
     */
    private VoiceFeatureDTO voiceFeature;
    /**
     * 味特征
     */
    private OdorFeatureDTO odorFeature;
}
