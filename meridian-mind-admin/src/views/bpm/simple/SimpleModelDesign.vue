<template>
  <ContentWrap :bodyStyle="{ padding: '20px 16px' }">
    <SimpleProcessDesigner
      ref="designerRef"
      :model-id="modelId"
      :model-key="modelKey"
      :model-name="modelName"
      :start-user-ids="startUserIds"
      @success="handleSuccess"
    />
  </ContentWrap>
</template>
<script lang="ts" setup>
import { SimpleProcessDesigner } from '@/components/SimpleProcessDesignerV2/src/'

defineOptions({
  name: 'SimpleModelDesign'
})

defineProps<{
  modelId?: string
  modelKey?: string
  modelName?: string
  startUserIds?: number[]
}>()

const emit = defineEmits(['success'])
const designerRef = ref()

// 修改成功回调
const handleSuccess = (data?: any) => {
  console.info('handleSuccess', data)
  if (data) {
    emit('success', data)
  }
}
</script>
<style lang="scss" scoped></style>
