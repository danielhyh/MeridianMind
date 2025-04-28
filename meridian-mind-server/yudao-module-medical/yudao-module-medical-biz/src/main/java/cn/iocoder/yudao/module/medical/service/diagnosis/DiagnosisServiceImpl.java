package cn.iocoder.yudao.module.medical.service.diagnosis;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.api.chat.AiChatMessageApi;
import cn.iocoder.yudao.module.ai.api.chat.dto.AiChatMessageSendReqDTO;
import cn.iocoder.yudao.module.ai.api.chat.dto.AiChatMessageSendRespDTO;
import cn.iocoder.yudao.module.ai.api.prompt.AiPromptTemplateApi;
import cn.iocoder.yudao.module.ai.api.prompt.dto.AiPromptTemplateRenderReqDTO;
import cn.iocoder.yudao.module.ai.api.prompt.dto.AiPromptTemplateRespDTO;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosis.vo.DiagnosisPageReqVO;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosis.vo.DiagnosisSaveReqVO;
import cn.iocoder.yudao.module.medical.controller.app.diagnosis.vo.AppDiagnosisResultRespVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnosis.DiagnosisDO;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnostic.DiagnosticDO;
import cn.iocoder.yudao.module.medical.dal.dataobject.fourdiagnosis.FourDiagnosticDO;
import cn.iocoder.yudao.module.medical.dal.dataobject.patient.PatientProfileDO;
import cn.iocoder.yudao.module.medical.dal.mysql.diagnosis.DiagnosisMapper;
import cn.iocoder.yudao.module.medical.dal.mysql.diagnostic.DiagnosticMapper;
import cn.iocoder.yudao.module.medical.dal.mysql.fourdiagnosis.FourDiagnosticMapper;
import cn.iocoder.yudao.module.medical.dal.mysql.patient.PatientProfileMapper;
import cn.iocoder.yudao.module.member.api.user.MemberUserApi;
import cn.iocoder.yudao.module.member.api.user.dto.MemberUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.PROMPT_TEMPLATE_NOT_EXISTS;
import static cn.iocoder.yudao.module.medical.enums.ErrorCodeConstants.*;

/**
 * 诊断结果 Service 实现类
 *
 * @author 芋道源码
 */
@Slf4j
@Service
@Validated
public class DiagnosisServiceImpl implements DiagnosisService {

    // 四诊八纲辨证模板ID，实际应从配置中获取
    private static final Long FOUR_DIAGNOSIS_TEMPLATE_ID = 1L;
    // 默认AI模型ID，实际应从配置中获取
    private static final Long DEFAULT_MODEL_ID = 1L;
    @Resource
    private AiPromptTemplateApi aiPromptTemplateApi;
    @Resource
    private AiChatMessageApi aiChatApi;
    @Resource
    private DiagnosticMapper diagnosticMapper;
    @Resource
    private FourDiagnosticMapper fourDiagnosticMapper;
    @Resource
    private PatientProfileMapper patientProfileMapper;
    @Resource
    private DiagnosisMapper diagnosisMapper;
    @Resource
    private MemberUserApi memberUserApi;

    @Override
    public Long createDiagnosis(DiagnosisSaveReqVO createReqVO) {
        // 插入
        DiagnosisDO diagnosis = BeanUtils.toBean(createReqVO, DiagnosisDO.class);
        diagnosisMapper.insert(diagnosis);
        // 返回
        return diagnosis.getId();
    }

    @Override
    public void updateDiagnosis(DiagnosisSaveReqVO updateReqVO) {
        // 校验存在
        validateDiagnosisExists(updateReqVO.getId());
        // 更新
        DiagnosisDO updateObj = BeanUtils.toBean(updateReqVO, DiagnosisDO.class);
        diagnosisMapper.updateById(updateObj);
    }

    @Override
    public void deleteDiagnosis(Long id) {
        // 校验存在
        validateDiagnosisExists(id);
        // 删除
        diagnosisMapper.deleteById(id);
    }

    private void validateDiagnosisExists(Long id) {
        if (diagnosisMapper.selectById(id) == null) {
            throw exception(DIAGNOSIS_NOT_EXISTS);
        }
    }

    @Override
    public DiagnosisDO getDiagnosis(Long id) {
        return diagnosisMapper.selectById(id);
    }

