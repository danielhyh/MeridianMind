<template>
  <div class="panel-tab__content">
    <el-table :data="elementListenersList" border size="small">
      <el-table-column label="序号" type="index" width="50px" />
      <el-table-column label="事件类型" min-width="100px" prop="event" />
      <el-table-column
        :formatter="(row) => listenerTypeObject[row.listenerType]"
        label="监听器类型"
        min-width="100px"
        show-overflow-tooltip
      />
      <el-table-column label="操作" width="100px">
        <template #default="scope">
          <el-button link size="small" @click="openListenerForm(scope.row, scope.$index)"
            >编辑</el-button
          >
          <el-divider direction="vertical" />
          <el-button link size="small" style="color: #ff4d4f" @click="removeListener(scope.$index)"
            >移除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <div class="element-drawer__button">
      <XButton
        preIcon="ep:plus"
        size="small"
        title="添加监听器"
        type="primary"
        @click="openListenerForm(null)"
      />
      <XButton
        preIcon="ep:select"
        size="small"
        title="选择监听器"
        type="success"
        @click="openProcessListenerDialog"
      />
    </div>

    <!-- 监听器 编辑/创建 部分 -->
    <el-drawer
      v-model="listenerFormModelVisible"
      :size="`${width}px`"
      append-to-body
      destroy-on-close
      title="执行监听器"
    >
      <el-form ref="listenerFormRef" :model="listenerForm" label-width="96px">
        <el-form-item
          :rules="{ required: true, trigger: ['blur', 'change'] }"
          label="事件类型"
          prop="event"
        >
          <el-select v-model="listenerForm.event">
            <el-option label="start" value="start" />
            <el-option label="end" value="end" />
          </el-select>
        </el-form-item>
        <el-form-item
          :rules="{ required: true, trigger: ['blur', 'change'] }"
          label="监听器类型"
          prop="listenerType"
        >
          <el-select v-model="listenerForm.listenerType">
            <el-option
              v-for="i in Object.keys(listenerTypeObject)"
              :key="i"
              :label="listenerTypeObject[i]"
              :value="i"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          v-if="listenerForm.listenerType === 'classListener'"
          key="listener-class"
          :rules="{ required: true, trigger: ['blur', 'change'] }"
          label="Java类"
          prop="class"
        >
          <el-input v-model="listenerForm.class" clearable />
        </el-form-item>
        <el-form-item
          v-if="listenerForm.listenerType === 'expressionListener'"
          key="listener-expression"
          :rules="{ required: true, trigger: ['blur', 'change'] }"
          label="表达式"
          prop="expression"
        >
          <el-input v-model="listenerForm.expression" clearable />
        </el-form-item>
        <el-form-item
          v-if="listenerForm.listenerType === 'delegateExpressionListener'"
          key="listener-delegate"
          :rules="{ required: true, trigger: ['blur', 'change'] }"
          label="代理表达式"
          prop="delegateExpression"
        >
          <el-input v-model="listenerForm.delegateExpression" clearable />
        </el-form-item>
        <template v-if="listenerForm.listenerType === 'scriptListener'">
          <el-form-item
            key="listener-script-format"
            :rules="{ required: true, trigger: ['blur', 'change'], message: '请填写脚本格式' }"
            label="脚本格式"
            prop="scriptFormat"
          >
            <el-input v-model="listenerForm.scriptFormat" clearable />
          </el-form-item>
          <el-form-item
            key="listener-script-type"
            :rules="{ required: true, trigger: ['blur', 'change'], message: '请选择脚本类型' }"
            label="脚本类型"
            prop="scriptType"
          >
            <el-select v-model="listenerForm.scriptType">
              <el-option label="内联脚本" value="inlineScript" />
              <el-option label="外部脚本" value="externalScript" />
            </el-select>
          </el-form-item>
          <el-form-item
            v-if="listenerForm.scriptType === 'inlineScript'"
            key="listener-script"
            :rules="{ required: true, trigger: ['blur', 'change'], message: '请填写脚本内容' }"
            label="脚本内容"
            prop="value"
          >
            <el-input v-model="listenerForm.value" clearable />
          </el-form-item>
          <el-form-item
            v-if="listenerForm.scriptType === 'externalScript'"
            key="listener-resource"
            :rules="{ required: true, trigger: ['blur', 'change'], message: '请填写资源地址' }"
            label="资源地址"
            prop="resource"
          >
            <el-input v-model="listenerForm.resource" clearable />
          </el-form-item>
        </template>
      </el-form>
      <el-divider />
      <p class="listener-filed__title">
        <span><Icon icon="ep:menu" />注入字段：</span>
        <XButton title="添加字段" type="primary" @click="openListenerFieldForm(null)" />
      </p>
      <el-table
        :data="fieldsListOfListener"
        border
        fit
        max-height="240"
        size="small"
        style="flex: none"
      >
        <el-table-column label="序号" type="index" width="50px" />
        <el-table-column label="字段名称" min-width="100px" prop="name" />
        <el-table-column
          :formatter="(row) => fieldTypeObject[row.fieldType]"
          label="字段类型"
          min-width="80px"
          show-overflow-tooltip
        />
        <el-table-column
          :formatter="(row) => row.string || row.expression"
          label="字段值/表达式"
          min-width="100px"
          show-overflow-tooltip
        />
        <el-table-column label="操作" width="130px">
          <template #default="scope">
            <el-button link size="small" @click="openListenerFieldForm(scope.row, scope.$index)"
              >编辑</el-button
            >
            <el-divider direction="vertical" />
            <el-button
              link
              size="small"
              style="color: #ff4d4f"
              @click="removeListenerField(scope.$index)"
              >移除</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <div class="element-drawer__button">
        <el-button @click="listenerFormModelVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveListenerConfig">保 存</el-button>
      </div>
    </el-drawer>

    <!-- 注入西段 编辑/创建 部分 -->
    <el-dialog
      v-model="listenerFieldFormModelVisible"
      append-to-body
      destroy-on-close
      title="字段配置"
      width="600px"
    >
      <el-form
        ref="listenerFieldFormRef"
        :model="listenerFieldForm"
        label-width="96spx"
        style="height: 136px"
      >
        <el-form-item
          :rules="{ required: true, trigger: ['blur', 'change'] }"
          label="字段名称："
          prop="name"
        >
          <el-input v-model="listenerFieldForm.name" clearable />
        </el-form-item>
        <el-form-item
          :rules="{ required: true, trigger: ['blur', 'change'] }"
          label="字段类型："
          prop="fieldType"
        >
          <el-select v-model="listenerFieldForm.fieldType">
            <el-option
              v-for="i in Object.keys(fieldTypeObject)"
              :key="i"
              :label="fieldTypeObject[i]"
              :value="i"
            />
          </el-select>
        </el-form-item>
        <el-form-item
          v-if="listenerFieldForm.fieldType === 'string'"
          key="field-string"
          :rules="{ required: true, trigger: ['blur', 'change'] }"
          label="字段值："
          prop="string"
        >
          <el-input v-model="listenerFieldForm.string" clearable />
        </el-form-item>
        <el-form-item
          v-if="listenerFieldForm.fieldType === 'expression'"
          key="field-expression"
          :rules="{ required: true, trigger: ['blur', 'change'] }"
          label="表达式："
          prop="expression"
        >
          <el-input v-model="listenerFieldForm.expression" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button size="small" @click="listenerFieldFormModelVisible = false">取 消</el-button>
        <el-button size="small" type="primary" @click="saveListenerFiled">确 定</el-button>
      </template>
    </el-dialog>
  </div>

  <!-- 选择弹窗 -->
  <ProcessListenerDialog ref="processListenerDialogRef" @select="selectProcessListener" />
