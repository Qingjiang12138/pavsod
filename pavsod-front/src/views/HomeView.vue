<script setup lang="ts">
import StatsCard from '@/components/home/StatsCard.vue'
import TrendChart from '@/components/home/TrendChart.vue'
import TypeDistribution from '@/components/home/TypeDistribution.vue'
import RecentRecords from '@/components/home/RecentRecords.vue'

// 模拟数据 - 实际项目中应从 API 获取
const statsData = {
  total: { value: 128, trend: 12, unit: '个' },
  monthly: { value: 12, trend: 20, unit: '个' },
  quality: { value: 0.89, trend: 3, unit: '' },
  duration: { value: 48, unit: '小时' }
}

const trendData = [
  { date: '周一', count: 3 },
  { date: '周二', count: 5 },
  { date: '周三', count: 2 },
  { date: '周四', count: 8 },
  { date: '周五', count: 6 },
  { date: '周六', count: 4 },
  { date: '周日', count: 7 }
]

const typeData = {
  normal: 85,
  panoramic: 43
}

const recentRecords = [
  { id: '1', name: 'concert_video_360.mp4', date: '2小时前', status: 'completed' as const },
  { id: '2', name: 'product_demo.mp4', date: '昨天', status: 'completed' as const },
  { id: '3', name: 'vr_tour_panoramic.mp4', date: '3天前', status: 'processing' as const },
  { id: '4', name: 'meeting_recording.mp4', date: '5天前', status: 'completed' as const },
  { id: '5', name: 'sports_highlights.mp4', date: '1周前', status: 'failed' as const }
]
</script>

<template>
  <div class="home-page">
    <!-- 顶部欢迎区域 -->
    <header class="welcome-section">
      <div class="welcome-text">
        <h1 class="welcome-title">欢迎回来 👋</h1>
        <p class="welcome-desc">这是您今日的检测数据概览</p>
      </div>
      <router-link to="/detect" class="detect-btn">
        <span>+ 开始新检测</span>
      </router-link>
    </header>

    <!-- 核心统计卡片 -->
    <section class="stats-grid">
      <StatsCard
        label="累计检测视频"
        :value="statsData.total.value"
        :unit="statsData.total.unit"
        :trend="statsData.total.trend"
        trendLabel="较上月"
        icon="📊"
        color="blue"
      />
      <StatsCard
        label="本月检测"
        :value="statsData.monthly.value"
        :unit="statsData.monthly.unit"
        :trend="statsData.monthly.trend"
        trendLabel="较上月"
        icon="📈"
        color="green"
      />
      <StatsCard
        label="累计处理时长"
        :value="statsData.duration.value"
        :unit="statsData.duration.unit"
        icon="⏱️"
        color="orange"
      />
    </section>

    <!-- 图表区域 -->
    <section class="charts-grid">
      <TrendChart :data="trendData" />
      <TypeDistribution :normal="typeData.normal" :panoramic="typeData.panoramic" />
    </section>

    <!-- 最近记录 -->
    <section class="records-section">
      <RecentRecords :records="recentRecords" />
    </section>
  </div>
</template>

<style scoped>
.home-page {
  max-width: 1000px;
  margin: 0 auto;
  padding-bottom: 2rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

/* 欢迎区域 */
.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.welcome-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--color-heading);
  margin-bottom: 0.25rem;
}

.welcome-desc {
  font-size: 0.9rem;
  color: var(--color-text);
  opacity: 0.7;
}

.detect-btn {
  display: inline-flex;
  align-items: center;
  padding: 0.625rem 1.25rem;
  background: hsla(210, 80%, 45%, 1);
  color: white;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s;
}

.detect-btn:hover {
  background: hsla(210, 80%, 40%, 1);
  transform: translateY(-1px);
}

/* 统计卡片网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
}

/* 图表区域 */
.charts-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 1rem;
}

/* 响应式 */
@media (max-width: 900px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .welcome-section {
    flex-direction: column;
    align-items: flex-start;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .detect-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
