package cn.iocoder.yudao.module.medical.dal.mysql.aiprompt;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.medical.controller.admin.aiprompt.vo.AiPromptPageReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.aiprompt.AiPromptDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI提示词模板 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiPromptMapper extends BaseMapperX<AiPromptDO> {

    default PageResult<AiPromptDO> selectPage(AiPromptPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiPromptDO>()
                .likeIfPresent(AiPromptDO::getName, reqVO.getName())
                .eqIfPresent(AiPromptDO::getDescription, reqVO.getDescription())
                .eqIfPresent(AiPromptDO::getPromptText, reqVO.getPromptText())
                .eqIfPresent(AiPromptDO::getCategory, reqVO.getCategory())
                .eqIfPresent(AiPromptDO::getParameters, reqVO.getParameters())
                .eqIfPresent(AiPromptDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(AiPromptDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AiPromptDO::getId));
    }

}