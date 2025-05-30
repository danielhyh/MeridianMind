<!-- 客户总量统计 -->
<template>
  <!-- Echarts图 -->
  <el-card shadow="never">
    <el-skeleton :loading="loading" animated>
      <Echart :height="500" :options="echartsOption" />
    </el-skeleton>
  </el-card>

  <!-- 统计列表 -->
  <el-card class="mt-16px" shadow="never">
    <el-table v-loading="loading" :data="list">
      <el-table-column align="center" fixed="left" label="序号" type="index" width="80" />
      <el-table-column fixed="left" label="员工姓名" min-width="100" prop="ownerUserName" />
      <el-table-column
        align="right"
        label="新增客户数"
        min-width="200"
        prop="customerCreateCount"
      />
      <el-table-column align="right" label="成交客户数" min-width="200" prop="customerDealCount" />
      <el-table-column align="right" label="客户成交率(%)" min-width="200">
        <template #default="scope">
          {{ erpCalculatePercentage(scope.row.customerDealCount, scope.row.customerCreateCount) }}
        </template>
      </el-table-column>
      <el-table-column
        :formatter="erpPriceTableColumnFormatter"
        align="right"
        label="合同总金额"
        min-width="200"
        prop="contractPrice"
      />
      <el-table-column
        :formatter="erpPriceTableColumnFormatter"
        align="right"
        label="回款金额"
        min-width="200"
        prop="receivablePrice"
      />
      <el-table-column align="right" label="未回款金额" min-width="200">
        <template #default="scope">
          {{ erpCalculatePercentage(scope.row.receivablePrice, scope.row.contractPrice) }}
        </template>
      </el-table-column>
      <el-table-column align="right" fixed="right" label="回款完成率(%)" min-width="200">
        <template #default="scope">
          {{ erpCalculatePercentage(scope.row.receivablePrice, scope.row.contractPrice) }}
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>
<script lang="ts" setup>
import {
  StatisticsCustomerApi,
  CrmStatisticsCustomerSummaryByDateRespVO,
  CrmStatisticsCustomerSummaryByUserRespVO
} from '@/api/crm/statistics/customer'
import { EChartsOption } from 'echarts'
import { erpCalculatePercentage, erpPriceTableColumnFormatter } from '@/utils'

defineOptions({ name: 'CustomerSummary' })

const props = defineProps<{ queryParams: any }>() // 搜索参数

const loading = ref(false) // 加载中
const list = ref<CrmStatisticsCustomerSummaryByUserRespVO[]>([]) // 列表的数据

/** 柱状图配置：纵向 */
const echartsOption = reactive<EChartsOption>({
  grid: {
    left: 20,
    right: 30, // 让 X 轴右侧显示完整
    bottom: 20,
    containLabel: true
  },
  legend: {},
  series: [
    {
      name: '新增客户数',
      type: 'bar',
      yAxisIndex: 0,
      data: []
    },
    {
      name: '成交客户数',
      type: 'bar',
      yAxisIndex: 1,
      data: []
    }
  ],
  toolbox: {
    feature: {
      dataZoom: {
        xAxisIndex: false // 数据区域缩放：Y 轴不缩放
      },
      brush: {
        type: ['lineX', 'clear'] // 区域缩放按钮、还原按钮
      },
      saveAsImage: { show: true, name: '客户总量分析' } // 保存为图片
    }
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  yAxis: [
    {
      type: 'value',
      name: '新增客户数',
      min: 0,
      minInterval: 1 // 显示整数刻度
    },
    {
      type: 'value',
      name: '成交客户数',
      min: 0,
      minInterval: 1, // 显示整数刻度
      splitLine: {
        lineStyle: {
          type: 'dotted', // 右侧网格线虚化, 减少混乱
          opacity: 0.7
        }
      }
    }
  ],
  xAxis: {
    type: 'category',
    name: '日期',
    data: []
  }
}) as EChartsOption

/** 获取数据并填充图表 */
const fetchAndFill = async () => {
  // 1. 加载统计数据
  const customerSummaryByDate = await StatisticsCustomerApi.getCustomerSummaryByDate(
    props.queryParams
  )
  const customerSummaryByUser = await StatisticsCustomerApi.getCustomerSummaryByUser(
    props.queryParams
  )
  // 2.1 更新 Echarts 数据
  if (echartsOption.xAxis && echartsOption.xAxis['data']) {
    echartsOption.xAxis['data'] = customerSummaryByDate.map(
      (s: CrmStatisticsCustomerSummaryByDateRespVO) => s.time
    )
  }
  if (echartsOption.series && echartsOption.series[0] && echartsOption.series[0]['data']) {
    echartsOption.series[0]['data'] = customerSummaryByDate.map(
      (s: CrmStatisticsCustomerSummaryByDateRespVO) => s.customerCreateCount
    )
  }
  if (echartsOption.series && echartsOption.series[1] && echartsOption.series[1]['data']) {
    echartsOption.series[1]['data'] = customerSummaryByDate.map(
      (s: CrmStatisticsCustomerSummaryByDateRespVO) => s.customerDealCount
    )
  }

  // 2.2 更新列表数据
  list.value = customerSummaryByUser
}

/** 获取统计数据 */
const loadData = async () => {
  loading.value = true
  try {
    await fetchAndFill()
  } finally {
    loading.value = false
  }
}

defineExpose({ loadData })

/** 初始化 */
onMounted(() => {
  loadData()
})
</script>
