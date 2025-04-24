package cn.iocoder.yudao.module.ai.controller.admin.maxkb;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.ai.enums.maxkb.MaxKBApi;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
@RestController
@RequestMapping("/ai/maxkb")
@Validated
public class MaxKBController {

    @Resource
    private MaxKBApi maxKBApi;

    /**
     * 发送对话消息
     */
    @PostMapping("/chat/{chatId}")
    @PreAuthorize("@ss.hasPermission('ai:maxkb:chat')")
    public CommonResult<String> sendChatMessage(@PathVariable String chatId,
                                                @RequestParam String message,
                                                @RequestParam(required = false, defaultValue = "false") boolean reChat,
                                                @RequestParam(required = false, defaultValue = "false") boolean stream) {
        return success(maxKBApi.sendChatMessage(chatId, message, reChat, stream));
    }

    /**
     * 获取应用信息
     */
    @GetMapping("/profile")
    @PreAuthorize("@ss.hasPermission('ai:maxkb:query')")
    public CommonResult<Map<String, Object>> getApplicationProfile() {
        return success(maxKBApi.getApplicationProfile());
    }

    /**
     * 打开会话
     */
    @GetMapping("/{applicationId}/chat/open")
    @PreAuthorize("@ss.hasPermission('ai:maxkb:chat')")
    public CommonResult<String> openChat(@PathVariable String applicationId) {
        return success(maxKBApi.openChat(applicationId));
    }
}