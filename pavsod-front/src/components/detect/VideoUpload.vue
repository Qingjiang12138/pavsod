<script setup lang="ts">
import { ref } from 'vue'

const emit = defineEmits<{
  uploadComplete: [file: File]
}>()

// 上传状态
const uploadState = ref<'idle' | 'uploading' | 'detecting' | 'completed'>('idle')
const uploadProgress = ref(0)
const selectedFile = ref<File | null>(null)

// 处理文件选择
const handleFileSelect = (event: Event) => {
  const input = event.target as HTMLInputElement
  if (input.files && input.files[0]) {
    selectedFile.value = input.files[0]
    startUpload()
  }
}

// 处理拖拽上传
const handleDrop = (event: DragEvent) => {
  event.preventDefault()
  const files = event.dataTransfer?.files
  if (files && files[0] && files[0].type.startsWith('video/')) {
    selectedFile.value = files[0]
    startUpload()
  }
}

// 开始上传
const startUpload = () => {
  uploadState.value = 'uploading'
  uploadProgress.value = 0

  // 模拟上传进度
  const interval = setInterval(() => {
    uploadProgress.value += 5
    if (uploadProgress.value >= 100) {
      clearInterval(interval)
      startDetection()
    }
  }, 100)
}

// 开始检测
const startDetection = () => {
  uploadState.value = 'detecting'

  // 模拟检测完成
  setTimeout(() => {
    uploadState.value = 'completed'
    if (selectedFile.value) {
      emit('uploadComplete', selectedFile.value)
    }
  }, 2000)
}

// 重置上传
const resetUpload = () => {
  uploadState.value = 'idle'
  uploadProgress.value = 0
  selectedFile.value = null
}
</script>

<template>
  <section class="upload-section">
    <h2 class="section-title">上传视频</h2>
    <div
      class="upload-area"
      :class="{ 'uploading': uploadState === 'uploading', 'active': uploadState !== 'idle' }"
      @dragover.prevent
      @drop="handleDrop"
    >
      <input
        type="file"
        id="video-upload"
        accept="video/*"
        class="upload-input"
        @change="handleFileSelect"
        :disabled="uploadState !== 'idle'"
      />

      <label for="video-upload" class="upload-label">
        <div class="upload-content">
          <div class="upload-icon">📹</div>
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
              <p class="upload-main-text">✅ 检测完成: {{ selectedFile?.name }}</p>
              <p class="upload-sub-text">点击下方按钮可重新上传</p>
            </template>
          </div>
        </div>
      </label>

      <button v-if="uploadState === 'completed'" class="reset-btn" @click.stop="resetUpload">
        重新上传
      </button>
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
</style>
