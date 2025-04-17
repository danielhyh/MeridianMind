package cn.iocoder.yudao.module.medical.controller.admin.constitution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "管理后台 - 年龄段体质分布统计 Response VO")
public class AgeConstitutionDistributionRespVO {

    @Schema(description = "统计时间", required = true)
    private LocalDateTime statisticTime;

    @Schema(description = "年龄段分布数据", required = true)
    private List<AgeRangeData> data;

    @Data
    @Schema(description = "年龄段数据")
    public static class AgeRangeData {

        @Schema(description = "年龄段", required = true, example = "19-30")
        private String ageRange;

        @Schema(description = "体质分布数据", required = true)
        private List<ConstitutionData> data;
    }

    @Data
    @Schema(description = "体质数据")
    public static class ConstitutionData {

        @Schema(description = "体质类型", required = true, example = "qi_deficiency")
        private String type;

        @Schema(description = "体质类型名称", required = true, example = "气虚质")
        private String name;

        @Schema(description = "数量", required = true, example = "100")
        private Integer count;
    }
}