package cn.iocoder.yudao.module.medical.controller.app.fourdiagnosis;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.medical.controller.app.fourdiagnosis.vo.AppFourDiagnosticRespVO;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.FacialFeatureDTO;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.TongueFeatureDTO;
import cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto.VoiceFeatureDTO;
import cn.iocoder.yudao.module.medical.service.fourdiagnosis.FourDiagnosticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 四诊信息")
@RestController
@RequestMapping("/medical/four-diagnostic")
@Validated
@Slf4j
public class AppFourDiagnosticController {

    @Resource
    private FourDiagnosticService fourDiagnosticService;

    @PostMapping("/create")
    @Operation(summary = "创建四诊信息记录")
    @Parameter(name = "diagnosticId", description = "问诊记录ID", required = true, example = "1")
    public CommonResult<Long> createFourDiagnostic(@RequestParam("diagnosticId") Long diagnosticId) {
        return success(fourDiagnosticService.createFourDiagnostic(diagnosticId));
    }

    @PostMapping("/tongue-analysis")
    @Operation(summary = "舌象分析")
    public CommonResult<AppFourDiagnosticRespVO> analyzeTongue(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("tongueImage") MultipartFile tongueImage) throws IOException {
        return success(fourDiagnosticService.saveTongueAnalysis(id, tongueImage));
    }

    @PostMapping("/face-analysis")
    @Operation(summary = "面色分析")
    public CommonResult<AppFourDiagnosticRespVO> analyzeFace(
            @RequestParam("id") Long id,
            @RequestParam("facialImage") MultipartFile facialImage) {
        return success(fourDiagnosticService.saveFaceAnalysis(id, facialImage));
    }

    @PostMapping("/voice-analysis")
    @Operation(summary = "语音分析")
    public CommonResult<AppFourDiagnosticRespVO> analyzeVoice(
            @RequestParam("id") Long id,
            @RequestParam("voiceAudio") MultipartFile voiceAudio) {
        return success(fourDiagnosticService.saveVoiceAnalysis(id, voiceAudio));
    }

    @PostMapping("/update-tongue-features")
    @Operation(summary = "更新舌象分析结果")
    public CommonResult<AppFourDiagnosticRespVO> updateTongueFeatures(
            @RequestParam("id") Long id,
            @RequestBody TongueFeatureDTO tongueFeature) {
        return success(fourDiagnosticService.updateTongueFeatures(id, tongueFeature));
    }

    @PostMapping("/update-face-features")
    @Operation(summary = "更新面色分析结果")
    public CommonResult<AppFourDiagnosticRespVO> updateFaceFeatures(
            @RequestParam("id") Long id,
            @RequestBody FacialFeatureDTO facialFeature) {
        return success(fourDiagnosticService.updateFaceFeatures(id, facialFeature));
    }

    @PostMapping("/update-voice-features")
    @Operation(summary = "更新语音分析结果")
    public CommonResult<AppFourDiagnosticRespVO> updateVoiceFeatures(
            @RequestParam("id") Long id,
            @RequestBody VoiceFeatureDTO voiceFeature) {
        return success(fourDiagnosticService.updateVoiceFeatures(id, voiceFeature));
    }

    @PostMapping("/save-inquiry")
    @Operation(summary = "保存问诊数据")
    public CommonResult<AppFourDiagnosticRespVO> saveInquiry(
            @RequestParam("id") Long id,
            @RequestParam("inquiryData") String inquiryData) {
        return success(fourDiagnosticService.saveInquiry(id, inquiryData));
    }

    @PostMapping("/save-palpation")
    @Operation(summary = "保存脉象数据")
    public CommonResult<AppFourDiagnosticRespVO> savePalpation(
            @RequestParam("id") Long id,
            @RequestParam("palpationData") String palpationData,
            @RequestParam(value = "pulseDescription", required = false) String pulseDescription) {
        return success(fourDiagnosticService.savePalpation(id, palpationData, pulseDescription));
    }

    @GetMapping("/get")
    @Operation(summary = "获取四诊信息")
    @Parameter(name = "id", description = "四诊信息ID", required = true, example = "1")
    public CommonResult<AppFourDiagnosticRespVO> getFourDiagnostic(@RequestParam("id") Long id) {
        return success(fourDiagnosticService.getFourDiagnostic(id));
    }

    @GetMapping("/get-by-diagnostic-id")
    @Operation(summary = "根据问诊记录ID获取四诊信息")
    @Parameter(name = "diagnosticId", description = "问诊记录ID", required = true, example = "1")
    public CommonResult<AppFourDiagnosticRespVO> getFourDiagnosticByDiagnosticId(@RequestParam("diagnosticId") Long diagnosticId) {
        return success(fourDiagnosticService.getFourDiagnosticByDiagnosticId(diagnosticId));
    }
}