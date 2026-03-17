<script setup lang="ts">
interface Props {
  detectCount: number
  storageUsed: number // GB
  storageTotal: number // GB
}

const props = defineProps<Props>()

const storagePercent = Math.round((props.storageUsed / props.storageTotal) * 100)
</script>

<template>
  <div class="user-stats">
    <h3 class="section-title">我的数据</h3>
    <div class="stats-grid">
      <div class="stat-item">
        <div class="stat-icon">📊</div>
        <div class="stat-info">
          <span class="stat-value">{{ detectCount }}</span>
          <span class="stat-label">检测次数</span>
        </div>
      </div>

      <div class="stat-item">
        <div class="stat-icon">💾</div>
        <div class="stat-info">
          <div class="storage-header">
            <span class="stat-value">{{ storageUsed }}</span>
            <span class="stat-unit">/ {{ storageTotal }} GB</span>
          </div>
          <div class="storage-bar">
            <div
              class="storage-fill"
              :style="{ width: storagePercent + '%' }"
              :class="{ 'storage-warning': storagePercent > 80 }"
            ></div>
          </div>
          <span class="stat-label">存储空间</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.user-stats {
  background: var(--color-background-soft);
  border-radius: 12px;
  padding: 1.25rem;
  border: 1px solid var(--color-border);
}

.section-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--color-heading);
  margin-bottom: 1rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
  background: var(--color-background);
  border-radius: 8px;
}

.stat-icon {
  font-size: 1.5rem;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  flex: 1;
}

.stat-value {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-heading);
}

.stat-unit {
  font-size: 0.8rem;
  color: var(--color-text);
  opacity: 0.6;
}

.stat-label {
  font-size: 0.75rem;
  color: var(--color-text);
  opacity: 0.6;
}

.storage-header {
  display: flex;
  align-items: baseline;
  gap: 0.25rem;
}

.storage-bar {
  height: 4px;
  background: var(--color-background-mute);
  border-radius: 2px;
  overflow: hidden;
  margin: 0.25rem 0;
}

.storage-fill {
  height: 100%;
  background: hsla(210, 80%, 45%, 1);
  border-radius: 2px;
  transition: width 0.3s ease;
}

.storage-warning {
  background: hsla(30, 90%, 50%, 1);
}

@media (max-width: 600px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
