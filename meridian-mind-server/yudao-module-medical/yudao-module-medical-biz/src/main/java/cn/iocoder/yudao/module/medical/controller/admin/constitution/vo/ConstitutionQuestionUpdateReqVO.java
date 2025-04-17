package cn.iocoder.yudao.module.medical.controller.admin.constitution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "管理后台 - 体质评估问题更新 Request VO")
public class ConstitutionQuestionUpdateReqVO {

    @Schema(description = "问题ID", required = true, example = "1024")
    @NotNull(message = "问题ID不能为空")
    private Long id;

    @Schema(description = "问题内容", required = true, example = "您是否容易疲劳？")
    @NotEmpty(message = "问题内容不能为空")
    private String question;

    @Schema(description = "问题类型（1-单选 2-多选）", required = true, example = "1")
    @NotNull(message = "问题类型不能为空")
    private Integer questionType;

    @Schema(description = "选项内容及分值", required = true)
    @NotEmpty(message = "选项不能为空")
    private List<ConstitutionQuestionCreateReqVO.QuestionOptionVO> options;

    @Schema(description = "排序号", required = true, example = "1")
    @NotNull(message = "排序号不能为空")
    private Integer sort;

    @Schema(description = "关联体质类型", required = true, example = "qi_deficiency")
    @NotEmpty(message = "体质类型不能为空")
    private String constitutionType;
}