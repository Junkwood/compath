<!-- projectSubDashboard -->
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
          <!-- 상단 프로젝트 헤더 -->
          <div class="page-title-card">
            <div class="title-left">
              <h1 class="page-title">하위 프로젝트 대시보드</h1>

              <div class="title-meta-group">
                <div class="title-meta-row">
                  <span class="meta-chip">최상위 프로젝트</span>
                  <span class="meta-project">
                    {{ projectInfo.projectName }}
                  </span>
                  <span class="meta-period">
                    {{ projectInfo.startDate }} - {{ projectInfo.endDate }}
                  </span>
                </div>

                <div class="title-meta-row">
                  <span class="meta-chip sub">하위 프로젝트</span>
                  <span class="meta-project strong">
                    {{ subProjectInfo.projectName }}
                  </span>
                  <span class="meta-period">
                    {{ subProjectInfo.startDate }} -
                    {{ subProjectInfo.endDate }}
                  </span>
                </div>
              </div>
            </div>

            <div class="title-actions">
              <el-button class="action-btn btn-neutral" @click="handleGoBack">
                돌아가기
              </el-button>
              <el-button
                class="action-btn btn-primary"
                @click="handleCreateTask"
              >
                업무 생성
              </el-button>
              <el-button
                class="action-btn btn-secondary"
                @click="handleSubProjectSetting"
              >
                하위프로젝트 수정
              </el-button>
              <el-button class="action-btn btn-danger" @click="handleDelete">
                프로젝트 삭제
              </el-button>
            </div>
          </div>

          <!-- 본문 -->
          <div class="content-grid">
            <!-- 좌측 메인 -->
            <div class="left-col">
              <!-- 업무 현황 -->
              <div class="panel">
                <div class="panel-head">
                  <div>
                    <h2 class="panel-title">업무 현황</h2>
                    <div class="panel-subtext">
                      {{ subProjectInfo.projectName }} 하위 프로젝트의 전체 현황
                    </div>
                  </div>
                </div>

                <div class="panel-body">
                  <div class="table-card">
                    <el-table
                      :data="taskSummaryData"
                      style="width: 100%"
                      :header-cell-style="tableHeaderStyle"
                      :cell-style="tableCellStyle"
                    >
                      <el-table-column
                        prop="type"
                        label="유형"
                        min-width="90"
                      />
                      <el-table-column
                        prop="total"
                        label="전체"
                        min-width="72"
                        align="center"
                      />
                      <el-table-column
                        prop="inProgress"
                        label="진행중"
                        min-width="80"
                        align="center"
                      >
                        <template #default="{ row }">
                          <span class="num-hi">{{ row.inProgress }}</span>
                        </template>
                      </el-table-column>
                      <el-table-column
                        prop="done"
                        label="완료"
                        min-width="72"
                        align="center"
                      />
                      <el-table-column
                        prop="rejected"
                        label="반려"
                        min-width="72"
                        align="center"
                      >
                        <template #default="{ row }">
                          <span
                            :class="{
                              'text-red-500 font-bold': row.rejected > 0,
                            }"
                          >
                            {{ row.rejected }}
                          </span>
                        </template>
                      </el-table-column>
                      <el-table-column
                        prop="totalSum"
                        label="합계"
                        min-width="72"
                        align="center"
                      />
                    </el-table>
                  </div>
                </div>
              </div>

              <!-- 업무 목록 -->
              <div class="panel">
                <div class="panel-head">
                  <div class="flex items-center gap-2">
                    <h2 class="panel-title">업무 목록</h2>
                    <span class="count-tag">{{ taskList.length }}</span>
                  </div>
                  <div class="panel-sub-mini">
                    [{{ subProjectInfo.projectName }}]
                  </div>
                </div>

                <div class="panel-body task-list-panel-body">
                  <div class="table-card task-table-card">
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

            <!-- 우측 요약 -->
            <div class="right-col">
              <div class="panel summary-panel">
                <div class="panel-head">
                  <h2 class="panel-title">프로젝트 요약</h2>
                </div>

                <div class="summary-body">
                  <div class="summary-card manager-summary">
                    <div class="summary-label">하위프로젝트 관리자</div>

                    <div class="manager-wrap">
                      <div class="manager-avatar">
                        {{ subProjectInfo.managerName?.charAt(0) }}
                      </div>

                      <div class="manager-info">
                        <div class="manager-name">
                          {{ subProjectInfo.managerName || "-" }}
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="summary-card period-summary">
                    <div class="summary-label">프로젝트 기간</div>
                    <div class="period-text">
                      {{ subProjectInfo.startDate }} -
                      {{ subProjectInfo.endDate }}
                    </div>
                  </div>

                  <div class="summary-card graph-summary">
                    <div class="summary-label">업무 상태 그래프</div>

                    <div class="graph-placeholder" v-if="graphData.length > 0">
                      <div class="graph-bars">
                        <div
                          v-for="item in graphData"
                          :key="item.label"
                          class="graph-item"
                        >
                          <div class="graph-bar-wrap">
                            <div
                              class="graph-bar"
                              :style="{
                                height:
                                  item.value > 0 ? `${item.value}%` : '0%',
                              }"
                            ></div>
                          </div>
                          <div class="graph-label">{{ item.label }}</div>
                          <div class="graph-value">{{ item.raw }}</div>
                        </div>
                      </div>
                    </div>

                    <div v-else class="graph-empty">
                      표시할 업무 데이터가 없습니다.
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- 우측 끝 -->
          </div>
        </div>
      </main>
    </div>
  </div>

  <ProjectSubCreateModal
    v-model="subProjectModalOpen"
    :projectId="rootProjectId"
    :parentProjectName="projectInfo.projectName"
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

