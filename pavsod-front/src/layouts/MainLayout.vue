<script setup lang="ts">
import { RouterLink, RouterView, useRoute } from 'vue-router'
import { computed } from 'vue'

const route = useRoute()

const navItems = [
  { path: '/', name: '首页', icon: '🏠' },
  { path: '/detect', name: '开始检测', icon: '🎯' },
  { path: '/records', name: '检测记录', icon: '📋' },
  { path: '/profile', name: '个人中心', icon: '👤' },
]

// 当前页面标题
const pageTitle = computed(() => {
  const currentNav = navItems.find(item => item.path === route.path)
  return currentNav?.name || 'PAV-SOD'
})

// 模拟用户数据
const user = {
  name: '张三',
  avatar: '' // 如果有头像URL则显示，否则显示首字母
}
</script>

<template>
  <div class="main-layout">
    <!-- 顶部 Header -->
    <header class="top-header">
      <div class="header-left">
        <span class="page-title">{{ pageTitle }}</span>
      </div>
      <div class="header-right">
        <router-link to="/profile" class="user-info">
          <span class="user-name">{{ user.name }}</span>
          <div class="user-avatar">
            <img v-if="user.avatar" :src="user.avatar" alt="头像" />
            <span v-else>{{ user.name.charAt(0) }}</span>
          </div>
        </router-link>
      </div>
    </header>

    <div class="layout-body">
      <aside class="sidebar">
        <div class="sidebar-header">
          <h1 class="app-title">PAV-SOD</h1>
          <p class="app-desc">全景级音视频显著性检测系统</p>
        </div>
        <nav class="nav-menu">
          <RouterLink
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            class="nav-item"
            exact-active-class="nav-item--active"
          >
            <span class="nav-icon">{{ item.icon }}</span>
            <span class="nav-text">{{ item.name }}</span>
          </RouterLink>
        </nav>
      </aside>
      <main class="main-content">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<style scoped>
.main-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* 顶部 Header */
.top-header {
  height: 60px;
  background: var(--color-background-soft);
  border-bottom: 1px solid var(--color-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 2rem;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--color-heading);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  text-decoration: none;
  padding: 0.375rem 0.75rem 0.375rem 1rem;
  border-radius: 24px;
  background: var(--color-background);
  border: 1px solid var(--color-border);
  transition: all 0.2s;
}

.user-info:hover {
  border-color: hsla(210, 80%, 45%, 0.3);
  background: hsla(210, 80%, 45%, 0.05);
}

.user-name {
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--color-heading);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, hsla(210, 80%, 45%, 1), hsla(260, 60%, 50%, 1));
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-avatar span {
  font-size: 0.875rem;
  font-weight: 600;
  color: white;
}

/* 下方布局主体 */
.layout-body {
  display: flex;
  flex: 1;
}

.sidebar {
  width: 220px;
  background: var(--color-background-soft);
  border-right: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 60px;
  bottom: 0;
  overflow-y: auto;
  z-index: 50;
}

.sidebar-header {
  padding: 1.5rem;
  border-bottom: 1px solid var(--color-border);
  flex-shrink: 0;
}

.nav-menu {
  padding: 1rem 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  flex: 1;
}

.main-content {
  flex: 1;
  margin-left: 220px;
  padding: 2rem;
  overflow: auto;
  background: var(--color-background);
  min-height: calc(100vh - 60px);
}

.sidebar-header {
  padding: 1.5rem;
  border-bottom: 1px solid var(--color-border);
}

.app-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-heading);
  letter-spacing: 0.05em;
}

.app-desc {
  font-size: 0.75rem;
  color: var(--color-text);
  opacity: 0.7;
  margin-top: 0.25rem;
}

.nav-menu {
  padding: 1rem 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  color: var(--color-text);
  text-decoration: none;
  transition: background-color 0.2s, color 0.2s;
}

.nav-item:hover {
  background: var(--color-background-mute);
}

.nav-item--active {
  background: hsla(210, 80%, 45%, 0.12);
  color: hsla(210, 80%, 45%, 1);
  font-weight: 500;
}

.nav-icon {
  font-size: 1.1rem;
  width: 24px;
  text-align: center;
}

.nav-text {
  font-size: 0.9rem;
}

.main-content {
  flex: 1;
  padding: 2rem;
  overflow: auto;
  background: var(--color-background);
}

@media (max-width: 768px) {
  .top-header {
    padding: 0 1rem;
  }

  .user-name {
    display: none;
  }

  .sidebar {
    width: 64px;
  }

  .sidebar-header {
    padding: 1rem 0.5rem;
    text-align: center;
  }

  .app-title {
    font-size: 0.875rem;
  }

  .app-desc {
    display: none;
  }

  .nav-text {
    display: none;
  }

  .nav-item {
    justify-content: center;
    padding: 0.75rem;
  }

  .main-content {
    margin-left: 64px;
  }
}
</style>
