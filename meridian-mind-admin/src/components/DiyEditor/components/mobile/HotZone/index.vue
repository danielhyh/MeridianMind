<template>
  <div class="relative h-full min-h-30px w-full">
    <el-image :src="property.imgUrl" class="pointer-events-none h-full w-full select-none" />
    <div
      v-for="(item, index) in property.list"
      :key="index"
      :style="{
        width: `${item.width}px`,
        height: `${item.height}px`,
        top: `${item.top}px`,
        left: `${item.left}px`
      }"
      class="hot-zone"
    >
      {{ item.name }}
    </div>
  </div>
</template>

<script lang="ts" setup>
import { HotZoneProperty } from './config'

/** 热区 */
defineOptions({ name: 'HotZone' })
const props = defineProps<{ property: HotZoneProperty }>()
</script>

<style lang="scss" scoped>
.hot-zone {
  position: absolute;
  background: var(--el-color-primary-light-7);
  opacity: 0.8;
  border: 1px solid var(--el-color-primary);
  color: var(--el-color-primary);
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: move;
  z-index: 10;
}
</style>
