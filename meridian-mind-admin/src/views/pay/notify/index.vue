<template>
  <doc-alert title="支付功能开启" url="https://doc.iocoder.cn/pay/build/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="100px"
    >
      <el-form-item label="应用编号" prop="appId">
        <el-select
          v-model="queryParams.appId"
          class="!w-240px"
          clearable
          filterable
          placeholder="请选择应用信息"
        >
          <el-option v-for="item in appList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="通知类型" prop="type">
        <el-select
          v-model="queryParams.type"
          class="!w-240px"
          clearable
          placeholder="请选择通知类型"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.PAY_NOTIFY_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="关联编号" prop="dataId">
        <el-input
          v-model="queryParams.dataId"
          class="!w-240px"
          clearable
          placeholder="请输入关联编号"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="通知状态" prop="status">
        <el-select
          v-model="queryParams.status"
          class="!w-240px"
          clearable
          placeholder="请选择通知状态"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.PAY_NOTIFY_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="商户订单编号" prop="merchantOrderId">
        <el-input
          v-model="queryParams.merchantOrderId"
          class="!w-240px"
          clearable
          placeholder="请输入商户订单编号"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
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
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column align="center" label="任务编号" prop="id" />
      <el-table-column align="center" label="应用编号" prop="appName" />
      <el-table-column align="center" label="商户订单编号" prop="merchantOrderId" />
      <el-table-column align="center" label="通知类型" prop="type">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PAY_NOTIFY_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="关联编号" prop="dataId" />
      <el-table-column align="center" label="通知状态" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PAY_NOTIFY_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="最后通知时间"
        prop="lastExecuteTime"
        width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="下次通知时间"
        prop="nextNotifyTime"
        width="180"
      />
      <el-table-column align="center" label="通知次数" prop="notifyTimes">
        <template #default="scope">
          <el-tag size="small" type="success">
            {{ scope.row.notifyTimes }} / {{ scope.row.maxNotifyTimes }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template #default="scope">
          <el-button
            v-hasPermi="['pay:notify:query']"
            link
            type="primary"
            @click="openDetail(scope.row.id)"
          >
            查看详情
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

  <!-- 表单弹窗：预览 -->
  <NotifyDetail ref="detailRef" />
</template>

<script lang="ts" setup>
import * as PayNotifyApi from '@/api/pay/notify'
import * as PayAppApi from '@/api/pay/app'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import NotifyDetail from './NotifyDetail.vue'

defineOptions({ name: 'PayNotify' })

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref() // 列表的数据
const queryParams = ref({
  pageNo: 1,
  pageSize: 10,
  appId: null,
  type: null,
  dataId: null,
  status: null,
  merchantOrderId: null,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const appList = ref([]) // 支付应用列表集合
// 是否显示弹出层
const open = ref(false)
// 通知详情
const notifyDetail = ref<any>({
  logs: []
})

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNo = 1
  getList()
}

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await PayNotifyApi.getNotifyTaskPage(queryParams.value)
    list.value = data.list
    total.value = data.total
    loading.value = false
  } finally {
    loading.value = false
  }
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}

/** 详情按钮操作 */
const detailRef = ref()
const openDetail = (id: number) => {
  detailRef.value.open(id)
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  // 获得筛选项
  appList.value = await PayAppApi.getAppList()
})
</script>
