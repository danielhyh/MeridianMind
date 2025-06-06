<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="120px"
    >
      <el-form-item label="所属平台" prop="platform">
        <el-select v-model="formData.platform" clearable placeholder="请输入平台">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.AI_PLATFORM)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="模型类型" prop="type">
        <el-select
          v-model="formData.type"
          :disabled="formData.id"
          clearable
          placeholder="请输入模型类型"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.AI_MODEL_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="API 秘钥" prop="keyId">
        <el-select v-model="formData.keyId" clearable placeholder="请选择 API 秘钥">
          <el-option
            v-for="apiKey in apiKeyList"
            :key="apiKey.id"
            :label="apiKey.name"
            :value="apiKey.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="模型名字" prop="name">
        <el-input v-model="formData.name" placeholder="请输入模型名字" />
      </el-form-item>
      <el-form-item label="模型标识" prop="model">
        <el-input v-model="formData.model" placeholder="请输入模型标识" />
      </el-form-item>
      <el-form-item label="模型排序" prop="sort">
        <el-input-number v-model="formData.sort" class="!w-1/1" placeholder="请输入模型排序" />
      </el-form-item>
      <el-form-item label="开启状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
            :value="dict.value"
          >
            {{ dict.label }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item
        v-if="formData.type === AiModelTypeEnum.CHAT"
        label="温度参数"
        prop="temperature"
      >
        <el-input-number
          v-model="formData.temperature"
          :max="2"
          :min="0"
          :precision="2"
          class="!w-1/1"
          placeholder="请输入温度参数"
        />
      </el-form-item>
      <el-form-item
        v-if="formData.type === AiModelTypeEnum.CHAT"
        label="回复数 Token 数"
        prop="maxTokens"
      >
        <el-input-number
          v-model="formData.maxTokens"
          :max="8192"
          :min="0"
          class="!w-1/1"
          placeholder="请输入回复数 Token 数"
        />
      </el-form-item>
      <el-form-item
        v-if="formData.type === AiModelTypeEnum.CHAT"
        label="上下文数量"
        prop="maxContexts"
      >
        <el-input-number
          v-model="formData.maxContexts"
          :max="20"
          :min="0"
          class="!w-1/1"
          placeholder="请输入上下文数量"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { ModelApi, ModelVO } from '@/api/ai/model/model'
import { ApiKeyApi, ApiKeyVO } from '@/api/ai/model/apiKey'
import { CommonStatusEnum } from '@/utils/constants'
import { DICT_TYPE, getIntDictOptions, getStrDictOptions } from '@/utils/dict'
import { AiModelTypeEnum } from '@/views/ai/utils/constants'

/** API 模型的表单 */
defineOptions({ name: 'ModelForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改
const formData = ref({
  id: undefined,
  keyId: undefined,
  name: undefined,
  model: undefined,
  platform: undefined,
  type: undefined,
  sort: undefined,
  status: CommonStatusEnum.ENABLE,
  temperature: undefined,
  maxTokens: undefined,
  maxContexts: undefined
})
const formRules = reactive({
  keyId: [{ required: true, message: 'API 秘钥不能为空', trigger: 'blur' }],
  name: [{ required: true, message: '模型名字不能为空', trigger: 'blur' }],
  model: [{ required: true, message: '模型标识不能为空', trigger: 'blur' }],
  platform: [{ required: true, message: '所属平台不能为空', trigger: 'blur' }],
  type: [{ required: true, message: '模型类型不能为空', trigger: 'blur' }],
  sort: [{ required: true, message: '排序不能为空', trigger: 'blur' }],
  status: [{ required: true, message: '状态不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref
const apiKeyList = ref([] as ApiKeyVO[]) // API 密钥列表

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
      formData.value = await ModelApi.getModel(id)
    } finally {
      formLoading.value = false
    }
  }
  // 获得下拉数据
  apiKeyList.value = await ApiKeyApi.getApiKeySimpleList()
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
    const data = formData.value as unknown as ModelVO
    if (data.type !== AiModelTypeEnum.CHAT) {
      delete data.temperature
      delete data.maxTokens
      delete data.maxContexts
    }
    if (formType.value === 'create') {
      await ModelApi.createModel(data)
      message.success(t('common.createSuccess'))
    } else {
      await ModelApi.updateModel(data)
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
    id: undefined,
    keyId: undefined,
    name: undefined,
    model: undefined,
    platform: undefined,
    type: undefined,
    sort: undefined,
    status: CommonStatusEnum.ENABLE,
    temperature: undefined,
    maxTokens: undefined,
    maxContexts: undefined
  }
  formRef.value?.resetFields()
}
</script>
