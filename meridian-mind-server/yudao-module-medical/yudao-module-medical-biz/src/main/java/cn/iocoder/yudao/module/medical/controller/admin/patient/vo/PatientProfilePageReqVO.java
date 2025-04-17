package cn.iocoder.yudao.module.medical.controller.admin.patient.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 患者管理分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PatientProfilePageReqVO extends PageParam {

    @Schema(description = "会员用户ID", example = "28025")
    private Long userId;

    @Schema(description = "身高(cm)")
    private BigDecimal height;

    @Schema(description = "体重(kg)")
    private BigDecimal weight;

    @Schema(description = "血型", example = "1")
    private String bloodType;

    @Schema(description = "过敏史")
    private String allergies;

    @Schema(description = "既往病史")
    private String medicalHistory;

    @Schema(description = "家族病史")
    private String familyMedicalHistory;

    @Schema(description = "中医体质类型", example = "2")
    private String constitutionType;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}