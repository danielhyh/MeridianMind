<template>
  <ComponentContainerProperty v-model="formData.style">
    <el-form :model="formData" label-width="40px">
      <el-form-item label="文章" prop="id">
        <el-select
          v-model="formData.id"
          :loading="loading"
          :remote-method="queryArticleList"
          class="w-full"
          filterable
          placeholder="请选择文章"
          remote
        >
          <el-option
            v-for="article in articles"
            :key="article.id"
            :label="article.title"
            :value="article.id"
          />
        </el-select>
      </el-form-item>
    </el-form>
  </ComponentContainerProperty>
</template>

<script lang="ts" setup>
import { PromotionArticleProperty } from './config'
import { useVModel } from '@vueuse/core'
import * as ArticleApi from '@/api/mall/promotion/article/index'

// 营销文章属性面板
defineOptions({ name: 'PromotionArticleProperty' })

const props = defineProps<{ modelValue: PromotionArticleProperty }>()
const emit = defineEmits(['update:modelValue'])
const formData = useVModel(props, 'modelValue', emit)
// 文章列表
const articles = ref<ArticleApi.ArticleVO>([])

// 加载中
const loading = ref(false)
// 查询文章列表
const queryArticleList = async (title?: string) => {
  loading.value = true
  const { list } = await ArticleApi.getArticlePage({ title, pageSize: 10 })
  articles.value = list
  loading.value = false
}

// 初始化
onMounted(() => {
  queryArticleList()
})
</script>

<style lang="scss" scoped></style>
