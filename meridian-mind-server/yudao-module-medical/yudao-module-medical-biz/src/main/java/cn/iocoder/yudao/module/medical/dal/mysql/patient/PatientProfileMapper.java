package cn.iocoder.yudao.module.medical.dal.mysql.patient;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.medical.controller.admin.patient.vo.PatientProfilePageReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.patient.PatientProfileDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 患者管理 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface PatientProfileMapper extends BaseMapperX<PatientProfileDO> {

    default PageResult<PatientProfileDO> selectPage(PatientProfilePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PatientProfileDO>()
                .eqIfPresent(PatientProfileDO::getUserId, reqVO.getUserId())
                .eqIfPresent(PatientProfileDO::getHeight, reqVO.getHeight())
                .eqIfPresent(PatientProfileDO::getWeight, reqVO.getWeight())
                .eqIfPresent(PatientProfileDO::getBloodType, reqVO.getBloodType())
                .eqIfPresent(PatientProfileDO::getAllergies, reqVO.getAllergies())
                .eqIfPresent(PatientProfileDO::getMedicalHistory, reqVO.getMedicalHistory())
                .eqIfPresent(PatientProfileDO::getFamilyMedicalHistory, reqVO.getFamilyMedicalHistory())
                .eqIfPresent(PatientProfileDO::getConstitutionType, reqVO.getConstitutionType())
                .betweenIfPresent(PatientProfileDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PatientProfileDO::getId));
    }

    default PatientProfileDO selectByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<PatientProfileDO>().eq(PatientProfileDO::getUserId, userId));
    }
}