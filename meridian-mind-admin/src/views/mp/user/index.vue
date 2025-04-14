<template>
  <doc-alert title="公众号粉丝" url="https://doc.iocoder.cn/mp/user/" />

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
      <el-form-item label="用户标识" prop="openid">
        <el-input
          v-model="queryParams.openid"
          class="!w-240px"
          clearable
          placeholder="请输入用户标识"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input
          v-model="queryParams.nickname"
          class="!w-240px"
          clearable
          placeholder="请输入昵称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"> <Icon icon="ep:search" />搜索 </el-button>
        <el-button @click="resetQuery"> <Icon icon="ep:refresh" />重置 </el-button>
        <el-button
          v-hasPermi="['mp:user:sync']"
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
      <el-table-column align="center" label="用户标识" prop="openid" width="260" />
      <el-table-column align="center" label="昵称" prop="nickname" />
      <el-table-column align="center" label="备注" prop="remark" />
      <el-table-column align="center" label="标签" prop="tagIds" width="200">
        <template #default="scope">
          <span v-for="(tagId, index) in scope.row.tagIds" :key="index">
            <el-tag>{{ tagList.find((tag) => tag.tagId === tagId)?.name }} </el-tag>&nbsp;
          </span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="订阅状态" prop="subscribeStatus">
        <template #default="scope">
          <el-tag v-if="scope.row.subscribeStatus === 0" type="success">已订阅</el-tag>
          <el-tag v-else type="danger">未订阅</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="订阅时间"
        prop="subscribeTime"
        width="180"
      />
      <el-table-column align="center" label="操作">
        <template #default="scope">
          <el-button
            v-hasPermi="['mp:user:update']"
            link
            type="primary"
            @click="openForm(scope.row.id)"
          >
            修改
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

  <!-- 表单弹窗：修改 -->
  <UserForm ref="formRef" @success="getList" />
</template>
<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as MpUserApi from '@/api/mp/user'
import * as MpTagApi from '@/api/mp/tag'
import WxAccountSelect from '@/views/mp/components/wx-account-select'
import type { FormInstance } from 'element-plus'
import UserForm from './UserForm.vue'

defineOptions({ name: 'MpUser' })

const message = useMessage() // 消息

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref<any[]>([]) // 列表的数据

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  accountId: -1,
  openid: '',
  nickname: ''
})
const queryFormRef = ref<FormInstance | null>(null) // 搜索的表单
const tagList = ref<any[]>([]) // 公众号标签列表

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
    const data = await MpUserApi.getUserPage(queryParams)
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
  const accountId = queryParams.accountId
  queryFormRef.value?.resetFields()
  queryParams.accountId = accountId
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref<InstanceType<typeof UserForm> | null>(null)
const openForm = (id: number) => {
  formRef.value?.open(id)
}

/** 同步标签 */
const handleSync = async () => {
  try {
    await message.confirm('是否确认同步粉丝？')
    await MpUserApi.syncUser(queryParams.accountId)
    message.success('开始从微信公众号同步粉丝信息，同步需要一段时间，建议稍后再查询')
    await getList()
  } catch {}
}

/** 初始化 */
onMounted(async () => {
  tagList.value = await MpTagApi.getSimpleTagList()
})
</script>
