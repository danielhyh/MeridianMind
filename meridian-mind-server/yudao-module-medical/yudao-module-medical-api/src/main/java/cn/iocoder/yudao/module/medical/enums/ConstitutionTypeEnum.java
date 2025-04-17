package cn.iocoder.yudao.module.medical.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 中医体质类型枚举
 */
@Getter
@AllArgsConstructor
public enum ConstitutionTypeEnum implements ArrayValuable<String> {

    NEUTRAL("neutral", "平和质"),
    QI_DEFICIENCY("qi_deficiency", "气虚质"),
    YANG_DEFICIENCY("yang_deficiency", "阳虚质"),
    YIN_DEFICIENCY("yin_deficiency", "阴虚质"),
    PHLEGM_DAMPNESS("phlegm_dampness", "痰湿质"),
    DAMP_HEAT("damp_heat", "湿热质"),
    BLOOD_STASIS("blood_stasis", "血瘀质"),
    QI_STAGNATION("qi_stagnation", "气郁质"),
    SPECIAL("special", "特禀质");

    public static final String[] ARRAYS = Arrays.stream(values()).map(ConstitutionTypeEnum::getValue).toArray(String[]::new);

    /**
     * 体质类型值
     */
    private final String value;

    /**
     * 体质类型名称
     */
    private final String name;

    /**
     * 根据体质类型值获取枚举实例
     *
     * @param value 体质类型值
     * @return 体质类型枚举
     */
    public static ConstitutionTypeEnum fromValue(String value) {
        return ArrayUtil.firstMatch(type -> type.getValue().equals(value), ConstitutionTypeEnum.values());
    }

    /**
     * 获取指定值对应的体质类型名称
     *
     * @param value 体质类型值
     * @return 体质类型名称
     */
    public static String nameOf(String value) {
        ConstitutionTypeEnum type = fromValue(value);
        return type != null ? type.getName() : "未知";
    }

    /**
     * 获取所有体质类型的映射（值 -> 名称）
     *
     * @return 体质类型映射
     */
    public static Map<String, String> toMap() {
        return Arrays.stream(values())
                .collect(Collectors.toMap(ConstitutionTypeEnum::getValue, ConstitutionTypeEnum::getName));
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }
}