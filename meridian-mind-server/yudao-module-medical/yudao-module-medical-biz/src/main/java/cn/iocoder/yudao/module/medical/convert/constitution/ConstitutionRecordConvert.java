package cn.iocoder.yudao.module.medical.convert.constitution;

import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionRadarRespVO;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionRecordDetailRespVO;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionRecordRespVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionRecordDO;
import cn.iocoder.yudao.module.medical.enums.ConstitutionTypeEnum;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * 体质评估记录 Convert
 */
@Mapper
public interface ConstitutionRecordConvert {

    ConstitutionRecordConvert INSTANCE = Mappers.getMapper(ConstitutionRecordConvert.class);

    ConstitutionRecordRespVO convert(ConstitutionRecordDO bean);

    ConstitutionRecordDetailRespVO convertDetail(ConstitutionRecordDO bean);

    List<ConstitutionRecordRespVO> convertList(List<ConstitutionRecordDO> list);
    /**
     * 将评估记录转换为雷达图数据
     */
    default ConstitutionRadarRespVO convertToRadar(ConstitutionRecordDO record) {
        if (record == null) {
            return null;
        }

        ConstitutionRadarRespVO vo = new ConstitutionRadarRespVO();
        vo.setId(record.getId());
        vo.setCreateTime(record.getCreateTime());
        vo.setPrimaryType(record.getPrimaryType());
        vo.setPrimaryTypeName(ConstitutionTypeEnum.nameOf(record.getPrimaryType()));

        // 转换各体质类型评分
        if (record.getScores() != null) {
            List<ConstitutionRadarRespVO.TypeScore> typeScores = new ArrayList<>();
            record.getScores().forEach((type, score) -> {
                ConstitutionRadarRespVO.TypeScore typeScore = new ConstitutionRadarRespVO.TypeScore();
                typeScore.setType(type);
                typeScore.setName(ConstitutionTypeEnum.nameOf(type));
                typeScore.setScore(score);
                typeScores.add(typeScore);
            });
            vo.setTypeScores(typeScores);
        }

        return vo;
    }
}