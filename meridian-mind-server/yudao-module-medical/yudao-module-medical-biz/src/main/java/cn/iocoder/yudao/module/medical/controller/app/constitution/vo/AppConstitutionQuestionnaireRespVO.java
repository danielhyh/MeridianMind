package cn.iocoder.yudao.module.medical.controller.app.constitution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "用户 APP - 体质评估问卷 Response VO")
@Data
public class AppConstitutionQuestionnaireRespVO {

    @Schema(description = "问卷ID", required = true, example = "1024")
    private Long id;

    @Schema(description = "问卷标题", required = true, example = "中医体质辨识问卷")
    private String title;

    @Schema(description = "问卷描述", example = "根据《中医体质分类与判定》标准")
    private String description;

    @Schema(description = "问卷版本", required = true, example = "1.0")
    private String version;

    @Schema(description = "步骤数量", required = true, example = "4")
    private Integer stepCount;

    @Schema(description = "问题列表，按步骤分组")
    private List<StepQuestions> steps;

    @Data
    @Schema(description = "步骤问题")
    public static class StepQuestions {

        @Schema(description = "步骤序号", required = true, example = "1")
        private String step;

        @Schema(description = "问题列表", required = true)
        private List<Question> questions;
    }

    @Schema(description = "问题信息")
    @Data
    public static class Question {

        @Schema(description = "问题ID", required = true, example = "1")
        private Long id;
        @Schema(description = "步骤序号", required = true, example = "1")
        private String step;

        @Schema(description = "问题内容", required = true, example = "您是否容易疲劳？")
        private String question;

        @Schema(description = "问题类型：1单选 2多选", required = true, example = "1")
        private Integer questionType;

        @Schema(description = "选项列表", required = true)
        private List<Option> options;

        @Schema(description = "排序号", required = true, example = "1")
        private Integer sort;
    }

    @Schema(description = "选项信息")
    @Data
    public static class Option {

        @Schema(description = "选项标签", required = true, example = "没有")
        private String label;

        @Schema(description = "选项值", required = true, example = "A")
        private String value;
    }
}