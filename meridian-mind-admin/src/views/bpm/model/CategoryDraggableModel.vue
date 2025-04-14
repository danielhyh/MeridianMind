<template>
  <div v-memo="[categoryInfo.name, isCategorySorting]" class="flex items-center h-50px">
    <!-- 头部：分类名 -->
    <div class="flex items-center">
      <el-tooltip v-if="isCategorySorting" content="拖动排序">
        <Icon
          :size="22"
          class="ml-10px category-drag-icon cursor-move text-#8a909c"
          icon="ic:round-drag-indicator"
        />
      </el-tooltip>
      <h3 class="ml-20px mr-8px text-18px">{{ categoryInfo.name }}</h3>
      <div class="color-gray-600 text-16px"> ({{ categoryInfo.modelList?.length || 0 }}) </div>
    </div>
    <!-- 头部：操作 -->
    <div v-show="!isCategorySorting" class="flex-1 flex">
      <div
        v-if="categoryInfo.modelList.length > 0"
        :class="[
          'transition-transform duration-300 cursor-pointer',
          isExpand ? 'rotate-180' : 'rotate-0'
        ]"
        class="ml-20px flex items-center"
        @click="isExpand = !isExpand"
      >
        <Icon color="#999" icon="ep:arrow-down-bold" />
      </div>
      <div :class="isModelSorting ? 'mr-15px' : 'mr-45px'" class="ml-auto flex items-center">
        <template v-if="!isModelSorting">
          <el-button
            v-if="categoryInfo.modelList.length > 0"
            class="mr-20px"
            link
            type="info"
            @click.stop="handleModelSort"
          >
            <Icon class="mr-5px" icon="fa:sort-amount-desc" />
            排序
          </el-button>
          <el-button v-else class="mr-20px" link type="info" @click.stop="openModelForm('create')">
            <Icon class="mr-5px" icon="fa:plus" />
            新建
          </el-button>
          <el-dropdown
            placement="bottom"
            @command="(command) => handleCategoryCommand(command, categoryInfo)"
          >
            <el-button link type="info">
              <Icon class="mr-5px" icon="ep:setting" />
              分类
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="handleRename"> 重命名 </el-dropdown-item>
                <el-dropdown-item command="handleDeleteCategory"> 删除该类 </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button @click.stop="handleModelSortCancel"> 取 消 </el-button>
          <el-button type="primary" @click.stop="handleModelSortSubmit"> 保存排序 </el-button>
        </template>
      </div>
    </div>
  </div>

  <!-- 模型列表 -->
  <el-collapse-transition>
    <div v-show="isExpand">
      <el-table
        v-if="modelList && modelList.length > 0"
        ref="tableRef"
        :cell-style="tableCellStyle"
        :class="categoryInfo.name"
        :data="modelList"
        :header-cell-style="tableHeaderStyle"
        :row-style="{ height: '68px' }"
        row-key="id"
      >
        <el-table-column label="流程名" min-width="150" prop="name">
          <template #default="{ row }">
            <div class="flex items-center">
              <el-tooltip v-if="isModelSorting" content="拖动排序">
                <Icon
                  class="drag-icon cursor-move text-#8a909c mr-10px"
                  icon="ic:round-drag-indicator"
                />
              </el-tooltip>
              <el-image v-if="row.icon" :src="row.icon" class="h-38px w-38px mr-10px rounded" />
              <div v-else class="flow-icon">
                <span style="font-size: 12px; color: #fff">{{ subString(row.name, 0, 2) }}</span>
              </div>
              {{ row.name }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="可见范围" min-width="150" prop="startUserIds">
          <template #default="{ row }">
            <el-text v-if="!row.startUsers?.length"> 全部可见 </el-text>
            <el-text v-else-if="row.startUsers.length === 1">
              {{ row.startUsers[0].nickname }}
            </el-text>
            <el-text v-else>
              <el-tooltip
                :content="row.startUsers.map((user: any) => user.nickname).join('、')"
                class="box-item"
                effect="dark"
                placement="top"
              >
                {{ row.startUsers[0].nickname }}等 {{ row.startUsers.length }} 人可见
              </el-tooltip>
            </el-text>
          </template>
        </el-table-column>
        <el-table-column label="流程类型" min-width="120" prop="type">
          <template #default="{ row }">
            <dict-tag :type="DICT_TYPE.BPM_MODEL_TYPE" :value="row.type" />
          </template>
        </el-table-column>
        <el-table-column label="表单信息" min-width="150" prop="formType">
          <template #default="scope">
            <el-button
              v-if="scope.row.formType === BpmModelFormType.NORMAL"
              link
              type="primary"
              @click="handleFormDetail(scope.row)"
            >
              <span>{{ scope.row.formName }}</span>
            </el-button>
            <el-button
              v-else-if="scope.row.formType === BpmModelFormType.CUSTOM"
              link
              type="primary"
              @click="handleFormDetail(scope.row)"
            >
              <span>{{ scope.row.formCustomCreatePath }}</span>
            </el-button>
            <label v-else>暂无表单</label>
          </template>
        </el-table-column>
        <el-table-column label="最后发布" min-width="250" prop="deploymentTime">
          <template #default="scope">
            <div class="flex items-center">
              <span v-if="scope.row.processDefinition" class="w-150px">
                {{ formatDate(scope.row.processDefinition.deploymentTime) }}
              </span>
              <el-tag v-if="scope.row.processDefinition">
                v{{ scope.row.processDefinition.version }}
              </el-tag>
              <el-tag v-else type="warning">未部署</el-tag>
              <el-tag
                v-if="scope.row.processDefinition?.suspensionState === 2"
                class="ml-10px"
                type="warning"
              >
                已停用
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="200">
          <template #default="scope">
            <el-button
              v-if="hasPermiUpdate"
              :disabled="!isManagerUser(scope.row)"
              link
              type="primary"
              @click="openModelForm('update', scope.row.id)"
            >
              修改
            </el-button>
            <el-button
              v-if="hasPermiUpdate"
              :disabled="!isManagerUser(scope.row)"
              link
              type="primary"
              @click="openModelForm('copy', scope.row.id)"
            >
              复制
            </el-button>
            <el-button
              v-if="hasPermiDeploy"
              :disabled="!isManagerUser(scope.row)"
              class="!ml-5px"
              link
              type="primary"
              @click="handleDeploy(scope.row)"
            >
              发布
            </el-button>
            <el-dropdown
              v-if="hasPermiMore"
              class="!align-middle ml-5px"
              @command="(command) => handleModelCommand(command, scope.row)"
            >
              <el-button link type="primary">更多</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="hasPermiPdQuery" command="handleDefinitionList">
                    历史
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-if="
                      checkPermi(['bpm:process-instance:manager-query']) &&
                      scope.row.processDefinition
                    "
                    :disabled="!isManagerUser(scope.row)"
                    command="handleReport"
                  >
                    报表
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-if="hasPermiUpdate && scope.row.processDefinition"
                    :disabled="!isManagerUser(scope.row)"
                    command="handleChangeState"
                  >
                    {{ scope.row.processDefinition.suspensionState === 1 ? '停用' : '启用' }}
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-if="checkPermi(['bpm:model:clean'])"
                    :disabled="!isManagerUser(scope.row)"
                    command="handleClean"
                    type="danger"
                  >
                    清理
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-if="hasPermiDelete"
                    :disabled="!isManagerUser(scope.row)"
                    command="handleDelete"
                    type="danger"
                  >
                    删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </el-collapse-transition>

  <!-- 弹窗：重命名分类 -->
  <Dialog v-model="renameCategoryVisible" :fullscreen="false" class="rename-dialog" width="400">
    <template #title>
      <div class="pl-10px font-bold text-18px"> 重命名分类 </div>
    </template>
    <div class="px-30px">
      <el-input v-model="renameCategoryForm.name" />
    </div>
    <template #footer>
      <div class="pr-25px pb-25px">
        <el-button @click="renameCategoryVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleRenameConfirm">确 定</el-button>
      </div>
    </template>
  </Dialog>

  <!-- 弹窗：表单详情 -->
  <Dialog v-model="formDetailVisible" :fullscreen="true" title="表单详情">
    <form-create :option="formDetailPreview.option" :rule="formDetailPreview.rule" />
  </Dialog>
</template>

<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import { CategoryApi, CategoryVO } from '@/api/bpm/category'
import Sortable from 'sortablejs'
import { formatDate } from '@/utils/formatTime'
import * as ModelApi from '@/api/bpm/model'
import * as FormApi from '@/api/bpm/form'
import { setConfAndFields2 } from '@/utils/formCreate'
import { BpmModelFormType } from '@/utils/constants'
import { checkPermi } from '@/utils/permission'
import { useUserStoreWithOut } from '@/store/modules/user'
import { useAppStore } from '@/store/modules/app'
import { cloneDeep, isEqual } from 'lodash-es'
import { useDebounceFn } from '@vueuse/core'
import { subString } from '@/utils/index'

defineOptions({ name: 'BpmModel' })

// 优化 Props 类型定义
interface UserInfo {
  nickname: string
  [key: string]: any
}

interface ProcessDefinition {
  deploymentTime: string
  version: number
  suspensionState: number
}

interface ModelInfo {
  id: number
  name: string
  icon?: string
  startUsers?: UserInfo[]
  processDefinition?: ProcessDefinition
  formType?: number
  formId?: number
  formName?: string
  formCustomCreatePath?: string
  managerUserIds?: number[]
  [key: string]: any
}

interface CategoryInfoProps {
  id: number
  name: string
  modelList: ModelInfo[]
}

const props = defineProps<{
  categoryInfo: CategoryInfoProps
  isCategorySorting: boolean
}>()

const emit = defineEmits(['success'])
const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const { push } = useRouter() // 路由
const userStore = useUserStoreWithOut() // 用户信息缓存
const isDark = computed(() => useAppStore().getIsDark) // 是否黑暗模式
const router = useRouter() // 路由

const isModelSorting = ref(false) // 是否正处于排序状态
const originalData = ref<ModelInfo[]>([]) // 原始数据
const modelList = ref<ModelInfo[]>([]) // 模型列表
const isExpand = ref(false) // 是否处于展开状态

// 使用 computed 优化表格样式计算
const tableHeaderStyle = computed(() => ({
  backgroundColor: isDark.value ? '' : '#edeff0',
  paddingLeft: '10px'
}))

const tableCellStyle = computed(() => ({
  paddingLeft: '10px'
}))

/** 权限校验：通过 computed 解决列表的卡顿问题 */
const hasPermiUpdate = computed(() => {
  return checkPermi(['bpm:model:update'])
})
const hasPermiDelete = computed(() => {
  return checkPermi(['bpm:model:delete'])
})
const hasPermiDeploy = computed(() => {
  return checkPermi(['bpm:model:deploy'])
})
const hasPermiMore = computed(() => {
  return checkPermi(['bpm:process-definition:query', 'bpm:model:update', 'bpm:model:delete'])
})
const hasPermiPdQuery = computed(() => {
  return checkPermi(['bpm:process-definition:query'])
})

/** '更多'操作按钮 */
const handleModelCommand = (command: string, row: any) => {
  switch (command) {
    case 'handleDefinitionList':
      handleDefinitionList(row)
      break
    case 'handleDelete':
      handleDelete(row)
      break
    case 'handleChangeState':
      handleChangeState(row)
      break
    case 'handleClean':
      handleClean(row)
      break
    case 'handleReport':
      router.push({
        name: 'BpmProcessInstanceReport',
        query: {
          processDefinitionId: row.processDefinition.id,
          processDefinitionKey: row.key
        }
      })
      break
    default:
      break
  }
}

/** '分类'操作按钮 */
const handleCategoryCommand = async (command: string, row: any) => {
  switch (command) {
    case 'handleRename':
      renameCategoryForm.value = await CategoryApi.getCategory(row.id)
      renameCategoryVisible.value = true
      break
    case 'handleDeleteCategory':
      await handleDeleteCategory()
      break
    default:
      break
  }
}

/** 删除按钮操作 */
const handleDelete = async (row: any) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await ModelApi.deleteModel(row.id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    emit('success')
  } catch {}
}

