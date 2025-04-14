<template>
  <div>
    <el-form-item key="executeType" label="执行类型">
      <el-select v-model="serviceTaskForm.executeType">
        <el-option label="Java类" value="class" />
        <el-option label="表达式" value="expression" />
        <el-option label="代理表达式" value="delegateExpression" />
      </el-select>
    </el-form-item>
    <el-form-item
      v-if="serviceTaskForm.executeType === 'class'"
      key="execute-class"
      label="Java类"
      prop="class"
    >
      <el-input v-model="serviceTaskForm.class" clearable @change="updateElementTask" />
    </el-form-item>
    <el-form-item
      v-if="serviceTaskForm.executeType === 'expression'"
      key="execute-expression"
      label="表达式"
      prop="expression"
    >
      <el-input v-model="serviceTaskForm.expression" clearable @change="updateElementTask" />
    </el-form-item>
    <el-form-item
      v-if="serviceTaskForm.executeType === 'delegateExpression'"
      key="execute-delegate"
      label="代理表达式"
      prop="delegateExpression"
    >
      <el-input v-model="serviceTaskForm.delegateExpression" clearable @change="updateElementTask" />
    </el-form-item>
  </div>
</template>

<script lang="ts" setup>
defineOptions({ name: 'ServiceTask' })
const props = defineProps({
  id: String,
  type: String
})

const defaultTaskForm = ref({
  executeType: '',
  class: '',
  expression: '',
  delegateExpression: ''
})

const serviceTaskForm = ref<any>({})
const bpmnElement = ref()

const bpmnInstances = () => (window as any)?.bpmnInstances

const resetTaskForm = () => {
  for (let key in defaultTaskForm.value) {
    let value = bpmnElement.value?.businessObject[key] || defaultTaskForm.value[key]
    serviceTaskForm.value[key] = value
    if (value) {
      serviceTaskForm.value.executeType = key
    }
  }
}

const updateElementTask = () => {
  let taskAttr = Object.create(null);
  const type = serviceTaskForm.value.executeType;
  for (let key in serviceTaskForm.value) {
    if (key !== 'executeType' && key !== type) taskAttr[key] = null;
  }
  taskAttr[type] = serviceTaskForm.value[type] || "";
  bpmnInstances().modeling.updateProperties(toRaw(bpmnElement.value), taskAttr)
}

onBeforeUnmount(() => {
  bpmnElement.value = null
})

watch(
  () => props.id,
  () => {
    bpmnElement.value = bpmnInstances().bpmnElement
    nextTick(() => {
      resetTaskForm()
    })
  },
  { immediate: true }
)

</script>
