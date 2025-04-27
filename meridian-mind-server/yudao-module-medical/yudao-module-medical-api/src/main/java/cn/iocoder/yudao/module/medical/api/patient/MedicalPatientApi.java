package cn.iocoder.yudao.module.medical.api.patient;

import cn.iocoder.yudao.module.medical.api.patient.dto.PatientProfileCreateReqDTO;
import jakarta.validation.Valid;

/**
 * 患者服务 API 接口
 */
public interface MedicalPatientApi {

    /**
     * 创建患者健康档案
     *
     * @param createReq 创建信息
     * @return 编号
     */
    Long createPatientProfile(@Valid PatientProfileCreateReqDTO createReq);
}