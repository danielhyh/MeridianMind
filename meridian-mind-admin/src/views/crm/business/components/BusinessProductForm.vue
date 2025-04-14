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
    <el-table :data="formData" class="-mt-10px">
      <el-table-column align="center" label="序号" type="index" width="60" />
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
      <el-table-column label="条码" min-width="150">
        <template #default="{ row }">
          <el-form-item class="mb-0px!">
            <el-input v-model="row.productNo" disabled />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="单位" min-width="80">
        <template #default="{ row }">
          <dict-tag :type="DICT_TYPE.CRM_PRODUCT_UNIT" :value="row.productUnit" />
        </template>
      </el-table-column>
      <el-table-column label="价格（元）" min-width="120">
        <template #default="{ row }">
          <el-form-item class="mb-0px!">
            <el-input v-model="row.productPrice" :formatter="erpPriceInputFormatter" disabled />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="售价（元）" min-width="140">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.businessPrice`" class="mb-0px!">
            <el-input-number
              v-model="row.businessPrice"
              :min="0.001"
              :precision="2"
              class="!w-100%"
              controls-position="right"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="数量" min-width="120" prop="count">
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
      <el-table-column fixed="right" label="合计" min-width="140" prop="totalPrice">
        <template #default="{ row, $index }">
          <el-form-item :prop="`${$index}.totalPrice`" class="mb-0px!">
            <el-input v-model="row.totalPrice" :formatter="erpPriceInputFormatter" disabled />
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
    <el-button round @click="handleAdd">+ 添加产品</el-button>
  </el-row>
</template>
<script lang="ts" setup>
import * as ProductApi from '@/api/crm/product'
import { erpPriceInputFormatter, erpPriceMultiply } from '@/utils'
import { DICT_TYPE } from '@/utils/dict'

const props = defineProps<{
  products: undefined
  disabled: false
}>()
const formLoading = ref(false) // 表单的加载中
const formData = ref([])
const formRules = reactive({
  productId: [{ required: true, message: '产品不能为空', trigger: 'blur' }],
  businessPrice: [{ required: true, message: '合同价格不能为空', trigger: 'blur' }],
  count: [{ required: true, message: '产品数量不能为空', trigger: 'blur' }]
})
const formRef = ref([]) // 表单 Ref
const productList = ref<ProductApi.ProductVO[]>([]) // 产品列表

/** 初始化设置产品项 */
watch(
  () => props.products,
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
      if (item.businessPrice != null && item.count != null) {
        item.totalPrice = erpPriceMultiply(item.businessPrice, item.count)
      } else {
        item.totalPrice = undefined
      }
    })
  },
  { deep: true }
)

/** 新增按钮操作 */
const handleAdd = () => {
  const row = {
    id: undefined,
    productId: undefined,
    productUnit: undefined, // 产品单位
    productNo: undefined, // 产品条码
    productPrice: undefined, // 产品价格
    businessPrice: undefined,
    count: 1
  }
  formData.value.push(row)
}

/** 删除按钮操作 */
const handleDelete = (index: number) => {
  formData.value.splice(index, 1)
}

/** 处理产品变更 */
const onChangeProduct = (productId, row) => {
  const product = productList.value.find((item) => item.id === productId)
  if (product) {
    row.productUnit = product.unit
    row.productNo = product.no
    row.productPrice = product.price
    row.businessPrice = product.price
  }
}

/** 表单校验 */
const validate = () => {
  return formRef.value.validate()
}
defineExpose({ validate })

/** 初始化 */
onMounted(async () => {
  productList.value = await ProductApi.getProductSimpleList()
})
</script>
