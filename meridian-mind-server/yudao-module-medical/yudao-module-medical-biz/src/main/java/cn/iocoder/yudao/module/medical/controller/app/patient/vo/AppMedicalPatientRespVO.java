package cn.iocoder.yudao.module.medical.controller.app.patient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "用户 APP - 用户个人信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppMedicalPatientRespVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    private String nickname;

    @Schema(description = "用户头像", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn/xxx.png")
    private String avatar;

    @Schema(description = "用户手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    private String mobile;

    @Schema(description = "用户性别", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sex;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "健康档案信息")
    private Profile profile;

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
