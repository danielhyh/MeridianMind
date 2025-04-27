package cn.iocoder.yudao.module.ai.controller.admin.prompttemplate;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.ai.controller.admin.prompttemplate.vo.PromptTemplatePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.prompttemplate.vo.PromptTemplateRenderReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.prompttemplate.vo.PromptTemplateRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.prompttemplate.vo.PromptTemplateSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.prompttemplate.PromptTemplateDO;
import cn.iocoder.yudao.module.ai.service.prompttemplate.PromptTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI提示词模板")
@RestController
@RequestMapping("/ai/prompt-template")
@Validated
public class PromptTemplateController {

    @Resource
    private PromptTemplateService promptTemplateService;

    @PostMapping("/create")
    @Operation(summary = "创建AI提示词模板")
    @PreAuthorize("@ss.hasPermission('ai:prompt-template:create')")
    public CommonResult<Long> createPromptTemplate(@Valid @RequestBody PromptTemplateSaveReqVO createReqVO) {
        return success(promptTemplateService.createPromptTemplate(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新AI提示词模板")
    @PreAuthorize("@ss.hasPermission('ai:prompt-template:update')")
    public CommonResult<Boolean> updatePromptTemplate(@Valid @RequestBody PromptTemplateSaveReqVO updateReqVO) {
        promptTemplateService.updatePromptTemplate(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除AI提示词模板")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:prompt-template:delete')")
    public CommonResult<Boolean> deletePromptTemplate(@RequestParam("id") Long id) {
        promptTemplateService.deletePromptTemplate(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得AI提示词模板")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ai:prompt-template:query')")
    public CommonResult<PromptTemplateRespVO> getPromptTemplate(@RequestParam("id") Long id) {
        PromptTemplateDO promptTemplate = promptTemplateService.getPromptTemplate(id);
        return success(BeanUtils.toBean(promptTemplate, PromptTemplateRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得AI提示词模板分页")
    @PreAuthorize("@ss.hasPermission('ai:prompt-template:query')")
    public CommonResult<PageResult<PromptTemplateRespVO>> getPromptTemplatePage(@Valid PromptTemplatePageReqVO pageReqVO) {
        PageResult<PromptTemplateDO> pageResult = promptTemplateService.getPromptTemplatePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PromptTemplateRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出AI提示词模板 Excel")
    @PreAuthorize("@ss.hasPermission('ai:prompt-template:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPromptTemplateExcel(@Valid PromptTemplatePageReqVO pageReqVO,
                                          HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PromptTemplateDO> list = promptTemplateService.getPromptTemplatePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "AI提示词模板.xls", "数据", PromptTemplateRespVO.class,
                BeanUtils.toBean(list, PromptTemplateRespVO.class));
    }

    @PostMapping("/render")
    @Operation(summary = "渲染提示词模板")
    @PreAuthorize("@ss.hasPermission('ai:prompt-template:query')")
    public CommonResult<String> renderPromptTemplate(@Valid @RequestBody PromptTemplateRenderReqVO renderReqVO) {
        String result = promptTemplateService.renderPromptTemplate(renderReqVO.getTemplateId(), renderReqVO.getParameters());
        return success(result);
    }

}