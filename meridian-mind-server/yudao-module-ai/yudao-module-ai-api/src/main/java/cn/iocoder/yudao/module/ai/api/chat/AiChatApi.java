package cn.iocoder.yudao.module.ai.api.chat;

import cn.iocoder.yudao.module.ai.api.chat.dto.AiChatConversationCreateMyReqDTO;
import cn.iocoder.yudao.module.ai.api.chat.dto.AiChatMessageSendReqDTO;
import cn.iocoder.yudao.module.ai.api.chat.dto.AiChatMessageSendRespDTO;

/**
 * AI 对话消息 API 接口
 */
public interface AiChatApi {

    /**
     * 发送消息
     *
     * @param sendReqDTO 发送信息
     * @param userId 用户编号
     * @return 发送结果
     */
    AiChatMessageSendRespDTO sendMessage(AiChatMessageSendReqDTO sendReqDTO, Long userId);
    /**
     * 创建【我的】聊天对话
     *
     * @param createMyReqDTO 创建信息
     * @return 编号
     */
    Long createChatConversationMy(AiChatConversationCreateMyReqDTO createMyReqDTO);

}