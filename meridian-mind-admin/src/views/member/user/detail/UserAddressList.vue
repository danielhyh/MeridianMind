<template>
  <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
    <el-table-column align="center" label="地址编号" prop="id" width="150px" />
    <el-table-column align="center" label="收件人名称" prop="name" width="150px" />
    <el-table-column align="center" label="手机号" prop="mobile" width="150px" />
    <el-table-column align="center" label="地区编码" prop="areaId" width="150px" />
    <el-table-column align="center" label="收件详细地址" prop="detailAddress" />
    <el-table-column align="center" label="是否默认" prop="defaultStatus" width="150px">
      <template #default="scope">
        <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="Number(scope.row.defaultStatus)" />
      </template>
    </el-table-column>
    <el-table-column
      :formatter="dateFormatter"
      align="center"
      label="创建时间"
      prop="createTime"
      width="180px"
    />
  </el-table>
</template>
<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as AddressApi from '@/api/member/address'

const { userId }: { userId: number } = defineProps({
  userId: {
    type: Number,
    required: true
  }
})

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    list.value = await AddressApi.getAddressList({ userId })
  } finally {
    loading.value = false
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped></style>
