<!-- 设备物模型 -> 运行状态 -> 查看数据（设备的属性值历史）-->
<template>
  <Dialog v-model="dialogVisible" title="查看数据">
    <ContentWrap>
      <!-- 搜索工作栏 -->
      <el-form
        ref="queryFormRef"
        :inline="true"
        :model="queryParams"
        class="-mb-15px"
        label-width="68px"
      >
        <el-form-item label="时间" prop="createTime">
          <el-date-picker
            v-model="queryParams.times"
            class="!w-350px"
            end-placeholder="结束日期"
            start-placeholder="开始日期"
            type="datetimerange"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item>
          <el-button @click="handleQuery">
            <Icon class="mr-5px" icon="ep:search" />
            搜索
          </el-button>
        </el-form-item>
      </el-form>
    </ContentWrap>

    <!-- TODO @haohao：可参考阿里云 IoT，改成“图标”、“表格”两个选项 -->
    <!-- 列表 -->
    <ContentWrap>
      <el-table v-loading="detailLoading" :data="list" :show-overflow-tooltip="true" :stripe="true">
        <el-table-column
          :formatter="dateFormatter"
          align="center"
          label="时间"
          prop="updateTime"
          width="180px"
        />
        <el-table-column align="center" label="属性值" prop="value" />
      </el-table>
      <!-- 分页 -->
      <Pagination
        v-model:limit="queryParams.pageSize"
        v-model:page="queryParams.pageNo"
        :total="total"
        @pagination="getList"
      />
    </ContentWrap>
  </Dialog>
</template>
<script lang="ts" setup>
import { DeviceApi, DeviceHistoryDataVO, DeviceVO } from '@/api/iot/device/device'
import { ProductVO } from '@/api/iot/product/product'
import { beginOfDay, dateFormatter, endOfDay, formatDate } from '@/utils/formatTime'

defineProps<{ product: ProductVO; device: DeviceVO }>()

/** IoT 设备数据详情 */
defineOptions({ name: 'IoTDeviceDataDetail' })

const dialogVisible = ref(false) // 弹窗的是否展示
const detailLoading = ref(false)

const list = ref<DeviceHistoryDataVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  deviceId: -1,
  identifier: '',
  times: [
    // 默认显示最近一周的数据
    formatDate(beginOfDay(new Date(new Date().getTime() - 3600 * 1000 * 24 * 7))),
    formatDate(endOfDay(new Date()))
  ]
})
const queryFormRef = ref() // 搜索的表单

/** 获得设备历史数据 */
const getList = async () => {
  detailLoading.value = true
  try {
    const data = await DeviceApi.getHistoryDevicePropertyPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    detailLoading.value = false
  }
}

/** 打开弹窗 */
const open = (deviceId: number, identifier: string) => {
  dialogVisible.value = true
  queryParams.deviceId = deviceId
  queryParams.identifier = identifier
  getList()
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

defineExpose({ open }) // 提供 open 方法，用于打开弹窗
</script>
