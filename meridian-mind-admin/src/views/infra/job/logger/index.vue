<template>
  <doc-alert title="定时任务" url="https://doc.iocoder.cn/job/" />
  <doc-alert title="异步任务" url="https://doc.iocoder.cn/async-task/" />
  <doc-alert title="消息队列" url="https://doc.iocoder.cn/message-queue/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="120px"
    >
      <el-form-item label="处理器的名字" prop="handlerName">
        <el-input
          v-model="queryParams.handlerName"
          class="!w-240px"
          clearable
          placeholder="请输入处理器的名字"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="开始执行时间" prop="beginTime">
        <el-date-picker
          v-model="queryParams.beginTime"
          class="!w-240px"
          clearable
          placeholder="选择开始执行时间"
          type="date"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item label="结束执行时间" prop="endTime">
        <el-date-picker
          v-model="queryParams.endTime"
          :default-time="new Date('1 23:59:59')"
          class="!w-240px"
          clearable
          placeholder="选择结束执行时间"
          type="date"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item label="任务状态" prop="status">
        <el-select
          v-model="queryParams.status"
          class="!w-240px"
          clearable
          placeholder="请选择任务状态"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.INFRA_JOB_LOG_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" /> 重置</el-button>
        <el-button
          v-hasPermi="['infra:job:export']"
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
      <el-table-column align="center" label="日志编号" prop="id" />
      <el-table-column align="center" label="任务编号" prop="jobId" />
      <el-table-column align="center" label="处理器的名字" prop="handlerName" />
      <el-table-column align="center" label="处理器的参数" prop="handlerParam" />
      <el-table-column align="center" label="第几次执行" prop="executeIndex" />
      <el-table-column align="center" label="执行时间" width="170s">
        <template #default="scope">
          <span>{{ formatDate(scope.row.beginTime) + ' ~ ' + formatDate(scope.row.endTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="执行时长" prop="duration">
        <template #default="scope">
          <span>{{ scope.row.duration + ' 毫秒' }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="任务状态" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.INFRA_JOB_LOG_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作">
        <template #default="scope">
          <el-button
            v-hasPermi="['infra:job:query']"
            link
            type="primary"
            @click="openDetail(scope.row.id)"
          >
            详细
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页组件 -->
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：查看 -->
  <JobLogDetail ref="detailRef" />
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import download from '@/utils/download'
import JobLogDetail from './JobLogDetail.vue'
import * as JobLogApi from '@/api/infra/jobLog'

defineOptions({ name: 'InfraJobLog' })

const message = useMessage() // 消息弹窗
const { query } = useRoute() // 查询参数

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  jobId: query.id,
  handlerName: undefined,
  beginTime: undefined,
  endTime: undefined,
  status: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await JobLogApi.getJobLogPage(queryParams)
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

/** 查看操作 */
const detailRef = ref()
const openDetail = (rowId?: number) => {
  detailRef.value.open(rowId)
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await JobLogApi.exportJobLog(queryParams)
    download.excel(data, '定时任务执行日志.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
