<script setup lang="ts">
import { ref } from 'vue'
import { uploadVideo } from '@/api/detect'
import { useAuth } from '@/stores/auth'

const { user } = useAuth()

const emit = defineEmits<{
  uploadComplete: [file: File, videoType: '2d' | 'panoramic', targetFps: number, videoId: string]
}>()

// 视频类型
const videoType = ref<'2d' | 'panoramic'>('2d')

// 帧率选择相关
const TARGET_FPS_OPTIONS = [30, 60, 120] as const
const selectedFps = ref<number>(30)
const originalFps = ref<number>(30)
const isCustomFps = ref(false)
const customFps = ref<number>(30)

// 处理自定义帧率变化
const handleCustomFpsChange = (e: Event) => {
  const val = parseInt((e.target as HTMLInputElement).value, 10)
  if (!isNaN(val) && val > 0) {
    customFps.value = val
    selectedFps.value = val
  }
}

// 上传状态
const uploadState = ref<'idle' | 'confirming' | 'uploading' | 'detecting' | 'completed'>('idle')

// 确认弹窗相关
const showConfirmModal = ref(false)
const selectedFile = ref<File | null>(null)
const videoPreview = ref({
  thumbnail: '',
  duration: '',
  size: '',
  fps: 0
})
const uploadProgress = ref(0)

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 格式化时长
const formatDuration = (seconds: number): string => {
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// 获取视频信息（时长、帧率、封面）
const getVideoInfo = (file: File): Promise<{ duration: string; fps: number; thumbnail: string }> => {
  return new Promise((resolve) => {
    const video = document.createElement('video')
    const url = URL.createObjectURL(file)
    video.src = url
    video.preload = 'metadata'
    video.crossOrigin = 'anonymous'

    video.onloadedmetadata = () => {
      // 获取时长
      const duration = formatDuration(video.duration)

      // 预估帧率（常见值）
      const estimatedFps = 30

      // 获取封面（第一帧）
      video.currentTime = 0.1
      video.onseeked = () => {
        const canvas = document.createElement('canvas')
        canvas.width = video.videoWidth || 320
        canvas.height = video.videoHeight || 180
        const ctx = canvas.getContext('2d')
        ctx?.drawImage(video, 0, 0, canvas.width, canvas.height)
        const thumbnail = canvas.toDataURL('image/jpeg', 0.8)

        URL.revokeObjectURL(url)
        resolve({ duration, fps: estimatedFps, thumbnail })
      }
    }

    // 超时处理
    setTimeout(() => {
      URL.revokeObjectURL(url)
      resolve({ duration: '00:00', fps: 30, thumbnail: '' })
    }, 5000)
  })
}

// 处理文件选择
const handleFileSelect = async (event: Event) => {
  const input = event.target as HTMLInputElement
  if (input.files && input.files[0]) {
    await openConfirmModal(input.files[0])
    // 清空 input，允许重复选择同一文件
    input.value = ''
  }
}

// 处理拖拽上传
const handleDrop = async (event: DragEvent) => {
  event.preventDefault()
  const files = event.dataTransfer?.files
  if (files && files[0] && files[0].type.startsWith('video/')) {
    await openConfirmModal(files[0])
  }
}

// 打开确认弹窗
const openConfirmModal = async (file: File) => {
  selectedFile.value = file
  uploadState.value = 'confirming'
  showConfirmModal.value = true

  // 设置文件大小
  videoPreview.value.size = formatFileSize(file.size)
  videoPreview.value.duration = '加载中...'
  videoPreview.value.fps = 30
  videoPreview.value.thumbnail = ''

  // 获取视频详细信息
  const info = await getVideoInfo(file)
  videoPreview.value.duration = info.duration
  videoPreview.value.fps = info.fps
  videoPreview.value.thumbnail = info.thumbnail

  // 设置原视频帧率和默认选择帧率
  originalFps.value = info.fps
  // 选择最接近但不超过原视频帧率的标准帧率
  const availableFps = TARGET_FPS_OPTIONS.filter(fps => fps <= info.fps)
  selectedFps.value = availableFps.length > 0 ? (availableFps[availableFps.length - 1] ?? 30) : 30
}

// 关闭确认弹窗
const closeConfirmModal = () => {
  showConfirmModal.value = false
  uploadState.value = 'idle'
  selectedFile.value = null
  videoPreview.value = {
    thumbnail: '',
    duration: '',
    size: '',
    fps: 0
  }
}

// 确认上传
const confirmUpload = async () => {
  if (!selectedFile.value || !user.value?.userId) {
    console.error('文件或用户信息不存在')
    return
  }

  showConfirmModal.value = false
  uploadState.value = 'uploading'
  uploadProgress.value = 0

  // 启动模拟进度条，缓慢增加到 90%
  let progressInterval: ReturnType<typeof setInterval> | null = null
  const startProgressSimulation = () => {
    progressInterval = setInterval(() => {
      if (uploadProgress.value < 90) {
        // 越接近90增长速度越慢，结果取整
        const remaining = 90 - uploadProgress.value
        const increment = Math.max(1, Math.floor(remaining * 0.05))
        uploadProgress.value = Math.min(90, uploadProgress.value + increment)
      }
    }, 200)
  }

  // 停止模拟进度
  const stopProgressSimulation = () => {
    if (progressInterval) {
      clearInterval(progressInterval)
      progressInterval = null
    }
  }

  try {
    // 解析时长为秒数
    const durationParts = videoPreview.value.duration.split(':')
    const durationSeconds = parseInt(durationParts[0] || '0') * 60 + parseInt(durationParts[1] || '0')

    // 开始模拟进度
    startProgressSimulation()

    // 调用真实上传接口
    const result = await uploadVideo({
      userId: user.value.userId,
      video: selectedFile.value,
      frame: selectedFps.value,
      video_type: videoType.value,
      duration: durationSeconds || 0
    })

    // 请求完成，停止模拟并拉满进度条
    stopProgressSimulation()
    uploadProgress.value = 100

    console.log('[Upload] 上传成功:', result)

    // 稍微延迟一下让用户看到 100%，然后进入检测状态
    setTimeout(() => {
      uploadState.value = 'detecting'
    }, 300)

    // 模拟检测过程（后端异步处理）
    setTimeout(() => {
      uploadState.value = 'completed'
      if (selectedFile.value) {
        emit('uploadComplete', selectedFile.value, videoType.value, selectedFps.value, result)
      }
    }, 2000)

  } catch (error: any) {
    stopProgressSimulation()
    console.error('[Upload] 上传失败:', error)
    alert('上传失败: ' + error.message)
    resetUpload()
  }
}

// 重置上传
const resetUpload = () => {
  uploadState.value = 'idle'
  uploadProgress.value = 0
  selectedFile.value = null
  videoType.value = '2d'
  selectedFps.value = 30
  originalFps.value = 30
  isCustomFps.value = false
  customFps.value = 30
  showConfirmModal.value = false
  videoPreview.value = {
    thumbnail: '',
    duration: '',
    size: '',
    fps: 0
  }
}
</script>

<template>
  <section class="upload-section">
    <h2 class="section-title">上传视频</h2>

    <!-- 视频类型选择 -->
    <div class="video-type-selector" v-if="uploadState === 'idle' || uploadState === 'confirming'">
      <span class="type-label">视频类型：</span>
      <div class="type-options">
        <label
          class="type-option"
          :class="{ 'active': videoType === '2d', 'disabled': uploadState === 'confirming' }"
        >
          <input
            type="radio"
            v-model="videoType"
            value="2d"
            name="videoType"
          />
          <span class="option-icon"></span>
          <div class="option-text">
            <span class="option-title">2D 视频</span>
            <span class="option-desc">普通平面视频</span>
          </div>
        </label>
        <label
          class="type-option"
          :class="{ 'active': videoType === 'panoramic', 'disabled': uploadState === 'confirming' }"
        >
          <input
            type="radio"
            v-model="videoType"
            value="panoramic"
            name="videoType"
          />
          <span class="option-icon"></span>
          <div class="option-text">
            <span class="option-title">全景视频</span>
            <span class="option-desc">360° VR 视频</span>
          </div>
        </label>
      </div>
    </div>

    <div
      class="upload-area"
      :class="{ 'uploading': uploadState === 'uploading', 'active': uploadState !== 'idle' && uploadState !== 'confirming' }"
      @dragover.prevent
      @drop="handleDrop"
    >
      <input
        type="file"
        id="video-upload"
        accept="video/*"
        class="upload-input"
        @change="handleFileSelect"
        :disabled="uploadState !== 'idle' && uploadState !== 'confirming'"
      />

      <label for="video-upload" class="upload-label">
        <div class="upload-content">
          <div class="upload-text">
            <template v-if="uploadState === 'idle'">
              <p class="upload-main-text">拖拽视频到此处，或点击上传</p>
              <p class="upload-sub-text">支持 MP4、AVI、MOV 等格式，最大 500MB</p>
            </template>
            <template v-else-if="uploadState === 'uploading'">
              <p class="upload-main-text">正在上传: {{ selectedFile?.name }}</p>
              <div class="upload-progress">
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
                </div>
                <span class="progress-text">{{ uploadProgress }}%</span>
              </div>
            </template>
            <template v-else-if="uploadState === 'detecting'">
              <p class="upload-main-text">AI 正在分析视频显著性...</p>
              <p class="upload-sub-text">请稍候，正在提取视觉和音频特征</p>
              <div class="detecting-animation">
                <span></span><span></span><span></span>
              </div>
            </template>
            <template v-else-if="uploadState === 'completed'">
              <p class="upload-main-text">上传成功: {{ selectedFile?.name }}</p>
              <p class="upload-sub-text">点击下方按钮可重新上传</p>
            </template>
          </div>
        </div>
      </label>

      <button v-if="uploadState === 'completed'" class="reset-btn" @click.stop="resetUpload">
        重新上传
      </button>
    </div>

    <!-- 确认弹窗 -->
    <div v-if="showConfirmModal" class="modal-overlay" @click.self="closeConfirmModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3 class="modal-title">确认视频信息</h3>
          <button class="modal-close" @click="closeConfirmModal">✕</button>
        </div>

        <div class="modal-body">
          <!-- 视频封面 -->
          <div class="preview-thumbnail">
            <img v-if="videoPreview.thumbnail" :src="videoPreview.thumbnail" alt="视频封面" />
            <div v-else class="thumbnail-placeholder">
              <p>加载中...</p>
            </div>
          </div>

          <!-- 视频信息 -->
          <div class="video-info-list">
            <div class="info-item">
              <span class="info-label">文件名称</span>
              <span class="info-value" :title="selectedFile?.name">{{ selectedFile?.name }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">视频时长</span>
              <span class="info-value">{{ videoPreview.duration }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">文件大小</span>
              <span class="info-value">{{ videoPreview.size }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">视频类型</span>
              <span class="info-value">{{ videoType === '2d' ? ' 2D视频' : ' 全景视频' }}</span>
            </div>
            <div class="info-item fps-selector">
              <span class="info-label">检测帧率</span>
              <div class="fps-options">
                <button
                  v-for="fps in TARGET_FPS_OPTIONS"
                  :key="fps"
                  class="fps-option"
                  :class="{
                    'active': selectedFps === fps && !isCustomFps,
                    'disabled': fps > originalFps
                  }"
                  :disabled="fps > originalFps"
                  @click="isCustomFps = false; selectedFps = fps"
                >
                  {{ fps }} FPS
                  <span v-if="fps > originalFps" class="fps-hint">超出原视频</span>
                </button>
                <button
                  class="fps-option"
                  :class="{ 'active': isCustomFps }"
                  @click="isCustomFps = true; selectedFps = customFps"
                >
                  自定义
                </button>
              </div>
              <div v-if="isCustomFps" class="custom-fps-input">
                <input
                  type="number"
                  min="1"
                  :value="customFps"
                  @input="handleCustomFpsChange"
                  placeholder="输入帧率"
                />
                <span>FPS</span>
              </div>
            </div>
          </div>

          <!-- 提示信息 -->
          <div class="info-tips">
            <p>⚡ 检测后的视频将以 <strong>{{ selectedFps }} FPS</strong> 的帧率生成显著性热力图</p>
            <p v-if="selectedFps > originalFps" class="fps-warning">⚠️ 目标帧率 ({{ selectedFps }} FPS) 超过原视频帧率 ({{ originalFps }} FPS)，可能影响检测效果</p>
            <p v-else-if="originalFps < 30" class="fps-warning">⚠️ 原视频帧率较低 ({{ originalFps }} FPS)，可能影响检测效果</p>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeConfirmModal">取消</button>
          <button class="btn btn-primary" @click="confirmUpload">确认上传并开始检测</button>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.section-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--color-heading);
  margin-bottom: 1rem;
}

/* 视频类型选择器 */
.video-type-selector {
  margin-bottom: 1.25rem;
}

.type-label {
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--color-heading);
  margin-bottom: 0.75rem;
  display: block;
}

