<template>
  <div
    :style="{
      color: property.textColor
    }"
    class="search-bar"
  >
    <!-- 搜索框 -->
    <div
      :style="{
        height: `${property.height}px`,
        background: property.backgroundColor,
        borderRadius: `${property.borderRadius}px`
      }"
      class="inner"
    >
      <div
        :style="{
          justifyContent: property.placeholderPosition
        }"
        class="placeholder"
      >
        <Icon icon="ep:search" />
        <span>{{ property.placeholder || '搜索商品' }}</span>
      </div>
      <div class="right">
        <!-- 搜索热词 -->
        <span v-for="(keyword, index) in property.hotKeywords" :key="index">{{ keyword }}</span>
        <!-- 扫一扫 -->
        <Icon v-show="property.showScan" icon="ant-design:scan-outlined" />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { SearchProperty } from './config'
/** 搜索框 */
defineOptions({ name: 'SearchBar' })
defineProps<{ property: SearchProperty }>()
</script>

<style lang="scss" scoped>
.search-bar {
  /* 搜索框 */
  .inner {
    position: relative;
    display: flex;
    min-height: 28px;
    font-size: 14px;
    align-items: center;

    .placeholder {
      display: flex;
      width: 100%;
      padding: 0 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      word-break: break-all;
      white-space: nowrap;
      align-items: center;
      gap: 2px;
    }

    .right {
      position: absolute;
      right: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    }
  }
}
</style>
