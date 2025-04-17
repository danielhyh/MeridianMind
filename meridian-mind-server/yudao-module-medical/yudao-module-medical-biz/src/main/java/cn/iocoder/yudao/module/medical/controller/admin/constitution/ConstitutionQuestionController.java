package cn.iocoder.yudao.module.medical.controller.admin.constitution;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.*;
import cn.iocoder.yudao.module.medical.service.constitution.ConstitutionQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 体质评估问题")
@RestController
@RequestMapping("/medical/constitution-question")
@Validated
public class ConstitutionQuestionController {

    @Resource
    private ConstitutionQuestionService questionService;

    @PostMapping("/create")
    @Operation(summary = "创建体质评估问题")
    @PreAuthorize("@ss.hasPermission('medical:constitution-question:create')")
    public CommonResult<Long> createQuestion(@Valid @RequestBody ConstitutionQuestionCreateReqVO createReqVO) {
        return success(questionService.createQuestion(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新体质评估问题")
    @PreAuthorize("@ss.hasPermission('medical:constitution:question:update')")
    public CommonResult<Boolean> updateQuestion(@Valid @RequestBody ConstitutionQuestionUpdateReqVO updateReqVO) {
        questionService.updateQuestion(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除体质评估问题")
    @Parameter(name = "id", description = "问题ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('medical:constitution:question:delete')")
    public CommonResult<Boolean> deleteQuestion(@RequestParam("id") Long id) {
        questionService.deleteQuestion(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取体质评估问题")
    @Parameter(name = "id", description = "问题ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('medical:constitution:question:query')")
    public CommonResult<ConstitutionQuestionRespVO> getQuestion(@RequestParam("id") Long id) {
        return success(questionService.getQuestion(id));
    }

    @GetMapping("/list")
    @Operation(summary = "获取体质评估问题列表")
    @PreAuthorize("@ss.hasPermission('medical:constitution:question:query')")
    public CommonResult<List<ConstitutionQuestionRespVO>> getQuestionList(
            @Valid ConstitutionQuestionListReqVO listReqVO) {
        return success(questionService.getQuestionList(listReqVO));
    }

    @PutMapping("/update-sort")
    @Operation(summary = "更新问题排序")
    @PreAuthorize("@ss.hasPermission('medical:constitution:question:update')")
    public CommonResult<Boolean> updateQuestionSort(
            @Valid @RequestBody ConstitutionQuestionUpdateSortReqVO reqVO) {
        questionService.updateQuestionSort(reqVO);
        return success(true);
    }
}