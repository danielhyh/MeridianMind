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
            v-for="dict in getIntDictOptions(DICT_TYPE.MEMBER_EXPERIENCE_BIZ_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="queryParams.title"
          class="!w-240px"
          clearable
          placeholder="请输入标题"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
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
      <el-table-column align="center" label="编号" prop="id" width="150px" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="获得时间"
        prop="createTime"
      />
      <el-table-column align="center" label="经验" prop="experience" width="150px">
        <template #default="scope">
          <el-tag v-if="scope.row.experience > 0" class="ml-2" effect="dark" type="success">
            +{{ scope.row.experience }}
          </el-tag>
          <el-tag v-else class="ml-2" effect="dark" type="danger">
            {{ scope.row.experience }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="总经验" prop="totalExperience" width="150px">
        <template #default="scope">
          <el-tag class="ml-2" effect="dark">
            {{ scope.row.totalExperience }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="标题" prop="title" width="150px" />
      <el-table-column align="center" label="描述" prop="description" />
      <el-table-column align="center" label="业务编号" prop="bizId" width="150px" />
      <el-table-column align="center" label="业务类型" prop="bizType" width="150px">
        <!--   TODO 芋艿：此处应创建对应的字典 -->
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.MEMBER_EXPERIENCE_BIZ_TYPE" :value="scope.row.bizType" />
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
import { dateFormatter } from '@/utils/formatTime'
import * as ExperienceRecordApi from '@/api/member/experience-record/index'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'

defineOptions({ name: 'UserExperienceRecordList' })

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: null,
  bizId: null,
  bizType: null,
  title: null,
  description: null,
  experience: null,
  totalExperience: null,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ExperienceRecordApi.getExperienceRecordPage(queryParams)
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
