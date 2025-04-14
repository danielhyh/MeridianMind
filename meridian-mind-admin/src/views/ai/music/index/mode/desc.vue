<template>
  <div>
    <Title desc="描述您想要的音乐风格和主题，使用流派和氛围而不是特定的艺术家和歌曲" title="音乐/歌词说明">
      <el-input
        v-model="formData.desc"
        :autosize="{ minRows: 6, maxRows: 6}"
        maxlength="1200"
        placeholder="一首关于糟糕分手的欢快歌曲"
        resize="none"
        show-word-limit
        type="textarea"
      />
    </Title>

    <Title desc="创建一首没有歌词的歌曲" title="纯音乐">
      <template #extra>
        <el-switch v-model="formData.pure" size="small"/>
      </template>
    </Title>

    <Title desc="描述您想要的音乐风格和主题，使用流派和氛围而不是特定的艺术家和歌曲" title="版本">
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

defineOptions({ name: 'Desc' })

const formData = reactive({
  desc: '',
  pure: false,
  version: '3'
})

defineExpose({
  formData
})

</script>
