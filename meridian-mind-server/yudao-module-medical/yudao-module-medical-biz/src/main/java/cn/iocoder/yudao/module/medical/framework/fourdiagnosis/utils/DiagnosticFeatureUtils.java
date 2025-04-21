package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.utils;

import cn.iocoder.yudao.module.system.api.dict.DictDataApi;
import cn.iocoder.yudao.module.system.api.dict.dto.DictDataRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 四诊特征判断工具类
 */
@Component
@Slf4j
public class DiagnosticFeatureUtils {

    // 缓存字典数据，避免频繁查询
    private final Map<String, List<DictDataRespDTO>> dictCache = new ConcurrentHashMap<>();
    @Resource
    private DictDataApi dictDataApi;

    /**
     * 根据舌象特征判断舌质颜色
     */
    public String determineTongueColor(double hueMean, double saturationMean, double valueMean) {
        // 判断逻辑，从Python服务迁移过来
        if (valueMean < 100) {
            return getDictValue("medical_tongue_color", "purple");  // 紫舌
        } else if (hueMean < 10 || hueMean > 170) {
            if (saturationMean > 150) {
                return getDictValue("medical_tongue_color", "crimson");  // 绛舌
            } else {
                return getDictValue("medical_tongue_color", "red");  // 红舌
            }
        } else if (10 <= hueMean && hueMean <= 30) {
            if (valueMean < 150) {
                return getDictValue("medical_tongue_color", "red");  // 红舌
            } else {
                return getDictValue("medical_tongue_color", "pale_red");  // 淡红舌
            }
        } else {
            return getDictValue("medical_tongue_color", "pale_white");  // 淡白舌
        }
    }

    /**
     * 根据舌象特征判断舌苔特征
     */
    public String determineTongueCoating(double saturationMean, double valueMean) {
        // 判断逻辑，从Python服务迁移过来
        if (valueMean < 80) {
            return getDictValue("medical_tongue_coating", "black");  // 黑苔
        } else if (valueMean < 120) {
            return getDictValue("medical_tongue_coating", "grey");  // 灰苔
        } else if (saturationMean < 50) {
            if (valueMean > 180) {
                return getDictValue("medical_tongue_coating", "thin_white");  // 薄白苔
            } else {
                return getDictValue("medical_tongue_coating", "thick_white");  // 厚白苔
            }
        } else if (50 <= saturationMean && saturationMean <= 100) {
            if (valueMean > 180) {
                return getDictValue("medical_tongue_coating", "thin_yellow");  // 薄黄苔
            } else {
                return getDictValue("medical_tongue_coating", "thick_yellow");  // 厚黄苔
            }
        } else {
            return getDictValue("medical_tongue_coating", "greasy");  // 腻苔
        }
    }

    /**
     * 根据面色特征判断面色
     */
    public String determineFaceColor(double hueMean, double saturationMean, double valueMean) {
        // 判断逻辑，从Python服务迁移过来
        if (valueMean < 100) {
            if (hueMean < 30 || hueMean > 330) {
                return getDictValue("medical_face_color", "dark_red");  // 暗红
            } else {
                return getDictValue("medical_face_color", "dark_black");  // 暗黑
            }
        } else if (saturationMean < 50) {
            if (valueMean < 180) {
                return getDictValue("medical_face_color", "grey_white");  // 灰白
            } else {
                return getDictValue("medical_face_color", "pale");  // 苍白
            }
        } else if (0 <= hueMean && hueMean <= 30 || 330 <= hueMean && hueMean <= 360) {
            if (saturationMean > 150) {
                return getDictValue("medical_face_color", "bright_red");  // 鲜红
            } else {
                return getDictValue("medical_face_color", "pale_red");  // 淡红
            }
        } else if (30 < hueMean && hueMean <= 60) {
            return getDictValue("medical_face_color", "yellow");  // 黄色
        } else if (60 < hueMean && hueMean <= 150) {
            return getDictValue("medical_face_color", "green");  // 青色
        } else if (150 < hueMean && hueMean <= 270) {
            return getDictValue("medical_face_color", "blue_purple");  // 青紫
        } else {
            return getDictValue("medical_face_color", "pale_purple");  // 淡紫
        }
    }

    /**
     * 根据语音特征判断声音强度
     */
    public String determineVoiceStrength(double rmsMean) {
        // 根据RMS均值判断声音强度
        if (rmsMean < 0.05) {
            return getDictValue("medical_voice_strength", "weak");  // 弱
        } else if (rmsMean > 0.15) {
            return getDictValue("medical_voice_strength", "strong");  // 强
        } else {
            return getDictValue("medical_voice_strength", "medium");  // 中等
        }
    }

