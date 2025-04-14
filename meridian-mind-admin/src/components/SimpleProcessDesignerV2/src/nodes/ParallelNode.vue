<template>
  <div class="branch-node-wrapper">
    <div class="branch-node-container">
      <div
        v-if="readonly"
        :class="`${useTaskStatusClass(currentNode?.activityStatus)}`"
        class="branch-node-readonly"
      >
        <span class="iconfont icon-parallel icon-size parallel"></span>
      </div>
      <el-button v-else class="branch-node-add" color="#626aef" plain @click="addCondition"
        >添加分支</el-button
      >
      <div
        v-for="(item, index) in currentNode.conditionNodes"
        :key="index"
        class="branch-node-item"
      >
        <template v-if="index == 0">
          <div class="branch-line-first-top"></div>
          <div class="branch-line-first-bottom"></div>
        </template>
        <template v-if="index + 1 == currentNode.conditionNodes?.length">
          <div class="branch-line-last-top"></div>
          <div class="branch-line-last-bottom"></div>
        </template>
        <div class="node-wrapper">
          <div class="node-container">
            <div :class="`${useTaskStatusClass(item.activityStatus)}`" class="node-box">
              <div class="branch-node-title-container">
                <div v-if="showInputs[index]">
                  <input
                    v-model="item.name"
                    v-mountedFocus
                    class="input-max-width editable-title-input"
                    type="text"
                    @blur="blurEvent(index)"
                  />
                </div>
                <div v-else class="branch-title" @click="clickEvent(index)"> {{ item.name }} </div>
                <div class="branch-priority">无优先级</div>
              </div>
              <div class="branch-node-content" @click="conditionNodeConfig(item.id)">
                <div v-if="item.showText" :title="item.showText" class="branch-node-text">
                  {{ item.showText }}
                </div>
                <div v-else class="branch-node-text">
                  {{ NODE_DEFAULT_TEXT.get(NodeType.CONDITION_NODE) }}
                </div>
              </div>
              <div v-if="!readonly" class="node-toolbar">
                <div class="toolbar-icon">
                  <Icon
                    :size="18"
                    color="#0089ff"
                    icon="ep:circle-close-filled"
                    @click="deleteCondition(index)"
                  />
                </div>
              </div>
            </div>
            <NodeHandler v-model:child-node="item.childNode" :current-node="item" />
          </div>
        </div>
        <!-- 递归显示子节点  -->
        <ProcessNodeTree
          v-if="item && item.childNode"
          v-model:flow-node="item.childNode"
          :parent-node="item"
          @find:recursive-find-parent-node="recursiveFindParentNode"
        />
      </div>
    </div>
    <NodeHandler
      v-if="currentNode"
      v-model:child-node="currentNode.childNode"
      :current-node="currentNode"
    />
  </div>
</template>

<script lang="ts" setup>
import NodeHandler from '../NodeHandler.vue'
import ProcessNodeTree from '../ProcessNodeTree.vue'
import { SimpleFlowNode, NodeType, NODE_DEFAULT_TEXT } from '../consts'
import { useTaskStatusClass } from '../node'
import { generateUUID } from '@/utils'
const { proxy } = getCurrentInstance() as any
defineOptions({
  name: 'ParallelNode'
})
const props = defineProps({
  flowNode: {
    type: Object as () => SimpleFlowNode,
    required: true
  }
})
// 定义事件，更新父组件
const emits = defineEmits<{
  'update:modelValue': [node: SimpleFlowNode | undefined]
  'find:parentNode': [nodeList: SimpleFlowNode[], nodeType: number]
  'find:recursiveFindParentNode': [
    nodeList: SimpleFlowNode[],
    curentNode: SimpleFlowNode,
    nodeType: number
  ]
}>()

const currentNode = ref<SimpleFlowNode>(props.flowNode)
// 是否只读
const readonly = inject<Boolean>('readonly')

watch(
  () => props.flowNode,
  (newValue) => {
    currentNode.value = newValue
  }
)

const showInputs = ref<boolean[]>([])
// 失去焦点
const blurEvent = (index: number) => {
  showInputs.value[index] = false
  const conditionNode = currentNode.value.conditionNodes?.at(index) as SimpleFlowNode
  conditionNode.name = conditionNode.name || `并行${index + 1}`
}

// 点击条件名称
const clickEvent = (index: number) => {
  showInputs.value[index] = true
}

const conditionNodeConfig = (nodeId: string) => {
  const conditionNode = proxy.$refs[nodeId][0]
  conditionNode.open()
}

// 新增条件
const addCondition = () => {
  const conditionNodes = currentNode.value.conditionNodes
  if (conditionNodes) {
    const len = conditionNodes.length
    let lastIndex = len - 1
    const conditionData: SimpleFlowNode = {
      id: 'Flow_' + generateUUID(),
      name: '并行' + len,
      showText: '无需配置条件同时执行',
      type: NodeType.CONDITION_NODE,
      childNode: undefined,
      conditionNodes: []
    }
    conditionNodes.splice(lastIndex, 0, conditionData)
  }
}

// 删除条件
const deleteCondition = (index: number) => {
  const conditionNodes = currentNode.value.conditionNodes
  if (conditionNodes) {
    conditionNodes.splice(index, 1)
    if (conditionNodes.length == 1) {
      const childNode = currentNode.value.childNode
      // 更新此节点为后续孩子节点
      emits('update:modelValue', childNode)
    }
  }
}

// 递归从父节点中查询匹配的节点
const recursiveFindParentNode = (
  nodeList: SimpleFlowNode[],
  node: SimpleFlowNode,
  nodeType: number
) => {
  if (!node || node.type === NodeType.START_USER_NODE) {
    return
  }
  if (node.type === nodeType) {
    nodeList.push(node)
  }
  // 条件节点 (NodeType.CONDITION_NODE) 比较特殊。需要调用其父节点并行节点（NodeType.PARALLEL_NODE) 继续查找
  emits('find:parentNode', nodeList, nodeType)
}
</script>
