<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="产品" prop="productId">
        <el-select
          v-model="queryParams.productId"
          class="!w-240px"
          clearable
          placeholder="请选择产品"
        >
          <el-option
            v-for="product in products"
            :key="product.id"
            :label="product.name"
            :value="product.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="DeviceName" prop="deviceName">
        <el-input
          v-model="queryParams.deviceName"
          class="!w-240px"
          clearable
          placeholder="请输入 DeviceName"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="备注名称" prop="nickname">
        <el-input
          v-model="queryParams.nickname"
          class="!w-240px"
          clearable
          placeholder="请输入备注名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备类型" prop="deviceType">
        <el-select
          v-model="queryParams.deviceType"
          class="!w-240px"
          clearable
          placeholder="请选择设备类型"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.IOT_PRODUCT_DEVICE_TYPE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="设备状态" prop="status">
        <el-select
          v-model="queryParams.status"
          class="!w-240px"
          clearable
          placeholder="请选择设备状态"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.IOT_DEVICE_STATE)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="设备分组" prop="groupId">
        <el-select
          v-model="queryParams.groupId"
          class="!w-240px"
          clearable
          placeholder="请选择设备分组"
        >
          <el-option
            v-for="group in deviceGroups"
            :key="group.id"
            :label="group.name"
            :value="group.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item class="float-right !mr-0 !mb-0">
        <el-button-group>
          <el-button :type="viewMode === 'card' ? 'primary' : 'default'" @click="viewMode = 'card'">
            <Icon icon="ep:grid" />
          </el-button>
          <el-button :type="viewMode === 'list' ? 'primary' : 'default'" @click="viewMode = 'list'">
            <Icon icon="ep:list" />
          </el-button>
        </el-button-group>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon class="mr-5px" icon="ep:search" />
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon class="mr-5px" icon="ep:refresh" />
          重置
        </el-button>
        <el-button
          v-hasPermi="['iot:device:create']"
          plain
          type="primary"
          @click="openForm('create')"
        >
          <Icon class="mr-5px" icon="ep:plus" />
          新增
        </el-button>
        <el-button
          v-hasPermi="['iot:device:export']"
          :loading="exportLoading"
          plain
          type="success"
          @click="handleExport"
        >
          <Icon class="mr-5px" icon="ep:download" /> 导出
        </el-button>
        <el-button v-hasPermi="['iot:device:import']" plain type="warning" @click="handleImport">
          <Icon icon="ep:upload" /> 导入
        </el-button>
        <el-button
          v-hasPermi="['iot:device:update']"
          :disabled="selectedIds.length === 0"
          plain
          type="primary"
          @click="openGroupForm"
        >
          <Icon class="mr-5px" icon="ep:folder-add" /> 添加到分组
        </el-button>
        <el-button
          v-hasPermi="['iot:device:delete']"
          :disabled="selectedIds.length === 0"
          plain
          type="danger"
          @click="handleDeleteList"
        >
          <Icon class="mr-5px" icon="ep:delete" /> 批量删除
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <template v-if="viewMode === 'card'">
      <el-row :gutter="16">
        <el-col v-for="item in list" :key="item.id" :lg="6" :md="12" :sm="12" :xs="24" class="mb-4">
          <el-card
            :body-style="{ padding: '0' }"
            class="h-full transition-colors relative overflow-hidden"
          >
            <!-- 添加渐变背景层 -->
            <div
              :class="[
                item.state === DeviceStateEnum.ONLINE
                  ? 'bg-gradient-to-b from-[#eefaff] to-transparent'
                  : 'bg-gradient-to-b from-[#fff1f1] to-transparent'
              ]"
              class="absolute top-0 left-0 right-0 h-[50px] pointer-events-none"
            >
            </div>
            <div class="p-4 relative">
              <!-- 标题区域 -->
              <div class="flex items-center mb-3">
                <div class="mr-2.5 flex items-center">
                  <el-image :src="defaultIconUrl" class="w-[18px] h-[18px]" />
                </div>
                <div class="text-[16px] font-600 flex-1">{{ item.deviceName }}</div>
                <!-- 添加设备状态标签 -->
                <div class="inline-flex items-center">
                  <div
                    :class="
                      item.state === DeviceStateEnum.ONLINE
                        ? 'bg-[var(--el-color-success)]'
                        : 'bg-[var(--el-color-danger)]'
                    "
                    class="w-1 h-1 rounded-full mr-1.5"
                  >
                  </div>
                  <el-text
                    :type="item.state === DeviceStateEnum.ONLINE ? 'success' : 'danger'"
                    class="!text-xs font-bold"
                  >
                    {{ getDictLabel(DICT_TYPE.IOT_DEVICE_STATE, item.state) }}
                  </el-text>
                </div>
              </div>

              <!-- 信息区域 -->
              <div class="flex items-center text-[14px]">
                <div class="flex-1">
                  <div class="mb-2.5 last:mb-0">
                    <span class="text-[#717c8e] mr-2.5">所属产品</span>
                    <span class="text-[#0070ff]">
                      {{ products.find((p) => p.id === item.productId)?.name }}
                    </span>
                  </div>
                  <div class="mb-2.5 last:mb-0">
                    <span class="text-[#717c8e] mr-2.5">设备类型</span>
                    <dict-tag :type="DICT_TYPE.IOT_PRODUCT_DEVICE_TYPE" :value="item.deviceType" />
                  </div>
                  <div class="mb-2.5 last:mb-0">
                    <span class="text-[#717c8e] mr-2.5">DeviceKey</span>
                    <span
                      class="text-[#0b1d30] inline-block align-middle overflow-hidden text-ellipsis whitespace-nowrap max-w-[130px]"
                    >
                      {{ item.deviceKey }}
                    </span>
                  </div>
                </div>
                <div class="w-[100px] h-[100px]">
                  <el-image :src="defaultPicUrl" class="w-full h-full" />
                </div>
              </div>

              <!-- 分隔线 -->
              <el-divider class="!my-3" />

              <!-- 按钮 -->
              <div class="flex items-center px-0">
                <el-button
                  v-hasPermi="['iot:device:update']"
                  class="flex-1 !px-2 !h-[32px] text-[13px]"
                  plain
                  type="primary"
                  @click="openForm('update', item.id)"
                >
                  <Icon class="mr-1" icon="ep:edit-pen" />
                  编辑
                </el-button>
                <el-button
                  class="flex-1 !px-2 !h-[32px] !ml-[10px] text-[13px]"
                  plain
                  type="warning"
                  @click="openDetail(item.id)"
                >
                  <Icon class="mr-1" icon="ep:view" />
                  详情
                </el-button>
                <el-button
                  class="flex-1 !px-2 !h-[32px] !ml-[10px] text-[13px]"
                  plain
                  type="info"
                  @click="openModel(item.id)"
                >
                  <Icon class="mr-1" icon="ep:tickets" />
                  数据
                </el-button>
                <div class="mx-[10px] h-[20px] w-[1px] bg-[#dcdfe6]"></div>
                <el-button
                  v-hasPermi="['iot:device:delete']"
                  class="!px-2 !h-[32px] text-[13px]"
                  plain
                  type="danger"
                  @click="handleDelete(item.id)"
                >
                  <Icon icon="ep:delete" />
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <!-- 列表视图 -->
    <el-table
      v-else
      v-loading="loading"
      :data="list"
      :show-overflow-tooltip="true"
      :stripe="true"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column align="center" label="DeviceName" prop="deviceName">
        <template #default="scope">
          <el-link @click="openDetail(scope.row.id)">{{ scope.row.deviceName }}</el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" label="备注名称" prop="nickname" />
      <el-table-column align="center" label="所属产品" prop="productId">
        <template #default="scope">
          {{ products.find((p) => p.id === scope.row.productId)?.name || '-' }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="设备类型" prop="deviceType">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.IOT_PRODUCT_DEVICE_TYPE" :value="scope.row.deviceType" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="所属分组" prop="groupId">
        <template #default="scope">
          <template v-if="scope.row.groupIds?.length">
            <el-tag v-for="id in scope.row.groupIds" :key="id" class="ml-5px" size="small">
              {{ deviceGroups.find((g) => g.id === id)?.name }}
            </el-tag>
          </template>
        </template>
      </el-table-column>
      <el-table-column align="center" label="设备状态" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.IOT_DEVICE_STATE" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="最后上线时间"
        prop="onlineTime"
        width="180px"
      />
      <el-table-column align="center" label="操作" min-width="120px">
        <template #default="scope">
          <el-button
            v-hasPermi="['iot:product:query']"
            link
            type="primary"
            @click="openDetail(scope.row.id)"
          >
            查看
          </el-button>
          <el-button link type="primary" @click="openModel(scope.row.id)"> 日志 </el-button>
          <el-button
            v-hasPermi="['iot:device:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-hasPermi="['iot:device:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <DeviceForm ref="formRef" @success="getList" />
  <!-- 分组表单组件 -->
  <DeviceGroupForm ref="groupFormRef" @success="getList" />
  <!-- 导入表单组件 -->
  <DeviceImportForm ref="importFormRef" @success="getList" />
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions, getDictLabel } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { DeviceApi, DeviceVO, DeviceStateEnum } from '@/api/iot/device/device'
import DeviceForm from './DeviceForm.vue'
import { ProductApi, ProductVO } from '@/api/iot/product/product'
import { DeviceGroupApi, DeviceGroupVO } from '@/api/iot/device/group'
import download from '@/utils/download'
import DeviceGroupForm from './DeviceGroupForm.vue'
import DeviceImportForm from './DeviceImportForm.vue'

/** IoT 设备列表 */
defineOptions({ name: 'IoTDevice' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表加载中
const list = ref<DeviceVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  deviceName: undefined,
  productId: undefined,
  deviceType: undefined,
  nickname: undefined,
  status: undefined,
  groupId: undefined
})
const queryFormRef = ref() // 搜索的表单
const exportLoading = ref(false) // 导出加载状态
const products = ref<ProductVO[]>([]) // 产品列表
const deviceGroups = ref<DeviceGroupVO[]>([]) // 设备分组列表
const selectedIds = ref<number[]>([]) // 选中的设备编号数组
const viewMode = ref<'card' | 'list'>('card') // 视图模式状态
const defaultPicUrl = ref('/src/assets/imgs/iot/device.png') // 默认设备图片
const defaultIconUrl = ref('/src/assets/svgs/iot/card-fill.svg') // 默认设备图标

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await DeviceApi.getDevicePage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  selectedIds.value = [] // 清空选择
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 打开详情 */
const { push } = useRouter()
const openDetail = (id: number) => {
  push({ name: 'IoTDeviceDetail', params: { id } })
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 起删除
    await DeviceApi.deleteDevice(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 导出方法 */
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await DeviceApi.exportDeviceExcel(queryParams)
    download.excel(data, '物联网设备.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: DeviceVO[]) => {
  selectedIds.value = selection.map((item) => item.id)
}

/** 批量删除按钮操作 */
const handleDeleteList = async () => {
  try {
    await message.delConfirm()
    // 执行批量删除
    await DeviceApi.deleteDeviceList(selectedIds.value)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 添加到分组操作 */
const groupFormRef = ref()
const openGroupForm = () => {
  groupFormRef.value.open(selectedIds.value)
}

/** 打开物模型数据 */
const openModel = (id: number) => {
  push({ name: 'IoTDeviceDetail', params: { id }, query: { tab: 'model' } })
}

/** 设备导入 */
const importFormRef = ref()
const handleImport = () => {
  importFormRef.value.open()
}

/** 初始化 **/
onMounted(async () => {
  getList()

  // 获取产品列表
  products.value = await ProductApi.getSimpleProductList()
  // 获取分组列表
  deviceGroups.value = await DeviceGroupApi.getSimpleDeviceGroupList()
})
</script>
