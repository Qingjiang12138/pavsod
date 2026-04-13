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

const isEmpty = computed(() => {
  return !props.data || props.data.length === 0 || props.data.every(d => d.count === 0)
})

const maxValue = computed(() => {
  if (isEmpty.value) return 1
  return Math.max(...props.data.map(d => d.count), 1)
})

// SVG 配置
const viewBoxW = 1000
const viewBoxH = 320
const padding = { top: 40, right: 40, bottom: 60, left: 40 }
const chartW = viewBoxW - padding.left - padding.right
const chartH = viewBoxH - padding.top - padding.bottom

const points = computed(() => {
  return props.data.map((item, index) => {
    const x = props.data.length === 1
      ? padding.left + chartW / 2
      : padding.left + (index / (props.data.length - 1)) * chartW
    const y = padding.top + chartH - (item.count / maxValue.value) * chartH
    return { x, y, value: item.count, label: item.date }
  })
})

const linePath = computed(() => {
  if (points.value.length === 0) return ''
  return points.value
    .map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`)
    .join(' ')
})

const areaPath = computed(() => {
  if (points.value.length === 0) return ''
  const first = points.value[0]
  const last = points.value[points.value.length - 1]
  if (!first || !last) return ''
  const bottomY = padding.top + chartH
  return `${linePath.value} L ${last.x} ${bottomY} L ${first.x} ${bottomY} Z`
})

// Y轴刻度（3~4条）
const yTicks = computed(() => {
  const max = maxValue.value
  const tickCount = 4
  return Array.from({ length: tickCount + 1 }, (_, i) => ({
    value: Math.round((max / tickCount) * i),
    y: padding.top + chartH - (i / tickCount) * chartH
  }))
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

    <div v-else class="chart-wrapper">
      <svg
        class="line-chart"
        :viewBox="`0 0 ${viewBoxW} ${viewBoxH}`"
        preserveAspectRatio="none"
      >
        <defs>
          <linearGradient id="lineGradient" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stop-color="hsla(210, 80%, 60%, 0.35)" />
            <stop offset="100%" stop-color="hsla(210, 80%, 60%, 0.02)" />
          </linearGradient>
        </defs>

        <!-- 网格线 -->
        <g class="grid-lines">
          <line
            v-for="tick in yTicks"
            :key="tick.y"
            :x1="padding.left"
            :y1="tick.y"
            :x2="viewBoxW - padding.right"
            :y2="tick.y"
          />
        </g>

        <!-- Y轴刻度值 -->
        <g class="y-ticks">
          <text
            v-for="tick in yTicks"
            :key="tick.y"
            :x="padding.left - 12"
            :y="tick.y + 5"
            text-anchor="end"
          >{{ tick.value }}</text>
        </g>

        <!-- 面积填充 -->
        <path :d="areaPath" fill="url(#lineGradient)" />

        <!-- 折线 -->
        <path :d="linePath" class="line-path" fill="none" />

        <!-- 数据点 -->
        <g class="data-points">
          <circle
            v-for="p in points"
            :key="p.x"
            :cx="p.x"
            :cy="p.y"
            r="6"
            class="point-circle"
          />
        </g>

        <!-- X轴标签 -->
        <g class="x-labels">
          <text
            v-for="p in points"
            :key="p.label"
            :x="p.x"
            :y="viewBoxH - 20"
            text-anchor="middle"
          >{{ p.label }}</text>
        </g>

        <!-- 数值标签 -->
        <g class="value-labels">
          <text
            v-for="p in points"
            :key="`v-${p.x}`"
            :x="p.x"
            :y="p.y - 14"
            text-anchor="middle"
          >{{ p.value }}</text>
        </g>
      </svg>
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

/* 折线图容器 */
.chart-wrapper {
  height: 180px;
  position: relative;
}

.line-chart {
  width: 100%;
  height: 100%;
  overflow: visible;
}

/* 网格线 */
.grid-lines line {
  stroke: var(--color-border);
  stroke-width: 1;
  stroke-dasharray: 4 4;
}

/* 折线 */
.line-path {
  stroke: hsla(210, 80%, 55%, 1);
  stroke-width: 3;
  stroke-linecap: round;
  stroke-linejoin: round;
}

/* 数据点 */
.point-circle {
  fill: hsla(210, 80%, 55%, 1);
  stroke: var(--color-background-soft);
  stroke-width: 2;
}

/* Y轴刻度 */
.y-ticks text {
  font-size: 18px;
  fill: var(--color-text);
  opacity: 0.5;
}

/* X轴标签 */
.x-labels text {
  font-size: 20px;
  fill: var(--color-text);
  opacity: 0.7;
}

/* 数值标签 */
.value-labels text {
  font-size: 20px;
  fill: var(--color-heading);
  font-weight: 600;
}
</style>
