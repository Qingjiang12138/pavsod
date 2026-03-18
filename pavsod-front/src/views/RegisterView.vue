<script setup lang="ts">
import { ref } from 'vue'
import { useAuth } from '@/stores/auth'

const { register } = useAuth()

const username = ref('')
const phone = ref('')
const password = ref('')
const confirmPassword = ref('')
const errorMsg = ref('')
const isLoading = ref(false)

const handleRegister = async () => {
  errorMsg.value = ''

  // 验证
  if (!username.value || !phone.value || !password.value) {
    errorMsg.value = '请填写所有必填项'
    return
  }

  if (username.value.length < 2 || username.value.length > 20) {
    errorMsg.value = '用户名长度为2-20个字符'
    return
  }

  if (!/^1[3-9]\d{9}$/.test(phone.value)) {
    errorMsg.value = '请输入有效的手机号码'
    return
  }

  if (password.value.length < 6) {
    errorMsg.value = '密码长度不能少于6位'
    return
  }

  if (password.value !== confirmPassword.value) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }

  isLoading.value = true

  // 模拟注册请求
  setTimeout(() => {
    const success = register(username.value, phone.value, password.value)
    if (!success) {
      errorMsg.value = '该手机号已被注册'
    }
    isLoading.value = false
  }, 500)
}

const goToLogin = () => {
  window.location.href = '/login'
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-header">
        <h1 class="auth-title">PAV-SOD</h1>
        <p class="auth-subtitle">全景级音视频显著性检测系统</p>
      </div>

      <div class="auth-form">
        <h2 class="form-title">创建账号</h2>

        <div class="form-group">
          <label class="form-label">用户名</label>
          <input
            v-model="username"
            type="text"
            class="form-input"
            placeholder="请输入用户名（2-20个字符）"
          />
        </div>

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
            placeholder="请输入密码（至少6位）"
          />
        </div>

        <div class="form-group">
          <label class="form-label">确认密码</label>
          <input
            v-model="confirmPassword"
            type="password"
            class="form-input"
            placeholder="请再次输入密码"
          />
        </div>

        <div v-if="errorMsg" class="error-message">
          {{ errorMsg }}
        </div>

        <button
          class="auth-btn"
          :disabled="isLoading"
          @click="handleRegister"
        >
          <span v-if="isLoading">注册中...</span>
          <span v-else>注册</span>
        </button>

        <div class="auth-footer">
          <span>已有账号？</span>
          <a href="#" class="auth-link" @click.prevent="goToLogin">立即登录</a>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, hsla(215, 80%, 45%, 0.08) 0%, hsla(250, 60%, 55%, 0.08) 100%);
  padding: 2rem;
}

.auth-container {
  width: 100%;
  max-width: 420px;
  background: var(--color-background-soft);
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.auth-header {
  background: linear-gradient(135deg, hsla(215, 80%, 45%, 1) 0%, hsla(250, 60%, 55%, 1) 100%);
  padding: 2.5rem 2rem;
  text-align: center;
  color: white;
}

.auth-title {
  font-size: 1.75rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  letter-spacing: 0.1em;
}

.auth-subtitle {
  font-size: 0.875rem;
  opacity: 0.9;
}

.auth-form {
  padding: 2rem;
}

.form-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--color-heading);
  margin-bottom: 1.5rem;
  text-align: center;
}

.form-group {
  margin-bottom: 1.25rem;
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
  padding: 0.875rem;
  background: linear-gradient(135deg, hsla(215, 80%, 45%, 1) 0%, hsla(250, 60%, 55%, 1) 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.auth-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px hsla(215, 80%, 45%, 0.35);
}

.auth-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.auth-footer {
  margin-top: 1.5rem;
  text-align: center;
  font-size: 0.875rem;
  color: var(--color-text);
}

.auth-link {
  color: hsla(215, 80%, 45%, 1);
  font-weight: 500;
  text-decoration: none;
  margin-left: 0.25rem;
}

.auth-link:hover {
  text-decoration: underline;
}
</style>
