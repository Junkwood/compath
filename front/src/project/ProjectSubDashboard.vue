<template>
  <div class="flex h-screen overflow-hidden">
    <!-- Sidebar -->
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <!-- Content area -->
    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden"
    >
      <!-- Site header -->
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
          <!-- 상단 타이틀 -->
          <div class="mb-6 proj-title-row">
            <div class="proj-title-left">
              <div class="proj-title-left">
                <h2
                  class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
                >
                  하위 프로젝트 대시보드
                </h2>
                <div class="proj-name-row">
                  <span class="proj-name"
                    >최상위 프로젝트 【 {{ projectInfo.projectName }} 】</span
                  >
                  <span class="proj-period"
                    >{{ projectInfo.startDate }} -
                    {{ projectInfo.endDate }}</span
                  >
                </div>
              </div>

              <div class="proj-name-row">
                <span class="proj-name"
                  >하위 프로젝트【{{ subProjectInfo.projectName }}】</span
                >
                <span class="proj-period">
                  {{ subProjectInfo.startDate }} - {{ subProjectInfo.endDate }}
                </span>
              </div>
            </div>

            <div class="proj-title-right">
              <el-button class="back-btn" @click="handleGoBack">
                돌아가기
              </el-button>
              <el-button class="task-create-btn" @click="handleCreateTask">
                업무 생성
              </el-button>
              <el-button class="setting-btn" @click="handleSubProjectSetting">
                ⚙ 하위프로젝트 수정
              </el-button>
            </div>
          </div>

          <!-- 본문 -->
          <div class="sub-dash-layout">
            <!-- 좌측 -->
            <div class="left-panel">
              <div class="card main-card">
                <div class="sub-header-row">
                  <div class="sub-header-left">
                    <div class="sub-title">하위 프로젝트</div>
                    <div class="sub-period-inline">
                      {{ subProjectInfo.startDate }} -
                      {{ subProjectInfo.endDate }}
                    </div>
                  </div>
                </div>

                <div class="sub-name-box">
                  [ {{ subProjectInfo.projectName }} ]
                </div>

                <!-- 업무 현황 테이블 -->
                <div class="section-block">
                  <div class="inner-table-wrap">
                    <el-table
                      :data="taskSummaryData"
                      class="status-table"
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
                      />
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
                      />
                      <el-table-column
                        prop="sum"
                        label="합계"
                        min-width="72"
                        align="center"
                      />
                    </el-table>
                  </div>
                </div>

                <!-- 업무목록 -->
                <div class="section-block task-list-section">
                  <div class="section-title">
                    [{{ subProjectInfo.projectName }}] 의 업무목록 리스트
                  </div>

                  <div class="inner-list-wrap">
                    <el-table
                      :data="taskList"
                      class="task-list-table"
                      style="width: 100%"
                      :show-header="false"
                      :cell-style="taskListCellStyle"
                      @row-click="handleTaskRowClick"
                    >
                      <el-table-column prop="title" min-width="260" />
                      <el-table-column label="" width="120" align="right">
                        <template #default="{ row }">
                          <span class="task-pl">PL {{ row.userName }}</span>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                </div>
              </div>
            </div>

            <!-- 우측 -->
            <div class="right-panel">
              <!-- 관리자 카드 -->
              <div class="card side-card manager-card">
                <div class="side-title">하위프로젝트 관리자</div>

                <div class="manager-item">
                  <div class="manager-avatar">
                    {{ subProjectInfo.managerName?.charAt(0) }}
                  </div>

                  <div class="manager-info">
                    <span class="manager-name">{{
                      subProjectInfo.managerName
                    }}</span>
                    <span class="manager-role">{{
                      subProjectInfo.managerRole
                    }}</span>
                  </div>
                </div>
              </div>

              <!-- 그래프 카드 -->
              <div class="card side-card graph-card">
                <div class="side-title">업무 상태 그래프</div>

                <div class="graph-body">
                  <div class="graph-placeholder">
                    <div class="graph-bars">
                      <div
                        v-for="item in graphData"
                        :key="item.label"
                        class="graph-item"
                      >
                        <div class="graph-bar-wrap">
                          <div
                            class="graph-bar"
                            :style="{ height: `${item.value}%` }"
                          ></div>
                        </div>
                        <div class="graph-label">{{ item.label }}</div>
                        <div class="graph-value">{{ item.raw }}</div>
                      </div>
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
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "../utils/api";
import ProjectSubCreateModal from "../project/ProjectSubCreateModal.vue";

import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";

const route = useRoute();
const router = useRouter();
const sidebarOpen = ref(false);

const subProjectId = route.params.subProjectId;
const rootProjectId = route.params.rootProjectId;

