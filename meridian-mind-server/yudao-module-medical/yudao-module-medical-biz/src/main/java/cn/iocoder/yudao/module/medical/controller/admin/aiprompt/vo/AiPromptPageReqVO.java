package cn.iocoder.yudao.module.medical.controller.admin.aiprompt.vo;

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
public class AiPromptPageReqVO extends PageParam {

    @Schema(description = "模板名称", example = "芋艿")
    private String name;

    @Schema(description = "模板描述", example = "随便")
    private String description;

    @Schema(description = "提示词文本")
    private String promptText;

    @Schema(description = "类别:1诊断 2治疗 3健康管理")
    private Integer category;

    @Schema(description = "参数定义(JSON)")
    private String parameters;

    @Schema(description = "状态:0禁用 1启用", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}