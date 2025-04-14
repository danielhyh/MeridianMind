<template>
  <el-form :model="formData" label-width="80px">
    <el-card class="property-group" header="按钮配置" shadow="never">
      <el-form-item label="展开方向" prop="direction">
        <el-radio-group v-model="formData.direction">
          <el-radio value="vertical">垂直</el-radio>
          <el-radio value="horizontal">水平</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="显示文字" prop="showText">
        <el-switch v-model="formData.showText" />
      </el-form-item>
    </el-card>
    <el-card class="property-group" header="按钮列表" shadow="never">
      <Draggable v-model="formData.list" :empty-item="{ textColor: '#fff' }">
        <template #default="{ element, index }">
          <el-form-item :prop="`list[${index}].imgUrl`" label="图标">
            <UploadImg v-model="element.imgUrl" height="56px" width="56px" />
          </el-form-item>
          <el-form-item :prop="`list[${index}].text`" label="文字">
            <InputWithColor v-model="element.text" v-model:color="element.textColor" />
          </el-form-item>
          <el-form-item :prop="`list[${index}].url`" label="跳转链接">
            <AppLinkInput v-model="element.url" />
          </el-form-item>
        </template>
      </Draggable>
    </el-card>
  </el-form>
</template>

<script lang="ts" setup>
import { FloatingActionButtonProperty } from './config'
import { useVModel } from '@vueuse/core'

// 悬浮按钮属性面板
defineOptions({ name: 'FloatingActionButtonProperty' })

const props = defineProps<{ modelValue: FloatingActionButtonProperty }>()
const emit = defineEmits(['update:modelValue'])
const formData = useVModel(props, 'modelValue', emit)
</script>

<style lang="scss" scoped></style>
