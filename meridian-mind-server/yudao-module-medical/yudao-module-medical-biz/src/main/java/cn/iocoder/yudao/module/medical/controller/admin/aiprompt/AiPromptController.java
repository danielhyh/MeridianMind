package cn.iocoder.yudao.module.medical.controller.admin.aiprompt;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.medical.controller.admin.aiprompt.vo.AiPromptPageReqVO;
import cn.iocoder.yudao.module.medical.controller.admin.aiprompt.vo.AiPromptRespVO;
import cn.iocoder.yudao.module.medical.controller.admin.aiprompt.vo.AiPromptSaveReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.aiprompt.AiPromptDO;
import cn.iocoder.yudao.module.medical.service.aiprompt.AiPromptService;
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
@RequestMapping("/medical/ai-prompt")
@Validated
public class AiPromptController {

    @Resource
    private AiPromptService aiPromptService;

    @PostMapping("/create")
    @Operation(summary = "创建AI提示词模板")
    @PreAuthorize("@ss.hasPermission('medical:ai-prompt:create')")
    public CommonResult<Long> createAiPrompt(@Valid @RequestBody AiPromptSaveReqVO createReqVO) {
        return success(aiPromptService.createAiPrompt(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新AI提示词模板")
    @PreAuthorize("@ss.hasPermission('medical:ai-prompt:update')")
    public CommonResult<Boolean> updateAiPrompt(@Valid @RequestBody AiPromptSaveReqVO updateReqVO) {
        aiPromptService.updateAiPrompt(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除AI提示词模板")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('medical:ai-prompt:delete')")
    public CommonResult<Boolean> deleteAiPrompt(@RequestParam("id") Long id) {
        aiPromptService.deleteAiPrompt(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得AI提示词模板")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('medical:ai-prompt:query')")
    public CommonResult<AiPromptRespVO> getAiPrompt(@RequestParam("id") Long id) {
        AiPromptDO aiPrompt = aiPromptService.getAiPrompt(id);
        return success(BeanUtils.toBean(aiPrompt, AiPromptRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得AI提示词模板分页")
    @PreAuthorize("@ss.hasPermission('medical:ai-prompt:query')")
    public CommonResult<PageResult<AiPromptRespVO>> getAiPromptPage(@Valid AiPromptPageReqVO pageReqVO) {
        PageResult<AiPromptDO> pageResult = aiPromptService.getAiPromptPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiPromptRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出AI提示词模板 Excel")
    @PreAuthorize("@ss.hasPermission('medical:ai-prompt:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportAiPromptExcel(@Valid AiPromptPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AiPromptDO> list = aiPromptService.getAiPromptPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "AI提示词模板.xls", "数据", AiPromptRespVO.class,
                        BeanUtils.toBean(list, AiPromptRespVO.class));
    }

}