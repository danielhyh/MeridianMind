package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.adapter;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.config.FourDiagnosisConfig.FourDiagnosisProperties;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.ApiResponseDTO;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.FacialFeatureDTO;
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
            ApiResponseDTO<FacialFeatureDTO> response = JSON.parseObject(responseStr,
                    new TypeReference<ApiResponseDTO<FacialFeatureDTO>>() {
                    });

            // 处理响应
            if (response == null || !Boolean.TRUE.equals(response.getSuccess())) {
                String errorMsg = response != null ? response.getMessage() : "面色分析服务无响应";
                log.error("[analyzeFacialImage] 面色分析失败: {}", errorMsg);
                throw new ServiceException(ERROR_CODE_FACE_SERVICE, StrUtil.isNotEmpty(errorMsg) ? errorMsg : "面色分析失败");
            }

            // 返回结果
            FacialFeatureDTO result = response.getData();
            log.info("[analyzeFacialImage] 面色分析成功: {}", result);

            return result;
        } catch (IOException e) {
            log.error("[analyzeFacialImage] 面色图像处理异常", e);
            throw new ServiceException(ERROR_CODE_FACE_SERVICE, "面色图像处理异常: " + e.getMessage());
        } catch (Exception e) {
            log.error("[analyzeFacialImage] 面色分析异常", e);
            throw new ServiceException(ERROR_CODE_FACE_SERVICE, "面色分析异常: " + e.getMessage());
        }
    }
}