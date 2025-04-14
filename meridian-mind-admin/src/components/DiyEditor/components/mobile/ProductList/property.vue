<template>
  <ComponentContainerProperty v-model="formData.style">
    <el-form :model="formData" label-width="80px">
      <el-card class="property-group" header="商品列表" shadow="never">
        <SpuShowcase v-model="formData.spuIds" />
      </el-card>
      <el-card class="property-group" header="商品样式" shadow="never">
        <el-form-item label="布局" prop="type">
          <el-radio-group v-model="formData.layoutType">
            <el-tooltip class="item" content="双列" placement="bottom">
              <el-radio-button value="twoCol">
                <Icon icon="fluent:text-column-two-24-filled" />
              </el-radio-button>
            </el-tooltip>
            <el-tooltip class="item" content="三列" placement="bottom">
              <el-radio-button value="threeCol">
                <Icon icon="fluent:text-column-three-24-filled" />
              </el-radio-button>
            </el-tooltip>
            <el-tooltip class="item" content="水平滑动" placement="bottom">
              <el-radio-button value="horizSwiper">
                <Icon icon="system-uicons:carousel" />
              </el-radio-button>
            </el-tooltip>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="商品名称" prop="fields.name.show">
          <div class="flex gap-8px">
            <ColorInput v-model="formData.fields.name.color" />
            <el-checkbox v-model="formData.fields.name.show" />
          </div>
        </el-form-item>
        <el-form-item label="商品价格" prop="fields.price.show">
          <div class="flex gap-8px">
            <ColorInput v-model="formData.fields.price.color" />
            <el-checkbox v-model="formData.fields.price.show" />
          </div>
        </el-form-item>
      </el-card>
      <el-card class="property-group" header="角标" shadow="never">
        <el-form-item label="角标" prop="badge.show">
          <el-switch v-model="formData.badge.show" />
        </el-form-item>
        <el-form-item v-if="formData.badge.show" label="角标" prop="badge.imgUrl">
          <UploadImg v-model="formData.badge.imgUrl" height="44px" width="72px">
            <template #tip> 建议尺寸：36 * 22 </template>
          </UploadImg>
        </el-form-item>
      </el-card>
      <el-card class="property-group" header="商品样式" shadow="never">
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
      </el-card>
    </el-form>
  </ComponentContainerProperty>
</template>

<script lang="ts" setup>
import { ProductListProperty } from './config'
import { useVModel } from '@vueuse/core'
import SpuShowcase from '@/views/mall/product/spu/components/SpuShowcase.vue'

// 商品栏属性面板
defineOptions({ name: 'ProductListProperty' })

const props = defineProps<{ modelValue: ProductListProperty }>()
const emit = defineEmits(['update:modelValue'])
const formData = useVModel(props, 'modelValue', emit)
</script>

<style lang="scss" scoped></style>
