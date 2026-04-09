<template>
  <div class="dashboard-page flex h-screen overflow-hidden">
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
        <div
          class="page-container px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto"
        >
          <!-- 페이지 타이틀 -->
          <div class="page-title-wrap mb-6">
            <h1 class="page-title text-2xl md:text-3xl font-bold">
              Main Dashboard
            </h1>
          </div>

          <!-- 상단 -->
          <div class="dashboard-top mb-6">
            <!-- 업무 통합 현황 -->
            <div class="card main-col">
              <div class="card-header">
                <span class="card-title">업무 통합 현황</span>
                <label class="toggle-label">
                  <span>내 업무만 보기</span>
                  <el-switch v-model="myTaskOnly" />
                </label>
              </div>

              <div class="table-inner-wrap">
                <el-table
                  :data="pagedTaskData"
                  style="width: 100%"
                  :header-cell-style="headerStyle"
                  :cell-style="cellStyle"
                >
                  <el-table-column
                    prop="no"
                    label="번호"
                    width="70"
                    align="center"
                  />
                  <el-table-column
                    prop="projectName"
                    label="프로젝트"
                    min-width="160"
                  />
                  <el-table-column
                    prop="created"
                    label="신규"
                    width="70"
                    align="center"
                  />
                  <el-table-column
                    prop="inProgress"
                    label="진행"
                    width="70"
                    align="center"
                  />
                  <el-table-column
                    prop="devDone"
                    label="개발완료"
                    width="90"
                    align="center"
                  />
                  <el-table-column
                    prop="rejected"
                    label="반려"
                    width="70"
                    align="center"
                  />
                  <el-table-column
                    prop="done"
                    label="종료"
                    width="70"
                    align="center"
                  />
                  <el-table-column
                    prop="totalSum"
                    label="합계"
                    width="70"
                    align="center"
                  />
                </el-table>
              </div>

              <div class="pagination-wrap">
                <el-pagination
                  v-model:current-page="workPage"
                  :page-size="workPageSize"
                  :total="taskProjects.length"
                  layout="prev, pager, next"
                  background
                />
              </div>

              <div class="new-project-btn-wrap">
                <el-button class="new-project-btn" @click="handleCreateProject">
                  + &nbsp; 새 프로젝트 생성
                </el-button>
              </div>
            </div>

            <!-- 우측 카드 -->
            <div class="side-col">
              <!-- 나의 업무 현황 -->
              <div class="card side-card">
                <div class="card-header">
                  <span class="card-title">나의 업무 현황</span>
                </div>

                <div class="task-body">
                  <div class="total-badge">
                    <span class="total-number">{{ totalTaskCount }}</span>
                  </div>

                  <ul class="dot-list">
                    <li
                      v-for="item in taskStatusList"
                      :key="item.label"
                      class="dot-item"
                    >
                      <div class="dot-left">
                        <span
                          class="dot"
                          :style="{ backgroundColor: item.color }"
                        />
                        <span class="dot-label">{{ item.label }}</span>
                      </div>
                      <span class="dot-count">{{ item.count }}</span>
                    </li>
                  </ul>
                </div>
              </div>

              <!-- 새 소식 -->
              <div class="card side-card">
                <div class="card-header">
                  <span class="card-title">새 소식</span>
                </div>

                <div class="news-body">
                  <ul class="dot-list">
                    <li
                      v-for="item in newsList"
                      :key="item.label"
                      class="dot-item"
                    >
                      <div class="dot-left">
                        <span
                          class="dot"
                          :style="{ backgroundColor: item.color }"
                        />
                        <span class="dot-label">{{ item.label }}</span>
                      </div>
                      <span class="dot-count">{{ item.count }}</span>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>

          <!-- 하단 프로젝트 목록 -->
          <div class="card">
            <div class="card-header">
              <span class="card-title">프로젝트 목록</span>
              <label class="toggle-label">
                <span>내 프로젝트만 보기</span>
                <el-switch v-model="myProjectOnly" />
              </label>
            </div>

            <div class="table-inner-wrap">
              <el-table
                v-loading="loadingProjects"
                :data="pagedProjectData"
                style="width: 100%"
                :header-cell-style="headerStyle"
                :cell-style="cellStyle"
                @row-click="goProjectDashboard"
              >
                <el-table-column
                  prop="projectName"
                  label="프로젝트명"
                  min-width="180"
                />
                <el-table-column label="진척도" min-width="200">
                  <template #default="{ row }">
                    <div class="progress-wrap">
                      <el-progress
                        :percentage="row.progressRate"
                        :stroke-width="8"
                        :show-text="false"
                        color="#3b82f6"
                        style="flex: 1"
                      />
                      <span class="progress-text">{{ row.progressRate }}%</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="endDate"
                  label="마감일"
                  width="130"
                  align="center"
                />
                <el-table-column
                  prop="pmUserId"
                  label="관리자"
                  width="110"
                  align="center"
                />
              </el-table>
            </div>

            <div class="pagination-wrap">
              <el-pagination
                v-model:current-page="projectPage"
                :page-size="projectPageSize"
                :total="projectList.length"
                layout="prev, pager, next"
                background
              />
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>

  <ProjectCreateModal v-model="createProjectModalOpen" />
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import api from "../utils/api";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import ProjectCreateModal from "../project/ProjectCreateModal.vue";

