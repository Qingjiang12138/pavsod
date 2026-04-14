<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuth } from '@/stores/auth'
import { useHome } from '@/stores/home'
import * as userApi from '@/api/user'
import UserProfileCard from '@/components/profile/UserProfileCard.vue'
import UserStats from '@/components/profile/UserStats.vue'
import SecuritySettings from '@/components/profile/SecuritySettings.vue'
import SupportLinks from '@/components/profile/SupportLinks.vue'

const { logout, user, updateUserInfo } = useAuth()
const { userStatsData } = useHome()

// 真实统计数据（来自 homeData）
const displayStats = computed(() => {
  return {
    detectCount: userStatsData.value?.detectCount ?? 0,
    storageUsed: userStatsData.value?.storageUsed ?? 0,
    storageTotal: userStatsData.value?.storageTotal ?? 10
  }
})

// 安全设置展示数据
const userData = ref({
  email: '',
  hasPassword: true
})

// 编辑资料弹窗
const showEditModal = ref(false)
const isLoadingProfile = ref(false)
const isSaving = ref(false)
const editError = ref('')
const editForm = ref({
  username: '',
  phone: '',
  email: '',
  password: ''
})

// 处理头像更新
const isUploadingAvatar = ref(false)

const handleAvatarUpdate = async (file: File) => {
  if (!user.value?.userId) {
    alert('用户信息不存在，无法上传头像')
    return
  }

  isUploadingAvatar.value = true
  try {
    await userApi.uploadAvatar(user.value.userId, file)
    // 后端不返回新头像 URL，重新拉取用户信息
    const profile = await userApi.fetchUserProfile(user.value.userId)
    if (profile.avatar) {
      updateUserInfo({ avatar: profile.avatar })
    }
  } catch (err: any) {
    console.error('上传头像失败:', err)
    alert(err.message || '上传头像失败')
  } finally {
    isUploadingAvatar.value = false
  }
}

// 打开编辑资料弹窗
const handleEditProfile = async () => {
  editError.value = ''
  editForm.value.username = user.value?.username || ''
  editForm.value.phone = user.value?.phone || ''
  editForm.value.email = userData.value.email
  editForm.value.password = ''
  showEditModal.value = true

  if (!user.value?.userId) return

  isLoadingProfile.value = true
  try {
    const profile = await userApi.fetchUserProfile(user.value.userId)
    editForm.value.email = profile.email || ''
    if (profile.username) editForm.value.username = profile.username
    if (profile.phone) editForm.value.phone = profile.phone
    userData.value.email = profile.email || ''
    if (profile.username || profile.phone) {
      updateUserInfo({
        username: profile.username || editForm.value.username,
        phone: profile.phone || editForm.value.phone
      })
    }
  } catch (err: any) {
    console.error('获取用户信息失败:', err)
  } finally {
    isLoadingProfile.value = false
  }
}

const closeEditModal = () => {
  showEditModal.value = false
}

const saveProfile = async () => {
  if (!user.value?.userId) {
    editError.value = '用户信息不存在'
    return
  }

  // 表单校验
  if (!editForm.value.username.trim()) {
    editError.value = '用户名不能为空'
    return
  }
  if (!/^1[3-9]\d{9}$/.test(editForm.value.phone)) {
    editError.value = '请输入有效的手机号码'
    return
  }
  if (editForm.value.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(editForm.value.email)) {
    editError.value = '请输入有效的邮箱地址'
    return
  }
  if (editForm.value.password && editForm.value.password.length < 6) {
    editError.value = '密码长度不能少于6位'
    return
  }

  isSaving.value = true
  editError.value = ''

  try {
    const params: userApi.UpdateUserParams = {
      id: user.value.userId,
      username: editForm.value.username,
      phone: editForm.value.phone,
      email: editForm.value.email
    }
    if (editForm.value.password) {
      params.password = editForm.value.password
    }

    await userApi.updateUser(params)

    // 更新本地状态
    updateUserInfo({
      username: editForm.value.username,
      phone: editForm.value.phone,
      email: editForm.value.email
    })
    userData.value.email = editForm.value.email
    if (editForm.value.password) {
      userData.value.hasPassword = true
    }

    showEditModal.value = false
  } catch (err: any) {
    editError.value = err.message || '保存失败，请稍后重试'
  } finally {
    isSaving.value = false
  }
}

