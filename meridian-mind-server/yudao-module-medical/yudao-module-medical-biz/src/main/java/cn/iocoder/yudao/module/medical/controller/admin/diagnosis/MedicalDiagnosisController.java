package cn.iocoder.yudao.module.medical.controller.admin.diagnosis;

import cn.iocoder.yudao.module.ai.service.chat.AiChatMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 医疗智能问诊 Controller
 */
@Tag(name = "管理后台 - 医疗智能问诊")
@RestController
@RequestMapping("/medical/diagnosis")
@Slf4j
public class MedicalDiagnosisController {

    @Resource
    private AiChatMessageService aiChatMessageService;

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