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
      <el-form-item label="公众号" prop="accountId">
        <WxAccountSelect @change="onAccountChanged" />
      </el-form-item>
      <el-form-item label="消息类型" prop="type">
        <el-select v-model="queryParams.type" class="!w-240px" placeholder="请选择消息类型">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.MP_MESSAGE_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="用户标识" prop="openid">
        <el-input
          v-model="queryParams.openid"
          :v-on="handleQuery"
          class="!w-240px"
          clearable
          placeholder="请输入用户标识"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          :default-time="['00:00:00', '23:59:59']"
          class="!w-240px"
          end-placeholder="结束日期"
          range-separator="-"
          start-placeholder="开始日期"
          style="width: 240px"
          type="daterange"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon class="mr-5px" icon="ep:search" />
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon class="mr-5px" icon="ep:refresh" />
          重置
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <MessageTable :list="list" :loading="loading" @send="handleSend" />
    <Pagination
      v-show="total > 0"
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 发送消息的弹窗 -->
  <el-dialog
    v-model="messageBox.show"
    destroy-on-close
    title="粉丝消息列表"
    width="50%"
    @click="messageBox.show = true"
  >
    <WxMsg :user-id="messageBox.userId" />
  </el-dialog>
</template>
<script lang="ts" setup>
import * as MpMessageApi from '@/api/mp/message'
import WxMsg from '@/views/mp/components/wx-msg'
import WxAccountSelect from '@/views/mp/components/wx-account-select'
import MessageTable from './MessageTable.vue'
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import { MsgType } from '@/views/mp/components/wx-msg/types'
import type { FormInstance } from 'element-plus'

defineOptions({ name: 'MpMessage' })

const loading = ref(false)
const total = ref(0) // 数据的总页数
const list = ref<any[]>([]) // 当前页的列表数据

// 搜索参数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  openid: '',
  accountId: -1,
  type: MsgType.Text,
  createTime: []
})
const queryFormRef = ref<FormInstance | null>(null) // 搜索的表单

// 消息对话框
const messageBox = reactive({
  show: false,
  userId: 0
})

/** 侦听accountId */
const onAccountChanged = (id: number) => {
  queryParams.accountId = id
  queryParams.pageNo = 1
  handleQuery()
}

/** 查询列表 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const getList = async () => {
  try {
    loading.value = true
    const data = await MpMessageApi.getMessagePage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 重置按钮操作 */
const resetQuery = async () => {
  // 暂存 accountId，并在 reset 后恢复
  const accountId = queryParams.accountId
  queryFormRef.value?.resetFields()
  queryParams.accountId = accountId
  handleQuery()
}

/** 打开消息发送窗口 */
const handleSend = async (userId: number) => {
  messageBox.userId = userId
  messageBox.show = true
}
</script>
