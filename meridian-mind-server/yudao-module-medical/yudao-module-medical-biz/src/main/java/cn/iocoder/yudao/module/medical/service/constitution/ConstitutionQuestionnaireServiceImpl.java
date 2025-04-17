package cn.iocoder.yudao.module.medical.service.constitution;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.*;
import cn.iocoder.yudao.module.medical.controller.app.constitution.vo.AppConstitutionQuestionnaireRespVO;
import cn.iocoder.yudao.module.medical.controller.app.constitution.vo.AppConstitutionResultRespVO;
import cn.iocoder.yudao.module.medical.controller.app.constitution.vo.AppConstitutionSubmitReqVO;
import cn.iocoder.yudao.module.medical.convert.constitution.ConstitutionQuestionnaireConvert;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionQuestionDO;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionQuestionnaireDO;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionRecordDO;
import cn.iocoder.yudao.module.medical.dal.mysql.constitution.ConstitutionQuestionMapper;
import cn.iocoder.yudao.module.medical.dal.mysql.constitution.ConstitutionQuestionnaireMapper;
import cn.iocoder.yudao.module.medical.dal.mysql.constitution.ConstitutionRecordMapper;
import cn.iocoder.yudao.module.medical.service.patient.PatientService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.medical.enums.ErrorCodeConstants.*;

/**
 * 体质评估问卷 Service 实现类
 */
@Service
@Slf4j
public class ConstitutionQuestionnaireServiceImpl implements ConstitutionQuestionnaireService {

    @Resource
    private ConstitutionQuestionnaireMapper questionnaireMapper;

    @Resource
    private ConstitutionQuestionMapper questionMapper;

    @Resource
    private ConstitutionRecordMapper recordMapper;

    @Resource
    private PatientService patientService;

