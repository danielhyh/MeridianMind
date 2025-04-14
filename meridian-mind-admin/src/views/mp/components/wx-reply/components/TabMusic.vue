<template>
  <div>
    <el-row align="middle" justify="center">
      <el-col :span="6">
        <el-row align="middle" class="thumb-div" justify="center">
          <el-col :span="24">
            <el-row align="middle" justify="center">
              <img v-if="reply.thumbMediaUrl" :src="reply.thumbMediaUrl" style="width: 100px" />
              <icon v-else icon="ep:plus" />
            </el-row>
            <el-row align="middle" justify="center" style="margin-top: 2%">
              <div class="thumb-but">
                <el-upload
                  :action="UPLOAD_URL"
                  :before-upload="beforeImageUpload"
                  :data="uploadData"
                  :file-list="fileList"
                  :headers="HEADERS"
                  :limit="1"
                  :on-success="onUploadSuccess"
                  multiple
                >
                  <template #trigger>
                    <el-button link type="primary">本地上传</el-button>
                  </template>
                  <el-button link style="margin-left: 5px" type="primary" @click="showDialog = true"
                    >素材库选择
                  </el-button>
                </el-upload>
              </div>
            </el-row>
          </el-col>
        </el-row>
        <el-dialog
          v-model="showDialog"
          append-to-body
          destroy-on-close
          title="选择图片"
          width="80%"
        >
          <WxMaterialSelect
            :account-id="reply.accountId"
            type="image"
            @select-material="selectMaterial"
          />
        </el-dialog>
      </el-col>
      <el-col :span="18">
        <el-input v-model="reply.title" placeholder="请输入标题" />
        <div style="margin: 20px 0"></div>
        <el-input v-model="reply.description" placeholder="请输入描述" />
      </el-col>
    </el-row>
    <div style="margin: 20px 0"></div>
    <el-input v-model="reply.musicUrl" placeholder="请输入音乐链接" />
    <div style="margin: 20px 0"></div>
    <el-input v-model="reply.hqMusicUrl" placeholder="请输入高质量音乐链接" />
  </div>
</template>

<script lang="ts" setup>
import WxMaterialSelect from '@/views/mp/components/wx-material-select'
import type { UploadRawFile } from 'element-plus'
import { UploadType, useBeforeUpload } from '@/views/mp/hooks/useUpload'
import { getAccessToken } from '@/utils/auth'
import { Reply } from './types'

const message = useMessage()

const UPLOAD_URL = import.meta.env.VITE_BASE_URL + '/admin-api/mp/material/upload-temporary'
const HEADERS = { Authorization: 'Bearer ' + getAccessToken() } // 设置上传的请求头部

const props = defineProps<{
  modelValue: Reply
}>()
const emit = defineEmits<{
  (e: 'update:modelValue', v: Reply)
}>()
const reply = computed<Reply>({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const showDialog = ref(false)
const fileList = ref([])
const uploadData = reactive({
  accountId: reply.value.accountId,
  type: 'thumb', // 音乐类型为thumb
  title: '',
  introduction: ''
})

const beforeImageUpload = (rawFile: UploadRawFile) => useBeforeUpload(UploadType.Image, 2)(rawFile)

const onUploadSuccess = (res: any) => {
  if (res.code !== 0) {
    message.error('上传出错：' + res.msg)
    return false
  }

  // 清空上传时的各种数据
  fileList.value = []
  uploadData.title = ''
  uploadData.introduction = ''

  // 上传好的文件，本质是个素材，所以可以进行选中
  selectMaterial(res.data)
}

const selectMaterial = (item: any) => {
  showDialog.value = false

  reply.value.thumbMediaId = item.mediaId
  reply.value.thumbMediaUrl = item.url
}
</script>
