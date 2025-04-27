import request from '@/config/axios'

// AI提示词模板 VO
export interface PromptTemplateVO {
  name: string // 模板名称
  description: string // 模板描述
  content: string // 提示词文本
  domain: number // 领域类型
  status: number // 状态
  defaultModelId: number // 默认模型编号
  defaultKnowledgeId: number // 默认知识库编号
}

// AI提示词模板 API
export const PromptTemplateApi = {
  // 查询AI提示词模板分页
  getPromptTemplatePage: async (params: any) => {
    return await request.get({ url: `/ai/prompt-template/page`, params })
  },

  // 查询AI提示词模板详情
  getPromptTemplate: async (id: number) => {
    return await request.get({ url: `/ai/prompt-template/get?id=` + id })
  },

  // 新增AI提示词模板
  createPromptTemplate: async (data: PromptTemplateVO) => {
    return await request.post({ url: `/ai/prompt-template/create`, data })
  },

  // 修改AI提示词模板
  updatePromptTemplate: async (data: PromptTemplateVO) => {
    return await request.put({ url: `/ai/prompt-template/update`, data })
  },

  // 删除AI提示词模板
  deletePromptTemplate: async (id: number) => {
    return await request.delete({ url: `/ai/prompt-template/delete?id=` + id })
  },

  // 导出AI提示词模板 Excel
  exportPromptTemplate: async (params) => {
    return await request.download({ url: `/ai/prompt-template/export-excel`, params })
  },
}
