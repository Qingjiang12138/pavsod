import { ref, computed } from 'vue'
import * as homeApi from '@/api/home'

const STORAGE_KEY = 'pavsod_home_data'
const STORAGE_TIME_KEY = 'pavsod_home_data_time'
const CACHE_DURATION = 5 * 60 * 1000 // 缓存5分钟

// 最近检测记录类型
export interface RecentRecord {
  video_id: string
  video_cover: string
  video_name: string
  video_url: string
  video_status: number | string // 0=处理中, completed=完成, failed=失败
  task_create_at: string
}

// 首页数据类型（与后端接口对应）
export interface HomeData {
  total_task: number
  month_task: number
  total_time: number // 总计秒数*10
  week_count: number[] // 过去一周检测数量 [周一, 周二, ..., 周日]
  '2d_video_count': number
  '3d_video_count': number
  current_storage?: number // 已用存储空间（字节）
  max_storage?: number // 最大存储空间（字节）
  create_at?: string // 用户注册日期
  last_records: RecentRecord[]
}

// 创建响应式状态
const homeData = ref<HomeData | null>(null)
const isLoading = ref(false)
const error = ref<string | null>(null)

// 初始化时检查 localStorage
const initHomeData = () => {
  const stored = localStorage.getItem(STORAGE_KEY)
  const storedTime = localStorage.getItem(STORAGE_TIME_KEY)

  if (stored && storedTime) {
    const cacheTime = parseInt(storedTime, 10)
    const now = Date.now()

    // 检查缓存是否过期
    if (now - cacheTime < CACHE_DURATION) {
      try {
        homeData.value = JSON.parse(stored)
      } catch {
        localStorage.removeItem(STORAGE_KEY)
        localStorage.removeItem(STORAGE_TIME_KEY)
      }
    } else {
      // 缓存过期，清除
      localStorage.removeItem(STORAGE_KEY)
      localStorage.removeItem(STORAGE_TIME_KEY)
    }
  }
}

// 保存数据到 localStorage
const saveHomeData = (data: HomeData) => {
  homeData.value = data
  localStorage.setItem(STORAGE_KEY, JSON.stringify(data))
  localStorage.setItem(STORAGE_TIME_KEY, Date.now().toString())
}

// 获取首页数据
const loadHomeData = async (userId: string, forceRefresh = false): Promise<void> => {
  // 如果有缓存且不强制刷新，直接返回
  if (!forceRefresh && homeData.value) {
    return
  }

  isLoading.value = true
  error.value = null

  try {
    const data = await homeApi.fetchHomeData(userId)
    saveHomeData(data)
  } catch (err: any) {
    error.value = err.message || '获取数据失败'
    throw err
  } finally {
    isLoading.value = false
  }
}

// 清空数据
const clearHomeData = () => {
  homeData.value = null
  localStorage.removeItem(STORAGE_KEY)
  localStorage.removeItem(STORAGE_TIME_KEY)
}

// 计算属性：统计数据
const statsData = computed(() => {
  if (!homeData.value) return null

  const data = homeData.value
  // total_time 是总计秒数*10，转换成分钟并保留一位小数
  const minutes = parseFloat((data.total_time / 10 / 60).toFixed(1))

  return {
    total: { value: data.total_task, unit: '个' },
    monthly: { value: data.month_task, unit: '个' },
    duration: { value: minutes, unit: '分钟' }
  }
})

// 计算属性：趋势数据（近7天）
interface TrendItem {
  date: string
  count: number
}

const trendData = computed<TrendItem[]>(() => {
  if (!homeData.value?.week_count) return []

  return homeData.value.week_count.map((count, index) => {
    const d = new Date()
    d.setDate(d.getDate() - (6 - index))
    const month = (d.getMonth() + 1).toString().padStart(2, '0')
    const day = d.getDate().toString().padStart(2, '0')
    return {
      date: `${month}-${day}`,
      count: count || 0
    }
  })
})

// 计算属性：视频类型分布
const typeData = computed(() => {
  if (!homeData.value) return { normal: 0, panoramic: 0 }

  return {
    normal: homeData.value['2d_video_count'],
    panoramic: homeData.value['3d_video_count']
  }
})

// 计算属性：注册日期
const registerDate = computed(() => {
  if (!homeData.value?.create_at) return null
  return homeData.value.create_at
})

// 计算属性：用户统计数据（我的数据）
const userStatsData = computed(() => {
  if (!homeData.value) return null

  const currentStorage = homeData.value.current_storage || 0
  const maxStorage = homeData.value.max_storage || 10737418240 // 默认 10GB

  return {
    detectCount: homeData.value.total_task,
    storageUsed: parseFloat((currentStorage / (1024 * 1024 * 1024)).toFixed(2)),
    storageTotal: parseFloat((maxStorage / (1024 * 1024 * 1024)).toFixed(2))
  }
})

// 计算属性：最近记录（转换格式适配组件）
const recentRecords = computed(() => {
  if (!homeData.value) return []

  const statusMap: Record<string, 'pending' | 'processing' | 'completed' | 'failed'> = {
    '0': 'pending',
    '1': 'processing',
    '2': 'completed',
    '3': 'failed',
    'completed': 'completed',
    'failed': 'failed'
  }

  return homeData.value.last_records.map(record => {
    // 计算相对时间
    const createTime = new Date(record.task_create_at)
    const now = new Date()
    const diffMs = now.getTime() - createTime.getTime()
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))

    let dateStr = ''
    if (diffHours < 1) {
      dateStr = '刚刚'
    } else if (diffHours < 24) {
      dateStr = `${diffHours}小时前`
    } else if (diffDays === 1) {
      dateStr = '昨天'
    } else if (diffDays < 7) {
      dateStr = `${diffDays}天前`
    } else {
      dateStr = createTime.toLocaleDateString('zh-CN')
    }

    return {
      id: record.video_id.toString(),
      name: record.video_name,
      date: dateStr,
      status: statusMap[record.video_status] || 'processing',
      thumbnail: record.video_cover
    }
  })
})

// Composable for use in components
export const useHome = () => {
  return {
    homeData: computed(() => homeData.value),
    isLoading: computed(() => isLoading.value),
    error: computed(() => error.value),
    statsData,
    trendData,
    typeData,
    recentRecords,
    registerDate,
    userStatsData,
    loadHomeData,
    clearHomeData
  }
}

// 初始化
initHomeData()

// 导出给外部使用
export { homeData, isLoading, error, loadHomeData, clearHomeData }