    /**
     * 根据语音特征判断音调
     */
    public String determineVoiceTone(double spectralCentroidMean) {
        // 根据谱质心判断音调
        if (spectralCentroidMean < 1000) {
            return getDictValue("medical_voice_tone", "low");  // 低沉
        } else if (spectralCentroidMean > 2000) {
            return getDictValue("medical_voice_tone", "high");  // 高亢
        } else {
            return getDictValue("medical_voice_tone", "medium");  // 中等
        }
    }

    /**
     * 根据语音特征判断呼吸特点
     */
    public String determineBreathPattern(double rmsMean, double zcrMean) {
        // 根据RMS和过零率判断呼吸特点
        if (rmsMean < 0.03) {
            return getDictValue("medical_breath_pattern", "weak");  // 微弱
        } else if (rmsMean > 0.2 && zcrMean > 0.1) {
            return getDictValue("medical_breath_pattern", "heavy");  // 粗重
        } else if (zcrMean > 0.15) {
            return getDictValue("medical_breath_pattern", "short");  // 短促
        } else {
            return getDictValue("medical_breath_pattern", "normal");  // 正常
        }
    }

    /**
     * 根据脉搏频率判断脉象类型
     */
    public String determinePulseTypeByRate(int pulseRate) {
        if (pulseRate < 60) {
            return getDictValue("medical_pulse_type", "slow");  // 迟脉
        } else if (pulseRate > 90) {
            return getDictValue("medical_pulse_type", "rapid");  // 数脉
        } else {
            return null;  // 返回null表示仅凭脉率无法判断脉象类型
        }
    }

    /**
     * 根据脉搏强度特征判断脉象类型
     */
    public String determinePulseTypeByStrength(double strengthValue, double regularityValue) {
        if (strengthValue < 0.3) {
            return getDictValue("medical_pulse_type", "weak");  // 虚脉
        } else if (strengthValue > 0.7) {
            if (regularityValue > 0.8) {
                return getDictValue("medical_pulse_type", "strong");  // 实脉
            } else {
                return getDictValue("medical_pulse_type", "wiry");  // 弦脉
            }
        } else {
            return null;  // 仅凭强度无法确定
        }
    }

    /**
     * 判断脉象强度
     */
    public String determinePulseStrength(double strengthValue) {
        if (strengthValue < 0.2) {
            return getDictValue("medical_pulse_strength", "very_weak");  // 微弱
        } else if (strengthValue < 0.4) {
            return getDictValue("medical_pulse_strength", "weak");  // 较弱
        } else if (strengthValue < 0.6) {
            return getDictValue("medical_pulse_strength", "medium");  // 中等
        } else if (strengthValue < 0.8) {
            return getDictValue("medical_pulse_strength", "strong");  // 较强
        } else {
            return getDictValue("medical_pulse_strength", "very_strong");  // 强劲
        }
    }

    /**
     * 判断脉搏节律
     */
    public String determinePulseRhythm(double regularityValue) {
        if (regularityValue < 0.6) {
            return getDictValue("medical_pulse_rhythm", "intermittent");  // 结代
        } else if (regularityValue < 0.85) {
            return getDictValue("medical_pulse_rhythm", "irregular");  // 不规则
        } else {
            return getDictValue("medical_pulse_rhythm", "regular");  // 规则
        }
    }

    /**
     * 判断腹部按诊感觉
     */
    public String determineAbdomenSensation(boolean hasTenderness, boolean hasMass, boolean hasRigidity) {
        if (hasMass) {
            return getDictValue("medical_abdomen_sensation", "mass");  // 硬结
        } else if (hasRigidity) {
            return getDictValue("medical_abdomen_sensation", "abdominal_rigidity");  // 腹肌紧张
        } else if (hasTenderness) {
            return getDictValue("medical_abdomen_sensation", "tenderness");  // 压痛
        } else {
            return getDictValue("medical_abdomen_sensation", "normal");  // 无异常
        }
    }

    /**
     * 判断腹部皮温
     */
    public String determineAbdomenTemperature(double temperatureValue) {
        // 假设temperatureValue是归一化的温度值，范围0-1
        if (temperatureValue < 0.4) {
            return getDictValue("medical_abdomen_temperature", "cool");  // 偏凉
        } else if (temperatureValue > 0.6) {
            return getDictValue("medical_abdomen_temperature", "warm");  // 偏热
        } else {
            return getDictValue("medical_abdomen_temperature", "normal");  // 正常
        }
    }

    /**
     * 从字典获取显示值
     */
    public String getDictValue(String dictType, String dictCode) {
        return dictDataApi.getDictDataLabel(dictType, dictCode);
    }

    /**
     * 从字典获取代码值
     */
    public String getDictCode(String dictType, String dictLabel) {
        return dictDataApi.getDictDataValue(dictType, dictLabel);
    }
}