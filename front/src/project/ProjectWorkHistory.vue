<template>
  <div class="dashboard-page flex h-screen overflow-hidden">
    <!-- Sidebar -->
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <!-- Content area -->
    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden bg-gray-50"
    >
      <!-- Header -->
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="sub-header">
          <div class="breadcrumb">
            <span>홈</span>
            <span class="bc-sep">›</span>
            <span>{{ projectInfo.projectName }}</span>
            <span class="bc-sep">›</span>
            <span class="bc-cur">작업내역</span>
          </div>
        </div>

        <div class="page-container">
          <!-- 페이지 타이틀 -->
          <div class="activity-title-row">
            <div class="activity-title-left">
              <div class="activity-sub-row">
                <span class="activity-project-name">
                  {{ projectInfo.projectName }} 작업내역
                </span>
                <span class="activity-project-period">
                  {{ projectInfo.startDate }} - {{ projectInfo.endDate }}
                </span>
              </div>
            </div>
          </div>

          <!-- 상단 필터 카드 -->
          <section class="panel activity-filter-card">
            <div class="panel-body filter-card-body">
              <div class="filter-row">
                <div class="filter-item search-box">
                  <label>검색</label>
                  <input
                    v-model="activityStore.searchKeyword"
                    type="text"
                    placeholder="사용자명, 대상명, 내용 검색"
                  />
                </div>

                <div class="filter-item">
                  <label>활동유형</label>
                  <select v-model="activityStore.selectedActivityType">
                    <option value="">전체</option>
                    <option value="J0">프로젝트 생성</option>
                    <option value="J1">프로젝트 수정</option>
                    <option value="J2">프로젝트 삭제</option>
                    <option value="J3">프로젝트 상태변경</option>
                    <option value="M0">마일스톤 생성</option>
                    <option value="M1">마일스톤 수정</option>
                    <option value="M2">마일스톤 삭제</option>
                    <option value="M3">마일스톤 상태변경</option>
                    <option value="T0">업무 생성</option>
                    <option value="T1">업무 수정</option>
                    <option value="T2">업무 삭제</option>
                    <option value="T3">업무 상태변경</option>
                    <option value="T4">업무 진척도변경</option>
                    <option value="T5">업무 소요시간변경</option>
                  </select>
                </div>

                <div class="filter-item">
                  <label>대상유형</label>
                  <select v-model="activityStore.selectedTargetType">
                    <option value="">전체</option>
                    <option value="projects">프로젝트</option>
                    <option value="tasks">업무</option>
                    <option value="milestones">마일스톤</option>
                    <option value="members">구성원</option>
                    <option value="memos">메모</option>
                  </select>
                </div>

                <div class="filter-item">
                  <label>정렬</label>
                  <select v-model="activityStore.sortOrder">
                    <option value="DESC">최신순</option>
                    <option value="ASC">오래된순</option>
                  </select>
                </div>
              </div>

              <div class="period-section">
                <div class="period-label">기간</div>

                <div class="period-chip-row">
                  <button
                    v-for="item in activityStore.quickRangeOptions"
                    :key="item.value"
                    class="period-chip"
                    :class="{
                      active: activityStore.selectedQuickRange === item.value,
                    }"
                    @click="handleQuickRange(item.value)"
                  >
                    {{ item.label }}
                  </button>
                </div>

                <div
                  v-if="activityStore.selectedQuickRange === 'CUSTOM'"
                  class="custom-date-row"
                >
                  <div class="date-input-wrap">
                    <label>시작일</label>
                    <input v-model="activityStore.startDate" type="date" />
                  </div>

                  <div class="date-tilde">~</div>

                  <div class="date-input-wrap">
                    <label>종료일</label>
                    <input v-model="activityStore.endDate" type="date" />
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- 작업내역 카드 -->
          <section class="panel activity-log-card">
            <div class="panel-head">
              <div class="content-top">
                <div class="panel-title-wrap">
                  <h3 class="panel-title">작업내역 목록</h3>
                  <div class="result-count">
                    총 <strong>{{ activityStore.filteredLogs.length }}</strong
                    >건
                  </div>
                </div>
              </div>
            </div>

            <div class="panel-body log-card-body">
              <div
                v-if="activityStore.groupedLogs.length > 0"
                class="date-group-wrap"
              >
                <div
                  v-for="group in activityStore.groupedLogs"
                  :key="group.date"
                  class="date-group"
                >
                  <div
                    class="date-group-header"
                    @click="toggleDateGroup(group.date)"
                  >
                    <div class="date-title-wrap">
                      <span class="fold-icon">
                        {{ isGroupOpen(group.date) ? "▾" : "▸" }}
                      </span>
                      <span class="date-title">
                        {{ activityStore.formatDateTitle(group.date) }}
                      </span>
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
                        <div class="log-time">
                          {{ activityStore.formatTime(log.createdAt) }}
                        </div>
                      </div>

                      <div class="log-main-col">
                        <div class="log-top-line">
                          <div class="log-user-row">
                            <span class="user-name">{{ log.userName }}</span>
                            <span class="dot">•</span>
                            <span class="target-type">
                              {{
                                activityStore.getTargetTypeLabel(log.targetType)
                              }}
                            </span>
                          </div>

                          <span
                            class="type-badge"
                            :class="activityStore.badgeClass(log.activityType)"
                          >
                            {{
                              activityStore.getActivityTypeLabel(
                                log.activityType,
                              )
                            }}
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
            </div>
          </section>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from "vue";
