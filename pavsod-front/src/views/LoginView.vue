<script setup lang="ts">
import { ref } from 'vue'
import { useAuth } from '@/stores/auth'

const { login } = useAuth()

const phone = ref('')
const password = ref('')
const errorMsg = ref('')
const isLoading = ref(false)

const handleLogin = async () => {
  errorMsg.value = ''

  // 简单验证
  if (!phone.value || !password.value) {
    errorMsg.value = '请填写手机号和密码'
    return
  }

  if (!/^1[3-9]\d{9}$/.test(phone.value)) {
    errorMsg.value = '请输入有效的手机号码'
    return
  }

  isLoading.value = true

  try {
    await login(phone.value, password.value)
    // 登录成功会自动跳转首页（在useAuth中处理）
  } catch (error: any) {
    errorMsg.value = error.message || '手机号或密码错误'
    isLoading.value = false
  }
}

const goToRegister = () => {
  window.location.href = '/register'
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-container">
      <section class="brand-panel">
        <div class="brand-mark">PAV-SOD</div>
        <h1 class="brand-title">全景级音视频显著性检测系统</h1>
        <p class="brand-desc">
          融合视觉帧与音频线索，定位视频中最吸引注意力的目标区域，并生成直观的显著性分析结果。
        </p>

        <div class="feature-list">
          <div class="feature-item">
            <span class="feature-value">Audio</span>
            <span class="feature-label">音频辅助建模</span>
          </div>
          <div class="feature-item">
            <span class="feature-value">360°</span>
            <span class="feature-label">全景视频分析</span>
          </div>
          <div class="feature-item">
            <span class="feature-value">AI</span>
            <span class="feature-label">智能结果评价</span>
          </div>
        </div>

        <div class="workflow-strip">
          <div class="workflow-item">
            <span class="workflow-index">01</span>
            <span class="workflow-label">上传视频</span>
          </div>
          <div class="workflow-item">
            <span class="workflow-index">02</span>
            <span class="workflow-label">提取音频</span>
          </div>
          <div class="workflow-item">
            <span class="workflow-index">03</span>
            <span class="workflow-label">显著检测</span>
          </div>
          <div class="workflow-item">
            <span class="workflow-index">04</span>
            <span class="workflow-label">生成评价</span>
          </div>
        </div>
      </section>

      <form class="auth-form" @submit.prevent="handleLogin">
        <p class="form-eyebrow">Welcome back</p>
        <h2 class="form-title">欢迎登录</h2>
        <p class="form-subtitle">登录后即可上传视频并查看检测结果</p>

        <div class="form-group">
          <label class="form-label">手机号码</label>
          <input
            v-model="phone"
            type="tel"
            class="form-input"
            placeholder="请输入手机号码"
            maxlength="11"
          />
        </div>

        <div class="form-group">
          <label class="form-label">登录密码</label>
          <input
            v-model="password"
            type="password"
            class="form-input"
            placeholder="请输入密码"
          />
        </div>

        <div v-if="errorMsg" class="error-message">
          {{ errorMsg }}
        </div>

        <button
          class="auth-btn"
          type="submit"
          :disabled="isLoading"
        >
          <span v-if="isLoading">登录中...</span>
          <span v-else>登录</span>
        </button>

        <div class="auth-footer">
          <span>还没有账号？</span>
          <a href="#" class="auth-link" @click.prevent="goToRegister">立即注册</a>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(circle at 18% 18%, rgba(45, 140, 255, 0.32), transparent 30%),
    radial-gradient(circle at 82% 24%, rgba(20, 184, 166, 0.22), transparent 28%),
    linear-gradient(135deg, #07111f 0%, #10233a 48%, #102d36 100%);
  padding: 2rem;
  overflow: hidden;
  position: relative;
}

.auth-page::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
  background-size: 44px 44px;
  mask-image: linear-gradient(to bottom, rgba(0, 0, 0, 0.75), transparent);
  pointer-events: none;
}

.auth-container {
  width: 100%;
  max-width: 980px;
  min-height: 590px;
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(360px, 0.95fr);
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.55);
  border-radius: 24px;
  box-shadow: 0 28px 80px rgba(0, 0, 0, 0.34);
  overflow: hidden;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(22px);
}

.brand-panel {
  padding: 3rem;
  color: white;
  background: linear-gradient(160deg, rgba(23, 98, 210, 0.96), rgba(13, 148, 136, 0.88));
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  position: relative;
  overflow: hidden;
}

