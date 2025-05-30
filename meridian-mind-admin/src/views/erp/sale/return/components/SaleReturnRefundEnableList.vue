<!-- 可退款的销售退货单列表 -->
<template>
  <Dialog
    v-model="dialogVisible"
    :appendToBody="true"
    :scroll="true"
    title="选择销售退货（仅展示可退款）"
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
        <el-form-item label="退货单号" prop="no">
          <el-input
            v-model="queryParams.no"
            class="!w-160px"
            clearable
            placeholder="请输入退货单号"
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
        <el-form-item label="退货时间" prop="orderTime">
          <el-date-picker
            v-model="queryParams.returnTime"
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
        <el-table-column align="center" label="退货单号" min-width="180" prop="no" />
        <el-table-column align="center" label="客户" prop="customerName" />
        <el-table-column align="center" label="产品信息" min-width="200" prop="productNames" />
        <el-table-column
          :formatter="dateFormatter2"
          align="center"
          label="退货时间"
          prop="returnTime"
          width="120px"
        />
        <el-table-column align="center" label="创建人" prop="creatorName" />
        <el-table-column
          :formatter="erpPriceTableColumnFormatter"
          align="center"
          label="应退金额"
          prop="totalPrice"
        />
        <el-table-column
          :formatter="erpPriceTableColumnFormatter"
          align="center"
          label="已退金额"
          prop="refundPrice"
        />
        <el-table-column align="center" label="未退金额">
          <template #default="scope">
            <span v-if="scope.row.refundPrice === scope.row.totalPrice">0</span>
            <el-tag v-else type="danger">
              {{ erpPriceInputFormatter(scope.row.totalPrice - scope.row.refundPrice) }}
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
import { SaleReturnApi, SaleReturnVO } from '@/api/erp/sale/return'

defineOptions({ name: 'SaleReturnPaymentEnableList' })

const list = ref<SaleReturnVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const loading = ref(false) // 列表的加载中
const dialogVisible = ref(false) // 弹窗的是否展示
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  no: undefined,
  productId: undefined,
  returnTime: [],
  refundEnable: true,
  customerId: undefined
})
const queryFormRef = ref() // 搜索的表单
const productList = ref<ProductVO[]>([]) // 产品列表

/** 选中操作 */
const selectionList = ref<SaleReturnVO[]>([])
const handleSelectionChange = (rows: SaleReturnVO[]) => {
  selectionList.value = rows
}

/** 打开弹窗 */
const open = async (customerId: number) => {
  dialogVisible.value = true
  await nextTick() // 等待，避免 queryFormRef 为空
  // 加载列表
  queryParams.customerId = customerId
  await resetQuery()
  // 加载产品列表
  productList.value = await ProductApi.getProductSimpleList()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交选择 */
const emits = defineEmits<{
  (e: 'success', value: SaleReturnVO[]): void
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
    const data = await SaleReturnApi.getSaleReturnPage(queryParams)
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
