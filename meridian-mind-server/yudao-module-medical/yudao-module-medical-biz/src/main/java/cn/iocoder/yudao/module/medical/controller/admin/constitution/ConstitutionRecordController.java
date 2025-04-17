package cn.iocoder.yudao.module.medical.controller.admin.constitution;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionRecordDetailRespVO;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionRecordPageReqVO;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.ConstitutionRecordRespVO;
import cn.iocoder.yudao.module.medical.service.constitution.ConstitutionRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 体质评估记录")
@RestController
@RequestMapping("/medical/constitution-record")
@Validated
public class ConstitutionRecordController {

    @Resource(name = "constitutionRecordService")
    private ConstitutionRecordService recordService;

    @GetMapping("/page")
    @Operation(summary = "获取体质评估记录分页")
    @PreAuthorize("@ss.hasPermission('medical:constitution-record:query')")
    public CommonResult<PageResult<ConstitutionRecordRespVO>> getRecordPage(
            @Valid ConstitutionRecordPageReqVO pageVO) {
        return success(recordService.getRecordPage(pageVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取体质评估记录详情")
    @Parameter(name = "id", description = "记录ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('medical:constitution-record:query')")
    public CommonResult<ConstitutionRecordDetailRespVO> getRecordDetail(@RequestParam("id") Long id) {
        return success(recordService.getRecordDetail(id));
    }

    @GetMapping("/get-user-latest")
    @Operation(summary = "获取用户最新的体质评估记录")
    @Parameter(name = "userId", description = "用户ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('medical:constitution-record:query')")
    public CommonResult<ConstitutionRecordRespVO> getUserLatestRecord(@RequestParam("userId") Long userId) {
        return success(recordService.getUserLatestRecord(userId));
    }
}