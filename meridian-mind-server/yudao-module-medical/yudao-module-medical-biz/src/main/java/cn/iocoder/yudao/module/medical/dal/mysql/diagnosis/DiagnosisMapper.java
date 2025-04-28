package cn.iocoder.yudao.module.medical.dal.mysql.diagnosis;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosis.vo.DiagnosisPageReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnosis.DiagnosisDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 诊断结果 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface DiagnosisMapper extends BaseMapperX<DiagnosisDO> {

    default PageResult<DiagnosisDO> selectPage(DiagnosisPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DiagnosisDO>()
                .eqIfPresent(DiagnosisDO::getDiagnosticId, reqVO.getDiagnosticId())
                .eqIfPresent(DiagnosisDO::getPrimarySyndrome, reqVO.getPrimarySyndrome())
                .eqIfPresent(DiagnosisDO::getSecondarySyndromes, reqVO.getSecondarySyndromes())
                .eqIfPresent(DiagnosisDO::getEightPrinciples, reqVO.getEightPrinciples())
                .eqIfPresent(DiagnosisDO::getDiagnosisExplanation, reqVO.getDiagnosisExplanation())
                .eqIfPresent(DiagnosisDO::getAiRawOutput, reqVO.getAiRawOutput())
                .eqIfPresent(DiagnosisDO::getConfidenceLevel, reqVO.getConfidenceLevel())
                .betweenIfPresent(DiagnosisDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DiagnosisDO::getId));
    }

}