<template>
  <el-form ref="listenerFormRef" :model="configForm" label-position="top">
    <div v-for="(listener, listenerIdx) in taskListener" :key="listenerIdx">
      <el-divider content-position="left">
        <el-text size="large" tag="b">{{ listener.name }}</el-text>
      </el-divider>
      <el-form-item>
        <el-switch
          v-model="configForm[`task${listener.type}ListenerEnable`]"
          active-text="开启"
          inactive-text="关闭"
        />
      </el-form-item>
      <div v-if="configForm[`task${listener.type}ListenerEnable`]">
        <el-form-item>
          <el-alert
            :closable="false"
            show-icon
            title="仅支持 POST 请求，以请求体方式接收参数"
            type="warning"
          />
        </el-form-item>
        <el-form-item
          :prop="`task${listener.type}ListenerPath`"
          :rules="{
            required: true,
            message: '请求地址不能为空',
            trigger: 'blur'
          }"
          label="请求地址"
        >
          <el-input v-model="configForm[`task${listener.type}ListenerPath`]" />
        </el-form-item>
        <HttpRequestParamSetting
          :bind="`task${listener.type}Listener`"
          :body="configForm[`task${listener.type}Listener`].body"
          :header="configForm[`task${listener.type}Listener`].header"
        />
      </div>
    </div>
  </el-form>
</template>

<script lang="ts" setup>
import HttpRequestParamSetting from './HttpRequestParamSetting.vue'

const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  },
  formFieldOptions: {
    type: Object,
    required: true
  }
})
const emit = defineEmits(['update:modelValue'])
const listenerFormRef = ref()
const configForm = computed({
  get() {
    return props.modelValue
  },
  set(newValue) {
    emit('update:modelValue', newValue)
  }
})
const taskListener = ref([
  {
    name: '创建任务',
    type: 'Create'
  },
  {
    name: '指派任务执行人员',
    type: 'Assign'
  },
  {
    name: '完成任务',
    type: 'Complete'
  }
])

const validate = async () => {
  if (!listenerFormRef) return false
  return await listenerFormRef.value.validate()
}

defineExpose({ validate })
</script>
