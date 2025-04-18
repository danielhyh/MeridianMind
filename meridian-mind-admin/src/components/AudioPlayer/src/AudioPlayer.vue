<template>
  <div class="audio-player">
    <div class="audio-info">
      <div class="audio-title">{{ title || '音频文件' }}</div>
      <div class="audio-duration">{{ formatTime(currentTime) }} / {{ formatTime(duration) }}</div>
    </div>
    <div class="audio-controls">
      <el-button text @click="togglePlay">
        <Icon :icon="isPlaying ? 'fa-solid:pause' : 'fa-solid:play'"/>
      </el-button>
      <el-slider
        v-model="currentTime"
        :max="duration"
        :step="0.1"
        :show-tooltip="false"
        class="audio-progress"
        @change="seekAudio"
      />
      <div class="volume-control"
           @mouseenter="showVolumeSlider = true"
           @mouseleave="showVolumeSlider = false">
        <el-button text @click="toggleMute">
          <Icon :icon="isMuted ? 'fa-solid:volume-slash' : getVolumeIcon()"/>
        </el-button>
        <div class="volume-slider-container" v-show="showVolumeSlider">
          <el-slider
            v-model="volume"
            :min="0"
            :max="1"
            :step="0.01"
            :show-tooltip="false"
            class="volume-slider"
            @change="changeVolume"
          />
        </div>
      </div>
    </div>
    <audio
      ref="audioElement"
      :src="src"
      preload="metadata"
      @timeupdate="onTimeUpdate"
      @loadedmetadata="onLoadedMetadata"
      @ended="onEnded"
    ></audio>
  </div>
</template>

<script setup>
import {ref, onMounted, onBeforeUnmount} from 'vue'

const props = defineProps({
  src: {
    type: String,
    required: true
  },
  title: {
    type: String,
    default: ''
  }
})

const audioElement = ref(null)
const isPlaying = ref(false)
const isMuted = ref(false)
const duration = ref(0)
const currentTime = ref(0)
const volume = ref(1) // 默认最大音量
const showVolumeSlider = ref(false) // 控制音量滑动条显示状态

// 播放/暂停切换
const togglePlay = () => {
  if (!audioElement.value) return

  if (isPlaying.value) {
    audioElement.value.pause()
  } else {
    audioElement.value.play()
  }
  isPlaying.value = !isPlaying.value
}

// 根据音量值获取合适的图标
const getVolumeIcon = () => {
  if (volume.value === 0) return 'fa-solid:volume-slash'
  if (volume.value < 0.5) return 'fa-solid:volume-down'
  return 'fa-solid:volume-up'
}

// 音量变化处理函数
const changeVolume = (value) => {
  if (!audioElement.value) return

  volume.value = value
  audioElement.value.volume = value

  // 音量为0时自动静音
  if (value === 0) {
    audioElement.value.muted = true
    isMuted.value = true
  } else if (isMuted.value) {
    // 如果之前是静音，恢复声音
    audioElement.value.muted = false
    isMuted.value = false
  }
}

// 静音切换
const toggleMute = () => {
  if (!audioElement.value) return

  audioElement.value.muted = !audioElement.value.muted
  isMuted.value = audioElement.value.muted

  // 取消静音时，如果音量为0，设置一个默认音量
  if (!isMuted.value && volume.value === 0) {
    volume.value = 0.5
    audioElement.value.volume = 0.5
  }
}

// 跳转到特定时间
const seekAudio = (value) => {
  if (!audioElement.value) return

  audioElement.value.currentTime = value
}

// 更新当前播放时间
const onTimeUpdate = () => {
  if (!audioElement.value) return

  currentTime.value = audioElement.value.currentTime
}

// 加载元数据
const onLoadedMetadata = () => {
  if (!audioElement.value) return

  duration.value = audioElement.value.duration
}

// 播放结束
const onEnded = () => {
  isPlaying.value = false
  currentTime.value = 0
}

// 格式化时间显示 (秒 -> MM:SS)
const formatTime = (seconds) => {
  if (!seconds) return '00:00'

  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

onMounted(() => {
  // 设置初始音量
  if (audioElement.value) {
    audioElement.value.volume = volume.value
  }
})

onBeforeUnmount(() => {
  // 组件卸载前停止播放
  if (audioElement.value && !audioElement.value.paused) {
    audioElement.value.pause()
  }
})
</script>

<style scoped>
.audio-player {
  width: 100%;
  max-width: 350px;
  padding: 10px;
  border-radius: 8px;
  background-color: #f5f7fa;
}

.audio-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.audio-title {
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 70%;
}

.audio-duration {
  font-size: 12px;
  color: #606266;
}

.audio-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.audio-progress {
  flex: 1;
}

/* 音量控制样式 */
.volume-control {
  position: relative;
  display: flex;
  align-items: center;
}

.volume-slider-container {
  position: absolute;
  right: 100%;
  top: 50%;
  transform: translateY(-50%);
  width: 80px;
  padding: 0 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.volume-slider {
  width: 100%;
}

.volume-slider :deep(.el-slider__button) {
  width: 10px;
  height: 10px;
}

.volume-slider :deep(.el-slider__runway) {
  height: 4px;
}
</style>
