<template>
  <ComponentContainerProperty v-model="formData.style">
    <el-form :model="formData" :rules="rules" label-width="80px">
      <el-form-item label="公告图标" prop="iconUrl">
        <UploadImg v-model="formData.iconUrl" height="48px">
          <template #tip>建议尺寸：24 * 24</template>
        </UploadImg>
      </el-form-item>
      <el-form-item label="背景颜色" prop="backgroundColor">
        <ColorInput v-model="formData.backgroundColor" />
      </el-form-item>
      <el-form-item label="文字颜色" prop="文字颜色">
        <ColorInput v-model="formData.textColor" />
      </el-form-item>
      <el-card class="property-group" header="公告内容" shadow="never">
        <Draggable v-model="formData.contents">
          <template #default="{ element }">
            <el-form-item label="公告" label-width="40px" prop="text">
              <el-input v-model="element.text" placeholder="请输入公告" />
            </el-form-item>
            <el-form-item label="链接" label-width="40px" prop="url">
              <AppLinkInput v-model="element.url" />
            </el-form-item>
          </template>
        </Draggable>
      </el-card>
    </el-form>
  </ComponentContainerProperty>
</template>

<script lang="ts" setup>
import { NoticeBarProperty } from './config'
import { useVModel } from '@vueuse/core'
// 通知栏属性面板
defineOptions({ name: 'NoticeBarProperty' })
// 表单校验
const rules = {
  content: [{ required: true, message: '请输入公告', trigger: 'blur' }]
}

const props = defineProps<{ modelValue: NoticeBarProperty }>()
const emit = defineEmits(['update:modelValue'])
const formData = useVModel(props, 'modelValue', emit)
</script>

<style lang="scss" scoped></style>
