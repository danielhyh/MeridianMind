<!-- 工作台图标 -->
<template>
  <view class="flex flex-col items-center gap-1" @click="handleClick">
    <view class="bg-blue-500 rounded-5px w-80rpx h-80rpx flex flex-col items-center justify-center">
      <view :class="props.icon" class="color-white w-60rpx h-60rpx"></view>
    </view>
    <view class="text-22rpx">{{ props.title }}</view>
  </view>
</template>

<script lang="ts" setup>
//
const props = defineProps({
  title: {
    type: String,
    default: '',
  },
  icon: {
    type: String,
    default: '',
  },
  link: {
    type: String,
    default: '',
  },
})

const handleClick = () => {
  uni.navigateTo({
    url: props.link,
  })
}
</script>

<style lang="scss" scoped>
//
</style>
