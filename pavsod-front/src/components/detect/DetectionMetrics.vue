<script setup lang="ts">
import { computed } from 'vue'

interface Metric {
  value: number
  label: string
  labelEn: string
  key: string
}

// 检测指标数据（默认值）
const metrics = computed<Metric[]>(() => [
  { key: 'fMeasure', value: 0.892, label: 'F-measure', labelEn: 'F-measure' },
  { key: 'mae', value: 0.034, label: 'MAE', labelEn: 'Mean Absolute Error' },
  { key: 'sMeasure', value: 0.915, label: 'S-measure', labelEn: 'S-measure' },
  { key: 'eMeasure', value: 0.938, label: 'E-measure', labelEn: 'E-measure' },
])

// 获取指标的最大值（用于计算进度条比例）
const getMaxValue = (key: string): number => {
  switch (key) {
    case 'mae':
      return 0.5 // MAE 通常范围 0-0.5
    default:
      return 1.0 // 其他指标范围 0-1
  }
}

// 格式化指标值（统一显示为质量分数百分比）
const formatMetric = (value: number, key: string) => {
  const score = getQualityScore(value, key)
  return (score * 100).toFixed(1)
}

// 获取指标颜色
const getMetricColor = (value: number, key: string) => {
  const score = getQualityScore(value, key)
  if (score >= 0.9) return '#22c55e' // 绿色
  if (score >= 0.8) return '#3b82f6' // 蓝色
  if (score >= 0.7) return '#f59e0b' // 橙色
  return '#ef4444' // 红色
}

// 获取质量分数（0-1，用于进度条和颜色）
const getQualityScore = (value: number, key: string): number => {
  const max = getMaxValue(key)
  if (key === 'mae') {
    // MAE 越低越好，反向计算质量分数
    return Math.max(0, 1 - value / max)
  }
  // 其他指标越高越好
  return Math.min(1, value / max)
}
</script>

<template>
  <section class="metrics-section">
    <div class="metrics-row">
      <div
        v-for="metric in metrics"
        :key="metric.key"
        class="metric-card"
      >
        <div class="metric-labels">
          <span class="metric-label-cn">{{ metric.label }}</span>
          <span class="metric-label-en">{{ metric.labelEn }}</span>
        </div>

        <div
          class="metric-value"
          :style="{ color: getMetricColor(metric.value, metric.key) }"
        >
          {{ formatMetric(metric.value, metric.key) }}
        </div>
        
        <div class="metric-progress">
          <div class="progress-track">
            <div
              class="progress-fill"
              :style="{
                width: (getQualityScore(metric.value, metric.key) * 100) + '%',
                background: getMetricColor(metric.value, metric.key)
              }"
            ></div>
          </div>
        </div>
        
      </div>
    </div>
  </section>
</template>

<style scoped>
.metrics-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
}

.metric-card {
  background: var(--color-background-soft);
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  transition: all 0.2s;
}

.metric-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.metric-labels {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  margin-bottom: 1rem;
}

.metric-label-cn {
  font-size: 1rem;
  font-weight: 500;
  color: var(--color-heading);
}

.metric-label-en {
  font-size: 0.75rem;
  color: var(--color-text);
  opacity: 0.6;
}

.metric-value {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  line-height: 1;
}

.metric-progress {
  width: 100%;
  padding: 0 0.5rem;
}

.progress-track {
  height: 6px;
  background: var(--color-background-mute);
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 1s ease;
}

/* 响应式 */
@media (max-width: 900px) {
  .metrics-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .metric-value {
    font-size: 2rem;
  }
}

@media (max-width: 480px) {
  .metrics-row {
    grid-template-columns: 1fr;
  }
}
</style>
