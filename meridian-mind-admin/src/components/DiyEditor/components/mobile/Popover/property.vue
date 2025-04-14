<template>
  <el-form :model="formData" label-width="80px">
    <Draggable v-model="formData.list" :empty-item="{ showType: 'once' }">
      <template #default="{ element, index }">
        <el-form-item :prop="`list[${index}].imgUrl`" label="图片">
          <UploadImg v-model="element.imgUrl" height="56px" width="56px" />
        </el-form-item>
        <el-form-item :prop="`list[${index}].url`" label="跳转链接">
          <AppLinkInput v-model="element.url" />
        </el-form-item>
        <el-form-item :prop="`list[${index}].showType`" label="显示次数">
          <el-radio-group v-model="element.showType">
            <el-tooltip content="只显示一次，下次打开时不显示" placement="bottom">
              <el-radio value="once">一次</el-radio>
            </el-tooltip>
            <el-tooltip content="每次打开时都会显示" placement="bottom">
              <el-radio value="always">不限</el-radio>
            </el-tooltip>
          </el-radio-group>
        </el-form-item>
      </template>
    </Draggable>
  </el-form>
</template>

<script lang="ts" setup>
import { PopoverProperty } from './config'
import { useVModel } from '@vueuse/core'

// 弹窗广告属性面板
defineOptions({ name: 'PopoverProperty' })

const props = defineProps<{ modelValue: PopoverProperty }>()
const emit = defineEmits(['update:modelValue'])
const formData = useVModel(props, 'modelValue', emit)
</script>

<style lang="scss" scoped></style>
