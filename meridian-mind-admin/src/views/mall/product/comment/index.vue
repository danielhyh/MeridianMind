<template>
  <doc-alert title="【商品】商品评价" url="https://doc.iocoder.cn/mall/product-comment/" />

  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="回复状态" prop="replyStatus">
        <el-select v-model="queryParams.replyStatus" class="!w-240px">
          <el-option :value="true" label="已回复" />
          <el-option :value="false" label="未回复" />
        </el-select>
      </el-form-item>
      <el-form-item label="商品名称" prop="spuName">
        <el-input
          v-model="queryParams.spuName"
          class="!w-240px"
          placeholder="请输入商品名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户名称" prop="userNickname">
        <el-input
          v-model="queryParams.userNickname"
          class="!w-240px"
          placeholder="请输入用户名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单编号" prop="orderId">
        <el-input
          v-model="queryParams.orderId"
          class="!w-240px"
          placeholder="请输入订单编号"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="评论时间" prop="createTime">
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
        <el-button @click="handleQuery">
          <Icon class="mr-5px" icon="ep:search" />
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon class="mr-5px" icon="ep:refresh" />
          重置
        </el-button>
        <el-button
          v-hasPermi="['product:comment:create']"
          plain
          type="primary"
          @click="openForm('create')"
        >
          <Icon class="mr-5px" icon="ep:plus" />
          添加虚拟评论
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="false" :stripe="true">
      <el-table-column align="center" label="评论编号" min-width="80" prop="id" />
      <el-table-column align="center" label="商品信息" min-width="400">
        <template #default="scope">
          <div class="row flex items-center gap-x-4px">
            <el-image
              v-if="scope.row.skuPicUrl"
              :preview-src-list="[scope.row.skuPicUrl]"
              :src="scope.row.skuPicUrl"
              class="h-40px w-40px shrink-0"
              preview-teleported
            />
            <div>{{ scope.row.spuName }}</div>
            <el-tag
              v-for="property in scope.row.skuProperties"
              :key="property.propertyId"
              class="mr-10px"
            >
              {{ property.propertyName }}: {{ property.valueName }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column align="center" label="用户名称" prop="userNickname" width="100" />
      <el-table-column align="center" label="商品评分" prop="descriptionScores" width="90" />
      <el-table-column align="center" label="服务评分" prop="benefitScores" width="90" />
      <el-table-column align="center" label="评论内容" min-width="210" prop="content">
        <template #default="scope">
          <p>{{ scope.row.content }}</p>
          <div class="flex justify-center gap-x-4px">
            <el-image
              v-for="(picUrl, index) in scope.row.picUrls"
              :key="index"
              :initial-index="index"
              :preview-src-list="scope.row.picUrls"
              :src="picUrl"
              class="h-40px w-40px"
              preview-teleported
            />
          </div>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        label="回复内容"
        min-width="250"
        prop="replyContent"
        show-overflow-tooltip
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="评论时间"
        prop="createTime"
        width="180"
      />
      <el-table-column align="center" label="是否展示" width="80px">
        <template #default="scope">
          <el-switch
            v-model="scope.row.visible"
            v-hasPermi="['product:comment:update']"
            :active-value="true"
            :inactive-value="false"
            @change="handleVisibleChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" min-width="60px">
        <template #default="scope">
          <el-button
            v-hasPermi="['product:comment:update']"
            link
            type="primary"
            @click="handleReply(scope.row.id)"
          >
            回复
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

  <!-- 表单弹窗：添加/修改 -->
  <CommentForm ref="formRef" @success="getList" />
  <!-- 回复表单弹窗 -->
  <ReplyForm ref="replyFormRef" @success="getList" />
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as CommentApi from '@/api/mall/product/comment'
import CommentForm from './CommentForm.vue'
import ReplyForm from './ReplyForm.vue'

defineOptions({ name: 'ProductComment' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const total = ref(0) // 列表的总页数
const list = ref([]) // 列表的数据
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  replyStatus: null,
  spuName: null,
  userNickname: null,
  orderId: null,
  createTime: []
})
const queryFormRef = ref() // 搜索的表单

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await CommentApi.getCommentPage(queryParams)
    // visible 如果为 null，会导致刷新的时候触发 e-switch 的 change 事件
    data.list.forEach((item) => {
      if (!item.visible) {
        item.visible = false
      }
    })
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

/** 回复按钮操作 **/
const replyFormRef = ref()
const handleReply = (id: number) => {
  replyFormRef.value.open(id)
}

/** 显示/隐藏 **/
const handleVisibleChange = async (row: CommentApi.CommentVO) => {
  if (loading.value) {
    return
  }
  let changedValue = row.visible
  try {
    await message.confirm(changedValue ? '是否显示评论？' : '是否隐藏评论？')
    await CommentApi.updateCommentVisible({ id: row.id, visible: changedValue })
    await getList()
  } catch {
    row.visible = !changedValue
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
