<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import RecordFilters from '@/components/records/RecordFilters.vue'
import RecordTable from '@/components/records/RecordTable.vue'
import Pagination from '@/components/records/Pagination.vue'
import { useAuth } from '@/stores/auth'
import { fetchRecords, type RecordItem } from '@/api/record'

interface VideoRecord {
  id: string
  name: string
  thumbnail?: string
  detectTime: string
  duration: string
  status: 'completed' | 'processing' | 'pending' | 'failed'
}

const { user } = useAuth()
const router = useRouter()

// 筛选状态
const searchQuery = ref('')
const selectedDate = ref('')

// 分页状态
const currentPage = ref(1)
const pageSize = ref(5)
const totalRecords = ref(0)

// 数据状态
const allRecords = ref<VideoRecord[]>([])
const isLoading = ref(false)
const error = ref('')

// 选中项
const selectedIds = ref<string[]>([])

// 状态映射（与 stores/home.ts 保持一致）
const statusMap: Record<string, 'pending' | 'processing' | 'completed' | 'failed'> = {
  '0': 'pending',
  '1': 'processing',
  '2': 'completed',
  '3': 'failed',
  'completed': 'completed',
  'failed': 'failed',
  'pending': 'pending',
  'processing': 'processing'
}

// 将秒数格式化为 mm:ss
const formatDuration = (seconds: number): string => {
  const m = Math.floor(seconds / 60)
  const s = Math.floor(seconds % 60)
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

// 将后端记录转换为前端格式
const mapRecord = (item: RecordItem): VideoRecord => {
  const datePart = item.task_create_at ? item.task_create_at.slice(0, 10) : '-'
  return {
    id: String(item.video_id),
    name: item.video_name,
    thumbnail: item.video_cover || undefined,
    detectTime: datePart,
    duration: formatDuration(item.video_duration || 0),
    status: statusMap[String(item.video_status)] || 'processing'
  }
}

// 加载记录数据
const loadRecords = async () => {
  if (!user.value?.userId) {
    error.value = '用户信息不存在'
    return
  }

  isLoading.value = true
  error.value = ''

  try {
    const res = await fetchRecords({
      userId: user.value.userId,
      start_page: currentPage.value,
      per_page: pageSize.value,
      find_name: searchQuery.value || undefined,
      find_date: selectedDate.value || undefined
    })

    allRecords.value = (res.record_list || []).map(mapRecord)
    totalRecords.value = res.total || 0

    // 如果当前页没有数据且不是第一页，自动回退到上一页
    if (allRecords.value.length === 0 && currentPage.value > 1) {
      currentPage.value--
      await loadRecords()
      return
    }
  } catch (err: any) {
    error.value = err.message || '获取检测记录失败'
    allRecords.value = []
  } finally {
    isLoading.value = false
  }
}

const totalPages = computed(() => {
  return Math.ceil(totalRecords.value / pageSize.value) || 1
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
  if (selectedIds.value.length === allRecords.value.length) {
    selectedIds.value = []
  } else {
    selectedIds.value = allRecords.value.map(r => r.id)
  }
}

// 处理操作
const viewResult = (id: string) => {
  router.push(`/detect?result=${id}`)
}

const deleteRecord = (id: string) => {
  console.log('删除记录:', id)
  // TODO: 接入删除接口
  allRecords.value = allRecords.value.filter(r => r.id !== id)
  selectedIds.value = selectedIds.value.filter(sid => sid !== id)
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  selectedIds.value = []
  loadRecords()
}

// 处理分页
const changePage = (page: number) => {
  currentPage.value = page
  selectedIds.value = []
  loadRecords()
}


// 页面加载时获取数据
onMounted(() => {
  if (user.value?.userId) {
    loadRecords()
  }
})
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
      @update-search="searchQuery = $event"
      @search="handleSearch"
      @update-date="selectedDate = $event"
    />

    <div v-if="error" class="error-msg">{{ error }}</div>

    <div class="table-wrapper" :class="{ loading: isLoading }">
      <RecordTable
        :records="allRecords"
        :selected-ids="selectedIds"
        @toggle-select="toggleSelect"
        @toggle-select-all="toggleSelectAll"
        @view-result="viewResult"
        @delete="deleteRecord"
      />
    </div>

    <Pagination
      :total="totalRecords"
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

.error-msg {
  margin-bottom: 1rem;
  padding: 0.75rem 1rem;
  background: hsla(0, 70%, 50%, 0.1);
  color: hsla(0, 70%, 50%, 1);
  border-radius: 8px;
  font-size: 0.875rem;
}

.table-wrapper.loading {
  opacity: 0.6;
  pointer-events: none;
}
</style>
