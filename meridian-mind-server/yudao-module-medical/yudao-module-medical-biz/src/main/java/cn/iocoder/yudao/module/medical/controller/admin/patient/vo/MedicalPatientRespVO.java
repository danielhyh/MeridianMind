package cn.iocoder.yudao.module.medical.controller.admin.patient.vo;

import cn.iocoder.yudao.module.medical.controller.app.patient.vo.AppMedicalPatientRespVO;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 患者管理 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MedicalPatientRespVO {

    @Schema(description = "会员编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "32617")
    @ExcelProperty("会员编号")
    private Long id;

    @Schema(description = "注册 IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    private String registerIp;

    @Schema(description = "最后登录IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    private String loginIp;

    @Schema(description = "最后登录时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime loginDate;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "会员标签", example = "[红色, 快乐]")
    private List<String> tagNames;

    @Schema(description = "用户分组", example = "购物达人")
    private String groupName;

    // ========== 健康档案 ==========

    @Schema(description = "健康档案信息")
    private AppMedicalPatientRespVO.Profile profile;

    @Schema(description = "用户 App - 健康档案信息")
    @Data
    public static class Profile {

        @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "32617")
        private Long id;

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

    }

}