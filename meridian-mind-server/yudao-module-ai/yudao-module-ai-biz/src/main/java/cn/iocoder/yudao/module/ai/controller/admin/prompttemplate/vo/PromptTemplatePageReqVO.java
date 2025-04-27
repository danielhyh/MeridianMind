package cn.iocoder.yudao.module.ai.controller.admin.prompttemplate.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - AI提示词模板分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PromptTemplatePageReqVO extends PageParam {

    @Schema(description = "模板名称", example = "李四")
    private String name;

    @Schema(description = "模板描述", example = "你猜")
    private String description;

    @Schema(description = "提示词文本")
    private String content;

    @Schema(description = "参数定义(JSON)")
    private String parameters;

    @Schema(description = "领域类型")
    private Integer domain;

    @Schema(description = "状态:0禁用 1启用", example = "1")
    private Integer status;

    @Schema(description = "默认模型编号", example = "518")
    private Long defaultModelId;

    @Schema(description = "默认知识库编号", example = "18915")
    private Long defaultKnowledgeId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}