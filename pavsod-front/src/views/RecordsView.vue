<script setup lang="ts">
import { ref, computed } from 'vue'
import RecordFilters from '@/components/records/RecordFilters.vue'
import RecordTable from '@/components/records/RecordTable.vue'
import Pagination from '@/components/records/Pagination.vue'

interface Record {
  id: string
  name: string
  thumbnail?: string
  detectTime: string
  duration: string
  status: 'completed' | 'processing' | 'failed'
}

// 模拟数据
const allRecords = ref<Record[]>([
  { id: '1', name: 'concert_video_360.mp4', thumbnail: '', detectTime: '2025-03-17 14:30', duration: '05:23', status: 'completed' },
  { id: '2', name: 'product_demo.mp4', thumbnail: '', detectTime: '2025-03-17 12:15', duration: '03:45', status: 'completed' },
  { id: '3', name: 'vr_tour_panoramic.mp4', thumbnail: '', detectTime: '2025-03-16 18:20', duration: '12:08', status: 'processing' },
  { id: '4', name: 'meeting_recording.mp4', thumbnail: '', detectTime: '2025-03-16 10:00', duration: '45:30', status: 'completed' },
  { id: '5', name: 'sports_highlights.mp4', thumbnail: '', detectTime: '2025-03-15 16:45', duration: '08:15', status: 'failed' },
  { id: '6', name: 'tutorial_video.mp4', thumbnail: '', detectTime: '2025-03-15 09:30', duration: '15:20', status: 'completed' },
  { id: '7', name: 'interview_clip.mp4', thumbnail: '', detectTime: '2025-03-14 14:00', duration: '06:45', status: 'completed' },
  { id: '8', name: 'gameplay_360.mp4', thumbnail: '', detectTime: '2025-03-14 11:20', duration: '22:10', status: 'processing' },
])

// 筛选状态
const searchQuery = ref('')
const selectedDate = ref('')

// 分页状态
const currentPage = ref(1)
const pageSize = ref(5)

// 选中项
const selectedIds = ref<string[]>([])

// 过滤后的记录
const filteredRecords = computed(() => {
  let result = allRecords.value

  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(r => r.name.toLowerCase().includes(query))
  }

  // 日期过滤
  if (selectedDate.value) {
    result = result.filter(r => r.detectTime.startsWith(selectedDate.value))
  }

  return result
})

// 分页后的记录
const paginatedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredRecords.value.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(filteredRecords.value.length / pageSize.value) || 1
})

// 处理选择
const toggleSelect = (id: string) => {
  const index = selectedIds.value.indexOf(id)
  if (index > -1) {
    selectedIds.value.splice(index, 1)
  } else {
    selectedIds.value.push(id)
  }
}

const toggleSelectAll = () => {
  if (selectedIds.value.length === paginatedRecords.value.length) {
    selectedIds.value = []
  } else {
    selectedIds.value = paginatedRecords.value.map(r => r.id)
  }
}

// 处理操作
const viewResult = (id: string) => {
  console.log('查看结果:', id)
  // TODO: 跳转到结果页面
}

const deleteRecord = (id: string) => {
  console.log('删除记录:', id)
  allRecords.value = allRecords.value.filter(r => r.id !== id)
  selectedIds.value = selectedIds.value.filter(sid => sid !== id)
}

// 处理分页
const changePage = (page: number) => {
  currentPage.value = page
  selectedIds.value = []
}
</script>

<template>
  <div class="records-page">
    <header class="page-header">
      <h1 class="page-title">检测记录</h1>
      <p class="page-desc">查看和管理您的历史检测记录</p>
    </header>

    <RecordFilters
      :search-query="searchQuery"
      :selected-date="selectedDate"
      @update-search="searchQuery = $event; currentPage = 1"
      @update-date="selectedDate = $event; currentPage = 1"
    />

    <RecordTable
      :records="paginatedRecords"
      :selected-ids="selectedIds"
      @toggle-select="toggleSelect"
      @toggle-select-all="toggleSelectAll"
      @view-result="viewResult"
      @delete="deleteRecord"
    />

    <Pagination
      :total="filteredRecords.length"
      :current-page="currentPage"
      :page-size="pageSize"
      :total-pages="totalPages"
      @change-page="changePage"
    />
  </div>
</template>

<style scoped>
.records-page {
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 1.5rem;
}

.page-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--color-heading);
  margin-bottom: 0.5rem;
}

.page-desc {
  color: var(--color-text);
  opacity: 0.8;
}
</style>
