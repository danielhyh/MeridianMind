package cn.iocoder.yudao.module.medical.controller.admin.constitution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "管理后台 - 体质评估问题更新 Request VO")
public class ConstitutionQuestionUpdateSortReqVO {

    @Schema(description = "问题ID", required = true, example = "1024")
    @NotNull(message = "问题ID不能为空")
    private Long id;

    @Schema(description = "排序号", required = true, example = "1")
    @NotNull(message = "排序号不能为空")
    private Integer sort;
}