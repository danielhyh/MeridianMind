<template>
  <el-form ref="formRef" :model="modelData" class="mt-20px" label-width="120px">
    <el-form-item class="mb-20px">
      <template #label>
        <el-text size="large" tag="b">提交人权限</el-text>
      </template>
      <div class="flex flex-col">
        <el-checkbox v-model="modelData.allowCancelRunningProcess" label="允许撤销审批中的申请" />
        <div class="ml-22px">
          <el-text type="info"> 第一个审批节点通过后，提交人仍可撤销申请 </el-text>
        </div>
      </div>
    </el-form-item>
    <el-form-item v-if="modelData.processIdRule" class="mb-20px">
      <template #label>
        <el-text size="large" tag="b">流程编码</el-text>
      </template>
      <div class="flex flex-col">
        <div>
          <el-input
            v-model="modelData.processIdRule.prefix"
            :disabled="!modelData.processIdRule.enable"
            class="w-130px!"
            placeholder="前缀"
          >
            <template #prepend>
              <el-checkbox v-model="modelData.processIdRule.enable" />
            </template>
          </el-input>
          <el-select
            v-model="modelData.processIdRule.infix"
            :disabled="!modelData.processIdRule.enable"
            class="w-130px! ml-5px"
            placeholder="中缀"
          >
            <el-option
              v-for="item in timeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <el-input
            v-model="modelData.processIdRule.postfix"
            :disabled="!modelData.processIdRule.enable"
            class="w-80px! ml-5px"
            placeholder="后缀"
          />
          <el-input-number
            v-model="modelData.processIdRule.length"
            :disabled="!modelData.processIdRule.enable"
            :min="5"
            class="w-120px! ml-5px"
          />
        </div>
        <div v-if="modelData.processIdRule.enable" class="ml-22px">
          <el-text type="info"> 编码示例：{{ numberExample }} </el-text>
        </div>
      </div>
    </el-form-item>
    <el-form-item class="mb-20px">
      <template #label>
        <el-text size="large" tag="b">自动去重</el-text>
      </template>
      <div class="flex flex-col">
        <div>
          <el-text> 同一审批人在流程中重复出现时： </el-text>
        </div>
        <el-radio-group v-model="modelData.autoApprovalType">
          <div class="flex flex-col">
            <el-radio :value="0">不自动通过</el-radio>
            <el-radio :value="1">仅审批一次，后续重复的审批节点均自动通过</el-radio>
            <el-radio :value="2">仅针对连续审批的节点自动通过</el-radio>
          </div>
        </el-radio-group>
      </div>
    </el-form-item>
    <el-form-item v-if="modelData.titleSetting" class="mb-20px">
      <template #label>
        <el-text size="large" tag="b">标题设置</el-text>
      </template>
      <div class="flex flex-col">
        <el-radio-group v-model="modelData.titleSetting.enable">
          <div class="flex flex-col">
            <el-radio :value="false"
              >系统默认 <el-text type="info"> 展示流程名称 </el-text></el-radio
            >
            <el-radio :value="true">
              自定义标题
              <el-text>
                <el-tooltip content="输入字符 '{' 即可插入表单字段" effect="light" placement="top">
                  <Icon class="ml-5px" icon="ep:question-filled" />
                </el-tooltip>
              </el-text>
            </el-radio>
          </div>
        </el-radio-group>
        <el-mention
          v-if="modelData.titleSetting.enable"
          v-model="modelData.titleSetting.title"
          :options="formFieldOptions4Title"
          class="w-600px!"
          placeholder="请插入表单字段（输入 '{' 可以选择表单字段）或输入文本"
          prefix="{"
          split="}"
          type="textarea"
          whole
        />
      </div>
    </el-form-item>
    <el-form-item
      v-if="modelData.summarySetting && modelData.formType === BpmModelFormType.NORMAL"
      class="mb-20px"
    >
      <template #label>
        <el-text size="large" tag="b">摘要设置</el-text>
      </template>
      <div class="flex flex-col">
        <el-radio-group v-model="modelData.summarySetting.enable">
          <div class="flex flex-col">
            <el-radio :value="false">
              系统默认 <el-text type="info"> 展示表单前 3 个字段 </el-text>
            </el-radio>
            <el-radio :value="true"> 自定义摘要 </el-radio>
          </div>
        </el-radio-group>
        <el-select
          v-if="modelData.summarySetting.enable"
          v-model="modelData.summarySetting.summary"
          class="w-500px!"
          multiple
          placeholder="请选择要展示的表单字段"
        >
          <el-option
            v-for="item in formFieldOptions4Summary"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </div>
    </el-form-item>
    <el-form-item class="mb-20px">
      <template #label>
        <el-text size="large" tag="b">流程前置通知</el-text>
      </template>
      <div class="flex flex-col w-100%">
        <div class="flex">
          <el-switch
            v-model="processBeforeTriggerEnable"
            @change="handlePreProcessNotifyEnableChange"
          />
          <div class="ml-80px">流程启动后通知</div>
        </div>
        <HttpRequestSetting
          v-if="processBeforeTriggerEnable"
          v-model:setting="modelData.processBeforeTriggerSetting"
          :formItemPrefix="'processBeforeTriggerSetting'"
          :responseEnable="true"
        />
      </div>
    </el-form-item>
    <el-form-item class="mb-20px">
      <template #label>
        <el-text size="large" tag="b">流程后置通知</el-text>
      </template>
      <div class="flex flex-col w-100%">
        <div class="flex">
          <el-switch
            v-model="processAfterTriggerEnable"
            @change="handlePostProcessNotifyEnableChange"
          />
          <div class="ml-80px">流程启动后通知</div>
        </div>
        <HttpRequestSetting
          v-if="processAfterTriggerEnable"
          v-model:setting="modelData.processAfterTriggerSetting"
          :formItemPrefix="'processAfterTriggerSetting'"
          :responseEnable="true"
        />
      </div>
    </el-form-item>
  </el-form>
