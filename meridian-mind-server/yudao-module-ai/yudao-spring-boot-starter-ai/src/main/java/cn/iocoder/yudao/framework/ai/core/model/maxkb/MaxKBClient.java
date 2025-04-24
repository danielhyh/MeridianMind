package cn.iocoder.yudao.framework.ai.core.model.maxkb;

import cn.iocoder.yudao.framework.ai.config.YudaoAiProperties;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class MaxKBClient {
    private final RestTemplate restTemplate;
    private final YudaoAiProperties.MaxKBConfig config;

    public MaxKBClient(RestTemplate restTemplate, YudaoAiProperties.MaxKBConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;

        // 配置RestTemplate，添加认证头等
    }

    /**
     * 发送对话消息
     */
    public String sendChatMessage(String chatId, String message, boolean reChat, boolean stream) {
        String url = config.getBaseUrl() + "/application/chat_message/" + chatId;

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("message", message);
        requestBody.put("re_chat", reChat);
        requestBody.put("stream", stream);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", config.getApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, Map.class);

        // 处理响应
        // ...

        return response.getBody().get("message").toString();
    }

    /**
     * 获取应用信息
     */
    public Map<String, Object> getApplicationProfile() {
        String url = config.getBaseUrl() + "/application/profile";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", config.getApiKey());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class);

        return response.getBody();
    }

    /**
     * 获取会话ID
     */
    public String openChat(String applicationId) {
        String url = config.getBaseUrl() + "/application/" + applicationId + "/chat/open";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", config.getApiKey());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, Map.class);

        // 假设响应中包含chat_id字段
        return response.getBody().get("chat_id").toString();
    }
}