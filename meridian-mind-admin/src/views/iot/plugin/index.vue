<!-- TODO @haohao：搞到 config 目录，会不会更好哈 -->
<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="68px"
    >
      <el-form-item label="插件名称" prop="name">
        <el-input
          v-model="queryParams.name"
          class="!w-240px"
          clearable
          placeholder="请输入插件名称"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          class="!w-240px"
          clearable
          placeholder="请选择状态"
          @change="handleQuery"
        >
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.IOT_PLUGIN_STATUS)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item class="float-right !mr-0 !mb-0">
        <el-button-group>
          <el-button :type="viewMode === 'card' ? 'primary' : 'default'" @click="viewMode = 'card'">
            <Icon icon="ep:grid" />
          </el-button>
          <el-button :type="viewMode === 'list' ? 'primary' : 'default'" @click="viewMode = 'list'">
            <Icon icon="ep:list" />
          </el-button>
        </el-button-group>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon class="mr-5px" icon="ep:search" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon class="mr-5px" icon="ep:refresh" /> 重置</el-button>
        <el-button
          v-hasPermi="['iot:plugin-config:create']"
          plain
          type="primary"
          @click="openForm('create')"
        >
          <Icon class="mr-5px" icon="ep:plus" /> 新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
    <template v-if="viewMode === 'list'">
      <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true">
        <el-table-column align="center" label="插件名称" prop="name" />
        <el-table-column align="center" label="插件标识" prop="pluginKey" />
        <el-table-column align="center" label="jar 包" prop="fileName" />
        <el-table-column align="center" label="版本号" prop="version" />
        <el-table-column align="center" label="部署方式" prop="deployType">
          <template #default="scope">
            <dict-tag :type="DICT_TYPE.IOT_PLUGIN_DEPLOY_TYPE" :value="scope.row.deployType" />
          </template>
        </el-table-column>
        <el-table-column align="center" label="状态" prop="status">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row.id, Number($event))"
            />
          </template>
        </el-table-column>
        <el-table-column
          :formatter="dateFormatter"
          align="center"
          label="创建时间"
          prop="createTime"
          width="180px"
        />
        <el-table-column align="center" label="操作" min-width="120px">
          <template #default="scope">
            <el-button
              v-hasPermi="['iot:product:query']"
              link
              type="primary"
              @click="openDetail(scope.row.id)"
            >
              查看
            </el-button>
            <el-button
              v-hasPermi="['iot:plugin-config:update']"
              link
              type="primary"
              @click="openForm('update', scope.row.id)"
            >
              编辑
            </el-button>
            <el-button
              v-hasPermi="['iot:plugin-config:delete']"
              link
              type="danger"
              @click="handleDelete(scope.row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>
    <template v-if="viewMode === 'card'">
      <el-row :gutter="16">
        <el-col v-for="item in list" :key="item.id" :lg="6" :md="12" :sm="12" :xs="24" class="mb-4">
          <el-card
            :body-style="{ padding: '0' }"
            class="h-full transition-colors relative overflow-hidden"
          >
            <div class="p-4 relative">
              <!-- 标题区域 -->
              <div class="flex items-center mb-3">
                <div class="mr-2.5 flex items-center">
                  <el-image :src="defaultIconUrl" class="w-[18px] h-[18px]" />
                </div>
                <div class="text-[16px] font-600 flex-1">{{ item.name }}</div>
                <!-- 添加插件状态标签 -->
                <div class="inline-flex items-center">
                  <div
                    :class="
                      item.status === 1
                        ? 'bg-[var(--el-color-success)]'
                        : 'bg-[var(--el-color-danger)]'
                    "
                    class="w-1 h-1 rounded-full mr-1.5"
                  >
                  </div>
                  <el-text
                    :type="item.status === 1 ? 'success' : 'danger'"
                    class="!text-xs font-bold"
                  >
                    {{ item.status === 1 ? '开启' : '禁用' }}
                  </el-text>
                </div>
              </div>

              <!-- 信息区域 -->
              <div class="flex items-center text-[14px]">
                <div class="flex-1">
                  <div class="mb-2.5 last:mb-0">
                    <span class="text-[#717c8e] mr-2.5">插件标识</span>
                    <span class="text-[#0b1d30] whitespace-normal break-all">
                      {{ item.pluginKey }}
                    </span>
                  </div>
                  <div class="mb-2.5 last:mb-0">
                    <span class="text-[#717c8e] mr-2.5">jar 包</span>
                    <span class="text-[#0b1d30]">{{ item.fileName }}</span>
                  </div>
                  <div class="mb-2.5 last:mb-0">
                    <span class="text-[#717c8e] mr-2.5">版本号</span>
                    <span class="text-[#0b1d30]">{{ item.version }}</span>
                  </div>
                  <div class="mb-2.5 last:mb-0">
                    <span class="text-[#717c8e] mr-2.5">部署方式</span>
                    <dict-tag :type="DICT_TYPE.IOT_PLUGIN_DEPLOY_TYPE" :value="item.deployType" />
                  </div>
                </div>
              </div>

              <!-- 分隔线 -->
              <el-divider class="!my-3" />

              <!-- 按钮 -->
              <div class="flex items-center px-0">
                <el-button
                  v-hasPermi="['iot:plugin-config:update']"
                  class="flex-1 !px-2 !h-[32px] text-[13px]"
                  plain
                  type="primary"
                  @click="openForm('update', item.id)"
                >
                  <Icon class="mr-1" icon="ep:edit-pen" />
                  编辑
                </el-button>
                <el-button
                  class="flex-1 !px-2 !h-[32px] !ml-[10px] text-[13px]"
                  plain
                  type="warning"
                  @click="openDetail(item.id)"
                >
                  <Icon class="mr-1" icon="ep:view" />
                  详情
                </el-button>
                <div class="mx-[10px] h-[20px] w-[1px] bg-[#dcdfe6]"></div>
                <el-button
                  v-hasPermi="['iot:device:delete']"
                  class="!px-2 !h-[32px] text-[13px]"
                  plain
                  type="danger"
                  @click="handleDelete(item.id)"
                >
                  <Icon icon="ep:delete" />
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <!-- 分页 -->
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <PluginConfigForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import { PluginConfigApi, PluginConfigVO } from '@/api/iot/plugin'
import PluginConfigForm from './PluginConfigForm.vue'

