package cn.iocoder.yudao.framework.ai.core.model.maxkb;

import lombok.Builder;
import lombok.Data;

/**
 * MaxKB API响应对象
 * 封装MaxKB API返回的数据
 */
@Data
@Builder
public class MaxKBApiResponse {
    /**
     * 会话ID
     */
    private String chatId;
    
    /**
     * 消息ID
     */
    private String id;
    
    /**
     * 是否成功
     */
    private Boolean operate;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 是否结束
     */
    private Boolean isEnd;
    
    /**
     * 推理内容
     */
    private String reasoningContent;
    
    /**
     * 提示词Token数量
     */
    private Integer promptTokens;
    
    /**
     * 生成Token数量
     */
    private Integer completionTokens;
}