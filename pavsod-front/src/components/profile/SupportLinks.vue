<script setup lang="ts">
const emit = defineEmits<{
  logout: []
}>()

const links = [
  { icon: '❓', label: '帮助中心', action: 'help' },
  { icon: '💬', label: '意见反馈', action: 'feedback' },
  { icon: 'ℹ️', label: '关于我们', action: 'about' }
]
</script>

<template>
  <div class="support-section">
    <!-- 快捷链接 -->
    <div class="quick-links">
      <button
        v-for="link in links"
        :key="link.action"
        class="link-btn"
        @click="$emit(link.action as any)"
      >
        <span class="link-icon">{{ link.icon }}</span>
        <span class="link-label">{{ link.label }}</span>
        <span class="link-arrow">›</span>
      </button>
    </div>

    <!-- 退出登录 -->
    <button class="logout-btn" @click="emit('logout')">
      <span>退出登录</span>
    </button>
  </div>
</template>

<style scoped>
.support-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.quick-links {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.link-btn {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.875rem 1rem;
  background: var(--color-background-soft);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  font-size: 0.9rem;
  color: var(--color-heading);
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
}

.link-btn:hover {
  background: var(--color-background-mute);
  border-color: var(--color-border-hover);
}

.link-icon {
  font-size: 1.25rem;
}

.link-label {
  flex: 1;
}

.link-arrow {
  font-size: 1.25rem;
  color: var(--color-text);
  opacity: 0.4;
}

.logout-btn {
  padding: 0.875rem;
  background: hsla(0, 70%, 50%, 0.1);
  border: 1px solid hsla(0, 70%, 50%, 0.3);
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  color: hsla(0, 70%, 50%, 1);
  cursor: pointer;
  transition: all 0.2s;
}

.logout-btn:hover {
  background: hsla(0, 70%, 50%, 0.15);
}
</style>
