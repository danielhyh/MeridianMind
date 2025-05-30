<!-- 可退货的订单列表 -->
<template>
  <Dialog
    v-model="dialogVisible"
    :appendToBody="true"
    :scroll="true"
    title="选择销售订单（仅展示可退货）"
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
        <el-form-item label="订单单号" prop="no">
          <el-input
            v-model="queryParams.no"
            class="!w-160px"
            clearable
            placeholder="请输入订单单号"
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
        <el-form-item label="订单时间" prop="orderTime">
          <el-date-picker
            v-model="queryParams.orderTime"
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
      <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
        <el-table-column align="center" width="65">
          <template #default="scope">
            <el-radio
              v-model="currentRowValue"
              :value="scope.row.id"
              @change="handleCurrentChange(scope.row)"
            >
              &nbsp;
            </el-radio>
          </template>
        </el-table-column>
        <el-table-column align="center" label="订单单号" min-width="180" prop="no" />
        <el-table-column align="center" label="客户" prop="customerName" />
        <el-table-column align="center" label="产品信息" min-width="200" prop="productNames" />
        <el-table-column
          :formatter="dateFormatter2"
          align="center"
          label="订单时间"
          prop="orderTime"
          width="120px"
        />
        <el-table-column align="center" label="创建人" prop="creatorName" />
        <el-table-column
          :formatter="erpCountTableColumnFormatter"
          align="center"
          label="总数量"
          prop="totalCount"
        />
        <el-table-column
          :formatter="erpCountTableColumnFormatter"
          align="center"
          label="出库数量"
          prop="outCount"
        />
        <el-table-column
          :formatter="erpCountTableColumnFormatter"
          align="center"
          label="退货数量"
          prop="returnCount"
        />
        <el-table-column
          :formatter="erpPriceTableColumnFormatter"
          align="center"
          label="金额合计"
          prop="totalProductPrice"
        />
        <el-table-column
          :formatter="erpPriceTableColumnFormatter"
          align="center"
          label="含税金额"
          prop="totalPrice"
        />
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
      <el-button :disabled="!currentRow" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { ElTable } from 'element-plus'
import { SaleOrderApi, SaleOrderVO } from '@/api/erp/sale/order'
import { dateFormatter2 } from '@/utils/formatTime'
import { erpCountTableColumnFormatter, erpPriceTableColumnFormatter } from '@/utils'
import { ProductApi, ProductVO } from '@/api/erp/product/product'

defineOptions({ name: 'SaleOrderReturnEnableList' })

const list = ref<SaleOrderVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const loading = ref(false) // 列表的加载中
const dialogVisible = ref(false) // 弹窗的是否展示
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  no: undefined,
  productId: undefined,
  orderTime: [],
  returnEnable: true
})
const queryFormRef = ref() // 搜索的表单
const productList = ref<ProductVO[]>([]) // 产品列表

/** 选中行 */
const currentRowValue = ref(undefined) // 选中行的 value
const currentRow = ref(undefined) // 选中行
const handleCurrentChange = (row) => {
  currentRow.value = row
}

/** 打开弹窗 */
const open = async () => {
  dialogVisible.value = true
  await nextTick() // 等待，避免 queryFormRef 为空
  // 加载可退货的订单列表
  await resetQuery()
  // 加载产品列表
  productList.value = await ProductApi.getProductSimpleList()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交选择 */
const emits = defineEmits<{
  (e: 'success', value: SaleOrderVO): void
}>()
const submitForm = () => {
  try {
    emits('success', currentRow.value)
  } finally {
    // 关闭弹窗
    dialogVisible.value = false
  }
}

/** 加载列表  */
const getList = async () => {
  loading.value = true
  try {
    const data = await SaleOrderApi.getSaleOrderPage(queryParams)
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
  currentRowValue.value = undefined
  currentRow.value = undefined
  getList()
}
</script>
