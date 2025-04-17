package cn.iocoder.yudao.module.medical.controller.admin.constitution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "管理后台 - 体质评估记录详情 Response VO")
public class ConstitutionRecordDetailRespVO extends ConstitutionRecordRespVO {

    @Schema(description = "答题记录")
    private Map<Long, String> answers;
    
    @Schema(description = "步骤答题详情")
    private List<StepAnswers> stepAnswers;
    @Data
    public static class StepAnswers {
        @Schema(description = "问题所属步骤", required = true, example = "1")
        private String step;

        @Schema(description = "答题详情列表")
        private List<AnswerDetail> answerDetails;
    }
    @Data
    public static class AnswerDetail {
        @Schema(description = "问题所属步骤", required = true, example = "1")
        private String step;
        @Schema(description = "问题ID", required = true, example = "1024")
        private Long questionId;
        
        @Schema(description = "问题内容", required = true, example = "您是否容易疲劳？")
        private String question;
        
        @Schema(description = "用户答案", required = true, example = "A")
        private String answer;
        
        @Schema(description = "答案文本", required = true, example = "没有")
        private String answerLabel;
        
        @Schema(description = "分值", required = true, example = "1")
        private Integer score;
        
        @Schema(description = "关联体质类型", required = true, example = "qi_deficiency")
        private String constitutionType;
        
        @Schema(description = "关联体质类型名称", required = true, example = "气虚质")
        private String constitutionTypeName;
    }
}