</template>
<script lang="ts" setup>
import { ElMessageBox } from 'element-plus'
import { createListenerObject, updateElementExtensions } from '../../utils'
import {
  initListenerType,
  initListenerForm,
  listenerType,
  fieldType,
  initListenerForm2
} from './utilSelf'
import ProcessListenerDialog from './ProcessListenerDialog.vue'

defineOptions({ name: 'ElementListeners' })

const props = defineProps({
  id: String,
  type: String
})
const prefix = inject('prefix')
const width = inject('width')
const elementListenersList = ref<any[]>([]) // 监听器列表
const listenerForm = ref<any>({}) // 监听器详情表单
const listenerFormModelVisible = ref(false) // 监听器 编辑 侧边栏显示状态
const fieldsListOfListener = ref<any[]>([])
const listenerFieldForm = ref<any>({}) // 监听器 注入字段 详情表单
const listenerFieldFormModelVisible = ref(false) // 监听器 注入字段表单弹窗 显示状态
const editingListenerIndex = ref(-1) // 监听器所在下标，-1 为新增
const editingListenerFieldIndex = ref(-1) // 字段所在下标，-1 为新增
const listenerTypeObject = ref(listenerType)
const fieldTypeObject = ref(fieldType)
const bpmnElement = ref()
const otherExtensionList = ref()
const bpmnElementListeners = ref()
const listenerFormRef = ref()
const listenerFieldFormRef = ref()
const bpmnInstances = () => (window as any)?.bpmnInstances

