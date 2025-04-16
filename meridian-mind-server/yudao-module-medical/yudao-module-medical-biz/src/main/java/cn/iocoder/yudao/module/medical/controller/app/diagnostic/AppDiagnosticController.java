package cn.iocoder.yudao.module.medical.controller.app.diagnostic;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.medical.controller.app.diagnostic.vo.AppDiagnosticPageReqVO;
import cn.iocoder.yudao.module.medical.controller.app.diagnostic.vo.AppDiagnosticRespVO;
import cn.iocoder.yudao.module.medical.controller.app.diagnostic.vo.AppDiagnosticSaveReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnostic.DiagnosticDO;
import cn.iocoder.yudao.module.medical.service.diagnostic.DiagnosticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 医疗智能问诊 Controller
 */
@Tag(name = "管理后台 - 问诊记录")
@RestController
@RequestMapping("/medical/diagnostic")
@Validated
public class AppDiagnosticController {
    @Resource
    private DiagnosticService diagnosticService;

    @PostMapping("/create")
    @Operation(summary = "创建问诊记录")
    public CommonResult<Long> createDiagnostic(@Valid @RequestBody AppDiagnosticSaveReqVO createReqVO) {
        return success(diagnosticService.createDiagnostic(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新问诊记录")
    @PreAuthorize("@ss.hasPermission('medical:diagnostic:update')")
    public CommonResult<Boolean> updateDiagnostic(@Valid @RequestBody AppDiagnosticSaveReqVO updateReqVO) {
        diagnosticService.updateDiagnostic(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除问诊记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('medical:diagnostic:delete')")
    public CommonResult<Boolean> deleteDiagnostic(@RequestParam("id") Long id) {
        diagnosticService.deleteDiagnostic(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得问诊记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('medical:diagnostic:query')")
    public CommonResult<AppDiagnosticRespVO> getDiagnostic(@RequestParam("id") Long id) {
        DiagnosticDO diagnostic = diagnosticService.getDiagnostic(id);
        return success(BeanUtils.toBean(diagnostic, AppDiagnosticRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得问诊记录分页")
    @PreAuthorize("@ss.hasPermission('medical:diagnostic:query')")
    public CommonResult<PageResult<AppDiagnosticRespVO>> getDiagnosticPage(@Valid AppDiagnosticPageReqVO pageReqVO) {
        PageResult<DiagnosticDO> pageResult = diagnosticService.getDiagnosticPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AppDiagnosticRespVO.class));
    }

//    @GetMapping("/export-excel")
//    @Operation(summary = "导出问诊记录 Excel")
//    @PreAuthorize("@ss.hasPermission('medical:diagnostic:export')")
//    @ApiAccessLog(operateType = EXPORT)
//    public void exportDiagnosticExcel(@Valid AppDiagnosticPageReqVO pageReqVO,
//                                      HttpServletResponse response) throws IOException {
//        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
//        List<DiagnosticDO> list = diagnosticService.getDiagnosticPage(pageReqVO).getList();
//        // 导出 Excel
//        ExcelUtils.write(response, "问诊记录.xls", "数据", AppDiagnosticRespVO.class,
//                BeanUtils.toBean(list, AppDiagnosticRespVO.class));
//    }
//    @Operation(summary = "提交智能问诊请求（流式）")
//    @PostMapping(value = "/send-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    @PreAuthorize("@ss.hasPermission('medical:diagnosis:query')")
//    public Flux<CommonResult<AiChatResponse>> sendDiagnosisStream(@RequestBody AiChatRequest request) {
//        // 目前直接复用AI模块的聊天功能，实现智能问诊
//        // TODO: 后续扩展为专门的医疗问诊功能，包括添加患者信息、保存诊断记录等
//        return aiChatMessageService.sendChatStream(request, getLoginUserId())
//                .map(response -> success(response));
//    }
//
//    @Operation(summary = "提交智能问诊请求")
//    @PostMapping("/send")
//    @PreAuthorize("@ss.hasPermission('medical:diagnosis:query')")
//    public CommonResult<AiChatResponse> sendDiagnosis(@RequestBody AiChatRequest request) {
//        // 目前直接复用AI模块的聊天功能，实现智能问诊
//        // TODO: 后续扩展为专门的医疗问诊功能，包括添加患者信息、保存诊断记录等
//        return success(aiChatMessageService.sendChat(request, getLoginUserId()));
//    }

} 