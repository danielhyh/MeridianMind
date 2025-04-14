<!-- TODO @lesan：要不直接放到 workflow 根目录 -->
<template>
  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="流程标识" prop="definitionKey">
        <el-input
          v-model="queryParams.definitionKey"
          class="!w-240px"
          clearable
          placeholder="请输入流程标识"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="流程名称" prop="name">
        <el-input
          v-model="queryParams.name"
          class="!w-240px"
          clearable
          placeholder="请输入流程名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
          end-placeholder="结束日期"
          start-placeholder="开始日期"
          type="daterange"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" /> 重置</el-button>
        <el-button
          v-hasPermi="['ai:workflow:create']"
          plain
          type="primary"
          @click="openForm('create')"
        >
          <Icon icon="ep:plus" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column :show-overflow-tooltip="true" align="center" label="编号" prop="id" />
      <el-table-column
        :show-overflow-tooltip="true"
        align="center"
        label="流程标识"
        prop="definitionKey"
      />
      <el-table-column :show-overflow-tooltip="true" align="center" label="流程名称" prop="name" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        prop="createTime"
        width="180"
      />
      <el-table-column align="center" fixed="right" label="操作" width="220">
        <template #default="scope">
          <el-button
            v-hasPermi="['ai:workflow:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            修改
          </el-button>
          <el-button
            v-hasPermi="['ai:workflow:update']"
            link
            type="primary"
            @click="openModelForm('update', scope.row.id)"
          >
            流程图
          </el-button>
          <el-button
            v-hasPermi="['ai:workflow:delete']"
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

  <!-- 添加或修改工作流对话框 -->
  <WorkflowForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import * as WorkflowApi from '@/api/ai/workflow'
import { dateFormatter } from '@/utils/formatTime'
import WorkflowForm from './WorkflowForm.vue'

/** AI 绘画 列表 */
defineOptions({ name: 'AiWorkflowManager' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const { push } = useRouter() // 路由

const loading = ref(true) // 列表的加载中
const list = ref([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  definitionKey: '',
  name: '',
  createTime: []
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await WorkflowApi.getWorkflowPage(queryParams)
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

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await WorkflowApi.deleteWorkflow(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 修改流程模型弹窗 */
const openModelForm = async (type: string, id?: number) => {
  if (type === 'create') {
    await push({ name: 'AiWorkflowCreate' })
  } else {
    await push({
      name: 'AiWorkflowUpdate',
      params: { id, type }
    })
  }
}

/** 初始化 **/
onMounted(async () => {
  getList()
})
</script>
