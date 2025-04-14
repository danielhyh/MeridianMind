<template>
  <div class="">
    <Title desc="自己编写歌词或使用Ai生成歌词，两节/8行效果最佳" title="歌词">
      <el-input
        v-model="formData.lyric"
        :autosize="{ minRows: 6, maxRows: 6}"
        maxlength="1200"
        placeholder="请输入您自己的歌词"
        resize="none"
        show-word-limit
        type="textarea"
      />
    </Title>

    <Title title="音乐风格">
      <el-space class="flex-wrap">
        <el-tag v-for="tag in tags" :key="tag" class="mb-8px" round>{{tag}}</el-tag>
      </el-space>

      <el-button
        :type="showCustom ? 'primary': 'default'" 
        class="mb-6px"
        round
        size="small"
        @click="showCustom = !showCustom"
      >自定义风格
      </el-button>
    </Title>

    <Title v-show="showCustom" class="-mt-12px" desc="描述您想要的音乐风格，Suno无法识别艺术家的名字，但可以理解流派和氛围">
      <el-input
        v-model="formData.style"
        :autosize="{ minRows: 4, maxRows: 4}"
        maxlength="256"
        placeholder="输入音乐风格(英文)"
        resize="none"
        show-word-limit
        type="textarea"
      />
    </Title>

    <Title title="音乐/歌曲名称">
      <el-input v-model="formData.name" placeholder="请输入音乐/歌曲名称"/>
    </Title>

    <Title title="版本">
      <el-select v-model="formData.version" placeholder="请选择">
        <el-option
          v-for="item in [{
            value: '3',
            label: 'V3'
          }, {
            value: '2',
            label: 'V2'
          }]"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </Title>
  </div>
</template>

<script lang="ts" setup>
import Title from '../title/index.vue'
defineOptions({ name: 'Lyric' })

const tags = ['rock', 'punk', 'jazz', 'soul', 'country', 'kidsmusic', 'pop']

const showCustom = ref(false)

const formData = reactive({
  lyric: '',
  style: '',
  name: '',
  version: ''
})

defineExpose({
  formData
})
</script>
