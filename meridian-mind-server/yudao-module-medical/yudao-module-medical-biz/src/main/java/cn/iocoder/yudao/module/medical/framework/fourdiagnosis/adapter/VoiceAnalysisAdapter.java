package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.adapter;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.config.FourDiagnosisConfig.FourDiagnosisProperties;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.ApiResponseDTO;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.VoiceFeatureDTO;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.utils.DiagnosticFeatureUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 语音分析适配器
 */
@Component
@Slf4j
public class VoiceAnalysisAdapter {

    private static final int ERROR_CODE_VOICE_SERVICE = 1_016_001_002; // 语音分析服务错误码

    // 支持的音频MIME类型映射
    private static final Map<String, String> SUPPORTED_AUDIO_MIME_TYPES = new HashMap<>();

    static {
        // 初始化支持的音频MIME类型
        SUPPORTED_AUDIO_MIME_TYPES.put("audio/wav", ".wav");
        SUPPORTED_AUDIO_MIME_TYPES.put("audio/x-wav", ".wav");
        SUPPORTED_AUDIO_MIME_TYPES.put("audio/mpeg", ".mp3");
        SUPPORTED_AUDIO_MIME_TYPES.put("audio/mp3", ".mp3");
        SUPPORTED_AUDIO_MIME_TYPES.put("audio/ogg", ".ogg");
        SUPPORTED_AUDIO_MIME_TYPES.put("audio/x-m4a", ".m4a");
        SUPPORTED_AUDIO_MIME_TYPES.put("audio/aac", ".aac");
        SUPPORTED_AUDIO_MIME_TYPES.put("audio/mp4", ".mp4");
        SUPPORTED_AUDIO_MIME_TYPES.put("audio/webm", ".webm");
        SUPPORTED_AUDIO_MIME_TYPES.put("audio/flac", ".flac");
        SUPPORTED_AUDIO_MIME_TYPES.put("application/octet-stream", ".bin"); // 对于未指定MIME类型的情况
    }

    @Resource
    private RestTemplate fourDiagnosisRestTemplate;

    @Resource
    private FourDiagnosisProperties fourDiagnosisProperties;

    @Resource
    private DiagnosticFeatureUtils diagnosticFeatureUtils;

    /**
     * 发送语音音频并获取分析结果
     *
     * @param voiceAudio 语音音频
     * @return 语音特征
     */
    public VoiceFeatureDTO analyzeVoiceAudio(MultipartFile voiceAudio) {
        Assert.notNull(voiceAudio, "语音音频不能为空");

        log.info("[analyzeVoiceAudio] 开始分析语音音频, 文件名: {}, 类型: {}, 大小: {}",
                voiceAudio.getOriginalFilename(), voiceAudio.getContentType(), voiceAudio.getSize());

        try {
            // 构建请求URL
            String url = fourDiagnosisProperties.getServiceUrl() + fourDiagnosisProperties.getVoiceAnalysisEndpoint();

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 获取文件扩展名
            String originalFilename = voiceAudio.getOriginalFilename();
            String extension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) :
                    SUPPORTED_AUDIO_MIME_TYPES.getOrDefault(voiceAudio.getContentType(), ".wav");

            // 构建请求体
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(voiceAudio.getBytes()) {
                @Override
                public String getFilename() {
                    return "audio" + extension;  // 确保文件名包含正确的扩展名
                }

                // 确保内容类型正确
                public String getContentType() {
                    // 尝试根据扩展名推断MIME类型
                    if (".wav".equals(extension)) return "audio/wav";
                    if (".mp3".equals(extension)) return "audio/mpeg";
                    if (".ogg".equals(extension)) return "audio/ogg";
                    if (".m4a".equals(extension)) return "audio/x-m4a";
                    if (".aac".equals(extension)) return "audio/aac";
                    if (".mp4".equals(extension)) return "audio/mp4";
                    if (".webm".equals(extension)) return "audio/webm";
                    if (".flac".equals(extension)) return "audio/flac";
                    // 默认返回原始内容类型
                    return voiceAudio.getContentType();
                }
            });

