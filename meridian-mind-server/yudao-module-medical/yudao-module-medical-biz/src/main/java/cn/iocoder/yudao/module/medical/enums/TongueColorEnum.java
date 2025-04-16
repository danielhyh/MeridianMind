package cn.iocoder.yudao.module.medical.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 舌质颜色枚举
 */
@Getter
@AllArgsConstructor
public enum TongueColorEnum {

    PALE("pale", "淡白舌"),
    LIGHT_RED("light_red", "淡红舌"),
    RED("red", "红舌"),
    CRIMSON("crimson", "绛舌"),
    PURPLE("purple", "紫舌"),
    BLUE("blue", "青舌");

    /**
     * 编码
     */
    private final String code;
    /**
     * 名称
     */
    private final String name;

} 