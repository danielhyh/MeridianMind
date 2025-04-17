package cn.iocoder.yudao.module.medical.controller.admin.patient.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 患者管理 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PatientProfileRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "32617")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "会员用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "28025")
    @ExcelProperty("会员用户ID")
    private Long userId;

    @Schema(description = "身高(cm)")
    @ExcelProperty("身高(cm)")
    private BigDecimal height;

    @Schema(description = "体重(kg)")
    @ExcelProperty("体重(kg)")
    private BigDecimal weight;

    @Schema(description = "血型", example = "1")
    @ExcelProperty(value = "血型", converter = DictConvert.class)
    @DictFormat("medical_blood_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String bloodType;

    @Schema(description = "过敏史")
    @ExcelProperty("过敏史")
    private String allergies;

    @Schema(description = "既往病史")
    @ExcelProperty("既往病史")
    private String medicalHistory;

    @Schema(description = "家族病史")
    @ExcelProperty("家族病史")
    private String familyMedicalHistory;

    @Schema(description = "中医体质类型", example = "2")
    @ExcelProperty(value = "中医体质类型", converter = DictConvert.class)
    @DictFormat("medical_constitution_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String constitutionType;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}