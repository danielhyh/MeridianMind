<template>
  <div v-loading="props.loading" class="waterfall">
    <template v-for="item in props.list" :key="item.articleId">
      <div v-if="item.content && item.content.newsItem" class="waterfall-item">
        <WxNews :articles="item.content.newsItem" />
        <!-- 操作按钮 -->
        <el-row>
          <el-button
            v-hasPermi="['mp:free-publish:submit']"
            circle
            type="success"
            @click="emit('publish', item)"
          >
            <Icon icon="fa:upload" />
          </el-button>
          <el-button
            v-hasPermi="['mp:draft:update']"
            circle
            type="primary"
            @click="emit('update', item)"
          >
            <Icon icon="ep:edit" />
          </el-button>
          <el-button
            v-hasPermi="['mp:draft:delete']"
            circle
            type="danger"
            @click="emit('delete', item)"
          >
            <Icon icon="ep:delete" />
          </el-button>
        </el-row>
      </div>
    </template>
  </div>
</template>

<script lang="ts" setup>
import WxNews from '@/views/mp/components/wx-news'

import { Article } from './types'

const props = defineProps<{
  list: Article[]
  loading: boolean
}>()

const emit = defineEmits<{
  (e: 'publish', v: Article)
  (e: 'update', v: Article)
  (e: 'delete', v: Article)
}>()
</script>

<style lang="scss" scoped>
.waterfall {
  width: 100%;
  column-gap: 10px;
  column-count: 5;
  margin: 0 auto;

  .waterfall-item {
    padding: 10px;
    margin-bottom: 10px;
    break-inside: avoid;
    border: 1px solid #eaeaea;
  }
}

@media (width >= 992px) and (width <= 1300px) {
  .waterfall {
    column-count: 3;
  }
}

@media (width >= 768px) and (width <= 991px) {
  .waterfall {
    column-count: 2;
  }
}

@media (width <= 767px) {
  .waterfall {
    column-count: 1;
  }
}
</style>
