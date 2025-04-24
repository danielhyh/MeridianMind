package cn.iocoder.yudao.module.ai.enums.maxkb;

import java.util.Map;

public interface MaxKBApi {

    /**
     * 发送对话消息
     *
     * @param chatId  会话ID
     * @param message 消息内容
     * @param reChat  是否重新生成
     * @param stream  是否流式输出
     * @return 响应消息
     */
    String sendChatMessage(String chatId, String message, boolean reChat, boolean stream);

    /**
     * 获取应用信息
     *
     * @return 应用信息
     */
    Map<String, Object> getApplicationProfile();

    /**
     * 打开会话
     *
     * @param applicationId 应用ID
     * @return 会话ID
     */
    String openChat(String applicationId);
}