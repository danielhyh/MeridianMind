<!-- 产品销量排行 -->
<template>
  <!-- 柱状图 -->
  <el-card shadow="never">
    <el-skeleton :loading="loading" animated>
      <Echart :height="500" :options="echartsOption" />
    </el-skeleton>
  </el-card>

  <!-- 排行列表 -->
  <el-card class="mt-16px" shadow="never">
    <el-table v-loading="loading" :data="list">
      <el-table-column align="center" label="公司排名" type="index" width="80" />
      <el-table-column align="center" label="员工" min-width="200" prop="nickname" />
      <el-table-column align="center" label="部门" min-width="200" prop="deptName" />
      <el-table-column align="center" label="产品销量" min-width="200" prop="count" />
    </el-table>
  </el-card>
</template>
<script lang="ts" setup>
import { StatisticsRankApi, StatisticsRankRespVO } from '@/api/crm/statistics/rank'
import { EChartsOption } from 'echarts'
import { clone } from 'lodash-es'

defineOptions({ name: 'ProductSalesRank' })
const props = defineProps<{ queryParams: any }>() // 搜索参数

const loading = ref(false) // 加载中
const list = ref<StatisticsRankRespVO[]>([]) // 列表的数据

/** 柱状图配置：横向 */
const echartsOption = reactive<EChartsOption>({
  dataset: {
    dimensions: ['nickname', 'count'],
    source: []
  },
  grid: {
    left: 20,
    right: 20,
    bottom: 20,
    containLabel: true
  },
  legend: {
    top: 50
  },
  series: [
    {
      name: '产品销量排行',
      type: 'bar'
    }
  ],
  toolbox: {
    feature: {
      dataZoom: {
        yAxisIndex: false // 数据区域缩放：Y 轴不缩放
      },
      brush: {
        type: ['lineX', 'clear'] // 区域缩放按钮、还原按钮
      },
      saveAsImage: { show: true, name: '产品销量排行' } // 保存为图片
    }
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  xAxis: {
    type: 'value',
    name: '产品销量'
  },
  yAxis: {
    type: 'category',
    name: '员工'
  }
}) as EChartsOption

/** 获取产品销量排行 */
const loadData = async () => {
  // 1. 加载排行数据
  loading.value = true
  const rankingList = await StatisticsRankApi.getProductSalesRank(props.queryParams)
  // 2.1 更新 Echarts 数据
  if (echartsOption.dataset && echartsOption.dataset['source']) {
    echartsOption.dataset['source'] = clone(rankingList).reverse()
  }
  // 2.2 更新列表数据
  list.value = rankingList
  loading.value = false
}
defineExpose({ loadData })

/** 初始化 */
onMounted(() => {
  loadData()
})
</script>
