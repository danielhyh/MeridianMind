<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="1080">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :disabled="disabled"
      :model="formData"
      :rules="formRules"
      label-width="100px"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="调度单号" prop="no">
            <el-input v-model="formData.no" disabled placeholder="保存时自动生成" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="调度时间" prop="moveTime">
            <el-date-picker
              v-model="formData.moveTime"
              class="!w-1/1"
              placeholder="选择调度时间"
              type="date"
              value-format="x"
            />
          </el-form-item>
        </el-col>
        <el-col :span="16">
          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="formData.remark"
              :rows="1"
              placeholder="请输入备注"
              type="textarea"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="附件" prop="fileUrl">
            <UploadFile v-model="formData.fileUrl" :is-show-tip="false" :limit="1" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <!-- 子表的表单 -->
    <ContentWrap>
      <el-tabs v-model="subTabsName" class="-mt-15px -mb-10px">
        <el-tab-pane label="调度产品清单" name="item">
          <StockMoveItemForm ref="itemFormRef" :disabled="disabled" :items="formData.items" />
        </el-tab-pane>
      </el-tabs>
    </ContentWrap>
    <template #footer>
      <el-button v-if="!disabled" :disabled="formLoading" type="primary" @click="submitForm">
        确 定
      </el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { StockMoveApi, StockMoveVO } from '@/api/erp/stock/move'
import StockMoveItemForm from './components/StockMoveItemForm.vue'

/** ERP 库存调度单表单 */
defineOptions({ name: 'StockMoveForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改；detail - 详情
const formData = ref({
  id: undefined,
  customerId: undefined,
  moveTime: undefined,
  remark: undefined,
  fileUrl: '',
  items: []
})
const formRules = reactive({
  moveTime: [{ required: true, message: '调度时间不能为空', trigger: 'blur' }]
})
const disabled = computed(() => formType.value === 'detail')
const formRef = ref() // 表单 Ref

/** 子表的表单 */
const subTabsName = ref('item')
const itemFormRef = ref()

/** 打开弹窗 */
const open = async (type: string, id?: number) => {
  dialogVisible.value = true
  dialogTitle.value = t('action.' + type)
  formType.value = type
  resetForm()
  // 修改时，设置数据
  if (id) {
    formLoading.value = true
    try {
      formData.value = await StockMoveApi.getStockMove(id)
    } finally {
      formLoading.value = false
    }
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  await itemFormRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as StockMoveVO
    if (formType.value === 'create') {
      await StockMoveApi.createStockMove(data)
      message.success(t('common.createSuccess'))
    } else {
      await StockMoveApi.updateStockMove(data)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
const resetForm = () => {
  formData.value = {
    id: undefined,
    customerId: undefined,
    moveTime: undefined,
    remark: undefined,
    fileUrl: undefined,
    items: []
  }
  formRef.value?.resetFields()
}
</script>
