package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto;

import lombok.Data;

@Data
public class AuscultationDTO {
    private VoiceFeatureDTO voiceFeature;
    private OdorFeatureDTO odorFeature;
}