    @Override
    public PageResult<DiagnosisDO> getDiagnosisPage(DiagnosisPageReqVO pageReqVO) {
        return diagnosisMapper.selectPage(pageReqVO);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppDiagnosisResultRespVO generateDiagnosis(Long userId, Long diagnosticId) {
        // 1. 验证并获取各项必要数据
        DiagnosticDO diagnostic = diagnosticMapper.selectById(diagnosticId);
        if (diagnostic == null) {
            throw exception(DIAGNOSTIC_NOT_EXISTS);
        }

        // 验证用户身份
        if (!diagnostic.getUserId().equals(userId)) {
            throw exception(DIAGNOSTIC_ACCESS_DENIED);
        }

        // 获取四诊信息
        FourDiagnosticDO fourDiagnostic = fourDiagnosticMapper.selectByDiagnosticId(diagnosticId);
        if (fourDiagnostic == null) {
            throw exception(FOUR_DIAGNOSTIC_NOT_EXISTS);
        }

        // 验证四诊信息是否完整
        validateFourDiagnosticComplete(fourDiagnostic);

        // 获取患者资料
        PatientProfileDO patientProfile = patientProfileMapper.selectByUserId(userId);
        if (patientProfile == null) {
            throw exception(PATIENT_PROFILE_NOT_EXISTS);
        }

        // 获取用户基本信息
        MemberUserRespDTO user = memberUserApi.getUser(userId);

        // 2. 获取AI提示词模板
        AiPromptTemplateRespDTO promptTemplate = aiPromptTemplateApi.getPromptTemplate(FOUR_DIAGNOSIS_TEMPLATE_ID);
        if (promptTemplate == null) {
            throw exception(PROMPT_TEMPLATE_NOT_EXISTS);
        }

        // 3. 准备模板参数
        Map<String, Object> parameters = preparePromptParameters(user, patientProfile, fourDiagnostic, diagnostic);

        // 4. 渲染提示词
        AiPromptTemplateRenderReqDTO renderReqDTO = new AiPromptTemplateRenderReqDTO();
        renderReqDTO.setTemplateId(FOUR_DIAGNOSIS_TEMPLATE_ID);
        renderReqDTO.setParameters(parameters);
        String promptContent = aiPromptTemplateApi.renderPromptTemplate(renderReqDTO);

        // 5. 调用AI服务
        AiChatMessageSendReqDTO chatReqDTO = new AiChatMessageSendReqDTO();
        chatReqDTO.setConversationId(promptTemplate.getDefaultModelId());
        chatReqDTO.setContent(promptContent);
        AiChatMessageSendRespDTO chatResp = aiChatApi.sendMessage(chatReqDTO, userId);

        // 6. 解析AI回复，转换为诊断结果
        AppDiagnosisResultRespVO diagnosisResult = parseAiResponse(chatResp.getReceive().getContent());

        // 7. 保存诊断结果
        saveDiagnosisResult(diagnosticId, diagnosisResult);

        // 8. 更新问诊状态为已完成
        diagnostic.setStatus(1); // 1-已完成
        diagnosticMapper.updateById(diagnostic);

        return diagnosisResult;
    }

    /**
     * 验证四诊信息是否完整
     */
    private void validateFourDiagnosticComplete(FourDiagnosticDO fourDiagnostic) {
        // 验证关键部分是否已填写，例如：
        if (StrUtil.isBlank(fourDiagnostic.getInquiry())) {
            throw exception(FOUR_DIAGNOSTIC_INQUIRY_MISSING);
        }

        if (StrUtil.isBlank(fourDiagnostic.getPalpation())) {
            throw exception(FOUR_DIAGNOSTIC_PALPATION_MISSING);
        }

        // 其他必要验证...
    }

    /**
     * 准备提示词模板参数
     */
    private Map<String, Object> preparePromptParameters(MemberUserRespDTO user,
                                                        PatientProfileDO patientProfile,
                                                        FourDiagnosticDO fourDiagnostic,
                                                        DiagnosticDO diagnostic) {
        Map<String, Object> parameters = new HashMap<>();

        // 1. 用户基本信息
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", user.getNickname());
        userInfo.put("sex", user.getSex());
        userInfo.put("age", calculateAge(user.getBirthday()));
        userInfo.put("constitutionType", patientProfile.getConstitutionType());
        userInfo.put("allergies", parseJsonArray(patientProfile.getAllergies()));
        userInfo.put("medicalHistory", parseJsonArray(patientProfile.getMedicalHistory()));
        userInfo.put("familyMedicalHistory", parseJsonArray(patientProfile.getFamilyMedicalHistory()));
        parameters.put("userInfo", userInfo);

        // 2. 望诊数据
        if (StrUtil.isNotBlank(fourDiagnostic.getInspection())) {
            parameters.put("inspection", JSONUtil.parseObj(fourDiagnostic.getInspection()));
        } else {
            parameters.put("inspection", new HashMap<>());
        }

        // 3. 闻诊数据
        if (StrUtil.isNotBlank(fourDiagnostic.getAuscultation())) {
            parameters.put("auscultation", JSONUtil.parseObj(fourDiagnostic.getAuscultation()));
        } else {
            parameters.put("auscultation", new HashMap<>());
        }

        // 4. 问诊数据
        if (StrUtil.isNotBlank(fourDiagnostic.getInquiry())) {
            parameters.put("inquiry", JSONUtil.parseObj(fourDiagnostic.getInquiry()));
        } else {
            parameters.put("inquiry", new HashMap<>());
        }

        // 5. 切诊数据
        if (StrUtil.isNotBlank(fourDiagnostic.getPalpation())) {
            parameters.put("palpation", JSONUtil.parseObj(fourDiagnostic.getPalpation()));
        } else {
            parameters.put("palpation", new HashMap<>());
        }

        // 6. 问诊记录信息
        parameters.put("chiefComplaint", diagnostic.getChiefComplaint());
//        parameters.put("illnessHistory", diagnostic.getIllnessHistory());
//        parameters.put("medicalHistory", diagnostic.getMedicalHistory());

        return parameters;
    }

    /**
     * 计算年龄
     */
    private int calculateAge(LocalDateTime birthday) {
        if (birthday == null) {
            return 0;
        }
        return LocalDateTime.now().getYear() - birthday.getYear();
    }

    /**
     * 解析JSON数组
     */
    private Object parseJsonArray(String json) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        try {
            return JSONUtil.parseArray(json);
        } catch (Exception e) {
            log.warn("解析JSON数组失败: {}", json, e);
            return null;
        }
    }

