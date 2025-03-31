<!-- 通讯录 item -->
<template>
  <view class="flex justify-between w-full">
    <!-- 头像 -->
    <view class="flex justify-center items-center w-100rpx">
      <image
        :src="props.item.avatar ? props.item.avatar : '/static/images/contacts.png'"
        class="w-80rpx h-80rpx rounded-5px overflow-hidden"
        mode="scaleToFill"
      />
    </view>
    <!-- 右侧信息 -->
    <view
      class="h-100rpx grow-1 flex flex-col justify-center border-solid border-b-[0.5px] border-x-0 border-t-0 border-[#E3E2E2]"
    >
      <!-- 如果是个人 -->
      <view v-if="props.item.isLeaf">
        <view class="text-24rpx">{{ props.item.name }}</view>
        <view class="text-22rpx">
          {{ props.item.post }}
        </view>
      </view>
      <view v-else>
        <view class="text-24rpx">
          {{ props.item.name }}
        </view>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
//
import {defineProps} from 'vue'

const props = defineProps(['item'])
</script>

<style lang="scss" scoped>
//
</style>
