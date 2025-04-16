package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.adapter;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.config.FourDiagnosisConfig.FourDiagnosisProperties;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.ApiResponseDTO;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.TongueFeatureDTO;
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
            ApiResponseDTO<TongueFeatureDTO> response = JSON.parseObject(responseStr,
                    new TypeReference<ApiResponseDTO<TongueFeatureDTO>>() {
                    });

            // 处理响应
            if (response == null || !Boolean.TRUE.equals(response.getSuccess())) {
                String errorMsg = response != null ? response.getMessage() : "舌象分析服务无响应";
                log.error("[analyzeTongueImage] 舌象分析失败: {}", errorMsg);
                throw new ServiceException(ERROR_CODE_TONGUE_SERVICE, StrUtil.isNotEmpty(errorMsg) ? errorMsg : "舌象分析失败");
            }

            // 返回结果
            TongueFeatureDTO result = response.getData();
            log.info("[analyzeTongueImage] 舌象分析成功: {}", result);

            return result;
        } catch (IOException e) {
            log.error("[analyzeTongueImage] 舌象图像处理异常", e);
            throw new ServiceException(ERROR_CODE_TONGUE_SERVICE, "舌象图像处理异常: " + e.getMessage());
        } catch (Exception e) {
            log.error("[analyzeTongueImage] 舌象分析异常", e);
            throw new ServiceException(ERROR_CODE_TONGUE_SERVICE, "舌象分析异常: " + e.getMessage());
        }
    }
}