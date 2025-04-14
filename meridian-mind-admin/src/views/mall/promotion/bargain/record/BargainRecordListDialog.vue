<template>
  <Dialog v-model="dialogVisible" title="助力列表">
    <!-- 列表 -->
    <ContentWrap>
      <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
        <el-table-column label="用户编号" min-width="80px" prop="userId" />
        <el-table-column label="用户头像" min-width="80px" prop="avatar">
          <template #default="scope">
            <el-avatar :src="scope.row.avatar" />
          </template>
        </el-table-column>
        <el-table-column label="用户昵称" min-width="100px" prop="nickname" />
        <el-table-column
          :formatter="fenToYuanFormat"
          label="砍价金额"
          min-width="100px"
          prop="reducePrice"
        />
        <el-table-column
          :formatter="dateFormatter"
          align="center"
          label="助力时间"
          prop="createTime"
          width="180px"
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
  </Dialog>
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as BargainHelpApi from '@/api/mall/promotion/bargain/bargainHelp'
import { fenToYuanFormat } from '@/utils/formatter'

/** 助力列表 */
defineOptions({ name: 'BargainRecordListDialog' })

const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  recordId: undefined
})
const queryFormRef = ref() // 搜索的表单

/** 打开弹窗 */
const dialogVisible = ref(false) // 弹窗的是否展示
const open = async (recordId: any) => {
  dialogVisible.value = true
  queryParams.recordId = recordId
  resetQuery()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await BargainHelpApi.getBargainHelpPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}
</script>
