<!-- 可付款的采购入库单列表 -->
<template>
  <Dialog
    v-model="dialogVisible"
    :appendToBody="true"
    :scroll="true"
    title="选择采购入库（仅展示可付款）"
    width="1080"
  >
    <ContentWrap>
      <!-- 搜索工作栏 -->
      <el-form
        ref="queryFormRef"
        :inline="true"
        :model="queryParams"
        class="-mb-15px"
        label-width="68px"
      >
        <el-form-item label="入库单号" prop="no">
          <el-input
            v-model="queryParams.no"
            class="!w-160px"
            clearable
            placeholder="请输入入库单号"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="产品" prop="productId">
          <el-select
            v-model="queryParams.productId"
            class="!w-160px"
            clearable
            filterable
            placeholder="请选择产品"
          >
            <el-option
              v-for="item in productList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="入库时间" prop="orderTime">
          <el-date-picker
            v-model="queryParams.inTime"
            :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
            class="!w-160px"
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

    <ContentWrap>
      <el-table
        v-loading="loading"
        :data="list"
        :show-overflow-tooltip="true"
        :stripe="true"
        @selection-change="handleSelectionChange"
      >
        <el-table-column label="选择" type="selection" width="30" />
        <el-table-column align="center" label="入库单号" min-width="180" prop="no" />
        <el-table-column align="center" label="供应商" prop="supplierName" />
        <el-table-column align="center" label="产品信息" min-width="200" prop="productNames" />
        <el-table-column
          :formatter="dateFormatter2"
          align="center"
          label="入库时间"
          prop="inTime"
          width="120px"
        />
        <el-table-column align="center" label="创建人" prop="creatorName" />
        <el-table-column
          :formatter="erpPriceTableColumnFormatter"
          align="center"
          label="应付金额"
          prop="totalPrice"
        />
        <el-table-column
          :formatter="erpPriceTableColumnFormatter"
          align="center"
          label="已付金额"
          prop="paymentPrice"
        />
        <el-table-column align="center" label="未付金额">
          <template #default="scope">
            <span v-if="scope.row.paymentPrice === scope.row.totalPrice">0</span>
            <el-tag v-else type="danger">
              {{ erpPriceInputFormatter(scope.row.totalPrice - scope.row.paymentPrice) }}
            </el-tag>
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
    <template #footer>
      <el-button :disabled="!selectionList.length" type="primary" @click="submitForm">
        确 定
      </el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { ElTable } from 'element-plus'
import { dateFormatter2 } from '@/utils/formatTime'
import { erpPriceInputFormatter, erpPriceTableColumnFormatter } from '@/utils'
import { ProductApi, ProductVO } from '@/api/erp/product/product'
import { PurchaseInApi, PurchaseInVO } from '@/api/erp/purchase/in'

defineOptions({ name: 'PurchaseInPaymentEnableList' })

const list = ref<PurchaseInVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const loading = ref(false) // 列表的加载中
const dialogVisible = ref(false) // 弹窗的是否展示
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  no: undefined,
  productId: undefined,
  inTime: [],
  paymentEnable: true,
  supplierId: undefined
})
const queryFormRef = ref() // 搜索的表单
const productList = ref<ProductVO[]>([]) // 产品列表

/** 选中操作 */
const selectionList = ref<PurchaseInVO[]>([])
const handleSelectionChange = (rows: PurchaseInVO[]) => {
  selectionList.value = rows
}

/** 打开弹窗 */
const open = async (supplierId: number) => {
  dialogVisible.value = true
  await nextTick() // 等待，避免 queryFormRef 为空
  // 加载可入库的订单列表
  queryParams.supplierId = supplierId
  await resetQuery()
  // 加载产品列表
  productList.value = await ProductApi.getProductSimpleList()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交选择 */
const emits = defineEmits<{
  (e: 'success', value: PurchaseInVO[]): void
}>()
const submitForm = () => {
  try {
    emits('success', selectionList.value)
  } finally {
    // 关闭弹窗
    dialogVisible.value = false
  }
}

/** 加载列表  */
const getList = async () => {
  loading.value = true
  try {
    const data = await PurchaseInApi.getPurchaseInPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  selectionList.value = []
  getList()
}
</script>
