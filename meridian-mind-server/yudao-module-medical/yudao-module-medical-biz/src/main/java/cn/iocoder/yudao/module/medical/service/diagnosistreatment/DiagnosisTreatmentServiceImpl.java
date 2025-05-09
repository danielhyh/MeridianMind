package cn.iocoder.yudao.module.medical.service.diagnosistreatment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosistreatment.vo.DiagnosisTreatmentPageReqVO;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosistreatment.vo.DiagnosisTreatmentSaveReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnosistreatment.DiagnosisTreatmentDO;
import cn.iocoder.yudao.module.medical.dal.mysql.diagnosistreatment.DiagnosisTreatmentMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.medical.enums.ErrorCodeConstants.DIAGNOSIS_TREATMENT_NOT_EXISTS;

/**
 * 中医诊断治疗一体化 Service 实现类
 *
 * @author fly
 */
@Service
@Validated
public class DiagnosisTreatmentServiceImpl implements DiagnosisTreatmentService {

    @Resource
    private DiagnosisTreatmentMapper diagnosisTreatmentMapper;

    @Override
    public Long createDiagnosisTreatment(DiagnosisTreatmentSaveReqVO createReqVO) {
        // 插入
        DiagnosisTreatmentDO diagnosisTreatment = BeanUtils.toBean(createReqVO, DiagnosisTreatmentDO.class);
        diagnosisTreatmentMapper.insert(diagnosisTreatment);
        // 返回
        return diagnosisTreatment.getId();
    }

    @Override
    public void updateDiagnosisTreatment(DiagnosisTreatmentSaveReqVO updateReqVO) {
        // 校验存在
        validateDiagnosisTreatmentExists(updateReqVO.getId());
        // 更新
        DiagnosisTreatmentDO updateObj = BeanUtils.toBean(updateReqVO, DiagnosisTreatmentDO.class);
        diagnosisTreatmentMapper.updateById(updateObj);
    }

    @Override
    public void deleteDiagnosisTreatment(Long id) {
        // 校验存在
        validateDiagnosisTreatmentExists(id);
        // 删除
        diagnosisTreatmentMapper.deleteById(id);
    }

    private void validateDiagnosisTreatmentExists(Long id) {
        if (diagnosisTreatmentMapper.selectById(id) == null) {
            throw exception(DIAGNOSIS_TREATMENT_NOT_EXISTS);
        }
    }

    @Override
    public DiagnosisTreatmentDO getDiagnosisTreatment(Long id) {
        return diagnosisTreatmentMapper.selectById(id);
    }

    @Override
    public PageResult<DiagnosisTreatmentDO> getDiagnosisTreatmentPage(DiagnosisTreatmentPageReqVO pageReqVO) {
        return diagnosisTreatmentMapper.selectPage(pageReqVO);
    }

}