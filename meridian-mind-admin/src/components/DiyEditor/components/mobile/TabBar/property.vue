<template>
  <div class="tab-bar">
    <!-- 表单 -->
    <el-form :model="formData" label-width="80px">
      <el-form-item label="主题" prop="theme">
        <el-select v-model="formData!.theme" @change="handleThemeChange">
          <el-option
            v-for="(theme, index) in THEME_LIST"
            :key="index"
            :label="theme.name"
            :value="theme.id"
          >
            <template #default>
              <div class="flex items-center justify-between">
                <Icon :color="theme.color" :icon="theme.icon" />
                <span>{{ theme.name }}</span>
              </div>
            </template>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="默认颜色">
        <ColorInput v-model="formData!.style.color" />
      </el-form-item>
      <el-form-item label="选中颜色">
        <ColorInput v-model="formData!.style.activeColor" />
      </el-form-item>
      <el-form-item label="导航背景">
        <el-radio-group v-model="formData!.style.bgType">
          <el-radio-button value="color">纯色</el-radio-button>
          <el-radio-button value="img">图片</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="formData!.style.bgType === 'color'" label="选择颜色">
        <ColorInput v-model="formData!.style.bgColor" />
      </el-form-item>
      <el-form-item v-if="formData!.style.bgType === 'img'" label="选择图片">
        <UploadImg v-model="formData!.style.bgImg" class="min-w-200px" height="50px" width="100%">
          <template #tip> 建议尺寸 375 * 50 </template>
        </UploadImg>
      </el-form-item>

      <el-text tag="p">图标设置</el-text>
      <el-text size="small" type="info"> 拖动左上角的小圆点可对其排序, 图标建议尺寸 44*44 </el-text>
      <Draggable v-model="formData.items" :limit="5">
        <template #default="{ element }">
          <div class="m-b-8px flex items-center justify-around">
            <div class="flex flex-col items-center justify-between">
              <UploadImg
                v-model="element.iconUrl"
                :show-btn-text="false"
                :show-delete="false"
                height="40px"
                width="40px"
              />
              <el-text size="small">未选中</el-text>
            </div>
            <div>
              <UploadImg
                v-model="element.activeIconUrl"
                :show-btn-text="false"
                :show-delete="false"
                height="40px"
                width="40px"
              />
              <el-text>已选中</el-text>
            </div>
          </div>
          <el-form-item class="m-b-8px!" label="文字" label-width="48px" prop="text">
            <el-input v-model="element.text" placeholder="请输入文字" />
          </el-form-item>
          <el-form-item class="m-b-0!" label="链接" label-width="48px" prop="url">
            <AppLinkInput v-model="element.url" />
          </el-form-item>
        </template>
      </Draggable>
    </el-form>
  </div>
</template>

<script lang="ts" setup>
import { TabBarProperty, component, THEME_LIST } from './config'
import { useVModel } from '@vueuse/core'
// 底部导航栏
defineOptions({ name: 'TabBarProperty' })

const props = defineProps<{ modelValue: TabBarProperty }>()
const emit = defineEmits(['update:modelValue'])
const formData = useVModel(props, 'modelValue', emit)

// 将数据库的值更新到右侧属性栏
component.property.items = formData.value.items

// 要的主题
const handleThemeChange = () => {
  const theme = THEME_LIST.find((theme) => theme.id === formData.value.theme)
  if (theme?.color) {
    formData.value.style.activeColor = theme.color
  }
}
</script>

<style lang="scss" scoped></style>
