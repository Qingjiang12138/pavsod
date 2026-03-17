<script setup lang="ts">
interface Props {
  theme: 'auto' | 'light' | 'dark'
  language: 'zh' | 'en'
  notifyOnComplete: boolean
  autoPlayVideo: boolean
}

const props = defineProps<Props>()

const emit = defineEmits<{
  updateTheme: [theme: 'auto' | 'light' | 'dark']
  updateLanguage: [lang: 'zh' | 'en']
  updateNotify: [value: boolean]
  updateAutoPlay: [value: boolean]
}>()

const themes = [
  { value: 'auto', label: '自动', icon: '🌓' },
  { value: 'light', label: '浅色', icon: '☀️' },
  { value: 'dark', label: '深色', icon: '🌙' }
] as const

const languages = [
  { value: 'zh', label: '简体中文' },
  { value: 'en', label: 'English' }
] as const
</script>

<template>
  <div class="preferences">
    <h3 class="section-title">偏好设置</h3>

    <div class="settings-list">
      <!-- 主题模式 -->
      <div class="setting-item">
        <div class="setting-info">
          <span class="setting-label">主题模式</span>
          <span class="setting-desc">选择您喜欢的界面主题</span>
        </div>
        <div class="theme-selector">
          <button
            v-for="t in themes"
            :key="t.value"
            class="theme-btn"
            :class="{ active: props.theme === t.value }"
            @click="emit('updateTheme', t.value)"
          >
            <span class="theme-icon">{{ t.icon }}</span>
            <span>{{ t.label }}</span>
          </button>
        </div>
      </div>

      <!-- 语言设置 -->
      <div class="setting-item">
        <div class="setting-info">
          <span class="setting-label">语言</span>
          <span class="setting-desc">界面显示语言</span>
        </div>
        <select
          :value="language"
          class="setting-select"
          @change="emit('updateLanguage', ($event.target as HTMLSelectElement).value as 'zh' | 'en')"
        >
          <option v-for="lang in languages" :key="lang.value" :value="lang.value">
            {{ lang.label }}
          </option>
        </select>
      </div>

      <!-- 通知设置 -->
      <div class="setting-item">
        <div class="setting-info">
          <span class="setting-label">检测完成通知</span>
          <span class="setting-desc">检测完成后发送浏览器通知</span>
        </div>
        <label class="switch">
          <input
            type="checkbox"
            :checked="notifyOnComplete"
            @change="emit('updateNotify', ($event.target as HTMLInputElement).checked)"
          />
          <span class="switch-slider"></span>
        </label>
      </div>

      <!-- 自动播放 -->
      <div class="setting-item">
        <div class="setting-info">
          <span class="setting-label">自动播放结果视频</span>
          <span class="setting-desc">检测完成后自动播放对比视频</span>
        </div>
        <label class="switch">
          <input
            type="checkbox"
            :checked="autoPlayVideo"
            @change="emit('updateAutoPlay', ($event.target as HTMLInputElement).checked)"
          />
          <span class="switch-slider"></span>
        </label>
      </div>
    </div>
  </div>
</template>

<style scoped>
.preferences {
  background: var(--color-background-soft);
  border-radius: 12px;
  padding: 1.25rem;
  border: 1px solid var(--color-border);
}

.section-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--color-heading);
  margin-bottom: 1rem;
}

.settings-list {
  display: flex;
  flex-direction: column;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 0;
  border-bottom: 1px solid var(--color-border);
  gap: 1rem;
}

.setting-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.setting-item:first-child {
  padding-top: 0;
}

.setting-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.setting-label {
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--color-heading);
}

.setting-desc {
  font-size: 0.75rem;
  color: var(--color-text);
  opacity: 0.6;
}

/* 主题选择器 */
.theme-selector {
  display: flex;
  gap: 0.5rem;
}

.theme-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
  padding: 0.5rem 0.75rem;
  background: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  font-size: 0.75rem;
  color: var(--color-text);
  cursor: pointer;
  transition: all 0.2s;
  min-width: 60px;
}

.theme-btn:hover {
  border-color: hsla(210, 80%, 45%, 0.5);
}

.theme-btn.active {
  border-color: hsla(210, 80%, 45%, 1);
  background: hsla(210, 80%, 45%, 0.1);
  color: hsla(210, 80%, 45%, 1);
}

.theme-icon {
  font-size: 1.25rem;
}

/* 下拉选择 */
.setting-select {
  padding: 0.5rem 1rem;
  background: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 0.85rem;
  color: var(--color-heading);
  cursor: pointer;
  min-width: 120px;
}

.setting-select:focus {
  outline: none;
  border-color: hsla(210, 80%, 45%, 1);
}

/* 开关 */
.switch {
  position: relative;
  width: 48px;
  height: 24px;
  cursor: pointer;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.switch-slider {
  position: absolute;
  inset: 0;
  background: var(--color-border);
  border-radius: 24px;
  transition: background 0.2s;
}

.switch-slider::before {
  content: '';
  position: absolute;
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background: white;
  border-radius: 50%;
  transition: transform 0.2s;
}

.switch input:checked + .switch-slider {
  background: hsla(210, 80%, 45%, 1);
}

.switch input:checked + .switch-slider::before {
  transform: translateX(24px);
}

@media (max-width: 600px) {
  .setting-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }
}
</style>
