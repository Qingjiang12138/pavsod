<script setup lang="ts">
import { ref } from 'vue'
import UserProfileCard from '@/components/profile/UserProfileCard.vue'
import UserStats from '@/components/profile/UserStats.vue'
import SecuritySettings from '@/components/profile/SecuritySettings.vue'
import SupportLinks from '@/components/profile/SupportLinks.vue'

// 模拟用户数据
const userData = ref({
  username: '张三',
  userId: '10086',
  joinDate: '2024-01-15',
  avatar: '',
  detectCount: 128,
  storageUsed: 2.3,
  storageTotal: 10,
  phone: '13812345678',
  email: 'zhangsan@example.com',
  hasPassword: true
})

// 偏好设置
const preferences = ref({
  theme: 'auto' as 'auto' | 'light' | 'dark',
  language: 'zh' as 'zh' | 'en',
  notifyOnComplete: true,
  autoPlayVideo: false
})

// 处理头像更新
const handleAvatarUpdate = (file: File) => {
  console.log('上传头像:', file.name)
  // TODO: 调用上传接口
}

// 处理编辑资料
const handleEditProfile = () => {
  console.log('编辑资料')
  // TODO: 打开编辑弹窗
}

// 处理安全设置
const handleChangePassword = () => {
  console.log('修改密码')
}

const handleBindPhone = () => {
  console.log('绑定/更换手机')
}

const handleBindEmail = () => {
  console.log('绑定/更换邮箱')
}

// 处理偏好更新
const updateTheme = (theme: 'auto' | 'light' | 'dark') => {
  preferences.value.theme = theme
}

const updateLanguage = (lang: 'zh' | 'en') => {
  preferences.value.language = lang
}

const updateNotify = (value: boolean) => {
  preferences.value.notifyOnComplete = value
}

const updateAutoPlay = (value: boolean) => {
  preferences.value.autoPlayVideo = value
}

// 处理支持链接
const handleHelp = () => {
  console.log('打开帮助中心')
}

const handleFeedback = () => {
  console.log('打开意见反馈')
}

const handleAbout = () => {
  console.log('打开关于我们')
}

const handleLogout = () => {
  console.log('退出登录')
  // TODO: 调用登出接口，跳转登录页
}
</script>

<template>
  <div class="profile-page">
    <header class="page-header">
      <h1 class="page-title">个人中心</h1>
      <p class="page-desc">管理您的账户信息和偏好设置</p>
    </header>

    <main class="profile-content">
      <!-- 左侧栏 -->
      <div class="profile-main">
        <UserProfileCard
          :username="userData.username"
          :user-id="userData.userId"
          :join-date="userData.joinDate"
          :avatar="userData.avatar"
          @update-avatar="handleAvatarUpdate"
          @edit-profile="handleEditProfile"
        />

        <UserStats
          :detect-count="userData.detectCount"
          :storage-used="userData.storageUsed"
          :storage-total="userData.storageTotal"
        />

        <SecuritySettings
          :phone="userData.phone"
          :email="userData.email"
          :has-password="userData.hasPassword"
          @change-password="handleChangePassword"
          @bind-phone="handleBindPhone"
          @bind-email="handleBindEmail"
        />
      </div>

      <!-- 右侧栏 -->
      <aside class="profile-sidebar">
        <SupportLinks
          @help="handleHelp"
          @feedback="handleFeedback"
          @about="handleAbout"
          @logout="handleLogout"
        />
      </aside>
    </main>
  </div>
</template>

<style scoped>
.profile-page {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 1.5rem;
}

.page-title {
  font-size: 1.75rem;
  font-weight: 600;
  color: var(--color-heading);
  margin-bottom: 0.5rem;
}

.page-desc {
  color: var(--color-text);
  opacity: 0.8;
}

.profile-content {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 1.5rem;
}

.profile-main {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.profile-sidebar {
  position: sticky;
  top: 1rem;
  height: fit-content;
}

@media (max-width: 768px) {
  .profile-content {
    grid-template-columns: 1fr;
  }

  .profile-sidebar {
    position: static;
  }
}
</style>