// 하위프로젝트 수정 모달
const subProjectModalOpen = ref(false);
const editData = ref(null);

// 하위프로젝트 수정 후 갱신
const handleSubProjectUpdated = async () => {
  await fetchSubInfo();
  subProjectModalOpen.value = false;
};

// 상단 제목용
const projectInfo = ref({
  projectId: null,
  projectName: "",
  startDate: "",
  endDate: "",
});

const fetchProjectDetail = async () => {
  try {
    const res = await api.get(`/ProjectDetail/${rootProjectId}`);
    projectInfo.value = res.data;
  } catch (err) {
    console.error("프로젝트 상세 조회 실패:", err);
  }
};

// 하위프로젝트 정보
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

// 뒤로가기
const handleGoBack = () => {
  if (window.history.length > 1) {
    router.back();
  } else {
    router.push(`/project/dashboard/${rootProjectId}`);
  }
};

// 업무 현황
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

// 업무목록
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

// 업무 클릭 시 상세 이동
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

// 그래프 데이터
const graphData = computed(() => {
  const rows = Array.isArray(taskSummaryData.value)
    ? taskSummaryData.value
    : [];

  if (rows.length === 0) return [];

  const normalized = rows.map((item) => {
    const raw = Number(item.totalSum ?? 0);
    return {
      label: item.type || "-",
      raw,
    };
  });

  const maxValue = Math.max(...normalized.map((item) => item.raw), 0);

  return normalized.map((item) => ({
    ...item,
    value: maxValue > 0 ? Math.round((item.raw / maxValue) * 100) : 0,
  }));
});

// 업무생성
const handleCreateTask = () => {
  router.push({
    name: "taskRegister",
    params: { projectId: subProjectId },
    query: { from: "dashboard" },
  });
};

// 하위프로젝트 수정
const handleSubProjectSetting = async () => {
  try {
    const res = await api.get(`/ProjectSubDetail/${subProjectId}`);
    editData.value = res.data;
    subProjectModalOpen.value = true;
  } catch (err) {
    console.error("하위프로젝트 수정용 상세 조회 실패:", err);
  }
};

// 하위프로젝트 삭제
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

const tableHeaderStyle = () => ({
  background: "#f9fafb",
  color: "#4b5563",
  fontSize: "12px",
  fontWeight: "600",
  height: "44px",
});

const tableCellStyle = () => ({
  color: "#1f2937",
  fontSize: "13px",
  padding: "10px 0",
});

const taskListCellStyle = ({ columnIndex }) => {
  if (columnIndex === 1) {
    return {
      color: "#1f2937",
      fontSize: "13px",
      padding: "12px 10px",
      textAlign: "center",
    };
  }

  return {
    color: "#1f2937",
    fontSize: "13px",
    padding: "12px 16px",
  };
};

const taskListHeaderStyle = ({ columnIndex }) => {
  if (columnIndex === 0) {
    return {
      background: "#f9fafb",
      color: "#4b5563",
      fontSize: "12px",
      fontWeight: "600",
      height: "44px",
      textAlign: "left",
      paddingLeft: "24px",
    };
  }

  return {
    background: "#f9fafb",
    color: "#4b5563",
    fontSize: "12px",
    fontWeight: "600",
    height: "44px",
    textAlign: "center",
  };
};

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

.page-title-card {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
}

.title-left {
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-width: 0;
}

.page-title {
  font-size: 28px;
  font-weight: 800;
  color: #111827;
  line-height: 1.2;
}

.title-meta-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.title-meta-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.meta-chip {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 12px;
  font-weight: 700;
}

.meta-chip.sub {
  background: #f3f4f6;
  color: #374151;
}

.meta-project {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
}

.meta-project.strong {
  color: #0f172a;
}

.meta-period {
  font-size: 13px;
  color: #6b7280;
}

.title-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: flex-end;
}

.action-btn {
  height: 40px !important;
  padding: 0 16px !important;
  border-radius: 10px !important;
  font-size: 13px !important;
  font-weight: 700 !important;
  transition: all 0.2s ease !important;
}

