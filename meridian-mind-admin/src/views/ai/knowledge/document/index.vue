<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="文件名称" prop="name">
        <el-input
          v-model="queryParams.name"
          class="!w-240px"
          clearable
          placeholder="请输入文件名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否启用" prop="status">
        <el-select
          v-model="queryParams.status"
          class="!w-240px"
          clearable
          placeholder="请选择是否启用"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" /> 重置</el-button>
        <el-button v-hasPermi="['ai:knowledge:create']" plain type="primary" @click="handleCreate">
          <Icon class="mr-5px" icon="ep:plus" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column align="center" label="文档编号" prop="id" />
      <el-table-column align="center" label="文件名称" prop="name" />
      <el-table-column align="center" label="字符数" prop="contentLength" />
      <el-table-column align="center" label="Token 数" prop="tokens" />
      <el-table-column align="center" label="分片最大 Token 数" prop="segmentMaxTokens" />
      <el-table-column align="center" label="召回次数" prop="retrievalCount" />
      <el-table-column align="center" label="是否启用" prop="status">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value="0"
            :disabled="!checkPermi(['ai:knowledge:update'])"
            :inactive-value="1"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="上传时间"
        prop="createTime"
        width="180px"
      />
      <el-table-column align="center" label="操作" min-width="120px">
        <template #default="scope">
          <el-button
            v-hasPermi="['ai:knowledge:update']"
            link
            type="primary"
            @click="handleUpdate(scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-hasPermi="['ai:knowledge:query']"
            link
            type="primary"
            @click="handleSegment(scope.row.id)"
          >
            分段
          </el-button>
          <el-button
            v-hasPermi="['ai:knowledge:delete']"
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
  <!-- <KnowledgeDocumentForm ref="formRef" @success="getList" /> -->
</template>

<script lang="ts" setup>
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { KnowledgeDocumentApi, KnowledgeDocumentVO } from '@/api/ai/knowledge/document'
import { useRoute, useRouter } from 'vue-router'
import { checkPermi } from '@/utils/permission'
import { CommonStatusEnum } from '@/utils/constants'
// import KnowledgeDocumentForm from './KnowledgeDocumentForm.vue'

/** AI 知识库文档 列表 */
defineOptions({ name: 'KnowledgeDocument' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const route = useRoute() // 路由
const router = useRouter() // 路由

const loading = ref(true) // 列表的加载中
const list = ref<KnowledgeDocumentVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  status: undefined,
  knowledgeId: undefined
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await KnowledgeDocumentApi.getKnowledgeDocumentPage(queryParams)
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

/** 跳转到创建文档页面 */
const handleCreate = () => {
  router.push({
    name: 'AiKnowledgeDocumentCreate',
    query: { knowledgeId: queryParams.knowledgeId }
  })
}

/** 跳转到更新文档页面 */
const handleUpdate = (id: number) => {
  router.push({
    name: 'AiKnowledgeDocumentUpdate',
    query: { id, knowledgeId: queryParams.knowledgeId }
  })
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await KnowledgeDocumentApi.deleteKnowledgeDocument(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 修改状态操作 */
const handleStatusChange = async (row: KnowledgeDocumentVO) => {
  try {
    // 修改状态的二次确认
    const text = row.status === CommonStatusEnum.ENABLE ? '启用' : '禁用'
    await message.confirm('确认要"' + text + '""' + row.name + '"文档吗?')
    // 发起修改状态
    await KnowledgeDocumentApi.updateKnowledgeDocumentStatus({ id: row.id, status: row.status })
    message.success(t('common.updateSuccess'))
    // 刷新列表
    await getList()
  } catch {
    // 取消后，进行恢复按钮
    row.status =
      row.status === CommonStatusEnum.ENABLE ? CommonStatusEnum.DISABLE : CommonStatusEnum.ENABLE
  }
}

/** 跳转到知识库分段页面 */
const handleSegment = (id: number) => {
  router.push({
    name: 'AiKnowledgeSegment',
    query: { documentId: id }
  })
}

/** 初始化 **/
onMounted(() => {
  // 如果知识库 ID 不存在，显示错误提示并关闭页面
  if (!route.query.knowledgeId) {
    message.error('知识库 ID 不存在，无法查看文档列表')
    // 关闭当前路由，返回到知识库列表页面
    router.push({ name: 'AiKnowledge' })
    return
  }

  // 从路由参数中获取知识库 ID
  queryParams.knowledgeId = route.query.knowledgeId as any
  getList()
})
</script>
