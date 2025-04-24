package cn.iocoder.yudao.module.ai.service.maxkb;

import cn.iocoder.yudao.framework.ai.core.model.maxkb.MaxKBClient;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.ai.enums.maxkb.MaxKBApi;
import cn.iocoder.yudao.module.ai.enums.maxkb.MaxKBConstants;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MaxKBApiImpl implements MaxKBApi {

    @Resource
    private MaxKBClient maxKBClient;

    @Override
    public String sendChatMessage(String chatId, String message, boolean reChat, boolean stream) {
        try {
            return maxKBClient.sendChatMessage(chatId, message, reChat, stream);
        } catch (Exception e) {
            throw new ServiceException(MaxKBConstants.ERROR_CODE_MAXKB_API_CALL_FAILED,
                    "调用MaxKB对话API失败：" + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getApplicationProfile() {
        try {
            return maxKBClient.getApplicationProfile();
        } catch (Exception e) {
            throw new ServiceException(MaxKBConstants.ERROR_CODE_MAXKB_API_CALL_FAILED,
                    "获取MaxKB应用信息失败：" + e.getMessage());
        }
    }

    @Override
    public String openChat(String applicationId) {
        try {
            return maxKBClient.openChat(applicationId);
        } catch (Exception e) {
            throw new ServiceException(MaxKBConstants.ERROR_CODE_MAXKB_API_CALL_FAILED,
                    "打开MaxKB会话失败：" + e.getMessage());
        }
    }
}