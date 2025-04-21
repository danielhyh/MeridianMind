package cn.iocoder.yudao.module.medical.framework.fourdiagnosis.dto;

import lombok.Data;

import java.util.List;

/**
 * 问诊数据传输对象
 */
@Data
public class InquiryDTO {
    /**
     * 症状选择，可选择多个症状
     */
    private List<String> symptoms;
    
    /**
     * 情绪状态
     */
    private String emotionalState;
    
    /**
     * 睡眠情况
     */
    private String sleepStatus;
    
    /**
     * 饮食情况
     */
    private String dietStatus;
    
    /**
     * 症状描述，患者自述
     */
    private String symptomDescription;
    
    /**
     * 发病时间（天数）
     */
    private Integer onsetDays;
    
    /**
     * 其他备注信息
     */
    private String remarks;
}