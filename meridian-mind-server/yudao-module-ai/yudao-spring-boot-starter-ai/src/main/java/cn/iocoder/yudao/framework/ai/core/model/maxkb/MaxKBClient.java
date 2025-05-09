package cn.iocoder.yudao.framework.ai.core.model.maxkb;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * MaxKB API客户端
 * 封装对MaxKB平台API的调用
 */
@Slf4j
public class MaxKBClient {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public MaxKBClient(String apiKey, String baseUrl) {
        // 对baseUrl进行trim处理，移除前导和尾随空格
        String trimmedBaseUrl = baseUrl != null ? baseUrl.trim() : "";
        this.webClient = WebClient.builder()
                .baseUrl(trimmedBaseUrl)
                .defaultHeader("Authorization", apiKey)
                .codecs(clientCodecConfigurer -> {
                    clientCodecConfigurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024); // 增加缓冲区大小
                })
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                        .responseTimeout(Duration.ofMinutes(10))))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 打开一个新的MaxKB会话
     *
     * @param applicationId MaxKB应用ID
     * @return 会话ID
     */
    public String openChat(@Nonnull String applicationId) {
        try {
            String response = webClient.get()
                    .uri("/application/{application_id}/chat/open", applicationId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            if (responseMap.get("code").equals(200)) {
                return (String) responseMap.get("data");
            } else {
                throw new ServiceException(500, "创建MaxKB会话失败: " + responseMap.get("message"));
            }
        } catch (Exception e) {
            throw new ServiceException(500, "创建MaxKB会话失败: " + e.getMessage());
        }
    }

    /**
     * 发送消息到MaxKB API并获取响应（段式）
     *
     * @param chatId  会话ID
     * @param message 消息内容
     * @param reChat  是否重新生成
     * @param stream  是否使用流式输出
     * @return MaxKB API响应
     */
    public MaxKBApiResponse sendChatMessage(String chatId, String message, boolean reChat, boolean stream) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("message", message);
            requestBody.put("re_chat", reChat);
            requestBody.put("stream", stream);

            String response = webClient.post()
                    .uri("/application/chat_message/{chatId}", chatId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMinutes(10))
                    .block(Duration.ofMinutes(10));

            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            if (responseMap.get("code").equals(200)) {
                Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
                return MaxKBApiResponse.builder()
                        .chatId((String) data.get("chat_id"))
                        .id((String) data.get("id"))
                        .content((String) data.get("content"))
                        .reasoningContent((String) data.get("reasoning_content"))
                        .promptTokens((Integer) data.get("prompt_tokens"))
                        .completionTokens((Integer) data.get("completion_tokens"))
                        .isEnd((Boolean) data.get("is_end"))
                        .build();
            } else {
                throw new ServiceException(500, "发送MaxKB消息失败: " + responseMap.get("message"));
            }
        } catch (Exception e) {
            log.error("发送MaxKB消息失败", e);
            throw new ServiceException(500, "发送MaxKB消息失败: " + e.getMessage());
        }
    }
    public Mono<MaxKBApiResponse> sendChatMessageAsync(String chatId, String message, boolean reChat, boolean stream) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("message", message);
        requestBody.put("re_chat", reChat);
        requestBody.put("stream", stream);

        return webClient.post()
                .uri("/application/chat_message/{chatId}", chatId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMinutes(10))
                .map(response -> {
                    try {
                        Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
                        if (responseMap.get("code").equals(200)) {
                            Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
                            return MaxKBApiResponse.builder()
                                    .chatId((String) data.get("chat_id"))
                                    .id((String) data.get("id"))
                                    .content((String) data.get("content"))
                                    .reasoningContent((String) data.get("reasoning_content"))
                                    .promptTokens((Integer) data.get("prompt_tokens"))
                                    .completionTokens((Integer) data.get("completion_tokens"))
                                    .isEnd((Boolean) data.get("is_end"))
                                    .build();
                        } else {
                            throw new ServiceException(500, "发送MaxKB消息失败: " + responseMap.get("message"));
                        }
                    } catch (Exception e) {
                        log.error("发送MaxKB消息失败", e);
                        return null;
                    }
                });
    }

    /**
     * 发送消息到MaxKB API并获取流式响应
     *
     * @param chatId  会话ID
     * @param message 消息内容
     * @return 流式响应
     */
    public Flux<MaxKBApiResponse> streamChatMessage(String chatId, String message) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("message", message);
        requestBody.put("re_chat", false);
        requestBody.put("stream", true);

        return webClient.post()
                .uri("/application/chat_message/{chatId}", chatId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .map(chunk -> {
                    try {
                        Map<String, Object> dataMap = objectMapper.readValue(chunk, Map.class);
                        // 提取思考内容
                        String reasoningContent = dataMap.containsKey("reasoning_content") ?
                                (String) dataMap.get("reasoning_content") : null;

                        return MaxKBApiResponse.builder()
                                .chatId((String) dataMap.get("chat_id"))
                                .id((String) dataMap.get("chat_record_id"))
                                .content((String) dataMap.get("content"))
                                .reasoningContent(reasoningContent) // 设置思考内容
                                .isEnd((Boolean) dataMap.get("is_end"))
                                .build();
                    } catch (Exception e) {
                        throw new RuntimeException("解析MaxKB流式响应失败", e);
                    }
                });
    }

    /**
     * 获取应用信息
     *
     * @return 应用信息
     */
    public Map<String, Object> getApplicationProfile() {
        try {
            String response = webClient.get()
                    .uri("/application/profile")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            if (responseMap.get("code").equals(200)) {
                return (Map<String, Object>) responseMap.get("data");
            } else {
                throw new ServiceException(500, "获取MaxKB应用信息失败: " + responseMap.get("message"));
            }
        } catch (Exception e) {
            throw new ServiceException(500, "获取MaxKB应用信息失败: " + e.getMessage());
        }
    }
}