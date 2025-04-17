package cn.iocoder.yudao.module.medical.controller.admin.constitution.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "管理后台 - 体质评估问卷分页查询 Request VO")
public class ConstitutionQuestionnairePageReqVO extends PageParam {

    @Schema(description = "问卷标题", example = "中医体质评估问卷")
    private String title;

    @Schema(description = "问卷状态（0-停用 1-启用）", example = "1")
    private Integer status;

    @Schema(description = "问卷版本", example = "1.0")
    private String version;

    @Schema(description = "开始创建时间")
    private LocalDateTime beginCreateTime;

    @Schema(description = "结束创建时间")
    private LocalDateTime endCreateTime;
}