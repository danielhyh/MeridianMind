package cn.iocoder.yudao.module.medical.dal.mysql.constitution;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionRecordPageReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ConstitutionRecordMapper extends BaseMapperX<ConstitutionRecordDO> {
    // 获取用户最新的评估记录
    default ConstitutionRecordDO selectLatestByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<ConstitutionRecordDO>()
                .eq(ConstitutionRecordDO::getUserId, userId)
                .orderByDesc(ConstitutionRecordDO::getCreateTime)
                .last("LIMIT 1"));
    }

    default PageResult<ConstitutionRecordDO> selectPage(ConstitutionRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ConstitutionRecordDO>()
                .eqIfPresent(ConstitutionRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ConstitutionRecordDO::getQuestionnaireId, reqVO.getQuestionnaireId())
                .eqIfPresent(ConstitutionRecordDO::getPrimaryType, reqVO.getPrimaryType())
                .betweenIfPresent(ConstitutionRecordDO::getCreateTime, reqVO.getBeginCreateTime(), reqVO.getEndCreateTime())
                .orderByDesc(ConstitutionRecordDO::getId));
    }

    default List<ConstitutionRecordDO> selectListByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return selectList(new LambdaQueryWrapperX<ConstitutionRecordDO>()
                .between(ConstitutionRecordDO::getCreateTime, startTime, endTime)
                .orderByAsc(ConstitutionRecordDO::getCreateTime));
    }

    /**
     * 统计指定时间范围内的评估记录数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 记录数
     */
    default Integer countByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return Math.toIntExact(selectCount(new LambdaQueryWrapperX<ConstitutionRecordDO>()
                .between(ConstitutionRecordDO::getCreateTime, startTime, endTime)));
    }

    /**
     * 统计指定时间范围内已完成的评估记录数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 已完成记录数
     */
    default Integer countCompletedAssessmentsByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return Math.toIntExact(selectCount(new LambdaQueryWrapperX<ConstitutionRecordDO>()
                .between(ConstitutionRecordDO::getCreateTime, startTime, endTime)
                .eq(ConstitutionRecordDO::getIsCompleted, true)));
    }

    /**
     * 统计指定时间范围内的总评估记录数（包括未完成的）
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 总记录数
     */
    default Integer countTotalAssessmentsByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return Math.toIntExact(selectCount(new LambdaQueryWrapperX<ConstitutionRecordDO>()
                .between(ConstitutionRecordDO::getCreateTime, startTime, endTime)));
    }
    default List<ConstitutionRecordDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<ConstitutionRecordDO>()
                .eq(ConstitutionRecordDO::getUserId, userId)
                .orderByDesc(ConstitutionRecordDO::getCreateTime));
    }

}