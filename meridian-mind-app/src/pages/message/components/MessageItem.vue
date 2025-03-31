<!-- 消息列表组件 -->
<template>
  <view class="w-full flex justify-between">
    <view class="w-100rpx h-100rpx flex items-center justify-center">
      <image
        :src="props.item.avatar"
        class="w-80rpx h-80rpx rounded-5px overflow-hidden"
        mode="scaleToFill"
      />
    </view>
    <view
      class="h-100rpx grow-1 ml-2 flex justify-between py-1 border-b-[0.5px] border-solid border-[#E3E2E2] border-x-0 border-t-0 box-border"
    >
      <view class="flex flex-col justify-between grow-1">
        <view>
          {{ props.item.title }}
        </view>
        <view class="overflow-hidden truncate text-20rpx w-500rpx">
          {{ props.item.content }}
        </view>
      </view>

      <view class="flex flex-col justify-between">
        <view class="text-20rpx text-[#999] truncate">
          {{ props.item.time }}
        </view>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
//
const props = defineProps({
  item: {
    type: Object,
    default: () => ({}),
  },
})
</script>

<style lang="scss" scoped>
//
</style>
