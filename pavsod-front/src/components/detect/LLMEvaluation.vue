<script setup lang="ts">
interface Props {
  evaluation?: string
}

withDefaults(defineProps<Props>(), {
  evaluation: '暂无评价内容，请上传视频并等待检测完成。'
})
</script>

<template>
  <section class="evaluation-section">
    <h3 class="panel-title">🤖 AI 智能评价</h3>
    <div class="evaluation-card">
      <div class="evaluation-content">
        <pre class="evaluation-text">{{ evaluation }}</pre>
      </div>
    </div>
  </section>
</template>

<style scoped>
.evaluation-section {
  background: var(--color-background-soft);
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid var(--color-border);
}

.panel-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--color-heading);
  margin-bottom: 1rem;
}

.evaluation-card {
  background: var(--color-background);
  border-radius: 8px;
  padding: 1.25rem;
  border-left: 3px solid hsla(210, 80%, 45%, 1);
}

.evaluation-text {
  font-size: 0.9rem;
  line-height: 1.8;
  color: var(--color-text);
  white-space: pre-wrap;
  font-family: inherit;
  margin: 0;
}
</style>
