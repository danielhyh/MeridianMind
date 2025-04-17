package cn.iocoder.yudao.module.medical.controller.admin.constitution.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "管理后台 - 体质评估记录 Response VO")
public class ConstitutionRecordRespVO {

    @Schema(description = "记录ID", required = true, example = "1024")
    private Long id;

    @Schema(description = "用户ID", required = true, example = "1024")
    private Long userId;
    
    @Schema(description = "用户昵称", required = true, example = "张三")
    private String nickname;

    @Schema(description = "问卷ID", required = true, example = "1024")
    private Long questionnaireId;
    
    @Schema(description = "问卷标题", required = true, example = "中医体质评估问卷")
    private String questionnaireTitle;

    @Schema(description = "主要体质类型", required = true, example = "qi_deficiency")
    private String primaryType;
    
    @Schema(description = "主要体质类型名称", required = true, example = "气虚质")
    private String primaryTypeName;

    @Schema(description = "是否完成评估", required = true, example = "true")
    private Boolean isCompleted;

    @Schema(description = "是否应用到用户档案", required = true, example = "true")
    private Boolean isApplied;

    @Schema(description = "体质描述", required = true)
    private String description;

    @Schema(description = "调理建议", required = true)
    private String advice;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    @Schema(description = "次要体质类型列表", required = true)
    private List<String> secondaryTypes;
    
    @Schema(description = "次要体质类型名称列表", required = true)
    private List<String> secondaryTypeNames;

    @Schema(description = "各体质得分", required = true)
    private Map<String, Integer> scores;

}