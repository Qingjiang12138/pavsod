<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'

const navItems = [
  { path: '/', name: '首页', icon: '🏠' },
  { path: '/detect', name: '开始检测', icon: '🔍' },
  { path: '/records', name: '检测记录', icon: '📋' },
  { path: '/profile', name: '个人中心', icon: '👤' },
]
</script>

<template>
  <div class="main-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <h1 class="app-title">PAV-SOD</h1>
        <h1 class="app-title">全景级音视频显著性检测系统</h1>
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
</template>

<style scoped>
.main-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 220px;
  background: var(--color-background-soft);
  border-right: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
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
  background: hsla(160, 100%, 37%, 0.15);
  color: hsla(160, 100%, 37%, 1);
  font-weight: 500;
}

.nav-icon {
  font-size: 1.25rem;
}

.nav-text {
  font-size: 0.95rem;
}

.main-content {
  flex: 1;
  padding: 2rem;
  overflow: auto;
  background: var(--color-background);
}
</style>
