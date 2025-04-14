<template>
  <doc-alert title="【商品】商品分类" url="https://doc.iocoder.cn/mall/product-category/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="分类名称" prop="name">
        <el-input
          v-model="queryParams.name"
          class="!w-240px"
          clearable
          placeholder="请输入分类名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" /> 重置</el-button>
        <el-button
          v-hasPermi="['product:category:create']"
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
    <el-table v-loading="loading" :data="list" default-expand-all row-key="id">
      <el-table-column label="名称" min-width="240" prop="name" sortable />
      <el-table-column align="center" label="分类图标" min-width="80" prop="picUrl">
        <template #default="scope">
          <img :src="scope.row.picUrl" alt="移动端分类图" class="h-36px" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="排序" min-width="150" prop="sort" />
      <el-table-column align="center" label="状态" min-width="150" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        prop="createTime"
        width="180"
      />
      <el-table-column align="center" label="操作" min-width="180">
        <template #default="scope">
          <el-button
            v-hasPermi="['product:category:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-if="scope.row.parentId > 0"
            v-hasPermi="['product:spu:query']"
            link
            type="primary"
            @click="handleViewSpu(scope.row.id)"
          >
            查看商品
          </el-button>
          <el-button
            v-hasPermi="['product:category:delete']"
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
  <CategoryForm ref="formRef" @success="getList" />
</template>
<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { handleTree } from '@/utils/tree'
import { dateFormatter } from '@/utils/formatTime'
import * as ProductCategoryApi from '@/api/mall/product/category'
import CategoryForm from './CategoryForm.vue'

defineOptions({ name: 'ProductCategory' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<any[]>([]) // 列表的数据
const queryParams = reactive({
  name: undefined
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ProductCategoryApi.getCategoryList(queryParams)
    list.value = handleTree(data, 'id', 'parentId')
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
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
    await ProductCategoryApi.deleteCategory(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 查看商品操作 */
const router = useRouter() // 路由
const handleViewSpu = (id: number) => {
  router.push({
    name: 'ProductSpu',
    query: { categoryId: id }
  })
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
