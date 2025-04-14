<template>
  <ComponentContainerProperty v-model="formData.style">
    <template #style>
      <el-form-item label="高度" prop="height">
        <el-slider
          v-model="formData.style.height"
          :max="500"
          :min="100"
          :show-input-controls="false"
          input-size="small"
          show-input
        />
      </el-form-item>
    </template>
    <el-form :model="formData" label-width="80px">
      <el-form-item label="上传视频" prop="videoUrl">
        <UploadFile
          v-model="formData.videoUrl"
          :file-size="100"
          :file-type="['mp4']"
          :limit="1"
          class="min-w-80px"
        />
      </el-form-item>
      <el-form-item label="上传封面" prop="posterUrl">
        <UploadImg
          v-model="formData.posterUrl"
          class="min-w-80px"
          draggable="false"
          height="80px"
          width="100%"
        >
          <template #tip> 建议宽度750 </template>
        </UploadImg>
      </el-form-item>
      <el-form-item label="自动播放" prop="autoplay">
        <el-switch v-model="formData.autoplay" />
      </el-form-item>
    </el-form>
  </ComponentContainerProperty>
</template>

<script lang="ts" setup>
import { VideoPlayerProperty } from './config'
import { useVModel } from '@vueuse/core'

// 视频播放属性面板
defineOptions({ name: 'VideoPlayerProperty' })

const props = defineProps<{ modelValue: VideoPlayerProperty }>()
const emit = defineEmits(['update:modelValue'])
const formData = useVModel(props, 'modelValue', emit)
</script>

<style lang="scss" scoped></style>
