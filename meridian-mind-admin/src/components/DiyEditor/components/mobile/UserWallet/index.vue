<template>
  <el-image
    src="https://shopro.sheepjs.com/admin/static/images/shop/decorate/walletCardStyle.png"
  />
</template>
<script lang="ts" setup>
import { UserWalletProperty } from './config'

/** 用户资产 */
defineOptions({ name: 'UserWallet' })
// 定义属性
defineProps<{ property: UserWalletProperty }>()
</script>

<style lang="scss" scoped></style>
