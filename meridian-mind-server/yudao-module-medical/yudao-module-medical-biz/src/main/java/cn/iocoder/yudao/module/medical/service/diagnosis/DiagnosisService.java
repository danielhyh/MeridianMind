package cn.iocoder.yudao.module.medical.service.diagnosis;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosis.vo.DiagnosisPageReqVO;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosis.vo.DiagnosisSaveReqVO;
import cn.iocoder.yudao.module.medical.controller.app.diagnosis.vo.AppDiagnosisResultRespVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnosis.DiagnosisDO;
import jakarta.validation.Valid;

/**
 * 诊断结果 Service 接口
 *
 * @author 芋道源码
 */
public interface DiagnosisService {

    /**
     * 创建诊断结果
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDiagnosis(@Valid DiagnosisSaveReqVO createReqVO);

    /**
     * 更新诊断结果
     *
     * @param updateReqVO 更新信息
     */
    void updateDiagnosis(@Valid DiagnosisSaveReqVO updateReqVO);

    /**
     * 删除诊断结果
     *
     * @param id 编号
     */
    void deleteDiagnosis(Long id);

    /**
     * 获得诊断结果
     *
     * @param id 编号
     * @return 诊断结果
     */
    DiagnosisDO getDiagnosis(Long id);

    /**
     * 获得诊断结果分页
     *
     * @param pageReqVO 分页查询
     * @return 诊断结果分页
     */
    PageResult<DiagnosisDO> getDiagnosisPage(DiagnosisPageReqVO pageReqVO);
    /**
     * 生成诊断结果
     *
     * @param userId 用户ID
     * @param diagnosticId 问诊记录ID
     * @return 诊断结果
     */
    AppDiagnosisResultRespVO generateDiagnosis(Long userId, Long diagnosticId);

    /**
     * 保存诊断结果
     *
     * @param diagnosticId 问诊记录ID
     * @param diagnosisResult 诊断结果
     * @return 诊断ID
     */
    Long saveDiagnosisResult(Long diagnosticId, AppDiagnosisResultRespVO diagnosisResult);

    /**
     * 获取诊断结果
     *
     * @param diagnosticId 问诊记录ID
     * @return 诊断结果
     */
    AppDiagnosisResultRespVO getDiagnosisResult(Long diagnosticId);

}