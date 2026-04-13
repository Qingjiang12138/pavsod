<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { useAuth } from '@/stores/auth'
import { useHome } from '@/stores/home'

const { user } = useAuth()
const { registerDate } = useHome()

const formatDate = (dateStr: string): string => {
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return dateStr
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const displayJoinDate = computed(() => registerDate.value ? formatDate(registerDate.value) : '-')

const emit = defineEmits<{
  updateAvatar: [file: File]
  editProfile: []
}>()

const avatarInput = ref<HTMLInputElement | null>(null)
const previewAvatar = ref<string | null>(user.value?.avatar || null)

// 持久化头像变化时更新预览
watch(() => user.value?.avatar, (newAvatar) => {
  if (newAvatar) {
    previewAvatar.value = newAvatar
  }
})

const triggerAvatarUpload = () => {
  avatarInput.value?.click()
}

const handleAvatarChange = (event: Event) => {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (file) {
    // 本地预览
    const reader = new FileReader()
    reader.onload = (e) => {
      previewAvatar.value = e.target?.result as string
    }
    reader.readAsDataURL(file)
    // 触发上传
    emit('updateAvatar', file)
  }
}

const displayName = user.value?.username || '未知用户'
const displayId = user.value?.userId || '-'
</script>

<template>
  <div class="profile-card">
    <div class="avatar-section">
      <div class="avatar-wrapper" @click="triggerAvatarUpload">
        <img
          v-if="previewAvatar"
          :src="previewAvatar"
          alt="头像"
          class="avatar-image"
        />
        <div v-else class="avatar-placeholder">
          {{ displayName.charAt(0).toUpperCase() }}
        </div>
        <div class="avatar-overlay">
          <span>更换</span>
        </div>
      </div>
      <input
        ref="avatarInput"
        type="file"
        accept="image/*"
        class="avatar-input"
        @change="handleAvatarChange"
      />
    </div>

    <div class="info-section">
      <h2 class="username">{{ displayName }}</h2>
      <div class="meta-info">
        <span class="meta-item">ID: {{ displayId }}</span>
        <span class="meta-divider">·</span>
        <span class="meta-item">注册于 {{ displayJoinDate }}</span>
      </div>
    </div>

    <button class="edit-btn" @click="emit('editProfile')">
      编辑资料
    </button>
  </div>
</template>

<style scoped>
.profile-card {
  background: var(--color-background-soft);
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid var(--color-border);
  display: flex;
  align-items: center;
  gap: 1.25rem;
  flex-wrap: wrap;
}

.avatar-section {
  flex-shrink: 0;
}

.avatar-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  background: linear-gradient(135deg, hsla(210, 80%, 45%, 1), hsla(260, 60%, 50%, 1));
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  font-weight: 600;
  color: white;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}

.avatar-overlay span {
  color: white;
  font-size: 0.8rem;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-input {
  display: none;
}

.info-section {
  flex: 1;
  min-width: 0;
}

.username {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-heading);
  margin-bottom: 0.5rem;
}

.meta-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.meta-item {
  font-size: 0.8rem;
  color: var(--color-text);
  opacity: 0.7;
}

.meta-divider {
  color: var(--color-text);
  opacity: 0.4;
}

.edit-btn {
  padding: 0.5rem 1rem;
  background: var(--color-background-mute);
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 0.85rem;
  color: var(--color-text);
  cursor: pointer;
  transition: all 0.2s;
  margin-left: auto;
}

.edit-btn:hover {
  background: var(--color-border);
}

@media (max-width: 600px) {
  .profile-card {
    flex-direction: column;
    text-align: center;
  }

  .edit-btn {
    margin-left: 0;
    width: 100%;
  }
}
</style>
