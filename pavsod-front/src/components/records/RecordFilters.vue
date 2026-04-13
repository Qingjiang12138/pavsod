<script setup lang="ts">
interface Props {
  searchQuery: string
  selectedDate: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  updateSearch: [value: string]
  search: []
  updateDate: [value: string]
}>()

const handleSearch = () => {
  emit('search')
}

const handleKeydown = (e: KeyboardEvent) => {
  if (e.key === 'Enter') {
    handleSearch()
  }
}
</script>

<template>
  <div class="record-filters">
    <div class="search-box">
      <span class="search-icon">🔍</span>
      <input
        type="text"
        placeholder="搜索视频名称"
        :value="searchQuery"
        @input="emit('updateSearch', ($event.target as HTMLInputElement).value)"
        @keydown="handleKeydown"
      />
      <button class="search-btn" @click="handleSearch">搜索</button>
    </div>
    <input
      type="date"
      class="date-picker"
      :value="selectedDate"
      @change="emit('updateDate', ($event.target as HTMLInputElement).value)"
    />
  </div>
</template>

<style scoped>
.record-filters {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.search-box {
  position: relative;
  flex: 1;
  max-width: 360px;
  display: flex;
  gap: 0.5rem;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 0.9rem;
  opacity: 0.5;
  pointer-events: none;
}

.search-box input {
  flex: 1;
  padding: 0.625rem 0.75rem 0.625rem 2.25rem;
  background: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 0.9rem;
  color: var(--color-heading);
}

.search-box input:focus {
  outline: none;
  border-color: hsla(210, 80%, 45%, 1);
}

.search-box input::placeholder {
  color: var(--color-text);
  opacity: 0.5;
}

.search-btn {
  padding: 0.625rem 1rem;
  background: hsla(210, 80%, 45%, 1);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}

.search-btn:hover {
  background: hsla(210, 80%, 40%, 1);
}

.date-picker {
  padding: 0.625rem 0.75rem;
  background: var(--color-background);
  border: 1px solid var(--color-border);
  border-radius: 6px;
  font-size: 0.9rem;
  color: var(--color-heading);
  cursor: pointer;
}

.date-picker:focus {
  outline: none;
  border-color: hsla(210, 80%, 45%, 1);
}

@media (max-width: 600px) {
  .record-filters {
    flex-direction: column;
  }

  .search-box {
    max-width: 100%;
  }
}
</style>
