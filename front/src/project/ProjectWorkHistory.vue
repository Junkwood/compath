<template>
  <div class="flex h-screen overflow-hidden">
    <!-- Sidebar -->
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <!-- Content area -->
    <div class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden">
      <!-- Header -->
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
          <!-- 페이지 타이틀 -->
          <div class="mb-6 activity-title-row">
            <div class="activity-title-left">
              <h2 class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold">
                작업내역
              </h2>
              <div class="activity-sub-row">
                <span class="activity-project-name">【 {{ projectInfo.projectName }} 】</span>
                <span class="activity-project-period">
                  {{ projectInfo.startDate }} - {{ projectInfo.endDate }}
                </span>
              </div>
            </div>
          </div>

          <!-- 상단 필터 카드 -->
          <section class="activity-filter-card mb-5">
            <div class="filter-row">
              <div class="filter-item search-box">
                <label>검색</label>
                <input
                  v-model="searchKeyword"
                  type="text"
                  placeholder="사용자명, 대상명, 내용 검색"
                />
              </div>

              <div class="filter-item">
                <label>활동유형</label>
                <select v-model="selectedActivityType">
                  <option value="">전체</option>
                  <option value="CREATE">생성</option>
                  <option value="UPDATE">수정</option>
                  <option value="DELETE">삭제</option>
                </select>
              </div>

              <div class="filter-item">
                <label>대상유형</label>
                <select v-model="selectedTargetType">
                  <option value="">전체</option>
                  <option value="PROJECT">프로젝트</option>
                  <option value="SUBPROJECT">하위프로젝트</option>
                  <option value="MILESTONE">마일스톤</option>
                  <option value="TASK">업무</option>
                  <option value="MEMBER">구성원</option>
                  <option value="MEMO">메모</option>
                </select>
              </div>

              <div class="filter-item">
                <label>정렬</label>
                <select v-model="sortOrder">
                  <option value="DESC">최신순</option>
                  <option value="ASC">오래된순</option>
                </select>
              </div>
            </div>

            <div class="period-section">
              <div class="period-label">기간</div>

              <div class="period-chip-row">
                <button
                  v-for="item in quickRangeOptions"
                  :key="item.value"
                  class="period-chip"
                  :class="{ active: selectedQuickRange === item.value }"
                  @click="applyQuickRange(item.value)"
                >
                  {{ item.label }}
                </button>
              </div>

              <div v-if="selectedQuickRange === 'CUSTOM'" class="custom-date-row">
                <div class="date-input-wrap">
                  <label>시작일</label>
                  <input v-model="startDate" type="date" />
                </div>

                <div class="date-tilde">~</div>

                <div class="date-input-wrap">
                  <label>종료일</label>
                  <input v-model="endDate" type="date" />
                </div>
              </div>
            </div>
          </section>

          <!-- 작업내역 카드 -->
          <section class="activity-log-card">
            <div class="content-top">
              <div class="result-count">
                총 <strong>{{ filteredLogs.length }}</strong>건
              </div>
            </div>

            <div v-if="groupedLogs.length > 0" class="date-group-wrap">
              <div
                v-for="group in groupedLogs"
                :key="group.date"
                class="date-group"
              >
                <div class="date-group-header" @click="toggleDateGroup(group.date)">
                  <div class="date-title-wrap">
                    <span class="fold-icon">
                      {{ isGroupOpen(group.date) ? "▾" : "▸" }}
                    </span>
                    <span class="date-title">{{ formatDateTitle(group.date) }}</span>
                    <span class="date-count">{{ group.items.length }}건</span>
                  </div>

                  <button class="toggle-btn">
                    {{ isGroupOpen(group.date) ? "접기" : "펼치기" }}
                  </button>
                </div>

                <div v-show="isGroupOpen(group.date)" class="date-log-list">
                  <div
                    v-for="log in group.items"
                    :key="log.id"
                    class="log-card"
                  >
                    <div class="log-time-col">
                      <div class="log-time">{{ formatTime(log.createdAt) }}</div>
                    </div>

                    <div class="log-main-col">
                      <div class="log-top-line">
                        <div class="log-user-row">
                          <span class="user-name">{{ log.userName }}</span>
                          <span class="dot">•</span>
                          <span class="target-type">
                            {{ getTargetTypeLabel(log.targetType) }}
                          </span>
                        </div>

                        <span
                          class="type-badge"
                          :class="badgeClass(log.activityType)"
                        >
                          {{ getActivityTypeLabel(log.activityType) }}
                        </span>
                      </div>

                      <div class="log-message">
                        {{ log.message }}
                      </div>

                      <div class="log-detail" v-if="log.detail">
                        {{ log.detail }}
                      </div>

                      <div class="log-meta-row">
                        <span class="meta-chip">{{ log.projectPath }}</span>
                        <span v-if="log.targetName" class="meta-chip light">
                          대상: {{ log.targetName }}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-else class="empty-box">
              검색 조건에 맞는 작업내역이 없습니다.
            </div>
          </section>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";

