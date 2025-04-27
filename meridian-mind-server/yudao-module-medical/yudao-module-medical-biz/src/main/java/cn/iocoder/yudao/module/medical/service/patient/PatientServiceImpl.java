package cn.iocoder.yudao.module.medical.service.patient;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.medical.controller.admin.patient.vo.PatientProfilePageReqVO;
import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppPatientProfileRespVO;
import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppPatientProfileSaveReqVO;
import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppPatientProfileUpdateReqVO;
import cn.iocoder.yudao.module.medical.convert.patient.PatientProfileConvert;
import cn.iocoder.yudao.module.medical.dal.dataobject.constitution.ConstitutionRecordDO;
import cn.iocoder.yudao.module.medical.dal.dataobject.patient.PatientProfileDO;
import cn.iocoder.yudao.module.medical.dal.mysql.constitution.ConstitutionRecordMapper;
import cn.iocoder.yudao.module.medical.dal.mysql.patient.PatientProfileMapper;
import cn.iocoder.yudao.module.medical.enums.ConstitutionTypeEnum;
import cn.iocoder.yudao.module.member.api.user.MemberUserApi;
import cn.iocoder.yudao.module.member.api.user.dto.MemberUserRespDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.medical.enums.ErrorCodeConstants.PATIENT_PROFILE_NOT_EXISTS;
import static cn.iocoder.yudao.module.medical.enums.ErrorCodeConstants.RECORD_NOT_EXISTS;

