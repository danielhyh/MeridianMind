<template>
  <div class="flex flex-wrap items-center gap-8px">
    <div
      v-for="(seckillActivity, index) in Activitys"
      :key="seckillActivity.id"
      class="select-box spu-pic"
    >
      <el-tooltip :content="seckillActivity.name">
        <div class="relative h-full w-full">
          <el-image :src="seckillActivity.picUrl" class="h-full w-full" />
          <Icon
            v-show="!disabled"
            class="del-icon"
            icon="ep:circle-close-filled"
            @click="handleRemoveActivity(index)"
          />
        </div>
      </el-tooltip>
    </div>
    <el-tooltip v-if="canAdd" content="选择活动">
      <div class="select-box" @click="openSeckillActivityTableSelect">
        <Icon icon="ep:plus" />
      </div>
    </el-tooltip>
  </div>
  <!-- 拼团活动选择对话框（表格形式） -->
  <SeckillTableSelect
    ref="seckillActivityTableSelectRef"
    :multiple="limit != 1"
    @change="handleActivitySelected"
  />
</template>
<script lang="ts" setup>
import * as SeckillActivityApi from '@/api/mall/promotion/seckill/seckillActivity'
import { propTypes } from '@/utils/propTypes'
import { oneOfType } from 'vue-types'
import { isArray } from '@/utils/is'
import SeckillTableSelect from '@/views/mall/promotion/seckill/components/SeckillTableSelect.vue'

// 活动橱窗，一般用于装修时使用
// 提供功能：展示活动列表、添加活动、删除活动
defineOptions({ name: 'SeckillShowcase' })

const props = defineProps({
  modelValue: oneOfType<number | Array<number>>([Number, Array]).isRequired,
  // 限制数量：默认不限制
  limit: propTypes.number.def(Number.MAX_VALUE),
  disabled: propTypes.bool.def(false)
})

// 计算是否可以添加
const canAdd = computed(() => {
  // 情况一：禁用时不可以添加
  if (props.disabled) return false
  // 情况二：未指定限制数量时，可以添加
  if (!props.limit) return true
  // 情况三：检查已添加数量是否小于限制数量
  return Activitys.value.length < props.limit
})

// 拼团活动列表
const Activitys = ref<SeckillActivityApi.SeckillActivityVO[]>([])

watch(
  () => props.modelValue,
  async () => {
    const ids = isArray(props.modelValue)
      ? // 情况一：多选
        props.modelValue
      : // 情况二：单选
        props.modelValue
        ? [props.modelValue]
        : []
    // 不需要返显
    if (ids.length === 0) {
      Activitys.value = []
      return
    }
    // 只有活动发生变化之后，才会查询活动
    if (
      Activitys.value.length === 0 ||
      Activitys.value.some((seckillActivity) => !ids.includes(seckillActivity.id!))
    ) {
      Activitys.value = await SeckillActivityApi.getSeckillActivityListByIds(ids)
    }
  },
  { immediate: true }
)

/** 活动表格选择对话框 */
const seckillActivityTableSelectRef = ref()
// 打开对话框
const openSeckillActivityTableSelect = () => {
  seckillActivityTableSelectRef.value.open(Activitys.value)
}

/**
 * 选择活动后触发
 * @param activityVOs 选中的活动列表
 */
const handleActivitySelected = (
  activityVOs: SeckillActivityApi.SeckillActivityVO | SeckillActivityApi.SeckillActivityVO[]
) => {
  Activitys.value = isArray(activityVOs) ? activityVOs : [activityVOs]
  emitActivityChange()
}

/**
 * 删除活动
 * @param index 活动索引
 */
const handleRemoveActivity = (index: number) => {
  Activitys.value.splice(index, 1)
  emitActivityChange()
}
const emit = defineEmits(['update:modelValue', 'change'])
const emitActivityChange = () => {
  if (props.limit === 1) {
    const seckillActivity = Activitys.value.length > 0 ? Activitys.value[0] : null
    emit('update:modelValue', seckillActivity?.id || 0)
    emit('change', seckillActivity)
  } else {
    emit(
      'update:modelValue',
      Activitys.value.map((seckillActivity) => seckillActivity.id)
    )
    emit('change', Activitys.value)
  }
}
</script>

<style lang="scss" scoped>
.select-box {
  display: flex;
  width: 60px;
  height: 60px;
  border: 1px dashed var(--el-border-color-darker);
  border-radius: 8px;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.spu-pic {
  position: relative;
}

.del-icon {
  position: absolute;
  top: -10px;
  right: -10px;
  z-index: 1;
  width: 20px !important;
  height: 20px !important;
}
</style>