import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";

const route = useRoute();
const sidebarOpen = ref(false);

const projectInfo = ref({
  projectId: null,
  projectName: "Compath",
  startDate: "2026-04-01",
  endDate: "2026-06-30",
});

const searchKeyword = ref("");
const selectedActivityType = ref("");
const selectedTargetType = ref("");
const sortOrder = ref("DESC");

const selectedQuickRange = ref("ALL");
const startDate = ref("");
const endDate = ref("");

const quickRangeOptions = [
  { label: "전체", value: "ALL" },
  { label: "오늘", value: "TODAY" },
  { label: "최근 7일", value: "7D" },
  { label: "최근 30일", value: "30D" },
  { label: "직접선택", value: "CUSTOM" },
];

const logs = ref([
  {
    id: 1,
    createdAt: "2026-04-08 14:32:00",
    userName: "장다정",
    activityType: "UPDATE",
    targetType: "PROJECT",
    targetName: "Compath",
    message: "프로젝트명을 수정했습니다.",
    detail: "기존: Compath_v1 → 변경: Compath",
    projectPath: "프로젝트 > 메인 프로젝트",
  },
  {
    id: 2,
    createdAt: "2026-04-08 13:50:00",
    userName: "김철수",
    activityType: "CREATE",
    targetType: "SUBPROJECT",
    targetName: "프론트엔드 개발",
    message: "하위 프로젝트를 생성했습니다.",
    detail: "하위프로젝트명: 프론트엔드 개발",
    projectPath: "프로젝트 > 1차 개발",
  },
  {
    id: 3,
    createdAt: "2026-04-08 11:20:00",
    userName: "이영희",
    activityType: "DELETE",
    targetType: "MILESTONE",
    targetName: "2차 개발",
    message: "마일스톤을 삭제했습니다.",
    detail: "삭제된 마일스톤: 2차 개발",
    projectPath: "프로젝트 > 마일스톤",
  },
  {
    id: 4,
    createdAt: "2026-04-07 17:05:00",
    userName: "장다정",
    activityType: "UPDATE",
    targetType: "TASK",
    targetName: "로그인 API 개발",
    message: "업무 상태를 변경했습니다.",
    detail: "기존: 시작 전 → 변경: 진행중",
    projectPath: "프로젝트 > 1차 개발 > 업무",
  },
  {
    id: 5,
    createdAt: "2026-04-07 15:30:00",
    userName: "박민수",
    activityType: "CREATE",
    targetType: "MEMBER",
    targetName: "홍길동",
    message: "프로젝트 구성원을 추가했습니다.",
    detail: "추가된 구성원: 홍길동 (PL)",
    projectPath: "프로젝트 > 구성원",
  },
  {
    id: 6,
    createdAt: "2026-04-07 10:10:00",
    userName: "장다정",
    activityType: "UPDATE",
    targetType: "MEMO",
    targetName: "회의 메모",
    message: "메모 내용을 수정했습니다.",
    detail: "일정 관련 코멘트가 수정되었습니다.",
    projectPath: "프로젝트 > 메모",
  },
]);

const openDateGroups = ref({});

