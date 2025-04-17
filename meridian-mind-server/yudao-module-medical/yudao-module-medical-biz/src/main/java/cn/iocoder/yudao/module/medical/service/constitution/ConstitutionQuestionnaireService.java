package cn.iocoder.yudao.module.medical.service.constitution;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.*;
import cn.iocoder.yudao.module.medical.controller.app.constitution.vo.AppConstitutionQuestionnaireRespVO;
import cn.iocoder.yudao.module.medical.controller.app.constitution.vo.AppConstitutionResultRespVO;
import cn.iocoder.yudao.module.medical.controller.app.constitution.vo.AppConstitutionSubmitReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionQuestionnaireDO;

/**
 * 体质评估问卷 Service 接口
 */
public interface ConstitutionQuestionnaireService {
    /**
     * 发起体质评估
     *
     * @param userId 用户ID
     * @param questionnaireId 问卷ID
     * @return 评估记录ID
     */
    Long initiateAssessment(Long userId, Long questionnaireId);

    /**
     * 应用评估结果到用户档案
     *
     * @param userId 用户ID
     * @param recordId 评估记录ID
     */
    void applyToProfile(Long userId, Long recordId);

    /**
     * 获取最新的体质评估问卷
     *
     * @return 问卷信息
     */
    AppConstitutionQuestionnaireRespVO getLatestQuestionnaire();

    /**
     * 提交体质评估问卷
     *
     * @param userId 用户ID
     * @param reqVO  提交信息
     * @return 评估结果
     */
    AppConstitutionResultRespVO submitQuestionnaire(Long userId, AppConstitutionSubmitReqVO reqVO);

    /**
     * 获取用户最近的体质评估结果
     *
     * @param userId 用户ID
     * @return 评估结果
     */
    AppConstitutionResultRespVO getLatestResult(Long userId);

    /**
     * 创建体质评估问卷
     *
     * @param createReqVO 创建信息
     * @return 问卷ID
     */
    Long createQuestionnaire(ConstitutionQuestionnaireCreateReqVO createReqVO);

    /**
     * 更新体质评估问卷
     *
     * @param updateReqVO 更新信息
     */
    void updateQuestionnaire(ConstitutionQuestionnaireUpdateReqVO updateReqVO);

    /**
     * 删除体质评估问卷
     *
     * @param id 问卷ID
     */
    void deleteQuestionnaire(Long id);

    /**
     * 获取体质评估问卷
     *
     * @param id 问卷ID
     * @return 问卷信息
     */
    ConstitutionQuestionnaireRespVO getQuestionnaire(Long id);

    /**
     * 获取体质评估问卷分页
     *
     * @param pageReqVO 分页条件
     * @return 问卷分页
     */
    PageResult<ConstitutionQuestionnaireRespVO> getQuestionnairePage(ConstitutionQuestionnairePageReqVO pageReqVO);

    /**
     * 更新问卷状态
     *
     * @param reqVO 更新状态请求
     */
    void updateQuestionnaireStatus(ConstitutionQuestionnaireUpdateStatusReqVO reqVO);

    /**
     * 获取问卷DO对象
     *
     * @param id 问卷ID
     * @return 问卷DO
     */
    ConstitutionQuestionnaireDO getQuestionnaireDO(Long id);
}