package cn.iocoder.yudao.module.medical.service.constitution;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionRecordDetailRespVO;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionRecordPageReqVO;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionRecordRespVO;
import cn.iocoder.yudao.module.medical.convert.constitution.ConstitutionQuestionnaireConvert;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionQuestionDO;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionQuestionnaireDO;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionRecordDO;
import cn.iocoder.yudao.module.medical.dal.mysql.constitution.ConstitutionRecordMapper;
import cn.iocoder.yudao.module.medical.enums.ConstitutionTypeEnum;
import cn.iocoder.yudao.module.member.api.user.MemberUserApi;
import cn.iocoder.yudao.module.member.api.user.dto.MemberUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.medical.enums.ErrorCodeConstants.RECORD_NOT_EXISTS;


/**
 * 体质评估记录 Service 实现类
 */
@Service("constitutionRecordService")
@Validated
@Slf4j
public class ConstitutionRecordServiceImpl implements ConstitutionRecordService {

    @Resource
    private ConstitutionRecordMapper recordMapper;

    @Resource
    private ConstitutionQuestionnaireService questionnaireService;

    @Resource
    private ConstitutionQuestionService questionService;

    @Resource
    private MemberUserApi memberUserApi;

    @Override
    public PageResult<ConstitutionRecordRespVO> getRecordPage(ConstitutionRecordPageReqVO pageReqVO) {
        PageResult<ConstitutionRecordDO> pageResult = recordMapper.selectPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return new PageResult<>(pageResult.getTotal());
        }

        // 获取用户信息
        Set<Long> userIds = pageResult.getList().stream()
                .map(ConstitutionRecordDO::getUserId)
                .collect(Collectors.toSet());
        Map<Long, MemberUserRespDTO> userMap = memberUserApi.getUserMap(userIds);

        // 获取问卷信息
        Set<Long> questionnaireIds = pageResult.getList().stream()
                .map(ConstitutionRecordDO::getQuestionnaireId)
                .collect(Collectors.toSet());
        Map<Long, ConstitutionQuestionnaireDO> questionnaireMap = new HashMap<>();
        for (Long id : questionnaireIds) {
            ConstitutionQuestionnaireDO questionnaire = questionnaireService.getQuestionnaireDO(id);
            if (questionnaire != null) {
                questionnaireMap.put(id, questionnaire);
            }
        }

        // 组装返回结果
        List<ConstitutionRecordRespVO> respList = new ArrayList<>(pageResult.getList().size());
        pageResult.getList().forEach(record -> {
            ConstitutionRecordRespVO respVO = BeanUtils.toBean(record, ConstitutionRecordRespVO.class);

            // 设置用户信息
            MemberUserRespDTO user = userMap.get(record.getUserId());
            if (user != null) {
                respVO.setNickname(user.getNickname());
            }

            // 设置问卷信息
            ConstitutionQuestionnaireDO questionnaire = questionnaireMap.get(record.getQuestionnaireId());
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

            respList.add(respVO);
        });

        return new PageResult<>(respList, pageResult.getTotal());
    }

    @Override
    public ConstitutionRecordDetailRespVO getRecordDetail(Long id) {
        // 获取记录
        ConstitutionRecordDO record = recordMapper.selectById(id);
        if (record == null) {
            throw exception(RECORD_NOT_EXISTS);
        }

        // 获取用户信息
        MemberUserRespDTO user = memberUserApi.getUser(record.getUserId());

        // 获取问卷信息
        ConstitutionQuestionnaireDO questionnaire = questionnaireService.getQuestionnaireDO(record.getQuestionnaireId());

        // 获取问题信息
        Map<Long, ConstitutionQuestionDO> questionMap = questionService.getQuestionMap(
                new ArrayList<>(record.getAnswers().keySet()));

        // 使用Convert转换基础信息
        ConstitutionRecordDetailRespVO respVO = ConstitutionQuestionnaireConvert.INSTANCE.convertToDetail(
                record, user, questionnaire);

        // 组装按step分组的答题详情
        List<ConstitutionRecordDetailRespVO.StepAnswers> stepAnswers = ConstitutionQuestionnaireConvert.INSTANCE
                .convertAnswerDetails(record, questionMap);
        respVO.setStepAnswers(stepAnswers);

        return respVO;
    }

    @Override
    public ConstitutionRecordRespVO getUserLatestRecord(Long userId) {
        // 获取用户最新记录
        ConstitutionRecordDO record = recordMapper.selectLatestByUserId(userId);
        if (record == null) {
            return null;
        }

        // 获取问卷信息
        ConstitutionQuestionnaireDO questionnaire = questionnaireService.getQuestionnaireDO(record.getQuestionnaireId());

        // 组装返回结果
        ConstitutionRecordRespVO respVO = BeanUtils.toBean(record, ConstitutionRecordRespVO.class);

        // 设置用户信息
        MemberUserRespDTO user = memberUserApi.getUser(userId);
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
}