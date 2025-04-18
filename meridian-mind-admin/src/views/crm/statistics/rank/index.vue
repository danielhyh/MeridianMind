<!-- BI 排行版 -->
<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="时间范围" prop="orderDate">
        <el-date-picker
          v-model="queryParams.times"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          :shortcuts="defaultShortcuts"
          class="!w-240px"
          end-placeholder="结束日期"
          start-placeholder="开始日期"
          type="daterange"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item label="归属部门" prop="deptId">
        <el-tree-select
          v-model="queryParams.deptId"
          :data="deptList"
          :props="defaultProps"
          check-strictly
          class="!w-240px"
          node-key="id"
          placeholder="请选择归属部门"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" /> 重置</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 排行数据 -->
  <el-col>
    <el-tabs v-model="activeTab">
      <!-- 合同金额排行 -->
      <el-tab-pane label="合同金额排行" lazy name="contractPriceRank">
        <ContractPriceRank ref="contractPriceRankRef" :query-params="queryParams" />
      </el-tab-pane>
      <!-- 回款金额排行 -->
      <el-tab-pane label="回款金额排行" lazy name="receivablePriceRank">
        <ReceivablePriceRank ref="receivablePriceRankRef" :query-params="queryParams" />
      </el-tab-pane>
      <!-- 签约合同排行 -->
      <el-tab-pane label="签约合同排行" lazy name="contractCountRank">
        <ContractCountRank ref="contractCountRankRef" :query-params="queryParams" />
      </el-tab-pane>
      <!-- 产品销量排行 -->
      <el-tab-pane label="产品销量排行" lazy name="productSalesRank">
        <ProductSalesRank ref="productSalesRankRef" :query-params="queryParams" />
      </el-tab-pane>
      <!-- 新增客户数排行 -->
      <el-tab-pane label="新增客户数排行" lazy name="customerCountRank">
        <CustomerCountRank ref="customerCountRankRef" :query-params="queryParams" />
      </el-tab-pane>
      <!-- 新增联系人数排行 -->
      <el-tab-pane label="新增联系人数排行" lazy name="contactCountRank">
        <ContactCountRank ref="contactCountRankRef" :query-params="queryParams" />
      </el-tab-pane>
      <!-- 跟进次数排行 -->
      <el-tab-pane label="跟进次数排行" lazy name="followCountRank">
        <FollowCountRank ref="followCountRankRef" :query-params="queryParams" />
      </el-tab-pane>
      <!-- 跟进客户数排行 -->
      <el-tab-pane label="跟进客户数排行" lazy name="followCustomerCountRank">
        <FollowCustomerCountRank ref="followCustomerCountRankRef" :query-params="queryParams" />
      </el-tab-pane>
    </el-tabs>
  </el-col>
</template>
<script lang="ts" setup>
import ContractPriceRank from './components/ContractPriceRank.vue'
import ReceivablePriceRank from './components/ReceivablePriceRank.vue'
import ContractCountRank from './components/ContractCountRank.vue'
import ProductSalesRank from './components/ProductSalesRank.vue'
import CustomerCountRank from './components/CustomerCountRank.vue'
import ContactCountRank from './components/ContactCountRank.vue'
import FollowCountRank from './components/FollowCountRank.vue'
import FollowCustomerCountRank from './components/FollowCustomerCountRank.vue'
import { defaultProps, handleTree } from '@/utils/tree'
import * as DeptApi from '@/api/system/dept'
import { beginOfDay, defaultShortcuts, endOfDay, formatDate } from '@/utils/formatTime'
import { useUserStore } from '@/store/modules/user'

defineOptions({ name: 'CrmStatisticsRank' })

const queryParams = reactive({
  deptId: useUserStore().getUser.deptId,
  times: [
    // 默认显示最近一周的数据
    formatDate(beginOfDay(new Date(new Date().getTime() - 3600 * 1000 * 24 * 7))),
    formatDate(endOfDay(new Date(new Date().getTime() - 3600 * 1000 * 24)))
  ]
})

const queryFormRef = ref() // 搜索的表单
const deptList = ref<Tree[]>([]) // 树形结构
const activeTab = ref('contractPriceRank')
const contractPriceRankRef = ref() // ContractPriceRank 组件的引用
const receivablePriceRankRef = ref() // ReceivablePriceRank 组件的引用
const contractCountRankRef = ref() // ContractCountRank 组件的引用
const productSalesRankRef = ref() // ProductSalesRank 组件的引用
const customerCountRankRef = ref() // CustomerCountRank 组件的引用
const contactCountRankRef = ref() // ContactCountRank 组件的引用
const followCountRankRef = ref() // FollowCountRank 组件的引用
const followCustomerCountRankRef = ref() // FollowCustomerCountRank 组件的引用

/** 搜索按钮操作 */
const handleQuery = () => {
  switch (activeTab.value) {
    case 'contractPriceRank': // 合同金额排行
      contractPriceRankRef.value?.loadData?.()
      break
    case 'receivablePriceRank': // 回款金额排行
      receivablePriceRankRef.value?.loadData?.()
      break
    case 'contractCountRank': // 签约合同排行
      contractCountRankRef.value?.loadData?.()
      break
    case 'productSalesRank': // 产品销量排行
      productSalesRankRef.value?.loadData?.()
      break
    case 'customerCountRank': // 新增客户数排行
      customerCountRankRef.value?.loadData?.()
      break
    case 'contactCountRank': // 新增联系人数排行
      contactCountRankRef.value?.loadData?.()
      break
    case 'followCountRank': // 跟进次数排行
      followCountRankRef.value?.loadData?.()
      break
    case 'followCustomerCountRank': // 跟进客户数排行
      followCustomerCountRankRef.value?.loadData?.()
      break
  }
}

// 当 activeTab 改变时，刷新当前活动的 tab
watch(activeTab, () => {
  handleQuery()
})

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

// 加载部门树
onMounted(async () => {
  deptList.value = handleTree(await DeptApi.getSimpleDeptList())
})
</script>
<style lang="scss" scoped></style>
