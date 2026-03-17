<script setup lang="ts">
// 大模型评价（默认文本）
const evaluationText = `该视频显著性检测效果整体表现优秀。从检测结果来看：

1. **空间一致性**：模型对主要显著性区域的定位准确，边界清晰，与人工标注结果高度吻合。

2. **时序稳定性**：视频序列中显著性区域的追踪平滑，无明显的抖动或突变现象。

3. **多模态融合**：音频信息与视觉信息的融合效果良好，语音出现区域的显著性权重提升合理。

4. **全景适应性**：在 360° 全景场景下，模型对赤道区域和极地区的处理均衡，畸变控制得当。

综合评价：本次检测结果可信度较高，建议可用于下游的 VR 内容分析或智能剪辑任务。`
</script>

<template>
  <section class="evaluation-section">
    <h3 class="panel-title">🤖 AI 智能评价</h3>
    <div class="evaluation-card">
      <div class="evaluation-content">
        <pre class="evaluation-text">{{ evaluationText }}</pre>
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
