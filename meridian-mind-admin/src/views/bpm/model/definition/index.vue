<template>
  <doc-alert title="工作流手册" url="https://doc.iocoder.cn/bpm/" />

  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column align="center" label="定义编号" min-width="250" prop="id" />
      <el-table-column align="center" label="流程名称" min-width="150" prop="name" />
      <el-table-column align="center" label="流程图标" min-width="50">
        <template #default="{ row }">
          <el-image v-if="row.icon" :src="row.icon" class="h-24px w-24pxrounded" />
        </template>
      </el-table-column>
      <el-table-column label="可见范围" min-width="100" prop="startUserIds">
        <template #default="{ row }">
          <el-text v-if="!row.startUsers?.length"> 全部可见 </el-text>
          <el-text v-else-if="row.startUsers.length === 1">
            {{ row.startUsers[0].nickname }}
          </el-text>
          <el-text v-else>
            <el-tooltip
              :content="row.startUsers.map((user: any) => user.nickname).join('、')"
              class="box-item"
              effect="dark"
              placement="top"
            >
              {{ row.startUsers[0].nickname }}等 {{ row.startUsers.length }} 人可见
            </el-tooltip>
          </el-text>
        </template>
      </el-table-column>
      <el-table-column label="流程类型" min-width="120" prop="modelType">
        <template #default="{ row }">
          <dict-tag :type="DICT_TYPE.BPM_MODEL_TYPE" :value="row.modelType" />
        </template>
      </el-table-column>
      <el-table-column label="表单信息" min-width="150" prop="formType">
        <template #default="scope">
          <el-button
            v-if="scope.row.formType === BpmModelFormType.NORMAL"
            link
            type="primary"
            @click="handleFormDetail(scope.row)"
          >
            <span>{{ scope.row.formName }}</span>
          </el-button>
          <el-button
            v-else-if="scope.row.formType === BpmModelFormType.CUSTOM"
            link
            type="primary"
            @click="handleFormDetail(scope.row)"
          >
            <span>{{ scope.row.formCustomCreatePath }}</span>
          </el-button>
          <label v-else>暂无表单</label>
        </template>
      </el-table-column>
      <el-table-column align="center" label="流程版本" min-width="80">
        <template #default="scope">
          <el-tag>v{{ scope.row.version }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="部署时间"
        prop="deploymentTime"
        width="180"
      />
      <el-table-column align="center" label="操作">
        <template #default="scope">
          <el-button
            v-hasPermi="['bpm:model:update']"
            link
            type="primary"
            @click="openModelForm(scope.row.id)"
          >
            恢复
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

  <!-- 弹窗：表单详情 -->
  <Dialog v-model="formDetailVisible" title="表单详情" width="800">
    <form-create :option="formDetailPreview.option" :rule="formDetailPreview.rule" />
  </Dialog>
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as DefinitionApi from '@/api/bpm/definition'
import { setConfAndFields2 } from '@/utils/formCreate'
import { DICT_TYPE } from '@/utils/dict'
import { BpmModelFormType } from '@/utils/constants'

defineOptions({ name: 'BpmProcessDefinition' })

const { push } = useRouter() // 路由
const { query } = useRoute() // 查询参数

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  key: query.key
})

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await DefinitionApi.getProcessDefinitionPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 流程表单的详情按钮操作 */
const formDetailVisible = ref(false)
const formDetailPreview = ref({
  rule: [],
  option: {}
})
const handleFormDetail = async (row: any) => {
  if (row.formType == BpmModelFormType.NORMAL) {
    // 设置表单
    setConfAndFields2(formDetailPreview, row.formConf, row.formFields)
    // 弹窗打开
    formDetailVisible.value = true
  } else {
    await push({
      path: row.formCustomCreatePath
    })
  }
}

/** 恢复流程模型弹窗 */
const openModelForm = async (id?: number) => {
  await push({
    name: 'BpmModelUpdate',
    params: { id, type: 'definition' }
  })
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.flow-icon {
  display: flex;
  width: 38px;
  height: 38px;
  margin-right: 10px;
  background-color: var(--el-color-primary);
  border-radius: 0.25rem;
  align-items: center;
  justify-content: center;
}
</style>
