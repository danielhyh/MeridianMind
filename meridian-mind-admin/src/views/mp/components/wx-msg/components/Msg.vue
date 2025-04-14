<template>
  <div>
    <MsgEvent v-if="item.type === MsgType.Event" :item="item" />

    <div v-else-if="item.type === MsgType.Text">{{ item.content }}</div>

    <div v-else-if="item.type === MsgType.Voice">
      <WxVoicePlayer :content="item.recognition" :url="item.mediaUrl" />
    </div>

    <div v-else-if="item.type === MsgType.Image">
      <a :href="item.mediaUrl" target="_blank">
        <img :src="item.mediaUrl" style="width: 100px" />
      </a>
    </div>

    <div
      v-else-if="item.type === MsgType.Video || item.type === 'shortvideo'"
      style="text-align: center"
    >
      <WxVideoPlayer :url="item.mediaUrl" />
    </div>

    <div v-else-if="item.type === MsgType.Link" class="avue-card__detail">
      <el-link :href="item.url" :underline="false" target="_blank" type="success">
        <div class="avue-card__title"><i class="el-icon-link"></i>{{ item.title }}</div>
      </el-link>
      <div class="avue-card__info" style="height: unset">{{ item.description }}</div>
    </div>

    <div v-else-if="item.type === MsgType.Location">
      <WxLocation :label="item.label" :location-x="item.locationX" :location-y="item.locationY" />
    </div>

    <div v-else-if="item.type === MsgType.News" style="width: 300px">
      <WxNews :articles="item.articles" />
    </div>

    <div v-else-if="item.type === MsgType.Music">
      <WxMusic
        :description="item.description"
        :hq-music-url="item.hqMusicUrl"
        :music-url="item.musicUrl"
        :thumb-media-url="item.thumbMediaUrl"
        :title="item.title"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import WxVideoPlayer from '@/views/mp/components/wx-video-play'
import WxVoicePlayer from '@/views/mp/components/wx-voice-play'
import WxNews from '@/views/mp/components/wx-news'
import WxLocation from '@/views/mp/components/wx-location'
import WxMusic from '@/views/mp/components/wx-music'
import MsgEvent from './MsgEvent.vue'
import { MsgType } from '../types'

defineOptions({ name: 'Msg' })

const props = defineProps<{
  item: any
}>()

const item = ref<any>(props.item)
</script>

<style scoped></style>
