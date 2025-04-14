<template>
  <ComponentContainerProperty v-model="formData.style">
    <el-form :model="formData" label-width="80px">
      <el-form-item label="上传图片" prop="imgUrl">
        <UploadImg
          v-model="formData.imgUrl"
          class="min-w-80px"
          draggable="false"
          height="80px"
          width="100%"
        >
          <template #tip> 建议宽度750 </template>
        </UploadImg>
      </el-form-item>
      <el-form-item label="链接" prop="url">
        <AppLinkInput v-model="formData.url" />
      </el-form-item>
    </el-form>
  </ComponentContainerProperty>
</template>

<script lang="ts" setup>
import { ImageBarProperty } from './config'
import { useVModel } from '@vueuse/core'

// 图片展示属性面板
defineOptions({ name: 'ImageBarProperty' })

const props = defineProps<{ modelValue: ImageBarProperty }>()
const emit = defineEmits(['update:modelValue'])
const formData = useVModel(props, 'modelValue', emit)
</script>

<style lang="scss" scoped></style>
