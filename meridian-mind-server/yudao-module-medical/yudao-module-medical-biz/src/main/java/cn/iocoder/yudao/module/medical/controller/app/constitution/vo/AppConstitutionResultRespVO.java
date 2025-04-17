package cn.iocoder.yudao.module.medical.controller.app.constitution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "用户 APP - 体质评估结果 Response VO")
@Data
public class AppConstitutionResultRespVO {

    @Schema(description = "评估ID", required = true, example = "1024")
    private Long id;

    @Schema(description = "问卷ID", required = true, example = "1024")
    private Long questionnaireId;

    @Schema(description = "主要体质类型", required = true, example = "qi_deficiency")
    private String primaryType;

    @Schema(description = "主要体质类型名称", required = true, example = "气虚质")
    private String primaryTypeName;

    @Schema(description = "次要体质类型列表", required = true, example = "[\"yang_deficiency\"]")
    private List<String> secondaryTypes;

    @Schema(description = "次要体质类型名称列表", required = true, example = "[\"阳虚质\"]")
    private List<String> secondaryTypeNames;

    @Schema(description = "各体质得分", required = true, example = "{\"qi_deficiency\": 30}")
    private Map<String, Integer> scores;

    @Schema(description = "评估时间", required = true)
    private LocalDateTime createTime;

    @Schema(description = "体质描述", required = true)
    private String description;

    @Schema(description = "调理建议", required = true)
    private String advice;
}