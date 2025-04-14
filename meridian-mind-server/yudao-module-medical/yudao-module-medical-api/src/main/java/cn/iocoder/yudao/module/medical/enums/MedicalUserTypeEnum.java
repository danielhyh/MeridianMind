package cn.iocoder.yudao.module.medical.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 医疗用户类型的枚举值
 *
 * @author hanford
 */
@Getter
@AllArgsConstructor
public enum MedicalUserTypeEnum {

    /** 患者用户 */
    PATIENT_USER(1),
    /** 医生用户 */
    DOCTOR_USER(2),
    /** 管理员用户 */
    ADMIN_USER(0);

    /**
     * 用户类型
     */
    private final Integer userType;

}
