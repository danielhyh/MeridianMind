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
      <el-table-column label="仓库名字" min-width="125">
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
              placeholder="请选择仓库名字"
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
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.productId`" :rules="formRules.productId" class="mb-0px!">
            <el-select
              v-model="row.productId"
              clearable
              filterable
              placeholder="请选择产品"
              @change="onChangeProduct($event, row)"
            >
              <el-option
                v-for="item in productList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="账面库存" min-width="100">
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
      <el-table-column fixed="right" label="实际库存" min-width="140">
        <template #default="{ row, $index }">
          <el-form-item
            :prop="`${$index}.actualCount`"
            :rules="formRules.actualCount"
            class="mb-0px!"
          >
            <el-input-number
              v-model="row.actualCount"
              :precision="3"
              class="!w-100%"
              controls-position="right"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="盈亏数量" min-width="110" prop="count">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.count`" :rules="formRules.count" class="mb-0px!">
            <el-input
              v-model="row.count"
              :formatter="erpCountInputFormatter"
              class="!w-100%"
              disabled
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
      <el-table-column fixed="right" label="合计金额" min-width="100" prop="totalPrice">
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
          <el-button link @click="handleDelete($index)">—</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-form>
  <el-row v-if="!disabled" class="mt-3" justify="center">
    <el-button round @click="handleAdd">+ 添加盘点产品</el-button>
  </el-row>
</template>
<script lang="ts" setup>
import { ProductApi, ProductVO } from '@/api/erp/product/product'
import { WarehouseApi, WarehouseVO } from '@/api/erp/stock/warehouse'
import { StockApi } from '@/api/erp/stock/stock'
import {
  erpCountInputFormatter,
  erpPriceInputFormatter,
  erpPriceMultiply,
  getSumValue
} from '@/utils'

const props = defineProps<{
  items: undefined
  disabled: false
}>()
const formLoading = ref(false) // 表单的加载中
const formData = ref([])
const formRules = reactive({
  inId: [{ required: true, message: '盘点编号不能为空', trigger: 'blur' }],
  warehouseId: [{ required: true, message: '仓库名字不能为空', trigger: 'blur' }],
  productId: [{ required: true, message: '产品不能为空', trigger: 'blur' }],
  count: [{ required: true, message: '产品数量不能为空', trigger: 'blur' }]
})
const formRef = ref([]) // 表单 Ref
const productList = ref<ProductVO[]>([]) // 产品列表
const warehouseList = ref<WarehouseVO[]>([]) // 仓库列表
const defaultWarehouse = ref<WarehouseVO>(undefined) // 默认仓库

/** 初始化设置盘点项 */
watch(
  () => props.items,
  async (val) => {
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
      if (item.stockCount != null && item.actualCount != null) {
        item.count = item.actualCount - item.stockCount
      } else {
        item.count = undefined
      }
      item.totalPrice = erpPriceMultiply(item.productPrice, item.count)
    })
  },
  { deep: true }
)

/** 合计 */
const getSummaries = (param: SummaryMethodProps) => {
  const { columns, data } = param
  const sums: string[] = []
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    if (['count', 'totalPrice'].includes(column.property)) {
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
    warehouseId: defaultWarehouse.value?.id,
    productId: undefined,
    productUnitName: undefined, // 产品单位
    productBarCode: undefined, // 产品条码
    productPrice: undefined,
    stockCount: undefined,
    actualCount: undefined,
    count: undefined,
    totalPrice: undefined,
    remark: undefined
  }
  formData.value.push(row)
}

/** 删除按钮操作 */
const handleDelete = (index) => {
  formData.value.splice(index, 1)
}

/** 处理仓库变更 */
const onChangeWarehouse = (warehouseId, row) => {
  // 加载库存
  setStockCount(row)
}

/** 处理产品变更 */
const onChangeProduct = (productId, row) => {
  const product = productList.value.find((item) => item.id === productId)
  if (product) {
    row.productUnitName = product.unitName
    row.productBarCode = product.barCode
    row.productPrice = product.minPrice
  }
  // 加载库存
  setStockCount(row)
}

/** 加载库存 */
const setStockCount = async (row) => {
  if (!row.productId || !row.warehouseId) {
    return
  }
  const stock = await StockApi.getStock2(row.productId, row.warehouseId)
  row.stockCount = stock ? stock.count : 0
  row.actualCount = row.stockCount
}

/** 表单校验 */
const validate = () => {
  return formRef.value.validate()
}
defineExpose({ validate })

/** 初始化 */
onMounted(async () => {
  productList.value = await ProductApi.getProductSimpleList()
  warehouseList.value = await WarehouseApi.getWarehouseSimpleList()
  defaultWarehouse.value = warehouseList.value.find((item) => item.defaultStatus)
  // 默认添加一个
  if (formData.value.length === 0) {
    handleAdd()
  }
})
</script>
