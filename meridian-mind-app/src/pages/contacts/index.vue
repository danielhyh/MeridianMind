<!-- 通讯录界面：用于 im 聊天 -->
<route lang="json5" type="page">
{
  layout: 'tabbar',
  style: {
    navigationStyle: 'custom',
    navigationBarTitleText: '通讯录',
  },
}
</route>

<template>
  <!-- 全部的背景 -->
  <view class="">
    <!-- 安全位置插入 -->
    <view :style="{ marginTop: safeAreaInsets?.top + 'px' }" class="overflow-hidden">
      <!-- 自定义顶部的导航栏 -->
      <view class="h-80rpx flex justify-between items-center px-2 bg-[#F8F8F8]">
        <view class="w-50rpx">
          <view
            v-show="historyStack.length > 1"
            class="i-ic-baseline-keyboard-arrow-left"
            @click="handleLeftArrow"
          ></view>
        </view>
        <view class="text-22rpx grow-1 text-center">通讯录</view>
        <view class="w-50rpx"></view>
      </view>

      <!-- 自定义间距 -->
      <view class="bg-[#f2f3f7] h-2 w-full"></view>
      <!-- 消息列表 -->
      <view class="flex flex-col bg-white p-2">
        <view v-for="(item, index) in treeList" :key="index" @click="handleClick(item)">
          <ContactsItem :item="item" />
        </view>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
//
import {onLoad} from '@dcloudio/uni-app'
import {useToast} from 'wot-design-uni'
import ContactsItem from './components/ContactsItem.vue'
import contactsTree from './mock'
// 提示组件
const toast = useToast()

defineOptions({
  name: 'Contacts',
})

// 获取屏幕边界到安全区域距离
const { safeAreaInsets } = uni.getSystemInfoSync()

// 搜索树的子节点
const searchTree = (tree, targetId) => {
  console.log('targetId', targetId)
  console.log('tree', tree)
  if (!targetId) {
    return tree
  }

  let queue = [...tree]
  let result = []
  while (queue.length) {
    const node = queue.shift()
    if (node.id === targetId) {
      result = node.children
      break
    }
    if (node.children) {
      queue = queue.concat(node.children)
    }
  }
  return result
}

const treeList = ref({})

const handleClick = (item) => {
  console.log('展示当前的item', item.id)

  if (item.isLeaf) {
    toast.info('功能尚未开放')
  } else {
    historyStack.value.push(item.id)
    treeList.value = searchTree(contactsTree, item.id)
  }
}

const handleLeftArrow = () => {
  if (historyStack.value.length > 1) {
    historyStack.value.pop()
  }
  const id = historyStack.value.at(-1)
  treeList.value = searchTree(contactsTree, id)
}

// 历史栈
const historyStack = ref([])

onLoad((_option) => {
  // 默认展示第一层级
  historyStack.value.push(undefined)
  treeList.value = searchTree(contactsTree, undefined)
})
</script>

<style lang="scss" scoped>
//
</style>
