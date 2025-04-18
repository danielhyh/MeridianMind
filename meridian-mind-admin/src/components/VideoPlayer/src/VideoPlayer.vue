<template>
  <div class="video-player-container">
    <div class="video-wrapper" @click="toggleControls">
      <video
        ref="videoElement"
        class="video-element"
        @timeupdate="onTimeUpdate"
        @loadedmetadata="onLoadedMetadata"
        @ended="onEnded"
        @click.stop="togglePlay"
      >
        <source :src="src" :type="type"/>
        您的浏览器不支持视频播放
      </video>

      <div class="video-controls" v-show="controlsVisible" @click.stop>
        <div class="progress-container">
          <el-slider
            v-model="currentTime"
            :max="duration"
            :step="0.1"
            :show-tooltip="false"
            class="video-progress"
            @change="seekVideo"
          />
        </div>

        <div class="controls-row">
          <el-button text @click="togglePlay">
            <Icon color="white" :icon="isPlaying ? 'fa-solid:pause' : 'fa-solid:play'" />
          </el-button>
          <div class="time-display">{{ formatTime(currentTime) }} / {{ formatTime(duration) }}</div>
          <div class="right-controls">
            <div class="volume-control"
                 @mouseenter="showVolumeSlider = true"
                 @mouseleave="showVolumeSlider = false">
              <el-button text @click="toggleMute">
                <Icon color="white" :icon="isMuted ? 'fa-solid:volume-slash' : getVolumeIcon()" />
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
            <el-button text @click="toggleFullscreen">
              <Icon color="white" icon="fa-solid:expand" />
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
  src: {
    type: String,
    required: true
  },
  type: {
    type: String,
    default: 'video/mp4'
  },
  autoplay: {
    type: Boolean,
    default: false
  }
})

const videoElement = ref(null)
const isPlaying = ref(false)
const isMuted = ref(false)
const duration = ref(0)
const currentTime = ref(0)
const controlsVisible = ref(true)
const volume = ref(1) // 默认最大音量
const showVolumeSlider = ref(false) // 控制音量滑动条显示状态
let controlsTimeout = null

// 播放/暂停切换
const togglePlay = () => {
  if (!videoElement.value) return

  if (isPlaying.value) {
    videoElement.value.pause()
  } else {
    videoElement.value.play()
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
  if (!videoElement.value) return

  volume.value = value
  videoElement.value.volume = value

  // 音量为0时自动静音
  if (value === 0) {
    videoElement.value.muted = true
    isMuted.value = true
  } else if (isMuted.value) {
    // 如果之前是静音，恢复声音
    videoElement.value.muted = false
    isMuted.value = false
  }
}

// 静音切换
const toggleMute = () => {
  if (!videoElement.value) return

  videoElement.value.muted = !videoElement.value.muted
  isMuted.value = videoElement.value.muted

  // 取消静音时，如果音量为0，设置一个默认音量
  if (!isMuted.value && volume.value === 0) {
    volume.value = 0.5
    videoElement.value.volume = 0.5
  }
}

// 全屏切换
const toggleFullscreen = () => {
  if (!videoElement.value) return

  if (document.fullscreenElement) {
    document.exitFullscreen()
  } else {
    videoElement.value.requestFullscreen()
  }
}

// 控制栏显示切换
const toggleControls = () => {
  controlsVisible.value = !controlsVisible.value

  if (controlsVisible.value) {
    // 3秒后自动隐藏控制栏
    clearTimeout(controlsTimeout)
    controlsTimeout = setTimeout(() => {
      if (isPlaying.value) {
        controlsVisible.value = false
      }
    }, 3000)
  }
}

// 跳转到特定时间
const seekVideo = (value) => {
  if (!videoElement.value) return

  videoElement.value.currentTime = value
}

// 更新当前播放时间
const onTimeUpdate = () => {
  if (!videoElement.value) return

  currentTime.value = videoElement.value.currentTime
}

// 加载元数据
const onLoadedMetadata = () => {
  if (!videoElement.value) return

  duration.value = videoElement.value.duration

  // 如果设置了自动播放
  if (props.autoplay) {
    togglePlay()
  }
}

// 播放结束
const onEnded = () => {
  isPlaying.value = false
  currentTime.value = 0
  controlsVisible.value = true
}

// 格式化时间显示 (秒 -> MM:SS)
const formatTime = (seconds) => {
  if (!seconds) return '00:00'

  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// 鼠标移动处理函数
const handleMouseMove = () => {
  controlsVisible.value = true

  clearTimeout(controlsTimeout)
  controlsTimeout = setTimeout(() => {
    if (isPlaying.value) {
      controlsVisible.value = false
    }
  }, 3000)
}

onMounted(() => {
  // 设置初始音量
  if (videoElement.value) {
    videoElement.value.volume = volume.value
  }

  // 移动鼠标时显示控制栏
  document.addEventListener('mousemove', handleMouseMove)
})

onBeforeUnmount(() => {
  document.removeEventListener('mousemove', handleMouseMove)

  // 组件卸载前停止播放
  if (videoElement.value && !videoElement.value.paused) {
    videoElement.value.pause()
  }

  clearTimeout(controlsTimeout)
})
</script>
<style scoped>
.video-player-container {
  width: 100%;
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  background-color: #000;
}

.video-wrapper {
  position: relative;
  width: 100%;
  height: 0;
  padding-bottom: 56.25%; /* 16:9 宽高比 */
}

.video-element {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.video-controls {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.5);
  padding: 8px;
  transition: opacity 0.3s;
}

.progress-container {
  margin-bottom: 8px;
}

.video-progress {
  width: 100%;
}

.controls-row {
  display: flex;
  align-items: center;
}

.time-display {
  color: #fff;
  margin: 0 12px;
  font-size: 12px;
  flex: 1;
}

.right-controls {
  display: flex;
  gap: 8px;
  align-items: center;
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
  background-color: rgba(0, 0, 0, 0.7);
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.3);
  z-index: 10;
}

.volume-slider {
  width: 100%;
}

/* 为视频播放器中的按钮设置半透明黑色背景悬停效果 */
:deep(.video-controls .el-button.is-text:hover) {
  background-color: rgba(0, 0, 0, 0.5) !important;
  border-color: transparent !important;
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
