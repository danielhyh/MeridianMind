package cn.iocoder.yudao.module.medical.service.patient;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.medical.controller.admin.patient.vo.PatientProfilePageReqVO;
import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppPatientProfileRespVO;
import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppPatientProfileSaveReqVO;
import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppPatientProfileUpdateReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.patient.PatientProfileDO;
import jakarta.validation.Valid;

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
    Long createPatientProfile(@Valid AppPatientProfileSaveReqVO createReqVO);

    /**
     * 更新患者管理
     *
     * @param updateReqVO 更新信息
     */
    void updatePatientProfile(@Valid AppPatientProfileUpdateReqVO updateReqVO);

    /**
     * 删除患者管理
     *
     * @param id 编号
     */
    void deletePatientProfile(Long id);

    /**
     * 获得患者管理
     *
     * @param userId 会员编号
     * @return 患者管理
     */
    AppPatientProfileRespVO getPatientProfile(Long userId);

    /**
     * 获得患者管理分页
     *
     * @param pageReqVO 分页查询
     * @return 患者管理分页
     */
    PageResult<PatientProfileDO> getPatientProfilePage(PatientProfilePageReqVO pageReqVO);
    /**
     * 更新用户体质类型
     *
     * @param useId 用户ID
     * @param recordId 体质类型
     */
    void updateConstitutionType(Long useId, Long recordId);

}