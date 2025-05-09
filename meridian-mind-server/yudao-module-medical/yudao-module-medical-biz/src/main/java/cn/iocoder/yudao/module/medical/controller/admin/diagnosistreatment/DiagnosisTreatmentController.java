package cn.iocoder.yudao.module.medical.controller.admin.diagnosistreatment;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosistreatment.vo.DiagnosisTreatmentPageReqVO;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosistreatment.vo.DiagnosisTreatmentRespVO;
import cn.iocoder.yudao.module.medical.controller.admin.diagnosistreatment.vo.DiagnosisTreatmentSaveReqVO;
import cn.iocoder.yudao.module.medical.dal.dataobject.diagnosistreatment.DiagnosisTreatmentDO;
import cn.iocoder.yudao.module.medical.service.diagnosistreatment.DiagnosisTreatmentService;
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

@Tag(name = "管理后台 - 中医诊断治疗一体化")
@RestController
@RequestMapping("/medical/diagnosis-treatment")
@Validated
public class DiagnosisTreatmentController {

    @Resource
    private DiagnosisTreatmentService diagnosisTreatmentService;

    @PostMapping("/create")
    @Operation(summary = "创建中医诊断治疗一体化")
    @PreAuthorize("@ss.hasPermission('medical:diagnosis-treatment:create')")
    public CommonResult<Long> createDiagnosisTreatment(@Valid @RequestBody DiagnosisTreatmentSaveReqVO createReqVO) {
        return success(diagnosisTreatmentService.createDiagnosisTreatment(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新中医诊断治疗一体化")
    @PreAuthorize("@ss.hasPermission('medical:diagnosis-treatment:update')")
    public CommonResult<Boolean> updateDiagnosisTreatment(@Valid @RequestBody DiagnosisTreatmentSaveReqVO updateReqVO) {
        diagnosisTreatmentService.updateDiagnosisTreatment(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除中医诊断治疗一体化")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('medical:diagnosis-treatment:delete')")
    public CommonResult<Boolean> deleteDiagnosisTreatment(@RequestParam("id") Long id) {
        diagnosisTreatmentService.deleteDiagnosisTreatment(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得中医诊断治疗一体化")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('medical:diagnosis-treatment:query')")
    public CommonResult<DiagnosisTreatmentRespVO> getDiagnosisTreatment(@RequestParam("id") Long id) {
        DiagnosisTreatmentDO diagnosisTreatment = diagnosisTreatmentService.getDiagnosisTreatment(id);
        return success(BeanUtils.toBean(diagnosisTreatment, DiagnosisTreatmentRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得中医诊断治疗一体化分页")
    @PreAuthorize("@ss.hasPermission('medical:diagnosis-treatment:query')")
    public CommonResult<PageResult<DiagnosisTreatmentRespVO>> getDiagnosisTreatmentPage(@Valid DiagnosisTreatmentPageReqVO pageReqVO) {
        PageResult<DiagnosisTreatmentDO> pageResult = diagnosisTreatmentService.getDiagnosisTreatmentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DiagnosisTreatmentRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出中医诊断治疗一体化 Excel")
    @PreAuthorize("@ss.hasPermission('medical:diagnosis-treatment:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDiagnosisTreatmentExcel(@Valid DiagnosisTreatmentPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DiagnosisTreatmentDO> list = diagnosisTreatmentService.getDiagnosisTreatmentPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "中医诊断治疗一体化.xls", "数据", DiagnosisTreatmentRespVO.class,
                        BeanUtils.toBean(list, DiagnosisTreatmentRespVO.class));
    }

}