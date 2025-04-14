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
      <el-table-column label="采购单据编号" min-width="200">
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
      <el-table-column fixed="right" label="已付金额" min-width="100" prop="paidPrice">
        <template #default="{ row }">
          <el-form-item class="mb-0px!">
            <el-input v-model="row.paidPrice" :formatter="erpPriceInputFormatter" disabled />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="本次付款" min-width="115" prop="paymentPrice">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.paymentPrice`" class="mb-0px!">
            <el-input-number
              v-model="row.paymentPrice"
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
    <el-button round @click="handleOpenPurchaseIn">+ 添加采购入库单</el-button>
    <el-button round @click="handleOpenPurchaseReturn">+ 添加采购退货单</el-button>
  </el-row>

  <!-- 可付款的【采购入库单】列表 -->
  <PurchaseInPaymentEnableList
    ref="purchaseInPaymentEnableListRef"
    @success="handleAddPurchaseIn"
  />
  <!-- 可付款的【采购入库单】列表 -->
  <PurchaseReturnRefundEnableList
    ref="purchaseReturnRefundEnableListRef"
    @success="handleAddPurchaseReturn"
  />
</template>
<script lang="ts" setup>
import { ProductVO } from '@/api/erp/product/product'
import { erpPriceInputFormatter, getSumValue } from '@/utils'
import PurchaseInPaymentEnableList from '@/views/erp/purchase/in/components/PurchaseInPaymentEnableList.vue'
import PurchaseReturnRefundEnableList from '@/views/erp/purchase/return/components/PurchaseReturnRefundEnableList.vue'
import { PurchaseInVO } from '@/api/erp/purchase/in'
import { ErpBizType } from '@/utils/constants'
import { PurchaseReturnVO } from '@/api/erp/purchase/return'

const props = defineProps<{
  items: undefined
  supplierId: undefined
  disabled: false
}>()
const message = useMessage()

const formLoading = ref(false) // 表单的加载中
const formData = ref([])
const formRules = reactive({
  paymentPrice: [{ required: true, message: '本次付款不能为空', trigger: 'blur' }]
})
const formRef = ref([]) // 表单 Ref
const productList = ref<ProductVO[]>([]) // 产品列表

/** 初始化设置入库项 */
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
    if (['totalPrice', 'paidPrice', 'paymentPrice'].includes(column.property)) {
      const sum = getSumValue(data.map((item) => Number(item[column.property])))
      sums[index] = erpPriceInputFormatter(sum)
    } else {
      sums[index] = ''
    }
  })
  return sums
}

/** 新增【采购入库】按钮操作 */
const purchaseInPaymentEnableListRef = ref()
const handleOpenPurchaseIn = () => {
  if (!props.supplierId) {
    message.error('请选择供应商')
    return
  }
  purchaseInPaymentEnableListRef.value.open(props.supplierId)
}
const handleAddPurchaseIn = (rows: PurchaseInVO[]) => {
  rows.forEach((row) => {
    formData.value.push({
      bizId: row.id,
      bizType: ErpBizType.PURCHASE_IN,
      bizNo: row.no,
      totalPrice: row.totalPrice,
      paidPrice: row.paymentPrice,
      paymentPrice: row.totalPrice - row.paymentPrice
    })
  })
}

/** 新增【采购退货】按钮操作 */
const purchaseReturnRefundEnableListRef = ref()
const handleOpenPurchaseReturn = () => {
  if (!props.supplierId) {
    message.error('请选择供应商')
    return
  }
  purchaseReturnRefundEnableListRef.value.open(props.supplierId)
}
const handleAddPurchaseReturn = (rows: PurchaseReturnVO[]) => {
  rows.forEach((row) => {
    formData.value.push({
      bizId: row.id,
      bizType: ErpBizType.PURCHASE_RETURN,
      bizNo: row.no,
      totalPrice: -row.totalPrice,
      paidPrice: -row.refundPrice,
      paymentPrice: -row.totalPrice + row.refundPrice
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
