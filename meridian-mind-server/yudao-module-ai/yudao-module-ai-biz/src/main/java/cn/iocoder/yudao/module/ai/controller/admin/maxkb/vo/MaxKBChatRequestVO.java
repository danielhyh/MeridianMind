package cn.iocoder.yudao.module.ai.controller.admin.maxkb.vo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class MaxKBChatRequestVO {
    /**
     * 消息内容
     */
    @NotEmpty(message = "消息内容不能为空")
    private String message;

    /**
     * 是否重新生成
     */
    private Boolean reChat = false;

    /**
     * 是否流式输出
     */
    private Boolean stream = false;
}