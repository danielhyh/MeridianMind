package cn.iocoder.yudao.module.medical.service.diagnostic;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.medical.controller.admin.diagnostic.vo.DiagnosticPageReqVO;
import cn.iocoder.yudao.module.medical.controller.app.diagnostic.vo.AppDiagnosticPageReqVO;
import cn.iocoder.yudao.module.medical.controller.app.diagnostic.vo.AppDiagnosticSaveReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnostic.DiagnosticDO;
import jakarta.validation.Valid;

/**
 * 问诊记录 Service 接口
 *
 * @author 芋道源码
 */
public interface DiagnosticService {

    /**
     * 创建问诊记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDiagnostic(@Valid AppDiagnosticSaveReqVO createReqVO);

    /**
     * 更新问诊记录
     *
     * @param updateReqVO 更新信息
     */
    void updateDiagnostic(@Valid AppDiagnosticSaveReqVO updateReqVO);

    /**
     * 删除问诊记录
     *
     * @param id 编号
     */
    void deleteDiagnostic(Long id);

    /**
     * 获得问诊记录
     *
     * @param id 编号
     * @return 问诊记录
     */
    DiagnosticDO getDiagnostic(Long id);

    /**
     * 获得问诊记录分页
     *
     * @param pageReqVO 分页查询
     * @return 问诊记录分页
     */
    PageResult<DiagnosticDO> getDiagnosticPage(DiagnosticPageReqVO pageReqVO);
    /**
     * APP-获得问诊记录分页
     *
     * @param pageReqVO 分页查询
     * @return 问诊记录分页
     */
    PageResult<DiagnosticDO> getDiagnosticPage(AppDiagnosticPageReqVO pageReqVO);
}