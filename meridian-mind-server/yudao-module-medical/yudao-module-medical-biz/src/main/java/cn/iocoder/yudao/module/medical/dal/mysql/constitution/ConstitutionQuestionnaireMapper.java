package cn.iocoder.yudao.module.medical.dal.mysql.constitution;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionQuestionnairePageReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionQuestionnaireDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConstitutionQuestionnaireMapper extends BaseMapperX<ConstitutionQuestionnaireDO> {
    /**
     * 获取最新的启用状态问卷
     */
    default ConstitutionQuestionnaireDO selectLatestEnabledQuestionnaire() {
        return selectOne(new LambdaQueryWrapperX<ConstitutionQuestionnaireDO>()
               .eq(ConstitutionQuestionnaireDO::getStatus, CommonStatusEnum.ENABLE.getStatus())
               .orderByDesc(ConstitutionQuestionnaireDO::getCreateTime)
               .last("LIMIT 1"));
    }
    default PageResult<ConstitutionQuestionnaireDO> selectPage(ConstitutionQuestionnairePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ConstitutionQuestionnaireDO>()
                .likeIfPresent(ConstitutionQuestionnaireDO::getTitle, reqVO.getTitle())
                .eqIfPresent(ConstitutionQuestionnaireDO::getStatus, reqVO.getStatus())
                .likeIfPresent(ConstitutionQuestionnaireDO::getVersion, reqVO.getVersion())
                .betweenIfPresent(ConstitutionQuestionnaireDO::getCreateTime, reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .orderByDesc(ConstitutionQuestionnaireDO::getId));
    }

}