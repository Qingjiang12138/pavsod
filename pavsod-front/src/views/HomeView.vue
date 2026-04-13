<script setup lang="ts">
import { onMounted, computed, watch } from 'vue'
import StatsCard from '@/components/home/StatsCard.vue'
import TrendChart from '@/components/home/TrendChart.vue'
import TypeDistribution from '@/components/home/TypeDistribution.vue'
import RecentRecords from '@/components/home/RecentRecords.vue'
import { useAuth } from '@/stores/auth'
import { useHome } from '@/stores/home'

const { user } = useAuth()
const { statsData, trendData, typeData, recentRecords, isLoading, error, loadHomeData } = useHome()

// 加载首页数据
const loadData = () => {
  if (user.value?.userId) {
    console.log('[HomeView] 加载数据，userId:', user.value.userId)
    loadHomeData(user.value.userId)
  } else {
    console.log('[HomeView] userId 不存在，跳过加载')
  }
}

// onMounted 尝试加载
onMounted(() => {
  loadData()
})

// 监听 user 变化，有值后再加载
watch(() => user.value?.userId, (newUserId) => {
  if (newUserId) {
    console.log('[HomeView] userId 变化，重新加载:', newUserId)
    loadHomeData(newUserId)
  }
})

// 计算统计数据（添加默认值防止 null）
const displayStats = computed(() => ({
  total: statsData.value?.total ?? { value: 0, unit: '个' },
  monthly: statsData.value?.monthly ?? { value: 0, unit: '个' },
  duration: statsData.value?.duration ?? { value: 0, unit: '小时' }
}))
</script>

<template>
  <div class="home-page">
    <!-- 顶部欢迎区域 -->
    <header class="welcome-section">
      <div class="welcome-text">
        <h1 class="welcome-title">欢迎回来</h1>
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
        :value="displayStats.total.value"
        :unit="displayStats.total.unit"
        color="blue"
      />
      <StatsCard
        label="本月检测"
        :value="displayStats.monthly.value"
        :unit="displayStats.monthly.unit"
        color="green"
      />
      <StatsCard
        label="累计处理时长"
        :value="displayStats.duration.value"
        :unit="displayStats.duration.unit"
        color="orange"
      />
    </section>

    <!-- 图表区域 -->
    <section class="charts-grid">
      <TrendChart :data="trendData.length ? trendData : []" />
      <TypeDistribution :normal="typeData?.normal ?? 0" :panoramic="typeData?.panoramic ?? 0" />
    </section>

    <!-- 最近记录 -->
    <section class="records-section">
      <RecentRecords :records="recentRecords ?? []" />
    </section>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-overlay">
      <div class="loading-spinner">加载中...</div>
    </div>

    <!-- 错误提示 -->
    <div v-if="error" class="error-toast">
      {{ error }}
    </div>
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

/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.loading-spinner {
  padding: 1rem 2rem;
  background: var(--color-background-soft);
  border-radius: 8px;
  font-size: 0.9rem;
  color: var(--color-text);
}

/* 错误提示 */
.error-toast {
  position: fixed;
  top: 1rem;
  left: 50%;
  transform: translateX(-50%);
  padding: 0.75rem 1.5rem;
  background: hsla(0, 70%, 50%, 0.9);
  color: white;
  border-radius: 8px;
  font-size: 0.9rem;
  z-index: 1001;
}
</style>
