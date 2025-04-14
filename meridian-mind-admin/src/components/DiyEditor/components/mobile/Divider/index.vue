<template>
  <div
    :style="{
      height: property.height + 'px'
    }"
    class="flex items-center"
  >
    <div
      :style="{
        borderTopStyle: property.borderType,
        borderTopColor: property.lineColor,
        borderTopWidth: `${property.lineWidth}px`,
        margin: property.paddingType === 'none' ? '0' : '0px 16px'
      }"
      class="w-full"
    ></div>
  </div>
</template>

<script lang="ts" setup>
import { DividerProperty } from './config'

/** 页面顶部导航栏 */
defineOptions({ name: 'Divider' })

defineProps<{ property: DividerProperty }>()
</script>

<style lang="scss" scoped></style>