const resetListenersList = () => {
  bpmnElement.value = bpmnInstances().bpmnElement
  otherExtensionList.value = []
  bpmnElementListeners.value =
    bpmnElement.value.businessObject?.extensionElements?.values?.filter(
      (ex) => ex.$type === `${prefix}:ExecutionListener`
    ) ?? []
  elementListenersList.value = bpmnElementListeners.value.map((listener) =>
    initListenerType(listener)
  )
}
// 打开 监听器详情 侧边栏
const openListenerForm = (listener, index?) => {
  // debugger
  if (listener) {
    listenerForm.value = initListenerForm(listener)
    editingListenerIndex.value = index
  } else {
    listenerForm.value = {}
    editingListenerIndex.value = -1 // 标记为新增
  }
  if (listener && listener.fields) {
    fieldsListOfListener.value = listener.fields.map((field) => ({
      ...field,
      fieldType: field.string ? 'string' : 'expression'
    }))
  } else {
    fieldsListOfListener.value = []
    listenerForm.value['fields'] = []
  }
  // 打开侧边栏并清楚验证状态
  listenerFormModelVisible.value = true
  nextTick(() => {
    if (listenerFormRef.value) {
      listenerFormRef.value.clearValidate()
    }
  })
}
// 打开监听器字段编辑弹窗
const openListenerFieldForm = (field, index?) => {
  listenerFieldForm.value = field ? JSON.parse(JSON.stringify(field)) : {}
  editingListenerFieldIndex.value = field ? index : -1
  listenerFieldFormModelVisible.value = true
  nextTick(() => {
    if (listenerFieldFormRef.value) {
      listenerFieldFormRef.value.clearValidate()
    }
  })
}
// 保存监听器注入字段
const saveListenerFiled = async () => {
  // debugger
  let validateStatus = await listenerFieldFormRef.value.validate()
  if (!validateStatus) return // 验证不通过直接返回
  if (editingListenerFieldIndex.value === -1) {
    fieldsListOfListener.value.push(listenerFieldForm.value)
    listenerForm.value.fields.push(listenerFieldForm.value)
  } else {
    fieldsListOfListener.value.splice(editingListenerFieldIndex.value, 1, listenerFieldForm.value)
    listenerForm.value.fields.splice(editingListenerFieldIndex.value, 1, listenerFieldForm.value)
  }
  listenerFieldFormModelVisible.value = false
  nextTick(() => {
    listenerFieldForm.value = {}
  })
}
// 移除监听器字段
const removeListenerField = (index) => {
  // debugger
  ElMessageBox.confirm('确认移除该字段吗？', '提示', {
    confirmButtonText: '确 认',
    cancelButtonText: '取 消'
  })
    .then(() => {
      fieldsListOfListener.value.splice(index, 1)
      listenerForm.value.fields.splice(index, 1)
    })
    .catch(() => console.info('操作取消'))
}
// 移除监听器
const removeListener = (index) => {
  debugger
  ElMessageBox.confirm('确认移除该监听器吗？', '提示', {
    confirmButtonText: '确 认',
    cancelButtonText: '取 消'
  })
    .then(() => {
      bpmnElementListeners.value.splice(index, 1)
      elementListenersList.value.splice(index, 1)
      updateElementExtensions(
        bpmnElement.value,
        otherExtensionList.value.concat(bpmnElementListeners.value)
      )
    })
    .catch(() => console.info('操作取消'))
}
// 保存监听器配置
const saveListenerConfig = async () => {
  // debugger
  let validateStatus = await listenerFormRef.value.validate()
  if (!validateStatus) return // 验证不通过直接返回
  const listenerObject = createListenerObject(listenerForm.value, false, prefix)
  if (editingListenerIndex.value === -1) {
    bpmnElementListeners.value.push(listenerObject)
    elementListenersList.value.push(listenerForm.value)
  } else {
    bpmnElementListeners.value.splice(editingListenerIndex.value, 1, listenerObject)
    elementListenersList.value.splice(editingListenerIndex.value, 1, listenerForm.value)
  }
  // 保存其他配置
  otherExtensionList.value =
    bpmnElement.value.businessObject?.extensionElements?.values?.filter(
      (ex) => ex.$type !== `${prefix}:ExecutionListener`
    ) ?? []
  updateElementExtensions(
    bpmnElement.value,
    otherExtensionList.value.concat(bpmnElementListeners.value)
  )
  // 4. 隐藏侧边栏
  listenerFormModelVisible.value = false
  listenerForm.value = {}
}

// 打开监听器弹窗
const processListenerDialogRef = ref()
const openProcessListenerDialog = async () => {
  processListenerDialogRef.value.open('execution')
}
const selectProcessListener = (listener) => {
  const listenerForm = initListenerForm2(listener)
  const listenerObject = createListenerObject(listenerForm, false, prefix)
  bpmnElementListeners.value.push(listenerObject)
  elementListenersList.value.push(listenerForm)

  // 保存其他配置
  otherExtensionList.value =
    bpmnElement.value.businessObject?.extensionElements?.values?.filter(
      (ex) => ex.$type !== `${prefix}:ExecutionListener`
    ) ?? []
  updateElementExtensions(
    bpmnElement.value,
    otherExtensionList.value.concat(bpmnElementListeners.value)
  )
}

watch(
  () => props.id,
  (val) => {
    val &&
      val.length &&
      nextTick(() => {
        resetListenersList()
      })
  },
  { immediate: true }
)
</script>
