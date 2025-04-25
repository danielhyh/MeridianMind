package cn.iocoder.yudao.framework.ai.core.model.maxkb;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MaxKB 模型适配器，实现ChatModel和StreamingChatModel接口
 * 将MaxKB的API调用与Spring AI标准接口对接
 */
public class MaxKBModelAdapter implements ChatModel, StreamingChatModel {

    // 会话ID缓存，用于存储会话ID与MaxKB会话ID的映射关系
    private static final Map<String, String> SESSION_CACHE = new ConcurrentHashMap<>();
    private final MaxKBClient client;
    private final String applicationId;

    public MaxKBModelAdapter(MaxKBClient client, String applicationId) {
        this.client = client;
        this.applicationId = applicationId;
    }

    /**
     * 获取或创建MaxKB会话ID
     *
     * @param sessionKey 内部会话标识
     * @return MaxKB会话ID
     */
    private String getOrCreateChatId(String sessionKey) {
        String chatId = SESSION_CACHE.get(sessionKey);
        if (chatId == null) {
            chatId = client.openChat(applicationId);
            SESSION_CACHE.put(sessionKey, chatId);
        }
        return chatId;
    }

    /**
     * 从Prompt中提取用户消息
     *
     * @param prompt Spring AI提供的Prompt对象
     * @return 用户消息内容
     */
    private String extractUserMessage(Prompt prompt) {
        // 从 Prompt 中提取用户消息
//        return prompt.getMessages().stream()
//                .filter(msg -> MessageType.USER.equals(msg.getMessageType()))
//                .reduce((first, second) -> second)  // 获取最后一条
//                .map(Message::getContent)
//                .orElse("");
        return null;
    }

    /**
     * 从MaxKB响应转换为ChatResponse对象
     *
     * @param response MaxKB API响应
     * @return Spring AI定义的ChatResponse对象
     */
    private ChatResponse convertToChatResponse(MaxKBApiResponse response) {
//        Map<String, Object> metadata = new HashMap<>();
//        metadata.put("prompt_tokens", response.getPromptTokens());
//        metadata.put("completion_tokens", response.getCompletionTokens());
//        metadata.put("reasoning_content", response.getReasoningContent());
//
//        AssistantMessage assistantMessage = new AssistantMessage(response.getContent());
//
//        return new ChatResponse(assistantMessage, metadata);
        return null;
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        // 生成或获取会话ID
        String sessionKey = prompt.getOptions().toString(); // 使用options作为会话唯一标识
        String chatId = getOrCreateChatId(sessionKey);

        // 提取用户消息
        String userMessage = extractUserMessage(prompt);

        // 调用MaxKB API
        MaxKBApiResponse response = client.sendChatMessage(chatId, userMessage, false, false);

        // 转换并返回结果
        return convertToChatResponse(response);
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return null;
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
//        // 生成或获取会话ID
//        String sessionKey = prompt.getOptions().toString();
//        String chatId = getOrCreateChatId(sessionKey);
//
//        // 提取用户消息
//        String userMessage = extractUserMessage(prompt);
//
//        // 由于MaxKB本身支持流式输出，我们需要解析SSE事件流
//        return client.streamChatMessage(chatId, userMessage)
//                .map(this::convertToChatResponse);
        return null;
    }
}