package cn.iocoder.yudao.module.ai.controller.admin.prompttemplate.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI提示词模板 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PromptTemplateRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "28216")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("模板名称")
    private String name;

    @Schema(description = "模板描述", example = "你猜")
    @ExcelProperty("模板描述")
    private String description;

    @Schema(description = "提示词文本", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("提示词文本")
    private String content;

    @Schema(description = "参数定义(JSON)")
    @ExcelProperty("参数定义(JSON)")
    private String parameters;

    @Schema(description = "领域类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("领域类型")
    private Integer domain;

    @Schema(description = "状态:0禁用 1启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("状态:0禁用 1启用")
    private Integer status;

    @Schema(description = "默认模型编号", example = "518")
    @ExcelProperty("默认模型编号")
    private Long defaultModelId;

    @Schema(description = "默认知识库编号", example = "18915")
    @ExcelProperty("默认知识库编号")
    private Long defaultKnowledgeId;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}