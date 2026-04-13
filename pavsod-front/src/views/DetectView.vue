<script setup lang="ts">
import { ref } from 'vue'
import VideoUpload from '@/components/detect/VideoUpload.vue'
import ResultVideo from '@/components/detect/ResultVideo.vue'
import LLMEvaluation from '@/components/detect/LLMEvaluation.vue'
import { getAIEvaluation, getDetectionResult } from '@/api/detect'
import { useAuth } from '@/stores/auth'

const { user } = useAuth()

const currentVideoType = ref<'2d' | 'panoramic' | null>(null)
const currentVideoId = ref<string | null>(null)
const originalUrl = ref('')
const saliencyUrl = ref('')
const aiEvaluation = ref('')
const isLoadingResult = ref(false)
const resultError = ref('')

const handleUploadComplete = async (file: File, videoType: '2d' | 'panoramic', targetFps: number, videoId: string) => {
  currentVideoType.value = videoType
  currentVideoId.value = videoId
  console.log('上传完成:', file.name, '类型:', videoType, '目标帧率:', targetFps, '视频ID:', videoId)

  if (!user.value?.userId) {
    resultError.value = '用户信息不存在，无法获取检测结果'
    return
  }

  isLoadingResult.value = true
  resultError.value = ''

  try {
    // 并行获取 AI 评价和检测视频 URL
    const [evalRes, detectRes] = await Promise.all([
      getAIEvaluation(user.value.userId, videoId),
      getDetectionResult(user.value.userId, videoId),
    ])

    originalUrl.value = detectRes.originalUrl
    saliencyUrl.value = detectRes.saliencyUrl
    aiEvaluation.value = evalRes.aiSuggestion
  } catch (error: any) {
    console.error('获取检测结果失败:', error)
    resultError.value = '获取检测结果失败: ' + error.message
  } finally {
    isLoadingResult.value = false
  }
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

      <!-- 加载检测结果中 -->
      <div v-if="isLoadingResult" class="result-loading">
        <div class="loading-spinner">正在加载检测结果...</div>
      </div>

      <!-- 错误提示 -->
      <div v-else-if="resultError" class="result-error">
        {{ resultError }}
      </div>

      <!-- 检测结果占位 -->
      <div v-else-if="!currentVideoId" class="result-placeholder">
        <div class="placeholder-card">
          <h3 class="placeholder-title">🎬 检测结果</h3>
          <p class="placeholder-text">上传视频后，将在此处展示原始视频与显著性检测视频的对比</p>
        </div>
        <div class="placeholder-card">
          <h3 class="placeholder-title">🤖 AI 智能评价</h3>
          <p class="placeholder-text">上传视频后，AI 将自动生成对检测效果的专业评价</p>
        </div>
      </div>

      <!-- 检测结果视频 -->
      <ResultVideo
        v-else
        :original-url="originalUrl"
        :saliency-url="saliencyUrl"
      />

      <!-- 大模型评价 -->
      <LLMEvaluation
        v-if="currentVideoId"
        :evaluation="aiEvaluation"
      />
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

.result-loading,
.result-error {
  background: var(--color-background-soft);
  border-radius: 12px;
  padding: 2rem;
  text-align: center;
  border: 1px solid var(--color-border);
}

.result-error {
  color: hsla(0, 70%, 50%, 1);
}

.result-placeholder {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.placeholder-card {
  background: var(--color-background-soft);
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid var(--color-border);
  text-align: center;
}

.placeholder-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--color-heading);
  margin-bottom: 0.75rem;
}

.placeholder-text {
  font-size: 0.9rem;
  color: var(--color-text);
  opacity: 0.7;
  margin: 0;
}
</style>
