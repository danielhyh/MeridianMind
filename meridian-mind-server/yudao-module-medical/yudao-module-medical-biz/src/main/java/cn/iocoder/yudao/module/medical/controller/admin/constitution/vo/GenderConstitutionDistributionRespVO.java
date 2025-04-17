package cn.iocoder.yudao.module.medical.controller.admin.constitution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "管理后台 - 性别体质分布统计 Response VO")
public class GenderConstitutionDistributionRespVO {

    @Schema(description = "统计时间", required = true)
    private LocalDateTime statisticTime;
    
    @Schema(description = "性别分布数据", required = true)
    private List<GenderData> data;

    @Data
    @Schema(description = "性别数据")
    public static class GenderData {

        @Schema(description = "性别", required = true, example = "1")
        private Integer gender;

        @Schema(description = "性别名称", required = true, example = "男")
        private String genderName;

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