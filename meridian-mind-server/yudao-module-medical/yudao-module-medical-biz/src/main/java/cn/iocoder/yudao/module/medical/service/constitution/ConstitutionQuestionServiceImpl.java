package cn.iocoder.yudao.module.medical.service.constitution;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.*;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionQuestionDO;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionQuestionnaireDO;
import cn.iocoder.yudao.module.medical.dal.mysql.constitution.ConstitutionQuestionMapper;
import cn.iocoder.yudao.module.medical.enums.ConstitutionTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.medical.enums.ErrorCodeConstants.QUESTIONNAIRE_NOT_EXISTS;
import static cn.iocoder.yudao.module.medical.enums.ErrorCodeConstants.QUESTION_NOT_EXISTS;

/**
 * 体质评估问题 Service 实现类
 */
@Service
@Validated
@Slf4j
public class ConstitutionQuestionServiceImpl implements ConstitutionQuestionService {

    @Resource
    private ConstitutionQuestionMapper questionMapper;

    @Resource
    private ConstitutionQuestionnaireService questionnaireService;

    @Override
    public Long createQuestion(ConstitutionQuestionCreateReqVO createReqVO) {
        // 校验问卷存在
        ConstitutionQuestionnaireDO questionnaire = questionnaireService.getQuestionnaireDO(createReqVO.getQuestionnaireId());
        if (questionnaire == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 创建问题
        ConstitutionQuestionDO question = BeanUtils.toBean(createReqVO, ConstitutionQuestionDO.class);
        questionMapper.insert(question);
        return question.getId();
    }

    @Override
    public void updateQuestion(ConstitutionQuestionUpdateReqVO updateReqVO) {
        // 校验问题存在
        validateQuestionExists(updateReqVO.getId());

        // 更新问题
        ConstitutionQuestionDO updateObj = BeanUtils.toBean(updateReqVO, ConstitutionQuestionDO.class);
        questionMapper.updateById(updateObj);
    }

    @Override
    public void deleteQuestion(Long id) {
        // 校验问题存在
        validateQuestionExists(id);

        // 删除问题
        questionMapper.deleteById(id);
    }

    @Override
    public ConstitutionQuestionRespVO getQuestion(Long id) {
        ConstitutionQuestionDO question = questionMapper.selectById(id);
        ConstitutionQuestionRespVO respVO = BeanUtils.toBean(question, ConstitutionQuestionRespVO.class);

        // 设置体质类型名称
        if (respVO != null && StrUtil.isNotBlank(respVO.getConstitutionType())) {
            respVO.setConstitutionTypeName(ConstitutionTypeEnum.nameOf(respVO.getConstitutionType()));
        }

        return respVO;
    }

    @Override
    public List<ConstitutionQuestionRespVO> getQuestionList(ConstitutionQuestionListReqVO listReqVO) {
        List<ConstitutionQuestionDO> list = questionMapper.selectList(listReqVO);
        List<ConstitutionQuestionRespVO> respList = BeanUtils.toBean(list, ConstitutionQuestionRespVO.class);

        // 设置体质类型名称
        if (CollUtil.isNotEmpty(respList)) {
            respList.forEach(question -> {
                if (StrUtil.isNotBlank(question.getConstitutionType())) {
                    question.setConstitutionTypeName(ConstitutionTypeEnum.nameOf(question.getConstitutionType()));
                }
            });
        }

        return respList;
    }

    @Override
    public void updateQuestionSort(ConstitutionQuestionUpdateSortReqVO reqVO) {
        // 校验问题存在
        validateQuestionExists(reqVO.getId());

        // 更新排序
        ConstitutionQuestionDO updateObj = new ConstitutionQuestionDO();
        updateObj.setId(reqVO.getId());
        updateObj.setSort(reqVO.getSort());
        questionMapper.updateById(updateObj);
    }

    @Override
    public ConstitutionQuestionDO getQuestionDO(Long id) {
        return questionMapper.selectById(id);
    }

    @Override
    public List<ConstitutionQuestionDO> getQuestionListByQuestionnaireId(Long questionnaireId) {
        return questionMapper.selectListByQuestionnaireId(questionnaireId);
    }

    @Override
    public Map<Long, ConstitutionQuestionDO> getQuestionMap(List<Long> questionIds) {
        if (CollUtil.isEmpty(questionIds)) {
            return new HashMap<>();
        }
        List<ConstitutionQuestionDO> list = questionMapper.selectByIds(questionIds);
        return CollectionUtils.convertMap(list, ConstitutionQuestionDO::getId);
    }

    private void validateQuestionExists(Long id) {
        if (questionMapper.selectById(id) == null) {
            throw exception(QUESTION_NOT_EXISTS);
        }
    }
}