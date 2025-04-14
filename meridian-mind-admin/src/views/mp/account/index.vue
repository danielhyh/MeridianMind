<template>
  <doc-alert title="公众号接入" url="https://doc.iocoder.cn/mp/account/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          class="!w-240px"
          clearable
          placeholder="请输入名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" />搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" />重置</el-button>
        <el-button v-hasPermi="['mp:account:create']" type="primary" @click="openForm('create')">
          <Icon class="mr-5px" icon="ep:plus" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column align="center" label="名称" prop="name" />
      <el-table-column align="center" label="微信号" prop="account" width="180" />
      <el-table-column align="center" label="appId" prop="appId" width="180" />
      <el-table-column align="center" label="服务器地址(URL)" prop="appId" width="360">
        <template #default="scope">
          {{ 'http://服务端地址/admin-api/mp/open/' + scope.row.appId }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="二维码" prop="qrCodeUrl">
        <template #default="scope">
          <img
            v-if="scope.row.qrCodeUrl"
            :src="scope.row.qrCodeUrl"
            alt="二维码"
            style="display: inline-block; height: 100px"
          />
          <el-button
            v-hasPermi="['mp:account:qr-code']"
            link
            type="primary"
            @click="handleGenerateQrCode(scope.row)"
          >
            生成二维码
          </el-button>
        </template>
      </el-table-column>
      <el-table-column align="center" label="备注" prop="remark" />
      <el-table-column align="center" label="操作">
        <template #default="scope">
          <el-button
            v-hasPermi="['mp:account:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-hasPermi="['mp:account:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            删除
          </el-button>
          <el-button
            v-hasPermi="['mp:account:clear-quota']"
            link
            type="danger"
            @click="handleCleanQuota(scope.row)"
          >
            清空 API 配额
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 对话框(添加 / 修改) -->
  <AccountForm ref="formRef" @success="getList" />
</template>
<script lang="ts" setup>
import * as AccountApi from '@/api/mp/account'
import AccountForm from './AccountForm.vue'

defineOptions({ name: 'MpAccount' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: null,
  account: null,
  appId: null
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await AccountApi.getAccountPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await AccountApi.deleteAccount(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 生成二维码的按钮操作 */
const handleGenerateQrCode = async (row) => {
  try {
    // 生成二维码的二次确认
    await message.confirm('是否确认生成公众号账号编号为"' + row.name + '"的二维码?')
    // 发起生成二维码
    await AccountApi.generateAccountQrCode(row.id)
    message.success('生成二维码成功')
    // 刷新列表
    await getList()
  } catch {}
}

/** 清空二维码 API 配额的按钮操作 */
const handleCleanQuota = async (row) => {
  try {
    // 清空 API 配额的二次确认
    await message.confirm('是否确认清空生成公众号账号编号为"' + row.name + '"的 API 配额?')
    // 发起清空 API 配额
    await AccountApi.clearAccountQuota(row.id)
    message.success('清空 API 配额成功')
  } catch {}
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
