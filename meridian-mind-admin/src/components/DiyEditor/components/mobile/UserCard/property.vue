<template>
  <ComponentContainerProperty v-model="formData.style" />
</template>

<script lang="ts" setup>
import { UserCardProperty } from './config'
import { useVModel } from '@vueuse/core'

// 用户卡片属性面板
defineOptions({ name: 'UserCardProperty' })

const props = defineProps<{ modelValue: UserCardProperty }>()
const emit = defineEmits(['update:modelValue'])
const formData = useVModel(props, 'modelValue', emit)
</script>

<style lang="scss" scoped></style>
