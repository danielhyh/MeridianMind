<!-- 数据统计 - 员工业绩分析 -->
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
      <el-form-item label="选择年份" prop="orderDate">
        <el-date-picker
          v-model="queryParams.times[0]"
          :default-time="[new Date().getFullYear()]"
          class="!w-240px"
          type="year"
          value-format="YYYY"
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
          @change="queryParams.userId = undefined"
        />
      </el-form-item>
      <el-form-item label="员工" prop="userId">
        <el-select v-model="queryParams.userId" class="!w-240px" clearable placeholder="员工">
          <el-option
            v-for="(user, index) in userListByDeptId"
            :key="index"
            :label="user.nickname"
            :value="user.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"> <Icon class="mr-5px" icon="ep:search" /> 搜索 </el-button>
        <el-button @click="resetQuery"> <Icon class="mr-5px" icon="ep:refresh" /> 重置 </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 员工业绩统计 -->
  <el-col>
    <el-tabs v-model="activeTab">
      <!-- 员工合同统计 -->
      <el-tab-pane label="员工合同数量统计" lazy name="ContractCountPerformance">
        <ContractCountPerformance ref="ContractCountPerformanceRef" :query-params="queryParams" />
      </el-tab-pane>
      <!-- 员工合同金额统计 -->
      <el-tab-pane label="员工合同金额统计" lazy name="ContractPricePerformance">
        <ContractPricePerformance ref="ContractPricePerformanceRef" :query-params="queryParams" />
      </el-tab-pane>
      <!-- 员工回款金额统计 -->
      <el-tab-pane label="员工回款金额统计" lazy name="ReceivablePricePerformance">
        <ReceivablePricePerformance
          ref="ReceivablePricePerformanceRef"
          :query-params="queryParams"
        />
      </el-tab-pane>
    </el-tabs>
  </el-col>
</template>

<script lang="ts" setup>
import * as DeptApi from '@/api/system/dept'
import * as UserApi from '@/api/system/user'
import { useUserStore } from '@/store/modules/user'
import { beginOfDay, endOfDay, formatDate } from '@/utils/formatTime'
import { defaultProps, handleTree } from '@/utils/tree'
import ContractCountPerformance from './components/ContractCountPerformance.vue'
import ContractPricePerformance from './components/ContractPricePerformance.vue'
import ReceivablePricePerformance from './components/ReceivablePricePerformance.vue'

defineOptions({ name: 'CrmStatisticsCustomer' })

const queryParams = reactive({
  deptId: useUserStore().getUser.deptId,
  userId: undefined,
  times: [
    formatDate(beginOfDay(new Date(new Date().getFullYear(), 0, 1))),
    formatDate(endOfDay(new Date(new Date().getFullYear(), 11, 31)))
  ]
})

const queryFormRef = ref() // 搜索的表单
const deptList = ref<Tree[]>([]) // 部门树形结构
const userList = ref<UserApi.UserVO[]>([]) // 全量用户清单
// 根据选择的部门筛选员工清单
const userListByDeptId = computed(() =>
  queryParams.deptId
    ? userList.value.filter((u: UserApi.UserVO) => u.deptId === queryParams.deptId)
    : []
)

// 活跃标签
const activeTab = ref('ContractCountPerformance')
const ContractCountPerformanceRef = ref() // 员工合同数量统计
const ContractPricePerformanceRef = ref() // 员工合同金额统计
const ReceivablePricePerformanceRef = ref() // 员工回款金额统计

/** 搜索按钮操作 */
const handleQuery = () => {
  // 从 queryParams.times[0] 中获取到了年份
  const selectYear = parseInt(queryParams.times[0])
  queryParams.times[0] = formatDate(beginOfDay(new Date(selectYear, 0, 1)))
  queryParams.times[1] = formatDate(endOfDay(new Date(selectYear, 11, 31)))

  // 执行查询
  switch (activeTab.value) {
    case 'ContractCountPerformance':
      ContractCountPerformanceRef.value?.loadData?.()
      break
    case 'ContractPricePerformance':
      ContractPricePerformanceRef.value?.loadData?.()
      break
    case 'ReceivablePricePerformance':
      ReceivablePricePerformanceRef.value?.loadData?.()
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
  userList.value = handleTree(await UserApi.getSimpleUserList())
})
</script>
