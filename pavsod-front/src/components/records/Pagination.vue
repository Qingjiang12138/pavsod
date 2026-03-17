<script setup lang="ts">
interface Props {
  total: number
  currentPage: number
  pageSize: number
  totalPages: number
}

const props = defineProps<Props>()

const emit = defineEmits<{
  changePage: [page: number]
}>()

const pages = computed(() => {
  const result: (number | string)[] = []
  const maxVisible = 5

  if (props.totalPages <= maxVisible) {
    for (let i = 1; i <= props.totalPages; i++) {
      result.push(i)
    }
  } else {
    if (props.currentPage <= 3) {
      for (let i = 1; i <= 4; i++) result.push(i)
      result.push('...')
      result.push(props.totalPages)
    } else if (props.currentPage >= props.totalPages - 2) {
      result.push(1)
      result.push('...')
      for (let i = props.totalPages - 3; i <= props.totalPages; i++) {
        result.push(i)
      }
    } else {
      result.push(1)
      result.push('...')
      for (let i = props.currentPage - 1; i <= props.currentPage + 1; i++) {
        result.push(i)
      }
      result.push('...')
      result.push(props.totalPages)
    }
  }

  return result
})

import { computed } from 'vue'
</script>

<template>
  <div class="pagination">
    <span class="pagination-info">
      共 {{ total }} 条记录，第 {{ currentPage }}/{{ totalPages }} 页
    </span>

    <div class="pagination-controls">
      <button
        class="page-btn"
        :disabled="currentPage === 1"
        @click="emit('changePage', currentPage - 1)"
      >
        ‹
      </button>

      <button
        v-for="page in pages"
        :key="page"
        class="page-btn"
        :class="{ active: page === currentPage }"
        :disabled="page === '...'"
        @click="typeof page === 'number' && emit('changePage', page)"
      >
        {{ page }}
      </button>

      <button
        class="page-btn"
        :disabled="currentPage === totalPages"
        @click="emit('changePage', currentPage + 1)"
      >
        ›
      </button>
    </div>
  </div>
</template>

<style scoped>
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.pagination-info {
  font-size: 0.875rem;
  color: var(--color-text);
  opacity: 0.7;
}

.pagination-controls {
  display: flex;
  gap: 0.375rem;
}

.page-btn {
  min-width: 32px;
  height: 32px;
  padding: 0 0.5rem;
  background: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 0.875rem;
  color: var(--color-text);
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-btn:hover:not(:disabled) {
  border-color: hsla(210, 80%, 45%, 1);
  color: hsla(210, 80%, 45%, 1);
}

.page-btn.active {
  background: hsla(210, 80%, 45%, 1);
  border-color: hsla(210, 80%, 45%, 1);
  color: white;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 600px) {
  .pagination {
    flex-direction: column;
    align-items: center;
  }
}
</style>
