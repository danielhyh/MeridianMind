<template>
  <ComponentContainerProperty v-model="formData.style">
    <!-- 表单 -->
    <el-form :model="formData" class="m-t-8px" label-width="80px">
      <el-text tag="p"> 魔方设置 </el-text>
      <el-text size="small" type="info"> 每格尺寸187 * 187 </el-text>
      <MagicCubeEditor
        v-model="formData.list"
        :cols="4"
        :rows="4"
        class="m-y-16px"
        @hot-area-selected="handleHotAreaSelected"
      />
      <template v-for="(hotArea, index) in formData.list" :key="index">
        <template v-if="selectedHotAreaIndex === index">
          <el-form-item :prop="`list[${index}].imgUrl`" label="上传图片">
            <UploadImg v-model="hotArea.imgUrl" height="80px" width="80px" />
          </el-form-item>
          <el-form-item :prop="`list[${index}].url`" label="链接">
            <AppLinkInput v-model="hotArea.url" />
          </el-form-item>
        </template>
      </template>
      <el-form-item label="上圆角" prop="borderRadiusTop">
        <el-slider
          v-model="formData.borderRadiusTop"
          :max="100"
          :min="0"
          :show-input-controls="false"
          input-size="small"
          show-input
        />
      </el-form-item>
      <el-form-item label="下圆角" prop="borderRadiusBottom">
        <el-slider
          v-model="formData.borderRadiusBottom"
          :max="100"
          :min="0"
          :show-input-controls="false"
          input-size="small"
          show-input
        />
      </el-form-item>
      <el-form-item label="间隔" prop="space">
        <el-slider
          v-model="formData.space"
          :max="100"
          :min="0"
          :show-input-controls="false"
          input-size="small"
          show-input
        />
      </el-form-item>
    </el-form>
  </ComponentContainerProperty>
</template>

<script lang="ts" setup>
import { useVModel } from '@vueuse/core'
import { MagicCubeProperty } from '@/components/DiyEditor/components/mobile/MagicCube/config'

/** 广告魔方属性面板 */
defineOptions({ name: 'MagicCubeProperty' })

const props = defineProps<{ modelValue: MagicCubeProperty }>()
const emit = defineEmits(['update:modelValue'])
const formData = useVModel(props, 'modelValue', emit)

// 选中的热区
const selectedHotAreaIndex = ref(-1)
const handleHotAreaSelected = (_: any, index: number) => {
  selectedHotAreaIndex.value = index
}
</script>

<style lang="scss" scoped></style>
