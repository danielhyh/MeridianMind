<template>
  <el-table :data="formData" :show-overflow-tooltip="true" :stripe="true" height="120">
    <el-table-column align="center" fixed="left" label="商机名称" prop="name" />
    <el-table-column
      :formatter="erpPriceTableColumnFormatter"
      align="center"
      label="商机金额"
      prop="totalPrice"
    />
    <el-table-column align="center" label="客户名称" prop="customerName" />
    <el-table-column align="center" label="商机组" prop="statusTypeName" />
    <el-table-column align="center" label="商机阶段" prop="statusName" />
    <el-table-column align="center" fixed="right" label="操作" width="80">
      <template #default="{ $index }">
        <el-button link type="danger" @click="handleDelete($index)"> 移除</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script lang="ts" setup>
import { erpPriceTableColumnFormatter } from '@/utils'

const props = defineProps<{
  businesses: undefined
}>()
const formData = ref([])

/** 初始化商机列表 */
watch(
  () => props.businesses,
  async (val) => {
    formData.value = val
  },
  { immediate: true }
)

/** 删除按钮操作 */
const handleDelete = (index: number) => {
  formData.value.splice(index, 1)
}
</script>
