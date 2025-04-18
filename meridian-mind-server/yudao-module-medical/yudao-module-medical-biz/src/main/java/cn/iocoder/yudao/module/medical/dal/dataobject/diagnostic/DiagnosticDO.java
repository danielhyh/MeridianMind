package cn.iocoder.yudao.module.medical.dal.dataobject.diagnostic;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 问诊记录 DO
 *
 * @author 芋道源码
 */
@TableName("medical_diagnostic")
@KeySequence("medical_diagnostic_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 患者ID
     */
    private Long userId;
    /**
     * 医生ID（预留）
     */
    private Long doctorId;
    /**
     * 问诊时间
     */
    private LocalDateTime diagnosticTime;
    /**
     * 主诉
     */
    private String chiefComplaint;
    /**
     * 发病时间
     */
    private String onsetTime;
    /**
     * 病程发展
     */
    private String diseaseCourse;
    /**
     * 状态：0进行中 1已完成 2已取消
     */
    private Integer status;

}