<route lang="json5" type="page">
{
  layout: 'tabbar',
  style: {
    navigationBarTitleText: '我的',
  },
}
</route>

<template>
  <view class="p-4 flex flex-col gap-4">
    <!-- 个人信息-登陆后 -->
    <view
      v-if="getAccessToken()"
      class="flex justify-between w-full items-center gap-4 h-150rpx bg-white rounded-xl py-15rpx px-10rpx box-border"
    >
      <view class="rounded-full w-120rpx h-120rpx">
        <image
          :src="user ? user.avatar : '/static/images/user.png'"
          class="h-120rpx w-120rpx"
          mode="scaleToFill"
        />
      </view>
      <view class="grow-1 flex flex-col justify-between h-120rpx py-2 box-border">
        <view>
          <text class="text-2xl font-600">{{ user?.nickname }}</text>
        </view>
        <view class="text-xs flex items-center">
          <text>芋道集团</text>
          <view class="i-ic-twotone-shield text-emerald"></view>
        </view>
      </view>
      <view class="h-full">
        <!-- todo 设置按钮 -->
        <view class="i-ic-outline-settings mr-15rpx"></view>
      </view>
    </view>
    <!-- 个人信息-登录前 -->
    <view v-else class="flex justify-between w-full items-start gap-4">
      <view class="text-2xl font-600" @click="handleLogin">点击登录</view>
    </view>

    <!-- // todo 日程安排 -->
    <!-- 应用设置 -->
    <view class="flex flex-col gap-2">
      <view>
        <text class="text-lg font-600">应用设置</text>
      </view>
      <view class="rounded-xl bg-white overflow-hidden">
        <wd-cell v-if="getAccessToken()" is-link title="修改密码">
          <template #icon>
            <view class="self-center flex item-center justify-center mr-2">
              <view class="i-ic-outline-lock"></view>
            </view>
          </template>
        </wd-cell>
        <wd-cell is-link title="隐私协议">
          <template #icon>
            <view class="self-center flex item-center justify-center mr-2">
              <view class="i-ic-outline-policy"></view>
            </view>
          </template>
        </wd-cell>
        <wd-cell is-link title="关于我们">
          <template #icon>
            <view class="self-center flex item-center justify-center mr-2">
              <view class="i-ic-outline-info"></view>
            </view>
          </template>
        </wd-cell>
        <wd-cell is-link title="投诉与建议">
          <template #icon>
            <view class="self-center flex item-center justify-center mr-2">
              <view class="i-ic-outline-feedback"></view>
            </view>
          </template>
        </wd-cell>
        <!-- 退出登录，红色的警告字体 -->
        <wd-cell v-if="getAccessToken()" clickable @click="handleLogout">
          <template #icon>
            <view class="self-center flex item-center justify-center mr-2">
              <view class="i-ic-outline-logout text-red-500"></view>
            </view>
          </template>
          <template #title>
            <view>
              <text class="text-red-500">退出登录</text>
            </view>
          </template>
        </wd-cell>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import {useUserStore} from '@/store'
import {useMessage} from 'wot-design-uni'
import {getAccessToken} from '@/utils/auth'

const message = useMessage()
//

const userStore = useUserStore()

const handleLogin = () => {
  uni.redirectTo({ url: '/pages/login/index' })
}
const handleLogout = async () => {
  message
    .confirm({
      msg: '确定退出登录？',
      title: '退出登录',
    })
    .then(async () => {
      await userStore.LogOut()
      uni.redirectTo({ url: '/pages/login/index' })
    })
}

const user = ref<any>({})

onLoad((_option) => {
  //
  user.value = userStore.userInfo.user
})
</script>

<style lang="scss" scoped>
//
</style>
