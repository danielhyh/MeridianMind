<template>
  <doc-alert title="会员等级、积分、签到" url="https://doc.iocoder.cn/member/level/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-button
      v-hasPermi="['point:sign-in-config:create']"
      plain
      type="primary"
      @click="openForm('create')"
    >
      <Icon class="mr-5px" icon="ep:plus" /> 新增
    </el-button>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column
        :formatter="(_, __, cellValue) => ['第', cellValue, '天'].join(' ')"
        align="center"
        label="签到天数"
        prop="day"
      />
      <el-table-column align="center" label="奖励积分" prop="point" />
      <el-table-column align="center" label="奖励经验" prop="experience" />
      <el-table-column align="center" label="状态" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作">
        <template #default="scope">
          <el-button
            v-hasPermi="['point:sign-in-config:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            编辑
          </el-button>
          <el-button
            v-hasPermi="['point:sign-in-config:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <SignInConfigForm ref="formRef" @success="getList" />
</template>
<script lang="ts" setup>
import * as SignInConfigApi from '@/api/member/signin/config'
import SignInConfigForm from './SignInConfigForm.vue'
import { DICT_TYPE } from '@/utils/dict'

defineOptions({ name: 'SignInConfig' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref([]) // 列表的数据

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await SignInConfigApi.getSignInConfigList()
    console.log(data)
    list.value = data
  } finally {
    loading.value = false
  }
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await SignInConfigApi.deleteSignInConfig(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
