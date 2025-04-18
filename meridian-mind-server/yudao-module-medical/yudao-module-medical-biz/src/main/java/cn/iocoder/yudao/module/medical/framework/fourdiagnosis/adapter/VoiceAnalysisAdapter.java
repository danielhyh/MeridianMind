package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.adapter;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.config.FourDiagnosisConfig.FourDiagnosisProperties;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.ApiResponseDTO;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.VoiceFeatureDTO;
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

                // 移除 @Override 注解
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
            ApiResponseDTO<VoiceFeatureDTO> response = JSON.parseObject(responseStr,
                    new TypeReference<>() {
                    });

            // 处理响应
            if (response == null || !Boolean.TRUE.equals(response.getSuccess())) {
                String errorMsg = response != null ? response.getMessage() : "语音分析服务无响应";
                log.error("[analyzeVoiceAudio] 语音分析失败: {}", errorMsg);
                throw new ServiceException(ERROR_CODE_VOICE_SERVICE, StrUtil.isNotEmpty(errorMsg) ? errorMsg : "语音分析失败");
            }

            // 返回结果
            VoiceFeatureDTO result = response.getData();
            log.info("[analyzeVoiceAudio] 语音分析成功: {}", result);

            return result;
        } catch (IOException e) {
            log.error("[analyzeVoiceAudio] 语音音频处理异常", e);
            throw new ServiceException(ERROR_CODE_VOICE_SERVICE, "语音音频处理异常: " + e.getMessage());
        } catch (Exception e) {
            log.error("[analyzeVoiceAudio] 语音分析异常", e);
            throw new ServiceException(ERROR_CODE_VOICE_SERVICE, "语音分析异常: " + e.getMessage());
        }
    }
}