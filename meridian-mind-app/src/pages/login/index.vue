<route lang="json5" type="page">
{
  style: {
    navigationBarTitleText: '登录页面',
  },
}
</route>

<template>
  <view class="relative h-full">
    <!-- banner区域 -->
    <view class="absolute w-full h-480rpx">
      <view class="color-white pt-[116rpx] pl-[68rpx] z-10">
        <view><text class="text-[64rpx] font-700">你好</text></view>
        <view class="mt-[36rpx]"><text class="text-[36rpx]">欢迎登录芋道快速开发平台</text></view>
      </view>
      <!-- TODO @芋艿：后续把静态资源，放到 CDN -->
      <image
        class="absolute w-full top-0 -z-1"
        mode="aspectFit"
        src="/static/images/login-bg.png"
      />
    </view>
    <!-- 主要容器区域 -->
    <!-- 430rpx 是上面banner的高度，50rpx是留给底部的空间 -->
    <view
      class="top-[430rpx] h-[calc(100vh-430rpx-50rpx)] w-full rounded-t-2xl bg-white flex flex-col items-center absolute px-4 box-border"
    >
      <!-- tabs 切换 -->
      <wd-tabs v-model="tab" custom-class="h-full">
        <block :key="0">
          <wd-tab title="手机号登录">手机号登录</wd-tab>
        </block>
        <block :key="1">
          <wd-tab title="密码登录">
            <LoginForm :agree="agree" />
          </wd-tab>
        </block>
      </wd-tabs>

      <!-- 隐私与用户条款 -->
      <view class="text-[14px] text-[#999] mt-4 h-50rpx flex justify-center">
        <wd-checkbox v-model="agree" class="inline-block"></wd-checkbox>
        <text>已阅读并同意</text>
        <text class="text-[#0B5EFF]">《用户协议》</text>
        <text>和</text>
        <text class="text-[#0B5EFF]">《隐私政策》</text>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import LoginForm from '@/pages/login/components/LoginForm.vue'
import {ref} from 'vue'

//

const tab = ref(1) // tabs的索引
const agree = ref(false) // 是否同意协议

onLoad(() => {
  //
})

onMounted(() => {})
</script>

<style lang="scss" scoped>
//
.login-bg {
  background-image: url('/static/images/login-bg.png');
}
</style>
