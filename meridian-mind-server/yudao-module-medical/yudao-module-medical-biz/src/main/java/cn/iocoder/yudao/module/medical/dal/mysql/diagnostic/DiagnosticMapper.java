package cn.iocoder.yudao.module.medical.dal.mysql.diagnostic;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.medical.controller.admin.diagnostic.vo.DiagnosticPageReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnostic.DiagnosticDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问诊记录 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface DiagnosticMapper extends BaseMapperX<DiagnosticDO> {

    default PageResult<DiagnosticDO> selectPage(DiagnosticPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DiagnosticDO>()
                .eqIfPresent(DiagnosticDO::getUserId, reqVO.getUserId())
                .eqIfPresent(DiagnosticDO::getDoctorId, reqVO.getDoctorId())
                .betweenIfPresent(DiagnosticDO::getDiagnosticTime, reqVO.getDiagnosticTime())
                .eqIfPresent(DiagnosticDO::getChiefComplaint, reqVO.getChiefComplaint())
                .eqIfPresent(DiagnosticDO::getIllnessHistory, reqVO.getIllnessHistory())
                .eqIfPresent(DiagnosticDO::getMedicalHistory, reqVO.getMedicalHistory())
                .eqIfPresent(DiagnosticDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(DiagnosticDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DiagnosticDO::getId));
    }

}