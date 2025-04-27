package cn.iocoder.yudao.module.ai.dal.dataobject.prompttemplate;

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
@TableName("ai_prompt_template")
@KeySequence("ai_prompt_template_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptTemplateDO extends BaseDO {

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
    private String content;
    /**
     * 参数定义(JSON)
     */
    private String parameters;
    /**
     * 领域类型
     */
    private Integer domain;
    /**
     * 状态:0禁用 1启用
     */
    private Integer status;
    /**
     * 默认模型编号
     */
    private Long defaultModelId;
    /**
     * 默认知识库编号
     */
    private Long defaultKnowledgeId;

}