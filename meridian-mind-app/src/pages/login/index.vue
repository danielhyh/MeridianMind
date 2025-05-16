<route lang="json5" type="page">
{
style: {
navigationBarTitleText: '经络心智',
},
}
</route>

<template>
  <view class="relative h-full">
    <!-- banner区域 -->
    <view class="absolute w-full h-480rpx">
      <view class="color-white text-center pt-[80rpx] z-10">
        <image src="/static/images/logo.png" class="logo-image mx-auto" />
        <view><text class="text-[64rpx] font-700">经络心智</text></view>
        <view class="mt-[16rpx]"><text class="text-[36rpx]">中医智能问诊系统</text></view>
      </view>
      <image
        class="absolute w-full top-0 -z-1"
        mode="aspectFit"
        src="/static/images/login-bg.png"
      />
    </view>

    <!-- 主要容器区域 -->
    <view
      class="top-[430rpx] h-[calc(100vh-430rpx-50rpx)] w-full rounded-t-2xl bg-white flex flex-col items-center absolute px-4 box-border"
    >
      <!-- tabs 切换 -->
      <wd-tabs v-model="tab" custom-class="h-full">
        <block :key="0">
          <wd-tab title="手机号登录">
            <PhoneLogin :agree="agree" />
          </wd-tab>
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
        <text class="text-primary" @click="openUserAgreement">《用户协议》</text>
        <text>和</text>
        <text class="text-primary" @click="openPrivacyPolicy">《隐私政策》</text>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import LoginForm from '@/pages/login/components/LoginForm.vue'
import PhoneLogin from '@/pages/login/components/PhoneLogin.vue'

const tab = ref(0) // 默认选择手机号登录
const agree = ref(false) // 是否同意协议

// 打开用户协议
const openUserAgreement = () => {
  uni.navigateTo({
    url: '/pages/agreement/user'
  })
}

// 打开隐私政策
const openPrivacyPolicy = () => {
  uni.navigateTo({
    url: '/pages/agreement/privacy'
  })
}

onLoad(() => {
  // 检查是否有缓存的登录态
  const token = uni.getStorageSync('token')
  if (token) {
    uni.switchTab({
      url: '/pages/work/index'
    })
  }
})
</script>

<style lang="scss" scoped>
:deep(.wd-tab) {
  color: var(--primary-color);
}

:deep(.wd-tabs__line) {
  background-color: var(--primary-color);
}

.text-primary {
  color: var(--primary-color);
}

.logo-image {
  width: 120rpx;
  height: 120rpx;
  margin-bottom: 20rpx;
}
</style>
