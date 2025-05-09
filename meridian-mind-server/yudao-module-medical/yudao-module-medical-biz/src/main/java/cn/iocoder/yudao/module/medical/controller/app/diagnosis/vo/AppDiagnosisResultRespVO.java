package cn.iocoder.yudao.module.medical.controller.app.diagnosis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "用户 APP - 诊断结果 Response VO")
@Data
@NoArgsConstructor
public class AppDiagnosisResultRespVO {

    @Schema(description = "主要证型", example = "心脾两虚证")
    private String primarySyndrome;

    @Schema(description = "主要证型详细解释")
    private String primarySyndromeExplanation;

    @Schema(description = "次要证型列表")
    private List<SecondarySyndrome> secondarySyndromes;

    @Schema(description = "八纲辨证")
    private EightPrinciples eightPrinciples;

    @Schema(description = "症状列表")
    private List<Symptom> symptoms;

    @Schema(description = "辨证分析")
    private String syndromeAnalysis;

    @Schema(description = "治疗原则", example = "['健脾益气', '养心安神']")
    private List<String> treatmentPrinciple;

    @Schema(description = "治疗原则解释")
    private String principleExplanation;

    @Schema(description = "推荐方剂")
    private List<Prescription> prescriptions;

    @Schema(description = "生活调养建议")
    private LifestyleAdvice lifestyleAdvice;

    @Schema(description = "食疗推荐")
    private List<DietRecipe> dietRecipes;

    @Schema(description = "随访管理")
    private FollowUp followUp;

    @Schema(description = "信心指数(1-100)", example = "90")
    private Integer confidence;

    @Data
    @NoArgsConstructor
    public static class SecondarySyndrome {
        @Schema(description = "证型名称", example = "气滞血瘀证")
        private String name;

        @Schema(description = "证型解释")
        private String explanation;
    }

    @Data
    @NoArgsConstructor
    public static class EightPrinciples {
        @Schema(description = "阴阳")
        private PrincipleDetail yinYang;

        @Schema(description = "表里")
        private PrincipleDetail exteriorInterior;

        @Schema(description = "寒热")
        private PrincipleDetail coldHeat;

        @Schema(description = "虚实")
        private PrincipleDetail deficiencyExcess;
    }

    @Data
    @NoArgsConstructor
    public static class PrincipleDetail {
        @Schema(description = "名称", example = "阴阳")
        private String name;

        @Schema(description = "数值", example = "30")
        private Integer value;

        @Schema(description = "结果", example = "阴证")
        private String result;

        @Schema(description = "详细解释")
        private String description;
    }

    @Data
    @NoArgsConstructor
    public static class Symptom {
        @Schema(description = "症状名称", example = "失眠")
        private String name;

        @Schema(description = "症状等级", example = "主症")
        private String level;

        @Schema(description = "病因病机分析")
        private String causeAnalysis;

        @Schema(description = "置信度", example = "90")
        private Integer confidence;
    }

    @Data
    @NoArgsConstructor
    public static class Prescription {
        @Schema(description = "方剂名称", example = "归脾汤")
        private String name;

        @Schema(description = "方剂类别", example = "补气养血剂")
        private String category;

        @Schema(description = "功效", example = "益气健脾，养血安神")
        private String effect;

        @Schema(description = "用法用量", example = "水煎服，每日1剂，分2次服用")
        private String usage;

        @Schema(description = "经典出处", example = "《济生方》")
        private String reference;

        @Schema(description = "组成药材")
        private List<Medicine> medicines;
    }

    @Data
    @NoArgsConstructor
    public static class Medicine {
        @Schema(description = "药材名称", example = "党参")
        private String name;

        @Schema(description = "剂量", example = "15")
        private String dose;

        @Schema(description = "单位", example = "g")
        private String unit;

        @Schema(description = "炮制方法", example = "蜜炙")
        private String processing;
    }

    @Data
    @NoArgsConstructor
    public static class LifestyleAdvice {
        @Schema(description = "饮食调理")
        private Diet diet;

        @Schema(description = "运动建议")
        private Exercise exercise;

        @Schema(description = "情志调摄")
        private Emotion emotion;

        @Schema(description = "穴位保健")
        private Acupoints acupoints;
    }

    @Data
    @NoArgsConstructor
    public static class Diet {
        @Schema(description = "饮食原则", example = "健脾养胃，补气养血")
        private String principles;

        @Schema(description = "推荐食物")
        private List<String> recommended;

        @Schema(description = "忌食食物")
        private List<String> avoid;

        @Schema(description = "饮食规律")
        private List<String> rules;
    }

    @Data
    @NoArgsConstructor
    public static class Exercise {
        @Schema(description = "适合运动")
        private List<String> suitable;

        @Schema(description = "运动量", example = "每天进行30-60分钟的轻度至中度运动")
        private String intensity;

        @Schema(description = "注意事项")
        private String notes;
    }

    @Data
    @NoArgsConstructor
    public static class Emotion {
        @Schema(description = "情志调摄要点", example = "保持心情舒畅，避免情绪波动")
        private String principles;

        @Schema(description = "调摄方法")
        private List<String> methods;

        @Schema(description = "作息建议", example = "保证充足睡眠，早睡早起")
        private String rest;
    }

    @Data
    @NoArgsConstructor
    public static class Acupoints {
        @Schema(description = "穴位列表")
        private List<Acupoint> points;

        @Schema(description = "总体说明", example = "取穴准确，手法适度，避免过重刺激")
        private String general;
    }

    @Data
    @NoArgsConstructor
    public static class Acupoint {
        @Schema(description = "穴位名称", example = "足三里")
        private String name;

        @Schema(description = "穴位位置", example = "外膝眼下3寸")
        private String location;

        @Schema(description = "按摩方法", example = "每日可按摩此穴位，每穴1-3分钟")
        private String method;
    }

    @Data
    @NoArgsConstructor
    public static class DietRecipe {
        @Schema(description = "食疗方名称", example = "山药莲子粥")
        private String name;

        @Schema(description = "功效", example = "健脾养胃，补气养血")
        private String effect;

        @Schema(description = "材料", example = "山药100g，莲子30g，大米50g")
        private String ingredients;

        @Schema(description = "做法", example = "将材料洗净，共同煮粥，温热食用")
        private String method;
    }

    @Data
    @NoArgsConstructor
    public static class FollowUp {
        @Schema(description = "随访时间", example = "2周后复诊")
        private String schedule;

        @Schema(description = "注意事项")
        private List<String> notes;

        @Schema(description = "健康管理建议")
        private List<String> healthManagement;
    }
}