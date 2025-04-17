package cn.iocoder.yudao.module.medical.controller.app.constitution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Schema(description = "用户 APP - 体质评估问卷提交 Request VO")
@Data
public class AppConstitutionSubmitReqVO {

    @Schema(description = "评估记录ID（如果是继续已有评估）", example = "1024")
    private Long recordId;

    @Schema(description = "问卷ID", required = true, example = "1024")
    @NotNull(message = "问卷ID不能为空")
    private Long questionnaireId;

    @Schema(description = "答题记录，格式：{\"问题ID\": \"选项值\", ...}", required = true)
    @NotEmpty(message = "答题记录不能为空")
    private Map<Long, String> answers;
}