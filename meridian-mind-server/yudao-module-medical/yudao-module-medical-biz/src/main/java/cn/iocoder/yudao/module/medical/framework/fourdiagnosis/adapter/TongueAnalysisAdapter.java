package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.adapter;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.config.FourDiagnosisConfig.FourDiagnosisProperties;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.ApiResponseDTO;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.TongueFeatureDTO;
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
import java.util.Map;

/**
 * 舌象分析适配器
 */
@Component
@Slf4j
public class TongueAnalysisAdapter {

    private static final int ERROR_CODE_TONGUE_SERVICE = 1_016_001_000; // 舌象分析服务错误码

    @Resource
    private RestTemplate fourDiagnosisRestTemplate;

    @Resource
    private FourDiagnosisProperties fourDiagnosisProperties;

    @Resource
    private DiagnosticFeatureUtils diagnosticFeatureUtils;

    /**
     * 发送舌象图像并获取分析结果
     *
     * @param tongueImage 舌象图像
     * @return 舌象特征
     */
    public TongueFeatureDTO analyzeTongueImage(MultipartFile tongueImage) {
        Assert.notNull(tongueImage, "舌象图像不能为空");

        log.info("[analyzeTongueImage] 开始分析舌象图像, 文件名: {}, 大小: {}",
                tongueImage.getOriginalFilename(), tongueImage.getSize());

        try {
            // 构建请求URL
            String url = fourDiagnosisProperties.getServiceUrl() + fourDiagnosisProperties.getTongueAnalysisEndpoint();

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 构建请求体
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(tongueImage.getBytes()) {
                @Override
                public String getFilename() {
                    return tongueImage.getOriginalFilename();
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
                String errorMsg = response != null ? response.getMessage() : "舌象分析服务无响应";
                log.error("[analyzeTongueImage] 舌象分析失败: {}", errorMsg);
                throw new ServiceException(ERROR_CODE_TONGUE_SERVICE, StrUtil.isNotEmpty(errorMsg) ? errorMsg : "舌象分析失败");
            }

            // 获取原始特征数据
            Map<String, Object> rawFeatures = response.getData();

            // 根据原始特征判断舌象特征
            TongueFeatureDTO tongueFeature = new TongueFeatureDTO();

            // 提取基础特征值
            double hueMean = getDoubleValue(rawFeatures, "hue_mean");
            double saturationMean = getDoubleValue(rawFeatures, "saturation_mean");
            double valueMean = getDoubleValue(rawFeatures, "value_mean");

            // 使用工具类进行判断
            String tongueColor = diagnosticFeatureUtils.determineTongueColor(hueMean, saturationMean, valueMean);
            String tongueCoating = diagnosticFeatureUtils.determineTongueCoating(saturationMean, valueMean);

            // 设置结果
            tongueFeature.setTongueColor(tongueColor);
            tongueFeature.setTongueCoating(tongueCoating);
            tongueFeature.setTongueShape("正常"); // 默认形状
            tongueFeature.setHasCrack(getBooleanValue(rawFeatures, "hasCrack"));
            tongueFeature.setHasToothMark(getBooleanValue(rawFeatures, "hasToothMark"));
            tongueFeature.setMoisture(getDoubleValue(rawFeatures, "moisture"));
            tongueFeature.setRawFeatures(rawFeatures);

            log.info("[analyzeTongueImage] 舌象分析成功: {}", tongueFeature);
            return tongueFeature;
        } catch (IOException e) {
            log.error("[analyzeTongueImage] 舌象图像处理异常", e);
            throw new ServiceException(ERROR_CODE_TONGUE_SERVICE, "舌象图像处理异常: " + e.getMessage());
        } catch (Exception e) {
            log.error("[analyzeTongueImage] 舌象分析异常", e);
            throw new ServiceException(ERROR_CODE_TONGUE_SERVICE, "舌象分析异常: " + e.getMessage());
        }
    }

    /**
     * 安全获取Double值
     */
    private Double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    /**
     * 安全获取Boolean值
     */
    private Boolean getBooleanValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        } else if (value instanceof Number) {
            return ((Number) value).intValue() > 0;
        }
        return false;
    }
}