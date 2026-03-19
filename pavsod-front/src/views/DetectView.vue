<script setup lang="ts">
import { ref } from 'vue'
import VideoUpload from '@/components/detect/VideoUpload.vue'
import ResultVideo from '@/components/detect/ResultVideo.vue'
import LLMEvaluation from '@/components/detect/LLMEvaluation.vue'

const currentVideoType = ref<'2d' | 'panoramic' | null>(null)

const handleUploadComplete = (file: File, videoType: '2d' | 'panoramic', targetFps: number) => {
  currentVideoType.value = videoType
  console.log('上传完成:', file.name, '类型:', videoType, '目标帧率:', targetFps)
  // TODO: 调用后端接口进行实际检测，传递 targetFps 参数
}
</script>

<template>
  <div class="detect-page">
    <header class="page-header">
      <h1 class="page-title">开始检测</h1>
      <p class="page-desc">上传视频，AI 将自动分析其中的显著性区域</p>
    </header>

    <div class="detect-content">
      <!-- 视频上传 -->
      <VideoUpload @upload-complete="handleUploadComplete" />

      <!-- 检测结果视频 -->
      <ResultVideo />

      <!-- 大模型评价 -->
      <LLMEvaluation />
    </div>
  </div>
</template>

<style scoped>
.detect-page {
  max-width: 1000px;
  margin: 0 auto;
  padding-bottom: 2rem;
}

.page-header {
  margin-bottom: 1.5rem;
}

.page-title {
  font-size: 1.75rem;
  font-weight: 600;
  color: var(--color-heading);
  margin-bottom: 0.5rem;
}

.page-desc {
  color: var(--color-text);
  opacity: 0.8;
}

.detect-content {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}
</style>
