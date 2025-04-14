<template>
  <div :style="{ height: `${property.style.height}px` }" class="w-full">
    <el-image v-if="property.posterUrl" :src="property.posterUrl" class="w-full w-full" />
    <video
      v-else
      :autoplay="property.autoplay"
      :poster="property.posterUrl"
      :src="property.videoUrl"
      class="w-full w-full"
      controls
    ></video>
  </div>
</template>
<script lang="ts" setup>
import { VideoPlayerProperty } from './config'

/** 视频播放 */
defineOptions({ name: 'VideoPlayer' })

defineProps<{ property: VideoPlayerProperty }>()
</script>

<style lang="scss" scoped>
/* 图片 */
img {
  display: block;
  width: 100%;
  height: 100%;
}
</style>
