// 用户信息相关 API 封装

const BASE_URL = 'http://localhost:2778'

// 通用请求封装
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

export interface UserProfile {
  id?: number
  username?: string
  phone?: string
  email?: string
  avatar?: string
  created_at?: string
}

// 获取用户信息
export async function fetchUserProfile(userId: string): Promise<UserProfile> {
  return request<UserProfile>('/user/data', {
    method: 'POST',
    body: JSON.stringify({ userId }),
  })
}

export interface UpdateUserParams {
  id: string
  username?: string
  phone?: string
  email?: string
  password?: string
}

// 修改用户信息
export async function updateUser(params: UpdateUserParams): Promise<void> {
  return request<void>('/user/change', {
    method: 'POST',
    body: JSON.stringify(params),
  })
}

// 上传头像（FormData，不走 JSON 请求封装）
export async function uploadAvatar(userId: string, file: File): Promise<void> {
  const token = localStorage.getItem('pavsod_token')
  const formData = new FormData()
  formData.append('id', userId)
  formData.append('photo', file)

  const response = await fetch(`${BASE_URL}/user/change/photo`, {
    method: 'POST',
    headers: {
      ...(token && { token }),
    },
    body: formData,
  })

  const data = await response.json()
  if (!response.ok || data.code !== 200) {
    const errorMsg = data.message || data.msg || '上传头像失败'
    throw new Error(errorMsg)
  }
}
