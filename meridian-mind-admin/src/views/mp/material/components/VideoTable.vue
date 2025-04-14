<template>
  <el-table v-loading="props.loading" :data="props.list" border stripe style="margin-top: 10px">
    <el-table-column align="center" label="编号" prop="mediaId" />
    <el-table-column align="center" label="文件名" prop="name" />
    <el-table-column align="center" label="标题" prop="title" />
    <el-table-column align="center" label="介绍" prop="introduction" />
    <el-table-column align="center" label="视频">
      <template #default="scope">
        <WxVideoPlayer v-if="scope.row.url" :url="scope.row.url" />
      </template>
    </el-table-column>
    <el-table-column
      :formatter="dateFormatter"
      align="center"
      label="上传时间"
      prop="createTime"
      width="180"
    >
      <template #default="scope">
        <span>{{ scope.row.createTime }}</span>
      </template>
    </el-table-column>
    <el-table-column align="center" fixed="right" label="操作">
      <template #default="scope">
        <el-button link type="primary" @click="handleDownload(scope.row.url)">
          <Icon icon="ep:download" />下载
        </el-button>
        <el-button
          v-hasPermi="['mp:material:delete']"
          link
          type="primary"
          @click="emit('delete', scope.row.id)"
        >
          <Icon icon="ep:delete" />删除
        </el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script lang="ts" setup>
import WxVideoPlayer from '@/views/mp/components/wx-video-play'
import { dateFormatter } from '@/utils/formatTime'

const props = defineProps<{
  list: any[]
  loading: boolean
}>()

const emit = defineEmits<{
  (e: 'delete', v: number)
  (e: 'download', v: string)
}>()

// 下载文件
const handleDownload = (url: string) => {
  window.open(url, '_blank')
}
</script>
