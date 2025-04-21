package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.adapter;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.config.FourDiagnosisConfig.FourDiagnosisProperties;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.ApiResponseDTO;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.FacialFeatureDTO;
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
 * 面色分析适配器
 */
@Component
@Slf4j
public class FaceAnalysisAdapter {

    private static final int ERROR_CODE_FACE_SERVICE = 1_016_001_001; // 面色分析服务错误码

    @Resource
    private RestTemplate fourDiagnosisRestTemplate;

    @Resource
    private FourDiagnosisProperties fourDiagnosisProperties;

    @Resource
    private DiagnosticFeatureUtils diagnosticFeatureUtils;

    /**
     * 发送面色图像并获取分析结果
     *
     * @param facialImage 面色图像
     * @return 面色特征
     */
    public FacialFeatureDTO analyzeFacialImage(MultipartFile facialImage) {
        Assert.notNull(facialImage, "面色图像不能为空");

        log.info("[analyzeFacialImage] 开始分析面色图像, 文件名: {}, 大小: {}",
                facialImage.getOriginalFilename(), facialImage.getSize());

        try {
            // 构建请求URL
            String url = fourDiagnosisProperties.getServiceUrl() + fourDiagnosisProperties.getFaceAnalysisEndpoint();

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 构建请求体
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(facialImage.getBytes()) {
                @Override
                public String getFilename() {
                    return facialImage.getOriginalFilename();
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
                String errorMsg = response != null ? response.getMessage() : "面色分析服务无响应";
                log.error("[analyzeFacialImage] 面色分析失败: {}", errorMsg);
                throw new ServiceException(ERROR_CODE_FACE_SERVICE, StrUtil.isNotEmpty(errorMsg) ? errorMsg : "面色分析失败");
            }

            // 获取原始特征数据
            Map<String, Object> rawFeatures = response.getData();

            // 提取基础特征值
            double hueMean = getDoubleValue(rawFeatures, "hue_mean");
            double saturationMean = getDoubleValue(rawFeatures, "saturation_mean");
            double valueMean = getDoubleValue(rawFeatures, "value_mean");

            // 使用工具类进行判断
            String faceColorCode = diagnosticFeatureUtils.getDictCode("medical_face_color",
                    diagnosticFeatureUtils.determineFaceColor(hueMean, saturationMean, valueMean));
            String faceColor = diagnosticFeatureUtils.getDictValue("medical_face_color", faceColorCode);

            // 处理区域颜色
            Map<String, Object> regionColors = new HashMap<>();
            Object rawRegionColors = rawFeatures.get("regionColors");
            if (rawRegionColors instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> rawRegionMap = (Map<String, Object>) rawRegionColors;

                for (Map.Entry<String, Object> entry : rawRegionMap.entrySet()) {
                    String regionName = entry.getKey();
                    if (entry.getValue() instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> regionData = (Map<String, Object>) entry.getValue();

                        double regionHueMean = getDoubleValue(regionData, "hue_mean", 0.0);
                        double regionSaturationMean = getDoubleValue(regionData, "saturation_mean", 0.0);
                        double regionValueMean = getDoubleValue(regionData, "value_mean", 0.0);

                        // 为每个区域判断面色
                        String regionColorCode = diagnosticFeatureUtils.getDictCode("medical_face_color",
                                diagnosticFeatureUtils.determineFaceColor(
                                        regionHueMean, regionSaturationMean, regionValueMean));
                        String regionColor = diagnosticFeatureUtils.getDictValue("medical_face_color", regionColorCode);

                        // 更新区域颜色信息
                        Map<String, Object> regionResult = new HashMap<>();
                        regionResult.put("color", regionColor);
                        regionResult.put("colorCode", regionColorCode);
                        regionResult.put("saturation", getDoubleValue(regionData, "saturation", 0.0));
                        regionResult.put("brightness", getDoubleValue(regionData, "brightness", 0.0));

                        regionColors.put(regionName, regionResult);
                    }
                }
            }

            // 构建面色特征结果
            FacialFeatureDTO facialFeature = new FacialFeatureDTO();
            facialFeature.setFaceColor(faceColor);
            facialFeature.setFaceColorCode(faceColorCode);
            facialFeature.setColorSaturation(saturationMean);
            facialFeature.setColorBrightness(valueMean);
            facialFeature.setRegionColors(regionColors);
            facialFeature.setRawFeatures(rawFeatures);

            log.info("[analyzeFacialImage] 面色分析成功: {}", facialFeature);
            return facialFeature;

        } catch (IOException e) {
            log.error("[analyzeFacialImage] 面色图像处理异常", e);
            throw new ServiceException(ERROR_CODE_FACE_SERVICE, "面色图像处理异常: " + e.getMessage());
        } catch (Exception e) {
            log.error("[analyzeFacialImage] 面色分析异常", e);
            throw new ServiceException(ERROR_CODE_FACE_SERVICE, "面色分析异常: " + e.getMessage());
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
        }
        return defaultValue;
    }
}