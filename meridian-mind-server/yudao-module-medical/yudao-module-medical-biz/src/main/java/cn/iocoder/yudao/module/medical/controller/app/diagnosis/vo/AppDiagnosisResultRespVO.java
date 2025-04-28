package cn.iocoder.yudao.module.medical.controller.app.diagnosis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "用户 APP - 诊断结果 Response VO")
@Data
public class AppDiagnosisResultRespVO {

    @Schema(description = "诊断类型（主证型）", example = "阳虚证兼痰湿证")
    private String diagnosisType;

    @Schema(description = "诊断详情")
    private DiagnosisDetail diagnosisDetail;

    @Schema(description = "治疗方案")
    private TreatmentPlan treatmentPlan;

    @Schema(description = "信心指数(1-100)", example = "85")
    private Integer confidence;

    @Data
    public static class DiagnosisDetail {
        @Schema(description = "八纲辨证")
        private Map<String, String> eightPrinciples;

        @Schema(description = "脏腑辨证结果", example = "脾肾阳虚")
        private String organPattern;

        @Schema(description = "主要证型", example = "阳虚证")
        private String primarySyndrome;

        @Schema(description = "次要证型", example = "['痰湿证', '脾虚证']")
        private List<String> secondarySyndromes;

        @Schema(description = "诊断依据")
        private List<String> evidence;

        @Schema(description = "病因病机分析")
        private String analysis;
    }

    @Data
    public static class TreatmentPlan {
        @Schema(description = "治疗原则", example = "温补阳气，健脾化湿")
        private String principle;

        @Schema(description = "推荐方剂")
        private List<Formula> formulas;

        @Schema(description = "针灸建议")
        private Acupuncture acupuncture;

        @Schema(description = "生活调养建议")
        private String lifestyle;

        @Schema(description = "饮食指导")
        private String diet;
    }

    @Data
    public static class Formula {
        @Schema(description = "方剂名称", example = "苓桂术甘汤")
        private String name;

        @Schema(description = "组成", example = "茯苓15g，桂枝10g，白术12g，甘草6g")
        private String composition;

        @Schema(description = "用法", example = "水煎服，每日1剂，分2次服用")
        private String usage;

        @Schema(description = "出处", example = "《伤寒论》")
        private String reference;
    }

    @Data
    public static class Acupuncture {
        @Schema(description = "穴位", example = "['关元', '气海', '脾俞', '肾俞', '足三里', '丰隆']")
        private List<String> points;

        @Schema(description = "方法", example = "温针灸，每周2-3次，每次留针30分钟")
        private String methods;
    }
}