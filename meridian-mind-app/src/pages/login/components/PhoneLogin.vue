<template>
  <view class="mt-4">
    <wd-form ref="phoneForm" :model="phoneData.loginForm">
      <view class="flex flex-col items-center gap-4">
        <!-- 手机号输入框 -->
        <view
          :class="{
            'border-4rpx border-primary p-8rpx': focusInput === 'mobile',
          }"
          class="rounded-xl p-10rpx border-2rpx border-primary border-solid"
        >
          <wd-input
            v-model="phoneData.loginForm.mobile"
            :no-border="true"
            custom-class="w-620rpx"
            placeholder="请输入手机号"
            type="number"
            :maxlength="11"
            @focus="() => handleInputFocus('mobile')"
          />
        </view>

        <!-- 验证码输入框 -->
        <view
          :class="{
            'border-4rpx border-primary p-8rpx': focusInput === 'code',
          }"
          class="rounded-xl p-10rpx border-2rpx border-primary border-solid flex justify-between"
        >
          <wd-input
            v-model="phoneData.loginForm.code"
            :no-border="true"
            custom-class="w-380rpx"
            placeholder="请输入验证码"
            type="number"
            :maxlength="6"
            @focus="() => handleInputFocus('code')"
          />
          <wd-button
            size="small"
            :loading="phoneData.codeSending"
            :disabled="phoneData.codeSending || !isValidPhone"
            @click="handleSendCode"
          >
            {{ phoneData.codeText }}
          </wd-button>
        </view>
      </view>
    </wd-form>

    <!-- 登录按钮 -->
    <view class="w-620rpx mx-auto">
      <wd-button
        block
        custom-class="mt-8"
        type="primary"
        @click="handlePhoneLogin"
        :disabled="!canLogin"
      >
        <text>登录/注册</text>
      </wd-button>
    </view>

    <!-- 分隔线 -->
    <view class="divider-container w-620rpx mx-auto flex items-center justify-center mt-8">
      <view class="divider"></view>
      <text class="mx-4 text-gray-400">或</text>
      <view class="divider"></view>
    </view>

    <!-- 微信一键登录按钮 -->
    <view class="w-620rpx mx-auto mt-6">
      <!-- 普通按钮 -->
      <button
        v-if="!isAuthBtnVisible"
        class="wechat-btn"
        @tap="checkAgreementBeforeAuth"
      >
        <view class="flex items-center justify-center">
          <text class="iconfont icon-chat mr-2"></text>
          <text>微信一键登录</text>
        </view>
      </button>

      <!-- 微信授权按钮（条件显示） -->
      <button
        v-if="isAuthBtnVisible"
        class="wechat-btn"
        open-type="getPhoneNumber"
        @getphonenumber="onGetPhoneNumber"
      >
        <view class="flex items-center justify-center">
          <text class="iconfont icon-chat mr-2"></text>
          <text>微信一键登录</text>
        </view>
      </button>
    </view>

    <!-- 提示组件 -->
    <wd-toast />
  </view>
</template>

<script lang="ts" setup>
import { reactive, ref, computed } from 'vue'
import { useToast } from 'wot-design-uni'
import { sendSmsCode, smsLogin, weixinMiniAppLogin } from '@/service/login/LoginAPI'
import { useUserStore } from '@/store'
import * as authUtil from '@/utils/auth'

/** 提示组件 */
const toast = useToast()
const userStore = useUserStore()
// 控制授权按钮的显示
const isAuthBtnVisible = ref(false)
/** 输入框焦点状态 */
const focusInput = ref('')
const handleInputFocus = (type: string) => {
  focusInput.value = type
}

/** 组件属性 */
const props = defineProps({
  agree: {
    type: Boolean,
    required: true,
    default: false,
  },
})

/** 手机号登录相关数据 */
const phoneData = reactive({
  loginForm: {
    mobile: '',
    code: '',
    scene: 1, // 登录场景
  },
  codeSending: false,
  countdown: 60,
  codeText: '获取验证码',
  timer: null as any
})
// 检查协议同意状态
const checkAgreementBeforeAuth = () => {
  // 检查是否同意协议
  if (!props.agree) {
    toast.warning('请阅读并同意《用户协议》和《隐私政策》')
    return
  }

  // 协议已同意，显示微信授权按钮
  isAuthBtnVisible.value = true
}
/** 验证手机号格式 */
const isValidPhone = computed(() => {
  const phoneReg = /^1[3-9]\d{9}$/
  return phoneReg.test(phoneData.loginForm.mobile)
})

