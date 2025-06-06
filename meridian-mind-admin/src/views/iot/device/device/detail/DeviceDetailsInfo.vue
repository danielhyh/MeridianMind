<!-- 设备信息 -->
<template>
  <ContentWrap>
    <el-descriptions :column="3" title="设备信息">
      <el-descriptions-item label="产品名称">{{ product.name }}</el-descriptions-item>
      <el-descriptions-item label="ProductKey">
        {{ product.productKey }}
        <el-button @click="copyToClipboard(product.productKey)">复制</el-button>
      </el-descriptions-item>
      <el-descriptions-item label="设备类型">
        <dict-tag :type="DICT_TYPE.IOT_PRODUCT_DEVICE_TYPE" :value="product.deviceType" />
      </el-descriptions-item>
      <el-descriptions-item label="DeviceName">
        {{ device.deviceName }}
        <el-button @click="copyToClipboard(device.deviceName)">复制</el-button>
      </el-descriptions-item>
      <el-descriptions-item label="备注名称">{{ device.nickname }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ formatDate(device.createTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="当前状态">
        <dict-tag :type="DICT_TYPE.IOT_DEVICE_STATE" :value="device.state" />
      </el-descriptions-item>
      <el-descriptions-item label="激活时间">
        {{ formatDate(device.activeTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="最后上线时间">
        {{ formatDate(device.onlineTime) }}
      </el-descriptions-item>
      <el-descriptions-item :span="3" label="最后离线时间">
        {{ formatDate(device.offlineTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="MQTT 连接参数">
        <el-button type="primary" @click="openMqttParams">查看</el-button>
      </el-descriptions-item>
    </el-descriptions>
  </ContentWrap>

  <!-- MQTT 连接参数弹框 -->
  <Dialog
    v-model="mqttDialogVisible"
    :before-close="handleCloseMqttDialog"
    title="MQTT 连接参数"
    width="50%"
  >
    <el-form :model="mqttParams" label-width="120px">
      <el-form-item label="clientId">
        <el-input v-model="mqttParams.mqttClientId" readonly>
          <template #append>
            <el-button type="primary" @click="copyToClipboard(mqttParams.mqttClientId)">
              <Icon icon="ph:copy" />
            </el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="username">
        <el-input v-model="mqttParams.mqttUsername" readonly>
          <template #append>
            <el-button type="primary" @click="copyToClipboard(mqttParams.mqttUsername)">
              <Icon icon="ph:copy" />
            </el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="passwd">
        <el-input
          v-model="mqttParams.mqttPassword"
          :type="passwordVisible ? 'text' : 'password'"
          readonly
        >
          <template #append>
            <el-button type="primary" @click="passwordVisible = !passwordVisible">
              <Icon :icon="passwordVisible ? 'ph:eye-slash' : 'ph:eye'" />
            </el-button>
            <el-button type="primary" @click="copyToClipboard(mqttParams.mqttPassword)">
              <Icon icon="ph:copy" />
            </el-button>
          </template>
        </el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="mqttDialogVisible = false">关闭</el-button>
    </template>
  </Dialog>

  <!-- TODO 待开发：设备标签 -->
  <!-- TODO 待开发：设备地图 -->
</template>
<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { ProductVO } from '@/api/iot/product/product'
import { formatDate } from '@/utils/formatTime'
import { DeviceVO } from '@/api/iot/device/device'
import { DeviceApi, MqttConnectionParamsVO } from '@/api/iot/device/device/index'

const message = useMessage() // 消息提示

const { product, device } = defineProps<{ product: ProductVO; device: DeviceVO }>() // 定义 Props
const emit = defineEmits(['refresh']) // 定义 Emits

const mqttDialogVisible = ref(false) // 定义 MQTT 弹框的可见性
const passwordVisible = ref(false) // 定义密码可见性状态
const mqttParams = ref({
  mqttClientId: '',
  mqttUsername: '',
  mqttPassword: ''
}) // 定义 MQTT 参数对象

/** 复制到剪贴板方法 */
const copyToClipboard = async (text: string) => {
  try {
    await navigator.clipboard.writeText(text)
    message.success('复制成功')
  } catch (error) {
    message.error('复制失败')
  }
}

/** 打开 MQTT 参数弹框的方法 */
const openMqttParams = async () => {
  try {
    const data = await DeviceApi.getMqttConnectionParams(device.id)
    // 根据 API 响应结构正确获取数据
    // TODO @haohao：'N/A' 是不是在 ui 里处理哈
    mqttParams.value = {
      mqttClientId: data.mqttClientId || 'N/A',
      mqttUsername: data.mqttUsername || 'N/A',
      mqttPassword: data.mqttPassword || 'N/A'
    }

    // 显示 MQTT 弹框
    mqttDialogVisible.value = true
  } catch (error) {
    console.error('获取 MQTT 连接参数出错：', error)
    message.error('获取MQTT连接参数失败，请检查网络连接或联系管理员')
  }
}

/** 关闭 MQTT 弹框的方法 */
const handleCloseMqttDialog = () => {
  mqttDialogVisible.value = false
}
</script>
