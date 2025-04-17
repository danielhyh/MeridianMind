package cn.iocoder.yudao.module.medical.dal.mysql.constitution;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionQuestionListReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionQuestionDO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper
public interface ConstitutionQuestionMapper extends BaseMapperX<ConstitutionQuestionDO> {
    // 获取问卷题目
    default List<ConstitutionQuestionDO> selectQuestionsByQuestionnaireId(Long questionnaireId) {
        return selectList(new LambdaQueryWrapperX<ConstitutionQuestionDO>()
                .eq(ConstitutionQuestionDO::getQuestionnaireId, questionnaireId)
                .orderByAsc(ConstitutionQuestionDO::getSort));
    }

    default List<ConstitutionQuestionDO> selectList(ConstitutionQuestionListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<ConstitutionQuestionDO>()
                .eqIfPresent(ConstitutionQuestionDO::getQuestionnaireId, reqVO.getQuestionnaireId())
                .likeIfPresent(ConstitutionQuestionDO::getQuestion, reqVO.getQuestion())
                .eqIfPresent(ConstitutionQuestionDO::getConstitutionType, reqVO.getConstitutionType())
                .orderByAsc(ConstitutionQuestionDO::getSort));
    }

    default List<ConstitutionQuestionDO> selectListByQuestionnaireId(Long questionnaireId) {
        return selectList(new LambdaQueryWrapperX<ConstitutionQuestionDO>()
                .eq(ConstitutionQuestionDO::getQuestionnaireId, questionnaireId)
                .orderByAsc(ConstitutionQuestionDO::getSort));
    }

    default void deleteByQuestionnaireId(Long questionnaireId) {
        delete(new LambdaQueryWrapperX<ConstitutionQuestionDO>()
                .eq(ConstitutionQuestionDO::getQuestionnaireId, questionnaireId));
    }

    /**
     * 查询问卷指定步骤的所有问题
     */
    default List<ConstitutionQuestionDO> selectByQuestionnaireIdAndStep(Long questionnaireId, String step) {
        return selectList(new LambdaQueryWrapperX<ConstitutionQuestionDO>()
                .eq(ConstitutionQuestionDO::getQuestionnaireId, questionnaireId)
                .eq(ConstitutionQuestionDO::getStep, step)
                .orderByAsc(ConstitutionQuestionDO::getSort));
    }

    /**
     * 批量统计多个问卷的题目数量
     *
     * @param questionnaireIds 问卷ID列表
     * @return 问卷ID -> 题目数量的映射
     */
    default Map<Long, Long> selectCountByQuestionnaireIds(Collection<Long> questionnaireIds) {
        if (CollUtil.isEmpty(questionnaireIds)) {
            return new HashMap<>();
        }

        // 使用MyBatis-Plus的单次查询获取所有问卷ID的题目数量
        List<Map<String, Object>> countResults = selectMaps(
                new QueryWrapper<ConstitutionQuestionDO>()
                        .select("questionnaire_id, count(*) as count")
                        .in("questionnaire_id", questionnaireIds)
                        .groupBy("questionnaire_id")
        );

        // 将结果转换为需要的Map格式
        Map<Long, Long> resultMap = countResults.stream()
                .collect(Collectors.toMap(
                        item -> ((Number) item.get("questionnaire_id")).longValue(),
                        item -> ((Number) item.get("count")).longValue()
                ));

        // 补充没有数据的问卷ID，设置数量为0
        questionnaireIds.forEach(id -> resultMap.putIfAbsent(id, 0L));

        return resultMap;
    }
}