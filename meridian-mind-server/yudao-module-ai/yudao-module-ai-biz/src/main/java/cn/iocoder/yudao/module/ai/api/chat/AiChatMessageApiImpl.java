package cn.iocoder.yudao.module.ai.api.chat;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.module.ai.api.chat.dto.AiChatMessageSendReqDTO;
import cn.iocoder.yudao.module.ai.api.chat.dto.AiChatMessageSendRespDTO;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.message.AiChatMessageSendReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.message.AiChatMessageSendRespVO;
import cn.iocoder.yudao.module.ai.service.chat.AiChatMessageService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AiChatMessageApiImpl implements AiChatMessageApi {
    @Resource
    private AiChatMessageService aiChatMessageService;

    @Override
    public AiChatMessageSendRespDTO sendMessage(AiChatMessageSendReqDTO sendReqDTO, Long userId) {
        AiChatMessageSendReqVO aiChatMessageSendReqVO = BeanUtil.copyProperties(sendReqDTO, AiChatMessageSendReqVO.class);
        AiChatMessageSendRespVO respVO = aiChatMessageService.sendMessage(aiChatMessageSendReqVO, userId);
        return BeanUtil.copyProperties(respVO, AiChatMessageSendRespDTO.class);
    }
}