const sidebarOpen = ref(false);

// 모달
const createProjectModalOpen = ref(false);

// 토글
const myTaskOnly = ref(true);
const myProjectOnly = ref(true);

// 페이지네이션
const workPage = ref(1);
const workPageSize = 5;
const projectPage = ref(1);
const projectPageSize = 5;

// 더미 데이터
const myTasks = ref({ inProgress: 5, done: 10, rejected: 3, deadline: 1 });

const taskStatusList = computed(() => [
  { label: "진행중", count: myTasks.value.inProgress, color: "#3b82f6" },
  { label: "완료", count: myTasks.value.done, color: "#22c55e" },
  { label: "반려", count: myTasks.value.rejected, color: "#94a3b8" },
  { label: "기한임박", count: myTasks.value.deadline, color: "#ef4444" },
]);

const totalTaskCount = computed(() =>
  Object.values(myTasks.value).reduce((sum, v) => sum + v, 0),
);

const newsList = [
  { label: "메세지", count: 5, color: "#3b82f6" },
  { label: "새 공지사항", count: 3, color: "#22c55e" },
  { label: "업무 생성", count: 2, color: "#94a3b8" },
];

const router = useRouter();

const taskProjects = ref([]);
const loadingTasks = ref(false);
const taskError = ref("");

const fetchTaskList = async () => {
  loadingTasks.value = true;
  taskError.value = "";

  try {
    const res = await api.get("/TaskListDash");
    console.log(res.data);
    taskProjects.value = res.data;
  } catch (err) {
    console.error("업무 목록 조회 실패:", err);
    projectError.value = "업무 목록 조회 실패";

    if (err.response) {
      console.error("status:", err.response.status);
      console.error("data:", err.response.data);
    } else if (err.request) {
      console.error("요청은 갔는데 응답이 없음");
    } else {
      console.error("axios 설정 오류");
    }
  } finally {
    loadingTasks.value = false;
  }
};

const loadingProjects = ref(false);
const projectError = ref("");
const projectList = ref([]);

const fetchProjectList = async () => {
  loadingProjects.value = true;
  projectError.value = "";

  try {
    const res = await api.get("/ProjectList");
    console.log(res.data);
    projectList.value = res.data;
  } catch (err) {
    console.error("프로젝트 목록 조회 실패:", err);
    projectError.value = "프로젝트 목록 조회 실패";

    if (err.response) {
      console.error("status:", err.response.status);
      console.error("data:", err.response.data);
    } else if (err.request) {
      console.error("요청은 갔는데 응답이 없음");
    } else {
      console.error("axios 설정 오류");
    }
  } finally {
    loadingProjects.value = false;
  }
};

onMounted(() => {
  fetchProjectList();
  fetchTaskList();
});

// 페이징 데이터
const pagedTaskData = computed(() => {
  const s = (workPage.value - 1) * workPageSize;
  return taskProjects.value.slice(s, s + workPageSize).map((item, index) => ({
    ...item,
    no: s + index + 1,
  }));
});

const pagedProjectData = computed(() => {
  const s = (projectPage.value - 1) * projectPageSize;
  return projectList.value.slice(s, s + projectPageSize);
});

// 테이블 공통 스타일
const headerStyle = () => ({
  background: "#f8fafc",
  color: "#475569",
  fontSize: "12px",
  fontWeight: "700",
});

const cellStyle = () => ({
  fontSize: "13px",
  color: "#334155",
});

// 이벤트
const handleCreateProject = () => {
  createProjectModalOpen.value = true;
};

const goProjectDashboard = (row) => {
  router.push({
    name: "projectDash",
    params: { projectId: row.projectId },
  });
};
</script>

<style scoped>
.dashboard-page {
  background: #f8fafc;
}

.page-container {
  color: #0f172a;
}

