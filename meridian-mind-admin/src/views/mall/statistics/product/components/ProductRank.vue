<template>
  <el-card shadow="never">
    <template #header>
      <!-- 标题 -->
      <div class="flex flex-row items-center justify-between">
        <CardTitle title="商品排行" />
        <!-- 查询条件 -->
        <ShortcutDateRangePicker ref="shortcutDateRangePicker" @change="handleDateRangeChange" />
      </div>
    </template>
    <!-- 排行列表 -->
    <el-table v-loading="loading" :data="list" @sort-change="handleSortChange">
      <el-table-column label="商品 ID" min-width="70" prop="spuId" />
      <el-table-column align="center" label="商品图片" prop="picUrl" width="80">
        <template #default="{ row }">
          <el-image
            :preview-src-list="[row.picUrl]"
            :src="row.picUrl"
            class="h-30px w-30px"
            preview-teleported
          />
        </template>
      </el-table-column>
      <el-table-column :show-overflow-tooltip="true" label="商品名称" min-width="200" prop="name" />
      <el-table-column label="浏览量" min-width="90" prop="browseCount" sortable="custom" />
      <el-table-column label="访客数" min-width="90" prop="browseUserCount" sortable="custom" />
      <el-table-column label="加购件数" min-width="105" prop="cartCount" sortable="custom" />
      <el-table-column label="下单件数" min-width="105" prop="orderCount" sortable="custom" />
      <el-table-column label="支付件数" min-width="105" prop="orderPayCount" sortable="custom" />
      <el-table-column
        :formatter="fenToYuanFormat"
        label="支付金额"
        min-width="105"
        prop="orderPayPrice"
        sortable="custom"
      />
      <el-table-column label="收藏数" min-width="90" prop="favoriteCount" sortable="custom" />
      <el-table-column
        :formatter="formatConvertRate"
        label="访客-支付转化率(%)"
        min-width="180"
        prop="browseConvertPercent"
        sortable="custom"
      />
    </el-table>
    <!-- 分页 -->
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getSpuList"
    />
  </el-card>
</template>
<script lang="ts" setup>
import { ProductStatisticsApi, ProductStatisticsVO } from '@/api/mall/statistics/product'
import { CardTitle } from '@/components/Card'
import { buildSortingField } from '@/utils'
import { fenToYuanFormat } from '@/utils/formatter'

/** 商品排行 */
defineOptions({ name: 'ProductRank' })

// 格式化：访客-支付转化率
const formatConvertRate = (row: ProductStatisticsVO) => {
  return `${row.browseConvertPercent}%`
}

const handleSortChange = (params: any) => {
  queryParams.sortingFields = [buildSortingField(params)]
  getSpuList()
}

const handleDateRangeChange = (times: any[]) => {
  queryParams.times = times as []
  getSpuList()
}

const shortcutDateRangePicker = ref()
// 查询参数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  times: [],
  sortingFields: {}
})
const loading = ref(false) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref<ProductStatisticsVO[]>([]) // 列表的数据

/** 查询商品列表 */
const getSpuList = async () => {
  loading.value = true
  try {
    const data = await ProductStatisticsApi.getProductStatisticsRankPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 初始化 **/
onMounted(async () => {
  await getSpuList()
})
</script>
<style lang="scss" scoped></style>
