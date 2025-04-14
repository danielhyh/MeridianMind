<template>
  <doc-alert title="系统日志" url="https://doc.iocoder.cn/system-log/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="操作人" prop="userId">
        <el-select
          v-model="queryParams.userId"
          class="!w-240px"
          clearable
          filterable
          placeholder="请输入操作人员"
        >
          <el-option
            v-for="user in userList"
            :key="user.id"
            :label="user.nickname"
            :value="user.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="操作模块" prop="type">
        <el-input
          v-model="queryParams.type"
          class="!w-240px"
          clearable
          placeholder="请输入操作模块"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作名" prop="subType">
        <el-input
          v-model="queryParams.subType"
          class="!w-240px"
          clearable
          placeholder="请输入操作名"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作内容" prop="action">
        <el-input
          v-model="queryParams.action"
          class="!w-240px"
          clearable
          placeholder="请输入操作名"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作时间" prop="createTime">
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
      <el-form-item label="业务编号" prop="bizId">
        <el-input
          v-model="queryParams.bizId"
          class="!w-240px"
          clearable
          placeholder="请输入业务编号"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" /> 重置</el-button>
        <el-button
          v-hasPermi="['system:operate-log:export']"
          :loading="exportLoading"
          plain
          type="success"
          @click="handleExport"
        >
          <Icon class="mr-5px" icon="ep:download" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column align="center" label="日志编号" prop="id" width="100" />
      <el-table-column align="center" label="操作人" prop="userName" width="120" />
      <el-table-column align="center" label="操作模块" prop="type" width="120" />
      <el-table-column align="center" label="操作名" prop="subType" width="160" />
      <el-table-column align="center" label="操作内容" prop="action" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="操作时间"
        prop="createTime"
        width="180"
      />
      <el-table-column align="center" label="业务编号" prop="bizId" width="120" />
      <el-table-column align="center" label="操作 IP" prop="userIp" width="120" />
      <el-table-column align="center" fixed="right" label="操作" width="60">
        <template #default="scope">
          <el-button
            v-hasPermi="['system:operate-log:query']"
            link
            type="primary"
            @click="openDetail(scope.row)"
          >
            详情
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

  <!-- 表单弹窗：详情 -->
  <OperateLogDetail ref="detailRef" />
</template>
<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import * as OperateLogApi from '@/api/system/operatelog'
import OperateLogDetail from './OperateLogDetail.vue'
import * as UserApi from '@/api/system/user'
const userList = ref<UserApi.UserVO[]>([]) // 用户列表

defineOptions({ name: 'SystemOperateLog' })

const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: undefined,
  type: undefined,
  subType: undefined,
  action: undefined,
  createTime: [],
  bizId: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await OperateLogApi.getOperateLogPage(queryParams)
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

/** 详情操作 */
const detailRef = ref()
const openDetail = (data: OperateLogApi.OperateLogVO) => {
  detailRef.value.open(data)
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await OperateLogApi.exportOperateLog(queryParams)
    download.excel(data, '操作日志.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  // 获得用户列表
  userList.value = await UserApi.getSimpleUserList()
})
</script>
