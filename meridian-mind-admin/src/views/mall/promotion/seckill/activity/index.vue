<template>
  <doc-alert title="【营销】秒杀活动" url="https://doc.iocoder.cn/mall/promotion-seckill/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="活动名称" prop="name">
        <el-input
          v-model="queryParams.name"
          class="!w-240px"
          clearable
          placeholder="请输入活动名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="活动状态" prop="status">
        <el-select
          v-model="queryParams.status"
          class="!w-240px"
          clearable
          placeholder="请选择活动状态"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.COMMON_STATUS)"
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
          v-hasPermi="['promotion:seckill-activity:create']"
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
      <el-table-column label="活动编号" min-width="80" prop="id" />
      <el-table-column label="活动名称" min-width="140" prop="name" />
      <el-table-column
        :show-overflow-tooltip="false"
        label="秒杀时段"
        prop="configIds"
        width="220px"
      >
        <template #default="scope">
          <el-tag v-for="(configId, index) in scope.row.configIds" :key="index" class="mr-5px">
            {{ formatConfigNames(configId) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="活动时间" min-width="210">
        <template #default="scope">
          {{ formatDate(scope.row.startTime, 'YYYY-MM-DD') }}
          ~ {{ formatDate(scope.row.endTime, 'YYYY-MM-DD') }}
        </template>
      </el-table-column>
      <el-table-column label="商品图片" min-width="80" prop="spuName">
        <template #default="scope">
          <el-image
            :preview-src-list="[scope.row.picUrl]"
            :src="scope.row.picUrl"
            class="h-40px w-40px"
            preview-teleported
          />
        </template>
      </el-table-column>
      <el-table-column label="商品标题" min-width="300" prop="spuName" />
      <el-table-column
        :formatter="fenToYuanFormat"
        label="原价"
        min-width="100"
        prop="marketPrice"
      />
      <el-table-column label="原价" min-width="100" prop="marketPrice" />
      <el-table-column label="秒杀价" min-width="100" prop="seckillPrice">
        <template #default="scope">
          {{ formatSeckillPrice(scope.row.products) }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="活动状态" min-width="100" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="库存" min-width="80" prop="stock" />
      <el-table-column align="center" label="总库存" min-width="80" prop="totalStock" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        prop="createTime"
        width="180px"
      />
      <el-table-column align="center" fixed="right" label="操作" width="150px">
        <template #default="scope">
          <el-button
            v-hasPermi="['promotion:seckill-activity:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-if="scope.row.status === 0"
            v-hasPermi="['promotion:seckill-activity:close']"
            link
            type="danger"
            @click="handleClose(scope.row.id)"
          >
            关闭
          </el-button>
          <el-button
            v-else
            v-hasPermi="['promotion:seckill-activity:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            删除
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

  <!-- 表单弹窗：添加/修改 -->
  <SeckillActivityForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as SeckillActivityApi from '@/api/mall/promotion/seckill/seckillActivity'
import { SeckillConfigApi } from '@/api/mall/promotion/seckill/seckillConfig'
import SeckillActivityForm from './SeckillActivityForm.vue'
import { formatDate } from '@/utils/formatTime'
import { fenToYuanFormat } from '@/utils/formatter'
import { fenToYuan } from '@/utils'

defineOptions({ name: 'SeckillActivity' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: null,
  status: null
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出的加载中

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await SeckillActivityApi.getSeckillActivityPage(queryParams)
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

/** 关闭按钮操作 */
const handleClose = async (id: number) => {
  try {
    // 关闭的二次确认
    await message.confirm('确认关闭该秒杀活动吗？')
    // 发起关闭
    await SeckillActivityApi.closeSeckillActivity(id)
    message.success('关闭成功')
    // 刷新列表
    await getList()
  } catch {}
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await SeckillActivityApi.deleteSeckillActivity(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

const configList = ref([]) // 时段配置精简列表
const formatConfigNames = (configId) => {
  const config = configList.value.find((item) => item.id === configId)
  return config != null ? `${config.name}[${config.startTime} ~ ${config.endTime}]` : ''
}

const formatSeckillPrice = (products) => {
  const seckillPrice = Math.min(...products.map((item) => item.seckillPrice))
  return `￥${fenToYuan(seckillPrice)}`
}

/** 初始化 **/
onMounted(async () => {
  await getList()
  // 获得秒杀时间段
  configList.value = await SeckillConfigApi.getSimpleSeckillConfigList()
})
</script>
