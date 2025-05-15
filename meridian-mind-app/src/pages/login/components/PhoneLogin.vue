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
        <text>登录</text>
      </wd-button>
    </view>

    <!-- 微信一键登录按钮 -->
    <view class="w-620rpx mx-auto mt-6">
      <wd-button
        block
        custom-class="wechat-btn"
        @click="handleWxLogin"
      >
        <view class="flex items-center justify-center">
          <text class="iconfont icon-chat mr-2"></text>
          <text>微信一键登录</text>
        </view>
      </wd-button>
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

// 提示组件
const toast = useToast()
const userStore = useUserStore()

// 输入框焦点状态
const focusInput = ref('')
const handleInputFocus = (type: string) => {
  focusInput.value = type
}

// 组件属性
const props = defineProps({
  agree: {
    type: Boolean,
    required: true,
    default: false,
  },
})

// 手机号登录相关数据
const phoneData = reactive({
  loginForm: {
    mobile: '',
    code: '',
    scene: 1, // 登录场景
    socialType: 0, // 社交类型
    socialCode: '', // 社交授权码
    socialState: '' // 社交状态码
  },
  codeSending: false,
  countdown: 60,
  codeText: '获取验证码',
  timer: null as any
})

// 验证手机号格式
const isValidPhone = computed(() => {
  const phoneReg = /^1[3-9]\d{9}$/
  return phoneReg.test(phoneData.loginForm.mobile)
})

// 判断是否可以登录
const canLogin = computed(() => {
  const validPhone = isValidPhone.value;
  const validCode = phoneData.loginForm.code.trim() !== '';

  return validPhone && validCode;
})

// 发送验证码
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

// 开始倒计时
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

// 手机号登录
const handlePhoneLogin = async () => {
  if (!isValidPhone.value) {
    toast.warning('请输入正确的手机号')
    return
  }

  if (phoneData.loginForm.code.trim() !== '') {
    toast.warning('请输入验证码');
    return;
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
      code: phoneData.loginForm.code,
      socialType: phoneData.loginForm.socialType,
      socialCode: phoneData.loginForm.socialCode,
      socialState: phoneData.loginForm.socialState,
      socialCodeValid: false
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

// 微信小程序登录
const handleWxLogin = async () => {
  if (!props.agree) {
    toast.warning('请阅读并同意《用户协议》和《隐私政策》')
    return
  }

  // #ifdef MP-WEIXIN
  try {
    // 显示加载中提示
    uni.showLoading({ title: '登录中...' });

    // 获取登录code
    const loginRes = await uni.login();
    if (!loginRes.code) {
      throw new Error('获取微信登录凭证失败');
    }

    const loginCode = loginRes.code;
    console.log('微信登录Code获取成功:', loginCode);

    // 获取用户信息前提示用户
    uni.showModal({
      title: '授权提示',
      content: '经络心智需要获取您的基本信息用于登录注册',
      success: (res) => {
        if (res.confirm) {
          // 用户同意授权，继续流程
          getUserProfile(loginCode);
        } else {
          // 用户拒绝授权，给出提示
          uni.hideLoading();
          toast.warning('您已取消授权，请选择其他方式登录');
        }
      }
    });
  } catch (error) {
    uni.hideLoading();
    console.error('微信登录失败', error);

    // 提供更友好的错误提示
    if (error.errMsg && error.errMsg.includes('access_token expired')) {
      toast.error('微信授权已过期，请重新授权或使用手机号登录');
    } else {
      toast.error('登录失败，请尝试使用手机号验证码登录');
    }
  }
  // #endif

  // #ifndef MP-WEIXIN
  toast.warning('请在微信小程序中使用此功能');
  // #endif
}

// 获取用户信息的函数，从主函数中分离出来
const getUserProfile = (loginCode) => {
  uni.getUserProfile({
    desc: '获取您的基本信息用于登录注册',
    success: (profileRes) => {
      console.log('获取用户信息成功');
      // 继续获取手机号流程
      getPhoneNumber(loginCode);
    },
    fail: (err) => {
      uni.hideLoading();
      console.error('获取用户信息失败', err);
      toast.error('获取用户信息失败，请使用手机号登录');
    }
  });
}

// 获取手机号的函数
const getPhoneNumber = (loginCode) => {
  uni.getPhoneNumber({
    success: async (phoneRes) => {
      if (!phoneRes.code) {
        throw new Error('获取手机号凭证失败');
      }

      const phoneCode = phoneRes.code;
      console.log('获取手机号Code成功');

      // 生成随机state
      const state = Math.random().toString(36).substring(2);

      try {
        // 调用微信小程序登录接口
        const res = await weixinMiniAppLogin({
          loginCode,
          phoneCode,
          state
        });

        if (!res) {
          throw new Error('登录接口返回数据异常');
        }

        // 保存登录凭证
        authUtil.setToken(res);

        // 获取用户信息
        await userStore.setUserInfoAction();

        uni.hideLoading();
        toast.success('登录成功');

        // 跳转到首页
        uni.switchTab({
          url: '/pages/work/index'
        });
      } catch (error) {
        uni.hideLoading();
        console.error('服务器登录失败', error);
        toast.error('登录失败，请稍后再试或使用手机号登录');
      }
    },
    fail: (err) => {
      uni.hideLoading();
      console.error('获取手机号失败', err);
      toast.error('获取手机号失败，请使用手机号验证码登录');
    }
  });
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
  background-color: #07c160;
  color: #ffffff;
}

.debug-panel {
  background-color: #f5f5f5;
  border: 1px dashed #ccc;
  border-radius: 8px;
  font-size: 12px;
  color: #333;
  width: 620rpx;
  margin: 0 auto;
}
</style>