.type-options {
  display: flex;
  gap: 1rem;
}

.type-option {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 1.25rem;
  background: var(--color-background);
  border: 2px solid var(--color-border);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.type-option:hover {
  border-color: hsla(215, 80%, 45%, 0.4);
}

.type-option.active {
  border-color: hsla(215, 80%, 45%, 1);
  background: hsla(215, 80%, 45%, 0.06);
}

.type-option input {
  display: none;
}

.type-option.disabled {
  opacity: 0.6;
  cursor: not-allowed;
  pointer-events: none;
}

.option-icon {
  font-size: 1.5rem;
}

.option-text {
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
}

.option-title {
  font-size: 0.95rem;
  font-weight: 500;
  color: var(--color-heading);
}

.option-desc {
  font-size: 0.75rem;
  color: var(--color-text);
  opacity: 0.6;
}

@media (max-width: 480px) {
  .type-options {
    flex-direction: column;
  }
}

.upload-section {
  margin-bottom: 2rem;
}

.upload-area {
  position: relative;
  border: 2px dashed var(--color-border);
  border-radius: 12px;
  background: var(--color-background-soft);
  transition: all 0.3s;
}

.upload-area:hover,
.upload-area.active {
  border-color: hsla(210, 80%, 45%, 0.5);
  background: hsla(210, 80%, 45%, 0.03);
}

.upload-area.uploading {
  border-color: hsla(210, 80%, 45%, 1);
}

.upload-input {
  display: none;
}

.upload-label {
  display: block;
  padding: 3rem 2rem;
  cursor: pointer;
}

.upload-content {
  text-align: center;
}

.upload-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.upload-main-text {
  font-size: 1.1rem;
  font-weight: 500;
  color: var(--color-heading);
  margin-bottom: 0.5rem;
}

.upload-sub-text {
  font-size: 0.875rem;
  color: var(--color-text);
  opacity: 0.6;
}

/* 上传进度 */
.upload-progress {
  display: flex;
  align-items: center;
  gap: 1rem;
  max-width: 300px;
  margin: 1rem auto 0;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: var(--color-background-mute);
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: hsla(210, 80%, 45%, 1);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 0.9rem;
  font-weight: 500;
  color: hsla(210, 80%, 45%, 1);
  min-width: 3rem;
}

/* 检测动画 */
.detecting-animation {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
  margin-top: 1rem;
}

.detecting-animation span {
  width: 10px;
  height: 10px;
  background: hsla(210, 80%, 45%, 1);
  border-radius: 50%;
  animation: bounce 1.4s ease-in-out infinite both;
}

.detecting-animation span:nth-child(1) {
  animation-delay: -0.32s;
}

.detecting-animation span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

/* 重置按钮 */
.reset-btn {
  position: absolute;
  bottom: 1rem;
  right: 1.5rem;
  padding: 0.5rem 1rem;
  background: var(--color-background-mute);
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 0.8rem;
  color: var(--color-text);
  cursor: pointer;
  transition: all 0.2s;
}

.reset-btn:hover {
  background: var(--color-border);
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.modal-content {
  background: var(--color-background-soft);
  border-radius: 16px;
  width: 100%;
  max-width: 560px;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: modalSlideIn 0.3s ease;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid var(--color-border);
}

.modal-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--color-heading);
  margin: 0;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.25rem;
  color: var(--color-text);
  opacity: 0.5;
  cursor: pointer;
  padding: 0.25rem;
  line-height: 1;
  transition: opacity 0.2s;
}