//하위프로젝트 수정 모달
const subProjectModalOpen = ref(false);
const editData = ref(null);

//하위프로젝트 수정 후 모달 닫으면 화면 갱신되게
const handleSubProjectUpdated = async () => {
  await fetchSubInfo();
  subProjectModalOpen.value = false;
};

//상단 제목용 (루뜨프로젝트 이름)
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

//하위프로젝트 정보
const subProjectInfo = ref({
  projectId: null,
  projectName: "",
  startDate: "",
  endDate: "",
});

const fetchSubInfo = async () => {
  try {
    const res = await api.get(`/ProjectDetail/${subProjectId}`);
    subProjectInfo.value = res.data;
  } catch (err) {
    console.error("하위프로젝트 상세 조회 실패:", err);
  }
};

//뒤로돌아가기
const handleGoBack = () => {
  console.log("go back rootProjectId:", rootProjectId);

  router.push(`/project/dashboard/${rootProjectId}`);
};

const taskSummaryData = ref([
  { type: "개발", total: 13, inProgress: 9, done: 0, rejected: 1, sum: 23 },
  { type: "업무", total: 13, inProgress: 9, done: 0, rejected: 1, sum: 23 },
  { type: "업무", total: 18, inProgress: 4, done: 1, rejected: 0, sum: 23 },
  { type: "기타", total: 5, inProgress: 4, done: 1, rejected: 0, sum: 10 },
  { type: "다스트", total: 5, inProgress: 0, done: 0, rejected: 0, sum: 2 },
]);

//하단 업무목록 테이블
const taskList = ref([]);

const fetchTaskList = async () => {
  try {
    const res = await api.get(`/SubProjectTaskList/${subProjectId}`);
    taskList.value = res.data;
  } catch (err) {
    console.error("하위프로젝트 업무목록 조회 실패:", err);
    taskList.value = [];
  }
};

//하단 업무목록 클릭시 상세로 이동
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

const graphData = ref([
  { label: "개발", raw: 23, value: 72 },
  { label: "업무", raw: 23, value: 72 },
  { label: "기타", raw: 10, value: 38 },
  { label: "다스트", raw: 2, value: 12 },
]);

