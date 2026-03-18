import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

// 预设用户数据（前端模拟）
const MOCK_USER = {
  phone: '18340237677',
  password: '123456',
  username: '张三',
  avatar: ''
}

const STORAGE_KEY = 'pavsod_auth'

// 创建响应式状态
const isAuthenticated = ref(false)
const user = ref<{
  phone: string
  username: string
  avatar: string
} | null>(null)

// 初始化时检查 localStorage
const initAuth = () => {
  const stored = localStorage.getItem(STORAGE_KEY)
  if (stored) {
    try {
      const data = JSON.parse(stored)
      isAuthenticated.value = true
      user.value = data
    } catch {
      localStorage.removeItem(STORAGE_KEY)
    }
  }
}

// 登录
const login = (phone: string, password: string): boolean => {
  // 验证预设用户
  if (phone === MOCK_USER.phone && password === MOCK_USER.password) {
    isAuthenticated.value = true
    user.value = {
      phone: MOCK_USER.phone,
      username: MOCK_USER.username,
      avatar: MOCK_USER.avatar
    }
    // 保存到 localStorage
    localStorage.setItem(STORAGE_KEY, JSON.stringify(user.value))
    return true
  }
  return false
}

// 注册（前端模拟，实际只是添加到本地存储）
const register = (username: string, phone: string, password: string): boolean => {
  // 检查手机号是否已被使用（预设用户）
  if (phone === MOCK_USER.phone) {
    return false
  }
  // 模拟注册成功，直接登录
  isAuthenticated.value = true
  user.value = {
    phone,
    username,
    avatar: ''
  }
  localStorage.setItem(STORAGE_KEY, JSON.stringify(user.value))
  return true
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
    login: (phone: string, password: string) => {
      const success = login(phone, password)
      if (success) {
        router.push('/')
      }
      return success
    },
    register: (username: string, phone: string, password: string) => {
      const success = register(username, phone, password)
      if (success) {
        router.push('/')
      }
      return success
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
export { isAuthenticated, user, login, logout }
