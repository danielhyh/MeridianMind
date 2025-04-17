package cn.iocoder.yudao.module.medical.dal.dataobject.constitution;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 体质评估问卷模板 DO
 */
@TableName("medical_constitution_questionnaire")
@KeySequence("medical_constitution_questionnaire_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ConstitutionQuestionnaireDO extends BaseDO {

    /**
     * 问卷ID
     */
    @TableId
    private Long id;

    /**
     * 问卷标题
     */
    private String title;

    /**
     * 问卷描述
     */
    private String description;

    /**
     * 版本号
     */
    private String version;

    /**
     * 状态：0启用 1停用
     */
    private Integer status;
}