// 处理偏好更新
const updateTheme = (theme: 'auto' | 'light' | 'dark') => {
  console.log('切换主题:', theme)
}

const updateLanguage = (lang: 'zh' | 'en') => {
  console.log('切换语言:', lang)
}

const updateNotify = (value: boolean) => {
  console.log('通知设置:', value)
}

const updateAutoPlay = (value: boolean) => {
  console.log('自动播放:', value)
}

// 帮助中心弹窗
const showHelpModal = ref(false)
const activeFaqIndex = ref<number | null>(0)

const faqList = [
  {
    q: '什么是音视频显著性检测？',
    a: '显著性检测是通过 AI 分析视频中人眼最可能关注的区域。PAV-SOD 系统支持普通 2D 视频和 360° 全景视频的音视频联合显著性检测，最终生成带有显著性热力图叠加的结果视频。'
  },
  {
    q: '支持上传哪些视频格式？',
    a: '系统支持 MP4、AVI、MOV、MKV、FLV 等常见视频格式。推荐使用 MP4 格式以获得最佳兼容性。单个视频大小不能超过 500MB。'
  },
  {
    q: '2D 视频和全景视频有什么区别？',
    a: '2D 视频是普通平面视频，显著性检测主要分析画面中的视觉焦点和声音方向；全景视频（360° VR）则需要考虑球面投影下用户的视线转移规律，检测难度更高，应用场景包括 VR 游览、沉浸式直播等。'
  },
  {
    q: '检测通常需要多长时间？',
    a: '检测时长取决于视频长度、帧率和当前服务器负载。一般情况下，1 分钟的 30fps 视频需要 3-5 分钟完成处理。您可以在检测记录页面查看任务进度。'
  },
  {
    q: '如何查看检测结果？',
    a: '视频处理完成后，您可以进入「开始检测」页面，系统会自动加载最近一次上传视频的检测结果。也可以前往「检测记录」页面，找到已完成的任务并点击「查看结果」。'
  },
  {
    q: '存储空间有限制吗？',
    a: '每位用户默认拥有 10GB 的云端存储空间，用于存放原始视频和检测结果视频。当存储空间不足时，请及时清理历史记录以释放空间。'
  }
]

const toggleFaq = (index: number) => {
  activeFaqIndex.value = activeFaqIndex.value === index ? null : index
}

// 处理支持链接
const handleHelp = () => {
  showHelpModal.value = true
}

const closeHelpModal = () => {
  showHelpModal.value = false
}

// 意见反馈弹窗
const showFeedbackModal = ref(false)
const isSubmittingFeedback = ref(false)
const feedbackError = ref('')
const feedbackForm = ref({
  type: 'feature' as 'feature' | 'bug' | 'other',
  content: ''
})

const feedbackTypes = [
  { value: 'feature', label: '功能建议' },
  { value: 'bug', label: 'Bug 反馈' },
  { value: 'other', label: '其他' }
]

const handleFeedback = () => {
  feedbackForm.value = { type: 'feature', content: '' }
  feedbackError.value = ''
  showFeedbackModal.value = true
}

const closeFeedbackModal = () => {
  showFeedbackModal.value = false
}

const submitFeedback = async () => {
  if (!feedbackForm.value.content.trim()) {
    feedbackError.value = '请填写反馈内容'
    return
  }
  if (feedbackForm.value.content.trim().length < 5) {
    feedbackError.value = '反馈内容不能少于 5 个字'
    return
  }

  isSubmittingFeedback.value = true
  feedbackError.value = ''

  try {
    // 暂无后端接口，先模拟提交
    console.log('提交反馈:', feedbackForm.value)
    await new Promise(resolve => setTimeout(resolve, 500))
    alert('反馈提交成功，感谢您的建议！')
    showFeedbackModal.value = false
  } catch (err: any) {
    feedbackError.value = err.message || '提交失败，请稍后重试'
  } finally {
    isSubmittingFeedback.value = false
  }
}

// 关于我们弹窗
const showAboutModal = ref(false)
const sourceCodeUrl = ref('https://github.com/Qingjiang12138/pavsod') // TODO: 替换为真实的源代码仓库链接

const handleAbout = () => {
  showAboutModal.value = true
}

