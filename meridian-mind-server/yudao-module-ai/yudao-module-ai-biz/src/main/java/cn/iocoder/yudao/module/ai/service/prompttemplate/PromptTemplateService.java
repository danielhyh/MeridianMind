package cn.iocoder.yudao.module.ai.service.prompttemplate;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.prompttemplate.vo.PromptTemplatePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.prompttemplate.vo.PromptTemplateSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.prompttemplate.PromptTemplateDO;
import jakarta.validation.Valid;

import java.util.Map;

/**
 * AI提示词模板 Service 接口
 *
 * @author 芋道源码
 */
public interface PromptTemplateService {

    /**
     * 创建AI提示词模板
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPromptTemplate(@Valid PromptTemplateSaveReqVO createReqVO);

    /**
     * 更新AI提示词模板
     *
     * @param updateReqVO 更新信息
     */
    void updatePromptTemplate(@Valid PromptTemplateSaveReqVO updateReqVO);

    /**
     * 删除AI提示词模板
     *
     * @param id 编号
     */
    void deletePromptTemplate(Long id);

    /**
     * 获得AI提示词模板
     *
     * @param id 编号
     * @return AI提示词模板
     */
    PromptTemplateDO getPromptTemplate(Long id);

    /**
     * 获得AI提示词模板分页
     *
     * @param pageReqVO 分页查询
     * @return AI提示词模板分页
     */
    PageResult<PromptTemplateDO> getPromptTemplatePage(PromptTemplatePageReqVO pageReqVO);

    /**
     * 渲染提示词模板
     *
     * @param templateId 模板编号
     * @param parameters 参数
     * @return 渲染后的内容
     */
    String renderPromptTemplate(Long templateId, Map<String, Object> parameters);

    /**
     * 检查提示词模板是否存在
     *
     * @param id 编号
     */
    void validatePromptTemplateExists(Long id);

}