const filteredLogs = computed(() => {
  let result = [...logs.value];

  if (selectedActivityType.value) {
    result = result.filter(
      (item) => item.activityType === selectedActivityType.value
    );
  }

  if (selectedTargetType.value) {
    result = result.filter(
      (item) => item.targetType === selectedTargetType.value
    );
  }

  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.trim().toLowerCase();
    result = result.filter((item) => {
      return (
        item.userName.toLowerCase().includes(keyword) ||
        (item.targetName || "").toLowerCase().includes(keyword) ||
        item.message.toLowerCase().includes(keyword) ||
        (item.detail || "").toLowerCase().includes(keyword) ||
        (item.projectPath || "").toLowerCase().includes(keyword)
      );
    });
  }

  if (selectedQuickRange.value === "CUSTOM" && startDate.value && endDate.value) {
    result = result.filter((item) => {
      const itemDate = item.createdAt.split(" ")[0];
      return itemDate >= startDate.value && itemDate <= endDate.value;
    });
  }

  if (selectedQuickRange.value === "TODAY") {
    const today = new Date().toISOString().slice(0, 10);
    result = result.filter((item) => item.createdAt.startsWith(today));
  }

  if (selectedQuickRange.value === "7D") {
    const baseDate = new Date();
    const fromDate = new Date(baseDate);
    fromDate.setDate(baseDate.getDate() - 6);

    result = result.filter((item) => {
      const itemDate = new Date(item.createdAt.replace(" ", "T"));
      return itemDate >= startOfDay(fromDate) && itemDate <= endOfDay(baseDate);
    });
  }

  if (selectedQuickRange.value === "30D") {
    const baseDate = new Date();
    const fromDate = new Date(baseDate);
    fromDate.setDate(baseDate.getDate() - 29);

    result = result.filter((item) => {
      const itemDate = new Date(item.createdAt.replace(" ", "T"));
      return itemDate >= startOfDay(fromDate) && itemDate <= endOfDay(baseDate);
    });
  }

  result.sort((a, b) => {
    const aTime = new Date(a.createdAt.replace(" ", "T")).getTime();
    const bTime = new Date(b.createdAt.replace(" ", "T")).getTime();
    return sortOrder.value === "DESC" ? bTime - aTime : aTime - bTime;
  });

  return result;
});

const groupedLogs = computed(() => {
  const groupMap = {};

  filteredLogs.value.forEach((log) => {
    const date = log.createdAt.split(" ")[0];
    if (!groupMap[date]) groupMap[date] = [];
    groupMap[date].push(log);
  });

  return Object.keys(groupMap).map((date) => ({
    date,
    items: groupMap[date],
  }));
});

watch(
  groupedLogs,
  (newGroups) => {
    const nextState = {};
    newGroups.forEach((group) => {
      nextState[group.date] =
        openDateGroups.value[group.date] !== undefined
          ? openDateGroups.value[group.date]
          : true;
    });
    openDateGroups.value = nextState;
  },
  { immediate: true }
);

function applyQuickRange(type) {
  selectedQuickRange.value = type;
  if (type !== "CUSTOM") {
    startDate.value = "";
    endDate.value = "";
  }
}

function isGroupOpen(date) {
  return openDateGroups.value[date];
}

function toggleDateGroup(date) {
  openDateGroups.value[date] = !openDateGroups.value[date];
}

function getActivityTypeLabel(type) {
  if (type === "CREATE") return "생성";
  if (type === "UPDATE") return "수정";
  if (type === "DELETE") return "삭제";
  return type;
}

function getTargetTypeLabel(type) {
  if (type === "PROJECT") return "프로젝트";
  if (type === "SUBPROJECT") return "하위프로젝트";
  if (type === "MILESTONE") return "마일스톤";
  if (type === "TASK") return "업무";
  if (type === "MEMBER") return "구성원";
  if (type === "MEMO") return "메모";
  return type;
}

function badgeClass(type) {
  if (type === "CREATE") return "create";
  if (type === "UPDATE") return "update";
  if (type === "DELETE") return "delete";
  return "";
}

