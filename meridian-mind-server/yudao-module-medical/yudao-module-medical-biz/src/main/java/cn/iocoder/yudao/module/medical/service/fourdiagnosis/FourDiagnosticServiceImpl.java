package cn.iocoder.yudao.module.medical.service.fourdiagnosis;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.medical.controller.app.fourdiagnosis.vo.AppFourDiagnosticRespVO;
import cn.iocoder.yudao.module.medical.convert.fourdiagnosis.FourDiagnosticConvert;
import cn.iocoder.yudao.module.medical.dal.dataobject.fourdiagnosis.FourDiagnosticDO;
import cn.iocoder.yudao.module.medical.dal.mysql.fourdiagnosis.FourDiagnosticMapper;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.adapter.FaceAnalysisAdapter;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.adapter.TongueAnalysisAdapter;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.adapter.VoiceAnalysisAdapter;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.FacialFeatureDTO;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.TongueFeatureDTO;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.VoiceFeatureDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 四诊信息 Service 实现类
 */
@Service
@Slf4j
public class FourDiagnosticServiceImpl implements FourDiagnosticService {

    private static final int ERROR_CODE_NOT_FOUND = 1_016_001_100; // 四诊信息不存在错误码
    @Resource
    private FourDiagnosticMapper fourDiagnosticMapper;
    @Resource
    private TongueAnalysisAdapter tongueAnalysisAdapter;
    @Resource
    private FaceAnalysisAdapter faceAnalysisAdapter;
    @Resource
    private VoiceAnalysisAdapter voiceAnalysisAdapter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFourDiagnostic(Long diagnosticId) {
        // 检查是否已存在
        FourDiagnosticDO existing = fourDiagnosticMapper.selectByDiagnosticId(diagnosticId);
        if (existing != null) {
            return existing.getId();
        }

        // 创建新记录
        FourDiagnosticDO fourDiagnosticDO = new FourDiagnosticDO();
        fourDiagnosticDO.setDiagnosticId(diagnosticId);
        fourDiagnosticMapper.insert(fourDiagnosticDO);

        return fourDiagnosticDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppFourDiagnosticRespVO saveTongueAnalysis(Long id, MultipartFile tongueImage) {
        // 获取或创建四诊信息
        FourDiagnosticDO fourDiagnosticDO = createDiagnosticIfAbsent(id);

        // 调用舌象分析
        TongueFeatureDTO tongueFeature = tongueAnalysisAdapter.analyzeTongueImage(tongueImage);

        // 更新数据
        fourDiagnosticDO.setInspection(JSONUtil.toJsonStr(
                JSONUtil.createObj().set("tongue", tongueFeature)
        ));
        fourDiagnosticDO.setTongueImage(tongueFeature.getImageUrl());

        // 保存更新
        fourDiagnosticMapper.updateById(fourDiagnosticDO);

        // 返回结果
        return FourDiagnosticConvert.INSTANCE.convert(fourDiagnosticDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppFourDiagnosticRespVO saveFaceAnalysis(Long id, MultipartFile facialImage) {
        // 获取或创建四诊信息
        FourDiagnosticDO fourDiagnosticDO = createDiagnosticIfAbsent(id);

        // 调用面色分析
        FacialFeatureDTO facialFeature = faceAnalysisAdapter.analyzeFacialImage(facialImage);

        // 获取当前inspection数据，避免覆盖已有舌象数据
        String inspectionJson = fourDiagnosticDO.getInspection();
        Object inspection = ObjectUtil.isNotEmpty(inspectionJson) ?
                JSONUtil.parseObj(inspectionJson) : JSONUtil.createObj();

        // 更新数据
        fourDiagnosticDO.setInspection(JSONUtil.toJsonStr(
                JSONUtil.parseObj(inspection).set("face", facialFeature)
        ));
        fourDiagnosticDO.setFaceImage(facialFeature.getImageUrl());

        // 保存更新
        fourDiagnosticMapper.updateById(fourDiagnosticDO);

        // 返回结果
        return FourDiagnosticConvert.INSTANCE.convert(fourDiagnosticDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppFourDiagnosticRespVO saveVoiceAnalysis(Long id, MultipartFile voiceAudio) {
        // 获取或创建四诊信息
        FourDiagnosticDO fourDiagnosticDO = createDiagnosticIfAbsent(id);

        // 调用语音分析
        VoiceFeatureDTO voiceFeature = voiceAnalysisAdapter.analyzeVoiceAudio(voiceAudio);

        // 更新数据
        fourDiagnosticDO.setAuscultation(JSONUtil.toJsonStr(
                JSONUtil.createObj().set("voice", voiceFeature)
        ));

        // 保存更新
        fourDiagnosticMapper.updateById(fourDiagnosticDO);

        // 返回结果
        return FourDiagnosticConvert.INSTANCE.convert(fourDiagnosticDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppFourDiagnosticRespVO saveInquiry(Long id, String inquiryData) {
        // 获取或创建四诊信息
        FourDiagnosticDO fourDiagnosticDO = validateAndGet(id);

        // 更新数据
        fourDiagnosticDO.setInquiry(inquiryData);

        // 保存更新
        fourDiagnosticMapper.updateById(fourDiagnosticDO);

        // 返回结果
        return FourDiagnosticConvert.INSTANCE.convert(fourDiagnosticDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppFourDiagnosticRespVO savePalpation(Long id, String palpationData, String pulseDescription) {
        // 获取并校验四诊信息
        FourDiagnosticDO fourDiagnosticDO = createDiagnosticIfAbsent(id);

        // 更新数据
        fourDiagnosticDO.setPalpation(palpationData);
        fourDiagnosticDO.setPulseDescription(pulseDescription);

        // 保存更新
        fourDiagnosticMapper.updateById(fourDiagnosticDO);

        // 返回结果
        return FourDiagnosticConvert.INSTANCE.convert(fourDiagnosticDO);
    }

    @Override
    public AppFourDiagnosticRespVO getFourDiagnostic(Long id) {
        FourDiagnosticDO fourDiagnosticDO = validateAndGet(id);
        return FourDiagnosticConvert.INSTANCE.convert(fourDiagnosticDO);
    }

    @Override
    public AppFourDiagnosticRespVO getFourDiagnosticByDiagnosticId(Long diagnosticId) {
        FourDiagnosticDO fourDiagnosticDO = fourDiagnosticMapper.selectByDiagnosticId(diagnosticId);
        if (fourDiagnosticDO == null) {
            throw new ServiceException(ERROR_CODE_NOT_FOUND, "四诊信息不存在");
        }
        return FourDiagnosticConvert.INSTANCE.convert(fourDiagnosticDO);
    }

    /**
     * 获取或创建四诊信息
     *
     * @param diagnosticId 四诊信息ID
     * @return 四诊信息DO
     */
    private FourDiagnosticDO createDiagnosticIfAbsent(Long diagnosticId) {
        // 检查是否已存在
        FourDiagnosticDO existing = fourDiagnosticMapper.selectByDiagnosticId(diagnosticId);
        if (existing != null) {
            return existing;
        }

        // 创建新记录
        FourDiagnosticDO fourDiagnosticDO = new FourDiagnosticDO();
        fourDiagnosticDO.setDiagnosticId(diagnosticId);
        fourDiagnosticMapper.insert(fourDiagnosticDO);

        return fourDiagnosticDO;
    }
    /**
     * 验证并获取四诊信息
     *
     * @param id 四诊信息ID
     * @return 四诊信息DO
     */
    private FourDiagnosticDO validateAndGet(Long id) {
        FourDiagnosticDO fourDiagnosticDO = fourDiagnosticMapper.selectById(id);
        if (fourDiagnosticDO == null) {
            throw new ServiceException(ERROR_CODE_NOT_FOUND, "四诊信息不存在");
        }
        return fourDiagnosticDO;
    }
}