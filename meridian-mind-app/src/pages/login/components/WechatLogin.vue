<!-- WechatLogin.vue - 微信一键登录组件 -->
<template>
  <view class="wechat-login-container mt-4 flex justify-center">
    <wd-button
      custom-class="wechat-login-btn"
      block
      type="success"
      @click="handleWechatLogin"
    >
      <view class="flex items-center justify-center">
        <image src="/static/images/wechat-icon.png" class="wechat-icon mr-2" />
        <text>微信一键登录</text>
      </view>
    </wd-button>

    <!-- 提示组件 -->
    <wd-toast />
  </view>
</template>

<script lang="ts" setup>
import { useToast } from 'wot-design-uni'
import { weixinMiniAppLogin } from '@/service/login/LoginAPI'
import { useUserStore } from '@/store'
import * as authUtil from '@/utils/auth'

/** 提示组件 */
const toast = useToast()

/** 属性定义 */
const props = defineProps({
  agree: {
    type: Boolean,
    required: true,
    default: false,
  },
})

/** 微信一键登录 */
const handleWechatLogin = () => {
  // 协议验证
  if (!props.agree) {
    toast.warning('请阅读并同意《用户协议》和《隐私政策》')
    return
  }

  // 显示加载提示
  uni.showLoading({
    title: '登录中...',
  })

  // 调用微信登录
  wx.login({
    success: (loginRes) => {
      if (loginRes.code) {
        // 获取手机号码
        wx.showModal({
          title: '提示',
          content: '微信登录需要获取您的手机号',
          success: (res) => {
            if (res.confirm) {
              // 调用获取手机号API
              wx.getPhoneNumber({
                success: async (phoneRes) => {
                  try {
                    // 调用后端微信小程序登录接口
                    const res = await weixinMiniAppLogin({
                      phoneCode: phoneRes.code,
                      loginCode: loginRes.code,
                      state: 'default'
                    })

                    if (!res) {
                      uni.hideLoading()
                      return
                    }

                    // 保存token
                    authUtil.setToken(res)

                    // 获取用户信息，保存到 store
                    const userStore = useUserStore()
                    await userStore.setUserInfoAction()

                    // 跳转到首页
                    uni.switchTab({
                      url: '/pages/work/index',
                    })
                    console.log('微信登录成功')
                  } catch (error) {
                    toast.error('微信登录失败')
                  } finally {
                    uni.hideLoading()
                  }
                },
                fail: () => {
                  toast.warning('获取手机号失败')
                  uni.hideLoading()
                }
              })
            } else {
              uni.hideLoading()
            }
          }
        })
      } else {
        toast.error('微信登录失败')
        uni.hideLoading()
      }
    },
    fail: () => {
      toast.error('微信登录失败')
      uni.hideLoading()
    }
  })
}
</script>

<style lang="scss" scoped>
.wechat-login-container {
  margin-top: 40rpx;
}

.wechat-login-btn {
  background-color: #07C160 !important;
  border-color: #07C160 !important;
}

.wechat-icon {
  width: 40rpx;
  height: 40rpx;
}
</style>
