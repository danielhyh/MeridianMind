package cn.iocoder.yudao.module.ai.service.model;

import cn.iocoder.yudao.framework.ai.core.enums.AiPlatformEnum;
import cn.iocoder.yudao.framework.ai.core.model.maxkb.MaxKBClient;
import cn.iocoder.yudao.framework.ai.core.model.midjourney.api.MidjourneyApi;
import cn.iocoder.yudao.framework.ai.core.model.suno.api.SunoApi;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.apikey.AiApiKeyPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.apikey.AiApiKeySaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiApiKeyDO;
import jakarta.validation.Valid;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;

/**
 * AI API 密钥 Service 接口
 *
 * @author 芋道源码
 */
public interface AiApiKeyService {

    /**
     * 创建 API 密钥
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createApiKey(@Valid AiApiKeySaveReqVO createReqVO);

    /**
     * 更新 API 密钥
     *
     * @param updateReqVO 更新信息
     */
    void updateApiKey(@Valid AiApiKeySaveReqVO updateReqVO);

    /**
     * 删除 API 密钥
     *
     * @param id 编号
     */
    void deleteApiKey(Long id);

    /**
     * 获得 API 密钥
     *
     * @param id 编号
     * @return API 密钥
     */
    AiApiKeyDO getApiKey(Long id);

    /**
     * 校验 API 密钥
     *
     * @param id 比那好
     * @return API 密钥
     */
    AiApiKeyDO validateApiKey(Long id);

    /**
     * 获得 API 密钥分页
     *
     * @param pageReqVO 分页查询
     * @return API 密钥分页
     */
    PageResult<AiApiKeyDO> getApiKeyPage(AiApiKeyPageReqVO pageReqVO);

    /**
     * 获得 API 密钥列表
     *
     * @return API 密钥列表
     */
    List<AiApiKeyDO> getApiKeyList();

    // ========== 与 spring-ai 集成 ==========

    /**
     * 获得 ChatModel 对象
     *
     * @param id 编号
     * @return ChatModel 对象
     */
    ChatModel getChatModel(Long id);

    /**
     * 获得 ImageModel 对象
     *
     * TODO 可优化点：目前默认获取 platform 对应的第一个开启的配置用于绘画；后续可以支持配置选择
     *
     * @param platform 平台
     * @return ImageModel 对象
     */
    ImageModel getImageModel(AiPlatformEnum platform);

    /**
     * 获得 MidjourneyApi 对象
     *
     * TODO 可优化点：目前默认获取 Midjourney 对应的第一个开启的配置用于绘画；后续可以支持配置选择
     *
     * @return MidjourneyApi 对象
     */
    MidjourneyApi getMidjourneyApi();

    /**
     * 获得 SunoApi 对象
     *
     * TODO 可优化点：目前默认获取 Suno 对应的第一个开启的配置用于音乐；后续可以支持配置选择
     *
     * @return SunoApi 对象
     */
    SunoApi getSunoApi();

    /**
     * 获得 EmbeddingModel 对象
     *
     * @param id 编号
     * @return EmbeddingModel 对象
     */
    EmbeddingModel getEmbeddingModel(Long id);

    /**
     * 获得 VectorStore 对象
     *
     * @param id 编号
     * @return VectorStore 对象
     */
    VectorStore getOrCreateVectorStore(Long id);
    /**
     * 获取MaxKB聊天模型
     *
     * @param id API密钥ID
     * @param applicationId MaxKB应用ID
     * @return ChatModel实例
     */
    ChatModel getMaxKBChatModel(Long id, String applicationId);

    /**
     * 获取MaxKB流式聊天模型
     *
     * @param id API密钥ID
     * @param applicationId MaxKB应用ID
     * @return StreamingChatModel实例
     */
    StreamingChatModel getMaxKBStreamingChatModel(Long id, String applicationId);

    /**
     * 获取MaxKB客户端
     *
     * @param id API密钥ID
     * @return MaxKBClient实例
     */
    MaxKBClient getMaxKBClient(Long id);
}