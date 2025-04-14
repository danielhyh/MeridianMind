<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="85px"
    >
      <el-form-item label="用户类型" prop="level">
        <el-radio-group v-model="queryParams.level" @change="handleQuery">
          <el-radio-button checked>全部</el-radio-button>
          <el-radio-button value="1">一级推广人</el-radio-button>
          <el-radio-button value="2">二级推广人</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="绑定时间" prop="bindUserTime">
        <el-date-picker
          v-model="queryParams.bindUserTime"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
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

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column align="center" label="用户编号" min-width="80px" prop="id" />
      <el-table-column align="center" label="头像" prop="avatar" width="70px">
        <template #default="scope">
          <el-avatar :src="scope.row.avatar" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="昵称" min-width="80px" prop="nickname" />
      <el-table-column align="center" label="等级" min-width="80px" prop="level">
        <template #default="scope">
          <el-tag v-if="scope.row.bindUserId === bindUserId">一级</el-tag>
          <el-tag v-else>二级</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="绑定时间"
        prop="bindUserTime"
        width="170px"
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
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as BrokerageUserApi from '@/api/mall/trade/brokerage/user'

/** 推广人列表 */
defineOptions({ name: 'UserBrokerageList' })

const { bindUserId }: { bindUserId: number } = defineProps({
  bindUserId: {
    type: Number,
    required: true
  }
}) //用户编号

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  bindUserId: null,
  level: '',
  bindUserTime: []
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    queryParams.bindUserId = bindUserId
    const data = await BrokerageUserApi.getBrokerageUserPage(queryParams)
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

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
