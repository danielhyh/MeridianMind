package cn.iocoder.yudao.module.medical.service.aiprompt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.medical.controller.admin.aiprompt.vo.AiPromptPageReqVO;
import cn.iocoder.yudao.module.medical.controller.admin.aiprompt.vo.AiPromptSaveReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.aiprompt.AiPromptDO;
import cn.iocoder.yudao.module.medical.dal.mysql.aiprompt.AiPromptMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.medical.enums.ErrorCodeConstants.AI_PROMPT_NOT_EXISTS;

/**
 * AI提示词模板 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AiPromptServiceImpl implements AiPromptService {

    @Resource
    private AiPromptMapper aiPromptMapper;

    @Override
    public Long createAiPrompt(AiPromptSaveReqVO createReqVO) {
        // 插入
        AiPromptDO aiPrompt = BeanUtils.toBean(createReqVO, AiPromptDO.class);
        aiPromptMapper.insert(aiPrompt);
        // 返回
        return aiPrompt.getId();
    }

    @Override
    public void updateAiPrompt(AiPromptSaveReqVO updateReqVO) {
        // 校验存在
        validateAiPromptExists(updateReqVO.getId());
        // 更新
        AiPromptDO updateObj = BeanUtils.toBean(updateReqVO, AiPromptDO.class);
        aiPromptMapper.updateById(updateObj);
    }

    @Override
    public void deleteAiPrompt(Long id) {
        // 校验存在
        validateAiPromptExists(id);
        // 删除
        aiPromptMapper.deleteById(id);
    }

    private void validateAiPromptExists(Long id) {
        if (aiPromptMapper.selectById(id) == null) {
            throw exception(AI_PROMPT_NOT_EXISTS);
        }
    }

    @Override
    public AiPromptDO getAiPrompt(Long id) {
        return aiPromptMapper.selectById(id);
    }

    @Override
    public PageResult<AiPromptDO> getAiPromptPage(AiPromptPageReqVO pageReqVO) {
        return aiPromptMapper.selectPage(pageReqVO);
    }

}