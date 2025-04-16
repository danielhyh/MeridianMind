package cn.iocoder.yudao.module.medical.dal.mysql.fourdiagnosis;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.medical.dal.dataobject.fourdiagnosis.FourDiagnosticDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 四诊信息 Mapper
 */
@Mapper
public interface FourDiagnosticMapper extends BaseMapperX<FourDiagnosticDO> {

    /**
     * 根据问诊记录ID查询四诊信息
     *
     * @param diagnosticId 问诊记录ID
     * @return 四诊信息
     */
    default FourDiagnosticDO selectByDiagnosticId(Long diagnosticId) {
        return selectOne(FourDiagnosticDO::getDiagnosticId, diagnosticId);
    }
}