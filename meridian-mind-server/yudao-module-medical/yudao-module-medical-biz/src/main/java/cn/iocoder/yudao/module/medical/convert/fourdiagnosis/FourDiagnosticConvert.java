package cn.iocoder.yudao.module.medical.convert.fourdiagnosis;

import cn.iocoder.yudao.module.medical.controller.app.fourdiagnosis.vo.AppFourDiagnosticRespVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.fourdiagnosis.FourDiagnosticDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 四诊信息 Convert
 */
@Mapper
public interface FourDiagnosticConvert {

    FourDiagnosticConvert INSTANCE = Mappers.getMapper(FourDiagnosticConvert.class);

    /**
     * DO 转 RespVO
     */
    AppFourDiagnosticRespVO convert(FourDiagnosticDO bean);
}