.modal-close:hover {
  opacity: 1;
}

.modal-body {
  padding: 1.5rem;
  overflow-y: auto;
  max-height: calc(90vh - 140px);
}

.preview-thumbnail {
  width: 100%;
  aspect-ratio: 16 / 9;
  background: var(--color-background-mute);
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 1.25rem;
}

.preview-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--color-text);
  opacity: 0.6;
}

.thumbnail-placeholder span {
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
}

.thumbnail-placeholder p {
  font-size: 0.875rem;
}

.video-info-list {
  display: flex;
  flex-direction: column;
  gap: 0.875rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  background: var(--color-background);
  border-radius: 8px;
}

.info-label {
  font-size: 0.875rem;
  color: var(--color-text);
  opacity: 0.7;
}

.info-value {
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--color-heading);
  max-width: 60%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.info-tips {
  margin-top: 1.25rem;
  padding: 0.875rem 1rem;
  background: hsla(215, 80%, 45%, 0.08);
  border-left: 3px solid hsla(215, 80%, 45%, 1);
  border-radius: 0 8px 8px 0;
}

.info-tips p {
  margin: 0;
  font-size: 0.85rem;
  color: var(--color-text);
  line-height: 1.5;
}

.info-tips strong {
  color: hsla(215, 80%, 45%, 1);
}

