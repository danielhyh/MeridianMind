package cn.iocoder.yudao.module.medical.controller.admin.constitution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "管理后台 - 月度评估趋势统计 Response VO")
public class MonthlyAssessmentTrendRespVO {

    @Schema(description = "统计时间", required = true)
    private LocalDateTime statisticTime;

    @Schema(description = "月度数据", required = true)
    private List<MonthData> data;

    @Data
    @Schema(description = "月度数据")
    public static class MonthData {

        @Schema(description = "月份", required = true, example = "2025-01")
        private String month;

        @Schema(description = "评估数量", required = true, example = "100")
        private Integer count;
    }
}