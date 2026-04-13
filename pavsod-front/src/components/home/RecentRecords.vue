<script setup lang="ts">
import { computed } from 'vue'

interface Record {
  id: string
  name: string
  date: string
  status: 'completed' | 'processing' | 'failed'
  thumbnail?: string
}

interface Props {
  records: Record[]
  title?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: '最近检测记录'
})

const statusMap = {
  completed: { text: '已完成', class: 'status-completed' },
  processing: { text: '处理中', class: 'status-processing' },
  failed: { text: '失败', class: 'status-failed' }
}

// 判断是否无数据
const isEmpty = computed(() => {
  return !props.records || props.records.length === 0
})
</script>

<template>
  <div class="recent-records">
    <div class="records-header">
      <h3 class="chart-title">{{ title }}</h3>
      <router-link to="/records" class="view-all">查看全部 →</router-link>
    </div>
    <!-- 空状态 -->
    <div v-if="isEmpty" class="empty-state">
      <div class="empty-icon">📝</div>
      <p class="empty-text">暂无检测记录</p>
      <p class="empty-subtext">开始您的第一次检测吧</p>
    </div>

    <div v-else class="records-list">
      <div
        v-for="record in records"
        :key="record.id"
        class="record-item"
      >
        <div class="record-thumb">
          <span v-if="!record.thumbnail">🎬</span>
          <img v-else :src="record.thumbnail" :alt="record.name" />
        </div>
        <div class="record-info">
          <span class="record-name" :title="record.name">{{ record.name }}</span>
          <span class="record-date">{{ record.date }}</span>
        </div>
        <span class="record-status" :class="statusMap[record.status].class">
          {{ statusMap[record.status].text }}
        </span>
        <router-link
          v-if="record.status === 'completed'"
          :to="`/detect?result=${record.id}`"
          class="record-action"
        >
          查看结果
        </router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.recent-records {
  background: var(--color-background-soft);
  border-radius: 12px;
  padding: 1.25rem;
  border: 1px solid var(--color-border);
}

/* 空状态样式 */
.empty-state {
  padding: 2rem 1rem;
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

.records-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.chart-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--color-heading);
}

.view-all {
  font-size: 0.8rem;
  color: hsla(210, 80%, 45%, 1);
  text-decoration: none;
}

.view-all:hover {
  text-decoration: underline;
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.record-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
  background: var(--color-background);
  border-radius: 8px;
  transition: all 0.2s;
}

.record-item:hover {
  background: var(--color-background-mute);
}

.record-thumb {
  width: 48px;
  height: 48px;
  background: var(--color-background-mute);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  flex-shrink: 0;
  overflow: hidden;
}

.record-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.record-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  min-width: 0;
}

.record-name {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-heading);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.record-date {
  font-size: 0.75rem;
  color: var(--color-text);
  opacity: 0.6;
}

.record-status {
  font-size: 0.7rem;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-weight: 500;
}

.status-completed {
  background: hsla(150, 60%, 45%, 0.15);
  color: hsla(150, 60%, 45%, 1);
}

.status-processing {
  background: hsla(210, 80%, 45%, 0.15);
  color: hsla(210, 80%, 45%, 1);
}

.status-failed {
  background: hsla(0, 70%, 50%, 0.15);
  color: hsla(0, 70%, 50%, 1);
}

.record-action {
  font-size: 0.75rem;
  color: hsla(210, 80%, 45%, 1);
  text-decoration: none;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  transition: background 0.2s;
}

.record-action:hover {
  background: hsla(210, 80%, 45%, 0.1);
}
</style>
