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
            <button @click="modalOpenModel = false" class="select-close">
              ✕
            </button>
          </div>

          <!-- 탭 (유저 선택 모달일 때만) -->
          <div v-if="tabs.length > 1" class="select-tabs">
            <button
              v-for="tab in tabs"
              :key="tab"
              :class="['tab-btn', activeTab === tab && 'active']"
              @click="onTabChange(tab)"
            >
              {{ tab }}
              <span class="tab-count">{{ countByTab(tab) }}</span>
            </button>
          </div>

          <!-- 리스트 -->
          <div class="select-body">
            <ul v-if="filteredItems.length > 0">
              <li
                v-for="item in pagedList"
                :key="item.id || item.codeValue"
                @click="selectItem(item)"
                class="select-item"
              >
                <span class="select-item-name">{{
                  item.name || item.codeName
                }}</span>
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
            <button @click="page--" :disabled="page === 1" class="page-btn">
              ＜
            </button>
            <button
              v-for="n in totalPages"
              :key="n"
              @click="page = n"
              :class="['page-btn', page === n && 'active']"
            >
              {{ n }}
            </button>
            <button
              @click="page++"
              :disabled="page === totalPages"
              class="page-btn"
            >
              ＞
            </button>
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
const activeTab = ref("전체");

// 탭 목록: 전체 + 고유 역할들
const tabs = computed(() => {
  const roles = [
    ...new Set(props.items.map((i) => i.userType).filter(Boolean)),
  ];
  return roles.length > 0 ? ["전체", ...roles] : [];
});

const countByTab = (tab) => {
  if (tab === "전체") return props.items.length;
  return props.items.filter((i) => i.userType === tab).length;
};

const onTabChange = (tab) => {
  activeTab.value = tab;
  page.value = 1;
};

// 탭 필터 적용된 아이템
const filteredItems = computed(() => {
  if (activeTab.value === "전체" || !activeTab.value) return props.items;
  return props.items.filter((i) => i.userType === activeTab.value);
});

watch(
  () => props.modelValue,
  (isOpen) => {
    if (isOpen) {
      page.value = 1;
      activeTab.value = "전체";
    }
  },
);

const totalPages = computed(
  () => Math.ceil(filteredItems.value.length / perPage) || 1,
);
const pagedList = computed(() => {
  const start = (page.value - 1) * perPage;
  return filteredItems.value.slice(start, start + perPage);
});

const selectItem = (item) => {
  emit("select", {
    name: item.name || item.codeName,
    value: item.id || item.codeValue,
    userType: item.userType,
  });
  modalOpenModel.value = false;
};

// 역할별 뱃지 색상 (동적으로 순환)
const BADGE_COLORS = [
  "badge-blue",
  "badge-purple",
  "badge-green",
  "badge-orange",
  "badge-pink",
  "badge-teal",
];

const roleColorMap = {};
let colorIndex = 0;

const getBadgeClass = (userType) => {
  if (!userType) return "";
  if (!roleColorMap[userType]) {
    roleColorMap[userType] = BADGE_COLORS[colorIndex % BADGE_COLORS.length];
    colorIndex++;
  }
  return roleColorMap[userType];
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
  z-index: 100000;
}

.select-box {
  background: #fff;
  border-radius: 14px;
  width: 400px;
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

/* 탭 */
.select-tabs {
  display: flex;
  gap: 4px;
  padding: 10px 14px;
  border-bottom: 1px solid #e2e8f0;
  flex-shrink: 0;
  overflow-x: auto;
  scrollbar-width: none;
}
.select-tabs::-webkit-scrollbar {
  display: none;
}

.tab-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 5px 12px;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  color: #64748b;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.15s;
}
.tab-btn:hover {
  background: #f1f5f9;
  border-color: #94a3b8;
}
.tab-btn.active {
  background: #1e3a5f;
  color: #fff;
  border-color: #1e3a5f;
}

.tab-count {
  background: rgba(255, 255, 255, 0.25);
  border-radius: 10px;
  padding: 0 5px;
  font-size: 11px;
}
.tab-btn:not(.active) .tab-count {
  background: #e2e8f0;
  color: #64748b;
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
.select-item:last-child {
  border-bottom: none;
}
.select-item:hover {
  background: #f8fafc;
}

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

/* 역할별 뱃지 색상 6종 */
.badge-blue {
  background: #dbeafe;
  color: #1d4ed8;
}
.badge-purple {
  background: #ede9fe;
  color: #7c3aed;
}
.badge-green {
  background: #dcfce7;
  color: #15803d;
}
.badge-orange {
  background: #ffedd5;
  color: #c2410c;
}
.badge-pink {
  background: #fce7f3;
  color: #be185d;
}
.badge-teal {
  background: #ccfbf1;
  color: #0f766e;
}

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
.page-btn:hover:not(:disabled) {
  background: #f1f5f9;
}
.page-btn:disabled {
  opacity: 0.25;
  cursor: not-allowed;
}
.page-btn.active {
  background: #1e3a5f;
  color: #fff;
}

.select-fade-enter-active {
  transition: opacity 0.18s ease;
}
.select-fade-leave-active {
  transition: opacity 0.15s ease;
}
.select-fade-enter-from,
.select-fade-leave-to {
  opacity: 0;
}
</style>
