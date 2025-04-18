package cn.iocoder.yudao.module.medical.controller.app.fourdiagnosis.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 四诊信息 Response VO")
@Data
public class AppFourDiagnosticRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "问诊记录ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long diagnosticId;

    @Schema(description = "望诊数据")
    private String inspection;

    @Schema(description = "闻诊数据")
    private String auscultation;

    @Schema(description = "问诊数据")
    private String inquiry;

    @Schema(description = "切诊数据")
    private String palpation;

    @Schema(description = "舌象图片URL")
    private String tongueImage;

    @Schema(description = "面象图片URL")
    private String faceImage;

    @Schema(description = "脉象描述")
    private String pulseDescription;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}