package cn.iocoder.yudao.module.medical.service.patient;

import jakarta.validation.*;
import cn.iocoder.yudao.module.medical.controller.admin.patient.vo.*;
import cn.iocoder.yudao.module.medical.dal.dataobject.patient.PatientProfileDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 患者管理 Service 接口
 *
 * @author 芋道源码
 */
public interface PatientService {

    /**
     * 创建患者管理
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPatientProfile(@Valid PatientProfileSaveReqVO createReqVO);

    /**
     * 更新患者管理
     *
     * @param updateReqVO 更新信息
     */
    void updatePatientProfile(@Valid PatientProfileSaveReqVO updateReqVO);

    /**
     * 删除患者管理
     *
     * @param id 编号
     */
    void deletePatientProfile(Long id);

    /**
     * 获得患者管理
     *
     * @param id 编号
     * @return 患者管理
     */
    PatientProfileDO getPatientProfile(Long id);

    /**
     * 获得患者管理分页
     *
     * @param pageReqVO 分页查询
     * @return 患者管理分页
     */
    PageResult<PatientProfileDO> getPatientProfilePage(PatientProfilePageReqVO pageReqVO);

}