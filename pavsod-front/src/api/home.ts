// 首页数据相关 API 封装
import type { HomeData } from '@/stores/home'

const BASE_URL = 'http://localhost:2778'

// 通用请求封装
async function request<T>(url: string, options: RequestInit): Promise<T> {
  const token = localStorage.getItem('pavsod_token')

  // 调试输出
  console.log('[API Request] URL:', url)
  console.log('[API Request] Token from localStorage:', token)
  console.log('[API Request] Request headers:', {
    'Content-Type': 'application/json',
    ...(token && { 'token': token }),
  })

  const response = await fetch(`${BASE_URL}${url}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'token': token }),
      ...options.headers,
    },
  })

  const data = await response.json()

  if (!response.ok || data.code !== 200) {
    const errorMsg = data.message || data.msg || '请求失败'
    throw new Error(errorMsg)
  }

  return data.data as T
}

// 获取首页数据
export async function fetchHomeData(userId: string): Promise<HomeData> {
  return request<HomeData>('/home/data', {
    method: 'POST',
    body: JSON.stringify({ userId }),
  })
}
