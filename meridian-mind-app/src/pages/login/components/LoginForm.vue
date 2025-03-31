<!-- 账号密码登录 -->
<template>
  <view class="mt-4">
    <view>
      <wd-form ref="gennerateForm" :model="loginData.loginForm" class="">
        <view class="flex flex-col items-center gap-4">
          <view
            :class="{
              'border-4rpx border-blue-500 p-8rpx': focusInput === 'tenantName',
            }"
            class="rounded-xl p-10rpx border-2rpx border-blue-400 border-solid"
          >
            <wd-input
              v-if="loginData.tenantEnable"
              v-model="loginData.loginForm.tenantName"
              :no-border="true"
              custom-class="w-620rpx"
              placeholder="请输入租户名称"
              type="text"
              @focus="() => handleInputFocus('tenantName')"
            />
          </view>

          <view
            :class="{
              'border-4rpx border-blue-500 p-8rpx': focusInput === 'username',
            }"
            class="rounded-xl p-10rpx border-2rpx border-blue-400 border-solid"
          >
            <wd-input
              v-model="loginData.loginForm.username"
              :no-border="true"
              custom-class="w-620rpx"
              placeholder="请输入账号"
              type="text"
              @focus="() => handleInputFocus('username')"
            />
          </view>

          <view
            :class="{
              'border-4rpx border-blue-500 p-8rpx': focusInput === 'password',
            }"
            class="rounded-xl p-10rpx border-2rpx border-blue-400 border-solid"
          >
            <wd-input
              v-model="loginData.loginForm.password"
              :no-border="true"
              custom-class="w-620rpx"
              placeholder="请输入密码"
              type="password"
              @focus="() => handleInputFocus('password')"
            />
          </view>
        </view>
      </wd-form>
    </view>
    <view class="w-620rpx mx-auto">
      <wd-button block custom-class="mt-8" type="primary" @click="handleLogin">
        <text>登录</text>
      </wd-button>
    </view>

    <!-- 提示组件 -->
    <wd-toast />
  </view>
</template>

<script lang="ts" setup>
import {reactive, ref} from 'vue'
import {useToast} from 'wot-design-uni'
import {getTenantIdByName, login} from '@/service/login/LoginAPI'
import {useUserStore} from '@/store'
import * as authUtil from '@/utils/auth'

// 提示组件 TODO @ Qiksy 讨论：// 提示组件 这种，我们要不放在 useToast()，作为尾注释，简洁一点。
const toast = useToast()

const focusInput = ref('')
const handleInputFocus = (type: string) => {
  focusInput.value = type
}

const props = defineProps({
  agree: {
    type: Boolean,
    required: true,
    default: false,
  },
})

const loginData = reactive({
  isShowPassword: false,
  captchaEnable: import.meta.env.VITE_APP_CAPTCHA_ENABLE,
  tenantEnable: import.meta.env.VITE_APP_TENANT_ENABLE,
  loginForm: {
    tenantName: import.meta.env.VITE_APP_DEFAULT_LOGIN_TENANT || '',
    username: import.meta.env.VITE_APP_DEFAULT_LOGIN_USERNAME || '',
    password: import.meta.env.VITE_APP_DEFAULT_LOGIN_PASSWORD || '',
    captchaVerification: '',
    rememberMe: true, // 默认记录我。如果不需要，可手动修改
  },
})

// 获取租户 ID // TODO @ Qiksy 讨论：这个方法注释，要不用 /** 获取租户 ID */ 高亮的更明显哈。
const getTenantId = async () => {
  if (loginData.tenantEnable === 'true') {
    const res = (await getTenantIdByName(loginData.loginForm.tenantName)) as string
    authUtil.setTenantId(res)
  }
}

const handleLogin = async () => {
  console.log('账号密码登录')
  focusInput.value = ''
  if (!props.agree) {
    toast.warning('请阅读并同意《用户协议》和《隐私政策》')
    return
  }

  // 1. 先根据租户名，获取 tenantId
  await getTenantId()

  // todo 校验form数据
  const res = await login(loginData.loginForm)
  if (!res) {
    return
  }

  authUtil.setToken(res)

  // 获取用户信息，保存到 store
  const userStore = useUserStore()
  await userStore.setUserInfoAction()

  // 暂时先跳到首页
  uni.switchTab({
    url: '/pages/work/index',
  })
  console.log('登录成功')

  // todo 判断重定向

  // todo 处理页面loading展示
}
</script>
