<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  normal: number
  panoramic: number
  title?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: '视频类型分布'
})

// 判断是否无数据（两种类型都为 0）
const isEmpty = computed(() => {
  return props.normal === 0 && props.panoramic === 0
})

// 计算总数
const total = computed(() => props.normal + props.panoramic)
</script>

<template>
  <div class="type-distribution">
    <h3 class="chart-title">{{ title }}</h3>

    <!-- 空状态 -->
    <div v-if="isEmpty" class="empty-state">
      <div class="empty-icon">🎬</div>
      <p class="empty-text">暂无视频数据</p>
      <p class="empty-subtext">上传视频后将显示分布</p>
    </div>

    <div v-else class="chart-content">
      <div class="pie-chart">
        <div
          class="pie"
          :style="{
            background: `conic-gradient(
              hsla(210, 80%, 45%, 1) 0deg ${(normal / total) * 360}deg,
              hsla(260, 60%, 50%, 1) ${(normal / total) * 360}deg 360deg
            )`
          }"
        >
          <div class="pie-center">
            <span class="total">{{ total }}</span>
            <span class="total-label">总数</span>
          </div>
        </div>
      </div>
      <div class="legend">
        <div class="legend-item">
          <div class="legend-color" style="background: hsla(210, 80%, 45%, 1)"></div>
          <div class="legend-info">
            <span class="legend-label">平面视频</span>
            <span class="legend-value">{{ normal }}</span>
          </div>
        </div>
        <div class="legend-item">
          <div class="legend-color" style="background: hsla(260, 60%, 50%, 1)"></div>
          <div class="legend-info">
            <span class="legend-label">360° 全景</span>
            <span class="legend-value">{{ panoramic }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.type-distribution {
  background: var(--color-background-soft);
  border-radius: 12px;
  padding: 1.25rem;
  border: 1px solid var(--color-border);
}

.chart-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--color-heading);
  margin-bottom: 1rem;
}

/* 空状态样式 */
.empty-state {
  height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.empty-icon {
  font-size: 2rem;
  opacity: 0.4;
}

.empty-text {
  font-size: 0.9rem;
  color: var(--color-text);
  opacity: 0.6;
  font-weight: 500;
}

.empty-subtext {
  font-size: 0.75rem;
  color: var(--color-text);
  opacity: 0.4;
}

.chart-content {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.pie-chart {
  flex-shrink: 0;
}

.pie {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  position: relative;
}

.pie-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 70px;
  height: 70px;
  background: var(--color-background-soft);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.total {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--color-heading);
  line-height: 1;
}

.total-label {
  font-size: 0.65rem;
  color: var(--color-text);
  opacity: 0.6;
}

.legend {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 3px;
}

.legend-info {
  display: flex;
  justify-content: space-between;
  flex: 1;
  gap: 0.5rem;
}

.legend-label {
  font-size: 0.8rem;
  color: var(--color-text);
}

.legend-value {
  font-size: 0.8rem;
  font-weight: 600;
  color: var(--color-heading);
}
</style>
