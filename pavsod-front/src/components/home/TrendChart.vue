<script setup lang="ts">
import { computed } from 'vue'

interface DayData {
  date: string
  count: number
}

interface Props {
  data: DayData[]
  title?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: '近7天检测趋势'
})

const maxValue = (data: DayData[]) => {
  return Math.max(...data.map(d => d.count), 1)
}

// 判断是否无数据（数组为空或所有 count 都为 0）
const isEmpty = computed(() => {
  return !props.data || props.data.length === 0 || props.data.every(d => d.count === 0)
})
</script>

<template>
  <div class="trend-chart">
    <h3 class="chart-title">{{ title }}</h3>

    <!-- 空状态 -->
    <div v-if="isEmpty" class="empty-state">
      <div class="empty-icon">📊</div>
      <p class="empty-text">暂无检测数据</p>
      <p class="empty-subtext">近7天没有检测记录</p>
    </div>

    <div v-else class="chart-container">
      <div class="chart-bars">
        <div
          v-for="item in data"
          :key="item.date"
          class="bar-wrapper"
        >
          <div class="bar-track">
            <div
              class="bar-fill"
              :style="{ height: (item.count / maxValue(data) * 100) + '%' }"
            >
              <span v-if="item.count > 0" class="bar-value">{{ item.count }}</span>
            </div>
          </div>
          <span class="bar-label">{{ item.date }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.trend-chart {
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
  height: 160px;
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

.chart-container {
  height: 160px;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  height: 100%;
  gap: 0.5rem;
}

.bar-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.bar-track {
  flex: 1;
  width: 100%;
  background: var(--color-background-mute);
  border-radius: 4px;
  position: relative;
  overflow: hidden;
  min-height: 20px;
}

.bar-fill {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, hsla(210, 80%, 45%, 1), hsla(210, 80%, 60%, 1));
  border-radius: 4px;
  transition: height 0.5s ease;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 4px;
}

.bar-value {
  font-size: 0.7rem;
  color: white;
  font-weight: 600;
}

.bar-label {
  font-size: 0.7rem;
  color: var(--color-text);
  opacity: 0.6;
}
</style>