const handleCreateTask = () => {
  router.push({
    name: "taskRegister",
    params: {
      projectId: subProjectId,
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

const tableHeaderStyle = () => ({
  background: "#f3f4f6",
  color: "#111827",
  fontSize: "12px",
  fontWeight: "700",
  padding: "8px 0",
  borderBottom: "1px solid #d1d5db",
});

const tableCellStyle = () => ({
  fontSize: "12px",
  color: "#374151",
  padding: "7px 0",
  height: "36px",
  borderBottom: "1px solid #e5e7eb",
});

const taskListCellStyle = () => ({
  fontSize: "12px",
  color: "#374151",
  padding: "7px 10px",
  height: "38px",
  borderBottom: "1px solid #d1d5db",
});

onMounted(() => {
  fetchProjectDetail();
  fetchSubInfo();
  fetchTaskList();
});
</script>

<style scoped>
/* ────────────────────────────────────────────
   타이틀 영역
──────────────────────────────────────────── */
.proj-title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 12px;
}

.proj-title-left {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.proj-name-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.proj-name {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
  letter-spacing: -0.01em;
}

.proj-period {
  font-size: 13px;
  color: #64748b;
}

.proj-title-right {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.task-create-btn,
.setting-btn {
  height: 36px;
  padding: 0 14px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: #f1f5f9;
  color: #475569;
  font-size: 13px;
  font-weight: 600;
}

.task-create-btn:hover,
.setting-btn:hover {
  background: #e2e8f0;
  border-color: #cbd5e1;
}

/* ────────────────────────────────────────────
   전체 레이아웃
──────────────────────────────────────────── */
.sub-dash-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 20px;
  align-items: start;
  min-width: 0;
}

.left-panel,
.right-panel {
  min-width: 0;
}

.right-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ────────────────────────────────────────────
   카드 공통
──────────────────────────────────────────── */
.card {
  background: #fff;
  border: 1px solid #edf2f7;
  border-radius: 20px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
  overflow: hidden;
}

.main-card {
  padding: 24px;
}

.side-card {
  padding: 20px;
}

.manager-card {
  min-height: 150px;
}

.graph-card {
  min-height: 420px;
}

/* ────────────────────────────────────────────
   좌측 메인 헤더
──────────────────────────────────────────── */
.sub-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.sub-header-left {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.sub-title {
  font-size: 16px;
  font-weight: 700;
  color: #111827;
}

.sub-period-inline {
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
}

.sub-name-box {
  margin-bottom: 22px;
  font-size: 24px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.02em;
}

/* ────────────────────────────────────────────
   섹션 공통
──────────────────────────────────────────── */
.section-block + .section-block {
  margin-top: 28px;
}

.section-title {
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 700;
  color: #334155;
}

.task-list-section {
  padding-bottom: 4px;
}

/* ────────────────────────────────────────────
   내부 박스 공통
──────────────────────────────────────────── */
.inner-table-wrap,
.inner-list-wrap {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  overflow: hidden;
}

.inner-table-wrap {
  max-width: 620px;
}

.inner-list-wrap {
  max-width: 560px;
}

/* ────────────────────────────────────────────
   테이블 공통
──────────────────────────────────────────── */
.status-table :deep(.el-table),
.task-list-table :deep(.el-table) {
  border: none !important;
  font-size: 12px;
}

.status-table :deep(.el-table__inner-wrapper::before),
.status-table :deep(.el-table::before),
.task-list-table :deep(.el-table__inner-wrapper::before),
.task-list-table :deep(.el-table::before) {
  display: none;
}

.status-table :deep(td.el-table__cell),
.task-list-table :deep(td.el-table__cell) {
  border-bottom: 1px solid #eef2f7 !important;
}

.status-table :deep(tr:last-child td.el-table__cell),
.task-list-table :deep(tr:last-child td.el-table__cell) {
  border-bottom: none !important;
}

.status-table :deep(.cell),
.task-list-table :deep(.cell) {
  padding-left: 12px !important;
  padding-right: 12px !important;
  color: #334155;
}

.status-table :deep(th.el-table__cell) {
  background: #f8fafc !important;
  color: #475569;
  font-weight: 700;
  border-bottom: 1px solid #e5e7eb !important;
}

.task-list-table :deep(.el-table__body .el-table__row) {
  cursor: pointer;
}

.task-pl {
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
}

/* ────────────────────────────────────────────
   우측 카드 타이틀
──────────────────────────────────────────── */
.side-title {
  margin-bottom: 18px;
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
}

/* ────────────────────────────────────────────
   관리자 카드
──────────────────────────────────────────── */
.manager-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.manager-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: linear-gradient(180deg, #dbeafe 0%, #bfdbfe 100%);
  color: #1d4ed8;
  font-size: 16px;
  font-weight: 800;
  box-shadow: inset 0 0 0 1px rgba(191, 219, 254, 0.9);
}

.manager-info {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.manager-name {
  font-size: 15px;
  font-weight: 700;
  color: #111827;
}

.manager-role {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 24px;
  padding: 0 10px;
  border: 1px solid #bfdbfe;
  border-radius: 999px;
  background: #dbeafe;
  color: #1d4ed8;
  font-size: 11px;
  font-weight: 700;
}

/* ────────────────────────────────────────────
   그래프 카드
──────────────────────────────────────────── */
.graph-body,
.graph-placeholder {
  min-height: 310px;
}

.graph-placeholder {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  width: 100%;
  height: 100%;
  padding: 22px 16px 16px;
  border: 1px dashed #dbe2ea;
  border-radius: 16px;
  background: linear-gradient(to bottom, #fbfdff, #f8fbff);
}

.graph-bars {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  align-items: end;
  gap: 14px;
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
  max-width: 42px;
  height: 210px;
  border-radius: 999px;
  background: #e5e7eb;
  overflow: hidden;
}

.graph-bar {
  width: 100%;
  min-height: 18px;
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
}

.graph-value {
  font-size: 11px;
  font-weight: 600;
  color: #94a3b8;
}

/* ────────────────────────────────────────────
   Element Plus 공통
──────────────────────────────────────────── */
:deep(.el-table) {
  --el-table-border-color: #eef2f7;
  --el-table-header-bg-color: #f8fafc;
}

:deep(.el-table__row:hover > td) {
  background: #f8fbff !important;
}

/* ────────────────────────────────────────────
   반응형
──────────────────────────────────────────── */
@media (max-width: 1024px) {
  .sub-dash-layout {
    grid-template-columns: 1fr;
  }

  .right-panel {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }

  .inner-table-wrap,
  .inner-list-wrap {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .proj-title-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .proj-title-right {
    width: 100%;
  }

  .task-create-btn,
  .setting-btn {
    flex: 1;
    justify-content: center;
  }

  .proj-name-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .proj-name {
    font-size: 15px;
  }

  .sub-name-box {
    font-size: 20px;
  }

  .right-panel {
    grid-template-columns: 1fr;
  }

  .main-card,
  .side-card {
    padding: 18px;
  }
}

@media (max-width: 480px) {
  .sub-name-box {
    font-size: 18px;
    line-height: 1.4;
  }

  .graph-bars {
    gap: 10px;
  }

  .graph-bar-wrap {
    max-width: 34px;
    height: 170px;
  }
}
</style>