function formatDateTitle(dateString) {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}.${month}.${day}`;
}

function formatTime(dateTimeString) {
  const timePart = dateTimeString.split(" ")[1];
  return timePart ? timePart.slice(0, 5) : "";
}

function startOfDay(date) {
  const d = new Date(date);
  d.setHours(0, 0, 0, 0);
  return d;
}

function endOfDay(date) {
  const d = new Date(date);
  d.setHours(23, 59, 59, 999);
  return d;
}

const fetchProjectDetail = async () => {
  try {
    const projectId = route.params.projectId;
    const res = await axios.get(`/api/ProjectDetail/${projectId}`);
    projectInfo.value = res.data;
  } catch (err) {
    console.error("프로젝트 상세 조회 실패:", err);
  }
};

onMounted(() => {
  fetchProjectDetail();
});
</script>

<style scoped>
.activity-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}

.activity-title-left {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.activity-sub-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.activity-project-name {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
}

.activity-project-period {
  font-size: 13px;
  color: #64748b;
}

.activity-filter-card,
.activity-log-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.activity-filter-card {
  padding: 20px;
}

.activity-log-card {
  padding: 20px;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  margin-bottom: 18px;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 160px;
}

.filter-item.search-box {
  flex: 1;
  min-width: 280px;
}

.filter-item label,
.date-input-wrap label,
.period-label {
  font-size: 13px;
  font-weight: 700;
  color: #4b5563;
}

.filter-item input,
.filter-item select,
.date-input-wrap input {
  height: 42px;
  border: 1px solid #dbe3ef;
  border-radius: 12px;
  padding: 0 14px;
  font-size: 14px;
  color: #111827;
  background: #fff;
  outline: none;
  box-sizing: border-box;
}

.period-section {
  border-top: 1px solid #eef2f7;
  padding-top: 18px;
}

.period-chip-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.period-chip {
  height: 38px;
  padding: 0 16px;
  border-radius: 999px;
  border: 1px solid #d8e1ee;
  background: #ffffff;
  color: #475569;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.2s;
}

.period-chip.active {
  background: #eaf2ff;
  border-color: #bfd3f6;
  color: #2f5fa7;
}

.custom-date-row {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  margin-top: 14px;
  flex-wrap: wrap;
}

.date-input-wrap {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.date-tilde {
  height: 42px;
  display: flex;
  align-items: center;
  color: #64748b;
  font-weight: 700;
}

.content-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.result-count {
  font-size: 15px;
  color: #4b5563;
}

.result-count strong {
  color: #111827;
}

.date-group-wrap {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.date-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.date-group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 2px;
  cursor: pointer;
}

.date-title-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
}

.fold-icon {
  font-size: 16px;
  color: #64748b;
  width: 14px;
}

.date-title {
  font-size: 17px;
  font-weight: 800;
  color: #1f2937;
}

.date-count {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: #f1f5f9;
  color: #475569;
  font-size: 12px;
  font-weight: 700;
}

.toggle-btn {
  border: none;
  background: transparent;
  color: #64748b;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
}

.date-log-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.log-card {
  border: 1px solid #e5eaf3;
  border-radius: 16px;
  background: #ffffff;
  padding: 18px 20px;
  display: flex;
  gap: 18px;
  transition: 0.2s;
}

.log-card:hover {
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.06);
  transform: translateY(-1px);
}

.log-time-col {
  width: 72px;
  flex-shrink: 0;
  padding-top: 2px;
}

.log-time {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 58px;
  height: 28px;
  border-radius: 999px;
  background: #f3f6fb;
  color: #475569;
  font-size: 13px;
  font-weight: 700;
}

.log-main-col {
  flex: 1;
  min-width: 0;
}

.log-top-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.log-user-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.user-name {
  font-size: 15px;
  font-weight: 800;
  color: #111827;
}

.dot {
  color: #9ca3af;
}

.target-type {
  font-size: 13px;
  font-weight: 700;
  color: #64748b;
}

.type-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 58px;
  height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
  flex-shrink: 0;
}

.type-badge.create {
  background: #e0f2fe;
  color: #0369a1;
}

.type-badge.update {
  background: #fef3c7;
  color: #b45309;
}

.type-badge.delete {
  background: #fee2e2;
  color: #b91c1c;
}

.log-message {
  font-size: 16px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 6px;
  line-height: 1.5;
}

.log-detail {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 12px;
  line-height: 1.6;
}

.log-meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.meta-chip {
  display: inline-flex;
  align-items: center;
  height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  background: #eef4ff;
  color: #315ea8;
  font-size: 12px;
  font-weight: 700;
}

.meta-chip.light {
  background: #f3f4f6;
  color: #4b5563;
}

.empty-box {
  border: 1px dashed #d1d9e6;
  border-radius: 16px;
  padding: 60px 20px;
  text-align: center;
  color: #6b7280;
  font-size: 15px;
  background: #fafbfd;
}

@media (max-width: 768px) {
  .activity-title-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .activity-sub-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .activity-project-name {
    font-size: 15px;
  }

  .log-card {
    flex-direction: column;
    gap: 12px;
  }

  .log-time-col {
    width: auto;
  }

  .log-top-line {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>