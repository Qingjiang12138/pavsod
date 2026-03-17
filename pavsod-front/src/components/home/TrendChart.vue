<script setup lang="ts">
interface DayData {
  date: string
  count: number
}

interface Props {
  data: DayData[]
  title?: string
}

withDefaults(defineProps<Props>(), {
  title: '近7天检测趋势'
})

const maxValue = (data: DayData[]) => {
  return Math.max(...data.map(d => d.count), 1)
}
</script>

<template>
  <div class="trend-chart">
    <h3 class="chart-title">{{ title }}</h3>
    <div class="chart-container">
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
