<template>
  <div class="panel-tab__content">
    <el-table :data="elementPropertyList" border fit max-height="240">
      <el-table-column label="序号" type="index" width="50px" />
      <el-table-column label="属性名" min-width="100px" prop="name" show-overflow-tooltip />
      <el-table-column label="属性值" min-width="100px" prop="value" show-overflow-tooltip />
      <el-table-column label="操作" width="110px">
        <template #default="scope">
          <el-button link size="small" @click="openAttributesForm(scope.row, scope.$index)">
            编辑
          </el-button>
          <el-divider direction="vertical" />
          <el-button
            link
            size="small"
            style="color: #ff4d4f"
            @click="removeAttributes(scope.row, scope.$index)"
          >
            移除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="element-drawer__button">
      <XButton
        preIcon="ep:plus"
        title="添加属性"
        type="primary"
        @click="openAttributesForm(null, -1)"
      />
    </div>

    <el-dialog
      v-model="propertyFormModelVisible"
      append-to-body
      destroy-on-close
      title="属性配置"
      width="600px"
    >
      <el-form ref="attributeFormRef" :model="propertyForm" label-width="80px">
        <el-form-item label="属性名：" prop="name">
          <el-input v-model="propertyForm.name" clearable />
        </el-form-item>
        <el-form-item label="属性值：" prop="value">
          <el-input v-model="propertyForm.value" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="propertyFormModelVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveAttribute">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ElMessageBox } from 'element-plus'
defineOptions({ name: 'ElementProperties' })
const props = defineProps({
  id: String,
  type: String
})
const prefix = inject('prefix')
// const width = inject('width')

const elementPropertyList = ref<any[]>([])
const propertyForm = ref<any>({})
const editingPropertyIndex = ref(-1)
const propertyFormModelVisible = ref(false)
const bpmnElement = ref()
const otherExtensionList = ref()
const bpmnElementProperties = ref()
const bpmnElementPropertyList = ref()
const attributeFormRef = ref()
const bpmnInstances = () => (window as any)?.bpmnInstances

const resetAttributesList = () => {
  bpmnElement.value = bpmnInstances().bpmnElement
  otherExtensionList.value = [] // 其他扩展配置
  bpmnElementProperties.value =
    // bpmnElement.value.businessObject?.extensionElements?.filter((ex) => {
    bpmnElement.value.businessObject?.extensionElements?.values?.filter((ex) => {
      if (ex.$type !== `${prefix}:Properties`) {
        otherExtensionList.value.push(ex)
      }
      return ex.$type === `${prefix}:Properties`
    }) ?? [];

  // 保存所有的 扩展属性字段
  bpmnElementPropertyList.value = bpmnElementProperties.value.reduce(
    (pre, current) => pre.concat(current.values),
    []
  )
  // 复制 显示
  elementPropertyList.value = JSON.parse(JSON.stringify(bpmnElementPropertyList.value ?? []))
}
const openAttributesForm = (attr, index) => {
  editingPropertyIndex.value = index
  propertyForm.value = index === -1 ? {} : JSON.parse(JSON.stringify(attr))
  propertyFormModelVisible.value = true
  nextTick(() => {
    if (attributeFormRef.value) attributeFormRef.value.clearValidate()
  })
}
const removeAttributes = (attr, index) => {
  console.log(attr, 'attr')
  ElMessageBox.confirm('确认移除该属性吗？', '提示', {
    confirmButtonText: '确 认',
    cancelButtonText: '取 消'
  })
    .then(() => {
      elementPropertyList.value.splice(index, 1)
      bpmnElementPropertyList.value.splice(index, 1)
      // 新建一个属性字段的保存列表
      const propertiesObject = bpmnInstances().moddle.create(`${prefix}:Properties`, {
        values: bpmnElementPropertyList.value
      })
      updateElementExtensions(propertiesObject)
      resetAttributesList()
    })
    .catch(() => console.info('操作取消'))
}
const saveAttribute = () => {
  console.log(propertyForm.value, 'propertyForm.value')
  const { name, value } = propertyForm.value
  if (editingPropertyIndex.value !== -1) {
    bpmnInstances().modeling.updateModdleProperties(
      toRaw(bpmnElement.value),
      toRaw(bpmnElementPropertyList.value)[toRaw(editingPropertyIndex.value)],
      {
        name,
        value
      }
    )
  } else {
    // 新建属性字段
    const newPropertyObject = bpmnInstances().moddle.create(`${prefix}:Property`, {
      name,
      value
    })
    // 新建一个属性字段的保存列表
    const propertiesObject = bpmnInstances().moddle.create(`${prefix}:Properties`, {
      values: bpmnElementPropertyList.value.concat([newPropertyObject])
    })
    updateElementExtensions(propertiesObject)
  }
  propertyFormModelVisible.value = false
  resetAttributesList()
}
const updateElementExtensions = (properties) => {
  const extensions = bpmnInstances().moddle.create('bpmn:ExtensionElements', {
    values: otherExtensionList.value.concat([properties])
  })
  bpmnInstances().modeling.updateProperties(toRaw(bpmnElement.value), {
    extensionElements: extensions
  })
}

watch(
  () => props.id,
  (val) => {
    if (val) {
      val && val.length && resetAttributesList()
    }
  },
  { immediate: true }
)
</script>
