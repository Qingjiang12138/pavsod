// 用户认证相关 API 封装

const BASE_URL = 'http://localhost:2778'

// 通用请求封装
async function request<T>(url: string, options: RequestInit): Promise<T> {
  const response = await fetch(`${BASE_URL}${url}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  })

  const data = await response.json()

  if (!response.ok || data.code !== 200) {
    throw new Error(data.message || '请求失败')
  }

  return data.data as T
}

// 注册接口
export interface RegisterParams {
  username: string
  phone: string
  password: string
}

export interface RegisterResult {
  userId: string
  username: string
  phone: string
  avatar: string
  token: string
}

export async function register(params: RegisterParams): Promise<RegisterResult> {
  return request<RegisterResult>('/user/register', {
    method: 'POST',
    body: JSON.stringify(params),
  })
}

// 登录接口
export interface LoginParams {
  phone: string
  password: string
}

export interface LoginResult {
  userId: string
  username: string
  phone: string
  avatar: string
  token: string
}

export async function login(params: LoginParams): Promise<LoginResult> {
  return request<LoginResult>('/user/login', {
    method: 'POST',
    body: JSON.stringify(params),
  })
}
