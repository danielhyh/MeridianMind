package cn.iocoder.yudao.module.medical.controller.admin.constitution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "管理后台 - 体质评估统计卡片指标 Response VO")
public class ConstitutionDashboardCardRespVO {

    @Schema(description = "今日评估数据")
    private DailyAssessment dailyAssessment;

    @Schema(description = "本周评估数据")
    private WeeklyAssessment weeklyAssessment;

    @Schema(description = "体质分布数据")
    private ConstitutionDistribution constitutionDistribution;

    @Schema(description = "评估完成率数据")
    private CompletionRate completionRate;

    @Data
    @Schema(description = "今日评估数据")
    public static class DailyAssessment {

        @Schema(description = "今日评估人数", required = true, example = "42")
        private Integer count;

        @Schema(description = "较昨日增长率", required = true, example = "12.0")
        private BigDecimal growthRate;
    }

    @Data
    @Schema(description = "本周评估数据")
    public static class WeeklyAssessment {

        @Schema(description = "本周评估人数", required = true, example = "187")
        private Integer count;

        @Schema(description = "较上周增长率", required = true, example = "8.0")
        private BigDecimal growthRate;
    }

    @Data
    @Schema(description = "体质分布数据")
    public static class ConstitutionDistribution {

        @Schema(description = "TOP体质类型", required = true)
        private List<ConstitutionTypeData> topTypes;

        @Data
        @Schema(description = "体质类型数据")
        public static class ConstitutionTypeData {

            @Schema(description = "体质类型", required = true, example = "qi_deficiency")
            private String type;

            @Schema(description = "体质类型名称", required = true, example = "气虚质")
            private String name;

            @Schema(description = "百分比", required = true, example = "23.0")
            private BigDecimal percentage;
        }
    }

    @Data
    @Schema(description = "评估完成率数据")
    public static class CompletionRate {

        @Schema(description = "评估完成率", required = true, example = "94.6")
        private BigDecimal rate;

        @Schema(description = "较上月增长率", required = true, example = "2.1")
        private BigDecimal growthRate;
    }
}