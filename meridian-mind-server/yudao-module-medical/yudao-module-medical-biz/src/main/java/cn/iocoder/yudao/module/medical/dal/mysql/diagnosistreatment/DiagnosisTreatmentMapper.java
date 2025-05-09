package cn.iocoder.yudao.module.medical.dal.mysql.diagnosistreatment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosistreatment.vo.DiagnosisTreatmentPageReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnosistreatment.DiagnosisTreatmentDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 中医诊断治疗一体化 Mapper
 *
 * @author fly
 */
@Mapper
public interface DiagnosisTreatmentMapper extends BaseMapperX<DiagnosisTreatmentDO> {

    default PageResult<DiagnosisTreatmentDO> selectPage(DiagnosisTreatmentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DiagnosisTreatmentDO>()
                .eqIfPresent(DiagnosisTreatmentDO::getDiagnosticId, reqVO.getDiagnosticId())
                .eqIfPresent(DiagnosisTreatmentDO::getUserId, reqVO.getUserId())
                .eqIfPresent(DiagnosisTreatmentDO::getPrimarySyndrome, reqVO.getPrimarySyndrome())
                .eqIfPresent(DiagnosisTreatmentDO::getPrimarySyndromeExplanation, reqVO.getPrimarySyndromeExplanation())
                .eqIfPresent(DiagnosisTreatmentDO::getSecondarySyndromes, reqVO.getSecondarySyndromes())
                .eqIfPresent(DiagnosisTreatmentDO::getEightPrinciples, reqVO.getEightPrinciples())
                .eqIfPresent(DiagnosisTreatmentDO::getSymptoms, reqVO.getSymptoms())
                .eqIfPresent(DiagnosisTreatmentDO::getSyndromeAnalysis, reqVO.getSyndromeAnalysis())
                .eqIfPresent(DiagnosisTreatmentDO::getTreatmentPrinciple, reqVO.getTreatmentPrinciple())
                .eqIfPresent(DiagnosisTreatmentDO::getPrincipleExplanation, reqVO.getPrincipleExplanation())
                .eqIfPresent(DiagnosisTreatmentDO::getPrescriptions, reqVO.getPrescriptions())
                .eqIfPresent(DiagnosisTreatmentDO::getLifestyleAdvice, reqVO.getLifestyleAdvice())
                .eqIfPresent(DiagnosisTreatmentDO::getDietRecipes, reqVO.getDietRecipes())
                .eqIfPresent(DiagnosisTreatmentDO::getFollowUp, reqVO.getFollowUp())
                .eqIfPresent(DiagnosisTreatmentDO::getAiRawOutput, reqVO.getAiRawOutput())
                .eqIfPresent(DiagnosisTreatmentDO::getConfidenceLevel, reqVO.getConfidenceLevel())
                .betweenIfPresent(DiagnosisTreatmentDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DiagnosisTreatmentDO::getId));
    }

}