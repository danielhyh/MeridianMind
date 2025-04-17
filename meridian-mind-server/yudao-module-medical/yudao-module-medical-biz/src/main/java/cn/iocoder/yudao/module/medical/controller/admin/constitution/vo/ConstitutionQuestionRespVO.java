package cn.iocoder.yudao.module.medical.controller.admin.constitution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "管理后台 - 体质评估问题 Response VO")
public class ConstitutionQuestionRespVO {

    @Schema(description = "问题ID", required = true, example = "1024")
    private Long id;

    @Schema(description = "问卷ID", required = true, example = "1024")
    private Long questionnaireId;

    @Schema(description = "问题内容", required = true, example = "您是否容易疲劳？")
    private String question;

    @Schema(description = "问题类型（1-单选 2-多选）", required = true, example = "1")
    private Integer questionType;

    @Schema(description = "排序号", required = true, example = "1")
    private Integer sort;

    @Schema(description = "所属步骤", required = true, example = "1")
    private String step;

    @Schema(description = "关联体质类型", required = true, example = "qi_deficiency")
    private String constitutionType;
    
    @Schema(description = "关联体质类型名称", required = true, example = "气虚质")
    private String constitutionTypeName;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    @Schema(description = "选项内容及分值")
    private List<QuestionOptionVO> options;

    @Data
    public static class QuestionOptionVO {
        
        @Schema(description = "选项标签", required = true, example = "没有")
        private String label;
        
        @Schema(description = "选项值", required = true, example = "A")
        private String value;
        
        @Schema(description = "选项分值", required = true, example = "1")
        private Integer score;
    }
}