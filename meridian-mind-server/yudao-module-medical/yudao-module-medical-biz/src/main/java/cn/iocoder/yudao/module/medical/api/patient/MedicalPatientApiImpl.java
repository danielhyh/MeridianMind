package cn.iocoder.yudao.module.medical.api.patient;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.medical.api.patient.dto.PatientProfileCreateReqDTO;
import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppPatientProfileSaveReqVO;
import cn.iocoder.yudao.module.medical.service.patient.PatientService;
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
    private PatientService patientService;
    
    @Override
    public Long createPatientProfile(PatientProfileCreateReqDTO createReq) {
        // 转换请求对象
        AppPatientProfileSaveReqVO reqVO = BeanUtils.toBean(createReq, AppPatientProfileSaveReqVO.class);
        return patientService.createPatientProfile(reqVO);
    }
}