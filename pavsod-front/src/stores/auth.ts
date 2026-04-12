import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import * as authApi from '@/api/auth'

const STORAGE_KEY = 'pavsod_auth'
const TOKEN_KEY = 'pavsod_token'

// 创建响应式状态
const isAuthenticated = ref(false)
const user = ref<{
  userId: string
  phone: string
  username: string
  avatar: string
} | null>(null)
const token = ref<string>('')

// 初始化时检查 localStorage
const initAuth = () => {
  const stored = localStorage.getItem(STORAGE_KEY)
  const storedToken = localStorage.getItem(TOKEN_KEY)
  if (stored && storedToken) {
    try {
      const data = JSON.parse(stored)
      isAuthenticated.value = true
      user.value = data
      token.value = storedToken
    } catch {
      localStorage.removeItem(STORAGE_KEY)
      localStorage.removeItem(TOKEN_KEY)
    }
  }
}

// 设置登录状态
const setAuth = (data: authApi.LoginResult | authApi.RegisterResult) => {
  isAuthenticated.value = true
  user.value = {
    userId: data.userId,
    phone: data.phone,
    username: data.username,
    avatar: data.avatar
  }
  token.value = data.token
  // 保存到 localStorage
  localStorage.setItem(STORAGE_KEY, JSON.stringify(user.value))
  localStorage.setItem(TOKEN_KEY, data.token)
}

// 登录
const login = async (phone: string, password: string): Promise<void> => {
  const result = await authApi.login({ phone, password })
  setAuth(result)
}

// 注册
const register = async (username: string, phone: string, password: string): Promise<void> => {
  const result = await authApi.register({ username, phone, password })
  setAuth(result)
}

// 登出
const logout = () => {
  isAuthenticated.value = false
  user.value = null
  localStorage.removeItem(STORAGE_KEY)
}

// Composable for use in components
export const useAuth = () => {
  const router = useRouter()

  return {
    isAuthenticated: computed(() => isAuthenticated.value),
    user: computed(() => user.value),
    token: computed(() => token.value),
    login: async (phone: string, password: string): Promise<void> => {
      await login(phone, password)
      router.push('/')
    },
    register: async (username: string, phone: string, password: string): Promise<void> => {
      await authApi.register({ username, phone, password })
      router.push('/login')
    },
    logout: () => {
      logout()
      router.push('/login')
    }
  }
}

// 初始化认证状态
initAuth()

// 导出给路由守卫使用
export { isAuthenticated, user, token, login, logout }
