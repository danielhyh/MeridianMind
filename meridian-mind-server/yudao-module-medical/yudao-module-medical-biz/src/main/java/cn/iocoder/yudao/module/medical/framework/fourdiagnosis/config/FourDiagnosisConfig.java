package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class FourDiagnosisConfig {

    @Bean
    @ConfigurationProperties(prefix = "yudao.four-diagnosis")
    public FourDiagnosisProperties fourDiagnosisProperties() {
        return new FourDiagnosisProperties();
    }

    @Bean
    public RestTemplate fourDiagnosisRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        return new RestTemplate(factory);
    }

    @Data
    public static class FourDiagnosisProperties {
        /**
         * 四诊服务URL
         */
        private String serviceUrl = "http://localhost:8000";

        /**
         * 舌象分析端点
         */
        private String tongueAnalysisEndpoint = "/api/tongue/analyze";

        /**
         * 面色分析端点
         */
        private String faceAnalysisEndpoint = "/api/face/analyze";

        /**
         * 语音分析端点
         */
        private String voiceAnalysisEndpoint = "/api/voice/analyze";
    }
}