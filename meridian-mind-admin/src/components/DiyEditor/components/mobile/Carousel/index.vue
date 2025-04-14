<template>
  <!-- 无图片 -->
  <div
    v-if="property.items.length === 0"
    class="h-250px flex items-center justify-center bg-gray-3"
  >
    <Icon class="text-gray-8 text-120px!" icon="tdesign:image" />
  </div>
  <div v-else class="relative">
    <el-carousel
      :autoplay="property.autoplay"
      :indicator-position="property.indicator === 'number' ? 'none' : undefined"
      :interval="property.interval * 1000"
      :type="property.type === 'card' ? 'card' : ''"
      height="174px"
      @change="handleIndexChange"
    >
      <el-carousel-item v-for="(item, index) in property.items" :key="index">
        <el-image :src="item.imgUrl" class="h-full w-full" />
      </el-carousel-item>
    </el-carousel>
    <div
      v-if="property.indicator === 'number'"
      class="absolute bottom-10px right-10px rounded-xl bg-black p-x-8px p-y-2px text-10px text-white opacity-40"
      >{{ currentIndex }} / {{ property.items.length }}</div
    >
  </div>
</template>
<script lang="ts" setup>
import { CarouselProperty } from './config'

/** 轮播图 */
defineOptions({ name: 'Carousel' })

defineProps<{ property: CarouselProperty }>()

const currentIndex = ref(0)
const handleIndexChange = (index: number) => {
  currentIndex.value = index + 1
}
</script>

<style lang="scss" scoped></style>