/**
 * 患者管理 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class PatientServiceImpl implements PatientService {

    @Resource
    private PatientProfileMapper patientProfileMapper;
    @Resource
    private MemberUserApi memberUserApi;
    @Resource
    private ConstitutionRecordMapper recordMapper;

    @Override
    public Long createPatientProfile(AppPatientProfileSaveReqVO createReqVO) {
        // 插入
        PatientProfileDO patientProfile = BeanUtils.toBean(createReqVO, PatientProfileDO.class);
        patientProfileMapper.insert(patientProfile);
        // 返回
        return patientProfile.getId();
    }

    @Override
    public void updatePatientProfile(AppPatientProfileUpdateReqVO updateReqVO) {
        // 查询现有档案
        PatientProfileDO profileDO = patientProfileMapper.selectByUserId(updateReqVO.getUserId());
        // 更新
        updateProfile(profileDO, updateReqVO);
        patientProfileMapper.updateById(profileDO);
    }

    /**
     * 更新患者档案
     */
    private void updateProfile(PatientProfileDO profileDO, AppPatientProfileUpdateReqVO updateReqVO) {
        // 仅更新非空字段
        if (updateReqVO.getHeight() != null) {
            profileDO.setHeight(updateReqVO.getHeight());
        }

        if (updateReqVO.getWeight() != null) {
            profileDO.setWeight(updateReqVO.getWeight());
        }

        if (updateReqVO.getBloodType() != null) {
            profileDO.setBloodType(updateReqVO.getBloodType());
        }

        if (updateReqVO.getConstitutionType() != null) {
            profileDO.setConstitutionType(updateReqVO.getConstitutionType());
        }

        // 更新过敏史记录
        if (updateReqVO.getAllergies() != null) {
            profileDO.setAllergies(JSONUtil.toJsonStr(updateReqVO.getAllergies()));
        }

        // 更新既往病史记录
        if (updateReqVO.getMedicalHistory() != null) {
            profileDO.setMedicalHistory(JSONUtil.toJsonStr(updateReqVO.getMedicalHistory()));
        }

        // 更新家族病史记录
        if (updateReqVO.getFamilyMedicalHistory() != null) {
            profileDO.setFamilyMedicalHistory(JSONUtil.toJsonStr(updateReqVO.getFamilyMedicalHistory()));
        }
    }

    @Override
    public void deletePatientProfile(Long id) {
        // 校验存在
        validatePatientProfileExists(id);
        // 删除
        patientProfileMapper.deleteById(id);
    }

    private void validatePatientProfileExists(Long id) {
        if (patientProfileMapper.selectById(id) == null) {
            throw exception(PATIENT_PROFILE_NOT_EXISTS);
        }
    }

    @Override
    public AppPatientProfileRespVO getPatientProfile(Long userId) {
        PatientProfileDO profileDO = patientProfileMapper.selectByUserId(userId);
        if (profileDO == null) {
            return new AppPatientProfileRespVO();
        }
        // 查询用户信息
        MemberUserRespDTO user = memberUserApi.getUser(userId);

        // 将JSON字符串转换为对象列表
        AppPatientProfileRespVO respVO = PatientProfileConvert.INSTANCE.convert(profileDO, user);
        // 年龄计算与设置
        if (user != null && user.getBirthday() != null) {
            LocalDate birthDate = user.getBirthday().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate currentDate = LocalDate.now();
            int age = Period.between(birthDate, currentDate).getYears();
            respVO.setAge(age);
        }

        // 转换过敏史记录
        if (StrUtil.isNotEmpty(profileDO.getAllergies())) {
            respVO.setAllergies(JSONUtil.toList(profileDO.getAllergies(),
                    AppPatientProfileRespVO.AllergyRecord.class));
        }

        // 转换既往病史记录
        if (StrUtil.isNotEmpty(profileDO.getMedicalHistory())) {
            respVO.setMedicalHistory(JSONUtil.toList(profileDO.getMedicalHistory(),
                    AppPatientProfileRespVO.DiseaseRecord.class));
        }

        // 转换家族病史记录
        if (StrUtil.isNotEmpty(profileDO.getFamilyMedicalHistory())) {
            respVO.setFamilyMedicalHistory(JSONUtil.toList(profileDO.getFamilyMedicalHistory(),
                    AppPatientProfileRespVO.FamilyMedicalRecord.class));
        }

        return respVO;
    }

    @Override
    public PageResult<PatientProfileDO> getPatientProfilePage(PatientProfilePageReqVO pageReqVO) {
        return patientProfileMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConstitutionType(Long userId, Long recordId) {
        // 1. 获取评估记录
        ConstitutionRecordDO record = recordMapper.selectById(recordId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw exception(RECORD_NOT_EXISTS);
        }

        // 2. 查询用户健康档案
        PatientProfileDO profile = patientProfileMapper.selectByUserId(userId);

        // 3. 构建体质信息JSON
        Map<String, Object> constitutionInfo = new HashMap<>();

        // 3.1 处理主体质，转换为中文
        String primaryChineseName = ConstitutionTypeEnum.nameOf(record.getPrimaryType());
        constitutionInfo.put("primary", primaryChineseName);

        // 3.2 处理次要体质，转换为中文
        if (record.getSecondaryTypes() != null) {
            List<String> secondaryTypeNames = record.getSecondaryTypes().stream()
                    .map(ConstitutionTypeEnum::nameOf)
                    .collect(Collectors.toList());
            constitutionInfo.put("secondary", secondaryTypeNames);
        }

        // 3.3 处理体质分数，转换键名为中文
        Map<String, Integer> originalScores = record.getScores();
        Map<String, Integer> chineseScores = new LinkedHashMap<>();

        if (originalScores != null && !originalScores.isEmpty()) {
            originalScores.forEach((englishType, score) -> {
                String chineseType = ConstitutionTypeEnum.nameOf(englishType);
                chineseScores.put(chineseType, score);
            });
            constitutionInfo.put("scores", chineseScores);
        }

        // 4. 转换为JSON字符串
        String constitutionJson = JSONUtil.toJsonStr(constitutionInfo);

        // 5. 更新用户档案
        patientProfileMapper.updateById(new PatientProfileDO()
                .setId(profile.getId())
                .setConstitutionType(constitutionJson));
    }

}