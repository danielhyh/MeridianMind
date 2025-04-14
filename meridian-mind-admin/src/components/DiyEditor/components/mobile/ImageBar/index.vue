<template>
  <!-- 无图片 -->
  <div v-if="!property.imgUrl" class="h-50px flex items-center justify-center bg-gray-3">
    <Icon class="text-gray-8 text-30px!" icon="ep:picture" />
  </div>
  <el-image v-else :src="property.imgUrl" class="min-h-30px" />
</template>
<script lang="ts" setup>
import { ImageBarProperty } from './config'

/** 图片展示 */
defineOptions({ name: 'ImageBar' })

defineProps<{ property: ImageBarProperty }>()
</script>

<style lang="scss" scoped>
/* 图片 */
img {
  display: block;
  width: 100%;
  height: 100%;
}
</style>
