<route lang="json5" type="page">
{
style: {
navigationBarTitleText: '经络心智',
},
}
</route>

<template>
  <view class="flex flex-col justify-between items-center min-h-screen bg-background relative px-6">
    <!-- 中医图案背景 -->
    <view class="absolute inset-0 z-0 opacity-5 bg-cover bg-center" style="background-image: url('https://img.freepik.com/free-vector/seamless-chinese-pattern_1159-6423.jpg');"></view>

    <!-- Logo和标题 -->
    <view class="w-full flex flex-col items-center mt-8 mb-4">
      <view class="relative mb-2">
        <view class="w-24 h-24 bg-white rounded-full flex justify-center items-center shadow">
          <image src="/static/images/logo.png" class="w-16 h-16" />
        </view>
      </view>
      <view class="text-xl font-bold text-gray-800">经络心智</view>
      <view class="text-gray-500 text-xs mt-1">中医智能问诊系统</view>
    </view>

    <!-- 登录表单 -->
    <view class="w-85% max-w-140 bg-white rounded-xl shadow p-6 mb-6">
      <MobileLoginForm :agree="agree" />
    </view>

    <!-- 用户协议 -->
    <view class="flex items-center justify-center mb-4">
      <wd-checkbox v-model="agree" custom-class="mr-1"></wd-checkbox>
      <text class="text-xs text-gray-500">登录即表示您同意</text>
      <text class="text-xs text-primary mx-1" @click="openUserAgreement">《用户协议》</text>
      <text class="text-xs text-gray-500">和</text>
      <text class="text-xs text-primary mx-1" @click="openPrivacyPolicy">《隐私政策》</text>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import MobileLoginForm from './components/MobileLoginForm.vue'

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

// onLoad(() => {
//   // 检查是否有缓存的登录态
//   const token = uni.getStorageSync('token')
//   if (token) {
//     uni.switchTab({
//       url: '/pages/work/index'
//     })
//   }
// })
</script>

<style>
/* 由于使用UnoCss，这里只需要定义极少量的自定义样式 */
.text-primary {
  color: var(--primary-color);
}

.bg-background {
  background-color: var(--background-color);
}
</style>