    @Override
    public Long initiateAssessment(Long userId, Long questionnaireId) {
        // 校验问卷是否存在
        ConstitutionQuestionnaireDO questionnaire = questionnaireMapper.selectById(questionnaireId);
        if (questionnaire == null || !CommonStatusEnum.ENABLE.getStatus().equals(questionnaire.getStatus())) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 创建未完成的评估记录
        ConstitutionRecordDO record = new ConstitutionRecordDO();
        record.setUserId(userId);
        record.setQuestionnaireId(questionnaireId);
        record.setIsCompleted(false);
        record.setIsApplied(false);
        recordMapper.insert(record);

        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyToProfile(Long userId, Long recordId) {
        // 1. 校验评估记录
        ConstitutionRecordDO record = recordMapper.selectById(recordId);
        if (record == null || !record.getUserId().equals(userId) || !Boolean.TRUE.equals(record.getIsCompleted())) {
            throw exception(RECORD_NOT_EXISTS_OR_NOT_COMPLETED);
        }

        // 2. 如果已经应用过，直接返回
        if (Boolean.TRUE.equals(record.getIsApplied())) {
            return;
        }

        // 3. 更新用户体质类型
        patientService.updateConstitutionType(userId, record.getPrimaryType());

        // 4. 标记记录为已应用
        record.setIsApplied(true);
        recordMapper.updateById(record);
    }

    @Override
    public AppConstitutionQuestionnaireRespVO getLatestQuestionnaire() {
        // 1. 获取最新的启用状态问卷
        ConstitutionQuestionnaireDO questionnaire = questionnaireMapper.selectLatestEnabledQuestionnaire();
        if (questionnaire == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 2. 获取问卷题目
        List<ConstitutionQuestionDO> questions = questionMapper.selectQuestionsByQuestionnaireId(questionnaire.getId());
        if (CollUtil.isEmpty(questions)) {
            throw exception(QUESTIONNAIRE_QUESTION_NOT_EXISTS);
        }

        // 3. 按步骤分组问题
        Map<String, List<ConstitutionQuestionDO>> questionsByStep = questions.stream()
                .collect(Collectors.groupingBy(ConstitutionQuestionDO::getStep));

        // 4. 构建步骤问题列表
        List<AppConstitutionQuestionnaireRespVO.StepQuestions> steps = new ArrayList<>();
        questionsByStep.forEach((step, stepQuestions) -> {
            if (CollUtil.isNotEmpty(stepQuestions)) {
                AppConstitutionQuestionnaireRespVO.StepQuestions stepQuestionsVO = new AppConstitutionQuestionnaireRespVO.StepQuestions();
                stepQuestionsVO.setStep(step);
                stepQuestionsVO.setQuestions(convertQuestions(stepQuestions));
                steps.add(stepQuestionsVO);
            }
        });

        // 按步骤序号排序
        steps.sort(Comparator.comparing(AppConstitutionQuestionnaireRespVO.StepQuestions::getStep));

        // 5. 转换并返回
        AppConstitutionQuestionnaireRespVO respVO = ConstitutionQuestionnaireConvert.INSTANCE.convert(questionnaire);
        respVO.setStepCount(steps.size());
        respVO.setSteps(steps);

        return respVO;
    }

    /**
     * 转换问题列表
     */
    private List<AppConstitutionQuestionnaireRespVO.Question> convertQuestions(List<ConstitutionQuestionDO> questions) {
        return questions.stream().map(q -> {
            AppConstitutionQuestionnaireRespVO.Question questionVO = new AppConstitutionQuestionnaireRespVO.Question();
            questionVO.setId(q.getId());
            questionVO.setQuestion(q.getQuestion());
            questionVO.setQuestionType(q.getQuestionType());

            // 转换选项，但不包含分数信息
            List<AppConstitutionQuestionnaireRespVO.Option> optionVOs = q.getOptions().stream()
                    .map(option -> {
                        AppConstitutionQuestionnaireRespVO.Option optionVO =
                                new AppConstitutionQuestionnaireRespVO.Option();
                        optionVO.setLabel(option.getLabel());
                        optionVO.setValue(option.getValue());
                        return optionVO;
                    })
                    .collect(Collectors.toList());
            questionVO.setOptions(optionVOs);
            questionVO.setSort(q.getSort());

            return questionVO;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppConstitutionResultRespVO submitQuestionnaire(Long userId, AppConstitutionSubmitReqVO reqVO) {
        // 1. 获取或创建评估记录
        ConstitutionRecordDO record;
        if (reqVO.getRecordId() != null) {
            // 更新已有记录
            record = recordMapper.selectById(reqVO.getRecordId());
            if (record == null || !record.getUserId().equals(userId)) {
                throw exception(RECORD_NOT_EXISTS);
            }
        } else {
            // 创建新记录（应该很少走到这个逻辑，因为一般都是先调用initiateAssessment）
            record = new ConstitutionRecordDO();
            record.setUserId(userId);
            record.setQuestionnaireId(reqVO.getQuestionnaireId());
            record.setIsCompleted(false);
            record.setIsApplied(false);
        }

        // 2. 校验问卷
        ConstitutionQuestionnaireDO questionnaire = questionnaireMapper.selectById(reqVO.getQuestionnaireId());
        if (questionnaire == null || !CommonStatusEnum.ENABLE.getStatus().equals(questionnaire.getStatus())) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 3. 获取问卷题目
        List<ConstitutionQuestionDO> questions = questionMapper.selectList(
                ConstitutionQuestionDO::getQuestionnaireId, reqVO.getQuestionnaireId());
        if (CollUtil.isEmpty(questions)) {
            throw exception(QUESTIONNAIRE_QUESTION_NOT_EXISTS);
        }

        // 4. 计算体质评估得分
        Map<String, Integer> constitutionScores = calculateConstitutionScores(questions, reqVO.getAnswers());

        // 5. 更新评估记录
        record.setAnswers(reqVO.getAnswers());
        record.setScores(constitutionScores);

        // 确定主要体质和次要体质
        determineConstitutionTypes(record, constitutionScores);

        // 标记为已完成
        record.setIsCompleted(true);

        if (record.getId() == null) {
            recordMapper.insert(record);
        } else {
            recordMapper.updateById(record);
        }

        // 6. 转换并返回结果（只返回结果，不更新用户档案）
        return ConstitutionQuestionnaireConvert.INSTANCE.convert(record);
    }

    /**
     * 确定体质类型
     */
    private void determineConstitutionTypes(ConstitutionRecordDO record, Map<String, Integer> constitutionScores) {
        // 确定主要体质类型和次要体质类型
        String primaryType = null;
        int highestScore = 0;
        List<String> secondaryTypes = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : constitutionScores.entrySet()) {
            if (entry.getValue() > highestScore) {
                // 如果之前有主要体质，降级为次要体质
                if (primaryType != null) {
                    secondaryTypes.add(primaryType);
                }
                highestScore = entry.getValue();
                primaryType = entry.getKey();
            } else if (entry.getValue() >= 60) { // 超过60分作为次要体质
                secondaryTypes.add(entry.getKey());
            }
        }

        // 如果没有超过60分的体质，默认为平和质
        if (primaryType == null || constitutionScores.get(primaryType) < 60) {
            primaryType = "neutral"; // 平和质
            secondaryTypes.clear();
        }

        record.setPrimaryType(primaryType);
        record.setSecondaryTypes(secondaryTypes);
    }

    @Override
    public AppConstitutionResultRespVO getLatestResult(Long userId) {
        // 1. 获取用户最新的评估记录
        ConstitutionRecordDO record = recordMapper.selectLatestByUserId(userId);
        if (record == null) {
            return null;
        }

        // 2. 转换并返回结果
        return ConstitutionQuestionnaireConvert.INSTANCE.convert(record);
    }

    /**
     * 计算各体质类型得分
     *
     * @param questions 问卷题目
     * @param answers   用户答案
     * @return 体质得分映射
     */
    private Map<String, Integer> calculateConstitutionScores(List<ConstitutionQuestionDO> questions,
                                                             Map<Long, String> answers) {
        // 初始化体质类型得分映射
        Map<String, Integer> scores = new HashMap<>();
        // 按体质类型分组题目
        Map<String, List<ConstitutionQuestionDO>> questionsByType = questions.stream()
                .filter(q -> StrUtil.isNotBlank(q.getConstitutionType()))
                .collect(Collectors.groupingBy(ConstitutionQuestionDO::getConstitutionType));

        // 计算每个体质类型的得分
        questionsByType.forEach((type, typeQuestions) -> {
            int totalScore = 0;
            int maxPossibleScore = 0;

            for (ConstitutionQuestionDO question : typeQuestions) {
                Long questionId = question.getId();
                // 如果用户回答了该题
                if (answers.containsKey(questionId)) {
                    String answer = answers.get(questionId);

                    // 根据用户选择获取分数
                    for (ConstitutionQuestionDO.QuestionOption option : question.getOptions()) {
                        // 计算该体质类型的最大可能分数
                        maxPossibleScore += option.getScore();

                        // 如果是用户选择的选项，累加得分
                        if (option.getValue().equals(answer)) {
                            totalScore += option.getScore();
                            break;
                        }
                    }
                }
            }

            // 计算标准化得分（0-100）
            int standardizedScore = maxPossibleScore > 0
                    ? (int) (totalScore * 100.0 / maxPossibleScore)
                    : 0;

            scores.put(type, standardizedScore);
        });
        // 按照分数倒序排序
        return scores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * 保存评估记录
     *
     * @param userId             用户ID
     * @param reqVO              请求数据
     * @param constitutionScores 体质得分
     * @return 保存的记录
     */
    private ConstitutionRecordDO saveEvaluationRecord(Long userId, AppConstitutionSubmitReqVO reqVO,
                                                      Map<String, Integer> constitutionScores) {
        // 1. 获取主要体质类型和次要体质类型
        String primaryType = null;
        int highestScore = 0;
        List<String> secondaryTypes = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : constitutionScores.entrySet()) {
            if (entry.getValue() > highestScore) {
                // 如果之前有主要体质，降级为次要体质
                if (primaryType != null) {
                    secondaryTypes.add(primaryType);
                }
                highestScore = entry.getValue();
                primaryType = entry.getKey();
            } else if (entry.getValue() >= 60) { // 超过60分作为次要体质
                secondaryTypes.add(entry.getKey());
            }
        }

        // 如果没有超过60分的体质，默认为平和质
        if (primaryType == null || constitutionScores.get(primaryType) < 60) {
            primaryType = "neutral"; // 平和质
            secondaryTypes.clear();
        }

        // 2. 创建评估记录
        ConstitutionRecordDO record = new ConstitutionRecordDO();
        record.setUserId(userId);
        record.setQuestionnaireId(reqVO.getQuestionnaireId());
        record.setAnswers(reqVO.getAnswers());
        record.setPrimaryType(primaryType);
        record.setSecondaryTypes(secondaryTypes);
        record.setScores(constitutionScores);
        recordMapper.insert(record);

        return record;
    }

    /**
     * 更新用户体质类型
     *
     * @param userId           用户ID
     * @param constitutionType 体质类型
     */
    private void updateUserConstitutionType(Long userId, String constitutionType) {
        patientService.updateConstitutionType(userId, constitutionType);
    }

    @Override
    public Long createQuestionnaire(ConstitutionQuestionnaireCreateReqVO createReqVO) {
        // 创建问卷
        ConstitutionQuestionnaireDO questionnaire = BeanUtils.toBean(createReqVO, ConstitutionQuestionnaireDO.class);
        questionnaireMapper.insert(questionnaire);
        return questionnaire.getId();
    }

    @Override
    public void updateQuestionnaire(ConstitutionQuestionnaireUpdateReqVO updateReqVO) {
        // 校验存在
        validateQuestionnaireExists(updateReqVO.getId());

        // 更新问卷
        ConstitutionQuestionnaireDO updateObj = BeanUtils.toBean(updateReqVO, ConstitutionQuestionnaireDO.class);
        questionnaireMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQuestionnaire(Long id) {
        // 校验存在
        validateQuestionnaireExists(id);

        // 删除问卷
        questionnaireMapper.deleteById(id);

        // 删除关联的问题
        questionMapper.deleteByQuestionnaireId(id);
    }

    @Override
    public ConstitutionQuestionnaireRespVO getQuestionnaire(Long id) {
        // 1. 获取问卷信息
        ConstitutionQuestionnaireDO questionnaire = questionnaireMapper.selectById(id);
        if (questionnaire == null) {
            return null;
        }

        // 2. 查询问卷题目数量
        Long questionCount = questionMapper.selectCount(ConstitutionQuestionDO::getQuestionnaireId, id);

        // 3. 转换并填充题目数量
        ConstitutionQuestionnaireRespVO respVO = BeanUtils.toBean(questionnaire, ConstitutionQuestionnaireRespVO.class);
        respVO.setQuestionCount(questionCount.intValue());

        return respVO;
    }

    @Override
    public PageResult<ConstitutionQuestionnaireRespVO> getQuestionnairePage(ConstitutionQuestionnairePageReqVO pageReqVO) {
        // 1. 获取问卷分页数据
        PageResult<ConstitutionQuestionnaireDO> pageResult = questionnaireMapper.selectPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return new PageResult<>(pageResult.getTotal());
        }

        // 2. 获取所有问卷ID
        List<Long> questionnaireIds = pageResult.getList().stream()
                .map(ConstitutionQuestionnaireDO::getId)
                .collect(Collectors.toList());

        // 3. 批量查询各问卷的题目数量
        Map<Long, Long> questionCountMap = questionMapper.selectCountByQuestionnaireIds(questionnaireIds);

        // 4. 转换并填充题目数量
        PageResult<ConstitutionQuestionnaireRespVO> respPageResult = BeanUtils.toBean(pageResult, ConstitutionQuestionnaireRespVO.class);
        respPageResult.getList().forEach(respVO ->
                respVO.setQuestionCount(questionCountMap.getOrDefault(respVO.getId(), 0L).intValue()));

        return respPageResult;
    }

    @Override
    public void updateQuestionnaireStatus(ConstitutionQuestionnaireUpdateStatusReqVO reqVO) {
        // 校验存在
        validateQuestionnaireExists(reqVO.getId());

        // 更新状态
        ConstitutionQuestionnaireDO updateObj = new ConstitutionQuestionnaireDO();
        updateObj.setId(reqVO.getId());
        updateObj.setStatus(reqVO.getStatus());
        questionnaireMapper.updateById(updateObj);
    }

    @Override
    public ConstitutionQuestionnaireDO getQuestionnaireDO(Long id) {
        return questionnaireMapper.selectById(id);
    }

    private void validateQuestionnaireExists(Long id) {
        if (questionnaireMapper.selectById(id) == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }
    }
}