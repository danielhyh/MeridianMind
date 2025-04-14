<template>
  <el-form-item>
    <el-alert
      :closable="false"
      show-icon
      title="仅支持 POST 请求，以请求体方式接收参数"
      type="warning"
    />
  </el-form-item>
  <!-- 请求地址-->
  <el-form-item
    :prop="`${formItemPrefix}.url`"
    :rules="{
      required: true,
      message: '请求地址不能为空',
      trigger: 'blur'
    }"
    label="请求地址"
    label-position="top"
  >
    <el-input v-model="setting.url" />
  </el-form-item>
  <!-- 请求头，请求体设置-->
  <HttpRequestParamSetting :bind="formItemPrefix" :body="setting.body" :header="setting.header" />
  <!-- 返回值设置-->
  <div v-if="responseEnable">
    <el-form-item label="返回值" label-position="top">
      <el-alert
        :closable="false"
        show-icon
        title="通过请求返回值, 可以修改流程表单的值"
        type="warning"
      />
    </el-form-item>
    <el-form-item>
      <div v-for="(item, index) in setting.response" :key="index" class="flex pt-2">
        <div class="mr-2">
          <el-form-item
            :prop="`${formItemPrefix}.response.${index}.key`"
            :rules="{
              required: true,
              message: '表单字段不能为空',
              trigger: 'blur'
            }"
          >
            <el-select v-model="item.key" class="w-160px!" placeholder="请选择表单字段">
              <el-option
                v-for="(field, fIdx) in formFields"
                :key="fIdx"
                :disabled="!field.required"
                :label="field.title"
                :value="field.field"
              />
            </el-select>
          </el-form-item>
        </div>
        <div class="mr-2">
          <el-form-item
            :prop="`${formItemPrefix}.response.${index}.value`"
            :rules="{
              required: true,
              message: '请求返回字段不能为空',
              trigger: 'blur'
            }"
          >
            <el-input v-model="item.value" class="w-160px" placeholder="请求返回字段" />
          </el-form-item>
        </div>
        <div class="mr-1 pt-1 cursor-pointer">
          <Icon
            :size="18"
            icon="ep:delete"
            @click="deleteHttpResponseSetting(setting.response!, index)"
          />
        </div>
      </div>
      <el-button text type="primary" @click="addHttpResponseSetting(setting.response!)">
        <Icon class="mr-5px" icon="ep:plus" />添加一行
      </el-button>
    </el-form-item>
  </div>
</template>
<script lang="ts" setup>
import HttpRequestParamSetting from './HttpRequestParamSetting.vue'
import { useFormFields } from '../../node'

const props = defineProps({
  setting: {
    type: Object,
    required: true
  },
  responseEnable: {
    type: Boolean,
    required: true
  },
  formItemPrefix: {
    type: String,
    required: true
  }
})
const { setting } = toRefs(props)
const emits = defineEmits(['update:setting'])
watch(
  () => setting,
  (val) => {
    emits('update:setting', val)
  }
)

/** 流程表单字段 */
const formFields = useFormFields()

/** 添加 HTTP 请求返回值设置项 */
const addHttpResponseSetting = (responseSetting: Record<string, string>[]) => {
  responseSetting.push({
    key: '',
    value: ''
  })
}

/** 删除 HTTP 请求返回值设置项 */
const deleteHttpResponseSetting = (responseSetting: Record<string, string>[], index: number) => {
  responseSetting.splice(index, 1)
}
</script>

<style lang="scss" scoped></style>
