<template>
  <doc-alert title="【营销】商城装修" url="https://doc.iocoder.cn/mall/diy/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="模板名称" prop="name">
        <el-input
          v-model="queryParams.name"
          class="!w-240px"
          clearable
          placeholder="请输入模板名称"
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
        <el-button
          v-hasPermi="['promotion:diy-template:create']"
          plain
          type="primary"
          @click="openForm('create')"
        >
          <Icon class="mr-5px" icon="ep:plus" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column align="center" label="编号" prop="id" />
      <el-table-column align="center" label="预览图" prop="previewPicUrls">
        <template #default="scope">
          <el-image
            v-for="(url, index) in scope.row.previewPicUrls"
            :key="index"
            :initial-index="index"
            :preview-src-list="scope.row.previewPicUrls"
            :src="url"
            class="h-40px max-w-40px"
            preview-teleported
          />
        </template>
      </el-table-column>
      <el-table-column align="center" label="模板名称" prop="name" />
      <el-table-column align="center" label="是否使用" prop="used">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="scope.row.used" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="使用时间"
        prop="usedTime"
        width="180px"
      />
      <el-table-column align="center" label="备注" prop="remark" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        prop="createTime"
        width="180px"
      />
      <el-table-column align="center" label="操作" width="200">
        <template #default="scope">
          <el-button
            v-hasPermi="['promotion:diy-template:update']"
            link
            type="primary"
            @click="handleDecorate(scope.row.id)"
          >
            装修
          </el-button>
          <el-button
            v-hasPermi="['promotion:diy-template:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <template v-if="!scope.row.used">
            <el-button
              v-hasPermi="['promotion:diy-template:use']"
              link
              type="primary"
              @click="handleUse(scope.row)"
            >
              使用
            </el-button>
            <el-button
              v-hasPermi="['promotion:diy-template:delete']"
              link
              type="danger"
              @click="handleDelete(scope.row.id)"
            >
              删除
            </el-button>
          </template>
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

  <!-- 表单弹窗：添加/修改 -->
  <DiyTemplateForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as DiyTemplateApi from '@/api/mall/promotion/diy/template'
import DiyTemplateForm from './DiyTemplateForm.vue'
import { DICT_TYPE } from '@/utils/dict'

/** 装修模板 */
defineOptions({ name: 'DiyTemplate' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: null,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await DiyTemplateApi.getDiyTemplatePage(queryParams)
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

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await DiyTemplateApi.deleteDiyTemplate(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 使用模板 */
const handleUse = async (row: DiyTemplateApi.DiyTemplateVO) => {
  try {
    // 使用模板的二次确认
    await message.confirm(`是否使用模板“${row.name}”?`)
    // 发起删除
    await DiyTemplateApi.useDiyTemplate(row.id!)
    message.success('使用成功')
    // 刷新列表
    await getList()
  } catch {}
}

/** 打开装修页面 */
const { push } = useRouter()
const handleDecorate = (id: number) => {
  push({ name: 'DiyTemplateDecorate', params: { id } })
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