</template>

<script lang="ts" setup>
import dayjs from 'dayjs'
import { BpmAutoApproveType, BpmModelFormType } from '@/utils/constants'
import * as FormApi from '@/api/bpm/form'
import { parseFormFields } from '@/components/FormCreate/src/utils'
import { ProcessVariableEnum } from '@/components/SimpleProcessDesignerV2/src/consts'
import HttpRequestSetting from '@/components/SimpleProcessDesignerV2/src/nodes-config/components/HttpRequestSetting.vue'

const modelData = defineModel<any>()

/** 自定义 ID 流程编码 */
const timeOptions = ref([
  {
    value: '',
    label: '无'
  },
  {
    value: 'DAY',
    label: '精确到日'
  },
  {
    value: 'HOUR',
    label: '精确到时'
  },
  {
    value: 'MINUTE',
    label: '精确到分'
  },
  {
    value: 'SECOND',
    label: '精确到秒'
  }
])
const numberExample = computed(() => {
  if (modelData.value.processIdRule.enable) {
    let infix = ''
    switch (modelData.value.processIdRule.infix) {
      case 'DAY':
        infix = dayjs().format('YYYYMMDD')
        break
      case 'HOUR':
        infix = dayjs().format('YYYYMMDDHH')
        break
      case 'MINUTE':
        infix = dayjs().format('YYYYMMDDHHmm')
        break
      case 'SECOND':
        infix = dayjs().format('YYYYMMDDHHmmss')
        break
      default:
        break
    }
    return (
      modelData.value.processIdRule.prefix +
      infix +
      modelData.value.processIdRule.postfix +
      '1'.padStart(modelData.value.processIdRule.length - 1, '0')
    )
  } else {
    return ''
  }
})

/** 是否开启流程前置通知 */
const processBeforeTriggerEnable = ref(false)
const handlePreProcessNotifyEnableChange = (val: boolean | string | number) => {
  if (val) {
    modelData.value.processBeforeTriggerSetting = {
      url: '',
      header: [],
      body: [],
      response: []
    }
  } else {
    modelData.value.processBeforeTriggerSetting = null
  }
}

/** 是否开启流程后置通知 */
const processAfterTriggerEnable = ref(false)
const handlePostProcessNotifyEnableChange = (val: boolean | string | number) => {
  if (val) {
    modelData.value.processAfterTriggerSetting = {
      url: '',
      header: [],
      body: [],
      response: []
    }
  } else {
    modelData.value.processAfterTriggerSetting = null
  }
}

/** 表单选项 */
const formField = ref<Array<{ field: string; title: string }>>([])
const formFieldOptions4Title = computed(() => {
  let cloneFormField = formField.value.map((item) => {
    return {
      label: item.title,
      value: item.field
    }
  })
  // 固定添加发起人 ID 字段
  cloneFormField.unshift({
    label: '流程名称',
    value: ProcessVariableEnum.PROCESS_DEFINITION_NAME
  })
  cloneFormField.unshift({
    label: '发起时间',
    value: ProcessVariableEnum.START_TIME
  })
  cloneFormField.unshift({
    label: '发起人',
    value: ProcessVariableEnum.START_USER_ID
  })
  return cloneFormField
})
const formFieldOptions4Summary = computed(() => {
  return formField.value.map((item) => {
    return {
      label: item.title,
      value: item.field
    }
  })
})

/** 兼容以前未配置更多设置的流程 */
const initData = () => {
  if (!modelData.value.processIdRule) {
    modelData.value.processIdRule = {
      enable: false,
      prefix: '',
      infix: '',
      postfix: '',
      length: 5
    }
  }
  if (!modelData.value.autoApprovalType) {
    modelData.value.autoApprovalType = BpmAutoApproveType.NONE
  }
  if (!modelData.value.titleSetting) {
    modelData.value.titleSetting = {
      enable: false,
      title: ''
    }
  }
  if (!modelData.value.summarySetting) {
    modelData.value.summarySetting = {
      enable: false,
      summary: []
    }
  }
  if (modelData.value.processBeforeTriggerSetting) {
    processBeforeTriggerEnable.value = true
  }
  if (modelData.value.processAfterTriggerSetting) {
    processAfterTriggerEnable.value = true
  }
}
defineExpose({ initData })

/** 监听表单 ID 变化，加载表单数据 */
watch(
  () => modelData.value.formId,
  async (newFormId) => {
    if (newFormId && modelData.value.formType === BpmModelFormType.NORMAL) {
      const data = await FormApi.getForm(newFormId)
      const result: Array<{ field: string; title: string }> = []
      if (data.fields) {
        data.fields.forEach((fieldStr: string) => {
          parseFormFields(JSON.parse(fieldStr), result)
        })
      }
      formField.value = result
    } else {
      formField.value = []
    }
  },
  { immediate: true }
)
</script>