/** 清理按钮操作 */
const handleClean = async (row: any) => {
  try {
    // 清理的二次确认
    await message.confirm('是否确认清理流程名字为"' + row.name + '"的数据项?')
    // 发起清理
    await ModelApi.cleanModel(row.id)
    message.success('清理成功')
    // 刷新列表
    emit('success')
  } catch {}
}

/** 更新状态操作 */
const handleChangeState = async (row: any) => {
  const state = row.processDefinition.suspensionState
  const newState = state === 1 ? 2 : 1
  try {
    // 修改状态的二次确认
    const id = row.id
    debugger
    const statusState = state === 1 ? '停用' : '启用'
    const content = '是否确认' + statusState + '流程名字为"' + row.name + '"的数据项?'
    await message.confirm(content)
    // 发起修改状态
    await ModelApi.updateModelState(id, newState)
    message.success(statusState + '成功')
    // 刷新列表
    emit('success')
  } catch {}
}

/** 发布流程 */
const handleDeploy = async (row: any) => {
  try {
    await message.confirm('是否确认发布该流程？')
    // 发起部署
    await ModelApi.deployModel(row.id)
    message.success(t('发布成功'))
    // 刷新列表
    emit('success')
  } catch {}
}

/** 跳转到指定流程定义列表 */
const handleDefinitionList = (row: any) => {
  push({
    name: 'BpmProcessDefinition',
    query: {
      key: row.key
    }
  })
}