/** 判断是否可以登录 */
const canLogin = computed(() => {
  return isValidPhone.value && phoneData.loginForm.code.trim() !== '';
})

/** 发送验证码 */
const handleSendCode = async () => {
  if (!isValidPhone.value) {
    toast.warning('请输入正确的手机号')
    return
  }

  try {
    phoneData.codeSending = true
    // 调用发送验证码接口
    const res = await sendSmsCode({
      mobile: phoneData.loginForm.mobile,
      scene: phoneData.loginForm.scene
    })

    if (res) {
      toast.success('验证码发送成功')
      startCountdown()
    }
  } catch (error) {
    console.error('发送验证码失败', error)
    toast.error('验证码发送失败，请稍后再试')
  } finally {
    phoneData.codeSending = false
  }
}

/** 开始倒计时 */
const startCountdown = () => {
  phoneData.countdown = 60
  phoneData.codeText = `${phoneData.countdown}秒后重新获取`

  if (phoneData.timer) {
    clearInterval(phoneData.timer)
  }

  phoneData.timer = setInterval(() => {
    phoneData.countdown--
    phoneData.codeText = `${phoneData.countdown}秒后重新获取`

    if (phoneData.countdown <= 0) {
      clearInterval(phoneData.timer)
      phoneData.codeText = '获取验证码'
    }
  }, 1000)
}

/** 手机号登录 */
const handlePhoneLogin = async () => {
  if (!isValidPhone.value) {
    toast.warning('请输入正确的手机号')
    return
  }

  if (phoneData.loginForm.code.trim() === '') {
    toast.warning('请输入验证码')
    return
  }

  if (!props.agree) {
    toast.warning('请阅读并同意《用户协议》和《隐私政策》')
    return
  }

  try {
    uni.showLoading({ title: '登录中...' })

    // 调用手机登录接口
    const res = await smsLogin({
      mobile: phoneData.loginForm.mobile,
      code: phoneData.loginForm.code
    })

    if (!res) {
      throw new Error('登录失败')
    }

    // 保存登录凭证
    authUtil.setToken(res)

    // 获取用户信息
    await userStore.setUserInfoAction()

    uni.hideLoading()
    toast.success('登录成功')

    // 跳转到首页
    uni.switchTab({
      url: '/pages/work/index'
    })
  } catch (error) {
    uni.hideLoading()
    console.error('登录失败', error)
    toast.error('登录失败，请检查手机号和验证码是否正确')
  }
}

/** 微信手机号授权回调 */
const onGetPhoneNumber = async (e) => {
  // 重置授权按钮状态，不论成功失败
  isAuthBtnVisible.value = false

  console.log('获取手机号回调', e)

  // 用户拒绝授权
  if (e.detail.errMsg && e.detail.errMsg.includes('deny')) {
    toast.warning('您已拒绝授权手机号，请使用验证码登录')
    return
  }

  // 授权失败
  if (!e.detail.code) {
    toast.error('获取手机号失败，请使用验证码登录')
    return
  }

  try {
    uni.showLoading({ title: '登录中...' })

    // 获取微信登录凭证
    const loginRes = await new Promise((resolve, reject) => {
      uni.login({
        success: (res) => resolve(res),
        fail: (err) => reject(err)
      })
    })

    if (!loginRes.code) {
      throw new Error('获取微信登录凭证失败')
    }

    // 生成随机state
    const state = Math.random().toString(36).substring(2)

    // 调用微信小程序登录接口
    const res = await weixinMiniAppLogin({
      phoneCode: e.detail.code,
      loginCode: loginRes.code,
      state: state
    })

    if (!res) {
      throw new Error('登录接口返回数据异常')
    }

    // 保存登录凭证
    authUtil.setToken(res)

    // 获取用户信息
    await userStore.setUserInfoAction()

    toast.success('登录成功')

    // 跳转到首页
    uni.switchTab({
      url: '/pages/work/index'
    })
  } catch (error) {
    console.error('微信登录失败', error)
    toast.error('登录失败，请尝试使用手机号登录')
  } finally {
    uni.hideLoading()
  }
}

// 组件销毁时清除定时器
onUnmounted(() => {
  if (phoneData.timer) {
    clearInterval(phoneData.timer)
  }
})
</script>

<style lang="scss" scoped>
.wechat-btn {
  width: 100%;
  height: 88rpx;
  background-color: #07c160;
  color: #ffffff;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  border: none;
}

.divider {
  flex: 1;
  height: 1px;
  background-color: #ddd;
}
</style>
