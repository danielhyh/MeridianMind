<template>
  <doc-alert title="会员等级、积分、签到" url="https://doc.iocoder.cn/member/level/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="等级名称" prop="name">
        <el-input
          v-model="queryParams.name"
          class="!w-240px"
          clearable
          placeholder="请输入等级名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" class="!w-240px" clearable placeholder="请选择状态">
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
        <el-button v-hasPermi="['member:level:create']" type="primary" @click="openForm('create')">
          <Icon class="mr-5px" icon="ep:plus" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
      <el-table-column align="center" label="编号" min-width="60" prop="id" />
      <el-table-column align="center" label="等级图标" min-width="80" prop="icon">
        <template #default="scope">
          <el-image
            :preview-src-list="[scope.row.icon]"
            :src="scope.row.icon"
            class="h-30px w-30px"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" label="等级背景图" min-width="100" prop="backgroundUrl">
        <template #default="scope">
          <el-image
            :preview-src-list="[scope.row.backgroundUrl]"
            :src="scope.row.backgroundUrl"
            class="h-30px w-30px"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" label="等级名称" min-width="100" prop="name" />
      <el-table-column align="center" label="等级" min-width="60" prop="level" />
      <el-table-column align="center" label="升级经验" min-width="80" prop="experience" />
      <el-table-column align="center" label="享受折扣(%)" min-width="110" prop="discountPercent" />
      <el-table-column align="center" label="状态" min-width="70" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        min-width="170"
        prop="createTime"
      />
      <el-table-column align="center" fixed="right" label="操作" min-width="110px">
        <template #default="scope">
          <el-button
            v-hasPermi="['member:level:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-hasPermi="['member:level:delete']"
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
  <LevelForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as LevelApi from '@/api/member/level'
import LevelForm from './LevelForm.vue'

/** 会员等级管理 **/
defineOptions({ name: 'MemberLevel' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref([]) // 列表的数据
const queryParams = reactive({
  name: null,
  status: null
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    list.value = await LevelApi.getLevelList(queryParams)
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
    await LevelApi.deleteLevel(id)
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
