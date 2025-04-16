package cn.iocoder.yudao.module.medical.controller.admin.diagnostic;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.medical.controller.admin.diagnostic.vo.DiagnosticPageReqVO;
import cn.iocoder.yudao.module.medical.controller.admin.diagnostic.vo.DiagnosticRespVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnostic.DiagnosticDO;
import cn.iocoder.yudao.module.medical.service.diagnostic.DiagnosticService;
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

/**
 * 医疗智能问诊 Controller
 */
@Tag(name = "管理后台 - 问诊记录")
@RestController
@RequestMapping("/medical/diagnostic")
@Validated
public class DiagnosticController {
    @Resource
    private DiagnosticService diagnosticService;

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
    public CommonResult<DiagnosticRespVO> getDiagnostic(@RequestParam("id") Long id) {
        DiagnosticDO diagnostic = diagnosticService.getDiagnostic(id);
        return success(BeanUtils.toBean(diagnostic, DiagnosticRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得问诊记录分页")
    @PreAuthorize("@ss.hasPermission('medical:diagnostic:query')")
    public CommonResult<PageResult<DiagnosticRespVO>> getDiagnosticPage(@Valid DiagnosticPageReqVO pageReqVO) {
        PageResult<DiagnosticDO> pageResult = diagnosticService.getDiagnosticPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DiagnosticRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出问诊记录 Excel")
    @PreAuthorize("@ss.hasPermission('medical:diagnostic:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDiagnosticExcel(@Valid DiagnosticPageReqVO pageReqVO,
                                      HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DiagnosticDO> list = diagnosticService.getDiagnosticPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "问诊记录.xls", "数据", DiagnosticRespVO.class,
                BeanUtils.toBean(list, DiagnosticRespVO.class));
    }
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