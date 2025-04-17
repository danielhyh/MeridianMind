package cn.iocoder.yudao.module.medical.controller.app.constitution;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.medical.controller.app.constitution.vo.AppConstitutionQuestionnaireRespVO;
import cn.iocoder.yudao.module.medical.controller.app.constitution.vo.AppConstitutionResultRespVO;
import cn.iocoder.yudao.module.medical.controller.app.constitution.vo.AppConstitutionSubmitReqVO;
import cn.iocoder.yudao.module.medical.service.constitution.ConstitutionQuestionnaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - 体质评估问卷")
@RestController
@RequestMapping("/medical/constitution")
@Validated
public class AppConstitutionController {

    @Resource
    private ConstitutionQuestionnaireService constitutionQuestionnaireService;

    @GetMapping("/questionnaire")
    @Operation(summary = "获取最新的体质评估问卷")
    public CommonResult<AppConstitutionQuestionnaireRespVO> getQuestionnaire() {
        return success(constitutionQuestionnaireService.getLatestQuestionnaire());
    }
    @PostMapping("/initiate")
    @Operation(summary = "发起体质评估")
    public CommonResult<Long> initiateAssessment(@RequestParam("questionnaireId") Long questionnaireId) {
        Long recordId = constitutionQuestionnaireService.initiateAssessment(getLoginUserId(), questionnaireId);
        return success(recordId);
    }

    @PostMapping("/submit")
    @Operation(summary = "提交体质评估问卷")
    public CommonResult<AppConstitutionResultRespVO> submitQuestionnaire(@Valid @RequestBody AppConstitutionSubmitReqVO reqVO) {
        return success(constitutionQuestionnaireService.submitQuestionnaire(getLoginUserId(), reqVO));
    }
    @PostMapping("/apply-to-profile")
    @Operation(summary = "应用评估结果到用户档案")
    public CommonResult<Boolean> applyToProfile(@RequestParam("recordId") Long recordId) {
        constitutionQuestionnaireService.applyToProfile(getLoginUserId(), recordId);
        return success(true);
    }

    @GetMapping("/result")
    @Operation(summary = "获取用户最近的体质评估结果")
    public CommonResult<AppConstitutionResultRespVO> getLatestResult() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return success(constitutionQuestionnaireService.getLatestResult(userId));
    }
}