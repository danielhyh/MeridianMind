package cn.iocoder.yudao.module.ai.api.prompt;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.api.prompt.dto.AiPromptTemplateRenderReqDTO;
import cn.iocoder.yudao.module.ai.api.prompt.dto.AiPromptTemplateRespDTO;
import cn.iocoder.yudao.module.ai.service.prompttemplate.PromptTemplateService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * AI 提示词模板 API 实现类
 */
@Service
@Validated
@Slf4j
public class AiPromptTemplateApiImpl implements AiPromptTemplateApi {

    @Resource
    private PromptTemplateService promptTemplateService;

    @Override
    public AiPromptTemplateRespDTO getPromptTemplate(Long id) {
        return BeanUtils.toBean(promptTemplateService.getPromptTemplate(id), AiPromptTemplateRespDTO.class);
    }

    @Override
    public String renderPromptTemplate(AiPromptTemplateRenderReqDTO renderReqDTO) {
        return promptTemplateService.renderPromptTemplate(
                renderReqDTO.getTemplateId(),
                renderReqDTO.getParameters()
        );
    }
}