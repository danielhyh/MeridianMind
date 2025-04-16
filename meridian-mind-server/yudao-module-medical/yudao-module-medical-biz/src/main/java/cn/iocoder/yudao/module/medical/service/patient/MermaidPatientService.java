package cn.iocoder.yudao.module.medical.service.patient;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.medical.controller.admin.patient.vo.PatientProfilePageReqVO;
import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppPatientProfileSaveReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.patient.PatientProfileDO;
import jakarta.validation.Valid;

/**
 * 患者管理 Service 接口
 *
 * @author 芋道源码
 */
public interface MermaidPatientService {

    /**
     * 创建患者管理
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPatientProfile(@Valid AppPatientProfileSaveReqVO createReqVO);

    /**
     * 更新患者管理
     *
     * @param updateReqVO 更新信息
     */
    void updatePatientProfile(@Valid AppPatientProfileSaveReqVO updateReqVO);

    /**
     * 删除患者管理
     *
     * @param id 编号
     */
    void deletePatientProfile(Long id);

    /**
     * 获得患者管理
     *
     * @param memberId 会员编号
     * @return 患者管理
     */
    PatientProfileDO getPatientProfile(Long memberId);

    /**
     * 获得患者管理分页
     *
     * @param pageReqVO 分页查询
     * @return 患者管理分页
     */
    PageResult<PatientProfileDO> getPatientProfilePage(PatientProfilePageReqVO pageReqVO);

}