    /**
     * 解析AI回复为诊断结果
     */
    private AppDiagnosisResultRespVO parseAiResponse(String aiResponse) {
        try {
            // 尝试解析JSON格式的回复
            return JSONUtil.toBean(aiResponse, AppDiagnosisResultRespVO.class);
        } catch (Exception e) {
            log.error("解析AI回复失败: {}", aiResponse, e);
            throw exception(AI_RESPONSE_PARSE_ERROR, e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveDiagnosisResult(Long diagnosticId, AppDiagnosisResultRespVO diagnosisResult) {
        // 检查是否已存在诊断结果
        DiagnosisDO existingDiagnosis = diagnosisMapper.selectById(diagnosticId);
        if (existingDiagnosis != null) {
            // 更新现有诊断
            updateExistingDiagnosis(existingDiagnosis, diagnosisResult);
            return existingDiagnosis.getId();
        } else {
            // 创建新诊断
            return createNewDiagnosis(diagnosticId, diagnosisResult);
        }
    }

    /**
     * 更新已有诊断结果
     */
    private void updateExistingDiagnosis(DiagnosisDO existingDiagnosis, AppDiagnosisResultRespVO diagnosisResult) {
        existingDiagnosis.setPrimarySyndrome(diagnosisResult.getDiagnosisDetail().getPrimarySyndrome());
        existingDiagnosis.setSecondarySyndromes(JSONUtil.toJsonStr(diagnosisResult.getDiagnosisDetail().getSecondarySyndromes()));
        existingDiagnosis.setEightPrinciples(JSONUtil.toJsonStr(diagnosisResult.getDiagnosisDetail().getEightPrinciples()));
        existingDiagnosis.setDiagnosisExplanation(diagnosisResult.getDiagnosisDetail().getAnalysis());
        existingDiagnosis.setAiRawOutput(JSONUtil.toJsonStr(diagnosisResult));
        existingDiagnosis.setConfidenceLevel(diagnosisResult.getConfidence());
        diagnosisMapper.updateById(existingDiagnosis);
    }

    /**
     * 创建新诊断结果
     */
    private Long createNewDiagnosis(Long diagnosticId, AppDiagnosisResultRespVO diagnosisResult) {
        DiagnosisDO diagnosis = new DiagnosisDO();
        diagnosis.setDiagnosticId(diagnosticId);
        diagnosis.setPrimarySyndrome(diagnosisResult.getDiagnosisDetail().getPrimarySyndrome());
        diagnosis.setSecondarySyndromes(JSONUtil.toJsonStr(diagnosisResult.getDiagnosisDetail().getSecondarySyndromes()));
        diagnosis.setEightPrinciples(JSONUtil.toJsonStr(diagnosisResult.getDiagnosisDetail().getEightPrinciples()));
        diagnosis.setDiagnosisExplanation(diagnosisResult.getDiagnosisDetail().getAnalysis());
        diagnosis.setAiRawOutput(JSONUtil.toJsonStr(diagnosisResult));
        diagnosis.setConfidenceLevel(diagnosisResult.getConfidence());
        diagnosisMapper.insert(diagnosis);

        // 创建治疗方案记录
        createTreatmentPlan(diagnosis.getId(), diagnosisResult);

        return diagnosis.getId();
    }

    /**
     * 创建治疗方案
     */
    private void createTreatmentPlan(Long diagnosisId, AppDiagnosisResultRespVO diagnosisResult) {
        // 治疗方案逻辑实现
        // 包括基本治疗原则、方剂、药材等信息的保存
        // 此处省略具体实现...
    }

    @Override
    public AppDiagnosisResultRespVO getDiagnosisResult(Long diagnosticId) {

        DiagnosisDO diagnosis = diagnosisMapper.selectById(diagnosticId);
        if (diagnosis == null) {
            return null;
        }

        try {
            return JSONUtil.toBean(diagnosis.getAiRawOutput(), AppDiagnosisResultRespVO.class);
        } catch (Exception e) {
            log.error("解析诊断结果失败: {}", diagnosis.getAiRawOutput(), e);
            throw exception(DIAGNOSIS_PARSE_ERROR, e.getMessage());
        }
    }

}