package cn.iocoder.yudao.module.medical.dal.dataobject.aiprompt;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * AI提示词模板 DO
 *
 * @author 芋道源码
 */
@TableName("medical_ai_prompt")
@KeySequence("medical_ai_prompt_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiPromptDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 模板名称
     */
    private String name;
    /**
     * 模板描述
     */
    private String description;
    /**
     * 提示词文本
     */
    private String promptText;
    /**
     * 类别:1诊断 2治疗 3健康管理
     */
    private Integer category;
    /**
     * 参数定义(JSON)
     */
    private String parameters;
    /**
     * 状态:0禁用 1启用
     */
    private Integer status;

}