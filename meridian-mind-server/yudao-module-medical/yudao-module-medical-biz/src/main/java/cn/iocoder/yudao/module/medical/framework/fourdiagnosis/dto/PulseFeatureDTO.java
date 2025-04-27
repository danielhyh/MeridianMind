package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 脉象特征 DTO
 */
@Data
public class PulseFeatureDTO {
    /**
     * 左脉象类型
     */
    @Schema(description = "左脉象类型", example = "滑脉")
    private String leftPulseType;
    /**
     * 右脉象类型
     */
    @Schema(description = "右脉象类型", example = "浮脉")
    private String rightPulseType;
    
    /**
     * 脉象描述
     */
    @Schema(description = "脉象描述")
    private String description;
}