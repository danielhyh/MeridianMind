<template>
  <ComponentContainerProperty v-model="formData.style" />
</template>

<script lang="ts" setup>
import { UserWalletProperty } from './config'
import { useVModel } from '@vueuse/core'

// 用户资产属性面板
defineOptions({ name: 'UserWalletProperty' })

const props = defineProps<{ modelValue: UserWalletProperty }>()
const emit = defineEmits(['update:modelValue'])
const formData = useVModel(props, 'modelValue', emit)
</script>

<style lang="scss" scoped></style>
