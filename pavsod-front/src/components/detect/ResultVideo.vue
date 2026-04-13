<script setup lang="ts">
import { ref, watch, computed } from 'vue'

interface Props {
  originalUrl: string
  saliencyUrl: string
}

const props = defineProps<Props>()

// 视频加载状态
const originalLoaded = ref(false)
const saliencyLoaded = ref(false)

const handleOriginalLoad = () => {
  originalLoaded.value = true
}

const handleSaliencyLoad = () => {
  saliencyLoaded.value = true
}

// URL 变化时重置加载状态
watch(() => props.originalUrl, () => {
  originalLoaded.value = false
})

watch(() => props.saliencyUrl, () => {
  saliencyLoaded.value = false
})

// 给 URL 加时间戳，绕过浏览器脏缓存（之前 CORS 失败留下的缓存条目）
const bustCache = (url: string) => {
  if (!url) return url
  const separator = url.includes('?') ? '&' : '?'
  return `${url}${separator}t=${Date.now()}`
}

const originalSrc = computed(() => bustCache(props.originalUrl))
const saliencySrc = computed(() => bustCache(props.saliencyUrl))
</script>

<template>
  <div class="video-panel">
    <h3 class="panel-title">🎬 检测结果</h3>
    <div class="video-compare">
      <!-- 原始视频 -->
      <div class="video-box">
        <div class="video-label">原始视频</div>
        <div class="video-wrapper">
          <video
            class="video-player"
            :src="originalSrc"
            controls
            muted
            loop
            playsinline
            @loadedmetadata="handleOriginalLoad"
            @error="originalLoaded = true"
          ></video>
          <div v-if="!originalLoaded" class="video-loading">
            <span>加载中...</span>
          </div>
        </div>
      </div>

      <div class="video-arrow">→</div>

      <!-- 显著性检测视频 -->
      <div class="video-box">
        <div class="video-label">显著性检测</div>
        <div class="video-wrapper">
          <video
            class="video-player saliency"
            :src="saliencySrc"
            controls
            muted
            loop
            playsinline
            @loadedmetadata="handleSaliencyLoad"
            @error="saliencyLoaded = true"
          ></video>
          <div v-if="!saliencyLoaded" class="video-loading">
            <span>加载中...</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.video-panel {
  background: var(--color-background-soft);
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid var(--color-border);
}

.panel-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--color-heading);
  margin-bottom: 1rem;
}

.video-compare {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.video-box {
  flex: 1;
  min-width: 0;
}

.video-label {
  font-size: 0.8rem;
  color: var(--color-text);
  opacity: 0.7;
  margin-bottom: 0.5rem;
  text-align: center;
}

.video-wrapper {
  position: relative;
  aspect-ratio: 16 / 9;
  background: var(--color-background-mute);
  border-radius: 8px;
  overflow: hidden;
}

.video-player {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: #000;
}

.video-player.saliency {
  border: 2px solid hsla(210, 80%, 45%, 0.3);
}

.video-loading {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-background-mute);
  color: var(--color-text);
  opacity: 0.6;
  font-size: 0.875rem;
}

.video-arrow {
  font-size: 1.5rem;
  color: var(--color-text);
  opacity: 0.5;
  flex-shrink: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .video-compare {
    flex-direction: column;
  }

  .video-arrow {
    transform: rotate(90deg);
  }
}
</style>
