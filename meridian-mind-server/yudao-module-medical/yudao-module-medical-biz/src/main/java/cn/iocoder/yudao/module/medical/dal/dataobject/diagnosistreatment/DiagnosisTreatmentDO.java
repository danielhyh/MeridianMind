package cn.iocoder.yudao.module.medical.dal.dataobject.diagnosistreatment;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 中医诊断治疗一体化 DO
 *
 * @author fly
 */
@TableName("medical_diagnosis_treatment")
@KeySequence("medical_diagnosis_treatment_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisTreatmentDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 问诊记录ID
     */
    private Long diagnosticId;
    /**
     * 患者ID
     */
    private Long userId;
    /**
     * 主要证型
     */
    private String primarySyndrome;
    /**
     * 主要证型解释
     */
    private String primarySyndromeExplanation;
    /**
     * 次要证型JSON数组，包含名称和解释
     */
    private String secondarySyndromes;
    /**
     * 八纲辨证JSON
     */
    private String eightPrinciples;
    /**
     * 症状详情JSON
     */
    private String symptoms;
    /**
     * 辨证分析
     */
    private String syndromeAnalysis;
    /**
     * 治疗原则，如"健脾益气，养心安神"
     */
    private String treatmentPrinciple;
    /**
     * 治疗原则解释
     */
    private String principleExplanation;
    /**
     * 方剂JSON，包含名称、组成、用法等
     */
    private String prescriptions;
    /**
     * 生活调养建议JSON
     */
    private String lifestyleAdvice;
    /**
     * 食疗推荐JSON
     */
    private String dietRecipes;
    /**
     * 随访管理JSON
     */
    private String followUp;
    /**
     * AI原始输出
     */
    private String aiRawOutput;
    /**
     * 置信度(1-100)
     */
    private Integer confidenceLevel;

}