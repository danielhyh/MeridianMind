package cn.iocoder.yudao.module.medical.service.aiprompt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.medical.controller.admin.aiprompt.vo.AiPromptPageReqVO;
import cn.iocoder.yudao.module.medical.controller.admin.aiprompt.vo.AiPromptSaveReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.aiprompt.AiPromptDO;
import jakarta.validation.Valid;

/**
 * AI提示词模板 Service 接口
 *
 * @author 芋道源码
 */
public interface AiPromptService {

    /**
     * 创建AI提示词模板
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAiPrompt(@Valid AiPromptSaveReqVO createReqVO);

    /**
     * 更新AI提示词模板
     *
     * @param updateReqVO 更新信息
     */
    void updateAiPrompt(@Valid AiPromptSaveReqVO updateReqVO);

    /**
     * 删除AI提示词模板
     *
     * @param id 编号
     */
    void deleteAiPrompt(Long id);

    /**
     * 获得AI提示词模板
     *
     * @param id 编号
     * @return AI提示词模板
     */
    AiPromptDO getAiPrompt(Long id);

    /**
     * 获得AI提示词模板分页
     *
     * @param pageReqVO 分页查询
     * @return AI提示词模板分页
     */
    PageResult<AiPromptDO> getAiPromptPage(AiPromptPageReqVO pageReqVO);

}