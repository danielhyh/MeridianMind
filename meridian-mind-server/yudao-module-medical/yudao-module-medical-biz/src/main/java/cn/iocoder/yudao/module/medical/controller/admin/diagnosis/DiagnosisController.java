package cn.iocoder.yudao.module.medical.controller.admin.diagnosis;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosis.vo.DiagnosisPageReqVO;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosis.vo.DiagnosisRespVO;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosis.vo.DiagnosisSaveReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnosis.DiagnosisDO;
import cn.iocoder.yudao.module.medical.service.diagnosis.DiagnosisService;
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

@Tag(name = "管理后台 - 诊断结果")
@RestController
@RequestMapping("/medical/diagnosis")
@Validated
public class DiagnosisController {

    @Resource
    private DiagnosisService diagnosisService;

    @PostMapping("/create")
    @Operation(summary = "创建诊断结果")
    @PreAuthorize("@ss.hasPermission('medical:diagnosis:create')")
    public CommonResult<Long> createDiagnosis(@Valid @RequestBody DiagnosisSaveReqVO createReqVO) {
        return success(diagnosisService.createDiagnosis(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新诊断结果")
    @PreAuthorize("@ss.hasPermission('medical:diagnosis:update')")
    public CommonResult<Boolean> updateDiagnosis(@Valid @RequestBody DiagnosisSaveReqVO updateReqVO) {
        diagnosisService.updateDiagnosis(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除诊断结果")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('medical:diagnosis:delete')")
    public CommonResult<Boolean> deleteDiagnosis(@RequestParam("id") Long id) {
        diagnosisService.deleteDiagnosis(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得诊断结果")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('medical:diagnosis:query')")
    public CommonResult<DiagnosisRespVO> getDiagnosis(@RequestParam("id") Long id) {
        DiagnosisDO diagnosis = diagnosisService.getDiagnosis(id);
        return success(BeanUtils.toBean(diagnosis, DiagnosisRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得诊断结果分页")
    @PreAuthorize("@ss.hasPermission('medical:diagnosis:query')")
    public CommonResult<PageResult<DiagnosisRespVO>> getDiagnosisPage(@Valid DiagnosisPageReqVO pageReqVO) {
        PageResult<DiagnosisDO> pageResult = diagnosisService.getDiagnosisPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DiagnosisRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出诊断结果 Excel")
    @PreAuthorize("@ss.hasPermission('medical:diagnosis:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDiagnosisExcel(@Valid DiagnosisPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DiagnosisDO> list = diagnosisService.getDiagnosisPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "诊断结果.xls", "数据", DiagnosisRespVO.class,
                        BeanUtils.toBean(list, DiagnosisRespVO.class));
    }

}