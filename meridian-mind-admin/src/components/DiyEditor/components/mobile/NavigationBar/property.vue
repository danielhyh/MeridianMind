<template>
  <el-form :model="formData" :rules="rules" label-width="80px">
    <el-form-item label="样式" prop="styleType">
      <el-radio-group v-model="formData!.styleType">
        <el-radio value="normal">标准</el-radio>
        <el-tooltip
          content="沉侵式头部仅支持微信小程序、APP，建议页面第一个组件为图片展示类组件"
          placement="top"
        >
          <el-radio value="inner">沉浸式</el-radio>
        </el-tooltip>
      </el-radio-group>
    </el-form-item>
    <el-form-item v-if="formData.styleType === 'inner'" label="常驻显示" prop="alwaysShow">
      <el-radio-group v-model="formData!.alwaysShow">
        <el-radio :value="false">关闭</el-radio>
        <el-tooltip content="常驻显示关闭后,头部小组件将在页面滑动时淡入" placement="top">
          <el-radio :value="true">开启</el-radio>
        </el-tooltip>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="背景类型" prop="bgType">
      <el-radio-group v-model="formData.bgType">
        <el-radio value="color">纯色</el-radio>
        <el-radio value="img">图片</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item v-if="formData.bgType === 'color'" label="背景颜色" prop="bgColor">
      <ColorInput v-model="formData.bgColor" />
    </el-form-item>
    <el-form-item v-else label="背景图片" prop="bgImg">
      <UploadImg v-model="formData.bgImg" :limit="1" height="56px" width="56px" />
    </el-form-item>
    <el-card class="property-group" shadow="never">
      <template #header>
        <div class="flex items-center justify-between">
          <span>内容（小程序）</span>
          <el-form-item class="m-b-0!" prop="_local.previewMp">
            <el-checkbox
              v-model="formData._local.previewMp"
              @change="formData._local.previewOther = !formData._local.previewMp"
              >预览</el-checkbox
            >
          </el-form-item>
        </div>
      </template>
      <NavigationBarCellProperty v-model="formData.mpCells" is-mp />
    </el-card>
    <el-card class="property-group" shadow="never">
      <template #header>
        <div class="flex items-center justify-between">
          <span>内容（非小程序）</span>
          <el-form-item class="m-b-0!" prop="_local.previewOther">
            <el-checkbox
              v-model="formData._local.previewOther"
              @change="formData._local.previewMp = !formData._local.previewOther"
              >预览</el-checkbox
            >
          </el-form-item>
        </div>
      </template>
      <NavigationBarCellProperty v-model="formData.otherCells" :is-mp="false" />
    </el-card>
  </el-form>
</template>

<script lang="ts" setup>
import { NavigationBarProperty } from './config'
import { useVModel } from '@vueuse/core'
import NavigationBarCellProperty from '@/components/DiyEditor/components/mobile/NavigationBar/components/CellProperty.vue'
// 导航栏属性面板
defineOptions({ name: 'NavigationBarProperty' })
// 表单校验
const rules = {
  name: [{ required: true, message: '请输入页面名称', trigger: 'blur' }]
}

const props = defineProps<{ modelValue: NavigationBarProperty }>()
const emit = defineEmits(['update:modelValue'])
const formData = useVModel(props, 'modelValue', emit)
if (!formData.value._local) {
  formData.value._local = { previewMp: true, previewOther: false }
}
</script>

<style lang="scss" scoped></style>