const closeAboutModal = () => {
  showAboutModal.value = false
}

const handleLogout = () => {
  logout()
}

// 页面加载时拉取最新用户信息（同步邮箱等字段到本地）
onMounted(() => {
  if (!user.value?.userId) return
  userApi.fetchUserProfile(user.value.userId).then((profile) => {
    if (profile.email !== undefined) {
      userData.value.email = profile.email
      updateUserInfo({ email: profile.email })
    }
    if (profile.username || profile.phone) {
      updateUserInfo({
        username: profile.username || user.value?.username || '',
        phone: profile.phone || user.value?.phone || ''
      })
    }
  }).catch((err) => {
    console.error('同步用户信息失败:', err)
  })
})
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
          @update-avatar="handleAvatarUpdate"
          @edit-profile="handleEditProfile"
        />

        <UserStats
          :detect-count="displayStats.detectCount"
          :storage-used="displayStats.storageUsed"
          :storage-total="displayStats.storageTotal"
        />

        <SecuritySettings
          :phone="user?.phone || ''"
          :email="user?.email || userData.email"
          :has-password="userData.hasPassword"
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

    <!-- 编辑资料弹窗 -->
    <div v-if="showEditModal" class="modal-overlay" @click.self="closeEditModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3 class="modal-title">编辑资料</h3>
          <button class="modal-close" @click="closeEditModal">✕</button>
        </div>

        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">用户名</label>
            <input
              v-model="editForm.username"
              type="text"
              class="form-input"
              placeholder="请输入用户名"
              :disabled="isLoadingProfile"
            />
          </div>

          <div class="form-group">
            <label class="form-label">手机号码</label>
            <input
              v-model="editForm.phone"
              type="tel"
              class="form-input"
              placeholder="请输入手机号码"
              maxlength="11"
              :disabled="isLoadingProfile"
            />
          </div>

          <div class="form-group">
            <label class="form-label">邮箱地址</label>
            <input
              v-model="editForm.email"
              type="email"
              class="form-input"
              placeholder="请输入邮箱地址"
              :disabled="isLoadingProfile"
            />
          </div>

          <div class="form-group">
            <label class="form-label">登录密码</label>
            <input
              v-model="editForm.password"
              type="password"
              class="form-input"
              placeholder="留空则不修改密码"
              :disabled="isLoadingProfile"
            />
            <p class="form-hint">如需修改密码，请输入新密码；留空则保持原密码不变</p>
          </div>

          <div v-if="editError" class="edit-error">
            {{ editError }}
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn btn-secondary" :disabled="isSaving" @click="closeEditModal">
            取消
          </button>
          <button class="btn btn-primary" :disabled="isSaving || isLoadingProfile" @click="saveProfile">
            <span v-if="isSaving">保存中...</span>
            <span v-else>保存</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 帮助中心弹窗 -->
    <div v-if="showHelpModal" class="modal-overlay" @click.self="closeHelpModal">
      <div class="modal-content help-modal">
        <div class="modal-header">
          <h3 class="modal-title">帮助中心</h3>
          <button class="modal-close" @click="closeHelpModal">✕</button>
        </div>

        <div class="modal-body">
          <div class="faq-list">
            <div
              v-for="(item, index) in faqList"
              :key="index"
              class="faq-item"
              :class="{ active: activeFaqIndex === index }"
              @click="toggleFaq(index)"
            >
              <div class="faq-question">
                <span class="faq-icon">Q</span>
                <span class="faq-text">{{ item.q }}</span>
                <span class="faq-arrow">{{ activeFaqIndex === index ? '−' : '+' }}</span>
              </div>
              <div v-show="activeFaqIndex === index" class="faq-answer">
                <p>{{ item.a }}</p>
              </div>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn btn-primary" @click="closeHelpModal">知道了</button>
        </div>
      </div>
    </div>

    <!-- 意见反馈弹窗 -->
    <div v-if="showFeedbackModal" class="modal-overlay" @click.self="closeFeedbackModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3 class="modal-title">意见反馈</h3>
          <button class="modal-close" @click="closeFeedbackModal">✕</button>
        </div>

        <div class="modal-body">
          <div class="form-group">
            <label class="form-label">反馈类型</label>
            <div class="feedback-types">
              <label
                v-for="t in feedbackTypes"
                :key="t.value"
                class="type-tag"
                :class="{ active: feedbackForm.type === t.value }"
              >
                <input
                  v-model="feedbackForm.type"
                  type="radio"
                  :value="t.value"
                  class="type-input"
                />
                <span>{{ t.label }}</span>
              </label>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">反馈内容</label>
            <textarea
              v-model="feedbackForm.content"
              class="form-textarea"
              rows="4"
              placeholder="请描述您遇到的问题或建议，我们会认真听取每一条反馈"
            ></textarea>
          </div>

          <div v-if="feedbackError" class="edit-error">
            {{ feedbackError }}
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn btn-secondary" :disabled="isSubmittingFeedback" @click="closeFeedbackModal">
            取消
          </button>
          <button class="btn btn-primary" :disabled="isSubmittingFeedback" @click="submitFeedback">
            <span v-if="isSubmittingFeedback">提交中...</span>
            <span v-else>提交反馈</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 关于我们弹窗 -->
    <div v-if="showAboutModal" class="modal-overlay" @click.self="closeAboutModal">
      <div class="modal-content about-modal">
        <div class="modal-header">
          <h3 class="modal-title">关于我们</h3>
          <button class="modal-close" @click="closeAboutModal">✕</button>
        </div>

        <div class="modal-body">
          <div class="about-content">
            <div class="about-badge">毕业设计</div>
            <h4 class="about-title">基于音频信息的视觉显著物体检测设计与实现</h4>
            <p class="about-desc">
              本系统为毕业设计项目，旨在探索音视频联合信息在视觉显著性检测中的应用。通过对视频中的音频线索与视觉画面进行联合建模，提升复杂场景下显著性预测的准确性。
            </p>

            <div class="about-section">
              <div class="section-label">核心模型</div>
              <div class="model-card">
                <span class="model-name">CAV-Net</span>
                <span class="model-meta">TOMM 2022</span>
              </div>
            </div>

            <div class="about-section">
              <div class="section-label">源代码</div>
              <a
                :href="sourceCodeUrl"
                class="source-link"
                target="_blank"
                rel="noopener noreferrer"
              >
                <span>查看项目源码</span>
                <span class="link-arrow">→</span>
              </a>
              <p class="source-hint">请将链接替换为您的真实仓库地址</p>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn btn-primary" @click="closeAboutModal">知道了</button>
        </div>
      </div>
    </div>
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

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.modal-content {
  background: var(--color-background-soft);
  border-radius: 16px;
  width: 100%;
  max-width: 480px;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: modalSlideIn 0.3s ease;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.25rem 1.5rem;
  border-bottom: 1px solid var(--color-border);
}

