<template>
  <div :class="['component', { active: active }]">
    <div
      :style="{
        ...style
      }"
    >
      <component :is="component.id" :property="component.property" />
    </div>
    <div class="component-wrap">
      <!-- 左侧：组件名（悬浮的小贴条） -->
      <div v-if="component.name" class="component-name">
        {{ component.name }}
      </div>
      <!-- 右侧：组件操作工具栏 -->
      <div v-if="showToolbar && component.name && active" class="component-toolbar">
        <VerticalButtonGroup type="primary">
          <el-tooltip content="上移" placement="right">
            <el-button :disabled="!canMoveUp" @click.stop="handleMoveComponent(-1)">
              <Icon icon="ep:arrow-up" />
            </el-button>
          </el-tooltip>
          <el-tooltip content="下移" placement="right">
            <el-button :disabled="!canMoveDown" @click.stop="handleMoveComponent(1)">
              <Icon icon="ep:arrow-down" />
            </el-button>
          </el-tooltip>
          <el-tooltip content="复制" placement="right">
            <el-button @click.stop="handleCopyComponent()">
              <Icon icon="ep:copy-document" />
            </el-button>
          </el-tooltip>
          <el-tooltip content="删除" placement="right">
            <el-button @click.stop="handleDeleteComponent()">
              <Icon icon="ep:delete" />
            </el-button>
          </el-tooltip>
        </VerticalButtonGroup>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
// 注册所有的组件
import { components } from '../components/mobile/index'
export default {
  components: { ...components }
}
</script>
<script lang="ts" setup>
import { ComponentStyle, DiyComponent } from '@/components/DiyEditor/util'
import { propTypes } from '@/utils/propTypes'
import { object } from 'vue-types'

/**
 * 组件容器：目前在中间部分
 * 用于包裹组件，为组件提供 背景、外边距、内边距、边框等样式
 */
defineOptions({ name: 'ComponentContainer' })

type DiyComponentWithStyle = DiyComponent<any> & { property: { style?: ComponentStyle } }
const props = defineProps({
  component: object<DiyComponentWithStyle>().isRequired,
  active: propTypes.bool.def(false),
  canMoveUp: propTypes.bool.def(false),
  canMoveDown: propTypes.bool.def(false),
  showToolbar: propTypes.bool.def(true)
})

/**
 * 组件样式
 */
const style = computed(() => {
  let componentStyle = props.component.property.style
  if (!componentStyle) {
    return {}
  }
  return {
    marginTop: `${componentStyle.marginTop || 0}px`,
    marginBottom: `${componentStyle.marginBottom || 0}px`,
    marginLeft: `${componentStyle.marginLeft || 0}px`,
    marginRight: `${componentStyle.marginRight || 0}px`,
    paddingTop: `${componentStyle.paddingTop || 0}px`,
    paddingRight: `${componentStyle.paddingRight || 0}px`,
    paddingBottom: `${componentStyle.paddingBottom || 0}px`,
    paddingLeft: `${componentStyle.paddingLeft || 0}px`,
    borderTopLeftRadius: `${componentStyle.borderTopLeftRadius || 0}px`,
    borderTopRightRadius: `${componentStyle.borderTopRightRadius || 0}px`,
    borderBottomRightRadius: `${componentStyle.borderBottomRightRadius || 0}px`,
    borderBottomLeftRadius: `${componentStyle.borderBottomLeftRadius || 0}px`,
    overflow: 'hidden',
    background:
      componentStyle.bgType === 'color' ? componentStyle.bgColor : `url(${componentStyle.bgImg})`
  }
})

const emits = defineEmits<{
  (e: 'move', direction: number): void
  (e: 'copy'): void
  (e: 'delete'): void
}>()

/**
 * 移动组件
 * @param direction 移动方向
 */
const handleMoveComponent = (direction: number) => {
  emits('move', direction)
}

/**
 * 复制组件
 */
const handleCopyComponent = () => {
  emits('copy')
}

/**
 * 删除组件
 */
const handleDeleteComponent = () => {
  emits('delete')
}
</script>

<style lang="scss" scoped>
$active-border-width: 2px;
$hover-border-width: 1px;
$name-position: -85px;
$toolbar-position: -55px;

/* 组件 */
.component {
  position: relative;
  cursor: move;

  .component-wrap {
    position: absolute;
    top: 0;
    left: -$active-border-width;
    display: block;
    width: 100%;
    height: 100%;

    /* 鼠标放到组件上时 */
    &:hover {
      border: $hover-border-width dashed var(--el-color-primary);
      box-shadow: 0 0 5px 0 rgb(24 144 255 / 30%);

      .component-name {
        top: $hover-border-width;

        /* 防止加了边框之后，位置移动 */
        left: $name-position - $hover-border-width;
      }
    }

    /* 左侧：组件名称 */
    .component-name {
      position: absolute;
      top: $active-border-width;
      left: $name-position;
      display: block;
      width: 80px;
      height: 25px;
      font-size: 12px;
      color: #6a6a6a;
      line-height: 25px;
      text-align: center;
      background: #fff;
      box-shadow:
        0 0 4px #00000014,
        0 2px 6px #0000000f,
        0 4px 8px 2px #0000000a;

      /* 右侧小三角 */
      &::after {
        position: absolute;
        top: 7.5px;
        right: -10px;
        width: 0;
        height: 0;
        border: 5px solid transparent;
        border-left-color: #fff;
        content: ' ';
      }
    }

    /* 右侧：组件操作工具栏 */
    .component-toolbar {
      position: absolute;
      top: 0;
      right: $toolbar-position;
      display: none;

      /* 左侧小三角 */
      &::before {
        position: absolute;
        top: 10px;
        left: -10px;
        width: 0;
        height: 0;
        border: 5px solid transparent;
        border-right-color: #2d8cf0;
        content: ' ';
      }
    }
  }

  /* 组件选中时 */
  &.active {
    margin-bottom: 4px;

    .component-wrap {
      margin-bottom: $active-border-width + $active-border-width;
      border: $active-border-width solid var(--el-color-primary) !important;
      box-shadow: 0 0 10px 0 rgb(24 144 255 / 30%);

      .component-name {
        top: 0 !important;

        /* 防止加了边框之后，位置移动 */
        left: $name-position - $active-border-width !important;
        color: #fff;
        background: var(--el-color-primary);

        &::after {
          border-left-color: var(--el-color-primary);
        }
      }

      .component-toolbar {
        display: block;
      }
    }
  }
}
</style>
