<template>
  <doc-alert title="AI 写作助手" url="https://doc.iocoder.cn/ai/write/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="用户编号" prop="userId">
        <el-select
          v-model="queryParams.userId"
          class="!w-240px"
          clearable
          placeholder="请输入用户编号"
        >
          <el-option
            v-for="item in userList"
            :key="item.id"
            :label="item.nickname"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="写作类型" prop="type">
        <el-select
          v-model="queryParams.type"
          class="!w-240px"
          clearable
          placeholder="请选择写作类型"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.AI_WRITE_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="平台" prop="platform">
        <el-select
          v-model="queryParams.platform"
          class="!w-240px"
          clearable
          placeholder="请选择平台"
        >
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.AI_PLATFORM)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
      <el-table-column align="center" fixed="left" label="编号" prop="id" width="120" />
      <el-table-column align="center" label="用户" prop="userId" width="180">
        <template #default="scope">
          <span>{{ userList.find((item) => item.id === scope.row.userId)?.nickname }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="写作类型" prop="type">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AI_WRITE_TYPE" :value="scope.row.type" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="平台" prop="platform" width="120">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AI_PLATFORM" :value="scope.row.platform" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="模型" prop="model" width="180" />
      <el-table-column
        align="center"
        label="生成内容提示"
        prop="prompt"
        show-overflow-tooltip
        width="180"
      />
      <el-table-column align="center" label="生成的内容" prop="generatedContent" width="180" />
      <el-table-column align="center" label="原文" prop="originalContent" width="180" />
      <el-table-column align="center" label="长度" prop="length">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AI_WRITE_LENGTH" :value="scope.row.length" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="格式" prop="format">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AI_WRITE_FORMAT" :value="scope.row.format" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="语气" prop="tone">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AI_WRITE_TONE" :value="scope.row.tone" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="语言" prop="language">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AI_WRITE_LANGUAGE" :value="scope.row.language" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        prop="createTime"
        width="180px"
      />
      <el-table-column align="center" label="错误信息" prop="errorMessage" />
      <el-table-column align="center" label="操作">
        <template #default="scope">
          <el-button
            v-hasPermi="['ai:write:delete']"
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
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions, getStrDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { useRouter } from 'vue-router'
import { WriteApi, AiWritePageReqVO, AiWriteRespVo } from '@/api/ai/write'
import * as UserApi from '@/api/system/user'

/** AI 写作列表 */
defineOptions({ name: 'AiWriteManager' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const router = useRouter() // 路由

const loading = ref(true) // 列表的加载中
const list = ref<AiWriteRespVo[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive<AiWritePageReqVO>({
  pageNo: 1,
  pageSize: 10,
  userId: undefined,
  type: undefined,
  platform: undefined,
  createTime: undefined
})
const queryFormRef = ref() // 搜索的表单
const userList = ref<UserApi.UserVO[]>([]) // 用户列表

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await WriteApi.getWritePage(queryParams)
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

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await WriteApi.deleteWrite(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(async () => {
  getList()
  // 获得用户列表
  userList.value = await UserApi.getSimpleUserList()
})
</script>