/** 流程表单的详情按钮操作 */
const formDetailVisible = ref(false)
const formDetailPreview = ref({
  rule: [],
  option: {}
})
const handleFormDetail = async (row: any) => {
  if (row.formType == BpmModelFormType.NORMAL) {
    // 设置表单
    const data = await FormApi.getForm(row.formId)
    setConfAndFields2(formDetailPreview, data.conf, data.fields)
    // 弹窗打开
    formDetailVisible.value = true
  } else {
    await push({
      path: row.formCustomCreatePath
    })
  }
}

/** 判断是否可以操作 */
const isManagerUser = (row: any) => {
  const userId = userStore.getUser.id
  return row.managerUserIds && row.managerUserIds.includes(userId)
}

/** 处理模型的排序 **/
const handleModelSort = () => {
  // 保存初始数据
  originalData.value = cloneDeep(props.categoryInfo.modelList)
  isModelSorting.value = true
  initSort()
}

/** 处理模型的排序提交 */
const handleModelSortSubmit = async () => {
  // 保存排序
  const ids = modelList.value.map((item: any) => item.id)
  await ModelApi.updateModelSortBatch(ids)
  // 刷新列表
  isModelSorting.value = false
  message.success('排序模型成功')
  emit('success')
}

/** 处理模型的排序取消 */
const handleModelSortCancel = () => {
  // 恢复初始数据
  modelList.value = cloneDeep(originalData.value)
  isModelSorting.value = false
}

