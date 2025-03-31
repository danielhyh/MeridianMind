package cn.iocoder.yudao.module.medical.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * 医疗模块错误码枚举类
 *
 * 医疗模块，使用 1-016-000-000 段
 */
public interface MedicalErrorCodeConstants {

    // ========== 诊断模块 1-016-000-100 ==========
    ErrorCode DIAGNOSIS_NOT_EXISTS = new ErrorCode(1_016_000_100, "诊断记录不存在");
    ErrorCode DIAGNOSIS_NOT_WAITING = new ErrorCode(1_016_000_101, "诊断不处于等待状态，无法开始");
    ErrorCode DIAGNOSIS_NOT_IN_PROGRESS = new ErrorCode(1_016_000_102, "诊断不处于进行中状态，无法完成");
    
    // ========== 患者模块 1-016-000-200 ==========
    ErrorCode PATIENT_NOT_EXISTS = new ErrorCode(1_016_000_200, "患者不存在");
    ErrorCode PATIENT_MOBILE_EXISTS = new ErrorCode(1_016_000_201, "手机号已被使用");
    
    // ========== 智能问诊模块 1-016-000-300 ==========
    ErrorCode MEDICAL_AI_ENGINE_ERROR = new ErrorCode(1_016_000_300, "AI诊断引擎异常");
    ErrorCode MEDICAL_RECORD_NOT_EXISTS = new ErrorCode(1_016_000_301, "医疗记录不存在");
} 