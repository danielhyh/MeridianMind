package cn.iocoder.yudao.module.medical.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * medical 错误码枚举类
 * <p>
 * medical 系统，使用 1-060-000-000 段
 */
public interface ErrorCodeConstants {
    ErrorCode PATIENT_PROFILE_NOT_EXISTS = new ErrorCode(1_060_000_000, "患者不存在");
}