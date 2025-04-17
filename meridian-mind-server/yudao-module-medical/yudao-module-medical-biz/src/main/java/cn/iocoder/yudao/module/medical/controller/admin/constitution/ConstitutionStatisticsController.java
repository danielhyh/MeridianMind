package cn.iocoder.yudao.module.medical.controller.admin.constitution;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.medical.controller.admin.constitution.vo.*;
import cn.iocoder.yudao.module.medical.service.constitution.ConstitutionStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 体质评估统计")
@RestController
@RequestMapping("/medical/constitution-statistics")
@Validated
public class ConstitutionStatisticsController {

    @Resource
    private ConstitutionStatisticsService statisticsService;

    @GetMapping("/dashboard-card")
    @Operation(summary = "获取首页卡片统计数据")
    @PreAuthorize("@ss.hasPermission('medical:constitution:statistics:query')")
    public CommonResult<ConstitutionDashboardCardRespVO> getDashboardCardData() {
        return success(statisticsService.getDashboardCardData());
    }

    @GetMapping("/radar")
    @Operation(summary = "获取体质评估雷达图数据")
    public CommonResult<ConstitutionRadarRespVO> getRadarData(
            @RequestParam(value = "recordId", required = false) Long recordId) {
        return success(statisticsService.getRadarData(getLoginUserId(), recordId));
    }

    @GetMapping("/history-radar")
    @Operation(summary = "获取历史体质评估雷达图数据")
    public CommonResult<List<ConstitutionRadarRespVO>> getHistoryRadarData() {
        return success(statisticsService.getHistoryRadarData(getLoginUserId()));
    }

    @GetMapping("/constitution-distribution")
    @Operation(summary = "获取体质分布统计")
    @PreAuthorize("@ss.hasPermission('medical:constitution-statistics:query')")
    public CommonResult<ConstitutionDistributionRespVO> getConstitutionDistribution() {
        return success(statisticsService.getConstitutionDistribution());
    }

    @GetMapping("/age-distribution")
    @Operation(summary = "获取年龄段体质分布统计")
    @PreAuthorize("@ss.hasPermission('medical:constitution-statistics:query')")
    public CommonResult<AgeConstitutionDistributionRespVO> getAgeConstitutionDistribution() {
        return success(statisticsService.getAgeConstitutionDistribution());
    }

    @GetMapping("/gender-distribution")
    @Operation(summary = "获取性别体质分布统计")
    @PreAuthorize("@ss.hasPermission('medical:constitution-statistics:query')")
    public CommonResult<GenderConstitutionDistributionRespVO> getGenderConstitutionDistribution() {
        return success(statisticsService.getGenderConstitutionDistribution());
    }

    @GetMapping("/month-trend")
    @Operation(summary = "获取月度评估趋势统计")
    @PreAuthorize("@ss.hasPermission('medical:constitution-statistics:query')")
    public CommonResult<MonthlyAssessmentTrendRespVO> getMonthlyAssessmentTrend() {
        return success(statisticsService.getMonthlyAssessmentTrend());
    }
}