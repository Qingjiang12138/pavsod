// 检测记录相关 API 封装

const BASE_URL = 'http://localhost:2778'

export interface RecordItem {
  video_id: number | string
  video_cover: string
  video_name: string
  video_url: string
  video_status: number | string
  video_duration: number
  task_create_at: string
}

export interface FetchRecordsParams {
  userId: string
  start_page: number
  per_page: number
  find_name?: string
  find_date?: string
}

async function request<T>(url: string, options: RequestInit): Promise<T> {
  const token = localStorage.getItem('pavsod_token')

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

export interface FetchRecordsResult {
  total: number
  record_list: RecordItem[]
}

// 查询检测记录
export async function fetchRecords(params: FetchRecordsParams): Promise<FetchRecordsResult> {
  return request<FetchRecordsResult>('/record/data', {
    method: 'POST',
    body: JSON.stringify(params),
  })
}
