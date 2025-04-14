<template>
  <div class="min-h-42px flex flex-col">
    <div
      v-for="(item, index) in property.list"
      :key="index"
      class="item h-42px flex flex-row items-center justify-between gap-4px p-x-12px"
    >
      <div class="flex flex-1 flex-row items-center gap-8px">
        <el-image v-if="item.iconUrl" :src="item.iconUrl" class="h-16px w-16px" />
        <span :style="{ color: item.titleColor }" class="text-16px">{{ item.title }}</span>
      </div>
      <div class="item-center flex flex-row justify-center gap-4px">
        <span :style="{ color: item.subtitleColor }" class="text-12px">{{ item.subtitle }}</span>
        <Icon :size="16" color="#000" icon="ep-arrow-right" />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { MenuListProperty } from './config'
/** 列表导航 */
defineOptions({ name: 'MenuList' })
defineProps<{ property: MenuListProperty }>()
</script>

<style lang="scss" scoped>
.item + .item {
  border-top: 1px solid #eee;
}
</style>