import { useRoute } from "vue-router";
import api from "../utils/api";
import { useActivityLogsStore } from "../stores/activityLogs";

import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";

const route = useRoute();
const sidebarOpen = ref(false);

const activityStore = useActivityLogsStore();

const projectInfo = ref({
  projectId: null,
  projectName: "",
  startDate: "",
  endDate: "",
});

const openDateGroups = ref({});

watch(
  () => activityStore.groupedLogs,
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
  { immediate: true, deep: true },
);

function isGroupOpen(date) {
  return openDateGroups.value[date];
}

function toggleDateGroup(date) {
  openDateGroups.value[date] = !openDateGroups.value[date];
}

const fetchProjectDetail = async () => {
  try {
    const projectId = route.params.projectId;
    if (!projectId) return;

    const res = await api.get(`/ProjectDetail/${projectId}`);
    projectInfo.value = res.data;
  } catch (err) {
    console.error("프로젝트 상세 조회 실패:", err);
  }
};

const fetchLogs = async () => {
  const projectId = route.params.projectId;
  if (!projectId) return;
  await activityStore.fetchActivityLogs(projectId);
};

const handleQuickRange = async (value) => {
  activityStore.applyQuickRange(value);
  await fetchLogs();
};

onMounted(async () => {
  await fetchProjectDetail();
  await fetchLogs();
});

watch(
  [
    () => activityStore.searchKeyword,
    () => activityStore.selectedActivityType,
    () => activityStore.selectedTargetType,
    () => activityStore.sortOrder,
    () => activityStore.startDate,
    () => activityStore.endDate,
  ],
  async () => {
    await fetchLogs();
  },
);

watch(
  () => route.params.projectId,
  async (newProjectId) => {
    if (!newProjectId) return;
    await fetchProjectDetail();
    await fetchLogs();
  },
);
</script>

<style scoped>
.dashboard-page {
  font-family: "Pretendard", sans-serif;
  background-color: #f3f4f6;
}

.sub-header {
  background: #fff;
  padding: 12px 24px;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 10;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.bc-home {
  color: #9ca3af;
}

.bc-sep {
  color: #d1d5db;
}

.bc-cur {
  color: #111827;
  font-weight: 600;
}

.page-container {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-title {
  font-size: 28px;
  line-height: 1.2;
  font-weight: 700;
  color: #111827;
}

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
  gap: 8px;
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
  color: #111827;
}

.activity-project-period {
  font-size: 13px;
  color: #6b7280;
}

.panel {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.panel-head {
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #f3f4f6;
  background: #fff;
}

.panel-body {
  background: #fff;
}

.panel-title {
  font-size: 15px;
  font-weight: 700;
  color: #111827;
}

.panel-title-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-card-body {
  padding: 20px;
}

.log-card-body {
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
  border: 1px solid #dbe3db;
  border-radius: 10px;
  padding: 0 14px;
  font-size: 14px;
  color: #111827;
  background: #fff;
  outline: none;
  box-sizing: border-box;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

.filter-item input:focus,
.filter-item select:focus,
.date-input-wrap input:focus {
  border-color: #93c5fd;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.12);
}

.period-section {
  border-top: 1px solid #f3f4f6;
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
  border: 1px solid #d1d5db;
  background: #ffffff;
  color: #4b5563;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
}

.period-chip:hover {
  background: #f9fafb;
  border-color: #cbd5e1;
}

.period-chip.active {
  background: #eff6ff;
  border-color: #bfdbfe;
  color: #1d4ed8;
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
  color: #6b7280;
  font-weight: 700;
}

.content-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.result-count {
  font-size: 14px;
  color: #6b7280;
}

.result-count strong {
  color: #111827;
  font-weight: 800;
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
  padding: 4px 2px;
  cursor: pointer;
}

.date-title-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.fold-icon {
  font-size: 16px;
  color: #6b7280;
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
  background: #f3f4f6;
  color: #4b5563;
  font-size: 12px;
  font-weight: 700;
}

.toggle-btn {
  border: none;
  background: transparent;
  color: #6b7280;
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
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #ffffff;
  padding: 18px 20px;
  display: flex;
  gap: 18px;
  transition: all 0.2s ease;
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
  background: #f3f4f6;
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
  color: #6b7280;
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
  background: #eff6ff;
  color: #1e40af;
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
  font-size: 15px;
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
  background: #eff6ff;
  color: #1e40af;
  font-size: 12px;
  font-weight: 700;
}

.meta-chip.light {
  background: #f3f4f6;
  color: #4b5563;
}

.empty-box {
  border: 1px dashed #d1d5db;
  border-radius: 12px;
  padding: 60px 20px;
  text-align: center;
  color: #6b7280;
  font-size: 15px;
  background: #f9fafb;
}

@media (max-width: 768px) {
  .sub-header {
    padding: 12px 16px;
  }

  .page-container {
    padding: 16px;
    gap: 16px;
  }

  .page-title {
    font-size: 24px;
  }

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

  .panel-head {
    padding: 14px 16px;
  }

  .filter-card-body,
  .log-card-body {
    padding: 16px;
  }

  .log-card {
    flex-direction: column;
    gap: 12px;
    padding: 16px;
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
