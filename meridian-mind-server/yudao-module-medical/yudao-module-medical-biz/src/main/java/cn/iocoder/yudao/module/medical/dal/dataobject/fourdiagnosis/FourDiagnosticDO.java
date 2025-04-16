package cn.iocoder.yudao.module.medical.dal.dataobject.fourdiagnosis;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 四诊信息 DO
 *
 * @author 芋道源码
 */
@TableName("medical_four_diagnostic")
@KeySequence("medical_four_diagnostic_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FourDiagnosticDO extends BaseDO {

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
     * 望诊数据
     */
    private String inspection;
    /**
     * 闻诊数据
     */
    private String auscultation;
    /**
     * 问诊数据
     */
    private String inquiry;
    /**
     * 切诊数据
     */
    private String palpation;
    /**
     * 舌象图片URL
     */
    private String tongueImage;
    /**
     * 面象图片URL
     */
    private String faceImage;
    /**
     * 脉象描述
     */
    private String pulseDescription;

}