package cn.iocoder.yudao.module.medical.convert.constitution;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.*;
import cn.iocoder.yudao.module.medical.controller.app.constitution.vo.AppConstitutionQuestionnaireRespVO;
import cn.iocoder.yudao.module.medical.controller.app.constitution.vo.AppConstitutionResultRespVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionQuestionDO;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionQuestionnaireDO;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionRecordDO;
import cn.iocoder.yudao.module.medical.enums.ConstitutionTypeEnum;
import cn.iocoder.yudao.module.member.api.user.dto.MemberUserRespDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 体质评估问卷 Convert
 */
@Mapper
public interface ConstitutionQuestionnaireConvert {

    ConstitutionQuestionnaireConvert INSTANCE = Mappers.getMapper(ConstitutionQuestionnaireConvert.class);

    /**
     * 将问卷DO转换为VO
     */
    @Mapping(target = "stepCount", ignore = true)
    @Mapping(target = "steps", ignore = true)
    AppConstitutionQuestionnaireRespVO convertBase(ConstitutionQuestionnaireDO questionnaire);

    /**
     * 将问卷DO转换为响应VO
     */
    @Mapping(target = "stepCount", ignore = true)
    @Mapping(target = "steps", ignore = true)
    AppConstitutionQuestionnaireRespVO convert(ConstitutionQuestionnaireDO questionnaire);

    /**
     * 将评估记录DO转换为基本的VO（不包含额外信息）
     */
    @Mapping(target = "primaryTypeName", ignore = true)
    @Mapping(target = "secondaryTypeNames", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "advice", ignore = true)
    AppConstitutionResultRespVO convertRecordBase(ConstitutionRecordDO record);

    /**
     * 将问卷和问题转换为问卷VO，支持按步骤分组
     */
    default AppConstitutionQuestionnaireRespVO convert(ConstitutionQuestionnaireDO questionnaire,
                                                       List<ConstitutionQuestionDO> questions) {
        if (questionnaire == null) {
            return null;
        }

        // 转换基本信息
        AppConstitutionQuestionnaireRespVO vo = convertBase(questionnaire);

        if (questions == null || questions.isEmpty()) {
            vo.setStepCount(0);
            vo.setSteps(Collections.emptyList());
            return vo;
        }

        // 2. 按步骤分组问题
        Map<String, List<ConstitutionQuestionDO>> questionsByStep = questions.stream()
                .collect(Collectors.groupingBy(ConstitutionQuestionDO::getStep));

        // 3. 构建步骤问题列表
        List<AppConstitutionQuestionnaireRespVO.StepQuestions> steps = new ArrayList<>();
        questionsByStep.forEach((step, stepQuestions) -> {
            if (!stepQuestions.isEmpty()) {
                AppConstitutionQuestionnaireRespVO.StepQuestions stepQuestionsVO =
                        new AppConstitutionQuestionnaireRespVO.StepQuestions();
                stepQuestionsVO.setStep(step);
                stepQuestionsVO.setQuestions(convertQuestions(stepQuestions));
                steps.add(stepQuestionsVO);
            }
        });

        // 按步骤序号排序
        steps.sort(Comparator.comparing(AppConstitutionQuestionnaireRespVO.StepQuestions::getStep));

        // 设置步骤信息
        vo.setStepCount(steps.size());
        vo.setSteps(steps);

        return vo;
    }

    /**
     * 转换问题列表
     */
    default List<AppConstitutionQuestionnaireRespVO.Question> convertQuestions(List<ConstitutionQuestionDO> questions) {
        if (questions == null) {
            return Collections.emptyList();
        }

        return questions.stream().map(q -> {
            AppConstitutionQuestionnaireRespVO.Question questionVO =
                    new AppConstitutionQuestionnaireRespVO.Question();
            questionVO.setId(q.getId());
            questionVO.setQuestion(q.getQuestion());
            questionVO.setQuestionType(q.getQuestionType());
            questionVO.setSort(q.getSort());

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

            return questionVO;
        }).collect(Collectors.toList());
    }

