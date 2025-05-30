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
    <el-tabs v-model="activeTabName" type="border-card">
      <el-tab-pane label="抄送人" name="user">
        <div>
          <el-form ref="formRef" :model="configForm" :rules="formRules" label-position="top">
            <el-form-item label="抄送人设置" prop="candidateStrategy">
              <el-radio-group
                v-model="configForm.candidateStrategy"
                @change="changeCandidateStrategy"
              >
                <el-radio
                  v-for="(dict, index) in copyUserStrategies"
                  :key="index"
                  :label="dict.value"
                  :value="dict.value"
                >
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item
              v-if="configForm.candidateStrategy == CandidateStrategy.ROLE"
              label="指定角色"
              prop="roleIds"
            >
              <el-select v-model="configForm.roleIds" clearable multiple style="width: 100%">
                <el-option
                  v-for="item in roleOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item
              v-if="
                configForm.candidateStrategy == CandidateStrategy.DEPT_MEMBER ||
                configForm.candidateStrategy == CandidateStrategy.DEPT_LEADER ||
                configForm.candidateStrategy == CandidateStrategy.MULTI_LEVEL_DEPT_LEADER
              "
              label="指定部门"
              prop="deptIds"
              span="24"
            >
              <el-tree-select
                ref="treeRef"
                v-model="configForm.deptIds"
                :data="deptTreeOptions"
                :props="defaultProps"
                empty-text="加载中，请稍后"
                multiple
                node-key="id"
                show-checkbox
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item
              v-if="configForm.candidateStrategy == CandidateStrategy.POST"
              label="指定岗位"
              prop="postIds"
              span="24"
            >
              <el-select v-model="configForm.postIds" clearable multiple style="width: 100%">
                <el-option
                  v-for="item in postOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id!"
                />
              </el-select>
            </el-form-item>
            <el-form-item
              v-if="configForm.candidateStrategy == CandidateStrategy.USER"
              label="指定用户"
              prop="userIds"
              span="24"
            >
              <el-select v-model="configForm.userIds" clearable multiple style="width: 100%">
                <el-option
                  v-for="item in userOptions"
                  :key="item.id"
                  :label="item.nickname"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item
              v-if="configForm.candidateStrategy === CandidateStrategy.USER_GROUP"
              label="指定用户组"
              prop="userGroups"
            >
              <el-select v-model="configForm.userGroups" clearable multiple style="width: 100%">
                <el-option
                  v-for="item in userGroupOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item
              v-if="configForm.candidateStrategy === CandidateStrategy.FORM_USER"
              label="表单内用户字段"
              prop="formUser"
            >
              <el-select v-model="configForm.formUser" clearable style="width: 100%">
                <el-option
                  v-for="(item, idx) in userFieldOnFormOptions"
                  :key="idx"
                  :disabled="!item.required"
                  :label="item.title"
                  :value="item.field"
                />
              </el-select>
            </el-form-item>
            <el-form-item
              v-if="configForm.candidateStrategy === CandidateStrategy.FORM_DEPT_LEADER"
              label="表单内部门字段"
              prop="formDept"
            >
              <el-select v-model="configForm.formDept" clearable style="width: 100%">
                <el-option
                  v-for="(item, idx) in deptFieldOnFormOptions"
                  :key="idx"
                  :disabled="!item.required"
                  :label="item.title"
                  :value="item.field"
                />
              </el-select>
            </el-form-item>
            <el-form-item
              v-if="
                configForm.candidateStrategy == CandidateStrategy.MULTI_LEVEL_DEPT_LEADER ||
                configForm.candidateStrategy == CandidateStrategy.START_USER_DEPT_LEADER ||
                configForm.candidateStrategy ==
                  CandidateStrategy.START_USER_MULTI_LEVEL_DEPT_LEADER ||
                configForm.candidateStrategy == CandidateStrategy.FORM_DEPT_LEADER
              "
              :label="deptLevelLabel!"
              prop="deptLevel"
              span="24"
            >
              <el-select v-model="configForm.deptLevel" clearable>
                <el-option
                  v-for="(item, index) in MULTI_LEVEL_DEPT"
                  :key="index"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item
              v-if="configForm.candidateStrategy === CandidateStrategy.EXPRESSION"
              label="流程表达式"
              prop="expression"
            >
              <el-input
                v-model="configForm.expression"
                clearable
                style="width: 100%"
                type="textarea"
              />
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
      <el-tab-pane v-if="formType === 10" label="表单字段权限" name="fields">
        <div class="field-setting-pane">
          <div class="field-setting-desc">字段权限</div>
          <div class="field-permit-title">
            <div class="setting-title-label first-title"> 字段名称 </div>
            <div class="other-titles">
              <span class="setting-title-label cursor-pointer" @click="updatePermission('READ')">
                只读
              </span>
              <span class="setting-title-label cursor-pointer" @click="updatePermission('WRITE')">
                可编辑
              </span>
              <span class="setting-title-label cursor-pointer" @click="updatePermission('NONE')">
                隐藏
              </span>
            </div>
          </div>
          <div
            v-for="(item, index) in fieldsPermissionConfig"
            :key="index"
            class="field-setting-item"
          >
            <div class="field-setting-item-label"> {{ item.title }} </div>
            <el-radio-group v-model="item.permission" class="field-setting-item-group">
              <div class="item-radio-wrap">
                <el-radio
                  :label="FieldPermissionType.WRITE"
                  :value="FieldPermissionType.READ"
                  size="large"
                  ><span></span
                ></el-radio>
              </div>
              <div class="item-radio-wrap">
                <el-radio
                  :label="FieldPermissionType.WRITE"
                  :value="FieldPermissionType.WRITE"
                  disabled
                  size="large"
                  ><span></span
                ></el-radio>
              </div>
              <div class="item-radio-wrap">
                <el-radio
                  :label="FieldPermissionType.NONE"
                  :value="FieldPermissionType.NONE"
                  size="large"
                  ><span></span
                ></el-radio>
              </div>
            </el-radio-group>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
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
  CandidateStrategy,
  NodeType,
  CANDIDATE_STRATEGY,
  FieldPermissionType,
  MULTI_LEVEL_DEPT
} from '../consts'
import {
  useWatchNode,
  useDrawer,
  useNodeName,
  useFormFieldsPermission,
  useNodeForm,
  CopyTaskFormType
} from '../node'
import { defaultProps } from '@/utils/tree'
defineOptions({
  name: 'CopyTaskNodeConfig'
})
const props = defineProps({
  flowNode: {
    type: Object as () => SimpleFlowNode,
    required: true
  }
})
const deptLevelLabel = computed(() => {
  let label = '部门负责人来源'
  if (configForm.value.candidateStrategy == CandidateStrategy.MULTI_LEVEL_DEPT_LEADER) {
    label = label + '(指定部门向上)'
  } else {
    label = label + '(发起人部门向上)'
  }
  return label
})
// 抽屉配置
const { settingVisible, closeDrawer, openDrawer } = useDrawer()
// 当前节点
const currentNode = useWatchNode(props)
// 节点名称
const { nodeName, showInput, clickIcon, blurEvent } = useNodeName(NodeType.COPY_TASK_NODE)
// 激活的 Tab 标签页
const activeTabName = ref('user')
// 表单字段权限配置
const { formType, fieldsPermissionConfig, formFieldOptions, getNodeConfigFormFields } =
  useFormFieldsPermission(FieldPermissionType.READ)
