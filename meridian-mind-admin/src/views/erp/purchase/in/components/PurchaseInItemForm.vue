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
      <el-table-column label="仓库名称" min-width="125">
        <template #default="{ row, $index }">
          <el-form-item
            :prop="`${$index}.warehouseId`"
            :rules="formRules.warehouseId"
            class="mb-0px!"
          >
            <el-select
              v-model="row.warehouseId"
              clearable
              filterable
              placeholder="请选择仓库"
              @change="onChangeWarehouse($event, row)"
            >
              <el-option
                v-for="item in warehouseList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="产品名称" min-width="180">
        <template #default="{ row }">
          <el-form-item class="mb-0px!">
            <el-input v-model="row.productName" disabled />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="库存" min-width="100">
        <template #default="{ row }">
          <el-form-item class="mb-0px!">
            <el-input v-model="row.stockCount" :formatter="erpCountInputFormatter" disabled />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="条码" min-width="150">
        <template #default="{ row }">
          <el-form-item class="mb-0px!">
            <el-input v-model="row.productBarCode" disabled />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="单位" min-width="80">
        <template #default="{ row }">
          <el-form-item class="mb-0px!">
            <el-input v-model="row.productUnitName" disabled />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        v-if="formData[0]?.totalCount != null"
        fixed="right"
        label="原数量"
        min-width="80"
      >
        <template #default="{ row }">
          <el-form-item class="mb-0px!">
            <el-input v-model="row.totalCount" :formatter="erpCountInputFormatter" disabled />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column
        v-if="formData[0]?.inCount != null"
        fixed="right"
        label="已入库"
        min-width="80"
      >
        <template #default="{ row }">
          <el-form-item class="mb-0px!">
            <el-input v-model="row.inCount" :formatter="erpCountInputFormatter" disabled />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="数量" min-width="140" prop="count">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.count`" :rules="formRules.count" class="mb-0px!">
            <el-input-number
              v-model="row.count"
              :min="0.001"
              :precision="3"
              class="!w-100%"
              controls-position="right"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="产品单价" min-width="120">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.productPrice`" class="mb-0px!">
            <el-input-number
              v-model="row.productPrice"
              :min="0.01"
              :precision="2"
              class="!w-100%"
              controls-position="right"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="金额" min-width="100" prop="totalProductPrice">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.totalProductPrice`" class="mb-0px!">
            <el-input
              v-model="row.totalProductPrice"
              :formatter="erpPriceInputFormatter"
              disabled
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="税率（%）" min-width="115">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.taxPercent`" class="mb-0px!">
            <el-input-number
              v-model="row.taxPercent"
              :min="0"
              :precision="2"
              class="!w-100%"
              controls-position="right"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="税额" min-width="120" prop="taxPrice">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.taxPrice`" class="mb-0px!">
            <el-form-item :prop="`${$index}.taxPrice`" class="mb-0px!">
              <el-input v-model="row.taxPrice" :formatter="erpPriceInputFormatter" disabled />
            </el-form-item>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="税额合计" min-width="100" prop="totalPrice">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.totalPrice`" class="mb-0px!">
            <el-input v-model="row.totalPrice" :formatter="erpPriceInputFormatter" disabled />
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
          <el-button :disabled="formData.length === 1" link @click="handleDelete($index)">
            —
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-form>
</template>
<script lang="ts" setup>
import { StockApi } from '@/api/erp/stock/stock'
import {
  erpCountInputFormatter,
  erpPriceInputFormatter,
  erpPriceMultiply,
  getSumValue
} from '@/utils'
import { WarehouseApi, WarehouseVO } from '@/api/erp/stock/warehouse'

const props = defineProps<{
  items: undefined
  disabled: false
}>()
const formLoading = ref(false) // 表单的加载中
const formData = ref([])
const formRules = reactive({
  warehouseId: [{ required: true, message: '仓库不能为空', trigger: 'blur' }],
  productId: [{ required: true, message: '产品不能为空', trigger: 'blur' }],
  count: [{ required: true, message: '产品数量不能为空', trigger: 'blur' }]
})
const formRef = ref([]) // 表单 Ref
const warehouseList = ref<WarehouseVO[]>([]) // 仓库列表
const defaultWarehouse = ref<WarehouseVO>(undefined) // 默认仓库

/** 初始化设置入库项 */
watch(
  () => props.items,
  async (val) => {
    val.forEach((item) => {
      if (item.warehouseId == null) {
        item.warehouseId = defaultWarehouse.value?.id
      }
      if (item.stockCount === null && item.warehouseId != null) {
        setStockCount(item)
      }
    })
    formData.value = val
  },
  { immediate: true }
)

/** 监听合同产品变化，计算合同产品总价 */
watch(
  () => formData.value,
  (val) => {
    if (!val || val.length === 0) {
      return
    }
    // 循环处理
    val.forEach((item) => {
      item.totalProductPrice = erpPriceMultiply(item.productPrice, item.count)
      item.taxPrice = erpPriceMultiply(item.totalProductPrice, item.taxPercent / 100.0)
      if (item.totalProductPrice != null) {
        item.totalPrice = item.totalProductPrice + (item.taxPrice || 0)
      } else {
        item.totalPrice = undefined
      }
    })
  },
  { deep: true }
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
    if (['count', 'totalProductPrice', 'taxPrice', 'totalPrice'].includes(column.property)) {
      const sum = getSumValue(data.map((item) => Number(item[column.property])))
      sums[index] =
        column.property === 'count' ? erpCountInputFormatter(sum) : erpPriceInputFormatter(sum)
    } else {
      sums[index] = ''
    }
  })

  return sums
}

/** 新增按钮操作 */
const handleAdd = () => {
  const row = {
    id: undefined,
    productId: undefined,
    productUnitName: undefined, // 产品单位
    productBarCode: undefined, // 产品条码
    productPrice: undefined,
    stockCount: undefined,
    count: 1,
    totalProductPrice: undefined,
    taxPercent: undefined,
    taxPrice: undefined,
    totalPrice: undefined,
    remark: undefined
  }
  formData.value.push(row)
}

/** 删除按钮操作 */
const handleDelete = (index: number) => {
  formData.value.splice(index, 1)
}

/** 加载库存 */
const setStockCount = async (row: any) => {
  if (!row.productId) {
    return
  }
  const count = await StockApi.getStockCount(row.productId)
  row.stockCount = count || 0
}

/** 表单校验 */
const validate = () => {
  return formRef.value.validate()
}
defineExpose({ validate })

/** 初始化 */
onMounted(async () => {
  warehouseList.value = await WarehouseApi.getWarehouseSimpleList()
  defaultWarehouse.value = warehouseList.value.find((item) => item.defaultStatus)
})
</script>