            // 发送请求
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            String responseStr = fourDiagnosisRestTemplate.postForObject(url, requestEntity, String.class);

            // 解析响应
            ApiResponseDTO<Map<String, Object>> response = JSON.parseObject(responseStr,
                    new TypeReference<ApiResponseDTO<Map<String, Object>>>() {
                    });

            // 处理响应
            if (response == null || !Boolean.TRUE.equals(response.getSuccess())) {
                String errorMsg = response != null ? response.getMessage() : "语音分析服务无响应";
                log.error("[analyzeVoiceAudio] 语音分析失败: {}", errorMsg);
                throw new ServiceException(ERROR_CODE_VOICE_SERVICE, StrUtil.isNotEmpty(errorMsg) ? errorMsg : "语音分析失败");
            }

            // 获取原始特征数据
            Map<String, Object> rawFeatures = response.getData();

            // 提取基础特征值
            double rmsMean = getDoubleValue(rawFeatures, "rms_mean");
            double zcrMean = getDoubleValue(rawFeatures, "zcr_mean");
            double spectralCentroidMean = getDoubleValue(rawFeatures, "spectral_centroid_mean");

            // 使用工具类进行判断
            String strengthCode = diagnosticFeatureUtils.getDictCode("medical_voice_strength",
                    diagnosticFeatureUtils.determineVoiceStrength(rmsMean));
            String strength = diagnosticFeatureUtils.getDictValue("medical_voice_strength", strengthCode);

            String toneCode = diagnosticFeatureUtils.getDictCode("medical_voice_tone",
                    diagnosticFeatureUtils.determineVoiceTone(spectralCentroidMean));
            String tone = diagnosticFeatureUtils.getDictValue("medical_voice_tone", toneCode);

            String breathPatternCode = diagnosticFeatureUtils.getDictCode("medical_breath_pattern",
                    diagnosticFeatureUtils.determineBreathPattern(rmsMean, zcrMean));
            String breathPattern = diagnosticFeatureUtils.getDictValue("medical_breath_pattern", breathPatternCode);

            // 语音节律判断 - 基于RMS标准差
            double rmsStd = getDoubleValue(rawFeatures, "rms_std");
            String rhythm = "均匀"; // 默认为均匀
            if (rmsStd > 0.1) {
                rhythm = "不均匀";
            }

            // 构建语音特征结果
            VoiceFeatureDTO voiceFeature = new VoiceFeatureDTO();
            voiceFeature.setStrength(strength);
            voiceFeature.setStrengthCode(strengthCode);
            voiceFeature.setTone(tone);
            voiceFeature.setToneCode(toneCode);
            voiceFeature.setRhythm(rhythm);
            voiceFeature.setBreathPattern(breathPattern);
            voiceFeature.setBreathPatternCode(breathPatternCode);
            voiceFeature.setRawFeatures(rawFeatures);

            log.info("[analyzeVoiceAudio] 语音分析成功: {}", voiceFeature);
            return voiceFeature;

        } catch (IOException e) {
            log.error("[analyzeVoiceAudio] 语音音频处理异常", e);
            throw new ServiceException(ERROR_CODE_VOICE_SERVICE, "语音音频处理异常: " + e.getMessage());
        } catch (Exception e) {
            log.error("[analyzeVoiceAudio] 语音分析异常", e);
            throw new ServiceException(ERROR_CODE_VOICE_SERVICE, "语音分析异常: " + e.getMessage());
        }
    }

    /**
     * 安全获取Double值
     */
    private Double getDoubleValue(Map<String, Object> map, String key) {
        return getDoubleValue(map, key, 0.0);
    }

    /**
     * 安全获取Double值
     */
    private Double getDoubleValue(Map<String, Object> map, String key, Double defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> subMap = (Map<String, Object>) value;
            // 处理嵌套值，例如可能存在的结构 { "feature": { "value": 0.5 } }
            if (subMap.containsKey("value")) {
                return getDoubleValue(subMap, "value", defaultValue);
            }
        }
        return defaultValue;
    }
}