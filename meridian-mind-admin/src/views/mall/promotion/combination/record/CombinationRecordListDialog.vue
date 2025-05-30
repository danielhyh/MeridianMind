<template>
  <Dialog v-model="dialogVisible" title="拼团列表" width="950">
    <!-- 列表 -->
    <ContentWrap>
      <el-table v-loading="loading" :data="list">
        <el-table-column align="center" label="编号" min-width="50" prop="id" />
        <el-table-column align="center" label="头像" min-width="80" prop="avatar">
          <template #default="scope">
            <el-avatar :src="scope.row.avatar" />
          </template>
        </el-table-column>
        <el-table-column align="center" label="昵称" min-width="100" prop="nickname" />
        <el-table-column align="center" label="开团团长" min-width="100" prop="headId">
          <template #default="{ row }: { row: CombinationRecordApi.CombinationRecordVO }">
            <el-tag> {{ row.headId === 0 ? '团长' : '团员' }} </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          :formatter="dateFormatter"
          align="center"
          label="参团时间"
          prop="createTime"
          width="180"
        />
        <el-table-column
          :formatter="dateFormatter"
          align="center"
          label="结束时间"
          prop="endTime"
          width="180"
        />
        <el-table-column align="center" label="拼团状态" min-width="150" prop="status">
          <template #default="scope">
            <dict-tag
              :type="DICT_TYPE.PROMOTION_COMBINATION_RECORD_STATUS"
              :value="scope.row.status"
            />
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
  </Dialog>
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as CombinationRecordApi from '@/api/mall/promotion/combination/combinationRecord'
import { DICT_TYPE } from '@/utils/dict'

/** 助力列表 */
defineOptions({ name: 'CombinationRecordListDialog' })

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  headId: undefined
})

/** 打开弹窗 */
const dialogVisible = ref(false) // 弹窗的是否展示
const open = async (headId: any) => {
  dialogVisible.value = true
  queryParams.headId = headId
  await getList()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CombinationRecordApi.getCombinationRecordPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}
</script>
