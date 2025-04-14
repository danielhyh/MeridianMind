<template>
  <doc-alert title="公众号标签" url="https://doc.iocoder.cn/mp/tag/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="公众号" prop="accountId">
        <WxAccountSelect @change="onAccountChanged" />
      </el-form-item>
      <el-form-item>
        <el-button
          v-hasPermi="['mp:tag:create']"
          :disabled="queryParams.accountId === 0"
          plain
          type="primary"
          @click="openForm('create')"
        >
          <Icon class="mr-5px" icon="ep:plus" /> 新增
        </el-button>
        <el-button
          v-hasPermi="['mp:tag:sync']"
          :disabled="queryParams.accountId === 0"
          plain
          type="success"
          @click="handleSync"
        >
          <Icon class="mr-5px" icon="ep:refresh" /> 同步
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column align="center" label="编号" prop="id" />
      <el-table-column align="center" label="标签名称" prop="name" />
      <el-table-column align="center" label="粉丝数" prop="count" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        prop="createTime"
        width="180"
      />
      <el-table-column align="center" label="操作">
        <template #default="scope">
          <el-button
            v-hasPermi="['mp:tag:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            修改
          </el-button>
          <el-button
            v-hasPermi="['mp:tag:delete']"
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
  <TagForm ref="formRef" @success="getList" />
</template>
<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as MpTagApi from '@/api/mp/tag'
import TagForm from './TagForm.vue'
import WxAccountSelect from '@/views/mp/components/wx-account-select'

defineOptions({ name: 'MpTag' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref<any[]>([]) // 列表的数据

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  accountId: -1
})

const formRef = ref<InstanceType<typeof TagForm> | null>(null)

/** 侦听公众号变化 **/
const onAccountChanged = (id: number) => {
  queryParams.accountId = id
  queryParams.pageNo = 1
  getList()
}

/** 查询列表 */
const getList = async () => {
  try {
    loading.value = true
    const data = await MpTagApi.getTagPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 添加/修改操作 */
const openForm = (type: 'create' | 'update', id?: number) => {
  formRef.value?.open(type, queryParams.accountId, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await MpTagApi.deleteTag(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 同步操作 */
const handleSync = async () => {
  try {
    await message.confirm('是否确认同步标签？')
    await MpTagApi.syncTag(queryParams.accountId as number)
    message.success('同步标签成功')
    await getList()
  } catch {}
}
</script>
