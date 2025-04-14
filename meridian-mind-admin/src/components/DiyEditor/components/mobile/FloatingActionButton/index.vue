<template>
  <div
    :class="[
      'absolute bottom-32px right-[calc(50%-375px/2+32px)] flex z-12 gap-12px items-center',
      {
        'flex-row': property.direction === 'horizontal',
        'flex-col': property.direction === 'vertical'
      }
    ]"
  >
    <template v-if="expanded">
      <div
        v-for="(item, index) in property.list"
        :key="index"
        class="flex flex-col items-center"
        @click="handleActive(index)"
      >
        <el-image :src="item.imgUrl" class="h-27px w-27px" fit="contain">
          <template #error>
            <div class="h-full w-full flex items-center justify-center">
              <Icon :color="item.textColor" icon="ep:picture" />
            </div>
          </template>
        </el-image>
        <span v-if="property.showText" :style="{ color: item.textColor }" class="mt-4px text-12px">
          {{ item.text }}
        </span>
      </div>
    </template>
    <!-- todo: @owen 使用APP主题色 -->
    <el-button circle size="large" type="primary" @click="handleToggleFab">
      <Icon :class="['fab-icon', { active: expanded }]" icon="ep:plus" />
    </el-button>
  </div>
  <!-- 模态背景：展开时显示，点击后折叠 -->
  <div v-if="expanded" class="modal-bg" @click="handleToggleFab"></div>
</template>
<script lang="ts" setup>
import { FloatingActionButtonProperty } from './config'

/** 悬浮按钮 */
defineOptions({ name: 'FloatingActionButton' })
// 定义属性
defineProps<{ property: FloatingActionButtonProperty }>()

// 是否展开
const expanded = ref(false)
// 处理展开/折叠
const handleToggleFab = () => {
  expanded.value = !expanded.value
}
</script>

<style lang="scss" scoped>
/* 模态背景 */
.modal-bg {
  position: absolute;
  left: calc(50% - 375px / 2);
  top: 0;
  z-index: 11;
  width: 375px;
  height: 100%;
  background-color: rgba(#000000, 0.4);
}

.fab-icon {
  transform: rotate(0deg);
  transition: transform 0.3s;

  &.active {
    transform: rotate(135deg);
  }
}
</style>
