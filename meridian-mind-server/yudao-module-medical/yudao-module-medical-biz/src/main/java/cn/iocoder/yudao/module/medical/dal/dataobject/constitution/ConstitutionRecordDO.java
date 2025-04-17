package cn.iocoder.yudao.module.medical.dal.dataobject.constitution;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 体质评估记录 DO
 */
@TableName(value = "medical_constitution_record", autoResultMap = true)
@KeySequence("medical_constitution_record_seq")
@Data
@ToString(callSuper = true)
public class ConstitutionRecordDO {

    /**
     * 记录ID
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 问卷ID
     */
    private Long questionnaireId;

    /**
     * 答题记录，格式：{"questionId": "选项值", ...}
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<Long, String> answers;

    /**
     * 主要体质类型
     */
    private String primaryType;

    /**
     * 次要体质类型列表
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> secondaryTypes;

    /**
     * 各体质得分，格式：{"体质类型": 分数, ...}
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Integer> scores;
    /**
     * 是否完成评估
     */
    private Boolean isCompleted;

    /**
     * 是否应用到用户档案
     */
    private Boolean isApplied;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建者
     */
    private String creator;
}