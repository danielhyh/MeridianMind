package cn.iocoder.yudao.module.medical.controller.app.diagnosis;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.medical.controller.app.diagnosis.vo.AppDiagnosisResultRespVO;
import cn.iocoder.yudao.module.medical.service.diagnosis.DiagnosisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - 诊断结果")
@RestController
@RequestMapping("/medical/diagnosis")
@Validated
public class AppDiagnosisController {

    @Resource
    private DiagnosisService diagnosisService;

    @GetMapping("/get-by-diagnostic-id")
    @Operation(summary = "获取诊断结果")
    @Parameter(name = "diagnosticId", description = "问诊记录ID", required = true)
    public CommonResult<AppDiagnosisResultRespVO> getDiagnosisResult(@RequestParam("diagnosticId") Long diagnosticId) {
        return success(diagnosisService.getDiagnosisResult(diagnosticId));
    }

    @PostMapping("/generate")
    @Operation(summary = "生成诊断结果")
    @Parameter(name = "diagnosticId", description = "问诊记录ID", required = true)
    public CommonResult<AppDiagnosisResultRespVO> generateDiagnosis(@RequestParam("diagnosticId") Long diagnosticId) {
        return success(diagnosisService.generateDiagnosis(getLoginUserId(), diagnosticId));
    }
}