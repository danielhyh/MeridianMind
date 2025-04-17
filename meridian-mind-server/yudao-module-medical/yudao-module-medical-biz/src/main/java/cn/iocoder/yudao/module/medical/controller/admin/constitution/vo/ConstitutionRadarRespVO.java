package cn.iocoder.yudao.module.medical.controller.admin.constitution.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConstitutionRadarRespVO {
    /**
     * 评估记录ID
     */
    private Long id;
    
    /**
     * 评估时间
     */
    private LocalDateTime createTime;
    
    /**
     * 主要体质类型
     */
    private String primaryType;
    
    /**
     * 主要体质类型名称
     */
    private String primaryTypeName;
    
    /**
     * 各体质类型评分数据
     */
    private List<TypeScore> typeScores;
    
    @Data
    public static class TypeScore {
        /**
         * 体质类型
         */
        private String type;
        
        /**
         * 体质类型名称
         */
        private String name;
        
        /**
         * 评分
         */
        private Integer score;
    }
}