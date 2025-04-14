<template>
  <el-form ref="formRef" :model="condition" :rules="formRules" label-position="top">
    <el-form-item label="配置方式" prop="conditionType">
      <el-radio-group v-model="condition.conditionType" @change="changeConditionType">
        <el-radio
          v-for="(dict, indexConditionType) in conditionConfigTypes"
          :key="indexConditionType"
          :label="dict.value"
          :value="dict.value"
        >
          {{ dict.label }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item
      v-if="condition.conditionType === ConditionType.RULE && condition.conditionGroups"
      label="条件规则"
    >
      <div class="condition-group-tool">
        <div class="flex items-center">
          <div class="mr-4">条件组关系</div>
          <el-switch
            v-model="condition.conditionGroups.and"
            active-text="且"
            inactive-text="或"
            inline-prompt
          />
        </div>
      </div>
      <el-space :spacer="condition.conditionGroups.and ? '且' : '或'" direction="vertical">
        <el-card
          v-for="(equation, cIdx) in condition.conditionGroups.conditions"
          :key="cIdx"
          class="condition-group"
          style="width: 530px"
        >
          <div
            v-if="condition.conditionGroups.conditions.length > 1"
            class="condition-group-delete"
          >
            <Icon
              :size="18"
              color="#0089ff"
              icon="ep:circle-close-filled"
              @click="deleteConditionGroup(condition.conditionGroups.conditions, cIdx)"
            />
          </div>
          <template #header>
            <div class="flex items-center justify-between">
              <div>条件组</div>
              <div class="flex">
                <div class="mr-4">规则关系</div>
                <el-switch
                  v-model="equation.and"
                  active-text="且"
                  inactive-text="或"
                  inline-prompt
                />
              </div>
            </div>
          </template>

          <div v-for="(rule, rIdx) in equation.rules" :key="rIdx" class="flex pt-2">
            <div class="mr-2">
              <el-form-item
                :prop="`conditionGroups.conditions.${cIdx}.rules.${rIdx}.leftSide`"
                :rules="{
                  required: true,
                  message: '左值不能为空',
                  trigger: 'change'
                }"
              >
                <el-select v-model="rule.leftSide" clearable style="width: 160px">
                  <el-option
                    v-for="(field, fIdx) in fieldOptions"
                    :key="fIdx"
                    :disabled="!field.required"
                    :label="field.title"
                    :value="field.field"
                  >
                    <el-tooltip
                      v-if="!field.required"
                      content="表单字段非必填时不能作为流程分支条件"
                      effect="dark"
                      placement="right-start"
                    >
                      <span>{{ field.title }}</span>
                    </el-tooltip>
                  </el-option>
                </el-select>
              </el-form-item>
            </div>
            <div class="mr-2">
              <el-select v-model="rule.opCode" style="width: 100px">
                <el-option
                  v-for="operator in COMPARISON_OPERATORS"
                  :key="operator.value"
                  :label="operator.label"
                  :value="operator.value"
                />
              </el-select>
            </div>
            <div class="mr-2">
              <el-form-item
                :prop="`conditionGroups.conditions.${cIdx}.rules.${rIdx}.rightSide`"
                :rules="{
                  required: true,
                  message: '右值不能为空',
                  trigger: 'blur'
                }"
              >
                <el-input v-model="rule.rightSide" style="width: 160px" />
              </el-form-item>
            </div>
            <div v-if="equation.rules.length > 1" class="mr-1 flex items-center">
              <Icon :size="18" icon="ep:delete" @click="deleteConditionRule(equation, rIdx)" />
            </div>
            <div class="flex items-center">
              <Icon :size="18" icon="ep:plus" @click="addConditionRule(equation, rIdx)" />
            </div>
          </div>
        </el-card>
      </el-space>
      <div class="mt-4 cursor-pointer" title="添加条件组">
        <Icon
          :size="24"
          color="#0089ff"
          icon="ep:plus"
          @click="addConditionGroup(condition.conditionGroups?.conditions)"
        />
      </div>
    </el-form-item>
    <el-form-item
      v-if="condition.conditionType === ConditionType.EXPRESSION"
      label="条件表达式"
      prop="conditionExpression"
    >
      <el-input
        v-model="condition.conditionExpression"
        clearable
        style="width: 100%"
        type="textarea"
      />
    </el-form-item>
  </el-form>
</template>

<script lang="ts" setup>
import {
  COMPARISON_OPERATORS,
  CONDITION_CONFIG_TYPES,
  ConditionType,
  DEFAULT_CONDITION_GROUP_VALUE
} from '../../consts'
import { BpmModelFormType } from '@/utils/constants'
import { useFormFieldsAndStartUser } from '../../node'

const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  }
})
const emit = defineEmits(['update:modelValue'])
const condition = computed({
  get() {
    return props.modelValue
  },
  set(newValue) {
    emit('update:modelValue', newValue)
  }
})
const formType = inject<Ref<number>>('formType') // 表单类型
const conditionConfigTypes = computed(() => {
  return CONDITION_CONFIG_TYPES.filter((item) => {
    // 业务表单暂时去掉条件规则选项
    if (formType?.value === BpmModelFormType.CUSTOM && item.value === ConditionType.RULE) {
      return false
    } else {
      return true
    }
  })
})

/** 条件规则可选择的表单字段 */
const fieldOptions = useFormFieldsAndStartUser()

// 表单校验规则
const formRules = reactive({
  conditionType: [{ required: true, message: '配置方式不能为空', trigger: 'blur' }],
  conditionExpression: [{ required: true, message: '条件表达式不能为空', trigger: 'blur' }]
})
const formRef = ref() // 表单 Ref

/** 切换条件配置方式 */
const changeConditionType = () => {
  if (condition.value.conditionType === ConditionType.RULE) {
    if (!condition.value.conditionGroups) {
      condition.value.conditionGroups = DEFAULT_CONDITION_GROUP_VALUE
    }
  }
}
const deleteConditionGroup = (conditions, index) => {
  conditions.splice(index, 1)
}

const deleteConditionRule = (condition, index) => {
  condition.rules.splice(index, 1)
}

const addConditionRule = (condition, index) => {
  const rule = {
    opCode: '==',
    leftSide: '',
    rightSide: ''
  }
  condition.rules.splice(index + 1, 0, rule)
}

const addConditionGroup = (conditions) => {
  const condition = {
    and: true,
    rules: [
      {
        opCode: '==',
        leftSide: '',
        rightSide: ''
      }
    ]
  }
  conditions.push(condition)
}

const validate = async () => {
  if (!formRef) return false
  return await formRef.value.validate()
}

defineExpose({ validate })
</script>

<style lang="scss" scoped>
.condition-group-tool {
  display: flex;
  justify-content: space-between;
  width: 500px;
  margin-bottom: 20px;
}

.condition-group {
  position: relative;

  &:hover {
    border-color: #0089ff;

    .condition-group-delete {
      opacity: 1;
    }
  }

  .condition-group-delete {
    position: absolute;
    top: 0;
    left: 0;
    display: flex;
    cursor: pointer;
    opacity: 0;
  }
}

::v-deep(.el-card__header) {
  padding: 8px var(--el-card-padding);
  border-bottom: 1px solid var(--el-card-border-color);
  box-sizing: border-box;
}
</style>