    /**
     * 将评估记录转换为结果VO
     */
    default AppConstitutionResultRespVO convert(ConstitutionRecordDO record) {
        if (record == null) {
            return null;
        }
        // 转换基本信息
        AppConstitutionResultRespVO vo = convertRecordBase(record);

        // 设置体质类型名称
        vo.setPrimaryTypeName(ConstitutionTypeEnum.nameOf(record.getPrimaryType()));

        // 转换次要体质类型名称
        if (record.getSecondaryTypes() != null) {
            List<String> secondaryTypeNames = record.getSecondaryTypes().stream()
                    .map(ConstitutionTypeEnum::nameOf)
                    .collect(Collectors.toList());
            vo.setSecondaryTypeNames(secondaryTypeNames);
        }

        // 添加体质描述和调理建议
        vo.setDescription(getDescription(record.getPrimaryType()));
        vo.setAdvice(getAdvice(record.getPrimaryType()));

        return vo;
    }

    ConstitutionQuestionnaireDO convert(ConstitutionQuestionnaireCreateReqVO bean);

    ConstitutionQuestionnaireDO convert(ConstitutionQuestionnaireUpdateReqVO bean);

    List<ConstitutionQuestionnaireRespVO> convertList(List<ConstitutionQuestionnaireDO> list);
    /**
     * 将评估记录转换为详情VO
     */
    default ConstitutionRecordDetailRespVO convertToDetail(ConstitutionRecordDO record,
                                                           MemberUserRespDTO user,
                                                           ConstitutionQuestionnaireDO questionnaire) {
        if (record == null) {
            return null;
        }

        // 转换基本信息
        ConstitutionRecordDetailRespVO respVO = BeanUtils.toBean(record, ConstitutionRecordDetailRespVO.class);

        // 设置用户信息
        if (user != null) {
            respVO.setNickname(user.getNickname());
        }

        // 设置问卷信息
        if (questionnaire != null) {
            respVO.setQuestionnaireTitle(questionnaire.getTitle());
        }

        // 设置体质类型名称
        respVO.setPrimaryTypeName(ConstitutionTypeEnum.nameOf(record.getPrimaryType()));
        if (CollUtil.isNotEmpty(record.getSecondaryTypes())) {
            List<String> secondaryTypeNames = record.getSecondaryTypes().stream()
                    .map(ConstitutionTypeEnum::nameOf)
                    .collect(Collectors.toList());
            respVO.setSecondaryTypeNames(secondaryTypeNames);
        }

        return respVO;
    }

