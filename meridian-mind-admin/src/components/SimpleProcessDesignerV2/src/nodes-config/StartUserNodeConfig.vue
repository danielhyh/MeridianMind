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
      <el-tab-pane label="权限" name="user">
        <el-text v-if="!startUserIds || startUserIds.length === 0"> 全部成员可以发起流程 </el-text>
        <el-text v-else-if="startUserIds.length == 1">
          {{ getUserNicknames(startUserIds) }} 可发起流程
        </el-text>
        <el-text v-else>
          <el-tooltip
            :content="getUserNicknames(startUserIds)"
            class="box-item"
            effect="dark"
            placement="top"
          >
            {{ getUserNicknames(startUserIds.slice(0, 2)) }} 等
            {{ startUserIds.length }} 人可发起流程
          </el-tooltip>
        </el-text>
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
                  :label="FieldPermissionType.READ"
                  :value="FieldPermissionType.READ"
                  size="large"
                  ><span></span
                ></el-radio>
              </div>
              <div class="item-radio-wrap">
                <el-radio
                  :label="FieldPermissionType.WRITE"
                  :value="FieldPermissionType.WRITE"
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
import { SimpleFlowNode, NodeType, FieldPermissionType, START_USER_BUTTON_SETTING } from '../consts'
import { useWatchNode, useDrawer, useNodeName, useFormFieldsPermission } from '../node'
import * as UserApi from '@/api/system/user'
defineOptions({
  name: 'StartUserNodeConfig'
})
const props = defineProps({
  flowNode: {
    type: Object as () => SimpleFlowNode,
    required: true
  }
})
// 可发起流程的用户编号
const startUserIds = inject<Ref<any[]>>('startUserIds')
// 用户列表
const userOptions = inject<Ref<UserApi.UserVO[]>>('userList')
// 抽屉配置
const { settingVisible, closeDrawer, openDrawer } = useDrawer()
// 当前节点
const currentNode = useWatchNode(props)
// 节点名称
const { nodeName, showInput, clickIcon, blurEvent } = useNodeName(NodeType.COPY_TASK_NODE)
// 激活的 Tab 标签页
const activeTabName = ref('user')
// 表单字段权限配置
const { formType, fieldsPermissionConfig, getNodeConfigFormFields } = useFormFieldsPermission(
  FieldPermissionType.WRITE
)
const getUserNicknames = (userIds: number[]): string => {
  if (!userIds || userIds.length === 0) {
    return ''
  }
  const nicknames: string[] = []
  userIds.forEach((userId) => {
    const found = userOptions?.value.find((item) => item.id === userId)
    if (found && found.nickname) {
      nicknames.push(found.nickname)
    }
  })
  return nicknames.join(',')
}
// 保存配置
const saveConfig = async () => {
  activeTabName.value = 'user'
  currentNode.value.name = nodeName.value!
  currentNode.value.showText = '已设置'
  // 设置表单权限
  currentNode.value.fieldsPermission = fieldsPermissionConfig.value
  // 设置发起人的按钮权限
  currentNode.value.buttonsSetting = START_USER_BUTTON_SETTING
  settingVisible.value = false
  return true
}
// 显示发起人节点配置， 由父组件传过来
const showStartUserNodeConfig = (node: SimpleFlowNode) => {
  nodeName.value = node.name
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
defineExpose({ openDrawer, showStartUserNodeConfig }) // 暴露方法给父组件
</script>

<style lang="scss" scoped></style>