.fps-warning {
  margin-top: 0.5rem;
  color: #f59e0b !important;
  font-size: 0.8rem;
}

/* 帧率选择器 */
.fps-selector {
  flex-direction: column;
  align-items: flex-start;
  gap: 0.75rem;
}

.fps-options {
  display: flex;
  gap: 0.5rem;
  width: 100%;
}

.fps-option {
  flex: 1;
  padding: 0.5rem 0.75rem;
  background: var(--color-background);
  border: 2px solid var(--color-border);
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-heading);
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
}

.fps-option:hover:not(.disabled) {
  border-color: hsla(215, 80%, 45%, 0.5);
}

.fps-option.active {
  border-color: hsla(215, 80%, 45%, 1);
  background: hsla(215, 80%, 45%, 0.1);
  color: hsla(215, 80%, 45%, 1);
}

.fps-option.disabled {
  opacity: 0.4;
  cursor: not-allowed;
  background: var(--color-background-mute);
}

.fps-hint {
  font-size: 0.7rem;
  color: var(--color-text);
  opacity: 0.7;
}

.custom-fps-input {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 0.5rem;
  width: 100%;
}

.custom-fps-input input {
  flex: 1;
  max-width: 120px;
  padding: 0.5rem 0.75rem;
  background: var(--color-background);
  border: 2px solid var(--color-border);
  border-radius: 8px;
  font-size: 0.875rem;
  color: var(--color-heading);
  outline: none;
}

.custom-fps-input input:focus {
  border-color: hsla(215, 80%, 45%, 1);
}

.custom-fps-input span {
  font-size: 0.875rem;
  color: var(--color-text);
  opacity: 0.8;
}

.modal-footer {
  display: flex;
  gap: 0.75rem;
  padding: 1rem 1.5rem 1.5rem;
  border-top: 1px solid var(--color-border);
}

.btn {
  flex: 1;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.btn-secondary {
  background: var(--color-background-mute);
  color: var(--color-text);
  border: 1px solid var(--color-border);
}

.btn-secondary:hover {
  background: var(--color-border);
}

.btn-primary {
  background: linear-gradient(135deg, hsla(215, 80%, 45%, 1) 0%, hsla(250, 60%, 55%, 1) 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px hsla(215, 80%, 45%, 0.35);
}

@media (max-width: 480px) {
  .modal-content {
    max-height: 95vh;
  }

  .modal-body {
    padding: 1rem;
  }

  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
  }

  .info-value {
    max-width: 100%;
  }
}
</style>
