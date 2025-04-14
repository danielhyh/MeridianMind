<template>
  <doc-alert title="工作流手册" url="https://doc.iocoder.cn/bpm/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="发起人" prop="startUserId">
        <el-select v-model="queryParams.startUserId" class="!w-240px" placeholder="请选择发起人">
          <el-option
            v-for="user in userList"
            :key="user.id"
            :label="user.nickname"
            :value="user.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="流程名称" prop="name">
        <el-input
          v-model="queryParams.name"
          class="!w-240px"
          clearable
          placeholder="请输入流程名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="流程状态" prop="status">
        <el-select
          v-model="queryParams.status"
          class="!w-240px"
          clearable
          placeholder="请选择流程状态"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="发起时间" prop="createTime">
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
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
          v-model="queryParams.endTime"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
          end-placeholder="结束日期"
          start-placeholder="开始日期"
          type="daterange"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item
        v-for="(item, index) in formFields"
        :key="index"
        :label="item.title"
        :prop="item.field"
      >
        <!-- TODO @lesan：目前只支持input类型的字符串搜索 -->
        <el-input
          v-model="queryParams.formFieldsParams[item.field]"
          :disabled="item.type !== 'input'"
          :placeholder="`请输入${item.title}`"
          class="!w-240px"
          clearable
          @keyup.enter="handleQuery"
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
    <el-table v-loading="loading" :data="list" border>
      <el-table-column align="center" fixed="left" label="流程名称" prop="name" width="200" />
      <el-table-column align="center" label="流程发起人" prop="startUser.nickname" width="120" />
      <el-table-column label="流程状态" prop="status" width="120">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="发起时间"
        prop="startTime"
        width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="结束时间"
        prop="endTime"
        width="180"
      />
      <el-table-column
        v-for="(item, index) in formFields"
        :key="index"
        :label="item.title"
        :prop="item.field"
        width="120"
      >
        <!-- TODO @lesan：可以根据formField的type进行展示方式的控制，现在全部以字符串 -->
        <template #default="scope">
          {{ scope.row.formVariables[item.field] ?? '' }}
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="180">
        <template #default="scope">
          <el-button
            v-hasPermi="['bpm:process-instance:cancel']"
            link
            type="primary"
            @click="handleDetail(scope.row)"
          >
            详情
          </el-button>
          <el-button
            v-if="scope.row.status === 1"
            v-hasPermi="['bpm:process-instance:query']"
            link
            type="primary"
            @click="handleCancel(scope.row)"
          >
            取消
          </el-button>
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
import * as ProcessInstanceApi from '@/api/bpm/processInstance'
import * as UserApi from '@/api/system/user'
import * as DefinitionApi from '@/api/bpm/definition'
import { parseFormFields } from '@/components/FormCreate/src/utils'
import { ElMessageBox } from 'element-plus'

defineOptions({ name: 'BpmProcessInstanceReport' })

const router = useRouter() // 路由
const { query } = useRoute()
const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const formFields = ref()
const processDefinitionId = query.processDefinitionId as string
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  startUserId: undefined,
  name: '',
  processDefinitionKey: query.processDefinitionKey,
  status: undefined,
  createTime: [],
  endTime: [],
  formFieldsParams: {}
})
const queryFormRef = ref() // 搜索的表单
const userList = ref<any[]>([]) // 用户列表

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ProcessInstanceApi.getProcessInstanceManagerPage({
      ...queryParams,
      formFieldsParams: JSON.stringify(queryParams.formFieldsParams)
    })
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 获取流程定义 */
const getProcessDefinition = async () => {
  const processDefinition = await DefinitionApi.getProcessDefinition(processDefinitionId)
  formFields.value = parseFormCreateFields(processDefinition.formFields)
}

/** 解析表单字段 */
const parseFormCreateFields = (formFields?: string[]) => {
  const result: Array<Record<string, any>> = []
  if (formFields) {
    formFields.forEach((fieldStr: string) => {
      parseFormFields(JSON.parse(fieldStr), result)
    })
  }
  return result
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  queryParams.formFieldsParams = {}
  handleQuery()
}

/** 查看详情 */
const handleDetail = (row) => {
  router.push({
    name: 'BpmProcessInstanceDetail',
    query: {
      id: row.id
    }
  })
}

/** 取消按钮操作 */
const handleCancel = async (row) => {
  // 二次确认
  const { value } = await ElMessageBox.prompt('请输入取消原因', '取消流程', {
    confirmButtonText: t('common.ok'),
    cancelButtonText: t('common.cancel'),
    inputPattern: /^[\s\S]*.*\S[\s\S]*$/, // 判断非空，且非空格
    inputErrorMessage: '取消原因不能为空'
  })
  // 发起取消
  await ProcessInstanceApi.cancelProcessInstanceByAdmin(row.id, value)
  message.success('取消成功')
  // 刷新列表
  await getList()
}

/** 初始化 **/
onMounted(async () => {
  // 获取流程定义，用于 table column 的展示
  await getProcessDefinition()
  // 获取流程列表
  await getList()
  // 获取用户列表
  userList.value = await UserApi.getSimpleUserList()
})
</script>
