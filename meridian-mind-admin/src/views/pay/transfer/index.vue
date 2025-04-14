<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="100px"
    >
      <el-form-item label="转账单号" prop="no">
        <el-input
          v-model="queryParams.no"
          class="!w-240px"
          clearable
          placeholder="请输入转账单号"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="转账渠道" prop="channelCode">
        <el-select
          v-model="queryParams.channelCode"
          class="!w-240px"
          clearable
          placeholder="请选择支付渠道"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.PAY_CHANNEL_CODE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="商户单号" prop="merchantTransferId">
        <el-input
          v-model="queryParams.merchantTransferId"
          class="!w-240px"
          clearable
          placeholder="请输入商户单号"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" class="!w-240px" clearable placeholder="请选择类型">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.PAY_TRANSFER_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="转账状态" prop="status">
        <el-select
          v-model="queryParams.status"
          class="!w-240px"
          clearable
          placeholder="请选择转账状态"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.PAY_TRANSFER_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="收款人姓名" prop="userName">
        <el-input
          v-model="queryParams.userName"
          class="!w-240px"
          clearable
          placeholder="请输入收款人姓名"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="渠道单号" prop="channelTransferNo">
        <el-input
          v-model="queryParams.channelTransferNo"
          class="!w-240px"
          clearable
          placeholder="渠道单号"
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
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column align="center" label="编号" prop="id" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        prop="createTime"
        width="180px"
      />
      <el-table-column align="center" label="应用编号" prop="appId" />
      <el-table-column align="center" label="类型" prop="type" width="120">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PAY_TRANSFER_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="转账金额" prop="price">
        <template #default="scope">
          <span>￥{{ (scope.row.price / 100.0).toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="转账状态" prop="status" width="120">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PAY_TRANSFER_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column align="left" label="订单号" width="300">
        <template #default="scope">
          <p class="transfer-font">
            <el-tag size="small"> 商户</el-tag>
            {{ scope.row.merchantTransferId }}
          </p>
          <p v-if="scope.row.no" class="transfer-font">
            <el-tag size="small" type="warning">转账</el-tag>
            {{ scope.row.no }}
          </p>
          <p v-if="scope.row.channelTransferNo" class="transfer-font">
            <el-tag size="small" type="success">渠道</el-tag>
            {{ scope.row.channelTransferNo }}
          </p>
        </template>
      </el-table-column>
      <el-table-column align="center" label="收款人姓名" prop="userName" width="120" />
      <el-table-column align="left" label="收款账号" width="200">
        <template #default="scope">
          <p v-if="scope.row.alipayLogonId" class="transfer-font">
            <el-tag size="small">支付宝登录号</el-tag>
            {{ scope.row.alipayLogonId }}
          </p>
          <p v-if="scope.row.openid" class="transfer-font">
            <el-tag size="small">微信 openId</el-tag>
            {{ scope.row.openid }}
          </p>
        </template>
      </el-table-column>
      <el-table-column align="center" label="转账标题" prop="subject" width="120" />
      <el-table-column align="center" label="转账渠道" prop="channelCode">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PAY_CHANNEL_CODE" :value="scope.row.channelCode" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="转账成功时间"
        prop="successTime"
        width="180px"
      />
      <el-table-column align="center" fixed="right" label="操作">
        <template #default="scope">
          <el-button link type="primary" @click="openDetail(scope.row.id)"> 详情 </el-button>
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
    <TransferDetail ref="detailRef" />
  </ContentWrap>
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as TransferApi from '@/api/pay/transfer'
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'
import TransferDetail from './TransferDetail.vue'
defineOptions({ name: 'PayTransfer' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  no: null,
  appId: null,
  channelId: null,
  channelCode: null,
  merchantTransferId: null,
  type: null,
  status: null,
  successTime: [],
  price: null,
  subject: null,
  userName: null,
  alipayLogonId: null,
  openid: null,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await TransferApi.getTransferPage(queryParams)
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
const detailRef = ref()
const openDetail = (id: number) => {
  detailRef.value.open(id)
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

<style scoped>
.transfer-font {
  padding: 2px 0;
  font-size: 12px;
}
</style>
