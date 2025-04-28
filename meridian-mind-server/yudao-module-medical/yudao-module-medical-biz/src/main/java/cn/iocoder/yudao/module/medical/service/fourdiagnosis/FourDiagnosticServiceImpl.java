package cn.iocoder.yudao.module.medical.service.fourdiagnosis;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.medical.controller.app.fourdiagnosis.vo.AppFourDiagnosticRespVO;
import cn.iocoder.yudao.module.medical.convert.fourdiagnosis.FourDiagnosticConvert;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnostic.DiagnosticDO;
import cn.iocoder.yudao.module.medical.dal.dataobject.fourdiagnosis.FourDiagnosticDO;
import cn.iocoder.yudao.module.medical.dal.mysql.diagnostic.DiagnosticMapper;
import cn.iocoder.yudao.module.medical.dal.mysql.fourdiagnosis.FourDiagnosticMapper;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.adapter.FaceAnalysisAdapter;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.adapter.TongueAnalysisAdapter;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.adapter.VoiceAnalysisAdapter;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.*;
import cn.iocoder.yudao.module.medical.service.diagnosis.DiagnosisService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.medical.enums.ErrorCodeConstants.ERROR_CODE_SAVE_FACE_IMAGE;
import static cn.iocoder.yudao.module.medical.enums.ErrorCodeConstants.ERROR_CODE_SAVE_VOICE_AUDIO;

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
    @Resource
    private FileApi fileApi;
    @Resource
    private DiagnosisService diagnosisService;
    @Resource
    private DiagnosticMapper diagnosticMapper;

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
    public AppFourDiagnosticRespVO saveTongueAnalysis(Long id, MultipartFile tongueImage) throws IOException {
        // 获取或创建四诊信息
        FourDiagnosticDO fourDiagnosticDO = createDiagnosticIfAbsent(id);

        // 调用舌象分析
        TongueFeatureDTO tongueFeature = tongueAnalysisAdapter.analyzeTongueImage(tongueImage);

        // 分析成功后，再保存文件
        String fileUrl = fileApi.createFile(tongueImage.getOriginalFilename(), "medical/tongue",
                IoUtil.readBytes(tongueImage.getInputStream()));
        tongueFeature.setImageUrl(fileUrl);

        // 更新数据
        fourDiagnosticDO.setInspection(JSONUtil.toJsonStr(
                JSONUtil.createObj().set("tongue", tongueFeature)
        ));
        fourDiagnosticDO.setTongueImage(fileUrl);

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

        // 分析成功后，保存文件
        try {
            String fileUrl = fileApi.createFile(facialImage.getOriginalFilename(),
                    "medical/face/" + id, facialImage.getBytes());

            // 设置图片URL
            facialFeature.setImageUrl(fileUrl);

            // 获取当前inspection数据，避免覆盖已有舌象数据
            String inspectionJson = fourDiagnosticDO.getInspection();
            Object inspection = ObjectUtil.isNotEmpty(inspectionJson) ?
                    JSONUtil.parseObj(inspectionJson) : JSONUtil.createObj();

            // 更新数据
            fourDiagnosticDO.setInspection(JSONUtil.toJsonStr(
                    JSONUtil.parseObj(inspection).set("face", facialFeature)
            ));
            fourDiagnosticDO.setFaceImage(fileUrl);

            // 保存更新
            fourDiagnosticMapper.updateById(fourDiagnosticDO);
        } catch (Exception e) {
            log.error("[saveFaceAnalysis] 保存面色图像失败: {}", e.getMessage());
            throw exception(ERROR_CODE_SAVE_FACE_IMAGE, "保存面色图像失败: " + e.getMessage());
        }

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

        // 分析成功后，保存文件
        try {
            String fileUrl = fileApi.createFile(voiceAudio.getOriginalFilename(),
                    "medical/voice/" + id, voiceAudio.getBytes());

            // 设置音频URL
            voiceFeature.setAudioUrl(fileUrl);

            // 更新数据
            fourDiagnosticDO.setAuscultation(JSONUtil.toJsonStr(
                    JSONUtil.createObj().set("voice", voiceFeature)
            ));

            // 保存更新
            fourDiagnosticMapper.updateById(fourDiagnosticDO);
        } catch (Exception e) {
            log.error("[saveVoiceAnalysis] 保存语音音频失败: {}", e.getMessage());
            throw exception(ERROR_CODE_SAVE_VOICE_AUDIO, "保存语音音频失败: " + e.getMessage());
        }

        // 返回结果
        return FourDiagnosticConvert.INSTANCE.convert(fourDiagnosticDO);
    }

    @Override
    public AppFourDiagnosticRespVO saveInquiry(Long id, InquiryDTO inquiryData) {
        // 获取并校验四诊信息
        FourDiagnosticDO fourDiagnosticDO = validateAndGet(id);

        // 获取当前inquiry数据，如果为空则创建新对象
        String inquiryJson = fourDiagnosticDO.getInquiry();
        Object inquiry = ObjectUtil.isNotEmpty(inquiryJson) ?
                JSONUtil.parseObj(inquiryJson) : JSONUtil.createObj();

        // 将DTO转换为JSON并保存
        fourDiagnosticDO.setInquiry(JSONUtil.toJsonStr(
                JSONUtil.parseObj(inquiry).set("inquiry", inquiryData)
        ));

        // 保存更新到数据库
        fourDiagnosticMapper.updateById(fourDiagnosticDO);

        // 触发AI诊断
        triggerAiDiagnosis(fourDiagnosticDO.getDiagnosticId());

        // 返回更新后的结果
        return FourDiagnosticConvert.INSTANCE.convert(fourDiagnosticDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppFourDiagnosticRespVO savePalpation(Long id, PulseFeatureDTO palpationData) {
        // 获取并校验四诊信息
        FourDiagnosticDO fourDiagnosticDO = createDiagnosticIfAbsent(id);

        // 获取当前inquiry数据，如果为空则创建新对象
        String palpationJson = fourDiagnosticDO.getPalpation();
        Object palpation = ObjectUtil.isNotEmpty(palpationJson) ?
                JSONUtil.parseObj(palpationJson) : JSONUtil.createObj();

        // 将DTO转换为JSON并保存
        fourDiagnosticDO.setPalpation(JSONUtil.toJsonStr(
                JSONUtil.parseObj(palpation).set("palpation", palpationData)
        ));

        // 保存更新
        fourDiagnosticMapper.updateById(fourDiagnosticDO);

        // 返回结果
        return FourDiagnosticConvert.INSTANCE.convert(fourDiagnosticDO);
    }
    /**
     * 触发AI诊断
     *
     * @param diagnosticId 问诊记录ID
     */
    private void triggerAiDiagnosis(Long diagnosticId) {
        try {
            // 获取问诊记录
            DiagnosticDO diagnostic = diagnosticMapper.selectById(diagnosticId);
            if (diagnostic == null) {
                log.error("问诊记录不存在，ID: {}", diagnosticId);
                return;
            }

            // 异步调用，避免等待时间过长
            CompletableFuture.runAsync(() -> {
                try {
                    log.info("开始AI诊断，问诊ID: {}", diagnosticId);
                    diagnosisService.generateDiagnosis(diagnostic.getUserId(), diagnosticId);
                    log.info("AI诊断完成，问诊ID: {}", diagnosticId);
                } catch (Exception e) {
                    log.error("AI诊断异常，问诊ID: {}", diagnosticId, e);
                }
            });
        } catch (Exception e) {
            log.error("触发AI诊断异常，问诊ID: {}", diagnosticId, e);
        }
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

    @Transactional(rollbackFor = Exception.class)
    public AppFourDiagnosticRespVO updateTongueFeatures(Long id, TongueFeatureDTO tongueFeature) {
        // 获取并校验四诊信息
        FourDiagnosticDO fourDiagnosticDO = validateAndGet(id);

        // 获取当前inspection数据
        String inspectionJson = fourDiagnosticDO.getInspection();
        Object inspection = ObjectUtil.isNotEmpty(inspectionJson) ?
                JSONUtil.parseObj(inspectionJson) : JSONUtil.createObj();

        // 更新舌象数据
        fourDiagnosticDO.setInspection(JSONUtil.toJsonStr(
                JSONUtil.parseObj(inspection).set("tongue", tongueFeature)
        ));

        // 保存更新
        fourDiagnosticMapper.updateById(fourDiagnosticDO);

        // 返回结果
        return FourDiagnosticConvert.INSTANCE.convert(fourDiagnosticDO);
    }

    @Override
    public AppFourDiagnosticRespVO updateFaceFeatures(Long id, FacialFeatureDTO facialFeature) {
        // 获取并校验四诊信息
        FourDiagnosticDO fourDiagnosticDO = validateAndGet(id);

        // 获取当前inspection数据
        String inspectionJson = fourDiagnosticDO.getInspection();
        Object inspection = ObjectUtil.isNotEmpty(inspectionJson) ?
                JSONUtil.parseObj(inspectionJson) : JSONUtil.createObj();

        // 更新面相数据
        fourDiagnosticDO.setInspection(JSONUtil.toJsonStr(
                JSONUtil.parseObj(inspection).set("face", facialFeature)
        ));

        // 保存更新
        fourDiagnosticMapper.updateById(fourDiagnosticDO);

        // 返回结果
        return FourDiagnosticConvert.INSTANCE.convert(fourDiagnosticDO);
    }

    @Override
    public AppFourDiagnosticRespVO updateVoiceFeatures(Long id, AuscultationDTO voiceFeature) {
        // 获取并校验四诊信息
        FourDiagnosticDO fourDiagnosticDO = validateAndGet(id);

        // 获取当前inspection数据
        String auscultation = fourDiagnosticDO.getAuscultation();
        Object entries = ObjectUtil.isNotEmpty(auscultation) ?
                JSONUtil.parseObj(auscultation) : JSONUtil.createObj();

        // 更新嗓音数据
        if (voiceFeature.getVoiceFeature() != null) {
            fourDiagnosticDO.setAuscultation(JSONUtil.toJsonStr(
                    JSONUtil.parseObj(entries).set("voice", voiceFeature.getVoiceFeature())
            ));
        }
        // 更新气味数据
        if (voiceFeature.getOdorFeature() != null) {
            fourDiagnosticDO.setAuscultation(JSONUtil.toJsonStr(
                    JSONUtil.parseObj(entries).set("odor", voiceFeature.getOdorFeature())
            ));
        }

        // 保存更新
        fourDiagnosticMapper.updateById(fourDiagnosticDO);

        // 返回结果
        return FourDiagnosticConvert.INSTANCE.convert(fourDiagnosticDO);
    }
}