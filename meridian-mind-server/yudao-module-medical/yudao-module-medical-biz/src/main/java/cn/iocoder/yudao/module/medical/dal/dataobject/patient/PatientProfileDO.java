package cn.iocoder.yudao.module.medical.dal.dataobject.patient;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 患者管理 DO
 *
 * @author 芋道源码
 */
@TableName("medical_patient_profile")
@KeySequence("medical_patient_profile_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 会员用户ID
     */
    private Long memberUserId;
    /**
     * 身高(cm)
     */
    private BigDecimal height;
    /**
     * 体重(kg)
     */
    private BigDecimal weight;
    /**
     * 血型
     *
     * 枚举 {@link TODO medical_blood_type 对应的类}
     */
    private String bloodType;
    /**
     * 过敏史
     */
    private String allergies;
    /**
     * 既往病史
     */
    private String medicalHistory;
    /**
     * 家族病史
     */
    private String familyMedicalHistory;
    /**
     * 中医体质类型
     *
     * 枚举 {@link TODO medical_constitution_type 对应的类}
     */
    private String constitutionType;

}