/** 创建拖拽实例 */
const tableRef = ref()
const initSort = useDebounceFn(() => {
  const table = document.querySelector(`.${props.categoryInfo.name} .el-table__body-wrapper tbody`)
  if (!table) return

  Sortable.create(table, {
    group: 'shared',
    animation: 150,
    draggable: '.el-table__row',
    handle: '.drag-icon',
    onEnd: ({ newDraggableIndex, oldDraggableIndex }) => {
      if (oldDraggableIndex !== newDraggableIndex) {
        modelList.value.splice(
          newDraggableIndex,
          0,
          modelList.value.splice(oldDraggableIndex, 1)[0]
        )
      }
    }
  })
}, 200)

/** 更新 modelList 模型列表 */
const updateModeList = useDebounceFn(() => {
  const newModelList = props.categoryInfo.modelList
  if (!isEqual(modelList.value, newModelList)) {
    modelList.value = cloneDeep(newModelList)
    if (newModelList?.length > 0) {
      isExpand.value = true
    }
  }
}, 100)

/** 重命名弹窗确定 */
const renameCategoryVisible = ref(false)
const renameCategoryForm = ref({
  name: ''
})
const handleRenameConfirm = async () => {
  if (renameCategoryForm.value?.name.length === 0) {
    return message.warning('请输入名称')
  }
  // 发起修改
  await CategoryApi.updateCategory(renameCategoryForm.value as CategoryVO)
  message.success('重命名成功')
  // 刷新列表
  renameCategoryVisible.value = false
  emit('success')
}

/** 删除分类 */
const handleDeleteCategory = async () => {
  try {
    if (props.categoryInfo.modelList.length > 0) {
      return message.warning('该分类下仍有流程定义,不允许删除')
    }
    await message.confirm('确认删除分类吗?')
    // 发起删除
    await CategoryApi.deleteCategory(props.categoryInfo.id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    emit('success')
  } catch {}
}

/** 添加/修改/复制流程模型弹窗 */
const openModelForm = async (type: string, id?: number) => {
  if (type === 'create') {
    await push({ name: 'BpmModelCreate' })
  } else {
    await push({
      name: 'BpmModelUpdate',
      params: { id, type }
    })
  }
}

watchEffect(() => {
  if (props.categoryInfo?.modelList) {
    updateModeList()
  }

  if (props.isCategorySorting) {
    isExpand.value = false
  }
})
</script>

<style lang="scss">
.rename-dialog.el-dialog {
  padding: 0 !important;

  .el-dialog__header {
    border-bottom: none;
  }

  .el-dialog__footer {
    border-top: none !important;
  }
}
</style>
<style lang="scss" scoped>
.flow-icon {
  display: flex;
  width: 38px;
  height: 38px;
  margin-right: 10px;
  background-color: var(--el-color-primary);
  border-radius: 0.25rem;
  align-items: center;
  justify-content: center;
}

.category-draggable-model {
  :deep(.el-table__cell) {
    overflow: hidden;
    border-bottom: none !important;
  }

  // 优化表格渲染性能
  :deep(.el-table__body) {
    will-change: transform;
    transform: translateZ(0);
  }
}
</style>
