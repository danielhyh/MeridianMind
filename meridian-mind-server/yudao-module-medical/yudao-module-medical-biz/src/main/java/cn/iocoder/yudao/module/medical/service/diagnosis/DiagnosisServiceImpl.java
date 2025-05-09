package cn.iocoder.yudao.module.medical.service.diagnosis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.api.chat.AiChatApi;
import cn.iocoder.yudao.module.ai.api.chat.dto.AiChatConversationCreateMyReqDTO;
import cn.iocoder.yudao.module.ai.api.chat.dto.AiChatMessageSendReqDTO;
import cn.iocoder.yudao.module.ai.api.chat.dto.AiChatMessageSendRespDTO;
import cn.iocoder.yudao.module.ai.api.prompt.AiPromptTemplateApi;
import cn.iocoder.yudao.module.ai.api.prompt.dto.AiPromptTemplateRenderReqDTO;
import cn.iocoder.yudao.module.ai.api.prompt.dto.AiPromptTemplateRespDTO;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosis.vo.DiagnosisPageReqVO;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosis.vo.DiagnosisSaveReqVO;
import cn.iocoder.yudao.module.medical.controller.app.diagnosis.vo.AppDiagnosisResultRespVO;
import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppPatientProfileRespVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnosis.DiagnosisDO;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnosistreatment.DiagnosisTreatmentDO;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnostic.DiagnosticDO;
import cn.iocoder.yudao.module.medical.dal.dataobject.fourdiagnosis.FourDiagnosticDO;
import cn.iocoder.yudao.module.medical.dal.mysql.diagnosis.DiagnosisMapper;
import cn.iocoder.yudao.module.medical.dal.mysql.diagnosistreatment.DiagnosisTreatmentMapper;
import cn.iocoder.yudao.module.medical.dal.mysql.diagnostic.DiagnosticMapper;
import cn.iocoder.yudao.module.medical.dal.mysql.fourdiagnosis.FourDiagnosticMapper;
import cn.iocoder.yudao.module.medical.enums.DiagnosisEnum;
import cn.iocoder.yudao.module.medical.service.patient.PatientService;
import cn.iocoder.yudao.module.system.api.dict.DictDataApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.PROMPT_TEMPLATE_NOT_EXISTS;
import static cn.iocoder.yudao.module.medical.enums.DictTypeConstants.MEDICAL_BLOOD_TYPE;
import static cn.iocoder.yudao.module.medical.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.system.enums.DictTypeConstants.USER_SEX;

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
    @Resource
    private AiPromptTemplateApi aiPromptTemplateApi;
    @Resource
    private AiChatApi aiChatApi;
    @Resource
    private DiagnosticMapper diagnosticMapper;
    @Resource
    private FourDiagnosticMapper fourDiagnosticMapper;
    @Resource
    private PatientService patientService;
    @Resource
    private DiagnosisMapper diagnosisMapper;
    @Resource
    private DictDataApi dictDataApi;
    @Resource
    private DiagnosisTreatmentMapper diagnosisTreatmentMapper;

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
//        validateFourDiagnosticComplete(fourDiagnostic);

        // 获取患者资料
        AppPatientProfileRespVO patientProfile = patientService.getPatientProfile(userId);
        if (patientProfile == null) {
            throw exception(PATIENT_PROFILE_NOT_EXISTS);
        }

        // 2. 获取AI提示词模板
        AiPromptTemplateRespDTO promptTemplate = aiPromptTemplateApi.getPromptTemplate(FOUR_DIAGNOSIS_TEMPLATE_ID);
        if (promptTemplate == null) {
            throw exception(PROMPT_TEMPLATE_NOT_EXISTS);
        }

        // 3. 准备模板参数
        Map<String, Object> parameters = preparePromptParameters(patientProfile, fourDiagnostic, diagnostic);

        // 4. 渲染提示词
        AiPromptTemplateRenderReqDTO renderReqDTO = new AiPromptTemplateRenderReqDTO();
        renderReqDTO.setTemplateId(FOUR_DIAGNOSIS_TEMPLATE_ID);
        renderReqDTO.setParameters(parameters);
        String promptContent = aiPromptTemplateApi.renderPromptTemplate(renderReqDTO);

        // 5. 调用AI服务
        AiChatConversationCreateMyReqDTO createMyReqDTO = new AiChatConversationCreateMyReqDTO()
                .setUserId(userId).setModelId(promptTemplate.getDefaultModelId());
        Long conversationId = aiChatApi.createChatConversationMy(createMyReqDTO);
        AiChatMessageSendReqDTO chatReqDTO = new AiChatMessageSendReqDTO();
        chatReqDTO.setConversationId(conversationId);
        chatReqDTO.setContent(promptContent);
        AiChatMessageSendRespDTO chatResp = aiChatApi.sendMessage(chatReqDTO, userId);

        // 6. 解析AI回复，转换为诊断结果
        AppDiagnosisResultRespVO result = parseAiResponse(chatResp.getReceive().getContent(), diagnosticId, userId);

        // 7. 更新问诊状态为已完成
        diagnostic.setStatus(DiagnosisEnum.COMPLETED.getStatus()); // 2-已完成
        diagnosticMapper.updateById(diagnostic);

        return result;
    }

    /**
     * 准备提示词模板参数
     */
    private Map<String, Object> preparePromptParameters(AppPatientProfileRespVO patientProfile,
                                                        FourDiagnosticDO fourDiagnostic,
                                                        DiagnosticDO diagnostic) {
        // 使用扁平化结构直接保存参数
        Map<String, Object> flatParameters = new HashMap<>();

        // 1. 处理基本信息
        flatParameters.put("name", patientProfile.getName());
        flatParameters.put("sex", dictDataApi.getDictDataLabel(USER_SEX, String.valueOf(patientProfile.getSex())));
        flatParameters.put("age", patientProfile.getAge());
        flatParameters.put("height", patientProfile.getHeight());
        flatParameters.put("weight", patientProfile.getWeight());
        flatParameters.put("bloodType", dictDataApi.getDictDataLabel(MEDICAL_BLOOD_TYPE, String.valueOf(patientProfile.getBloodType())));

        // 2. 处理体质信息
        JSONObject constitutionJson = JSONUtil.parseObj(patientProfile.getConstitutionType());
        // 只保留指定的字段，并转换为中文字段名
        JSONObject simplifiedConstitution = JSONUtil.createObj()
                .set("主体质", constitutionJson.getStr("primary"))
                .set("次体质", constitutionJson.getJSONArray("secondary"));

        flatParameters.put("constitutionType", simplifiedConstitution.toString());
        // 3. 处理过敏史、病史、家族病史
        String allergies = "无";
        if (CollUtil.isNotEmpty(patientProfile.getAllergies())) {
            allergies = patientProfile.getAllergies().stream()
                    .map(allergy -> allergy.getAllergySource() + "(" + allergy.getReaction() + ")" + allergy.getSeverity())
                    .collect(Collectors.joining("、"));
        }
        flatParameters.put("allergies", allergies);

        String medicalHistory = "无";
        if (CollUtil.isNotEmpty(patientProfile.getMedicalHistory())) {
            medicalHistory = patientProfile.getMedicalHistory().stream()
                    .map(history -> history.getDiseaseName() + "(" + history.getCurrentStatus() + ")" + history.getTreatment())
                    .collect(Collectors.joining("、"));
        }
        flatParameters.put("medicalHistory", medicalHistory);

        String familyMedicalHistory = "无";
        if (CollUtil.isNotEmpty(patientProfile.getFamilyMedicalHistory())) {
            familyMedicalHistory = patientProfile.getFamilyMedicalHistory().stream()
                    .map(history -> history.getRelationship() + ":" + history.getDiseaseName())
                    .collect(Collectors.joining("、"));
        }
        flatParameters.put("familyMedicalHistory", familyMedicalHistory);

        // 4. 处理望诊信息 - 脸部
        StringBuilder faceInfoBuilder = new StringBuilder();
        if (StrUtil.isNotBlank(fourDiagnostic.getInspection())) {
            JSONObject inspection = JSONUtil.parseObj(fourDiagnostic.getInspection());
            JSONObject face = inspection.getJSONObject("face");
            if (face != null) {
                faceInfoBuilder.append("面色：").append(face.getStr("faceColor", "未记录")).append("\n");

                // 处理面部特征，提前展开区域颜色信息
                JSONObject regionColors = face.getJSONObject("regionColors");
                if (regionColors != null && !regionColors.isEmpty()) {
                    faceInfoBuilder.append("面部特征：\n");
                    for (String key : regionColors.keySet()) {
                        JSONObject region = regionColors.getJSONObject(key);
                        if (region != null) {
                            faceInfoBuilder.append("  ").append(key).append("：")
                                    .append(region.getStr("color", "未记录")).append("\n");
                        }
                    }
                }
            }
        }
        flatParameters.put("faceInfo", faceInfoBuilder.toString().trim());

        // 5. 处理望诊信息 - 舌象
        StringBuilder tongueInfoBuilder = new StringBuilder();
        if (StrUtil.isNotBlank(fourDiagnostic.getInspection())) {
            JSONObject inspection = JSONUtil.parseObj(fourDiagnostic.getInspection());
            JSONObject tongue = inspection.getJSONObject("tongue");
            if (tongue != null) {
                tongueInfoBuilder.append("舌象：").append(tongue.getStr("tongueColor", ""));
                if (StrUtil.isNotBlank(tongue.getStr("tongueCoating"))) {
                    tongueInfoBuilder.append("，").append(tongue.getStr("tongueCoating"));
                }
                tongueInfoBuilder.append("\n");

                tongueInfoBuilder.append("舌体特征：").append(tongue.getStr("tongueShape", "未记录"));
                if (tongue.getBool("hasToothMark", false)) {
                    tongueInfoBuilder.append("，有齿痕");
                }
                if (tongue.getBool("hasCrack", false)) {
                    tongueInfoBuilder.append("，有裂纹");
                }
                tongueInfoBuilder.append("\n");

                tongueInfoBuilder.append("舌质特征：湿度：");
                Double moisture = tongue.getDouble("moisture", 0.0);
                tongueInfoBuilder.append(moisture > 0.5 ? "湿润" : "干燥");
            }
        }
        flatParameters.put("tongueInfo", tongueInfoBuilder.toString().trim());

        // 6. 处理闻诊信息 - 声音
        StringBuilder voiceInfoBuilder = new StringBuilder();
        if (StrUtil.isNotBlank(fourDiagnostic.getAuscultation())) {
            JSONObject auscultation = JSONUtil.parseObj(fourDiagnostic.getAuscultation());
            JSONObject voice = auscultation.getJSONObject("voice");
            if (voice != null) {
                voiceInfoBuilder.append("声音特征：")
                        .append(voice.getStr("strength", "")).append("，")
                        .append(voice.getStr("tone", "")).append("，")
                        .append(voice.getStr("rhythm", "")).append("\n");
                voiceInfoBuilder.append("呼吸特征：").append(voice.getStr("breathPattern", "未记录"));
            }
        }
        flatParameters.put("voiceInfo", voiceInfoBuilder.toString().trim());

        // 7. 处理闻诊信息 - 气味
        StringBuilder odorInfoBuilder = new StringBuilder();
        if (StrUtil.isNotBlank(fourDiagnostic.getAuscultation())) {
            JSONObject auscultation = JSONUtil.parseObj(fourDiagnostic.getAuscultation());
            JSONObject odor = auscultation.getJSONObject("odor");
            if (odor != null) {
                odorInfoBuilder.append("气味特征：体表气味")
                        .append(odor.getStr("bodySurface", "未记录")).append("，呼吸气味")
                        .append(odor.getStr("respiratory", "未记录"));
            }
        }
        flatParameters.put("odorInfo", odorInfoBuilder.toString().trim());

        // 8. 处理问诊信息
        StringBuilder symptomInfoBuilder = new StringBuilder();
        StringBuilder illnessInfoBuilder = new StringBuilder();
        if (StrUtil.isNotBlank(fourDiagnostic.getInquiry())) {
            JSONObject inquiry = JSONUtil.parseObj(fourDiagnostic.getInquiry());
            JSONObject inquiryData = inquiry.getJSONObject("inquiry");
            if (inquiryData != null) {
                // 症状信息
                if (inquiryData.containsKey("symptoms")) {
                    Object symptoms = inquiryData.get("symptoms");
                    if (symptoms instanceof Collection) {
                        symptomInfoBuilder.append("症状：").append(
                                ((Collection<?>) symptoms).stream()
                                        .map(Object::toString)
                                        .collect(Collectors.joining("、"))
                        ).append("\n");
                    } else if (symptoms != null) {
                        symptomInfoBuilder.append("症状：").append(symptoms).append("\n");
                    }
                }

                // 病程信息
                if (inquiryData.containsKey("onsetDays")) {
                    illnessInfoBuilder.append("病程：").append(inquiryData.getInt("onsetDays", 0)).append("天\n");
                }

                // 其他信息
                if (StrUtil.isNotBlank(inquiryData.getStr("dietStatus"))) {
                    illnessInfoBuilder.append("饮食状况：").append(inquiryData.getStr("dietStatus")).append("\n");
                }
                if (StrUtil.isNotBlank(inquiryData.getStr("sleepStatus"))) {
                    illnessInfoBuilder.append("睡眠状况：").append(inquiryData.getStr("sleepStatus")).append("\n");
                }
                if (StrUtil.isNotBlank(inquiryData.getStr("emotionalState"))) {
                    illnessInfoBuilder.append("情绪状态：").append(inquiryData.getStr("emotionalState")).append("\n");
                }
                if (StrUtil.isNotBlank(inquiryData.getStr("symptomDescription"))) {
                    symptomInfoBuilder.append("症状描述：").append(inquiryData.getStr("symptomDescription"));
                }
            }
        }
        flatParameters.put("symptomInfo", symptomInfoBuilder.toString().trim());
        flatParameters.put("illnessInfo", illnessInfoBuilder.toString().trim());

        // 9. 处理切诊信息
        StringBuilder pulseInfoBuilder = new StringBuilder();
        StringBuilder palpationDescBuilder = new StringBuilder();
        if (StrUtil.isNotBlank(fourDiagnostic.getPalpation())) {
            JSONObject palpation = JSONUtil.parseObj(fourDiagnostic.getPalpation());
            JSONObject palpationData = palpation.getJSONObject("palpation");
            if (palpationData != null) {
                if (StrUtil.isNotBlank(palpationData.getStr("leftPulseType"))) {
                    pulseInfoBuilder.append("左侧脉象：").append(palpationData.getStr("leftPulseType")).append("\n");
                }
                if (StrUtil.isNotBlank(palpationData.getStr("rightPulseType"))) {
                    pulseInfoBuilder.append("右侧脉象：").append(palpationData.getStr("rightPulseType"));
                }

                if (StrUtil.isNotBlank(palpationData.getStr("description"))) {
                    palpationDescBuilder.append("脉象描述：").append(palpationData.getStr("description"));
                }
            }
        }
        flatParameters.put("pulseInfo", pulseInfoBuilder.toString().trim());
        flatParameters.put("palpationDesc", palpationDescBuilder.toString().trim());

        // 10. 主诉
        flatParameters.put("chiefComplaint", diagnostic.getChiefComplaint());

        return flatParameters;
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
     * 解析AI响应并保存到数据库，返回诊断结果VO
     *
     * @param aiResponse   AI返回的诊断JSON字符串
     * @param diagnosticId 问诊记录ID
     * @param userId       用户ID
     * @return 诊断结果VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AppDiagnosisResultRespVO parseAiResponse(String aiResponse, Long diagnosticId, Long userId) {
        if (StrUtil.isBlank(aiResponse)) {
            throw exception(AI_RESPONSE_EMPTY);
        }

        try {
            // 1. 从AI响应中提取JSON内容
            String jsonContent = extractJsonContent(aiResponse);

            // 2. 解析JSON为响应对象
            AppDiagnosisResultRespVO resultVO = JSONUtil.toBean(jsonContent, AppDiagnosisResultRespVO.class);

            // 3. 保存诊断结果到数据库
            saveDiagnosisTreatment(resultVO, diagnosticId, userId);

            // 4. 直接返回解析后的VO对象
            return resultVO;
        } catch (Exception e) {
            log.error("解析AI响应失败", e);
            throw exception(AI_RESPONSE_PARSE_ERROR);
        }
    }

    /**
     * 保存诊断结果到数据库
     */
    private Long saveDiagnosisTreatment(AppDiagnosisResultRespVO resultVO, Long diagnosticId, Long userId) {
        // 构建数据库实体对象
        DiagnosisTreatmentDO record = new DiagnosisTreatmentDO();
        record.setDiagnosticId(diagnosticId);
        record.setUserId(userId);

        // 1. 设置证型信息
        record.setPrimarySyndrome(resultVO.getPrimarySyndrome());
        record.setPrimarySyndromeExplanation(resultVO.getPrimarySyndromeExplanation());

        // 2. 设置次要证型
        record.setSecondarySyndromes(JSONUtil.toJsonStr(resultVO.getSecondarySyndromes()));

        // 3. 设置八纲辨证
        record.setEightPrinciples(JSONUtil.toJsonStr(resultVO.getEightPrinciples()));

        // 4. 设置症状信息
        record.setSymptoms(JSONUtil.toJsonStr(resultVO.getSymptoms()));

        // 5. 设置辨证分析
        record.setSyndromeAnalysis(resultVO.getSyndromeAnalysis());

        // 6. 设置治疗原则
        record.setTreatmentPrinciple(CollUtil.join(resultVO.getTreatmentPrinciple(), "，"));
        record.setPrincipleExplanation(resultVO.getPrincipleExplanation());

        // 7. 设置方剂信息
        record.setPrescriptions(JSONUtil.toJsonStr(resultVO.getPrescriptions()));

        // 8. 设置生活调养建议
        record.setLifestyleAdvice(JSONUtil.toJsonStr(resultVO.getLifestyleAdvice()));

        // 9. 设置食疗推荐
        record.setDietRecipes(JSONUtil.toJsonStr(resultVO.getDietRecipes()));

        // 10. 设置随访管理
        record.setFollowUp(JSONUtil.toJsonStr(resultVO.getFollowUp()));

        // 11. 设置信心指数
        record.setConfidenceLevel(resultVO.getConfidence());

        // 12. 保存原始AI输出
        record.setAiRawOutput(JSONUtil.toJsonStr(resultVO));

        // 13. 保存到数据库
        diagnosisTreatmentMapper.insert(record);

        // 返回保存记录的ID
        return record.getId();
    }

    /**
     * 从AI回复中提取JSON内容
     */
    private String extractJsonContent(String aiResponse) {
        // 尝试直接解析
        try {
            JSONUtil.parseObj(aiResponse);
            return aiResponse;
        } catch (Exception e) {
            // 如果直接解析失败，尝试使用正则提取
            Pattern pattern = Pattern.compile("\\{[\\s\\S]*\\}");
            Matcher matcher = pattern.matcher(aiResponse);
            if (matcher.find()) {
                return matcher.group();
            }

            // 如果仍然无法提取，返回原始响应
            return aiResponse;
        }
    }

    /**
     * 验证八纲辨证数据是否符合要求
     *
     * @param eightPrinciples 八纲辨证JSON对象
     */
    private void validateEightPrinciples(JSONObject eightPrinciples) {
        // 检查必须包含的四个八纲分类
        String[] principles = {"yinYang", "exteriorInterior", "coldHeat", "deficiencyExcess"};
        for (String principle : principles) {
            JSONObject item = eightPrinciples.getJSONObject(principle);
            if (item == null) {
                throw exception(DIAGNOSIS_EIGHT_PRINCIPLES_MISSING);
            }

            // 检查每个分类是否包含必要字段
            if (!item.containsKey("name") || !item.containsKey("value") ||
                    !item.containsKey("result") || !item.containsKey("description")) {
                throw exception(DIAGNOSIS_EIGHT_PRINCIPLES_FORMAT_ERROR);
            }

            // 验证value值范围(0-100)
            int value = item.getInt("value");
            if (value < 0 || value > 100) {
                throw exception(DIAGNOSIS_EIGHT_PRINCIPLES_STRENGTH_ERROR);
            }
        }
    }

    /**
     * 解析AI回复，提取诊断结果
     */
/*    private AppDiagnosisResultRespVO parseAiResponse(String aiResponse) {
        try {
            // 提取JSON部分 - AI回复可能包含非JSON内容
            String jsonStr = extractJsonContent(aiResponse);

            // 解析JSON
            JSONObject jsonObject = JSONUtil.parseObj(jsonStr);

            // 构建返回对象
            AppDiagnosisResultRespVO result = new AppDiagnosisResultRespVO();

            // 设置诊断类型
            result.setDiagnosisType(jsonObject.getStr("diagnosisType"));

            // 设置诊断详情
            JSONObject diagnosisDetail = jsonObject.getJSONObject("diagnosisDetail");
            if (diagnosisDetail != null) {
                DiagnosisDetailVO detailVO = new DiagnosisDetailVO();

                // 设置八纲辨证
                JSONObject eightPrinciples = diagnosisDetail.getJSONObject("eightPrinciples");
                if (eightPrinciples != null) {
                    EightPrinciplesVO principlesVO = new EightPrinciplesVO();
                    principlesVO.setYinYang(eightPrinciples.getStr("yin_yang"));
                    principlesVO.setExteriorInterior(eightPrinciples.getStr("exterior_interior"));
                    principlesVO.setColdHeat(eightPrinciples.getStr("cold_heat"));
                    principlesVO.setDeficiencyExcess(eightPrinciples.getStr("deficiency_excess"));
                    detailVO.setEightPrinciples(principlesVO);
                }

                // 设置脏腑辨证
                detailVO.setOrganPattern(diagnosisDetail.getStr("organPattern"));

                // 设置主要证型
                detailVO.setPrimarySyndrome(diagnosisDetail.getStr("primarySyndrome"));

                // 设置次要证型
                JSONArray secondarySyndromes = diagnosisDetail.getJSONArray("secondarySyndromes");
                if (secondarySyndromes != null) {
                    List<String> syndromes = new ArrayList<>();
                    for (int i = 0; i < secondarySyndromes.size(); i++) {
                        syndromes.add(secondarySyndromes.getStr(i));
                    }
                    detailVO.setSecondarySyndromes(syndromes);
                }

                // 设置诊断依据和分析
                detailVO.setEvidence(diagnosisDetail.getStr("evidence"));
                detailVO.setAnalysis(diagnosisDetail.getStr("analysis"));

                result.setDiagnosisDetail(detailVO);
            }

            // 设置治疗方案
            JSONObject treatmentPlan = jsonObject.getJSONObject("treatmentPlan");
            if (treatmentPlan != null) {
                TreatmentPlanVO planVO = new TreatmentPlanVO();

                // 设置治疗原则
                planVO.setPrinciple(treatmentPlan.getStr("principle"));

                // 设置方剂
                JSONArray formulas = treatmentPlan.getJSONArray("formulas");
                if (formulas != null) {
                    List<FormulaVO> formulaList = new ArrayList<>();
                    for (int i = 0; i < formulas.size(); i++) {
                        JSONObject formula = formulas.getJSONObject(i);
                        FormulaVO formulaVO = new FormulaVO();
                        formulaVO.setName(formula.getStr("name"));
                        formulaVO.setComposition(formula.getStr("composition"));
                        formulaVO.setUsage(formula.getStr("usage"));
                        formulaVO.setReference(formula.getStr("reference"));
                        formulaList.add(formulaVO);
                    }
                    planVO.setFormulas(formulaList);
                }

                // 设置针灸建议
                JSONObject acupuncture = treatmentPlan.getJSONObject("acupuncture");
                if (acupuncture != null) {
                    AcupunctureVO acupunctureVO = new AcupunctureVO();
                    acupunctureVO.setPoints(acupuncture.getStr("points"));
                    acupunctureVO.setMethods(acupuncture.getStr("methods"));
                    planVO.setAcupuncture(acupunctureVO);
                }

                // 设置生活调养和饮食指导
                planVO.setLifestyle(treatmentPlan.getStr("lifestyle"));
                planVO.setDiet(treatmentPlan.getStr("diet"));

                result.setTreatmentPlan(planVO);
            }

            // 设置信心指数
            result.setConfidence(jsonObject.getInt("confidence", 0));

            return result;
        } catch (Exception e) {
            log.error("解析AI回复失败", e);
            throw exception(AI_RESPONSE_PARSE_ERROR);
        }
    }*/

    /**
     * 更新已有诊断结果
     */
    private void updateExistingDiagnosis(DiagnosisDO existingDiagnosis, AppDiagnosisResultRespVO diagnosisResult) {
        existingDiagnosis.setPrimarySyndrome(diagnosisResult.getPrimarySyndrome());
        existingDiagnosis.setSecondarySyndromes(JSONUtil.toJsonStr(diagnosisResult.getSecondarySyndromes()));
        existingDiagnosis.setEightPrinciples(JSONUtil.toJsonStr(diagnosisResult.getEightPrinciples()));
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
        diagnosis.setPrimarySyndrome(diagnosisResult.getPrimarySyndrome());
        diagnosis.setSecondarySyndromes(JSONUtil.toJsonStr(diagnosisResult.getSecondarySyndromes()));
        diagnosis.setEightPrinciples(JSONUtil.toJsonStr(diagnosisResult.getEightPrinciples()));
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