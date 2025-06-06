<template>
  <el-scrollbar ref="containerRef" class="z-1 min-h-30px" wrap-class="w-full">
    <!-- 商品网格 -->
    <div
      :style="{
        gridGap: `${property.space}px`,
        gridTemplateColumns,
        width: scrollbarWidth
      }"
      class="grid overflow-x-auto"
    >
      <!-- 商品 -->
      <div
        v-for="(spu, index) in spuList"
        :key="index"
        :style="{
          borderTopLeftRadius: `${property.borderRadiusTop}px`,
          borderTopRightRadius: `${property.borderRadiusTop}px`,
          borderBottomLeftRadius: `${property.borderRadiusBottom}px`,
          borderBottomRightRadius: `${property.borderRadiusBottom}px`
        }"
        class="relative box-content flex flex-row flex-wrap overflow-hidden bg-white"
      >
        <!-- 角标 -->
        <div
          v-if="property.badge.show"
          class="absolute left-0 top-0 z-1 items-center justify-center"
        >
          <el-image :src="property.badge.imgUrl" class="h-26px w-38px" fit="cover" />
        </div>
        <!-- 商品封面图 -->
        <el-image :src="spu.picUrl" :style="{ width: imageSize, height: imageSize }" fit="cover" />
        <div
          :class="[
            'flex flex-col gap-8px p-8px box-border',
            {
              'w-[calc(100%-64px)]': columns === 2,
              'w-full': columns === 3
            }
          ]"
        >
          <!-- 商品名称 -->
          <div
            v-if="property.fields.name.show"
            :style="{ color: property.fields.name.color }"
            class="truncate text-12px"
          >
            {{ spu.name }}
          </div>
          <div>
            <!-- 商品价格 -->
            <span
              v-if="property.fields.price.show"
              :style="{ color: property.fields.price.color }"
              class="text-12px"
            >
              ￥{{ fenToYuan(spu.price) }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </el-scrollbar>
</template>
<script lang="ts" setup>
import { ProductListProperty } from './config'
import * as ProductSpuApi from '@/api/mall/product/spu'
import { fenToYuan } from '@/utils'

/** 商品栏 */
defineOptions({ name: 'ProductList' })
// 定义属性
const props = defineProps<{ property: ProductListProperty }>()
// 商品列表
const spuList = ref<ProductSpuApi.Spu[]>([])
watch(
  () => props.property.spuIds,
  async () => {
    spuList.value = await ProductSpuApi.getSpuDetailList(props.property.spuIds)
  },
  {
    immediate: true,
    deep: true
  }
)
// 手机宽度
const phoneWidth = ref(375)
// 容器
const containerRef = ref()
// 商品的列数
const columns = ref(2)
// 滚动条宽度
const scrollbarWidth = ref('100%')
// 商品图大小
const imageSize = ref('0')
// 商品网络列数
const gridTemplateColumns = ref('')
// 计算布局参数
watch(
  () => [props.property, phoneWidth, spuList.value.length],
  () => {
    // 计算列数
    columns.value = props.property.layoutType === 'twoCol' ? 2 : 3
    // 每列的宽度为：（总宽度 - 间距 * (列数 - 1)）/ 列数
    const productWidth =
      (phoneWidth.value - props.property.space * (columns.value - 1)) / columns.value
    // 商品图布局：2列时，左右布局 3列时，上下布局
    imageSize.value = columns.value === 2 ? '64px' : `${productWidth}px`
    // 根据布局类型，计算行数、列数
    if (props.property.layoutType === 'horizSwiper') {
      // 单行显示
      gridTemplateColumns.value = `repeat(auto-fill, ${productWidth}px)`
      // 显示滚动条
      scrollbarWidth.value = `${
        productWidth * spuList.value.length + props.property.space * (spuList.value.length - 1)
      }px`
    } else {
      // 指定列数
      gridTemplateColumns.value = `repeat(${columns.value}, auto)`
      // 不滚动
      scrollbarWidth.value = '100%'
    }
  },
  { immediate: true, deep: true }
)
onMounted(() => {
  // 提取手机宽度
  phoneWidth.value = containerRef.value?.wrapRef?.offsetWidth || 375
})
</script>

<style lang="scss" scoped></style>
