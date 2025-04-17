package cn.iocoder.yudao.module.medical.controller.admin.constitution;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.*;
import cn.iocoder.yudao.module.medical.service.constitution.ConstitutionQuestionnaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 体质评估问卷")
@RestController
@RequestMapping("/medical/constitution-questionnaire")
@Validated
public class ConstitutionQuestionnaireController {

    @Resource
    private ConstitutionQuestionnaireService questionnaireService;

    @PostMapping("/create")
    @Operation(summary = "创建体质评估问卷")
    @PreAuthorize("@ss.hasPermission('medical:constitution-questionnaire:create')")
    public CommonResult<Long> createQuestionnaire(@Valid @RequestBody ConstitutionQuestionnaireCreateReqVO createReqVO) {
        return success(questionnaireService.createQuestionnaire(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新体质评估问卷")
    @PreAuthorize("@ss.hasPermission('medical:constitution-questionnaire:update')")
    public CommonResult<Boolean> updateQuestionnaire(@Valid @RequestBody ConstitutionQuestionnaireUpdateReqVO updateReqVO) {
        questionnaireService.updateQuestionnaire(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除体质评估问卷")
    @Parameter(name = "id", description = "问卷ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('medical:constitution-questionnaire:delete')")
    public CommonResult<Boolean> deleteQuestionnaire(@RequestParam("id") Long id) {
        questionnaireService.deleteQuestionnaire(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取体质评估问卷")
    @Parameter(name = "id", description = "问卷ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('medical:constitution-questionnaire:query')")
    public CommonResult<ConstitutionQuestionnaireRespVO> getQuestionnaire(@RequestParam("id") Long id) {
        return success(questionnaireService.getQuestionnaire(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获取体质评估问卷分页")
    @PreAuthorize("@ss.hasPermission('medical:constitution-questionnaire:query')")
    public CommonResult<PageResult<ConstitutionQuestionnaireRespVO>> getQuestionnairePage(
            @Valid ConstitutionQuestionnairePageReqVO pageVO) {
        return success(questionnaireService.getQuestionnairePage(pageVO));
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新问卷状态")
    @PreAuthorize("@ss.hasPermission('medical:constitution-questionnaire:update')")
    public CommonResult<Boolean> updateQuestionnaireStatus(@Valid @RequestBody ConstitutionQuestionnaireUpdateStatusReqVO reqVO) {
        questionnaireService.updateQuestionnaireStatus(reqVO);
        return success(true);
    }
}