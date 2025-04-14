<template>
  <el-table v-loading="props.loading" :data="props.list" border stripe style="margin-top: 10px">
    <el-table-column align="center" label="编号" prop="mediaId" />
    <el-table-column align="center" label="文件名" prop="name" />
    <el-table-column align="center" label="语音">
      <template #default="scope">
        <WxVoicePlayer v-if="scope.row.url" :url="scope.row.url" />
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
    <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
      <template #default="scope">
        <el-button link type="primary" @click="emit('delete', scope.row.id)">
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
import WxVoicePlayer from '@/views/mp/components/wx-voice-play'
import { dateFormatter } from '@/utils/formatTime'

const props = defineProps<{
  list: any[]
  loading: boolean
}>()

const emit = defineEmits<{
  (e: 'delete', v: number)
}>()
</script>
