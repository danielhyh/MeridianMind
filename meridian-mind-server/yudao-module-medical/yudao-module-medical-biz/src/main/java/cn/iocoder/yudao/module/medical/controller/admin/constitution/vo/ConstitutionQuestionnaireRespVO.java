package cn.iocoder.yudao.module.medical.controller.admin.constitution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "管理后台 - 体质评估问卷 Response VO")
public class ConstitutionQuestionnaireRespVO {

    @Schema(description = "问卷ID", required = true, example = "1024")
    private Long id;

    @Schema(description = "问卷标题", required = true, example = "中医体质评估问卷")
    private String title;

    @Schema(description = "问卷描述", example = "根据《中医体质分类与判定》标准")
    private String description;

    @Schema(description = "问卷版本", required = true, example = "1.0")
    private String version;

    @Schema(description = "问卷状态（0-停用 1-启用）", required = true, example = "1")
    private Integer status;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", required = true)
    private LocalDateTime updateTime;

    @Schema(description = "创建人", required = true, example = "admin")
    private String creator;

    @Schema(description = "题目数量", example = "30")
    private Integer questionCount;
}