package cn.iocoder.yudao.module.medical.api.patient;

import cn.iocoder.yudao.module.medical.api.patient.dto.PatientProfileCreateReqDTO;
import cn.iocoder.yudao.module.medical.api.patient.dto.PatientProfileDTO;
import cn.iocoder.yudao.module.medical.api.patient.dto.PatientProfileUpdateReqDTO;
import jakarta.validation.Valid;

/**
 * 患者服务 API 接口
 */
public interface MedicalPatientApi {

    /**
     * 获得患者健康档案
     *
     * @param memberId 会员用户ID
     * @return 患者健康档案信息
     */
    PatientProfileDTO getPatientProfile(Long memberId);

    /**
     * 创建患者健康档案
     *
     * @param createReq 创建信息
     * @return 编号
     */
    Long createPatientProfile(@Valid PatientProfileCreateReqDTO createReq);

    /**
     * 更新患者健康档案
     *
     * @param updateReq 更新信息
     */
    void updatePatientProfile(@Valid PatientProfileUpdateReqDTO updateReq);

    /**
     * 检查患者是否存在
     *
     * @param memberId 会员用户ID
     * @return 是否存在
     */
    boolean isPatientExists(Long memberId);
}