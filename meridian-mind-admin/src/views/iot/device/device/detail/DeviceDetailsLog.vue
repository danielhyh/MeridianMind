<!-- 设备日志 -->
<template>
  <ContentWrap>
    <!-- 搜索区域 -->
    <el-form :model="queryParams" inline>
      <el-form-item>
        <el-select v-model="queryParams.type" class="!w-160px" placeholder="所有">
          <el-option label="所有" value="" />
          <!-- TODO @super：搞成枚举 -->
          <el-option label="状态" value="state" />
          <el-option label="事件" value="event" />
          <el-option label="属性" value="property" />
          <el-option label="服务" value="service" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input v-model="queryParams.identifier" class="!w-200px" placeholder="日志识符" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">
          <Icon class="mr-5px" icon="ep:search" /> 搜索
        </el-button>
        <el-switch
          v-model="autoRefresh"
          active-text="定时刷新"
          class="ml-20px"
          inactive-text="定时刷新"
          inline-prompt
          size="large"
          style="--el-switch-on-color: #13ce66"
          width="80"
        />
      </el-form-item>
    </el-form>

    <!-- 日志列表 -->
    <el-table v-loading="loading" :data="list" :stripe="true" class="whitespace-nowrap">
      <el-table-column align="center" label="时间" prop="ts" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.ts) }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="类型" prop="type" width="120" />
      <!-- TODO @super：标识符需要翻译 -->
      <el-table-column align="center" label="标识符" prop="identifier" width="120" />
      <el-table-column :show-overflow-tooltip="true" align="center" label="内容" prop="content" />
    </el-table>

    <!-- 分页 -->
    <div class="mt-10px flex justify-end">
      <Pagination
        v-model:limit="queryParams.pageSize"
        v-model:page="queryParams.pageNo"
        :total="total"
        @pagination="getLogList"
      />
    </div>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { DeviceApi } from '@/api/iot/device/device'
import { formatDate } from '@/utils/formatTime'

const props = defineProps<{
  deviceKey: string
}>()

// 查询参数
const queryParams = reactive({
  deviceKey: props.deviceKey,
  type: '',
  identifier: '',
  pageNo: 1,
  pageSize: 10
})

// 列表数据
const loading = ref(false)
const total = ref(0)
const list = ref([])
const autoRefresh = ref(false)
let timer: any = null // TODO @super：autoRefreshEnable，autoRefreshTimer；对应上

// 类型映射 TODO @super：需要删除么？
const typeMap = {
  lifetime: '生命周期',
  state: '设备状态',
  property: '属性',
  event: '事件',
  service: '服务'
}

/** 查询日志列表 */
const getLogList = async () => {
  if (!props.deviceKey) return
  loading.value = true
  try {
    const data = await DeviceApi.getDeviceLogPage(queryParams)
    total.value = data.total
    list.value = data.list
  } finally {
    loading.value = false
  }
}

/** 获取日志名称 */
const getLogName = (log: any) => {
  const { type, identifier } = log
  let name = '未知'

  if (type === 'property') {
    if (identifier === 'set_reply') name = '设置回复'
    else if (identifier === 'report') name = '上报'
    else if (identifier === 'set') name = '设置'
  } else if (type === 'state') {
    name = identifier === 'online' ? '上线' : '下线'
  } else if (type === 'lifetime') {
    name = identifier === 'register' ? '注册' : name
  }

  return `${name}(${identifier})`
}

/** 搜索操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getLogList()
}

/** 监听自动刷新 */
watch(autoRefresh, (newValue) => {
  if (newValue) {
    timer = setInterval(() => {
      getLogList()
    }, 5000)
  } else {
    clearInterval(timer)
    timer = null
  }
})

/** 监听设备标识变化 */
watch(
  () => props.deviceKey,
  (newValue) => {
    if (newValue) {
      handleQuery()
    }
  }
)

/** 组件卸载时清除定时器 */
onBeforeUnmount(() => {
  if (timer) {
    clearInterval(timer)
  }
})

/** 初始化 */
onMounted(() => {
  if (props.deviceKey) {
    getLogList()
  }
})
</script>
