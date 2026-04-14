<template>
  <Teleport to="body">
    <Transition name="select-fade">
      <div
        v-if="modelValue"
        class="select-backdrop"
        @click.self="modalOpenModel = false"
      >
        <div class="select-box">
          <!-- 헤더 -->
          <div class="select-header">
            <span class="select-title">{{ title }}</span>
            <button @click="modalOpenModel = false" class="select-close">✕</button>
          </div>

          <!-- 리스트 -->
          <div class="select-body">
            <ul v-if="props.items.length > 0">
              <li
                v-for="item in pagedList"
                :key="item.id || item.codeValue"
                @click="selectItem(item)"
                class="select-item"
              >
                <span class="select-item-name">{{ item.name || item.codeName }}</span>
                <span
                  v-if="item.userType"
                  :class="['select-badge', getBadgeClass(item.userType)]"
                >
                  {{ item.userType }}
                </span>
              </li>
            </ul>
            <div v-else class="select-empty">데이터가 존재하지 않습니다.</div>
          </div>

          <!-- 페이지네이션 -->
          <div v-if="totalPages > 1" class="select-pagination">
            <button @click="page--" :disabled="page === 1" class="page-btn">＜</button>
            <button
              v-for="n in totalPages"
              :key="n"
              @click="page = n"
              :class="['page-btn', page === n && 'active']"
            >{{ n }}</button>
            <button @click="page++" :disabled="page === totalPages" class="page-btn">＞</button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch } from "vue";

const props = defineProps({
  modelValue: Boolean,
  items: { type: Array, default: () => [] },
  title: { type: String, default: "선택" },
});

const emit = defineEmits(["update:modelValue", "select"]);

const modalOpenModel = computed({
  get: () => props.modelValue,
  set: (val) => emit("update:modelValue", val),
});

const page = ref(1);
const perPage = 5;

watch(() => props.modelValue, (isOpen) => {
  if (isOpen) page.value = 1;
});

const totalPages = computed(() => Math.ceil(props.items.length / perPage) || 1);
const pagedList = computed(() => {
  const start = (page.value - 1) * perPage;
  return props.items.slice(start, start + perPage);
});

const selectItem = (item) => {
  emit("select", {
    name: item.name || item.codeName,
    value: item.id || item.codeValue,
    userType: item.userType,
  });
  modalOpenModel.value = false;
};

const getBadgeClass = (userType) => {
  if (userType === "PM") return "badge-pm";
  if (userType === "PL") return "badge-pl";
  return "badge-member";
};
</script>

<style scoped>
.select-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  /* ★ 핵심: 업무 모달(9999)보다 높게 */
  z-index: 100000;
}

.select-box {
  background: #fff;
  border-radius: 14px;
  width: 380px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25);
  overflow: hidden;
}

.select-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 18px;
  border-bottom: 1px solid #e2e8f0;
  flex-shrink: 0;
}
.select-title {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}
.select-close {
  background: none;
  border: none;
  font-size: 14px;
  color: #94a3b8;
  cursor: pointer;
  padding: 4px 6px;
  border-radius: 6px;
  transition: all 0.15s;
}
.select-close:hover {
  background: #f1f5f9;
  color: #475569;
}

.select-body {
  flex: 1;
  overflow-y: auto;
  min-height: 200px;
  padding: 8px 0;
}

.select-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 18px;
  cursor: pointer;
  transition: background 0.12s;
  border-bottom: 1px solid #f1f5f9;
}
.select-item:last-child { border-bottom: none; }
.select-item:hover { background: #f8fafc; }

.select-item-name {
  font-size: 13px;
  color: #1e293b;
}

.select-badge {
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 20px;
}
.badge-pm { background: #dbeafe; color: #1d4ed8; }
.badge-pl { background: #ede9fe; color: #7c3aed; }
.badge-member { background: #dcfce7; color: #15803d; }

.select-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 160px;
  font-size: 13px;
  color: #94a3b8;
}

.select-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 4px;
  padding: 12px 16px;
  border-top: 1px solid #e2e8f0;
  flex-shrink: 0;
}
.page-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: none;
  background: transparent;
  color: #64748b;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s;
}
.page-btn:hover:not(:disabled) { background: #f1f5f9; }
.page-btn:disabled { opacity: 0.25; cursor: not-allowed; }
.page-btn.active { background: #1e3a5f; color: #fff; }

/* 트랜지션 */
.select-fade-enter-active { transition: opacity 0.18s ease; }
.select-fade-leave-active { transition: opacity 0.15s ease; }
.select-fade-enter-from, .select-fade-leave-to { opacity: 0; }
</style>