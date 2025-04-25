package cn.iocoder.yudao.module.medical.controller.admin.aiprompt.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI提示词模板 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AiPromptRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "26269")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("模板名称")
    private String name;

    @Schema(description = "模板描述", example = "随便")
    @ExcelProperty("模板描述")
    private String description;

    @Schema(description = "提示词文本", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("提示词文本")
    private String promptText;

    @Schema(description = "类别:1诊断 2治疗 3健康管理", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("类别:1诊断 2治疗 3健康管理")
    private Integer category;

    @Schema(description = "参数定义(JSON)")
    @ExcelProperty("参数定义(JSON)")
    private String parameters;

    @Schema(description = "状态:0禁用 1启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("状态:0禁用 1启用")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}