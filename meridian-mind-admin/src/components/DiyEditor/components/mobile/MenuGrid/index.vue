<template>
  <div class="flex flex-row flex-wrap">
    <div
      v-for="(item, index) in property.list"
      :key="index"
      :style="{ width: `${100 * (1 / property.column)}%` }"
      class="relative flex flex-col items-center p-b-14px p-t-20px"
    >
      <!-- 右上角角标 -->
      <span
        v-if="item.badge?.show"
        :style="{ color: item.badge.textColor, backgroundColor: item.badge.bgColor }"
        class="absolute left-50% top-10px z-1 h-20px rounded-50% p-x-6px text-center text-12px leading-20px"
      >
        {{ item.badge.text }}
      </span>
      <el-image v-if="item.iconUrl" :src="item.iconUrl" class="h-28px w-28px" />
      <span :style="{ color: item.titleColor }" class="m-t-8px h-16px text-12px leading-16px">
        {{ item.title }}
      </span>
      <span :style="{ color: item.subtitleColor }" class="m-t-6px h-12px text-10px leading-12px">
        {{ item.subtitle }}
      </span>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { MenuGridProperty } from './config'
/** 宫格导航 */
defineOptions({ name: 'MenuGrid' })
defineProps<{ property: MenuGridProperty }>()
</script>

<style lang="scss" scoped></style>
