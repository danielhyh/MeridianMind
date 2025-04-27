<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      v-loading="formLoading"
    >
      <el-form-item label="模板名称" prop="name">
        <el-input v-model="formData.name" placeholder="请输入模板名称" />
      </el-form-item>
      <el-form-item label="模板描述" prop="description">
        <el-input v-model="formData.description" type="textarea" placeholder="请输入模板描述" />
      </el-form-item>
      <el-form-item label="提示词文本" prop="content">
        <el-input v-model="formData.content" type="textarea" placeholder="请输入提示词文本" />
      </el-form-item>
      <el-form-item label="领域类型" prop="domain">
        <el-select v-model="formData.domain" placeholder="请选择领域类型">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.AI_PROMPT_DOMAIN)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
            :label="dict.value"
          >
            {{ dict.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="默认模型编号" prop="defaultModelId">
        <el-select v-model="formData.defaultModelId" placeholder="请选择默认模型编号">
          <el-option label="请选择字典生成" value="" />
        </el-select>
      </el-form-item>
      <el-form-item label="默认知识库编号" prop="defaultKnowledgeId">
        <el-input v-model="formData.defaultKnowledgeId" placeholder="请输入默认知识库编号" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
import {PromptTemplateApi, PromptTemplateVO} from '@/api/ai/prompttemplate'
import {getIntDictOptions, DICT_TYPE} from "@/utils/dict";

/** AI提示词模板 表单 */
defineOptions({ name: 'PromptTemplateForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  name: undefined,
  description: undefined,
  content: undefined,
  domain: undefined,
  status: undefined,
  defaultModelId: undefined,
  defaultKnowledgeId: undefined,
})
const formRules = reactive({
  name: [{ required: true, message: '模板名称不能为空', trigger: 'blur' }],
  content: [{ required: true, message: '提示词文本不能为空', trigger: 'blur' }],
  domain: [{ required: true, message: '领域类型不能为空', trigger: 'change' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'blur' }],
})
const formRef = ref() // 表单 Ref

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await PromptTemplateApi.getPromptTemplate(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as PromptTemplateVO
    if (formType.value === 'create') {
      await PromptTemplateApi.createPromptTemplate(data)
      message.success(t('common.createSuccess'))
    } else {
      await PromptTemplateApi.updatePromptTemplate(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    name: undefined,
    description: undefined,
    content: undefined,
    domain: undefined,
    status: undefined,
    defaultModelId: undefined,
    defaultKnowledgeId: undefined,
  }
  formRef.value?.resetFields()
}
</script>
