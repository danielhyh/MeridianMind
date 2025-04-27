package cn.iocoder.yudao.module.medical.controller.app.patient.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "用户 APP - 用户个人信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppPatientProfileRespVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @Schema(description = "真实姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    private String name;

    @Schema(description = "用户头像", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn/xxx.png")
    private String avatar;

    @Schema(description = "用户手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    private String mobile;

    @Schema(description = "用户性别", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sex;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "出生日期", example = "2023-03-12")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDateTime birthday;

    @Schema(description = "身高(cm)")
    private BigDecimal height;

    @Schema(description = "体重(kg)")
    private BigDecimal weight;

    @Schema(description = "血型", example = "A")
    private String bloodType;

    @Schema(description = "过敏史记录")
    private List<AllergyRecord> allergies;

    @Schema(description = "既往病史记录")
    private List<DiseaseRecord> medicalHistory;

    @Schema(description = "家族病史记录")
    private List<FamilyMedicalRecord> familyMedicalHistory;

    @Schema(description = "中医体质类型", example = "平和质")
    private String constitutionType;
    @Schema(description = "年龄")
    private int age;

    @Data
    @Schema(description = "过敏记录信息")
    public static class AllergyRecord {
        @Schema(description = "过敏类型", example = "药物过敏")
        private String allergyType;

        @Schema(description = "过敏源", example = "青霉素")
        private String allergySource;

        @Schema(description = "严重程度", example = "中度")
        private String severity;

        @Schema(description = "过敏反应", example = "皮疹、瘙痒")
        private String reaction;

        @Schema(description = "记录日期", example = "2020/05/15")
        private String recordDate;
    }

    @Data
    @Schema(description = "疾病记录信息")
    public static class DiseaseRecord {
        @Schema(description = "疾病名称", example = "高血压")
        private String diseaseName;

        @Schema(description = "诊断日期", example = "2022/01/10")
        private String diagnosisDate;

        @Schema(description = "当前状态", example = "控制中")
        private String currentStatus;

        @Schema(description = "治疗方案", example = "服用降压药")
        private String treatment;

        @Schema(description = "诊断医院", example = "北京协和医院")
        private String hospital;
    }

    @Data
    @Schema(description = "家族病史记录信息")
    public static class FamilyMedicalRecord {
        @Schema(description = "关系", example = "父亲")
        private String relationship;

        @Schema(description = "疾病名称", example = "高血压")
        private String diseaseName;

        @Schema(description = "发病年龄", example = "45")
        private Integer onsetAge;

        @Schema(description = "当前状态", example = "在世")
        private String currentStatus;

        @Schema(description = "备注", example = "长期服用降压药")
        private String notes;
    }

}