.modal-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--color-heading);
  margin: 0;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.25rem;
  color: var(--color-text);
  opacity: 0.5;
  cursor: pointer;
  padding: 0.25rem;
  line-height: 1;
  transition: opacity 0.2s;
}

.modal-close:hover {
  opacity: 1;
}

.modal-body {
  padding: 1.5rem;
  overflow-y: auto;
  max-height: calc(90vh - 140px);
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-heading);
  margin-bottom: 0.5rem;
}

.form-input {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  font-size: 0.95rem;
  background: var(--color-background);
  color: var(--color-text);
  transition: all 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: hsla(215, 80%, 45%, 0.5);
  box-shadow: 0 0 0 3px hsla(215, 80%, 45%, 0.08);
}

.form-input::placeholder {
  color: var(--color-text);
  opacity: 0.4;
}

.form-input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background: var(--color-background-mute);
}

.form-hint {
  font-size: 0.75rem;
  color: var(--color-text);
  opacity: 0.6;
  margin-top: 0.375rem;
  margin-bottom: 0;
}

.edit-error {
  color: #dc2626;
  font-size: 0.875rem;
  padding: 0.75rem 1rem;
  background: rgba(220, 38, 38, 0.08);
  border-radius: 8px;
}

.modal-footer {
  display: flex;
  gap: 0.75rem;
  padding: 1rem 1.5rem 1.5rem;
  border-top: 1px solid var(--color-border);
}

