<!-- 消息界面：用于 im 聊天 -->
<route lang="json5" type="page">
{
  layout: 'tabbar',
  style: {
    navigationBarTitleText: '消息',
  },
}
</route>

<template>
  <!-- 搜索框 -->
  <wd-search
    v-model="searchValue"
    hide-cancel
    light
    maxlength="10"
    @blur="searchBlur"
    @focus="searchFocus"
    @search="search"
  />
  <view class="px-4 pb-4 pt-0 flex flex-col gap-2">
    <MessageItem v-for="(item, index) in messageList" :key="index" :item="item" />
  </view>
</template>

<script lang="ts" setup>
//
import {ref} from 'vue'
import MessageItem from './components/MessageItem.vue'
import messageList from './mock'

const searchValue = ref('')
/// ///////  搜索栏 事件////////////
const searchFocus = () => {}
const searchBlur = () => {}
const search = () => {}
</script>

<style lang="scss" scoped>
//
</style>
