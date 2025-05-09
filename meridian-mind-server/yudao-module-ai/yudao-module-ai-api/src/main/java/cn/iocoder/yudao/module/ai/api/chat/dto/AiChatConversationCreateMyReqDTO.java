package cn.iocoder.yudao.module.ai.api.chat.dto;

import lombok.Data;

@Data
public class AiChatConversationCreateMyReqDTO {

    private Long roleId;

    private Long knowledgeId;

    private Long UserId;

    private Long modelId;
}
