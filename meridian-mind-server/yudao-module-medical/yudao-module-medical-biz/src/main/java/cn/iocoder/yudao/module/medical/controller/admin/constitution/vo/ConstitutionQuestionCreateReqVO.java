package cn.iocoder.yudao.module.medical.controller.admin.constitution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "管理后台 - 体质评估问题创建 Request VO")
public class ConstitutionQuestionCreateReqVO {

    @Schema(description = "问卷ID", required = true, example = "1024")
    @NotNull(message = "问卷ID不能为空")
    private Long questionnaireId;

    @Schema(description = "问题内容", required = true, example = "您是否容易疲劳？")
    @NotEmpty(message = "问题内容不能为空")
    private String question;

    @Schema(description = "问题类型（1-单选 2-多选）", required = true, example = "1")
    @NotNull(message = "问题类型不能为空")
    private Integer questionType;

    @Schema(description = "排序号", required = true, example = "1")
    @NotNull(message = "排序号不能为空")
    private Integer sort;

    @Schema(description = "关联体质类型", required = true, example = "qi_deficiency")
    @NotEmpty(message = "体质类型不能为空")
    private String constitutionType;
    @Schema(description = "所属步骤", required = true, example = "1")
    private String step;

    @Schema(description = "选项内容及分值", required = true)
    @NotEmpty(message = "选项不能为空")
    private List<QuestionOptionVO> options;

    @Schema(description = "选项信息")
    @Data
    public static class QuestionOptionVO {
        
        @Schema(description = "选项标签", required = true, example = "没有")
        @NotEmpty(message = "选项标签不能为空")
        private String label;
        
        @Schema(description = "选项值", required = true, example = "A")
        @NotEmpty(message = "选项值不能为空")
        private String value;
        
        @Schema(description = "选项分值", required = true, example = "1")
        @NotNull(message = "选项分值不能为空")
        private Integer score;
    }
}