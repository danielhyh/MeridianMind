package cn.iocoder.yudao.module.medical.convert.constitution;

import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionQuestionCreateReqVO;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionQuestionRespVO;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionQuestionUpdateReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionQuestionDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 体质评估问题 Convert
 */
@Mapper
public interface ConstitutionQuestionConvert {

    ConstitutionQuestionConvert INSTANCE = Mappers.getMapper(ConstitutionQuestionConvert.class);

    ConstitutionQuestionDO convert(ConstitutionQuestionCreateReqVO bean);

    ConstitutionQuestionDO convert(ConstitutionQuestionUpdateReqVO bean);

    ConstitutionQuestionRespVO convert(ConstitutionQuestionDO bean);

    List<ConstitutionQuestionRespVO> convertList(List<ConstitutionQuestionDO> list);
}