<script setup lang="ts">
interface Props {
  label: string
  value: string | number
  unit?: string
  trend?: number // 正数为增长，负数为下降
  trendLabel?: string
  icon?: string
  color?: 'blue' | 'green' | 'purple' | 'orange'
}

withDefaults(defineProps<Props>(), {
  color: 'blue'
})

const colorMap = {
  blue: 'hsla(210, 80%, 45%, 1)',
  green: 'hsla(150, 60%, 45%, 1)',
  purple: 'hsla(260, 60%, 50%, 1)',
  orange: 'hsla(30, 90%, 50%, 1)'
}
</script>

<template>
  <div class="stats-card">
    <div v-if="icon" class="card-icon" :style="{ background: colorMap[color] + '15', color: colorMap[color] }">
      {{ icon }}
    </div>
    <div class="card-content">
      <span class="card-label">{{ label }}</span>
      <div class="card-value-row">
        <span class="card-value">{{ value }}</span>
        <span v-if="unit" class="card-unit">{{ unit }}</span>
      </div>
      <div v-if="trend !== undefined" class="card-trend" :class="{ 'trend-up': trend > 0, 'trend-down': trend < 0 }">
        <span class="trend-arrow">{{ trend > 0 ? '↑' : trend < 0 ? '↓' : '→' }}</span>
        <span>{{ Math.abs(trend) }}%</span>
        <span v-if="trendLabel" class="trend-label">{{ trendLabel }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.stats-card {
  background: var(--color-background-soft);
  border-radius: 12px;
  padding: 1.25rem;
  border: 1px solid var(--color-border);
  display: flex;
  align-items: center;
  gap: 1rem;
  transition: all 0.2s;
}

.stats-card:hover {
  border-color: var(--color-border-hover);
  transform: translateY(-2px);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  flex-shrink: 0;
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  flex: 1;
}

.card-label {
  font-size: 0.8rem;
  color: var(--color-text);
  opacity: 0.7;
}

.card-value-row {
  display: flex;
  align-items: baseline;
  gap: 0.25rem;
}

.card-value {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--color-heading);
  line-height: 1.2;
}

.card-unit {
  font-size: 0.875rem;
  color: var(--color-text);
  opacity: 0.6;
}

.card-trend {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.75rem;
  color: var(--color-text);
  opacity: 0.7;
}

.trend-up {
  color: hsla(150, 60%, 45%, 1);
  opacity: 1;
}

.trend-down {
  color: hsla(0, 70%, 50%, 1);
  opacity: 1;
}

.trend-label {
  margin-left: 0.25rem;
  opacity: 0.7;
}
</style>
