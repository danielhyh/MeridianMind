package cn.iocoder.yudao.module.medical.service.diagnosistreatment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosistreatment.vo.DiagnosisTreatmentPageReqVO;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosistreatment.vo.DiagnosisTreatmentSaveReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnosistreatment.DiagnosisTreatmentDO;
import jakarta.validation.Valid;

/**
 * 中医诊断治疗一体化 Service 接口
 *
 * @author fly
 */
public interface DiagnosisTreatmentService {

    /**
     * 创建中医诊断治疗一体化
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDiagnosisTreatment(@Valid DiagnosisTreatmentSaveReqVO createReqVO);

    /**
     * 更新中医诊断治疗一体化
     *
     * @param updateReqVO 更新信息
     */
    void updateDiagnosisTreatment(@Valid DiagnosisTreatmentSaveReqVO updateReqVO);

    /**
     * 删除中医诊断治疗一体化
     *
     * @param id 编号
     */
    void deleteDiagnosisTreatment(Long id);

    /**
     * 获得中医诊断治疗一体化
     *
     * @param id 编号
     * @return 中医诊断治疗一体化
     */
    DiagnosisTreatmentDO getDiagnosisTreatment(Long id);

    /**
     * 获得中医诊断治疗一体化分页
     *
     * @param pageReqVO 分页查询
     * @return 中医诊断治疗一体化分页
     */
    PageResult<DiagnosisTreatmentDO> getDiagnosisTreatmentPage(DiagnosisTreatmentPageReqVO pageReqVO);

}