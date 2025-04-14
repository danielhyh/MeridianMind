<template>
  <doc-alert title="【交易】分销返佣" url="https://doc.iocoder.cn/mall/trade-brokerage/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="用户编号" prop="userId">
        <el-input
          v-model="queryParams.userId"
          class="!w-240px"
          clearable
          placeholder="请输入用户编号"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="提现类型" prop="type">
        <el-select
          v-model="queryParams.type"
          class="!w-240px"
          clearable
          placeholder="请选择提现类型"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BROKERAGE_WITHDRAW_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="账号" prop="accountNo">
        <el-input
          v-model="queryParams.accountNo"
          class="!w-240px"
          clearable
          placeholder="请输入账号"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="提现银行" prop="bankName">
        <el-select
          v-model="queryParams.bankName"
          class="!w-240px"
          clearable
          placeholder="请选择提现银行"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.BROKERAGE_BANK_NAME)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" class="!w-240px" clearable placeholder="请选择状态">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BROKERAGE_WITHDRAW_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="申请时间" prop="createTime">
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
      <el-table-column align="left" label="编号" min-width="60px" prop="id" />
      <el-table-column align="left" label="用户信息" min-width="120px">
        <template #default="scope">
          <div>编号：{{ scope.row.userId }}</div>
          <div>昵称：{{ scope.row.userNickname }}</div>
        </template>
      </el-table-column>
      <el-table-column align="left" label="提现金额" min-width="80px" prop="price">
        <template #default="scope">
          <div>金　额：￥{{ fenToYuan(scope.row.price) }}</div>
          <div>手续费：￥{{ fenToYuan(scope.row.feePrice) }}</div>
        </template>
      </el-table-column>
      <el-table-column align="left" label="提现方式" min-width="120px" prop="type">
        <template #default="scope">
          <div v-if="scope.row.type === BrokerageWithdrawTypeEnum.WALLET.type"> 余额 </div>
          <div v-else>
            {{ getDictLabel(DICT_TYPE.BROKERAGE_WITHDRAW_TYPE, scope.row.type) }}
            <span v-if="scope.row.accountNo">账号：{{ scope.row.accountNo }}</span>
          </div>
          <template v-if="scope.row.type === BrokerageWithdrawTypeEnum.BANK.type">
            <div>真实姓名：{{ scope.row.name }}</div>
            <div>
              银行名称：
              <dict-tag :type="DICT_TYPE.BROKERAGE_BANK_NAME" :value="scope.row.bankName" />
            </div>
            <div>开户地址：{{ scope.row.bankAddress }}</div>
          </template>
        </template>
      </el-table-column>
      <el-table-column align="left" label="收款码" min-width="70px" prop="accountQrCodeUrl">
        <template #default="scope">
          <el-image
            v-if="scope.row.accountQrCodeUrl"
            :preview-src-list="[scope.row.accountQrCodeUrl]"
            :src="scope.row.accountQrCodeUrl"
            class="h-40px w-40px"
            preview-teleported
          />
          <span v-else>无</span>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="left"
        label="申请时间"
        prop="createTime"
        width="180px"
      />
      <el-table-column align="left" label="备注" prop="remark" />
      <el-table-column align="left" label="状态" min-width="120px" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BROKERAGE_WITHDRAW_STATUS" :value="scope.row.status" />
          <div v-if="scope.row.auditTime" class="text-xs">
            时间：{{ formatDate(scope.row.auditTime) }}
          </div>
          <div v-if="scope.row.auditReason" class="text-xs">
            原因：{{ scope.row.auditReason }}
          </div>
        </template>
      </el-table-column>
      <el-table-column align="left" fixed="right" label="操作" width="110px">
        <template #default="scope">
          <template v-if="scope.row.status === BrokerageWithdrawStatusEnum.AUDITING.status">
            <el-button
              v-hasPermi="['trade:brokerage-withdraw:audit']"
              link
              type="primary"
              @click="handleApprove(scope.row.id)"
            >
              通过
            </el-button>
            <el-button
              v-hasPermi="['trade:brokerage-withdraw:audit']"
              link
              type="danger"
              @click="openForm(scope.row.id)"
            >
              驳回
            </el-button>
          </template>
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
  <BrokerageWithdrawRejectForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { DICT_TYPE, getDictLabel, getIntDictOptions, getStrDictOptions } from '@/utils/dict'
import { dateFormatter, formatDate } from '@/utils/formatTime'
import * as BrokerageWithdrawApi from '@/api/mall/trade/brokerage/withdraw'
import BrokerageWithdrawRejectForm from './BrokerageWithdrawRejectForm.vue'
import { BrokerageWithdrawStatusEnum, BrokerageWithdrawTypeEnum } from '@/utils/constants'
import { fenToYuanFormat } from '@/utils/formatter'
import { fenToYuan } from '@/utils'

defineOptions({ name: 'BrokerageWithdraw' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗
const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: null,
  type: null,
  name: null,
  accountNo: null,
  bankName: null,
  status: null,
  auditReason: null,
  auditTime: [],
  remark: null,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await BrokerageWithdrawApi.getBrokerageWithdrawPage(queryParams)
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
const formRef = ref()
const openForm = (id: number) => {
  formRef.value.open(id)
}

/** 审核通过 */
const handleApprove = async (id: number) => {
  try {
    loading.value = true
    await message.confirm('确定要审核通过吗？')
    await BrokerageWithdrawApi.approveBrokerageWithdraw(id)
    await message.success(t('common.success'))
    await getList()
  } finally {
    loading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
