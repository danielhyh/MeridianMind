package cn.iocoder.yudao.module.medical.service.fourdiagnosis;

import cn.iocoder.yudao.module.medical.controller.admin.fourdiagnosis.vo.FourDiagnosticRespVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 四诊信息 Service 接口
 */
public interface FourDiagnosticService {

    /**
     * 创建四诊信息记录
     *
     * @param diagnosticId 问诊记录ID
     * @return 四诊信息ID
     */
    Long createFourDiagnostic(Long diagnosticId);

    /**
     * 保存舌象分析结果
     *
     * @param id          四诊信息ID
     * @param tongueImage 舌象图像
     * @return 更新后的四诊信息
     */
    FourDiagnosticRespVO saveTongueAnalysis(Long id, MultipartFile tongueImage);

    /**
     * 保存面色分析结果
     *
     * @param id          四诊信息ID
     * @param facialImage 面色图像
     * @return 更新后的四诊信息
     */
    FourDiagnosticRespVO saveFaceAnalysis(Long id, MultipartFile facialImage);

    /**
     * 保存语音分析结果
     *
     * @param id         四诊信息ID
     * @param voiceAudio 语音音频
     * @return 更新后的四诊信息
     */
    FourDiagnosticRespVO saveVoiceAnalysis(Long id, MultipartFile voiceAudio);

    /**
     * 保存问诊数据
     *
     * @param id          四诊信息ID
     * @param inquiryData 问诊数据
     * @return 更新后的四诊信息
     */
    FourDiagnosticRespVO saveInquiry(Long id, String inquiryData);

    /**
     * 保存脉象数据
     *
     * @param id               四诊信息ID
     * @param palpationData    脉象数据
     * @param pulseDescription 脉象描述
     * @return 更新后的四诊信息
     */
    FourDiagnosticRespVO savePalpation(Long id, String palpationData, String pulseDescription);

    /**
     * 获取四诊信息
     *
     * @param id 四诊信息ID
     * @return 四诊信息
     */
    FourDiagnosticRespVO getFourDiagnostic(Long id);

    /**
     * 根据问诊记录ID获取四诊信息
     *
     * @param diagnosticId 问诊记录ID
     * @return 四诊信息
     */
    FourDiagnosticRespVO getFourDiagnosticByDiagnosticId(Long diagnosticId);
}