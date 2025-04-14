<template>
  <el-table v-loading="props.loading" :data="props.list">
    <el-table-column
      v-if="msgType === MsgType.Message"
      align="center"
      label="请求消息类型"
      prop="requestMessageType"
    />
    <el-table-column
      v-if="msgType === MsgType.Keyword"
      align="center"
      label="关键词"
      prop="requestKeyword"
    />
    <el-table-column
      v-if="msgType === MsgType.Keyword"
      align="center"
      label="匹配类型"
      prop="requestMatch"
    >
      <template #default="scope">
        <dict-tag :type="DICT_TYPE.MP_AUTO_REPLY_REQUEST_MATCH" :value="scope.row.requestMatch" />
      </template>
    </el-table-column>
    <el-table-column align="center" label="回复消息类型">
      <template #default="scope">
        <dict-tag :type="DICT_TYPE.MP_MESSAGE_TYPE" :value="scope.row.responseMessageType" />
      </template>
    </el-table-column>
    <el-table-column align="center" label="回复内容">
      <template #default="scope">
        <div v-if="scope.row.responseMessageType === 'text'">{{ scope.row.responseContent }}</div>
        <div v-else-if="scope.row.responseMessageType === 'voice'">
          <WxVoicePlayer v-if="scope.row.responseMediaUrl" :url="scope.row.responseMediaUrl" />
        </div>
        <div v-else-if="scope.row.responseMessageType === 'image'">
          <a :href="scope.row.responseMediaUrl" target="_blank">
            <img :src="scope.row.responseMediaUrl" style="width: 100px" />
          </a>
        </div>
        <div
          v-else-if="
            scope.row.responseMessageType === 'video' ||
            scope.row.responseMessageType === 'shortvideo'
          "
        >
          <WxVideoPlayer
            v-if="scope.row.responseMediaUrl"
            :url="scope.row.responseMediaUrl"
            style="margin-top: 10px"
          />
        </div>
        <div v-else-if="scope.row.responseMessageType === 'news'">
          <WxNews :articles="scope.row.responseArticles" />
        </div>
        <div v-else-if="scope.row.responseMessageType === 'music'">
          <WxMusic
            :description="scope.row.responseDescription"
            :hq-music-url="scope.row.responseHqMusicUrl"
            :music-url="scope.row.responseMusicUrl"
            :thumb-media-url="scope.row.responseThumbMediaUrl"
            :title="scope.row.responseTitle"
          />
        </div>
      </template>
    </el-table-column>
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
          v-hasPermi="['mp:auto-reply:update']"
          link
          type="primary"
          @click="emit('on-update', scope.row.id)"
        >
          修改
        </el-button>
        <el-button
          v-hasPermi="['mp:auto-reply:delete']"
          link
          type="danger"
          @click="emit('on-delete', scope.row.id)"
        >
          删除
        </el-button>
      </template>
    </el-table-column>
  </el-table>
</template>
<script lang="ts" setup>
import WxVideoPlayer from '@/views/mp/components/wx-video-play'
import WxVoicePlayer from '@/views/mp/components/wx-voice-play'
import WxMusic from '@/views/mp/components/wx-music'
import WxNews from '@/views/mp/components/wx-news'
import { dateFormatter } from '@/utils/formatTime'
import { DICT_TYPE } from '@/utils/dict'
import { MsgType } from './types'

const props = defineProps<{
  loading: boolean
  list: any[]
  msgType: MsgType
}>()

const emit = defineEmits<{
  (e: 'on-update', v: number)
  (e: 'on-delete', v: number)
}>()
</script>
