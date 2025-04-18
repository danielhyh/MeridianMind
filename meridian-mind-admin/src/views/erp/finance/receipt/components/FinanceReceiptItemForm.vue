<template>
  <el-form
    ref="formRef"
    v-loading="formLoading"
    :disabled="disabled"
    :inline-message="true"
    :model="formData"
    :rules="formRules"
    label-width="0px"
  >
    <el-table :data="formData" :summary-method="getSummaries" class="-mt-10px" show-summary>
      <el-table-column align="center" label="序号" type="index" width="60" />
      <el-table-column label="销售单据编号" min-width="200">
        <template #default="{ row }">
          <el-form-item class="mb-0px!">
            <el-input v-model="row.bizNo" disabled />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="应付金额" min-width="100" prop="totalPrice">
        <template #default="{ row }">
          <el-form-item class="mb-0px!">
            <el-input v-model="row.totalPrice" :formatter="erpPriceInputFormatter" disabled />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="已付金额" min-width="100" prop="receiptedPrice">
        <template #default="{ row }">
          <el-form-item class="mb-0px!">
            <el-input v-model="row.receiptedPrice" :formatter="erpPriceInputFormatter" disabled />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="本次收款" min-width="115" prop="receiptPrice">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.receiptPrice`" class="mb-0px!">
            <el-input-number
              v-model="row.receiptPrice"
              :precision="2"
              class="!w-100%"
              controls-position="right"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="备注" min-width="150">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.remark`" class="mb-0px!">
            <el-input v-model="row.remark" placeholder="请输入备注" />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="60">
        <template #default="{ $index }">
          <el-button link @click="handleDelete($index)">—</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-form>
  <el-row v-if="!disabled" class="mt-3" justify="center">
    <el-button round @click="handleOpenSaleOut">+ 添加销售出库单</el-button>
    <el-button round @click="handleOpenSaleReturn">+ 添加销售退货单</el-button>
  </el-row>

  <!-- 可收款的【销售出库单】列表 -->
  <SaleOutReceiptEnableList ref="saleOutReceiptEnableListRef" @success="handleAddSaleOut" />
  <!-- 可收款的【销售出库单】列表 -->
  <SaleReturnRefundEnableList ref="saleReturnRefundEnableListRef" @success="handleAddSaleReturn" />
</template>
<script lang="ts" setup>
import { ProductVO } from '@/api/erp/product/product'
import { erpPriceInputFormatter, getSumValue } from '@/utils'
import SaleOutReceiptEnableList from '@/views/erp/sale/out/components/SaleOutReceiptEnableList.vue'
import SaleReturnRefundEnableList from '@/views/erp/sale/return/components/SaleReturnRefundEnableList.vue'
import { SaleOutVO } from '@/api/erp/sale/out'
import { ErpBizType } from '@/utils/constants'
import { SaleReturnVO } from '@/api/erp/sale/return'

const props = defineProps<{
  items: undefined
  customerId: undefined
  disabled: false
}>()
const message = useMessage()

const formLoading = ref(false) // 表单的加载中
const formData = ref([])
const formRules = reactive({
  receiptPrice: [{ required: true, message: '本次收款不能为空', trigger: 'blur' }]
})
const formRef = ref([]) // 表单 Ref
const productList = ref<ProductVO[]>([]) // 产品列表

/** 初始化设置出库项 */
watch(
  () => props.items,
  async (val) => {
    formData.value = val
  },
  { immediate: true }
)

/** 合计 */
const getSummaries = (param: SummaryMethodProps) => {
  const { columns, data } = param
  const sums: string[] = []
  columns.forEach((column, index: number) => {
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    if (['totalPrice', 'receiptedPrice', 'receiptPrice'].includes(column.property)) {
      const sum = getSumValue(data.map((item) => Number(item[column.property])))
      sums[index] = erpPriceInputFormatter(sum)
    } else {
      sums[index] = ''
    }
  })
  return sums
}

/** 新增【销售出库】按钮操作 */
const saleOutReceiptEnableListRef = ref()
const handleOpenSaleOut = () => {
  if (!props.customerId) {
    message.error('请选择客户')
    return
  }
  saleOutReceiptEnableListRef.value.open(props.customerId)
}
const handleAddSaleOut = (rows: SaleOutVO[]) => {
  rows.forEach((row) => {
    formData.value.push({
      bizId: row.id,
      bizType: ErpBizType.SALE_OUT,
      bizNo: row.no,
      totalPrice: row.totalPrice,
      receiptedPrice: row.receiptPrice,
      receiptPrice: row.totalPrice - row.receiptPrice
    })
  })
}

/** 新增【销售退货】按钮操作 */
const saleReturnRefundEnableListRef = ref()
const handleOpenSaleReturn = () => {
  if (!props.customerId) {
    message.error('请选择客户')
    return
  }
  saleReturnRefundEnableListRef.value.open(props.customerId)
}
const handleAddSaleReturn = (rows: SaleReturnVO[]) => {
  rows.forEach((row) => {
    formData.value.push({
      bizId: row.id,
      bizType: ErpBizType.SALE_RETURN,
      bizNo: row.no,
      totalPrice: -row.totalPrice,
      receiptedPrice: -row.refundPrice,
      receiptPrice: -row.totalPrice + row.refundPrice
    })
  })
}

/** 删除按钮操作 */
const handleDelete = (index: number) => {
  formData.value.splice(index, 1)
}

/** 表单校验 */
const validate = () => {
  return formRef.value.validate()
}
defineExpose({ validate })
</script>
