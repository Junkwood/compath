<template>
  <div class="dashboard-page flex h-screen overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden bg-gray-50"
    >
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <!-- 서브헤더 breadcrumb -->
        <div class="sub-header">
          <div class="breadcrumb">
            <span class="bc-home">홈</span>
            <span class="bc-sep">/</span>
            <span class="bc-home">프로젝트</span>
            <span class="bc-sep">/</span>
            <span class="bc-cur">하위 프로젝트 대시보드</span>
          </div>
        </div>

        <div class="page-container">
          <!-- ── 페이지 헤더 카드 ── -->
          <div class="page-title-card">
            <div class="title-section">
              <div class="title-tags">
                <span class="meta-chip">최상위</span>
                <span class="meta-project-name">{{
                  projectInfo.projectName
                }}</span>
                <span class="bc-sep" style="color: #d1d5db">/</span>
                <span class="meta-chip sub">하위</span>
                <span class="meta-project-name sub">{{
                  subProjectInfo.projectName
                }}</span>
                <span
                  v-if="projectInfo.useMilestone === 'O1'"
                  class="milestone-badge on"
                  >마일스톤 사용</span
                >
                <span
                  v-else-if="projectInfo.useMilestone === 'O2'"
                  class="milestone-badge off"
                  >마일스톤 미사용</span
                >
              </div>
              <h1 class="page-title">하위 프로젝트 대시보드</h1>
              <p class="page-subtitle">
                {{ subProjectInfo.startDate }} – {{ subProjectInfo.endDate }}
              </p>
            </div>

            <!-- 버튼 그룹 -->
            <div class="action-group">
              <el-button class="action-btn btn-neutral" @click="handleGoBack">
                돌아가기
              </el-button>
              <div class="action-divider"></div>
              <el-button
                class="action-btn btn-primary"
                @click="handleCreateTask"
              >
                + 업무 생성
              </el-button>
              <el-button
                class="action-btn btn-secondary"
                @click="handleSubProjectSetting"
              >
                하위프로젝트 수정
              </el-button>
              <el-button class="action-btn btn-danger" @click="handleDelete">
                삭제
              </el-button>
            </div>
          </div>

          <!-- ── 요약 stat 카드 행 ── -->
          <div class="stat-row">
            <div class="stat-card">
              <div class="stat-icon manager-icon">
                {{ subProjectInfo.managerName?.charAt(0) }}
              </div>
              <div class="stat-info">
                <div class="stat-label">담당 관리자</div>
                <div class="stat-value">
                  {{ subProjectInfo.managerName || "-" }}
                </div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon period-icon">
                <svg
                  width="20"
                  height="20"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <rect x="3" y="4" width="18" height="18" rx="2" />
                  <line x1="16" y1="2" x2="16" y2="6" />
                  <line x1="8" y1="2" x2="8" y2="6" />
                  <line x1="3" y1="10" x2="21" y2="10" />
                </svg>
              </div>
              <div class="stat-info">
                <div class="stat-label">프로젝트 기간</div>
                <div class="stat-value">
                  {{ subProjectInfo.startDate }} – {{ subProjectInfo.endDate }}
                </div>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon milestone-icon">
                <svg
                  width="20"
                  height="20"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <path d="M3 12h18M12 3v18" />
                  <circle cx="12" cy="12" r="3" />
                </svg>
              </div>
              <div class="stat-info">
                <div class="stat-label">마일스톤</div>
                <div class="stat-value">{{ milestoneStatusText }}</div>
              </div>
            </div>

            <div class="stat-card stat-card-total">
              <div class="stat-icon total-icon">
                <svg
                  width="20"
                  height="20"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <path d="M9 11l3 3L22 4" />
                  <path
                    d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"
                  />
                </svg>
              </div>
              <div class="stat-info">
                <div class="stat-label">전체 업무</div>
                <div class="stat-value accent">{{ taskList.length }}</div>
              </div>
            </div>
          </div>

          <!-- ── 본문: 업무 현황 + 업무 목록 ── -->
          <div class="content-col">
            <!-- 업무 현황 테이블 -->
            <div class="panel">
              <div class="panel-head">
                <div>
                  <h2 class="panel-title">업무 현황</h2>
                  <p class="panel-subtext">
                    {{ subProjectInfo.projectName }} 하위 프로젝트의 전체 현황
                  </p>
                </div>
              </div>
              <div class="panel-body">
                <el-table
                  :data="taskSummaryData"
                  style="width: 100%"
                  :header-cell-style="tableHeaderStyle"
                  :cell-style="tableCellStyle"
                >
                  <el-table-column prop="type" label="유형" min-width="110" />
                  <el-table-column
                    prop="total"
                    label="전체"
                    min-width="80"
                    align="center"
                  />
                  <el-table-column
                    prop="inProgress"
                    label="진행중"
                    min-width="90"
                    align="center"
                  >
                    <template #default="{ row }">
                      <span class="num-hi">{{ row.inProgress }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    prop="done"
                    label="완료"
                    min-width="80"
                    align="center"
                  >
                    <template #default="{ row }">
                      <span class="num-done">{{ row.done }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    prop="rejected"
                    label="반려"
                    min-width="80"
                    align="center"
                  >
                    <template #default="{ row }">
                      <span :class="{ 'num-reject': row.rejected > 0 }">{{
                        row.rejected
                      }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    prop="totalSum"
                    label="합계"
                    min-width="80"
                    align="center"
                  >
                    <template #default="{ row }">
                      <span class="num-total">{{ row.totalSum }}</span>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>

            <!-- 업무 목록 -->
            <div class="panel">
              <div class="panel-head">
                <div class="flex items-center gap-2">
                  <h2 class="panel-title">업무 목록</h2>
                  <span class="count-tag">{{ taskList.length }}</span>
                </div>
                <span class="panel-sub-mini"
                  >[{{ subProjectInfo.projectName }}]</span
                >
              </div>
              <div class="panel-body">
                <el-table
                  :data="taskList"
                  style="width: 100%"
                  :cell-style="taskListCellStyle"
                  :header-cell-style="taskListHeaderStyle"
                  row-class-name="clickable-row"
                  @row-click="handleTaskRowClick"
                >
                  <el-table-column
                    prop="title"
                    label="업무명"
                    min-width="260"
                  />
                  <el-table-column
                    prop="userName"
                    label="담당자 이름"
                    width="180"
                    align="center"
                    header-align="center"
                  >
                    <template #default="{ row }">
                      <span class="task-pl">{{ row.userName || "-" }}</span>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>

  <ProjectSubCreateModal
    v-model="subProjectModalOpen"
    :projectId="rootProjectId"
    :parentProjectName="projectInfo.projectName"
    :parentStartDate="projectInfo.startDate"
    :parentEndDate="projectInfo.endDate"
    :parentUseMilestone="projectInfo.useMilestone"
    :isEditMode="true"
    :editData="editData"
    @submitted="handleSubProjectUpdated"
  />
</template>

<script setup>
import { onMounted, ref, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";
import api from "../utils/api";
import Swal from "sweetalert2";

import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import ProjectSubCreateModal from "../project/ProjectSubCreateModal.vue";

const route = useRoute();
const router = useRouter();
const sidebarOpen = ref(false);
const authStore = useAuthStore();

const subProjectId = Number(route.params.subProjectId);
const rootProjectId = Number(route.params.projectId);

const subProjectModalOpen = ref(false);
const editData = ref(null);

const handleSubProjectUpdated = async () => {
  await fetchSubInfo();
  subProjectModalOpen.value = false;
};

const projectInfo = ref({
  projectId: null,
  projectName: "",
  startDate: "",
  endDate: "",
  useMilestone: "",
});

const fetchProjectDetail = async () => {
  try {
    const res = await api.get(`/ProjectDetail/${rootProjectId}`);
    projectInfo.value = {
      projectId: res.data?.projectId ?? null,
      projectName: res.data?.projectName ?? "",
      startDate: res.data?.startDate ?? "",
      endDate: res.data?.endDate ?? "",
      useMilestone: res.data?.useMilestone ?? "",
    };
  } catch (err) {
    console.error("프로젝트 상세 조회 실패:", err);
  }
};

const subProjectInfo = ref({
  projectId: null,
  projectName: "",
  startDate: "",
  endDate: "",
  managerName: "",
});

const fetchSubInfo = async () => {
  try {
    const res = await api.get(`/ProjectDetail/${subProjectId}`);
    subProjectInfo.value = res.data;
  } catch (err) {
    console.error("하위프로젝트 상세 조회 실패:", err);
  }
};

const milestoneStatusText = computed(() => {
  if (projectInfo.value.useMilestone === "O1") return "사용";
  if (projectInfo.value.useMilestone === "O2") return "사용안함";
  return "-";
});

const handleGoBack = () => {
  if (window.history.length > 1) {
    router.back();
  } else {
    router.push(`/project/dashboard/${rootProjectId}`);
  }
};

const taskSummaryData = ref([]);

const fetchTaskSummary = async () => {
  try {
    const res = await api.get(`/TaskSummary/${subProjectId}`);
    taskSummaryData.value = res.data || [];
  } catch (err) {
    console.error("하위프로젝트 업무 현황 조회 실패:", err);
    taskSummaryData.value = [];
  }
};

const taskList = ref([]);

const fetchTaskList = async () => {
  try {
    const res = await api.get(`/SubProjectTaskList/${subProjectId}`);
    taskList.value = res.data || [];
  } catch (err) {
    console.error("하위프로젝트 업무목록 조회 실패:", err);
    taskList.value = [];
  }
};

const handleTaskRowClick = (row) => {
  router.push({
    name: "taskDetail",
    params: {
      subProjectId: subProjectId,
      projectId: rootProjectId,
      taskId: row.taskId,
    },
  });
};

const handleCreateTask = () => {
  router.push({
    name: "taskRegister",
    params: { projectId: rootProjectId },
    query: {
      from: "dashboard",
      subProjectId: subProjectId,
    },
  });
};

const handleSubProjectSetting = async () => {
  try {
    const res = await api.get(`/ProjectSubDetail/${subProjectId}`);
    editData.value = res.data;
    subProjectModalOpen.value = true;
  } catch (err) {
    console.error("하위프로젝트 수정용 상세 조회 실패:", err);
  }
};

const handleDelete = async () => {
  try {
    const result = await Swal.fire({
      title: "하위프로젝트를 삭제하시겠습니까?",
      text: "업무 등록이 되지 않은 프로젝트만 삭제 가능합니다.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "삭제",
      cancelButtonText: "취소",
    });

    if (!result.isConfirmed) return;

    await api.put(`/ProjectSubDelete/${subProjectId}`, {
      userId: authStore.user?.userId,
    });

    await Swal.fire({
      icon: "success",
      title: "삭제되었습니다.",
      confirmButtonText: "확인",
    });

    router.push(`/project/dashboard/${rootProjectId}`);
  } catch (err) {
    console.error("하위프로젝트 삭제 실패:", err);
    Swal.fire({
      icon: "error",
      title: "삭제 실패",
      text: "하위프로젝트 삭제 중 오류가 발생했습니다.",
      confirmButtonText: "확인",
    });
  }
};

/* ─── 테이블 스타일 ─── */
const tableHeaderStyle = () => ({
  background: "#f9fafb",
  color: "#6b7280",
  fontSize: "12px",
  fontWeight: "600",
  letterSpacing: "0.02em",
  height: "42px",
  borderBottom: "1px solid #e5e7eb",
});

const tableCellStyle = () => ({
  color: "#374151",
  fontSize: "13.5px",
  padding: "12px 0",
  borderBottom: "1px solid #f3f4f6",
});

const taskListCellStyle = ({ columnIndex }) => ({
  color: "#374151",
  fontSize: "13.5px",
  padding: "13px 16px",
  borderBottom: "1px solid #f3f4f6",
  textAlign: columnIndex === 1 ? "center" : "left",
});

const taskListHeaderStyle = ({ columnIndex }) => ({
  background: "#f9fafb",
  color: "#6b7280",
  fontSize: "12px",
  fontWeight: "600",
  letterSpacing: "0.02em",
  height: "42px",
  textAlign: columnIndex === 1 ? "center" : "left",
  paddingLeft: columnIndex === 0 ? "24px" : "12px",
  borderBottom: "1px solid #e5e7eb",
});

onMounted(() => {
  fetchProjectDetail();
  fetchSubInfo();
  fetchTaskList();
  fetchTaskSummary();
});
</script>

<style scoped>
.dashboard-page {
  font-family: "Pretendard", sans-serif;
  background-color: #f3f4f6;
}

/* ── 서브헤더 ── */
.sub-header {
  background: #fff;
  padding: 11px 28px;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 10;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 12.5px;
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

/* ── 페이지 컨테이너 ── */
.page-container {
  padding: 24px 28px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 1280px;
  margin: 0 auto;
  width: 100%;
}

/* ── 헤더 카드 ── */
.page-title-card {
  background: #fff;
  border-radius: 14px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  padding: 22px 28px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.title-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
}

.title-tags {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.meta-chip {
  display: inline-flex;
  align-items: center;
  height: 22px;
  padding: 0 9px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.meta-chip.sub {
  background: #f3f4f6;
  color: #4b5563;
}

.meta-project-name {
  font-size: 14px;
  font-weight: 700;
  color: #374151;
}

.meta-project-name.sub {
  color: #111827;
}

.page-title {
  font-size: 26px;
  font-weight: 800;
  color: #0f172a;
  line-height: 1.2;
  letter-spacing: -0.02em;
}

.page-subtitle {
  font-size: 13px;
  color: #9ca3af;
  font-weight: 500;
}

.milestone-badge {
  display: inline-flex;
  align-items: center;
  height: 22px;
  padding: 0 9px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
}

.milestone-badge.on {
  background: #ecfdf5;
  color: #059669;
}
.milestone-badge.off {
  background: #f3f4f6;
  color: #9ca3af;
}

/* ── 버튼 그룹 ── */
.action-group {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.action-divider {
  width: 1px;
  height: 28px;
  background: #e5e7eb;
  margin: 0 2px;
}

.action-btn {
  height: 38px !important;
  padding: 0 16px !important;
  border-radius: 9px !important;
  font-size: 13px !important;
  font-weight: 600 !important;
  transition: all 0.15s ease !important;
  letter-spacing: -0.01em !important;
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1) !important;
}

.btn-primary {
  background: #1b5c9c !important;
  border: 1px solid #1b5c9c !important;
  color: #fff !important;
}
.btn-primary:hover {
  background: #174d83 !important;
  border-color: #174d83 !important;
}

.btn-secondary {
  background: #f8fafc !important;
  border: 1px solid #dbe3ec !important;
  color: #334155 !important;
}
.btn-secondary:hover {
  background: #f1f5f9 !important;
}

.btn-neutral {
  background: #fff !important;
  border: 1px solid #d1d5db !important;
  color: #374151 !important;
}
.btn-neutral:hover {
  background: #f9fafb !important;
}

.btn-danger {
  background: #fff5f5 !important;
  border: 1px solid #fecaca !important;
  color: #dc2626 !important;
}
.btn-danger:hover {
  background: #fef2f2 !important;
}

/* ── stat 카드 행 ── */
.stat-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  padding: 18px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: box-shadow 0.15s;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.07);
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 17px;
  font-weight: 800;
}

.manager-icon {
  background: #dbeafe;
  color: #1d4ed8;
}
.period-icon {
  background: #f0fdf4;
  color: #16a34a;
}
.milestone-icon {
  background: #fef9c3;
  color: #a16207;
}
.total-icon {
  background: #eff6ff;
  color: #1b5c9c;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

.stat-label {
  font-size: 11.5px;
  font-weight: 600;
  color: #9ca3af;
  letter-spacing: 0.03em;
  text-transform: uppercase;
}

.stat-value {
  font-size: 14px;
  font-weight: 700;
  color: #111827;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stat-value.accent {
  font-size: 22px;
  color: #1b5c9c;
}

/* ── 콘텐츠 컬럼 ── */
.content-col {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* ── 패널 ── */
.panel {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.panel-head {
  padding: 16px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  border-bottom: 1px solid #f3f4f6;
}

.panel-title {
  font-size: 15px;
  font-weight: 700;
  color: #111827;
}

.panel-subtext {
  margin-top: 3px;
  font-size: 12.5px;
  color: #9ca3af;
}

.panel-sub-mini {
  font-size: 12px;
  color: #9ca3af;
  font-weight: 600;
}

.panel-body {
  padding: 0;
}

/* count tag */
.count-tag {
  min-width: 22px;
  height: 20px;
  padding: 0 7px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 11.5px;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

/* ── 숫자 강조 ── */
.num-hi {
  color: #1b5c9c;
  font-weight: 700;
}
.num-done {
  color: #059669;
  font-weight: 600;
}
.num-reject {
  color: #dc2626;
  font-weight: 700;
}
.num-total {
  color: #374151;
  font-weight: 700;
}

.task-pl {
  font-size: 12.5px;
  font-weight: 600;
  color: #64748b;
}

/* ── 테이블 공통 ── */
:deep(.el-table) {
  --el-table-header-bg-color: #f9fafb;
  border-radius: 0 !important;
}

:deep(.el-table__inner-wrapper::before),
:deep(.el-table::before) {
  display: none;
}

:deep(.el-table td.el-table__cell),
:deep(.el-table th.el-table__cell) {
  border-bottom: 1px solid #f3f4f6 !important;
}

:deep(.el-table .cell) {
  padding-left: 14px !important;
  padding-right: 14px !important;
}

:deep(.el-table__row:hover > td) {
  background: #f8fbff !important;
}

:deep(.clickable-row) {
  cursor: pointer;
}

/* ── 반응형 ── */
@media (max-width: 1100px) {
  .stat-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .page-container {
    padding: 16px;
  }

  .page-title-card {
    flex-direction: column;
    align-items: stretch;
  }

  .action-group {
    flex-wrap: wrap;
    width: 100%;
  }

  .action-btn {
    flex: 1;
    min-width: 80px;
  }

  .action-divider {
    display: none;
  }

  .page-title {
    font-size: 22px;
  }

  .stat-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .stat-row {
    grid-template-columns: 1fr;
  }
}
</style>
