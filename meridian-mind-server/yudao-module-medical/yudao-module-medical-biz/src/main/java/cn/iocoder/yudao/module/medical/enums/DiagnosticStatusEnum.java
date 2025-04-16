package cn.iocoder.yudao.module.medical.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 诊断状态枚举
 */
@Getter
@AllArgsConstructor
public enum DiagnosticStatusEnum {

    CREATED(1, "已创建"),
    PROCESSING(2, "处理中"),
    COMPLETED(3, "已完成"),
    FAILED(4, "失败");

    /**
     * 状态编码
     */
    private final Integer code;
    /**
     * 状态描述
     */
    private final String desc;

} 