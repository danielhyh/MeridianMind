package cn.iocoder.yudao.module.medical.dal.dataobject.diagnosis;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 诊断结果 DO
 *
 * @author 芋道源码
 */
@TableName("medical_diagnosis")
@KeySequence("medical_diagnosis_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisDO extends BaseDO {

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
     * 主要证型
     */
    private String primarySyndrome;
    /**
     * 次要证型(JSON数组)
     */
    private String secondarySyndromes;
    /**
     * 八纲辨证(JSON)
     */
    private String eightPrinciples;
    /**
     * 诊断解释
     */
    private String diagnosisExplanation;
    /**
     * AI原始输出
     */
    private String aiRawOutput;
    /**
     * 置信度(1-100)
     */
    private Integer confidenceLevel;

}