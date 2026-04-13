import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import { isAuthenticated } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { title: '登录', public: true },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { title: '注册', public: true },
    },
    {
      path: '/',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/HomeView.vue'),
          meta: { title: '首页' },
        },
        {
          path: 'detect',
          name: 'detect',
          component: () => import('@/views/DetectView.vue'),
          meta: { title: '开始检测' },
        },
        {
          path: 'records',
          name: 'records',
          component: () => import('@/views/RecordsView.vue'),
          meta: { title: '检测记录' },
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('@/views/ProfileView.vue'),
          meta: { title: '个人中心' },
        },
      ],
    },
  ],
})

// 路由守卫：检查登录状态
router.beforeEach((to) => {
  // 如果路由标记为 public，直接放行
  if (to.meta.public) {
    // 已登录用户访问登录/注册页，跳转到首页
    if (isAuthenticated.value && (to.path === '/login' || to.path === '/register')) {
      return '/'
    }
    return true
  }

  // 需要登录的页面，检查登录状态
  if (!isAuthenticated.value) {
    return '/login'
  }

  return true
})

export default router
