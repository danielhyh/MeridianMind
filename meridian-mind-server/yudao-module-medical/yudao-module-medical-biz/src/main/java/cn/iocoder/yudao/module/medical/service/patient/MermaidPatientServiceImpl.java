package cn.iocoder.yudao.module.medical.service.patient;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.medical.controller.admin.patient.vo.PatientProfilePageReqVO;
import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppPatientProfileSaveReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.patient.PatientProfileDO;
import cn.iocoder.yudao.module.medical.dal.mysql.patient.PatientProfileMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.medical.enums.ErrorCodeConstants.PATIENT_PROFILE_NOT_EXISTS;

/**
 * 患者管理 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MermaidPatientServiceImpl implements MermaidPatientService {

    @Resource
    private PatientProfileMapper patientProfileMapper;

    @Override
    public Long createPatientProfile(AppPatientProfileSaveReqVO createReqVO) {
        // 插入
        PatientProfileDO patientProfile = BeanUtils.toBean(createReqVO, PatientProfileDO.class);
        patientProfileMapper.insert(patientProfile);
        // 返回
        return patientProfile.getId();
    }

    @Override
    public void updatePatientProfile(AppPatientProfileSaveReqVO updateReqVO) {
        // 校验存在
        validatePatientProfileExists(updateReqVO.getId());
        // 更新
        PatientProfileDO updateObj = BeanUtils.toBean(updateReqVO, PatientProfileDO.class);
        patientProfileMapper.updateById(updateObj);
    }

    @Override
    public void deletePatientProfile(Long id) {
        // 校验存在
        validatePatientProfileExists(id);
        // 删除
        patientProfileMapper.deleteById(id);
    }

    private void validatePatientProfileExists(Long id) {
        if (patientProfileMapper.selectById(id) == null) {
            throw exception(PATIENT_PROFILE_NOT_EXISTS);
        }
    }

    @Override
    public PatientProfileDO getPatientProfile(Long memberId) {
        return patientProfileMapper.selectByMemberId(memberId);
    }

    @Override
    public PageResult<PatientProfileDO> getPatientProfilePage(PatientProfilePageReqVO pageReqVO) {
        return patientProfileMapper.selectPage(pageReqVO);
    }

}