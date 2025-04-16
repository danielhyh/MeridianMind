package cn.iocoder.yudao.module.medical.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 舌苔特征枚举
 */
@Getter
@AllArgsConstructor
public enum TongueCoatingEnum {

    THIN_WHITE("thin_white", "薄白苔"),
    THICK_WHITE("thick_white", "厚白苔"),
    THIN_YELLOW("thin_yellow", "薄黄苔"),
    THICK_YELLOW("thick_yellow", "厚黄苔"),
    GRAY("gray", "灰苔"),
    BLACK("black", "黑苔"),
    NO_COATING("no_coating", "无苔"),
    GREASY("greasy", "腻苔"),
    DRY("dry", "燥苔");

    /**
     * 编码
     */
    private final String code;
    /**
     * 名称
     */
    private final String name;

} 