    /**
     * 组装答题详情，按step分组
     */
    default List<ConstitutionRecordDetailRespVO.StepAnswers> convertAnswerDetails(
            ConstitutionRecordDO record,
            Map<Long, ConstitutionQuestionDO> questionMap) {

        if (CollUtil.isEmpty(record.getAnswers()) || CollUtil.isEmpty(questionMap)) {
            return Collections.emptyList();
        }

        // 先创建所有答题详情
        List<ConstitutionRecordDetailRespVO.AnswerDetail> allDetails = new ArrayList<>();

        record.getAnswers().forEach((questionId, answer) -> {
            ConstitutionQuestionDO question = questionMap.get(questionId);
            if (question != null) {
                ConstitutionRecordDetailRespVO.AnswerDetail detail = new ConstitutionRecordDetailRespVO.AnswerDetail();
                detail.setQuestionId(questionId);
                detail.setQuestion(question.getQuestion());
                detail.setAnswer(answer);

                // 获取答案文本和分值
                String answerLabel = "";
                Integer score = 0;
                if (CollUtil.isNotEmpty(question.getOptions())) {
                    for (ConstitutionQuestionDO.QuestionOption option : question.getOptions()) {
                        if (option.getValue().equals(answer)) {
                            answerLabel = option.getLabel();
                            score = option.getScore();
                            break;
                        }
                    }
                }
                detail.setAnswerLabel(answerLabel);
                detail.setScore(score);

                // 设置体质类型
                detail.setConstitutionType(question.getConstitutionType());
                detail.setConstitutionTypeName(ConstitutionTypeEnum.nameOf(question.getConstitutionType()));

                // 设置step信息
                detail.setStep(question.getStep());

                allDetails.add(detail);
            }
        });

        // 按问题排序号排序
        allDetails.sort(Comparator.comparing(detail -> {
            ConstitutionQuestionDO question = questionMap.get(detail.getQuestionId());
            return question != null ? question.getSort() : 0;
        }));

        // 按step分组
        Map<String, List<ConstitutionRecordDetailRespVO.AnswerDetail>> answersByStep =
                allDetails.stream().collect(Collectors.groupingBy(
                        ConstitutionRecordDetailRespVO.AnswerDetail::getStep));

        // 转换为分步骤的答题详情
        List<ConstitutionRecordDetailRespVO.StepAnswers> stepAnswers = new ArrayList<>();
        answersByStep.forEach((step, details) -> {
            if (!details.isEmpty()) {
                ConstitutionRecordDetailRespVO.StepAnswers stepAnswer =
                        new ConstitutionRecordDetailRespVO.StepAnswers();
                stepAnswer.setStep(step);
                stepAnswer.setAnswerDetails(details);
                stepAnswers.add(stepAnswer);
            }
        });

        // 按step排序
        stepAnswers.sort(Comparator.comparing(ConstitutionRecordDetailRespVO.StepAnswers::getStep));

        return stepAnswers;
    }
    ConstitutionRadarRespVO convertToRadar(ConstitutionRecordDO record);
    /**
     * 获取体质描述
     */
    @Named("getDescription")
    default String getDescription(String constitutionType) {
        return switch (constitutionType) {
            case "qi_deficiency" -> "气虚体质的人常表现为疲乏无力、气短懒言、容易出汗、舌淡苔薄等特征。";
            case "yang_deficiency" -> "阳虚体质的人常表现为怕冷、手脚发凉、面色苍白、舌淡胖等特征。";
            case "yin_deficiency" -> "阴虚体质的人常表现为手足心热、口干咽燥、眼睛干涩、舌红少苔等特征。";
            case "phlegm_dampness" -> "痰湿体质的人常表现为体形肥胖、腹部松软、面部油腻、苔厚腻等特征。";
            case "damp_heat" -> "湿热体质的人常表现为面垢油光、容易长痘痘、口苦、小便黄等特征。";
            case "blood_stasis" -> "血瘀体质的人常表现为面色晦暗、唇色偏暗、舌有瘀点、肤色不均等特征。";
            case "qi_stagnation" -> "气郁体质的人常表现为情绪不稳、易怒或忧郁、胸胁胀闷、舌边有齿痕等特征。";
            case "special" -> "特禀体质的人常表现为对某些物质过敏、易患特定疾病等特征。";
            default -> "平和体质是人体阴阳气血调和、脏腑功能平衡的理想状态，表现为精力充沛、体形匀称、面色红润等特征。";
        };
    }

    /**
     * 获取体质调理建议
     */
    @Named("getAdvice")
    default String getAdvice(String constitutionType) {
        return switch (constitutionType) {
            case "qi_deficiency" ->
                    "饮食宜清淡而富有营养，多食健脾益气之品；加强锻炼，但避免过度疲劳；保持良好心态，避免忧思过度。";
            case "yang_deficiency" ->
                    "饮食宜温热，避免生冷食物；注意保暖，避免风寒侵袭；适当进行温和运动，如太极拳、八段锦等。";
            case "yin_deficiency" -> "饮食宜滋阴润燥，避免辛辣刺激食物；保持充足睡眠；适当进行放松性运动，如散步、瑜伽等。";
            case "phlegm_dampness" -> "饮食宜清淡，少食肥甘厚味；增加有氧运动，如快走、慢跑等；保持环境干燥，避免潮湿环境。";
            case "damp_heat" -> "饮食宜清淡，避免辛辣油腻食物；多饮水，保持大便通畅；适当户外活动，但避免阳光暴晒。";
            case "blood_stasis" ->
                    "饮食宜活血化瘀，如醋、黑木耳等；适当进行有氧运动，促进血液循环；保持心情舒畅，避免情绪激动。";
            case "qi_stagnation" -> "保持心情舒畅，学会情绪管理；适当进行有氧运动，如跑步、游泳等；饮食规律，避免暴饮暴食。";
            case "special" -> "注意饮食卫生，避免接触过敏原；保持良好生活习惯；出现特异性症状时及时就医。";
            default -> "保持规律作息、均衡饮食；适当运动，增强体质；调节情志，保持愉快心情；注意防护，随四时变化调整生活方式。";
        };
    }

}