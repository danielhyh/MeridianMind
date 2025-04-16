package cn.iocoder.yudao.module.medical.api.patient;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.medical.api.patient.dto.PatientProfileCreateReqDTO;
import cn.iocoder.yudao.module.medical.api.patient.dto.PatientProfileDTO;
import cn.iocoder.yudao.module.medical.api.patient.dto.PatientProfileUpdateReqDTO;
import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppPatientProfileSaveReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.patient.PatientProfileDO;
import cn.iocoder.yudao.module.medical.service.patient.MermaidPatientService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 患者服务 API 实现类
 */
@Service
@Validated
public class MedicalPatientApiImpl implements MedicalPatientApi {

    @Resource
    private MermaidPatientService patientService;
    
    @Override
    public PatientProfileDTO getPatientProfile(Long memberId) {
        PatientProfileDO profile = patientService.getPatientProfile(memberId);
        return BeanUtils.toBean(profile, PatientProfileDTO.class);
    }
    
    @Override
    public Long createPatientProfile(PatientProfileCreateReqDTO createReq) {
        // 转换请求对象
        AppPatientProfileSaveReqVO reqVO = BeanUtils.toBean(createReq, AppPatientProfileSaveReqVO.class);
        return patientService.createPatientProfile(reqVO);
    }
    
    @Override
    public void updatePatientProfile(PatientProfileUpdateReqDTO updateReq) {
        // 转换请求对象
        AppPatientProfileSaveReqVO reqVO = BeanUtils.toBean(updateReq, AppPatientProfileSaveReqVO.class);
        patientService.updatePatientProfile(reqVO);
    }
    
    @Override
    public boolean isPatientExists(Long memberId) {
        return patientService.getPatientProfile(memberId) != null;
    }
}