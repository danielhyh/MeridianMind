<template>
  <el-image src="https://shopro.sheepjs.com/admin/static/images/shop/decorate/orderCardStyle.png" />
</template>
<script lang="ts" setup>
import { UserOrderProperty } from './config'

/** 用户订单 */
defineOptions({ name: 'UserOrder' })
// 定义属性
defineProps<{ property: UserOrderProperty }>()
</script>

<style lang="scss" scoped></style>