// 表单内用户字段选项, 必须是必填和用户选择器
const userFieldOnFormOptions = computed(() => {
  return formFieldOptions.filter((item) => item.type === 'UserSelect')
})
// 表单内部门字段选项, 必须是必填和部门选择器
const deptFieldOnFormOptions = computed(() => {
  return formFieldOptions.filter((item) => item.type === 'DeptSelect')
})
// 抄送人表单配置
const formRef = ref() // 表单 Ref
// 表单校验规则
const formRules = reactive({
  candidateStrategy: [{ required: true, message: '抄送人设置不能为空', trigger: 'change' }],
  userIds: [{ required: true, message: '用户不能为空', trigger: 'change' }],
  roleIds: [{ required: true, message: '角色不能为空', trigger: 'change' }],
  deptIds: [{ required: true, message: '部门不能为空', trigger: 'change' }],
  userGroups: [{ required: true, message: '用户组不能为空', trigger: 'change' }],
  postIds: [{ required: true, message: '岗位不能为空', trigger: 'change' }],
  formUser: [{ required: true, message: '表单内用户字段不能为空', trigger: 'change' }],
  formDept: [{ required: true, message: '表单内部门字段不能为空', trigger: 'change' }],
  expression: [{ required: true, message: '流程表达式不能为空', trigger: 'blur' }]
})

const {
  configForm: tempConfigForm,
  roleOptions,
  postOptions,
  userOptions,
  userGroupOptions,
  deptTreeOptions,
  getShowText,
  handleCandidateParam,
  parseCandidateParam
} = useNodeForm(NodeType.COPY_TASK_NODE)
const configForm = tempConfigForm as Ref<CopyTaskFormType>
// 抄送人策略， 去掉发起人自选 和 发起人自己
const copyUserStrategies = computed(() => {
  return CANDIDATE_STRATEGY.filter((item) => item.value !== CandidateStrategy.START_USER)
})
// 改变抄送人设置策略
const changeCandidateStrategy = () => {
  configForm.value.userIds = []
  configForm.value.deptIds = []
  configForm.value.roleIds = []
  configForm.value.postIds = []
  configForm.value.userGroups = []
  configForm.value.deptLevel = 1
  configForm.value.formUser = ''
}
// 保存配置
const saveConfig = async () => {
  activeTabName.value = 'user'
  if (!formRef) return false
  const valid = await formRef.value.validate()
  if (!valid) return false
  const showText = getShowText()
  if (!showText) return false
  currentNode.value.name = nodeName.value!
  currentNode.value.candidateParam = handleCandidateParam()
  currentNode.value.candidateStrategy = configForm.value.candidateStrategy
  currentNode.value.showText = showText
  currentNode.value.fieldsPermission = fieldsPermissionConfig.value
  settingVisible.value = false
  return true
}
// 显示抄送节点配置， 由父组件传过来
const showCopyTaskNodeConfig = (node: SimpleFlowNode) => {
  nodeName.value = node.name
  // 抄送人设置
  configForm.value.candidateStrategy = node.candidateStrategy!
  parseCandidateParam(node.candidateStrategy!, node?.candidateParam)
  // 表单字段权限
  getNodeConfigFormFields(node.fieldsPermission)
}

/** 批量更新权限 */
const updatePermission = (type: string) => {
  fieldsPermissionConfig.value.forEach((field) => {
    field.permission =
      type === 'READ'
        ? FieldPermissionType.READ
        : type === 'WRITE'
          ? FieldPermissionType.WRITE
          : FieldPermissionType.NONE
  })
}

defineExpose({ openDrawer, showCopyTaskNodeConfig }) // 暴露方法给父组件
</script>

<style lang="scss" scoped></style>
