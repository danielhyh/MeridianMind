<!--
  - Copyright (C) 2018-2019
  - All rights reserved, Designed By www.joolun.com
  【微信消息 - 视频】
  芋道源码：
  ① bug 修复：
    1）joolun 的做法：使用 mediaId 从微信公众号，下载对应的 mp4 素材，从而播放内容；
      存在的问题：mediaId 有效期是 3 天，超过时间后无法播放
    2）重构后的做法：后端接收到微信公众号的视频消息后，将视频消息的 media_id 的文件内容保存到文件服务器中，这样前端可以直接使用 URL 播放。
  ② 体验优化：弹窗关闭后，自动暂停视频的播放

-->
<template>
  <div @click="playVideo()">
    <!-- 提示 -->
    <div>
      <Icon :size="32" class="mr-5px" icon="ep:video-play" />
      <p class="text-sm">点击播放视频</p>
    </div>

    <!-- 弹窗播放 -->
    <el-dialog v-model="dialogVideo" append-to-body title="视频播放">
      <video-player
        v-if="dialogVideo"
        :playback-rates="[0.7, 1.0, 1.5, 2.0]"
        :src="props.url"
        :volume="0.6"
        :width="800"
        class="video-player vjs-big-play-centered"
        controls
        crossorigin="anonymous"
        playsinline
        poster=""
      />
      <!--     事件，暫時沒用
      @mounted="handleMounted"-->
      <!--        @ready="handleEvent($event)"-->
      <!--        @play="handleEvent($event)"-->
      <!--        @pause="handleEvent($event)"-->
      <!--        @ended="handleEvent($event)"-->
      <!--        @loadeddata="handleEvent($event)"-->
      <!--        @waiting="handleEvent($event)"-->
      <!--        @playing="handleEvent($event)"-->
      <!--        @canplay="handleEvent($event)"-->
      <!--        @canplaythrough="handleEvent($event)"-->
      <!--        @timeupdate="handleEvent(player?.currentTime())"-->
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import 'video.js/dist/video-js.css'
import { VideoPlayer } from '@videojs-player/vue'

defineOptions({ name: 'WxVideoPlayer' })

const props = defineProps({
  url: {
    type: String,
    required: true
  }
})

const dialogVideo = ref(false)

// const handleEvent = (log) => {
//   console.log('Basic player event', log)
// }

const playVideo = () => {
  dialogVideo.value = true
}
</script>
