<template>
  <doc-alert title="【营销】优惠劵" url="https://doc.iocoder.cn/mall/promotion-coupon/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="会员昵称" prop="nickname">
        <el-input
          v-model="queryParams.nickname"
          class="!w-240px"
          clearable
          placeholder="请输入会员昵称"
          @keyup="handleQuery"
        />
      </el-form-item>
      <el-form-item label="领取时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
          end-placeholder="结束日期"
          start-placeholder="开始日期"
          type="daterange"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"> <Icon class="mr-5px" icon="ep:search" />搜索 </el-button>
        <el-button @click="resetQuery"> <Icon class="mr-5px" icon="ep:refresh" />重置 </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <!-- Tab 选项：真正的内容在 Lab -->
    <el-tabs v-model="activeTab" type="card" @tab-change="onTabChange">
      <el-tab-pane
        v-for="tab in statusTabs"
        :key="tab.value"
        :label="tab.label"
        :name="tab.value"
      />
    </el-tabs>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column align="center" label="会员昵称" min-width="100" prop="nickname" />
      <el-table-column align="center" label="优惠券名称" min-width="140" prop="name" />
      <el-table-column align="center" label="类型" prop="discountType">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PROMOTION_PRODUCT_SCOPE" :value="scope.row.productScope" />
        </template>
      </el-table-column>
      <el-table-column label="优惠" min-width="100" prop="discount">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PROMOTION_DISCOUNT_TYPE" :value="scope.row.discountType" />
          {{ discountFormat(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="领取方式" prop="takeType">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PROMOTION_COUPON_TAKE_TYPE" :value="scope.row.takeType" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.PROMOTION_COUPON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="领取时间"
        prop="createTime"
        width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="使用时间"
        prop="useTime"
        width="180"
      />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template #default="scope">
          <el-button
            v-hasPermi="['promotion:coupon:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            回收
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
</template>

<script lang="ts" name="PromotionCoupon" setup>
import { deleteCoupon, getCouponPage } from '@/api/mall/promotion/coupon/coupon'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { discountFormat } from '@/views/mall/promotion/coupon/formatter'

defineOptions({ name: 'PromotionCoupon' })

const message = useMessage() // 消息弹窗

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 字典表格数据
// 查询参数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  createTime: [],
  status: undefined,
  nickname: undefined
})
const queryFormRef = ref() // 搜索的表单

const activeTab = ref('all') // Tab 筛选
const statusTabs = reactive([
  {
    label: '全部',
    value: 'all'
  }
])

/** 查询列表 */
const getList = async () => {
  loading.value = true
  // 执行查询
  try {
    const data = await getCouponPage(queryParams)
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
  queryFormRef.value?.resetFields()
  handleQuery()
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 二次确认
    await message.confirm(
      '回收将会收回会员领取的待使用的优惠券，已使用的将无法回收，确定要回收所选优惠券吗？'
    )
    // 发起删除
    await deleteCoupon(id)
    message.notifySuccess('回收成功')
    // 重新加载列表
    await getList()
  } catch {}
}

/** tab 切换 */
const onTabChange = (tabName) => {
  queryParams.status = tabName === 'all' ? undefined : tabName
  getList()
}

/** 初始化 **/
onMounted(() => {
  getList()
  // 设置 statuses 过滤
  for (const dict of getIntDictOptions(DICT_TYPE.PROMOTION_COUPON_STATUS)) {
    statusTabs.push({
      label: dict.label,
      value: dict.value as string
    })
  }
})
</script>
