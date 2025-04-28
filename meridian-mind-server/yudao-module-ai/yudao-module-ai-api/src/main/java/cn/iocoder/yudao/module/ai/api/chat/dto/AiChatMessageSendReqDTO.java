package cn.iocoder.yudao.module.ai.api.chat.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AiChatMessageSendReqDTO {

    @NotNull(message = "聊天对话编号不能为空")
    private Long conversationId;

    @NotEmpty(message = "聊天内容不能为空")
    private String content;

    private Boolean useContext;

}
