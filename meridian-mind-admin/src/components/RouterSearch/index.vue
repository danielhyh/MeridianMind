<template>
  <ElDialog v-if="isModal" v-model="showSearch" :show-close="false" title="菜单搜索">
    <el-select
      :remote-method="remoteMethod"
      :reserve-keyword="false"
      filterable
      placeholder="请输入菜单内容"
      remote
      style="width: 100%"
      @change="handleChange"
    >
      <el-option
        v-for="item in options"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      />
    </el-select>
  </ElDialog>
  <div v-else class="custom-hover" @click.stop="showTopSearch = !showTopSearch">
    <Icon icon="ep:search" />
    <el-select
      :class="showTopSearch ? '!w-220px ml2' : '!w-0'"
      :remote-method="remoteMethod"
      :reserve-keyword="false"
      class="overflow-hidden transition-all-600"
      filterable
      placeholder="请输入菜单内容"
      remote
      @change="handleChange"
      @click.stop
    >
      <el-option
        v-for="item in options"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      />
    </el-select>
  </div>
</template>

<script lang="ts" setup>
defineProps({
  isModal: {
    type: Boolean,
    default: true
  }
})

const router = useRouter() // 路由对象
const showSearch = ref(false) // 是否显示弹框
const showTopSearch = ref(false) // 是否显示顶部搜索框
const value: Ref = ref('') // 用户输入的值

const routers = router.getRoutes() // 路由对象
const options = computed(() => {
  // 提示选项
  if (!value.value) {
    return []
  }
  const list = routers.filter((item: any) => {
    if (item.meta.title?.indexOf(value.value) > -1 || item.path.indexOf(value.value) > -1) {
      return true
    }
  })
  return list.map((item) => {
    return {
      label: `${item.meta.title}${item.path}`,
      value: item.path
    }
  })
})

function remoteMethod(data) {
  // 这里可以执行相应的操作（例如打开搜索框等）
  value.value = data
}

function handleChange(path) {
  router.push({ path })
  hiddenSearch()
  hiddenTopSearch()
}

function hiddenSearch() {
  showSearch.value = false
}

function hiddenTopSearch() {
  showTopSearch.value = false
}

onMounted(() => {
  window.addEventListener('keydown', listenKey)
  window.addEventListener('click', hiddenTopSearch)
})

onUnmounted(() => {
  window.removeEventListener('keydown', listenKey)
  window.removeEventListener('click', hiddenTopSearch)
})

// 监听 ctrl + k
function listenKey(event) {
  if ((event.ctrlKey || event.metaKey) && event.key === 'k') {
    // 阻止触发浏览器默认事件
    event.preventDefault()
    showSearch.value = !showSearch.value
    // 这里可以执行相应的操作（例如打开搜索框等）
  }
}

defineExpose({
  openSearch: () => {
    showSearch.value = true
  }
})
</script>
