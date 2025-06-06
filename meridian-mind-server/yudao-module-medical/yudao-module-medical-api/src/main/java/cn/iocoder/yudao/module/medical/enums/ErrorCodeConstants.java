package cn.iocoder.yudao.module.medical.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * medical 错误码枚举类
 * <p>
 * medical 系统，使用 1-060-000-000 段
 */
public interface ErrorCodeConstants {
    // ========== 通用错误码 1_060_000_000 ~ 1_060_099_999 ==========
    ErrorCode PATIENT_PROFILE_NOT_EXISTS = new ErrorCode(1_060_000_000, "患者健康档案不存在");
    // ========== 体质评估模块 1_060_100_000 ~ 1_060_199_999 ==========
    // 问卷相关
    ErrorCode QUESTIONNAIRE_NOT_EXISTS = new ErrorCode(1_060_100_000, "体质评估问卷不存在");
    ErrorCode QUESTIONNAIRE_NAME_DUPLICATE = new ErrorCode(1_060_100_001, "体质评估问卷名称已存在");
    ErrorCode QUESTIONNAIRE_HAS_QUESTIONS = new ErrorCode(1_060_100_002, "问卷下存在题目，无法删除");
    ErrorCode QUESTIONNAIRE_HAS_RECORDS = new ErrorCode(1_060_100_003, "问卷已有评估记录，无法删除");
    ErrorCode QUESTIONNAIRE_QUESTION_NOT_EXISTS = new ErrorCode(1_060_000_004, "问卷没有题目");

    // 问题相关
    ErrorCode QUESTION_NOT_EXISTS = new ErrorCode(1_060_101_000, "体质评估问题不存在");
    ErrorCode QUESTION_OPTION_EMPTY = new ErrorCode(1_060_101_001, "问题选项不能为空");
    ErrorCode QUESTION_OPTION_DUPLICATE = new ErrorCode(1_060_101_002, "问题选项值重复");

    // 评估记录相关
    ErrorCode RECORD_NOT_EXISTS = new ErrorCode(1_060_102_000, "体质评估记录不存在");
    ErrorCode RECORD_ANSWER_INCOMPLETE = new ErrorCode(1_060_102_001, "体质评估答案不完整");
    ErrorCode RECORD_NOT_EXISTS_OR_NOT_COMPLETED = new ErrorCode(1_060_102_002, "评估记录不存在或未完成");
    ErrorCode ERROR_CODE_SAVE_FACE_IMAGE = new ErrorCode(1_060_102_003, "保存面色图像失败");
    ErrorCode ERROR_CODE_SAVE_VOICE_AUDIO = new ErrorCode(1_060_102_004, "保存声音音频失败");

    // 统计分析相关
    ErrorCode STATISTICS_DATA_EMPTY = new ErrorCode(1_060_103_000, "暂无统计数据");

    // 问诊记录相关
    ErrorCode DIAGNOSTIC_NOT_EXISTS = new ErrorCode(1_060_104_001, "问诊记录不存在");
    // ========== 四诊信息 ==========
    ErrorCode FOUR_DIAGNOSTIC_NOT_EXISTS = new ErrorCode(1_022_004_000, "四诊信息不存在");
    ErrorCode FOUR_DIAGNOSTIC_INQUIRY_MISSING = new ErrorCode(1_022_004_001, "问诊信息未填写");
    ErrorCode FOUR_DIAGNOSTIC_PALPATION_MISSING = new ErrorCode(1_022_004_002, "脉象信息未填写");

    // ========== 诊断结果 1_060_105_000 ==========
    ErrorCode DIAGNOSIS_NOT_EXISTS = new ErrorCode(1_022_005_000, "诊断结果不存在");
    ErrorCode DIAGNOSIS_PARSE_ERROR = new ErrorCode(1_022_005_001, "诊断结果解析错误");
    ErrorCode AI_RESPONSE_PARSE_ERROR = new ErrorCode(1_022_005_002, "AI回复解析错误");
    ErrorCode DIAGNOSTIC_ACCESS_DENIED = new ErrorCode(1_022_005_003, "无权访问该问诊记录");
    ErrorCode AI_RESPONSE_EMPTY = new ErrorCode(1_022_005_004, "AI回复内容为空");
    ErrorCode DIAGNOSIS_EIGHT_PRINCIPLES_MISSING = new ErrorCode(1_022_005_005, "八纲辨证数据缺失");
    ErrorCode DIAGNOSIS_EIGHT_PRINCIPLES_FORMAT_ERROR = new ErrorCode(1_022_005_006, "八纲辨证数据格式错误");
    ErrorCode DIAGNOSIS_EIGHT_PRINCIPLES_STRENGTH_ERROR = new ErrorCode(1_022_005_007, "八纲辨证强度值范围错误");

    // ========== 中医诊断治疗一体化 1_060_105_000 ==========
    ErrorCode DIAGNOSIS_TREATMENT_NOT_EXISTS = new ErrorCode(1_022_005_000, "中医诊断治疗一体化不存在");

}