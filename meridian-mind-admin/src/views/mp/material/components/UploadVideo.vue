<template>
  <el-dialog v-model="showDialog" title="新建视频" width="600px">
    <el-upload
      ref="uploadVideoRef"
      :action="UPLOAD_URL"
      :auto-upload="false"
      :before-upload="beforeVideoUpload"
      :data="uploadData"
      :file-list="fileList"
      :headers="HEADERS"
      :limit="1"
      :on-error="onUploadError"
      :on-success="onUploadSuccess"
      class="mb-5"
      multiple
    >
      <template #trigger>
        <el-button plain type="primary">选择视频</el-button>
      </template>
      <template #tip>
        <span class="el-upload__tip" style="margin-left: 10px"
          >格式支持 MP4，文件大小不超过 10MB</span
        >
      </template>
    </el-upload>
    <el-divider />
    <el-form ref="uploadFormRef" :model="uploadData" :rules="uploadRules">
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="uploadData.title"
          placeholder="标题将展示在相关播放页面，建议填写清晰、准确、生动的标题"
        />
      </el-form-item>
      <el-form-item label="描述" prop="introduction">
        <el-input
          v-model="uploadData.introduction"
          :rows="3"
          placeholder="介绍语将展示在相关播放页面，建议填写简洁明确、有信息量的内容"
          type="textarea"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="showDialog = false">取 消</el-button>
      <el-button type="primary" @click="submitVideo">提 交</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import type {
  FormInstance,
  FormRules,
  UploadInstance,
  UploadProps,
  UploadUserFile
} from 'element-plus'
import { HEADERS, UploadData, UPLOAD_URL, UploadType, beforeVideoUpload } from './upload'

const message = useMessage()

const accountId = inject<number>('accountId')

const uploadRules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  introduction: [{ required: true, message: '请输入描述', trigger: 'blur' }]
}

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})
const emit = defineEmits<{
  (e: 'update:modelValue', v: boolean)
  (e: 'uploaded', v: void)
}>()

const showDialog = computed<boolean>({
  get() {
    return props.modelValue
  },
  set(val) {
    emit('update:modelValue', val)
  }
})

const fileList = ref<UploadUserFile[]>([])

const uploadData: UploadData = reactive({
  type: UploadType.Video,
  title: '',
  introduction: '',
  accountId: accountId!
})

const uploadFormRef = ref<FormInstance | null>(null)
const uploadVideoRef = ref<UploadInstance | null>(null)

const submitVideo = () => {
  uploadFormRef.value?.validate((valid) => {
    if (!valid) {
      return false
    }
    uploadVideoRef.value?.submit()
  })
}

/** 上传成功处理 */
const onUploadSuccess: UploadProps['onSuccess'] = (res: any) => {
  if (res.code !== 0) {
    message.error('上传出错：' + res.msg)
    return false
  }

  // 清空上传时的各种数据
  fileList.value = []
  uploadData.title = ''
  uploadData.introduction = ''

  showDialog.value = false
  message.notifySuccess('上传成功')
  emit('uploaded')
}

/** 上传失败处理 */
const onUploadError = (err: Error) => message.error(`上传失败: ${err.message}`)
</script>
