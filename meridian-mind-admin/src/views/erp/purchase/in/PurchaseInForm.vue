<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="1440">
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
          <el-form-item label="入库单号" prop="no">
            <el-input v-model="formData.no" disabled placeholder="保存时自动生成" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="入库时间" prop="inTime">
            <el-date-picker
              v-model="formData.inTime"
              class="!w-1/1"
              placeholder="选择入库时间"
              type="date"
              value-format="x"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="关联订单" prop="orderNo">
            <el-input v-model="formData.orderNo" readonly>
              <template #append>
                <el-button @click="openPurchaseOrderInEnableList">
                  <Icon icon="ep:search" /> 选择
                </el-button>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="供应商" prop="supplierId">
            <el-select
              v-model="formData.supplierId"
              class="!w-1/1"
              clearable
              disabled
              filterable
              placeholder="请选择供应商"
            >
              <el-option
                v-for="item in supplierList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
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
      <!-- 子表的表单 -->
      <ContentWrap>
        <el-tabs v-model="subTabsName" class="-mt-15px -mb-10px">
          <el-tab-pane label="入库产品清单" name="item">
            <PurchaseInItemForm ref="itemFormRef" :disabled="disabled" :items="formData.items" />
          </el-tab-pane>
        </el-tabs>
      </ContentWrap>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="优惠率（%）" prop="discountPercent">
            <el-input-number
              v-model="formData.discountPercent"
              :min="0"
              :precision="2"
              class="!w-1/1"
              controls-position="right"
              placeholder="请输入优惠率"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="付款优惠" prop="discountPrice">
            <el-input
              v-model="formData.discountPrice"
              :formatter="erpPriceInputFormatter"
              disabled
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="优惠后金额">
            <el-input
              :formatter="erpPriceInputFormatter"
              :model-value="formData.totalPrice - formData.otherPrice"
              disabled
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="其它费用" prop="otherPrice">
            <el-input-number
              v-model="formData.otherPrice"
              :min="0"
              :precision="2"
              class="!w-1/1"
              controls-position="right"
              placeholder="请输入其它费用"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="结算账户" prop="accountId">
            <el-select
              v-model="formData.accountId"
              class="!w-1/1"
              clearable
              filterable
              placeholder="请选择结算账户"
            >
              <el-option
                v-for="item in accountList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="应付金额">
            <el-input v-model="formData.totalPrice" :formatter="erpPriceInputFormatter" disabled />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button v-if="!disabled" :disabled="formLoading" type="primary" @click="submitForm">
        确 定
      </el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>

  <!-- 可入库的订单列表 -->
  <PurchaseOrderInEnableList
    ref="purchaseOrderInEnableListRef"
    @success="handlePurchaseOrderChange"
  />
</template>
<script lang="ts" setup>
import { PurchaseInApi, PurchaseInVO } from '@/api/erp/purchase/in'
import PurchaseInItemForm from './components/PurchaseInItemForm.vue'
import { AccountApi, AccountVO } from '@/api/erp/finance/account'
import { erpPriceInputFormatter, erpPriceMultiply } from '@/utils'
import PurchaseOrderInEnableList from '@/views/erp/purchase/order/components/PurchaseOrderInEnableList.vue'
import { PurchaseOrderVO } from '@/api/erp/purchase/order'
import * as UserApi from '@/api/system/user'
import { SupplierApi, SupplierVO } from '@/api/erp/purchase/supplier'

/** ERP 销售入库表单 */
defineOptions({ name: 'PurchaseInForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
const formType = ref('') // 表单的类型：create - 新增；update - 修改；detail - 详情
const formData = ref({
  id: undefined,
  supplierId: undefined,
  accountId: undefined,
  inTime: undefined,
  remark: undefined,
  fileUrl: '',
  discountPercent: 0,
  discountPrice: 0,
  totalPrice: 0,
  otherPrice: 0,
  orderNo: undefined,
  items: [],
  no: undefined // 入库单号，后端返回
})
const formRules = reactive({
  supplierId: [{ required: true, message: '供应商不能为空', trigger: 'blur' }],
  inTime: [{ required: true, message: '入库时间不能为空', trigger: 'blur' }]
})
const disabled = computed(() => formType.value === 'detail')
const formRef = ref() // 表单 Ref
const supplierList = ref<SupplierVO[]>([]) // 供应商列表
const accountList = ref<AccountVO[]>([]) // 账户列表
const userList = ref<UserApi.UserVO[]>([]) // 用户列表

/** 子表的表单 */
const subTabsName = ref('item')
const itemFormRef = ref()

/** 计算 discountPrice、totalPrice 价格 */
watch(
  () => formData.value,
  (val) => {
    if (!val) {
      return
    }
    // 计算
    const totalPrice = val.items.reduce((prev, curr) => prev + curr.totalPrice, 0)
    const discountPrice =
      val.discountPercent != null ? erpPriceMultiply(totalPrice, val.discountPercent / 100.0) : 0
    formData.value.discountPrice = discountPrice
    formData.value.totalPrice = totalPrice - discountPrice + val.otherPrice
  },
  { deep: true }
)

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
      formData.value = await PurchaseInApi.getPurchaseIn(id)
    } finally {
      formLoading.value = false
    }
  }
  // 加载供应商列表
  supplierList.value = await SupplierApi.getSupplierSimpleList()
  // 加载用户列表
  userList.value = await UserApi.getSimpleUserList()
  // 加载账户列表
  accountList.value = await AccountApi.getAccountSimpleList()
  const defaultAccount = accountList.value.find((item) => item.defaultStatus)
  if (defaultAccount) {
    formData.value.accountId = defaultAccount.id
  }
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 打开【可入库的订单列表】弹窗 */
const purchaseOrderInEnableListRef = ref() // 可入库的订单列表 Ref
const openPurchaseOrderInEnableList = () => {
  purchaseOrderInEnableListRef.value.open()
}

const handlePurchaseOrderChange = (order: PurchaseOrderVO) => {
  // 将订单设置到入库单
  formData.value.orderId = order.id
  formData.value.orderNo = order.no
  formData.value.supplierId = order.supplierId
  formData.value.accountId = order.accountId
  formData.value.discountPercent = order.discountPercent
  formData.value.remark = order.remark
  formData.value.fileUrl = order.fileUrl
  // 将订单项设置到入库单项
  order.items.forEach((item) => {
    item.totalCount = item.count
    item.count = item.totalCount - item.inCount
    item.orderItemId = item.id
    item.id = undefined
  })
  formData.value.items = order.items.filter((item) => item.count > 0)
}

/** 提交表单 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitForm = async () => {
  // 校验表单
  await formRef.value.validate()
  await itemFormRef.value.validate()
  // 提交请求
  formLoading.value = true
  try {
    const data = formData.value as unknown as PurchaseInVO
    if (formType.value === 'create') {
      await PurchaseInApi.createPurchaseIn(data)
      message.success(t('common.createSuccess'))
    } else {
      await PurchaseInApi.updatePurchaseIn(data)
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
    supplierId: undefined,
    accountId: undefined,
    inTime: undefined,
    remark: undefined,
    fileUrl: undefined,
    discountPercent: 0,
    discountPrice: 0,
    totalPrice: 0,
    otherPrice: 0,
    items: []
  }
  formRef.value?.resetFields()
}
</script>
