package cn.iocoder.yudao.module.ai.dal.mysql.prompttemplate;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.prompttemplate.vo.PromptTemplatePageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.prompttemplate.PromptTemplateDO;
import cn.iocoder.yudao.module.ai.enums.prompt.AiPromptTemplateStatusEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI提示词模板 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface PromptTemplateMapper extends BaseMapperX<PromptTemplateDO> {

    default PageResult<PromptTemplateDO> selectPage(PromptTemplatePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PromptTemplateDO>()
                .likeIfPresent(PromptTemplateDO::getName, reqVO.getName())
                .eqIfPresent(PromptTemplateDO::getDescription, reqVO.getDescription())
                .eqIfPresent(PromptTemplateDO::getContent, reqVO.getContent())
                .eqIfPresent(PromptTemplateDO::getParameters, reqVO.getParameters())
                .eqIfPresent(PromptTemplateDO::getDomain, reqVO.getDomain())
                .eqIfPresent(PromptTemplateDO::getStatus, reqVO.getStatus())
                .eqIfPresent(PromptTemplateDO::getDefaultModelId, reqVO.getDefaultModelId())
                .eqIfPresent(PromptTemplateDO::getDefaultKnowledgeId, reqVO.getDefaultKnowledgeId())
                .betweenIfPresent(PromptTemplateDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PromptTemplateDO::getId));
    }

    default List<PromptTemplateDO> selectListByDomainAndSubType(Integer domain, Integer subType) {
        return selectList(new LambdaQueryWrapperX<PromptTemplateDO>()
                .eq(PromptTemplateDO::getDomain, domain)
                .eq(PromptTemplateDO::getStatus, AiPromptTemplateStatusEnum.ENABLED.getStatus())); // 仅查启用的
    }

}