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
      <el-form-item label="业务类型" prop="bizType">
        <el-select
          v-model="queryParams.bizType"
          class="!w-240px"
          clearable
          placeholder="请选择业务类型"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.MEMBER_POINT_BIZ_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="积分标题" prop="title">
        <el-input
          v-model="queryParams.title"
          class="!w-240px"
          clearable
          placeholder="请输入积分标题"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="获得时间" prop="createDate">
        <el-date-picker
          v-model="queryParams.createDate"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
          end-placeholder="结束日期"
          start-placeholder="开始日期"
          type="daterange"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon class="mr-5px" icon="ep:search" />
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon class="mr-5px" icon="ep:refresh" />
          重置
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column align="center" label="编号" prop="id" width="180" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="获得时间"
        prop="createTime"
        width="180"
      />
      <el-table-column align="center" label="获得积分" prop="point" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.point > 0" class="ml-2" effect="dark" type="success">
            +{{ scope.row.point }}
          </el-tag>
          <el-tag v-else class="ml-2" effect="dark" type="danger"> {{ scope.row.point }} </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="总积分" prop="totalPoint" width="100" />
      <el-table-column align="center" label="标题" prop="title" />
      <el-table-column align="center" label="描述" prop="description" />
      <el-table-column align="center" label="业务编码" prop="bizId" />
      <el-table-column align="center" label="业务类型" prop="bizType">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.MEMBER_POINT_BIZ_TYPE" :value="scope.row.bizType" />
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
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as RecordApi from '@/api//member/point/record'

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  bizType: undefined,
  title: null,
  createDate: [],
  userId: NaN
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await RecordApi.getRecordPage(queryParams)
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
  queryFormRef.value.resetFields()
  handleQuery()
}

const { userId } = defineProps({
  userId: {
    type: Number,
    required: true
  }
})

/** 初始化 **/
onMounted(() => {
  queryParams.userId = userId
  getList()
})
</script>