/** IoT 插件配置 列表 */
defineOptions({ name: 'IoTPlugin' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(true) // 列表的加载中
const list = ref<PluginConfigVO[]>([]) // 列表的数据
const total = ref(0) // 列表的总页数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: undefined,
  status: undefined
})
const queryFormRef = ref() // 搜索的表单
const defaultIconUrl = ref('/src/assets/svgs/iot/card-fill.svg') // 默认插件图标
const viewMode = ref<'card' | 'list'>('card') // 视图模式状态

/** 查询列表 */
const getList = async () => {
  loading.value = true
  try {
    const data = await PluginConfigApi.getPluginConfigPage(queryParams)
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

/** 打开详情 */
const { push } = useRouter()
const openDetail = (id: number) => {
  push({ name: 'IoTPluginDetail', params: { id } })
}

/** 删除按钮操作 */
const handleDelete = async (id: number) => {
  try {
    // 删除的二次确认
    await message.delConfirm()
    // 发起删除
    await PluginConfigApi.deletePluginConfig(id)
    message.success(t('common.delSuccess'))
    // 刷新列表
    await getList()
  } catch {}
}

/** 处理状态变更 */
const handleStatusChange = async (id: number, status: number) => {
  try {
    // 修改状态的二次确认
    const text = status === 1 ? '启用' : '停用'
    await message.confirm('确认要"' + text + '"插件吗?')
    await PluginConfigApi.updatePluginStatus({
      id: id,
      status
    })
    message.success('更新状态成功')
    getList()
  } catch (error) {
    message.error('更新状态失败')
  }
}

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>
