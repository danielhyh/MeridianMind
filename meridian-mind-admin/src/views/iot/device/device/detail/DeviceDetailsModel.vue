<!-- 设备物模型：运行状态（属性）、事件管理、服务调用 -->
<template>
  <ContentWrap>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="运行状态" name="status">
        <ContentWrap>
          <!-- 搜索工作栏 -->
          <el-form
            ref="queryFormRef"
            :inline="true"
            :model="queryParams"
            class="-mb-15px"
            label-width="68px"
          >
            <el-form-item label="标识符" prop="identifier">
              <el-input
                v-model="queryParams.identifier"
                class="!w-240px"
                clearable
                placeholder="请输入标识符"
              />
            </el-form-item>
            <el-form-item label="属性名称" prop="name">
              <el-input
                v-model="queryParams.name"
                class="!w-240px"
                clearable
                placeholder="请输入属性名称"
              />
            </el-form-item>
            <el-form-item>
              <el-button @click="handleQuery"
                ><Icon class="mr-5px" icon="ep:search" /> 搜索</el-button
              >
              <el-button @click="resetQuery"
                ><Icon class="mr-5px" icon="ep:refresh" /> 重置</el-button
              >
            </el-form-item>
          </el-form>
        </ContentWrap>
        <ContentWrap>
          <el-tabs>
            <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
              <el-table-column align="center" label="属性标识符" prop="property.identifier" />
              <el-table-column align="center" label="属性名称" prop="property.name" />
              <el-table-column align="center" label="数据类型" prop="property.dataType" />
              <el-table-column align="center" label="属性值" prop="value" />
              <el-table-column
                :formatter="dateFormatter"
                align="center"
                label="更新时间"
                prop="updateTime"
                width="180px"
              />
              <el-table-column align="center" label="操作">
                <template #default="scope">
                  <el-button
                    link
                    type="primary"
                    @click="openDetail(props.device.id, scope.row.property.identifier)"
                  >
                    查看数据
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tabs>
          <!-- 表单弹窗：添加/修改 -->
          <DeviceDataDetail ref="detailRef" :device="device" :product="product" />
        </ContentWrap>
      </el-tab-pane>
      <el-tab-pane label="事件管理" name="event">
        <p>事件管理</p>
      </el-tab-pane>
      <el-tab-pane label="服务调用" name="service">
        <p>服务调用</p>
      </el-tab-pane>
    </el-tabs>
  </ContentWrap>
</template>
<script lang="ts" setup>
import { ProductVO } from '@/api/iot/product/product'
import { DeviceApi, DeviceDataVO, DeviceVO } from '@/api/iot/device/device'
import { dateFormatter } from '@/utils/formatTime'
import DeviceDataDetail from './DeviceDataDetail.vue'

const props = defineProps<{ product: ProductVO; device: DeviceVO }>()

const loading = ref(true) // 列表的加载中
const list = ref<DeviceDataVO[]>([]) // 列表的数据
const queryParams = reactive({
  deviceId: -1,
  identifier: undefined as string | undefined,
  name: undefined as string | undefined
})

const queryFormRef = ref() // 搜索的表单
const activeTab = ref('status') // 默认选中的标签

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    queryParams.deviceId = props.device.id
    list.value = await DeviceApi.getLatestDeviceProperties(queryParams)
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  queryParams.identifier = undefined
  queryParams.name = undefined
  handleQuery()
}

/** 添加/修改操作 */
const detailRef = ref()
const openDetail = (deviceId: number, identifier: string) => {
  detailRef.value.open(deviceId, identifier)
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
