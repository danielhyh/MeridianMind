<template>
  <el-form ref="formRef" :model="modelData" :rules="rules" class="mt-20px" label-width="120px">
    <el-form-item class="mb-20px" label="流程标识" prop="key">
      <div class="flex items-center">
        <el-input
          v-model="modelData.key"
          :disabled="!!modelData.id"
          class="!w-440px"
          placeholder="请输入流程标识，以字母或下划线开头"
        />
        <el-tooltip
          :content="modelData.id ? '流程标识不可修改！' : '新建后，流程标识不可修改！'"
          class="item"
          effect="light"
          placement="top"
        >
          <Icon class="ml-5px" icon="ep:question-filled" />
        </el-tooltip>
      </div>
    </el-form-item>
    <el-form-item class="mb-20px" label="流程名称" prop="name">
      <el-input
        v-model="modelData.name"
        :disabled="!!modelData.id"
        clearable
        placeholder="请输入流程名称"
      />
    </el-form-item>
    <el-form-item class="mb-20px" label="流程分类" prop="category">
      <el-select
        v-model="modelData.category"
        class="!w-full"
        clearable
        placeholder="请选择流程分类"
      >
        <el-option
          v-for="category in categoryList"
          :key="category.code"
          :label="category.name"
          :value="category.code"
        />
      </el-select>
    </el-form-item>
    <el-form-item class="mb-20px" label="流程图标">
      <UploadImg v-model="modelData.icon" :limit="1" height="64px" width="64px" />
    </el-form-item>
    <el-form-item class="mb-20px" label="流程描述" prop="description">
      <el-input v-model="modelData.description" clearable type="textarea" />
    </el-form-item>
    <el-form-item class="mb-20px" label="流程类型" prop="type">
      <el-radio-group v-model="modelData.type">
        <el-radio
          v-for="dict in getIntDictOptions(DICT_TYPE.BPM_MODEL_TYPE)"
          :key="dict.value"
          :value="dict.value"
        >
          {{ dict.label }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item class="mb-20px" label="是否可见" prop="visible">
      <el-radio-group v-model="modelData.visible">
        <el-radio
          v-for="dict in getBoolDictOptions(DICT_TYPE.INFRA_BOOLEAN_STRING)"
          :key="dict.value as string"
          :value="dict.value"
        >
          {{ dict.label }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item class="mb-20px" label="谁可以发起" prop="startUserType">
      <el-select
        v-model="modelData.startUserType"
        placeholder="请选择谁可以发起"
        @change="handleStartUserTypeChange"
      >
        <el-option :value="0" label="全员" />
        <el-option :value="1" label="指定人员" />
      </el-select>
      <div v-if="modelData.startUserType === 1" class="mt-2 flex flex-wrap gap-2">
        <div
          v-for="user in selectedStartUsers"
          :key="user.id"
          class="bg-gray-100 h-35px rounded-3xl flex items-center pr-8px dark:color-gray-600 position-relative"
        >
          <el-avatar v-if="user.avatar" :size="28" :src="user.avatar" class="!m-5px" />
          <el-avatar v-else :size="28" class="!m-5px">
            {{ user.nickname.substring(0, 1) }}
          </el-avatar>
          {{ user.nickname }}
          <Icon
            class="ml-2 cursor-pointer hover:text-red-500"
            icon="ep:close"
            @click="handleRemoveStartUser(user)"
          />
        </div>
        <el-button link type="primary" @click="openStartUserSelect">
          <Icon icon="ep:plus" /> 选择人员
        </el-button>
      </div>
    </el-form-item>
    <el-form-item class="mb-20px" label="流程管理员" prop="managerUserIds">
      <div class="flex flex-wrap gap-2">
        <div
          v-for="user in selectedManagerUsers"
          :key="user.id"
          class="bg-gray-100 h-35px rounded-3xl flex items-center pr-8px dark:color-gray-600 position-relative"
        >
          <el-avatar v-if="user.avatar" :size="28" :src="user.avatar" class="!m-5px" />
          <el-avatar v-else :size="28" class="!m-5px">
            {{ user.nickname.substring(0, 1) }}
          </el-avatar>
          {{ user.nickname }}
          <Icon
            class="ml-2 cursor-pointer hover:text-red-500"
            icon="ep:close"
            @click="handleRemoveManagerUser(user)"
          />
        </div>
        <el-button link type="primary" @click="openManagerUserSelect">
          <Icon icon="ep:plus" />选择人员
        </el-button>
      </div>
    </el-form-item>
  </el-form>

  <!-- 用户选择弹窗 -->
  <UserSelectForm ref="userSelectFormRef" @confirm="handleUserSelectConfirm" />
</template>

<script lang="ts" setup>
import { DICT_TYPE, getBoolDictOptions, getIntDictOptions } from '@/utils/dict'
import { UserVO } from '@/api/system/user'
import { CategoryVO } from '@/api/bpm/category'

const props = defineProps({
  categoryList: {
    type: Array as PropType<CategoryVO[]>,
    required: true
  },
  userList: {
    type: Array,
    required: true
  }
})

const formRef = ref()
const selectedStartUsers = ref<UserVO[]>([])
const selectedManagerUsers = ref<UserVO[]>([])
const userSelectFormRef = ref()
const currentSelectType = ref<'start' | 'manager'>('start')

const rules = {
  name: [{ required: true, message: '流程名称不能为空', trigger: 'blur' }],
  key: [{ required: true, message: '流程标识不能为空', trigger: 'blur' }],
  category: [{ required: true, message: '流程分类不能为空', trigger: 'blur' }],
  type: [{ required: true, message: '是否可见不能为空', trigger: 'blur' }],
  visible: [{ required: true, message: '是否可见不能为空', trigger: 'blur' }],
  managerUserIds: [{ required: true, message: '流程管理员不能为空', trigger: 'blur' }]
}

// 创建本地数据副本
const modelData = defineModel<any>()

// 初始化选中的用户
watch(
  () => modelData.value,
  (newVal) => {
    if (newVal.startUserIds?.length) {
      selectedStartUsers.value = props.userList.filter((user: UserVO) =>
        newVal.startUserIds.includes(user.id)
      ) as UserVO[]
    } else {
      selectedStartUsers.value = []
    }
    if (newVal.managerUserIds?.length) {
      selectedManagerUsers.value = props.userList.filter((user: UserVO) =>
        newVal.managerUserIds.includes(user.id)
      ) as UserVO[]
    } else {
      selectedManagerUsers.value = []
    }
  },
  {
    immediate: true
  }
)

/** 打开发起人选择 */
const openStartUserSelect = () => {
  currentSelectType.value = 'start'
  userSelectFormRef.value.open(0, selectedStartUsers.value)
}

/** 打开管理员选择 */
const openManagerUserSelect = () => {
  currentSelectType.value = 'manager'
  userSelectFormRef.value.open(0, selectedManagerUsers.value)
}

/** 处理用户选择确认 */
const handleUserSelectConfirm = (_, users: UserVO[]) => {
  if (currentSelectType.value === 'start') {
    modelData.value = {
      ...modelData.value,
      startUserIds: users.map((u) => u.id)
    }
  } else {
    modelData.value = {
      ...modelData.value,
      managerUserIds: users.map((u) => u.id)
    }
  }
}

/** 处理发起人类型变化 */
const handleStartUserTypeChange = (value: number) => {
  if (value !== 1) {
    modelData.value = {
      ...modelData.value,
      startUserIds: []
    }
  }
}

/** 移除发起人 */
const handleRemoveStartUser = (user: UserVO) => {
  modelData.value = {
    ...modelData.value,
    startUserIds: modelData.value.startUserIds.filter((id: number) => id !== user.id)
  }
}

/** 移除管理员 */
const handleRemoveManagerUser = (user: UserVO) => {
  modelData.value = {
    ...modelData.value,
    managerUserIds: modelData.value.managerUserIds.filter((id: number) => id !== user.id)
  }
}

/** 表单校验 */
const validate = async () => {
  await formRef.value?.validate()
}

defineExpose({
  validate
})
</script>

<style lang="scss" scoped>
.bg-gray-100 {
  background-color: #f5f7fa;
  transition: all 0.3s;

  &:hover {
    background-color: #e6e8eb;
  }

  .ep-close {
    font-size: 14px;
    color: #909399;
    transition: color 0.3s;

    &:hover {
      color: #f56c6c;
    }
  }
}
</style>
