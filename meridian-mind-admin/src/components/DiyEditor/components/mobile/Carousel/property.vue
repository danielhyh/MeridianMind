<template>
  <ComponentContainerProperty v-model="formData.style">
    <el-form :model="formData" label-width="80px">
      <el-card class="property-group" header="样式设置" shadow="never">
        <el-form-item label="样式" prop="type">
          <el-radio-group v-model="formData.type">
            <el-tooltip class="item" content="默认" placement="bottom">
              <el-radio-button value="default">
                <Icon icon="system-uicons:carousel" />
              </el-radio-button>
            </el-tooltip>
            <el-tooltip class="item" content="卡片" placement="bottom">
              <el-radio-button value="card">
                <Icon icon="ic:round-view-carousel" />
              </el-radio-button>
            </el-tooltip>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="指示器" prop="indicator">
          <el-radio-group v-model="formData.indicator">
            <el-radio value="dot">小圆点</el-radio>
            <el-radio value="number">数字</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否轮播" prop="autoplay">
          <el-switch v-model="formData.autoplay" />
        </el-form-item>
        <el-form-item v-if="formData.autoplay" label="播放间隔" prop="interval">
          <el-slider
            v-model="formData.interval"
            :max="10"
            :min="0.5"
            :show-input-controls="false"
            :step="0.5"
            input-size="small"
            show-input
          />
          <el-text type="info">单位：秒</el-text>
        </el-form-item>
      </el-card>
      <el-card class="property-group" header="内容设置" shadow="never">
        <Draggable v-model="formData.items" :empty-item="{ type: 'img' }">
          <template #default="{ element }">
            <el-form-item class="m-b-8px!" label="类型" label-width="40px" prop="type">
              <el-radio-group v-model="element.type">
                <el-radio value="img">图片</el-radio>
                <el-radio value="video">视频</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item
              v-if="element.type === 'img'"
              class="m-b-8px!"
              label="图片"
              label-width="40px"
            >
              <UploadImg
                v-model="element.imgUrl"
                class="min-w-80px"
                draggable="false"
                height="80px"
                width="100%"
              />
            </el-form-item>
            <template v-else>
              <el-form-item class="m-b-8px!" label="封面" label-width="40px">
                <UploadImg
                  v-model="element.imgUrl"
                  class="min-w-80px"
                  draggable="false"
                  height="80px"
                  width="100%"
                />
              </el-form-item>
              <el-form-item class="m-b-8px!" label="视频" label-width="40px">
                <UploadFile
                  v-model="element.videoUrl"
                  :file-size="100"
                  :file-type="['mp4']"
                  :limit="1"
                  class="min-w-80px"
                />
              </el-form-item>
            </template>
            <el-form-item class="m-b-8px!" label="链接" label-width="40px">
              <AppLinkInput v-model="element.url" />
            </el-form-item>
          </template>
        </Draggable>
      </el-card>
    </el-form>
  </ComponentContainerProperty>
</template>

<script lang="ts" setup>
import { CarouselProperty } from './config'
import { useVModel } from '@vueuse/core'

// 轮播图属性面板
defineOptions({ name: 'CarouselProperty' })

const props = defineProps<{ modelValue: CarouselProperty }>()
const emit = defineEmits(['update:modelValue'])
const formData = useVModel(props, 'modelValue', emit)
</script>

<style lang="scss" scoped></style>
