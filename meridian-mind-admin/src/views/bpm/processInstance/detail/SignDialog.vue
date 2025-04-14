<template>
  <el-dialog v-model="signDialogVisible" title="签名" width="935">
    <div class="position-relative">
      <Vue3Signature ref="signature" class="b b-solid b-gray" h="400px" w="900px" />
      <el-button
        class="pos-absolute bottom-20px right-10px"
        size="small"
        text
        type="primary"
        @click="signature.clear()"
      >
        <Icon class="mr-5px" icon="ep:delete" />
        清除
      </el-button>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="signDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit"> 提交 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import Vue3Signature from 'vue3-signature'
import * as FileApi from '@/api/infra/file'
import download from '@/utils/download'

const message = useMessage() // 消息弹窗
const signDialogVisible = ref(false)
const signature = ref()

const open = async () => {
  signDialogVisible.value = true
}
defineExpose({ open })

const emits = defineEmits(['success'])
const submit = async () => {
  message.success('签名上传中请稍等。。。')
  const res = await FileApi.updateFile({
    file: download.base64ToFile(signature.value.save('image/png'), '签名')
  })
  emits('success', res.data)
  signDialogVisible.value = false
}
</script>

<style scoped></style>