.page-title {
  color: #0f172a;
  letter-spacing: -0.03em;
}

/* 상단 레이아웃 */
.dashboard-top {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  gap: 20px;
  align-items: start;
}

.side-col {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 카드 공통 */
.card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 18px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.05);
  overflow: hidden;
}

.card-header {
  padding: 18px 22px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  border-bottom: 1px solid #f1f5f9;
}

.card-title {
  font-weight: 700;
  font-size: 15px;
  color: #0f172a;
  letter-spacing: -0.02em;
}

.toggle-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #64748b;
  cursor: pointer;
  white-space: nowrap;
}

/* 테이블 영역 */
.table-inner-wrap {
  padding: 16px 20px 8px;
  background: #ffffff;
}

:deep(.table-inner-wrap .el-table) {
  border: 1px solid #e9eef5;
  border-radius: 14px;
  overflow: hidden;
}

:deep(.el-table th.el-table__cell) {
  background: #f8fafc !important;
}

:deep(.el-table td.el-table__cell),
:deep(.el-table th.el-table__cell) {
  border-bottom: 1px solid #eef2f7 !important;
}

:deep(.el-table__body-wrapper .el-table__row) {
  cursor: pointer;
  transition: all 0.2s ease;
}

:deep(.el-table__row:hover > td) {
  background: #f8fbff !important;
}

:deep(.el-table) {
  --el-table-border-color: #edf2f7;
  --el-table-header-bg-color: #f8fafc;
  --el-table-row-hover-bg-color: #f8fbff;
}

/* 페이지네이션 */
.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 10px 20px 18px;
}

/* 새 프로젝트 버튼 */
.new-project-btn-wrap {
  padding: 0 20px 20px;
}

.new-project-btn {
  width: 100%;
  height: 44px;
  border-radius: 12px;
  border: 1px solid #dbeafe;
  background: linear-gradient(180deg, #eff6ff 0%, #e0edff 100%);
  color: #1d4ed8;
  font-weight: 700;
  font-size: 14px;
  transition: all 0.2s ease;
}

.new-project-btn:hover {
  border-color: #bfdbfe;
  background: linear-gradient(180deg, #dbeafe 0%, #d3e6ff 100%);
  color: #1e40af;
}

/* 사이드 카드 */
.side-card {
  min-height: 190px;
}

.task-body {
  padding: 22px;
  display: flex;
  align-items: center;
  gap: 18px;
  min-height: 134px;
}

.news-body {
  padding: 18px 22px 22px;
  min-height: 134px;
}

.total-badge {
  width: 68px;
  height: 68px;
  border-radius: 999px;
  background: linear-gradient(180deg, #eff6ff 0%, #dbeafe 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 1px solid #dbeafe;
}

.total-number {
  font-size: 24px;
  font-weight: 800;
  color: #1d4ed8;
}

.dot-list {
  list-style: none;
  margin: 0;
  padding: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.dot-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-size: 13px;
}

.dot-left {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
  box-shadow: 0 0 0 4px rgba(148, 163, 184, 0.08);
}

.dot-label {
  color: #475569;
  font-weight: 500;
}

.dot-count {
  font-weight: 700;
  color: #0f172a;
}

/* 진행률 */
.progress-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-text {
  font-size: 12px;
  font-weight: 700;
  color: #2563eb;
  min-width: 38px;
}

:deep(.el-progress-bar__outer) {
  background: #eaf1fb;
  border-radius: 999px;
}

:deep(.el-progress-bar__inner) {
  border-radius: 999px;
}

/* Element Plus Pagination */
:deep(.el-pagination.is-background .btn-next),
:deep(.el-pagination.is-background .btn-prev),
:deep(.el-pagination.is-background .el-pager li) {
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  color: #475569;
  border-radius: 10px;
  min-width: 32px;
  height: 32px;
  line-height: 30px;
}

:deep(.el-pagination.is-background .el-pager li.is-active) {
  background: #3b82f6;
  border-color: #3b82f6;
  color: #fff;
}

/* 반응형 */
@media (max-width: 1200px) {
  .dashboard-top {
    grid-template-columns: 1fr;
  }

  .side-col {
    display: grid;
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 768px) {
  .side-col {
    grid-template-columns: 1fr;
  }

  .card-header {
    padding: 16px 18px 14px;
  }

  .table-inner-wrap {
    padding: 14px 14px 6px;
  }

  .news-body,
  .task-body {
    padding: 18px;
  }

  .new-project-btn-wrap {
    padding: 0 14px 16px;
  }

  .pagination-wrap {
    padding: 10px 14px 16px;
  }
}
</style>
