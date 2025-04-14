<template>
  <el-drawer
    v-model="settingVisible"
    :append-to-body="true"
    :before-close="saveConfig"
    :show-close="false"
    :size="550"
  >
    <template #header>
      <div class="config-header">
        <input
          v-if="showInput"
          v-model="nodeName"
          v-mountedFocus
          :placeholder="nodeName"
          class="config-editable-input"
          type="text"
          @blur="blurEvent()"
        />
        <div v-else class="node-name">
          {{ nodeName }} <Icon :size="16" class="ml-1" icon="ep:edit-pen" @click="clickIcon()" />
        </div>
        <div class="divide-line"></div>
      </div>
    </template>
    <div>
      <el-form ref="formRef" :model="configForm" :rules="formRules" label-position="top">
        <el-form-item label="延迟时间" prop="delayType">
          <el-radio-group v-model="configForm.delayType">
            <el-radio-button
              v-for="item in DELAY_TYPE"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="configForm.delayType === DelayTypeEnum.FIXED_TIME_DURATION">
          <el-form-item prop="timeDuration">
            <el-input-number
              v-model="configForm.timeDuration"
              :min="1"
              :style="{ width: '100px' }"
              class="mr-2"
              controls-position="right"
            />
          </el-form-item>
          <el-select v-model="configForm.timeUnit" :style="{ width: '100px' }" class="mr-2">
            <el-option
              v-for="item in TIME_UNIT_TYPES"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <el-text>后进入下一节点</el-text>
        </el-form-item>
        <el-form-item v-if="configForm.delayType === DelayTypeEnum.FIXED_DATE_TIME" prop="dateTime">
          <el-date-picker
            v-model="configForm.dateTime"
            class="mr-2"
            placeholder="请选择日期和时间"
            type="datetime"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
          <el-text>后进入下一节点</el-text>
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <el-divider />
      <div>
        <el-button type="primary" @click="saveConfig">确 定</el-button>
        <el-button @click="closeDrawer">取 消</el-button>
      </div>
    </template>
  </el-drawer>
</template>
<script lang="ts" setup>
import {
  SimpleFlowNode,
  NodeType,
  TIME_UNIT_TYPES,
  TimeUnitType,
  DelayTypeEnum,
  DELAY_TYPE
} from '../consts'
import { useWatchNode, useDrawer, useNodeName } from '../node'
import { convertTimeUnit } from '../utils'
defineOptions({
  name: 'DelayTimerNodeConfig'
})
const props = defineProps({
  flowNode: {
    type: Object as () => SimpleFlowNode,
    required: true
  }
})
// 抽屉配置
const { settingVisible, closeDrawer, openDrawer } = useDrawer()
// 当前节点
const currentNode = useWatchNode(props)
// 节点名称
const { nodeName, showInput, clickIcon, blurEvent } = useNodeName(NodeType.DELAY_TIMER_NODE)
// 抄送人表单配置
const formRef = ref() // 表单 Ref
// 表单校验规则
const formRules = reactive({
  delayType: [{ required: true, message: '延迟时间不能为空', trigger: 'change' }],
  timeDuration: [{ required: true, message: '延迟时间不能为空', trigger: 'change' }],
  dateTime: [{ required: true, message: '延迟时间不能为空', trigger: 'change' }]
})
// 配置表单数据
const configForm = ref({
  delayType: DelayTypeEnum.FIXED_TIME_DURATION,
  timeDuration: 1,
  timeUnit: TimeUnitType.HOUR,
  dateTime: ''
})
// 保存配置
const saveConfig = async () => {
  if (!formRef) return false
  const valid = await formRef.value.validate()
  if (!valid) return false
  const showText = getShowText()
  if (!showText) return false
  currentNode.value.name = nodeName.value!
  currentNode.value.showText = showText
  if (configForm.value.delayType === DelayTypeEnum.FIXED_TIME_DURATION) {
    currentNode.value.delaySetting = {
      delayType: configForm.value.delayType,
      delayTime: getIsoTimeDuration()
    }
  }
  if (configForm.value.delayType === DelayTypeEnum.FIXED_DATE_TIME) {
    currentNode.value.delaySetting = {
      delayType: configForm.value.delayType,
      delayTime: configForm.value.dateTime
    }
  }
  settingVisible.value = false
  return true
}
const getShowText = (): string => {
  let showText = ''
  if (configForm.value.delayType === DelayTypeEnum.FIXED_TIME_DURATION) {
    showText = `延迟${configForm.value.timeDuration}${TIME_UNIT_TYPES.find((item) => item.value === configForm.value.timeUnit).label}`
  }
  if (configForm.value.delayType === DelayTypeEnum.FIXED_DATE_TIME) {
    showText = `延迟至${configForm.value.dateTime.replace('T', ' ')}`
  }
  return showText
}
const getIsoTimeDuration = () => {
  let strTimeDuration = 'PT'
  if (configForm.value.timeUnit === TimeUnitType.MINUTE) {
    strTimeDuration += configForm.value.timeDuration + 'M'
  }
  if (configForm.value.timeUnit === TimeUnitType.HOUR) {
    strTimeDuration += configForm.value.timeDuration + 'H'
  }
  if (configForm.value.timeUnit === TimeUnitType.DAY) {
    strTimeDuration += configForm.value.timeDuration + 'D'
  }
  return strTimeDuration
}
// 显示延迟器节点配置， 由父组件传过来
const showDelayTimerNodeConfig = (node: SimpleFlowNode) => {
  nodeName.value = node.name
  if (node.delaySetting) {
    configForm.value.delayType = node.delaySetting.delayType
    // 固定时长
    if (configForm.value.delayType === DelayTypeEnum.FIXED_TIME_DURATION) {
      const strTimeDuration = node.delaySetting.delayTime
      let parseTime = strTimeDuration.slice(2, strTimeDuration.length - 1)
      let parseTimeUnit = strTimeDuration.slice(strTimeDuration.length - 1)
      configForm.value.timeDuration = parseInt(parseTime)
      configForm.value.timeUnit = convertTimeUnit(parseTimeUnit)
    }
    // 固定日期时间
    if (configForm.value.delayType === DelayTypeEnum.FIXED_DATE_TIME) {
      configForm.value.dateTime = node.delaySetting.delayTime
    }
  }
}

defineExpose({ openDrawer, showDelayTimerNodeConfig }) // 暴露方法给父组件
</script>

<style lang="scss" scoped></style>
