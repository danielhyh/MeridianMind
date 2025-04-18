<template>
  <doc-alert title="上传下载" url="https://doc.iocoder.cn/file/" />
  <!-- 搜索 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="文件路径" prop="path">
        <el-input
          v-model="queryParams.path"
          class="!w-240px"
          clearable
          placeholder="请输入文件路径"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="文件类型" prop="type" width="80">
        <el-input
          v-model="queryParams.type"
          class="!w-240px"
          clearable
          placeholder="请输入文件类型"
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
        <el-button plain type="primary" @click="openForm">
          <Icon class="mr-5px" icon="ep:upload" /> 上传文件
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column :show-overflow-tooltip="true" align="center" label="文件名" prop="name" />
      <el-table-column :show-overflow-tooltip="true" align="center" label="文件路径" prop="path" />
      <el-table-column :show-overflow-tooltip="true" align="center" label="URL" prop="url" />
      <el-table-column
        :formatter="fileSizeFormatter"
        align="center"
        label="文件大小"
        prop="size"
        width="120"
      />
      <el-table-column align="center" label="文件类型" prop="type" width="180px" />
      <el-table-column align="center" label="文件内容" prop="url" width="110px">
        <template #default="{ row }">
          <!-- 图片预览 -->
          <el-image
            v-if="row.type.includes('image')"
            :preview-src-list="[row.url]"
            :src="row.url"
            class="h-80px w-80px"
            fit="cover"
            lazy
            preview-teleported
          />

          <!-- 音频预览 -->
          <div v-else-if="row.type.includes('audio')" class="audio-preview">
            <el-button size="small" @click="openAudioDialog(row)">
              <Icon class="mr-5px" icon="ep:headset" /> 播放音频
            </el-button>
          </div>

          <!-- 视频预览 -->
          <div v-else-if="row.type.includes('video')" class="video-preview">
            <el-button size="small" @click="openVideoDialog(row)">
              <Icon class="mr-5px" icon="ep:video-play" /> 播放视频
            </el-button>
          </div>

          <!-- PDF预览 -->
          <el-link
            v-else-if="row.type.includes('pdf')"
            :href="row.url"
            :underline="false"
            target="_blank"
            type="primary"
          >预览</el-link>

          <!-- 其他文件下载 -->
          <el-link
            v-else
            :href="row.url"
            :underline="false"
            download
            target="_blank"
            type="primary"
          >下载</el-link>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="上传时间"
        prop="createTime"
        width="180"
      />
      <el-table-column align="center" label="操作">
        <template #default="scope">
          <el-button link type="primary" @click="copyToClipboard(scope.row.url)">
            复制链接
          </el-button>
          <el-button
            v-hasPermi="['infra:file:delete']"
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
  <FileForm ref="formRef" @success="getList" />
  <!-- 音频播放弹窗 -->
  <el-dialog
    v-model="audioDialogVisible"
    title="音频播放"
    width="400px"
    :destroy-on-close="true"
    center
  >
    <audio-player :src="currentAudioUrl" :title="currentAudioName" />
  </el-dialog>

  <!-- 视频播放弹窗 -->
  <el-dialog
    v-model="videoDialogVisible"
    title="视频播放"
    width="70%"
    :destroy-on-close="true"
    center
  >
    <video-player :src="currentVideoUrl" :type="currentVideoType" :autoplay="true" />
  </el-dialog>
</template>
<script lang="ts" setup>
import { fileSizeFormatter } from '@/utils'
import { dateFormatter } from '@/utils/formatTime'
import * as FileApi from '@/api/infra/file'
import FileForm from './FileForm.vue'
import { AudioPlayer } from '@/components/AudioPlayer'
import { VideoPlayer } from '@/components/VideoPlayer'

defineOptions({ name: 'InfraFile' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  type: undefined,
  path: undefined,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单

// 音频预览相关
const audioDialogVisible = ref(false)
const currentAudioUrl = ref('')
const currentAudioName = ref('')

// 视频预览相关
const videoDialogVisible = ref(false)
const currentVideoUrl = ref('')
const currentVideoType = ref('')

// 打开音频预览弹窗
const openAudioDialog = (row) => {
  currentAudioUrl.value = row.url
  currentAudioName.value = row.name
  audioDialogVisible.value = true
}

// 打开视频预览弹窗
const openVideoDialog = (row) => {
  currentVideoUrl.value = row.url
  currentVideoType.value = row.type
  videoDialogVisible.value = true
}
/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await FileApi.getFilePage(queryParams)
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
const openForm = () => {
  formRef.value.open()
}

/** 复制到剪贴板方法 */
const copyToClipboard = (text: string) => {
  navigator.clipboard.writeText(text).then(() => {
    message.success('复制成功')
  })
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await FileApi.deleteFile(id)
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
