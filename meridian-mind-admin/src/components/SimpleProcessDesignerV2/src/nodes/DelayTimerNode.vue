<template>
  <div class="node-wrapper">
    <div class="node-container">
      <div
        :class="[
          { 'node-config-error': !currentNode.showText },
          `${useTaskStatusClass(currentNode?.activityStatus)}`
        ]"
        class="node-box"
      >
        <div class="node-title-container">
          <div class="node-title-icon delay-node"><span class="iconfont icon-delay"></span></div>
          <input
            v-if="!readonly && showInput"
            v-model="currentNode.name"
            v-mountedFocus
            :placeholder="currentNode.name"
            class="editable-title-input"
            type="text"
            @blur="blurEvent()"
          />
          <div v-else class="node-title" @click="clickTitle">
            {{ currentNode.name }}
          </div>
        </div>
        <div class="node-content" @click="openNodeConfig">
          <div v-if="currentNode.showText" :title="currentNode.showText" class="node-text">
            {{ currentNode.showText }}
          </div>
          <div v-else class="node-text">
            {{ NODE_DEFAULT_TEXT.get(NodeType.DELAY_TIMER_NODE) }}
          </div>
          <Icon v-if="!readonly" icon="ep:arrow-right-bold" />
        </div>
        <div v-if="!readonly" class="node-toolbar">
          <div class="toolbar-icon"
            ><Icon :size="18" color="#0089ff" icon="ep:circle-close-filled" @click="deleteNode"
          /></div>
        </div>
      </div>

      <!-- 传递子节点给添加节点组件。会在子节点前面添加节点 -->
      <NodeHandler
        v-if="currentNode"
        v-model:child-node="currentNode.childNode"
        :current-node="currentNode"
      />
    </div>
    <DelayTimerNodeConfig
      v-if="!readonly && currentNode"
      ref="nodeSetting"
      :flow-node="currentNode"
    />
  </div>
</template>
<script lang="ts" setup>
import { SimpleFlowNode, NodeType, NODE_DEFAULT_TEXT } from '../consts'
import NodeHandler from '../NodeHandler.vue'
import { useNodeName2, useWatchNode, useTaskStatusClass } from '../node'
import DelayTimerNodeConfig from '../nodes-config/DelayTimerNodeConfig.vue'
defineOptions({
  name: 'DelayTimerNode'
})
const props = defineProps({
  flowNode: {
    type: Object as () => SimpleFlowNode,
    required: true
  }
})
// 定义事件，更新父组件。
const emits = defineEmits<{
  'update:flowNode': [node: SimpleFlowNode | undefined]
}>()
// 是否只读
const readonly = inject<Boolean>('readonly')
// 监控节点的变化
const currentNode = useWatchNode(props)
// 节点名称编辑
const { showInput, blurEvent, clickTitle } = useNodeName2(currentNode, NodeType.DELAY_TIMER_NODE)

const nodeSetting = ref()
// 打开节点配置
const openNodeConfig = () => {
  if (readonly) {
    return
  }
  nodeSetting.value.showDelayTimerNodeConfig(currentNode.value)
  nodeSetting.value.openDrawer()
}

// 删除节点。更新当前节点为孩子节点
const deleteNode = () => {
  emits('update:flowNode', currentNode.value.childNode)
}
</script>

<style lang="scss" scoped></style>
