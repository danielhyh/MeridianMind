<template>
  <el-card body-class="" class="image-card">
    <div class="image-operation">
      <div>
        <el-button v-if="detail?.status === AiImageStatusEnum.IN_PROGRESS" bg text type="primary">
          生成中
        </el-button>
        <el-button v-else-if="detail?.status === AiImageStatusEnum.SUCCESS" bg text>
          已完成
        </el-button>
        <el-button v-else-if="detail?.status === AiImageStatusEnum.FAIL" bg text type="danger">
          异常
        </el-button>
      </div>
      <!-- 操作区 -->
      <div>
        <el-button
          :icon="Download"
          class="btn"
          text
          @click="handleButtonClick('download', detail)"
        />
        <el-button
          :icon="RefreshRight"
          class="btn"
          text
          @click="handleButtonClick('regeneration', detail)"
        />
        <el-button :icon="Delete" class="btn" text @click="handleButtonClick('delete', detail)" />
        <el-button :icon="More" class="btn" text @click="handleButtonClick('more', detail)" />
      </div>
    </div>
    <div ref="cardImageRef" class="image-wrapper">
      <el-image
        :preview-src-list="[detail.picUrl]"
        :src="detail?.picUrl"
        class="image"
        preview-teleported
      />
      <div v-if="detail?.status === AiImageStatusEnum.FAIL">
        {{ detail?.errorMessage }}
      </div>
    </div>
    <!-- Midjourney 专属操作 -->
    <div class="image-mj-btns">
      <el-button
        v-for="button in detail?.buttons"
        :key="button"
        class="min-w-40px ml-0 mr-10px mt-5px"
        size="small"
        @click="handleMidjourneyBtnClick(button)"
      >
        {{ button.label }}{{ button.emoji }}
      </el-button>
    </div>
  </el-card>
</template>
<script lang="ts" setup>
import { Delete, Download, More, RefreshRight } from '@element-plus/icons-vue'
import { ImageVO, ImageMidjourneyButtonsVO } from '@/api/ai/image'
import { PropType } from 'vue'
import { ElLoading, LoadingOptionsResolved } from 'element-plus'
import { AiImageStatusEnum } from '@/views/ai/utils/constants'

const message = useMessage() // 消息

const props = defineProps({
  detail: {
    type: Object as PropType<ImageVO>,
    require: true
  }
})

const cardImageRef = ref<any>() // 卡片 image ref
const cardImageLoadingInstance = ref<any>() // 卡片 image ref

/** 处理点击事件  */
const handleButtonClick = async (type, detail: ImageVO) => {
  emits('onBtnClick', type, detail)
}

/** 处理 Midjourney 按钮点击事件  */
const handleMidjourneyBtnClick = async (button: ImageMidjourneyButtonsVO) => {
  // 确认窗体
  await message.confirm(`确认操作 "${button.label} ${button.emoji}" ?`)
  emits('onMjBtnClick', button, props.detail)
}

const emits = defineEmits(['onBtnClick', 'onMjBtnClick']) // emits

/** 监听详情 */
const { detail } = toRefs(props)
watch(detail, async (newVal, oldVal) => {
  await handleLoading(newVal.status as string)
})

/** 处理加载状态 */
const handleLoading = async (status: number) => {
  // 情况一：如果是生成中，则设置加载中的 loading
  if (status === AiImageStatusEnum.IN_PROGRESS) {
    cardImageLoadingInstance.value = ElLoading.service({
      target: cardImageRef.value,
      text: '生成中...'
    } as LoadingOptionsResolved)
    // 情况二：如果已经生成结束，则移除 loading
  } else {
    if (cardImageLoadingInstance.value) {
      cardImageLoadingInstance.value.close()
      cardImageLoadingInstance.value = null
    }
  }
}

/** 初始化 */
onMounted(async () => {
  await handleLoading(props.detail.status as string)
})
</script>

<style lang="scss" scoped>
.image-card {
  width: 320px;
  height: auto;
  border-radius: 10px;
  position: relative;
  display: flex;
  flex-direction: column;

  .image-operation {
    display: flex;
    flex-direction: row;
    justify-content: space-between;

    .btn {
      //border: 1px solid red;
      padding: 10px;
      margin: 0;
    }
  }

  .image-wrapper {
    overflow: hidden;
    margin-top: 20px;
    height: 280px;
    flex: 1;

    .image {
      width: 100%;
      border-radius: 10px;
    }
  }

  .image-mj-btns {
    margin-top: 5px;
    width: 100%;
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: flex-start;
  }
}
</style>
