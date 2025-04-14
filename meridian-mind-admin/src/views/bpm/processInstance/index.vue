<template>
  <doc-alert title="流程发起、取消、重新发起" url="https://doc.iocoder.cn/bpm/process-instance/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="" prop="name">
        <el-input
          v-model="queryParams.name"
          class="!w-240px"
          clearable
          placeholder="请输入流程名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>

      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" /> 搜索</el-button>
      </el-form-item>

      <el-form-item class="absolute right-[300px]" label="" prop="category">
        <el-select
          v-model="queryParams.category"
          class="!w-155px"
          clearable
          placeholder="请选择流程分类"
          @change="handleQuery"
        >
          <el-option
            v-for="category in categoryList"
            :key="category.code"
            :label="category.name"
            :value="category.code"
          />
        </el-select>
      </el-form-item>
      <el-form-item class="absolute right-[130px]" label="" prop="status">
        <el-select
          v-model="queryParams.status"
          class="!w-155px"
          clearable
          placeholder="请选择流程状态"
          @change="handleQuery"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>

      <!-- 高级筛选 -->
      <el-form-item class="absolute right-0">
        <el-popover
          :show-arrow="false"
          :visible="showPopover"
          :width="400"
          persistent
          placement="bottom-end"
        >
          <template #reference>
            <el-button @click="showPopover = !showPopover">
              <Icon class="mr-5px" icon="ep:plus" />高级筛选
            </el-button>
          </template>
          <el-form-item
            class="font-bold"
            label="所属流程"
            label-position="top"
            prop="processDefinitionKey"
          >
            <el-select
              v-model="queryParams.processDefinitionKey"
              class="!w-390px"
              clearable
              placeholder="请选择流程定义"
              @change="handleQuery"
            >
              <el-option
                v-for="item in processDefinitionList"
                :key="item.key"
                :label="item.name"
                :value="item.key"
              />
            </el-select>
          </el-form-item>
          <el-form-item class="font-bold" label="发起时间" label-position="top" prop="createTime">
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
          <el-form-item class="font-bold" label-position="top">
            <div class="flex justify-end w-full">
              <el-button @click="resetQuery">清空</el-button>
              <el-button @click="showPopover = false">取消</el-button>
              <el-button type="primary" @click="handleQuery">确认</el-button>
            </div>
          </el-form-item>
        </el-popover>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column align="center" fixed="left" label="流程名称" min-width="200px" prop="name" />
      <el-table-column fixed="left" label="摘要" prop="summary" width="180">
        <template #default="scope">
          <div v-if="scope.row.summary && scope.row.summary.length > 0" class="flex flex-col">
            <div v-for="(item, index) in scope.row.summary" :key="index">
              <el-text type="info"> {{ item.key }} : {{ item.value }} </el-text>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        fixed="left"
        label="流程分类"
        min-width="100"
        prop="categoryName"
      />
      <el-table-column label="流程状态" min-width="200" prop="status">
        <template #default="scope">
          <!-- 审批中状态 -->
          <template
            v-if="
              scope.row.status === BpmProcessInstanceStatus.RUNNING && scope.row.tasks?.length > 0
            "
          >
            <!-- 单人审批 -->
            <template v-if="scope.row.tasks.length === 1">
              <span>
                <el-button link type="primary" @click="handleDetail(scope.row)">
                  {{ scope.row.tasks[0].assigneeUser?.nickname }}
                </el-button>
                ({{ scope.row.tasks[0].name }}) 审批中
              </span>
            </template>
            <!-- 多人审批 -->
            <template v-else>
              <span>
                <el-button link type="primary" @click="handleDetail(scope.row)">
                  {{ scope.row.tasks[0].assigneeUser?.nickname }}
                </el-button>
                等 {{ scope.row.tasks.length }} 人 ({{ scope.row.tasks[0].name }})审批中
              </span>
            </template>
          </template>
          <!-- 非审批中状态 -->
          <template v-else>
            <dict-tag :type="DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS" :value="scope.row.status" />
          </template>
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
          <el-button v-else link type="primary" @click="handleCreate(scope.row)">
            重新发起
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
import { ElMessageBox } from 'element-plus'
import * as ProcessInstanceApi from '@/api/bpm/processInstance'
import { CategoryApi, CategoryVO } from '@/api/bpm/category'
import { ProcessInstanceVO } from '@/api/bpm/processInstance'
import * as DefinitionApi from '@/api/bpm/definition'
import { BpmProcessInstanceStatus } from '@/utils/constants'

defineOptions({ name: 'BpmProcessInstanceMy' })

const router = useRouter() // 路由
const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const processDefinitionList = ref<any[]>([]) // 流程定义列表
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: '',
  processDefinitionKey: undefined,
  category: undefined,
  status: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const categoryList = ref<CategoryVO[]>([]) // 流程分类列表
const showPopover = ref(false) // 高级筛选是否展示

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ProcessInstanceApi.getProcessInstanceMyPage(queryParams)
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

/** 发起流程操作 **/
const handleCreate = async (row?: ProcessInstanceVO) => {
  // 如果是【业务表单】，不支持重新发起
  if (row?.id) {
    const processDefinitionDetail = await DefinitionApi.getProcessDefinition(
      row.processDefinitionId
    )
    if (processDefinitionDetail.formType === 20) {
      message.error('重新发起流程失败，原因：该流程使用业务表单，不支持重新发起')
      return
    }
  }
  // 跳转发起流程界面
  await router.push({
    name: 'BpmProcessInstanceCreate',
    query: { processInstanceId: row?.id }
  })
}

/** 查看详情 */
const handleDetail = (row: ProcessInstanceVO) => {
  router.push({
    name: 'BpmProcessInstanceDetail',
    query: {
      id: row.id
    }
  })
}

/** 取消按钮操作 */
const handleCancel = async (row: ProcessInstanceVO) => {
  // 二次确认
  const { value } = await ElMessageBox.prompt('请输入取消原因', '取消流程', {
    confirmButtonText: t('common.ok'),
    cancelButtonText: t('common.cancel'),
    inputPattern: /^[\s\S]*.*\S[\s\S]*$/, // 判断非空，且非空格
    inputErrorMessage: '取消原因不能为空'
  })
  // 发起取消
  await ProcessInstanceApi.cancelProcessInstanceByStartUser(row.id, value)
  message.success('取消成功')
  // 刷新列表
  await getList()
}

/** 激活时 **/
onActivated(() => {
  getList()
})

/** 初始化 **/
onMounted(async () => {
  await getList()
  categoryList.value = await CategoryApi.getCategorySimpleList()
  // 获取流程定义列表
  processDefinitionList.value = await DefinitionApi.getSimpleProcessDefinitionList()
})
</script>