.brand-panel::after {
  content: '';
  position: absolute;
  width: 340px;
  height: 340px;
  right: -130px;
  bottom: -120px;
  border: 1px solid rgba(255, 255, 255, 0.22);
  border-radius: 50%;
  box-shadow: inset 0 0 70px rgba(255, 255, 255, 0.12);
}

.brand-mark {
  width: fit-content;
  padding: 0.45rem 0.7rem;
  border: 1px solid rgba(255, 255, 255, 0.38);
  border-radius: 8px;
  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.16em;
  position: relative;
  z-index: 1;
}

.brand-title {
  max-width: 430px;
  font-size: 2.4rem;
  line-height: 1.18;
  font-weight: 700;
  margin: 6rem 0 1rem;
  position: relative;
  z-index: 1;
}

.brand-desc {
  max-width: 430px;
  font-size: 1rem;
  line-height: 1.8;
  opacity: 0.9;
  position: relative;
  z-index: 1;
}

.feature-list {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.75rem;
  margin-top: 2.25rem;
  position: relative;
  z-index: 1;
}

.feature-item {
  padding: 0.85rem;
  border: 1px solid rgba(255, 255, 255, 0.24);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.12);
}

.feature-value {
  display: block;
  font-size: 1rem;
  font-weight: 700;
}

.feature-label {
  display: block;
  margin-top: 0.2rem;
  font-size: 0.74rem;
  opacity: 0.78;
}

.workflow-strip {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 0.5rem;
  margin-top: 2rem;
  padding-top: 1.25rem;
  border-top: 1px solid rgba(255, 255, 255, 0.18);
  position: relative;
  z-index: 1;
}

.workflow-item {
  position: relative;
  min-height: 56px;
}

.workflow-item::after {
  content: '';
  position: absolute;
  top: 14px;
  left: 38px;
  right: -0.25rem;
  height: 1px;
  background: rgba(255, 255, 255, 0.28);
}

.workflow-item:last-child::after {
  display: none;
}

.workflow-index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.26);
  font-size: 0.7rem;
  font-weight: 700;
}

.workflow-label {
  display: block;
  margin-top: 0.5rem;
  font-size: 0.74rem;
  opacity: 0.86;
}

.auth-form {
  padding: 3.25rem 3rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-eyebrow {
  color: #0d9488;
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  margin-bottom: 0.35rem;
}

.form-title {
  font-size: 1.9rem;
  font-weight: 700;
  color: #10233a;
  margin-bottom: 0.4rem;
}

.form-subtitle {
  color: rgba(16, 35, 58, 0.62);
  font-size: 0.92rem;
  margin-bottom: 2rem;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: #1f3349;
  margin-bottom: 0.5rem;
}

.form-input {
  width: 100%;
  padding: 0.85rem 1rem;
  border: 1px solid rgba(45, 85, 120, 0.16);
  border-radius: 10px;
  font-size: 0.95rem;
  background: rgba(248, 250, 252, 0.92);
  color: #10233a;
  transition: all 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: rgba(13, 148, 136, 0.65);
  background: #ffffff;
  box-shadow: 0 0 0 4px rgba(13, 148, 136, 0.1);
}

.form-input::placeholder {
  color: rgba(16, 35, 58, 0.42);
}

.error-message {
  color: #dc2626;
  font-size: 0.875rem;
  margin-bottom: 1rem;
  padding: 0.5rem 0.75rem;
  background: rgba(220, 38, 38, 0.08);
  border-radius: 6px;
}

.auth-btn {
  width: 100%;
  padding: 0.95rem;
  background: linear-gradient(135deg, #1762d2 0%, #0d9488 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.auth-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 12px 26px rgba(23, 98, 210, 0.26);
}

.auth-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.auth-footer {
  margin-top: 1.5rem;
  text-align: center;
  font-size: 0.875rem;
  color: rgba(16, 35, 58, 0.68);
}

.auth-link {
  color: #1762d2;
  font-weight: 700;
  text-decoration: none;
  margin-left: 0.25rem;
}

.auth-link:hover {
  text-decoration: underline;
}

@media (max-width: 820px) {
  .auth-container {
    grid-template-columns: 1fr;
    max-width: 520px;
  }

  .brand-panel {
    min-height: 320px;
    padding: 2rem;
  }

  .brand-title {
    font-size: 1.9rem;
    margin-top: 2rem;
  }

  .auth-form {
    padding: 2rem;
  }
}

@media (max-width: 520px) {
  .auth-page {
    padding: 1rem;
  }

  .feature-list {
    grid-template-columns: 1fr;
  }

  .workflow-strip {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .workflow-item::after {
    display: none;
  }
}
</style>
