<template>
  <doc-alert title="【交易】快递发货" url="https://doc.iocoder.cn/mall/trade-delivery-express/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="100px"
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
      <el-form-item label="计费方式" prop="chargeMode">
        <el-select
          v-model="queryParams.chargeMode"
          class="!w-240px"
          clearable
          placeholder="计费方式"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.EXPRESS_CHARGE_MODE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" /> 重置</el-button>
        <el-button
          v-hasPermi="['trade:delivery:express-template:create']"
          plain
          type="primary"
          @click="openForm('create')"
        >
          <Icon class="mr-5px" icon="ep:plus" />
          新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" min-width="60" prop="id" />
      <el-table-column label="模板名称" min-width="100" prop="name" />
      <el-table-column align="center" label="计费方式" min-width="100" prop="chargeMode">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.EXPRESS_CHARGE_MODE" :value="scope.row.chargeMode" />
        </template>
      </el-table-column>
      <el-table-column label="排序" min-width="100" prop="sort" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        prop="createTime"
        width="180"
      />
      <el-table-column align="center" label="操作">
        <template #default="scope">
          <el-button
            v-hasPermi="['trade:delivery:express-template:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-hasPermi="['trade:delivery:express-template:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <ExpressTemplateForm ref="formRef" @success="getList" />
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as DeliveryExpressTemplateApi from '@/api/mall/trade/delivery/expressTemplate'
import ExpressTemplateForm from './ExpressTemplateForm.vue'

defineOptions({ name: 'DeliveryExpressTemplate' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const total = ref(0) // 列表的总页数
const loading = ref(true) // 列表的加载中
const list = ref<any[]>([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: '',
  chargeMode: undefined
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await DeliveryExpressTemplateApi.getDeliveryExpressTemplatePage(queryParams)
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
    await DeliveryExpressTemplateApi.deleteDeliveryExpressTemplate(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
