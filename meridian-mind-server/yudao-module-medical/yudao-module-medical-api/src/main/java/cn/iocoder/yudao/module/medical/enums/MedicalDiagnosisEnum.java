package cn.iocoder.yudao.module.medical.enums;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 医疗诊断状态枚举
 */
@Getter
@AllArgsConstructor
public enum MedicalDiagnosisEnum implements ArrayValuable<Integer> {

    WAITING(0, "等待诊断"),
    IN_PROGRESS(1, "诊断中"),
    COMPLETED(2, "已完成"),
    CANCELLED(3, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(MedicalDiagnosisEnum::getStatus).toArray(Integer[]::new);

    /**
     * 状态
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }
} 