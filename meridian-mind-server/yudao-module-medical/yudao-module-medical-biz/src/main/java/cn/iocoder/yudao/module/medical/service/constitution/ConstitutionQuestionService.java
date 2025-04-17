package cn.iocoder.yudao.module.medical.service.constitution;


import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.*;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionQuestionDO;

import java.util.List;
import java.util.Map;

/**
 * 体质评估问题 Service 接口
 */
public interface ConstitutionQuestionService {

    /**
     * 创建体质评估问题
     *
     * @param createReqVO 创建信息
     * @return 问题ID
     */
    Long createQuestion(ConstitutionQuestionCreateReqVO createReqVO);

    /**
     * 更新体质评估问题
     *
     * @param updateReqVO 更新信息
     */
    void updateQuestion(ConstitutionQuestionUpdateReqVO updateReqVO);

    /**
     * 删除体质评估问题
     *
     * @param id 问题ID
     */
    void deleteQuestion(Long id);

    /**
     * 获取体质评估问题
     *
     * @param id 问题ID
     * @return 问题信息
     */
    ConstitutionQuestionRespVO getQuestion(Long id);

    /**
     * 获取体质评估问题列表
     *
     * @param listReqVO 查询条件
     * @return 问题列表
     */
    List<ConstitutionQuestionRespVO> getQuestionList(ConstitutionQuestionListReqVO listReqVO);

    /**
     * 更新问题排序
     *
     * @param reqVO 更新排序请求
     */
    void updateQuestionSort(ConstitutionQuestionUpdateSortReqVO reqVO);

    /**
     * 根据问题ID获取问题DO对象
     *
     * @param id 问题ID
     * @return 问题DO
     */
    ConstitutionQuestionDO getQuestionDO(Long id);

    /**
     * 根据问卷ID获取问题列表
     *
     * @param questionnaireId 问卷ID
     * @return 问题列表
     */
    List<ConstitutionQuestionDO> getQuestionListByQuestionnaireId(Long questionnaireId);

    /**
     * 获取问题映射，Key为问题ID
     *
     * @param questionIds 问题ID列表
     * @return 问题映射
     */
    Map<Long, ConstitutionQuestionDO> getQuestionMap(List<Long> questionIds);
}