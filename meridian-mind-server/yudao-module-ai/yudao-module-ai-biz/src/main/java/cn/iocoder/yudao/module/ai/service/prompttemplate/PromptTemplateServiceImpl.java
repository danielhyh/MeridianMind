package cn.iocoder.yudao.module.ai.service.prompttemplate;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.prompttemplate.vo.PromptTemplatePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.prompttemplate.vo.PromptTemplateSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.prompttemplate.PromptTemplateDO;
import cn.iocoder.yudao.module.ai.dal.mysql.prompttemplate.PromptTemplateMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.PROMPT_TEMPLATE_NOT_EXISTS;

/**
 * AI提示词模板 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class PromptTemplateServiceImpl implements PromptTemplateService {

    @Resource
    private PromptTemplateMapper promptTemplateMapper;

    @Override
    public Long createPromptTemplate(PromptTemplateSaveReqVO createReqVO) {
        // 插入
        PromptTemplateDO promptTemplate = BeanUtils.toBean(createReqVO, PromptTemplateDO.class);
        promptTemplateMapper.insert(promptTemplate);
        // 返回
        return promptTemplate.getId();
    }

    @Override
    public void updatePromptTemplate(PromptTemplateSaveReqVO updateReqVO) {
        // 校验存在
        validatePromptTemplateExists(updateReqVO.getId());
        // 更新
        PromptTemplateDO updateObj = BeanUtils.toBean(updateReqVO, PromptTemplateDO.class);
        promptTemplateMapper.updateById(updateObj);
    }

    @Override
    public void deletePromptTemplate(Long id) {
        // 校验存在
        validatePromptTemplateExists(id);
        // 删除
        promptTemplateMapper.deleteById(id);
    }

    @Override
    public void validatePromptTemplateExists(Long id) {
        if (promptTemplateMapper.selectById(id) == null) {
            throw exception(PROMPT_TEMPLATE_NOT_EXISTS);
        }
    }

    @Override
    public PromptTemplateDO getPromptTemplate(Long id) {
        return promptTemplateMapper.selectById(id);
    }

    @Override
    public PageResult<PromptTemplateDO> getPromptTemplatePage(PromptTemplatePageReqVO pageReqVO) {
        return promptTemplateMapper.selectPage(pageReqVO);
    }

    @Override
    public String renderPromptTemplate(Long templateId, Map<String, Object> parameters) {
        // 获取模板
        PromptTemplateDO template = getPromptTemplate(templateId);
        if (template == null) {
            throw exception(PROMPT_TEMPLATE_NOT_EXISTS);
        }

        String content = template.getContent();
        if (CollUtil.isEmpty(parameters)) {
            return content;
        }

        // 使用正则表达式匹配所有{{...}}模式
        Pattern pattern = Pattern.compile("\\{\\{([^{}]+)\\}\\}");
        Matcher matcher = pattern.matcher(content);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String placeholder = matcher.group(1).trim();
            // 获取对应参数值
            Object value = parameters.get(placeholder);
            String replacement = (value != null) ? value.toString() : "";

            // 替换当前占位符
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

}