<template>
  <doc-alert
    title="【财务】采购付款、销售收款"
    url="https://doc.iocoder.cn/sale/finance-payment-receipt/"
  />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="付款单号" prop="no">
        <el-input
          v-model="queryParams.no"
          class="!w-240px"
          clearable
          placeholder="请输入付款单号"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="付款时间" prop="paymentTime">
        <el-date-picker
          v-model="queryParams.paymentTime"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
          end-placeholder="结束日期"
          start-placeholder="开始日期"
          type="daterange"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item label="供应商" prop="supplierId">
        <el-select
          v-model="queryParams.supplierId"
          class="!w-240px"
          clearable
          filterable
          placeholder="请选择供供应商"
        >
          <el-option
            v-for="item in supplierList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建人" prop="creator">
        <el-select
          v-model="queryParams.creator"
          class="!w-240px"
          clearable
          filterable
          placeholder="请选择创建人"
        >
          <el-option
            v-for="item in userList"
            :key="item.id"
            :label="item.nickname"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="财务人员" prop="financeUserId">
        <el-select
          v-model="queryParams.financeUserId"
          class="!w-240px"
          clearable
          filterable
          placeholder="请选择财务人员"
        >
          <el-option
            v-for="item in userList"
            :key="item.id"
            :label="item.nickname"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="付款账户" prop="accountId">
        <el-select
          v-model="queryParams.accountId"
          class="!w-240px"
          clearable
          filterable
          placeholder="请选择付款账户"
        >
          <el-option
            v-for="item in accountList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" class="!w-240px" clearable placeholder="请选择状态">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.ERP_AUDIT_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="queryParams.remark"
          class="!w-240px"
          clearable
          placeholder="请输入备注"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="采购单号" prop="bizNo">
        <el-input
          v-model="queryParams.bizNo"
          class="!w-240px"
          clearable
          placeholder="请输入采购单号"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" /> 重置</el-button>
        <el-button
          v-hasPermi="['erp:finance-payment:create']"
          plain
          type="primary"
          @click="openForm('create')"
        >
          <Icon class="mr-5px" icon="ep:plus" /> 新增
        </el-button>
        <el-button
          v-hasPermi="['erp:finance-payment:export']"
          :loading="exportLoading"
          plain
          type="success"
          @click="handleExport"
        >
          <Icon class="mr-5px" icon="ep:download" /> 导出
        </el-button>
        <el-button
          v-hasPermi="['erp:finance-payment:delete']"
          :disabled="selectionList.length === 0"
          plain
          type="danger"
          @click="handleDelete(selectionList.map((item) => item.id))"
        >
          <Icon class="mr-5px" icon="ep:delete" /> 删除
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table
      v-loading="loading"
      :data="list"
      :show-overflow-tooltip="true"
      :stripe="true"
      @selection-change="handleSelectionChange"
    >
      <el-table-column label="选择" type="selection" width="30" />
      <el-table-column align="center" label="付款单号" min-width="180" prop="no" />
      <el-table-column align="center" label="供应商" prop="supplierName" />
      <el-table-column
        :formatter="dateFormatter2"
        align="center"
        label="付款时间"
        prop="paymentTime"
        width="120px"
      />
      <el-table-column align="center" label="创建人" prop="creatorName" />
      <el-table-column align="center" label="财务人员" prop="financeUserName" />
      <el-table-column align="center" label="付款账户" prop="accountName" />
      <el-table-column
        :formatter="erpPriceTableColumnFormatter"
        align="center"
        label="合计付款"
        prop="totalPrice"
      />
      <el-table-column
        :formatter="erpPriceTableColumnFormatter"
        align="center"
        label="优惠金额"
        prop="discountPrice"
      />
      <el-table-column
        :formatter="erpPriceTableColumnFormatter"
        align="center"
        label="实际付款"
        prop="paymentPrice"
      />
      <el-table-column align="center" fixed="right" label="状态" prop="status" width="90">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.ERP_AUDIT_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="220">
        <template #default="scope">
          <el-button
            v-hasPermi="['erp:finance-payment:query']"
            link
            @click="openForm('detail', scope.row.id)"
          >
            详情
          </el-button>
          <el-button
            v-hasPermi="['erp:finance-payment:update']"
            :disabled="scope.row.status === 20"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-if="scope.row.status === 10"
            v-hasPermi="['erp:finance-payment:update-status']"
            link
            type="primary"
            @click="handleUpdateStatus(scope.row.id, 20)"
          >
            审批
          </el-button>
          <el-button
            v-else
            v-hasPermi="['erp:finance-payment:update-status']"
            link
            type="danger"
            @click="handleUpdateStatus(scope.row.id, 10)"
          >
            反审批
          </el-button>
          <el-button
            v-hasPermi="['erp:finance-payment:delete']"
            link
            type="danger"
            @click="handleDelete([scope.row.id])"
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
  <FinancePaymentForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { getIntDictOptions, DICT_TYPE } from '@/utils/dict'
import { dateFormatter2 } from '@/utils/formatTime'
import download from '@/utils/download'
import { FinancePaymentApi, FinancePaymentVO } from '@/api/erp/finance/payment'
import FinancePaymentForm from './FinancePaymentForm.vue'
import { UserVO } from '@/api/system/user'
import * as UserApi from '@/api/system/user'
import { erpPriceTableColumnFormatter } from '@/utils'
import { SupplierApi, SupplierVO } from '@/api/erp/purchase/supplier'
import { AccountApi, AccountVO } from '@/api/erp/finance/account'

/** ERP 付款单列表 */
defineOptions({ name: 'ErpPurchaseOrder' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<FinancePaymentVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  no: undefined,
  paymentTime: [],
  supplierId: undefined,
  creator: undefined,
  financeUserId: undefined,
  accountId: undefined,
  status: undefined,
  remark: undefined,
  bizNo: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中
const supplierList = ref<SupplierVO[]>([]) // 供应商列表
const userList = ref<UserVO[]>([]) // 用户列表
const accountList = ref<AccountVO[]>([]) // 账户列表

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await FinancePaymentApi.getFinancePaymentPage(queryParams)
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
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (ids: number[]) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await FinancePaymentApi.deleteFinancePayment(ids)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
    selectionList.value = selectionList.value.filter((item) => !ids.includes(item.id))
  } catch {}
}

/** 审批/反审批操作 */
const handleUpdateStatus = async (id: number, status: number) => {
  try {
    // 审批的二次确认
    await message.confirm(`确定${status === 20 ? '审批' : '反审批'}该付款单吗？`)
    // 发起审批
    await FinancePaymentApi.updateFinancePaymentStatus(id, status)
    message.success(`${status === 20 ? '审批' : '反审批'}成功`)
    // 刷新列表
    await getList()
  } catch {}
}

/** 导出按钮操作 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await FinancePaymentApi.exportFinancePayment(queryParams)
    download.excel(data, '付款单.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 选中操作 */
const selectionList = ref<FinancePaymentVO[]>([])
const handleSelectionChange = (rows: FinancePaymentVO[]) => {
  selectionList.value = rows
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  // 加载供应商、用户、账户
  supplierList.value = await SupplierApi.getSupplierSimpleList()
  userList.value = await UserApi.getSimpleUserList()
  accountList.value = await AccountApi.getAccountSimpleList()
})
// TODO 芋艿：可优化功能：列表界面，支持导入
// TODO 芋艿：可优化功能：详情界面，支持打印
</script>
