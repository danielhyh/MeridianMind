<template>
  <doc-alert title="AI 手册" url="https://doc.iocoder.cn/ai/build/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="模型名字" prop="name">
        <el-input
          v-model="queryParams.name"
          class="!w-240px"
          clearable
          placeholder="请输入模型名字"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="模型标识" prop="model">
        <el-input
          v-model="queryParams.model"
          class="!w-240px"
          clearable
          placeholder="请输入模型标识"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="模型平台" prop="platform">
        <el-input
          v-model="queryParams.platform"
          class="!w-240px"
          clearable
          placeholder="请输入模型平台"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" /> 重置</el-button>
        <el-button
          v-hasPermi="['ai:model:create']"
          plain
          type="primary"
          @click="openForm('create')"
        >
          <Icon class="mr-5px" icon="ep:plus" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column align="center" label="所属平台" min-width="100" prop="platform">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AI_PLATFORM" :value="scope.row.platform" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="模型类型" min-width="100" prop="platform">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AI_MODEL_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="模型名字" min-width="180" prop="name" />
      <el-table-column align="center" label="模型标识" min-width="180" prop="model" />
      <el-table-column align="center" label="API 秘钥" min-width="140" prop="keyId">
        <template #default="scope">
          <span>{{ apiKeyList.find((item) => item.id === scope.row.keyId)?.name }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="排序" min-width="80" prop="sort" />
      <el-table-column align="center" label="状态" min-width="80" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="温度参数" min-width="80" prop="temperature" />
      <el-table-column align="center" label="回复数 Token 数" min-width="140" prop="maxTokens" />
      <el-table-column align="center" label="上下文数量" min-width="100" prop="maxContexts" />
      <el-table-column align="center" fixed="right" label="操作" width="180">
        <template #default="scope">
          <el-button
            v-hasPermi="['ai:model:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-hasPermi="['ai:model:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <ModelForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { ModelApi, ModelVO } from '@/api/ai/model/model'
import ModelForm from './ModelForm.vue'
import { DICT_TYPE } from '@/utils/dict'
import { ApiKeyApi, ApiKeyVO } from '@/api/ai/model/apiKey'

/** API 模型列表 */
defineOptions({ name: 'AiModel' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<ModelVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  model: undefined,
  platform: undefined
})
const queryFormRef = ref() // 搜索的表单
const apiKeyList = ref([] as ApiKeyVO[]) // API 密钥列表

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ModelApi.getModelPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await ModelApi.deleteModel(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  // 获得下拉数据
  apiKeyList.value = await ApiKeyApi.getApiKeySimpleList()
})
</script>
