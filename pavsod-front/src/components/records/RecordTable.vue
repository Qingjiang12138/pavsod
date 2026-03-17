<script setup lang="ts">
import { computed } from 'vue'

interface Record {
  id: string
  name: string
  thumbnail?: string
  detectTime: string
  duration: string
  status: 'completed' | 'processing' | 'failed'
}

interface Props {
  records: Record[]
  selectedIds: string[]
}

const props = defineProps<Props>()

const emit = defineEmits<{
  toggleSelect: [id: string]
  toggleSelectAll: []
  viewResult: [id: string]
  delete: [id: string]
}>()

const allSelected = computed(() => {
  return props.records.length > 0 && props.records.every(r => props.selectedIds.includes(r.id))
})

const someSelected = computed(() => {
  return props.selectedIds.length > 0 && props.selectedIds.length < props.records.length
})

const statusMap = {
  completed: { text: '已完成', class: 'status-completed' },
  processing: { text: '处理中', class: 'status-processing' },
  failed: { text: '失败', class: 'status-failed' }
}
</script>

<template>
  <div class="record-table-wrapper">
    <table class="record-table">
      <colgroup>
        <col class="col-checkbox" />
        <col class="col-index" />
        <col class="col-thumbnail" />
        <col class="col-name" />
        <col class="col-time" />
        <col class="col-duration" />
        <col class="col-status" />
        <col class="col-action" />
      </colgroup>
      <thead>
        <tr>
          <th class="col-checkbox">
            <label class="checkbox">
              <input
                type="checkbox"
                :checked="allSelected"
                :indeterminate="someSelected"
                @change="emit('toggleSelectAll')"
              />
              <span class="checkmark"></span>
            </label>
          </th>
          <th class="col-index">序号</th>
          <th class="col-thumbnail">封面</th>
          <th class="col-name">视频名称</th>
          <th class="col-time">检测时间</th>
          <th class="col-duration">视频时长</th>
          <th class="col-status">处理状态</th>
          <th class="col-action">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(record, index) in records" :key="record.id">
          <td class="col-checkbox">
            <label class="checkbox">
              <input
                type="checkbox"
                :checked="selectedIds.includes(record.id)"
                @change="emit('toggleSelect', record.id)"
              />
              <span class="checkmark"></span>
            </label>
          </td>
          <td class="col-index">{{ index + 1 }}</td>
          <td class="col-thumbnail">
            <div class="thumb-wrapper">
              <img v-if="record.thumbnail" :src="record.thumbnail" :alt="record.name" />
              <span v-else class="thumb-placeholder">🎬</span>
            </div>
          </td>
          <td class="col-name" :title="record.name">{{ record.name }}</td>
          <td class="col-time">{{ record.detectTime }}</td>
          <td class="col-duration">{{ record.duration }}</td>
          <td class="col-status">
            <span class="status-tag" :class="statusMap[record.status].class">
              {{ statusMap[record.status].text }}
            </span>
          </td>
          <td class="col-action">
            <div class="action-btns">
              <button
                v-if="record.status === 'completed'"
                class="action-btn view"
                @click="emit('viewResult', record.id)"
              >
                查看结果
              </button>
              <button
                v-else-if="record.status === 'processing'"
                class="action-btn processing"
                disabled
              >
                处理中
              </button>
              <button
                v-else
                class="action-btn retry"
                @click="emit('viewResult', record.id)"
              >
                重试
              </button>
              <button class="action-btn delete" @click="emit('delete', record.id)">
                删除
              </button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.record-table-wrapper {
  width: 100%;
  background: var(--color-background-soft);
  border-radius: 12px;
  border: 1px solid var(--color-border);
  overflow: hidden;
}

.record-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.875rem;
  table-layout: fixed;
}

.record-table th,
.record-table td {
  padding: 0.75rem;
  text-align: left;
  border-bottom: 1px solid var(--color-border);
  vertical-align: middle;
}

.record-table th {
  background: var(--color-background-mute);
  font-weight: 600;
  color: var(--color-heading);
  font-size: 0.8rem;
}

.record-table tbody tr:hover {
  background: var(--color-background);
}

.record-table tbody tr:last-child td {
  border-bottom: none;
}

/* 列宽 - 百分比分配确保填满 */
col.col-checkbox,
th.col-checkbox,
td.col-checkbox {
  width: 4%;
  text-align: center;
}

col.col-index,
th.col-index,
td.col-index {
  width: 6%;
}

col.col-thumbnail,
th.col-thumbnail,
td.col-thumbnail {
  width: 8%;
}

col.col-name,
th.col-name,
td.col-name {
  width: 24%;
}

col.col-time,
th.col-time,
td.col-time {
  width: 16%;
}

col.col-duration,
th.col-duration,
td.col-duration {
  width: 10%;
}

col.col-status,
th.col-status,
td.col-status {
  width: 10%;
}

col.col-action,
th.col-action,
td.col-action {
  width: 22%;
}

/* 复选框 */
.checkbox {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.checkbox input {
  opacity: 0;
  width: 0;
  height: 0;
}

.checkmark {
  display: block;
  width: 18px;
  height: 18px;
  border: 2px solid var(--color-border);
  border-radius: 4px;
  transition: all 0.2s;
}

.checkbox input:checked + .checkmark {
  background: hsla(210, 80%, 45%, 1);
  border-color: hsla(210, 80%, 45%, 1);
}

.checkmark::after {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 12px;
  opacity: 0;
}

.checkbox input:checked + .checkmark::after {
  opacity: 1;
}

/* 封面缩略图 */
.thumb-wrapper {
  width: 48px;
  height: 36px;
  border-radius: 4px;
  overflow: hidden;
  background: var(--color-background-mute);
  display: flex;
  align-items: center;
  justify-content: center;
}

.thumb-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumb-placeholder {
  font-size: 1.25rem;
}

/* 单元格内容 */
.col-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 状态标签 */
.status-tag {
  display: inline-block;
  padding: 0.25rem 0.625rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 500;
}

.status-completed {
  background: hsla(150, 60%, 45%, 0.15);
  color: hsla(150, 60%, 45%, 1);
}

.status-processing {
  background: hsla(210, 80%, 45%, 0.15);
  color: hsla(210, 80%, 45%, 1);
}

.status-failed {
  background: hsla(0, 70%, 50%, 0.15);
  color: hsla(0, 70%, 50%, 1);
}

/* 操作按钮 - 横向排列 */
.action-btns {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.action-btn {
  padding: 0.375rem 0.625rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
  white-space: nowrap;
  flex-shrink: 0;
}

.action-btn.view {
  background: hsla(210, 80%, 45%, 0.1);
  color: hsla(210, 80%, 45%, 1);
}

.action-btn.view:hover {
  background: hsla(210, 80%, 45%, 0.2);
}

.action-btn.processing {
  background: var(--color-background-mute);
  color: var(--color-text);
  opacity: 0.6;
  cursor: not-allowed;
}

.action-btn.retry {
  background: hsla(30, 90%, 50%, 0.1);
  color: hsla(30, 90%, 50%, 1);
}

.action-btn.retry:hover {
  background: hsla(30, 90%, 50%, 0.2);
}

.action-btn.delete {
  background: hsla(0, 70%, 50%, 0.1);
  color: hsla(0, 70%, 50%, 1);
}

.action-btn.delete:hover {
  background: hsla(0, 70%, 50%, 0.2);
}

/* 响应式 */
@media (max-width: 900px) {
  .record-table-wrapper {
    overflow-x: auto;
  }

  .record-table {
    min-width: 800px;
  }
}
</style>
