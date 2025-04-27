package cn.iocoder.yudao.module.ai.api.prompt;

import cn.iocoder.yudao.module.ai.api.prompt.dto.AiPromptTemplateRenderReqDTO;
import cn.iocoder.yudao.module.ai.api.prompt.dto.AiPromptTemplateRespDTO;

/**
 * AI 提示词模板 API 接口
 */
public interface AiPromptTemplateApi {

    /**
     * 获取提示词模板
     *
     * @param id 模板编号
     * @return 提示词模板
     */
    AiPromptTemplateRespDTO getPromptTemplate(Long id);

    /**
     * 渲染提示词模板
     *
     * @param renderReqDTO 渲染请求
     * @return 渲染后的内容
     */
    String renderPromptTemplate(AiPromptTemplateRenderReqDTO renderReqDTO);
}