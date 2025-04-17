package cn.iocoder.yudao.module.medical.dal.dataobject.constitution;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 体质评估问题 DO
 */
@TableName(value = "medical_constitution_question", autoResultMap = true)
@KeySequence("medical_constitution_question_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ConstitutionQuestionDO extends BaseDO {

    /**
     * 问题ID
     */
    @TableId
    private Long id;

    /**
     * 问卷ID
     */
    private Long questionnaireId;

    /**
     * 问题内容
     */
    private String question;

    /**
     * 问题类型：1单选 2多选
     */
    private Integer questionType;

    /**
     * 选项内容及分值，格式：[{"label": "选项1", "value": "A", "score": 1}, ...]
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<QuestionOption> options;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 关联体质类型，对应字典值
     */
    private String constitutionType;
    /**
     * 步骤代码
     *
     * 枚举 {@link cn.iocoder.yudao.module.medical.enums.DictTypeConstants#MEDICAL_CONSTITUTION_QUESTION_STEP}
     */
    private String step;

    /**
     * 问题选项
     */
    @Data
    public static class QuestionOption {
        /**
         * 选项标签
         */
        private String label;
        
        /**
         * 选项值
         */
        private String value;
        
        /**
         * 选项分值
         */
        private Integer score;
    }
}