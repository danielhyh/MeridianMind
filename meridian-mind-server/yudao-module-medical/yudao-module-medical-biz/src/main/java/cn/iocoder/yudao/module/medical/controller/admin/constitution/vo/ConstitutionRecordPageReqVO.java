package cn.iocoder.yudao.module.medical.controller.admin.constitution.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "管理后台 - 体质评估记录分页查询 Request VO")
public class ConstitutionRecordPageReqVO extends PageParam {

    @Schema(description = "用户ID", example = "1024")
    private Long userId;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

    @Schema(description = "问卷ID", example = "1024")
    private Long questionnaireId;

    @Schema(description = "主要体质类型", example = "qi_deficiency")
    private String primaryType;

    @Schema(description = "开始创建时间")
    private LocalDateTime beginCreateTime;

    @Schema(description = "结束创建时间")
    private LocalDateTime endCreateTime;
}