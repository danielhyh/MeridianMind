<template>
  <doc-alert title="AI 绘图创作" url="https://doc.iocoder.cn/ai/image/" />

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
      <el-form-item label="平台" prop="platform">
        <el-select v-model="queryParams.status" class="!w-240px" clearable placeholder="请选择平台">
          <el-option
            v-for="dict in getStrDictOptions(DICT_TYPE.AI_PLATFORM)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="绘画状态" prop="status">
        <el-select
          v-model="queryParams.status"
          class="!w-240px"
          clearable
          placeholder="请选择绘画状态"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.AI_IMAGE_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="是否发布" prop="publicStatus">
        <el-select
          v-model="queryParams.publicStatus"
          class="!w-240px"
          clearable
          placeholder="请选择是否发布"
        >
          <el-option
            v-for="dict in getBoolDictOptions(DICT_TYPE.INFRA_BOOLEAN_STRING)"
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
      <el-table-column align="center" fixed="left" label="编号" prop="id" width="180" />
      <el-table-column align="center" fixed="left" label="图片" prop="picUrl" width="110px">
        <template #default="{ row }">
          <el-image
            v-if="row.picUrl?.length > 0"
            :preview-src-list="[row.picUrl]"
            :src="row.picUrl"
            class="h-80px w-80px"
            fit="cover"
            lazy
            preview-teleported
          />
        </template>
      </el-table-column>
      <el-table-column align="center" label="用户" prop="userId" width="180">
        <template #default="scope">
          <span>{{ userList.find((item) => item.id === scope.row.userId)?.nickname }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="平台" prop="platform" width="120">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AI_PLATFORM" :value="scope.row.platform" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="模型" prop="model" width="180" />
      <el-table-column align="center" label="绘画状态" prop="status" width="100">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.AI_IMAGE_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="是否发布" prop="publicStatus">
        <template #default="scope">
          <el-switch
            v-model="scope.row.publicStatus"
            :active-value="true"
            :disabled="scope.row.status !== AiImageStatusEnum.SUCCESS"
            :inactive-value="false"
            @change="handleUpdatePublicStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" label="提示词" prop="prompt" width="180" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        prop="createTime"
        width="180px"
      />
      <el-table-column align="center" label="宽度" prop="width" />
      <el-table-column align="center" label="高度" prop="height" />
      <el-table-column align="center" label="错误信息" prop="errorMessage" />
      <el-table-column align="center" label="任务编号" prop="taskId" />
      <el-table-column align="center" fixed="right" label="操作" width="100">
        <template #default="scope">
          <el-button
            v-hasPermi="['ai:image:delete']"
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
import { getIntDictOptions, DICT_TYPE, getStrDictOptions, getBoolDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { ImageApi, ImageVO } from '@/api/ai/image'
import * as UserApi from '@/api/system/user'
import { AiImageStatusEnum } from '@/views/ai/utils/constants'

/** AI 绘画 列表 */
defineOptions({ name: 'AiImageManager' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<ImageVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: undefined,
  platform: undefined,
  status: undefined,
  publicStatus: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单
const userList = ref<UserApi.UserVO[]>([]) // 用户列表

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await ImageApi.getImagePage(queryParams)
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
    await ImageApi.deleteImage(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 修改是否发布 */
const handleUpdatePublicStatusChange = async (row: ImageVO) => {
  try {
    // 修改状态的二次确认
    const text = row.publicStatus ? '公开' : '私有'
    await message.confirm('确认要"' + text + '"该图片吗?')
    // 发起修改状态
    await ImageApi.updateImage({
      id: row.id,
      publicStatus: row.publicStatus
    })
    await getList()
  } catch {
    row.publicStatus = !row.publicStatus
  }
}

/** 初始化 **/
onMounted(async () => {
  getList()
  // 获得用户列表
  userList.value = await UserApi.getSimpleUserList()
})
</script>