.action-btn:hover {
  transform: translateY(-1px);
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

.btn-secondary:hover,
.btn-neutral:hover {
  background: #f1f5f9 !important;
}

.btn-neutral {
  background: #fff !important;
  border: 1px solid #d1d5db !important;
  color: #374151 !important;
}

.btn-danger {
  background: #fff5f5 !important;
  border: 1px solid #fecaca !important;
  color: #dc2626 !important;
}

.btn-danger:hover {
  background: #fef2f2 !important;
}

.content-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  gap: 24px;
  align-items: start;
}

.left-col,
.right-col {
  min-width: 0;
}

.left-col {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.right-col {
  position: sticky;
  top: 24px;
  align-self: start;
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
  gap: 12px;
}

.panel-title {
  font-size: 15px;
  font-weight: 700;
  color: #111827;
}

.panel-subtext {
  margin-top: 4px;
  font-size: 13px;
  color: #6b7280;
}

.panel-sub-mini {
  font-size: 12px;
  color: #6b7280;
  font-weight: 600;
}

.panel-body {
  padding: 20px;
  background: #fff;
}

.task-list-panel-body {
  padding-top: 16px;
}

.count-tag {
  min-width: 24px;
  height: 22px;
  padding: 0 8px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 12px;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.table-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
}

.task-table-card {
  min-height: 260px;
}

.summary-panel .panel-head {
  border-bottom: 1px solid #f3f4f6;
}

.summary-body {
  padding: 18px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.summary-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #fff;
  padding: 16px;
}

.summary-label {
  font-size: 13px;
  font-weight: 700;
  color: #374151;
  margin-bottom: 12px;
}

.manager-wrap {
  display: flex;
  align-items: center;
  gap: 14px;
}

.manager-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #dbeafe;
  color: #1d4ed8;
  font-size: 18px;
  font-weight: 800;
}

.manager-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.manager-name {
  font-size: 15px;
  font-weight: 700;
  color: #111827;
}

.period-text {
  font-size: 14px;
  color: #111827;
  font-weight: 700;
  line-height: 1.5;
}

.graph-placeholder {
  min-height: 300px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  width: 100%;
  padding: 20px 12px 12px;
  border: 1px dashed #dbe2ea;
  border-radius: 12px;
  background: linear-gradient(to bottom, #fbfdff, #f8fbff);
}

.graph-empty {
  min-height: 220px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed #dbe2ea;
  border-radius: 12px;
  background: linear-gradient(to bottom, #fbfdff, #f8fbff);
  color: #94a3b8;
  font-size: 13px;
  font-weight: 600;
}

.graph-bars {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(42px, 1fr));
  align-items: end;
  gap: 12px;
  width: 100%;
  height: 100%;
}

.graph-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
  height: 100%;
}

.graph-bar-wrap {
  display: flex;
  align-items: flex-end;
  width: 100%;
  max-width: 38px;
  height: 190px;
  border-radius: 999px;
  background: #e5e7eb;
  overflow: hidden;
}

.graph-bar {
  width: 100%;
  min-height: 0;
  border-radius: 999px;
  background: linear-gradient(180deg, #93c5fd 0%, #60a5fa 55%, #3b82f6 100%);
  box-shadow: 0 4px 10px rgba(59, 130, 246, 0.18);
  transition: height 0.25s ease;
}

.graph-label {
  font-size: 12px;
  font-weight: 700;
  color: #475569;
  text-align: center;
  word-break: keep-all;
}

.graph-value {
  font-size: 11px;
  font-weight: 600;
  color: #94a3b8;
}

.task-pl {
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
}

.num-hi {
  color: #1b5c9c;
  font-weight: 700;
}

:deep(.el-table) {
  --el-table-header-bg-color: #f9fafb;
}

:deep(.el-table__inner-wrapper::before),
:deep(.el-table::before) {
  display: none;
}

:deep(.el-table td.el-table__cell),
:deep(.el-table th.el-table__cell) {
  border-bottom: 1px solid #eef2f7 !important;
}

:deep(.el-table .cell) {
  padding-left: 12px !important;
  padding-right: 12px !important;
}

:deep(.el-table__row:hover > td) {
  background: #f8fbff !important;
}

:deep(.clickable-row) {
  cursor: pointer;
}

@media (max-width: 1200px) {
  .content-grid {
    grid-template-columns: 1fr;
  }

  .right-col {
    position: static;
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

  .title-actions {
    width: 100%;
    justify-content: flex-start;
  }

  .action-btn {
    flex: 1;
  }

  .page-title {
    font-size: 24px;
  }

  .meta-project {
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .title-meta-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }

  .graph-bars {
    gap: 10px;
  }

  .graph-bar-wrap {
    max-width: 32px;
    height: 160px;
  }
}
</style>
