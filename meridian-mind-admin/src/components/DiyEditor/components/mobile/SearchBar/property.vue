<template>
  <ComponentContainerProperty v-model="formData.style">
    <!-- 表单 -->
    <el-form :model="formData" class="m-t-8px" label-width="80px">
      <el-card class="property-group" header="搜索热词" shadow="never">
        <Draggable v-model="formData.hotKeywords" :empty-item="''">
          <template #default="{ index }">
            <el-input v-model="formData.hotKeywords[index]" placeholder="请输入热词" />
          </template>
        </Draggable>
      </el-card>
      <el-card class="property-group" header="搜索样式" shadow="never">
        <el-form-item label="框体样式">
          <el-radio-group v-model="formData!.borderRadius">
            <el-tooltip content="方形" placement="top">
              <el-radio-button :value="0">
                <Icon icon="tabler:input-search" />
              </el-radio-button>
            </el-tooltip>
            <el-tooltip content="圆形" placement="top">
              <el-radio-button :value="10">
                <Icon icon="iconoir:input-search" />
              </el-radio-button>
            </el-tooltip>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="提示文字" prop="placeholder">
          <el-input v-model="formData.placeholder" />
        </el-form-item>
        <el-form-item label="文本位置" prop="placeholderPosition">
          <el-radio-group v-model="formData!.placeholderPosition">
            <el-tooltip content="居左" placement="top">
              <el-radio-button value="left">
                <Icon icon="ant-design:align-left-outlined" />
              </el-radio-button>
            </el-tooltip>
            <el-tooltip content="居中" placement="top">
              <el-radio-button value="center">
                <Icon icon="ant-design:align-center-outlined" />
              </el-radio-button>
            </el-tooltip>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="扫一扫" prop="showScan">
          <el-switch v-model="formData!.showScan" />
        </el-form-item>
        <el-form-item label="框体高度" prop="height">
          <el-slider v-model="formData!.height" :max="50" :min="28" input-size="small" show-input />
        </el-form-item>
        <el-form-item label="框体颜色" prop="backgroundColor">
          <ColorInput v-model="formData.backgroundColor" />
        </el-form-item>
        <el-form-item class="lef" label="文本颜色" prop="textColor">
          <ColorInput v-model="formData.textColor" />
        </el-form-item>
      </el-card>
    </el-form>
  </ComponentContainerProperty>
</template>

<script lang="ts" setup>
import { useVModel } from '@vueuse/core'
import { SearchProperty } from '@/components/DiyEditor/components/mobile/SearchBar/config'

/** 搜索框属性面板 */
defineOptions({ name: 'SearchProperty' })

const props = defineProps<{ modelValue: SearchProperty }>()
const emit = defineEmits(['update:modelValue'])
const formData = useVModel(props, 'modelValue', emit)
</script>

<style lang="scss" scoped></style>
