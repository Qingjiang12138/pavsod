<script setup lang="ts">
interface Props {
  phone?: string
  email?: string
  hasPassword: boolean
}

const props = defineProps<Props>()

const maskPhone = (phone: string) => {
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

const maskEmail = (email: string) => {
  const [name, domain] = email.split('@')
  const maskedName = name.length > 2 ? name.slice(0, 2) + '***' : '***'
  return `${maskedName}@${domain}`
}
</script>

<template>
  <div class="security-settings">
    <h3 class="section-title">账户安全</h3>

    <div class="security-list">
      <!-- 登录密码 -->
      <div class="security-item">
        <div class="security-icon"></div>
        <div class="security-info">
          <span class="security-label">登录密码</span>
          <span class="security-status" :class="{ 'status-active': hasPassword }">
            {{ hasPassword ? '已设置' : '未设置' }}
          </span>
        </div>
      </div>

      <!-- 手机绑定 -->
      <div class="security-item">
        <div class="security-icon"></div>
        <div class="security-info">
          <span class="security-label">手机号码</span>
          <span class="security-status" :class="{ 'status-active': phone }">
            {{ phone ? maskPhone(phone) : '未绑定' }}
          </span>
        </div>
      </div>

      <!-- 邮箱绑定 -->
      <div class="security-item">
        <div class="security-icon"></div>
        <div class="security-info">
          <span class="security-label">邮箱地址</span>
          <span class="security-status" :class="{ 'status-active': email }">
            {{ email ? maskEmail(email) : '未绑定' }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.security-settings {
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

.security-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.security-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.875rem;
  background: var(--color-background);
  border-radius: 8px;
}

.security-icon {
  font-size: 1.5rem;
  width: 40px;
  text-align: center;
}

.security-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  min-width: 0;
}

.security-label {
  font-size: 0.85rem;
  font-weight: 500;
  color: var(--color-heading);
}

.security-status {
  font-size: 0.75rem;
  color: var(--color-text);
  opacity: 0.6;
}

.status-active {
  color: hsla(150, 60%, 45%, 1);
  opacity: 1;
}

.security-action {
  padding: 0.4rem 0.875rem;
  background: transparent;
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 0.8rem;
  color: var(--color-text);
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.security-action:hover {
  border-color: hsla(210, 80%, 45%, 1);
  color: hsla(210, 80%, 45%, 1);
  background: hsla(210, 80%, 45%, 0.05);
}
</style>
