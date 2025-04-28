package cn.iocoder.yudao.module.ai.api.chat.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiChatMessageSendRespDTO {

    private Message send;

    private Message receive;
    private String reasoningContent;

    @Data
    public static class Message {

        private Long id;

        private String type; // 参见 MessageType 枚举类

        private String content;

        private LocalDateTime createTime;
        private String reasoningContent;

    }

}