.btn {
  flex: 1;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.btn-secondary {
  background: var(--color-background-mute);
  color: var(--color-text);
  border: 1px solid var(--color-border);
}

.btn-secondary:hover:not(:disabled) {
  background: var(--color-border);
}

.btn-primary {
  background: linear-gradient(135deg, hsla(215, 80%, 45%, 1) 0%, hsla(250, 60%, 55%, 1) 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px hsla(215, 80%, 45%, 0.35);
}

.btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* 帮助中心弹窗 */
.help-modal {
  max-width: 560px;
}

.faq-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.faq-item {
  background: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
}

.faq-item:hover {
  border-color: hsla(215, 80%, 45%, 0.3);
}

.faq-item.active {
  border-color: hsla(215, 80%, 45%, 0.5);
}

.faq-question {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 1.25rem;
}

.faq-icon {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  background: hsla(215, 80%, 45%, 0.1);
  color: hsla(215, 80%, 45%, 1);
  font-size: 0.75rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.faq-text {
  flex: 1;
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--color-heading);
  text-align: left;
}

.faq-arrow {
  font-size: 1.25rem;
  color: var(--color-text);
  opacity: 0.5;
  flex-shrink: 0;
  line-height: 1;
}

.faq-answer {
  padding: 0 1.25rem 1.25rem 3rem;
  font-size: 0.85rem;
  color: var(--color-text);
  line-height: 1.7;
  opacity: 0.9;
  text-align: left;
}

.faq-answer p {
  margin: 0;
}

/* 意见反馈弹窗 */
.feedback-types {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.type-tag {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.5rem 0.875rem;
  background: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 20px;
  font-size: 0.85rem;
  color: var(--color-text);
  cursor: pointer;
  transition: all 0.2s;
}

.type-tag:hover {
  border-color: hsla(215, 80%, 45%, 0.4);
}

.type-tag.active {
  border-color: hsla(215, 80%, 45%, 1);
  background: hsla(215, 80%, 45%, 0.1);
  color: hsla(215, 80%, 45%, 1);
}

.type-input {
  display: none;
}

.form-textarea {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  font-size: 0.95rem;
  background: var(--color-background);
  color: var(--color-text);
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
  line-height: 1.5;
  transition: all 0.2s;
}

.form-textarea:focus {
  outline: none;
  border-color: hsla(215, 80%, 45%, 0.5);
  box-shadow: 0 0 0 3px hsla(215, 80%, 45%, 0.08);
}

.form-textarea::placeholder {
  color: var(--color-text);
  opacity: 0.4;
}

/* 关于我们弹窗 */
.about-modal {
  max-width: 480px;
}

.about-content {
  text-align: center;
}

.about-badge {
  display: inline-block;
  padding: 0.375rem 1rem;
  background: hsla(215, 80%, 45%, 0.1);
  color: hsla(215, 80%, 45%, 1);
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
  margin-bottom: 1rem;
}

.about-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--color-heading);
  margin-bottom: 1rem;
  line-height: 1.5;
}

.about-desc {
  font-size: 0.9rem;
  color: var(--color-text);
  line-height: 1.8;
  opacity: 0.9;
  margin-bottom: 1.5rem;
  text-align: left;
}

.about-section {
  text-align: left;
  margin-bottom: 1.25rem;
}

.about-section:last-child {
  margin-bottom: 0;
}

.section-label {
  font-size: 0.8rem;
  font-weight: 500;
  color: var(--color-heading);
  margin-bottom: 0.5rem;
}

.model-card {
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.625rem 1rem;
  background: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 8px;
}

.model-name {
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--color-heading);
}

.model-meta {
  font-size: 0.75rem;
  color: var(--color-text);
  opacity: 0.6;
  padding: 0.125rem 0.5rem;
  background: var(--color-background-mute);
  border-radius: 4px;
}

.source-link {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  padding: 0.875rem 1rem;
  background: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  text-decoration: none;
  color: var(--color-heading);
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s;
}

.source-link:hover {
  border-color: hsla(215, 80%, 45%, 1);
  background: hsla(215, 80%, 45%, 0.05);
}

.link-arrow {
  color: hsla(215, 80%, 45%, 1);
  font-size: 1rem;
}

.source-hint {
  font-size: 0.75rem;
  color: var(--color-text);
  opacity: 0.5;
  margin-top: 0.5rem;
  margin-bottom: 0;
}
</style>
