<!--
  - Copyright (C) 2018-2019
  - All rights reserved, Designed By www.joolun.com
  芋道源码：
  ① 移除 avue 组件，使用 ElementUI 原生组件
-->
<template>
  <div class="pb-30px">
    <!-- 类型：image -->
    <div v-if="props.type === 'image'">
      <div v-loading="loading" class="waterfall">
        <div v-for="item in list" :key="item.mediaId" class="waterfall-item">
          <img :src="item.url" class="material-img" />
          <p class="item-name">{{ item.name }}</p>
          <el-row class="ope-row">
            <el-button type="success" @click="selectMaterialFun(item)">
              选择
              <Icon icon="ep:circle-check" />
            </el-button>
          </el-row>
        </div>
      </div>
      <!-- 分页组件 -->
      <Pagination
        v-model:limit="queryParams.pageSize"
        v-model:page="queryParams.pageNo"
        :total="total"
        @pagination="getMaterialPageFun"
      />
    </div>
    <!-- 类型：voice -->
    <div v-else-if="props.type === 'voice'">
      <!-- 列表 -->
      <el-table v-loading="loading" :data="list">
        <el-table-column align="center" label="编号" prop="mediaId" />
        <el-table-column align="center" label="文件名" prop="name" />
        <el-table-column align="center" label="语音">
          <template #default="scope">
            <WxVoicePlayer :url="scope.row.url" />
          </template>
        </el-table-column>
        <el-table-column
          :formatter="dateFormatter"
          align="center"
          label="上传时间"
          prop="createTime"
          width="180"
        />
        <el-table-column align="center" fixed="right" label="操作">
          <template #default="scope">
            <el-button link type="primary" @click="selectMaterialFun(scope.row)"
              >选择
              <Icon icon="ep:plus" />
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页组件 -->
      <Pagination
        v-model:limit="queryParams.pageSize"
        v-model:page="queryParams.pageNo"
        :total="total"
        @pagination="getPage"
      />
    </div>
    <!-- 类型：video -->
    <div v-else-if="props.type === 'video'">
      <!-- 列表 -->
      <el-table v-loading="loading" :data="list">
        <el-table-column align="center" label="编号" prop="mediaId" />
        <el-table-column align="center" label="文件名" prop="name" />
        <el-table-column align="center" label="标题" prop="title" />
        <el-table-column align="center" label="介绍" prop="introduction" />
        <el-table-column align="center" label="视频">
          <template #default="scope">
            <WxVideoPlayer :url="scope.row.url" />
          </template>
        </el-table-column>
        <el-table-column
          :formatter="dateFormatter"
          align="center"
          label="上传时间"
          prop="createTime"
          width="180"
        />
        <el-table-column
          align="center"
          class-name="small-padding fixed-width"
          fixed="right"
          label="操作"
        >
          <template #default="scope">
            <el-button link type="primary" @click="selectMaterialFun(scope.row)"
              >选择
              <Icon icon="akar-icons:circle-plus" />
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页组件 -->
      <Pagination
        v-model:limit="queryParams.pageSize"
        v-model:page="queryParams.pageNo"
        :total="total"
        @pagination="getMaterialPageFun"
      />
    </div>
    <!-- 类型：news -->
    <div v-else-if="props.type === 'news'">
      <div v-loading="loading" class="waterfall">
        <div v-for="item in list" :key="item.mediaId" class="waterfall-item">
          <div v-if="item.content && item.content.newsItem">
            <WxNews :articles="item.content.newsItem" />
            <el-row class="ope-row">
              <el-button type="success" @click="selectMaterialFun(item)">
                选择
                <Icon icon="ep:circle-check" />
              </el-button>
            </el-row>
          </div>
        </div>
      </div>
      <!-- 分页组件 -->
      <Pagination
        v-model:limit="queryParams.pageSize"
        v-model:page="queryParams.pageNo"
        :total="total"
        @pagination="getMaterialPageFun"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import WxNews from '@/views/mp/components/wx-news'
import WxVoicePlayer from '@/views/mp/components/wx-voice-play'
import WxVideoPlayer from '@/views/mp/components/wx-video-play'
import { NewsType } from './types'
import * as MpMaterialApi from '@/api/mp/material'
import * as MpFreePublishApi from '@/api/mp/freePublish'
import * as MpDraftApi from '@/api/mp/draft'
import { dateFormatter } from '@/utils/formatTime'

defineOptions({ name: 'WxMaterialSelect' })

const props = withDefaults(
  defineProps<{
    type: string
    accountId: number
    newsType?: NewsType
  }>(),
  {
    newsType: NewsType.Published
  }
)

const emit = defineEmits(['select-material'])

// 遮罩层
const loading = ref(false)
// 总条数
const total = ref(0)
// 数据列表
const list = ref<any[]>([])
// 查询参数
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  accountId: props.accountId
})

const selectMaterialFun = (item) => {
  emit('select-material', item)
}

const getPage = async () => {
  loading.value = true
  try {
    if (props.type === 'news' && props.newsType === NewsType.Published) {
      // 【图文】+ 【已发布】
      await getFreePublishPageFun()
    } else if (props.type === 'news' && props.newsType === NewsType.Draft) {
      // 【图文】+ 【草稿】
      await getDraftPageFun()
    } else {
      // 【素材】
      await getMaterialPageFun()
    }
  } finally {
    loading.value = false
  }
}

const getMaterialPageFun = async () => {
  const data = await MpMaterialApi.getMaterialPage({
    ...queryParams,
    type: props.type
  })
  list.value = data.list
  total.value = data.total
}

const getFreePublishPageFun = async () => {
  const data = await MpFreePublishApi.getFreePublishPage(queryParams)
  data.list.forEach((item: any) => {
    const articles = item.content.newsItem
    articles.forEach((article: any) => {
      article.picUrl = article.thumbUrl
    })
  })
  list.value = data.list
  total.value = data.total
}

const getDraftPageFun = async () => {
  const data = await MpDraftApi.getDraftPage(queryParams)
  data.list.forEach((draft: any) => {
    const articles = draft.content.newsItem
    articles.forEach((article: any) => {
      article.picUrl = article.thumbUrl
    })
  })
  list.value = data.list
  total.value = data.total
}

onMounted(async () => {
  getPage()
})
</script>
<style lang="scss" scoped>
@media (width >= 992px) and (width <= 1300px) {
  .waterfall {
    column-count: 3;
  }

  p {
    color: red;
  }
}

@media (width >= 768px) and (width <= 991px) {
  .waterfall {
    column-count: 2;
  }

  p {
    color: orange;
  }
}

@media (width <= 767px) {
  .waterfall {
    column-count: 1;
  }
}

.waterfall {
  width: 100%;
  column-gap: 10px;
  column-count: 5;
  margin: 0 auto;
}

.waterfall-item {
  padding: 10px;
  margin-bottom: 10px;
  break-inside: avoid;
  border: 1px solid #eaeaea;
}

.material-img {
  width: 100%;
}

p {
  line-height: 30px;
}
</style>
