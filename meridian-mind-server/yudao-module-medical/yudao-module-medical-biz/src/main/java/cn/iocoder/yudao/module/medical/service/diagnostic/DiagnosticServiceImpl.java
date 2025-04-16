package cn.iocoder.yudao.module.medical.service.diagnostic;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.medical.controller.admin.diagnostic.vo.DiagnosticPageReqVO;
import cn.iocoder.yudao.module.medical.controller.app.diagnostic.vo.AppDiagnosticPageReqVO;
import cn.iocoder.yudao.module.medical.controller.app.diagnostic.vo.AppDiagnosticSaveReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnostic.DiagnosticDO;
import cn.iocoder.yudao.module.medical.dal.mysql.diagnostic.DiagnosticMapper;
import cn.iocoder.yudao.module.medical.service.fourdiagnosis.FourDiagnosticService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.medical.enums.ErrorCodeConstants.DIAGNOSTIC_NOT_EXISTS;

/**
 * 问诊记录 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class DiagnosticServiceImpl implements DiagnosticService {

    @Resource
    private DiagnosticMapper diagnosticMapper;
    @Resource
    private FourDiagnosticService fourDiagnosticService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDiagnostic(AppDiagnosticSaveReqVO createReqVO) {
        // 插入
        DiagnosticDO diagnostic = BeanUtils.toBean(createReqVO, DiagnosticDO.class);
        diagnosticMapper.insert(diagnostic);
        // 初始化四诊记录
        fourDiagnosticService.createFourDiagnostic(diagnostic.getId());
        // 返回
        return diagnostic.getId();
    }

    @Override
    public void updateDiagnostic(AppDiagnosticSaveReqVO updateReqVO) {
        // 校验存在
        validateDiagnosticExists(updateReqVO.getId());
        // 更新
        DiagnosticDO updateObj = BeanUtils.toBean(updateReqVO, DiagnosticDO.class);
        diagnosticMapper.updateById(updateObj);
    }

    @Override
    public void deleteDiagnostic(Long id) {
        // 校验存在
        validateDiagnosticExists(id);
        // 删除
        diagnosticMapper.deleteById(id);
    }

    private void validateDiagnosticExists(Long id) {
        if (diagnosticMapper.selectById(id) == null) {
            throw exception(DIAGNOSTIC_NOT_EXISTS);
        }
    }

    @Override
    public DiagnosticDO getDiagnostic(Long id) {
        return diagnosticMapper.selectById(id);
    }

    @Override
    public PageResult<DiagnosticDO> getDiagnosticPage(DiagnosticPageReqVO pageReqVO) {
        return diagnosticMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<DiagnosticDO> getDiagnosticPage(AppDiagnosticPageReqVO appPageReqVO) {
        DiagnosticPageReqVO bean = BeanUtils.toBean(appPageReqVO, DiagnosticPageReqVO.class);
        return diagnosticMapper.selectPage(bean);
    }

}