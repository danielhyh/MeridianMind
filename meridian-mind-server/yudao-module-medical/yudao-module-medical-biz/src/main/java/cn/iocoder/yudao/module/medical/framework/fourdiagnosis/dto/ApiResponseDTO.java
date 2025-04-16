package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto;

import lombok.Data;

/**
 * API响应 DTO
 */
@Data
public class ApiResponseDTO<T> {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应消息
     */
    private String message;
}