<template>
  <doc-alert title="【营销】砍价活动" url="https://doc.iocoder.cn/mall/promotion-bargain/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="砍价状态" prop="status">
        <el-select
          v-model="queryParams.status"
          class="!w-240px"
          clearable
          placeholder="请选择砍价状态"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.PROMOTION_BARGAIN_RECORD_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
          v-hasPermi="['promotion:bargain-record:create']"
          plain
          type="primary"
          @click="openForm('create')"
        >
          <Icon class="mr-5px" icon="ep:plus" /> 新增
        </el-button>
        <el-button
          v-hasPermi="['promotion:bargain-record:export']"
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
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column label="编号" min-width="50" prop="id" />
      <el-table-column label="发起用户" min-width="120">
        <template #default="scope">
          <el-image
            :preview-src-list="[scope.row.avatar]"
            :src="scope.row.avatar"
            class="h-20px w-20px"
            preview-teleported
          />
          {{ scope.row.nickname }}
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="发起时间"
        prop="createTime"
        width="180px"
      />
      <el-table-column label="砍价活动" min-width="150" prop="activity.name" />
      <el-table-column
        :formatter="fenToYuanFormat"
        label="最低价"
        min-width="100"
        prop="activity.bargainMinPrice"
      />
      <el-table-column
        :formatter="fenToYuanFormat"
        label="当前价"
        min-width="100"
        prop="bargainPrice"
      />
      <el-table-column label="总砍价次数" min-width="100" prop="activity.helpMaxCount" />
      <el-table-column label="剩余砍价次数" min-width="100" prop="helpCount" />
      <el-table-column align="center" label="砍价状态" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PROMOTION_BARGAIN_RECORD_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="结束时间"
        prop="endTime"
        width="180px"
      />
      <el-table-column align="center" label="订单编号" prop="orderId" />
      <el-table-column align="center" label="操作">
        <template #default="scope">
          <el-button
            v-hasPermi="['promotion:bargain-help:query']"
            link
            type="primary"
            @click="openRecordListDialog(scope.row.id)"
          >
            助力
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

  <!-- 表单弹窗 -->
  <BargainRecordListDialog ref="recordListDialogRef" />
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as BargainRecordApi from '@/api/mall/promotion/bargain/bargainRecord'
import { fenToYuanFormat } from '@/utils/formatter'
import BargainRecordListDialog from './BargainRecordListDialog.vue'

defineOptions({ name: 'PromotionBargainRecord' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  status: null,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await BargainRecordApi.getBargainRecordPage(queryParams)
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

/** 打开[助力]弹窗 */
const recordListDialogRef = ref()
const openRecordListDialog = (id?: number) => {
  recordListDialogRef.value.open(id)
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
