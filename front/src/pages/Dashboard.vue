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
            <span class="bc-cur">Main Dashboard</span>
          </div>
        </div>

        <div class="page-container">
          <div class="top-grid">
            <div class="panel main-task-panel">
              <div class="panel-head">
                <h2 class="panel-title">업무 통합 현황</h2>
              </div>

              <div class="panel-body">
                <el-table
                  v-loading="loadingTasks"
                  :data="pagedTaskData"
                  style="width: 100%"
                  :header-cell-style="headerStyle"
                  :cell-style="cellStyle"
                >
                  <el-table-column
                    prop="no"
                    label="#"
                    width="50"
                    align="center"
                  />
                  <el-table-column
                    prop="projectName"
                    label="프로젝트"
                    min-width="150"
                    show-overflow-tooltip
                  />
                  <el-table-column
                    prop="created"
                    label="신규"
                    width="60"
                    align="center"
                  />
                  <el-table-column
                    prop="inProgress"
                    label="진행"
                    width="60"
                    align="center"
                  >
                    <template #default="{ row }">
                      <span class="num-hi">{{ row.inProgress }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    prop="devDone"
                    label="개발완료"
                    width="80"
                    align="center"
                  />
                  <el-table-column
                    prop="rejected"
                    label="반려"
                    width="60"
                    align="center"
                  >
                    <template #default="{ row }">
                      <span
                        :class="{ 'text-red-500 font-bold': row.rejected > 0 }"
                      >
                        {{ row.rejected }}
                      </span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    prop="totalSum"
                    label="합계"
                    width="70"
                    align="center"
                  />
                </el-table>
              </div>

              <div class="pag-wrap">
                <el-pagination
                  v-model:current-page="workPage"
                  :page-size="workPageSize"
                  :total="taskProjects.length"
                  layout="prev, pager, next"
                  background
                />
              </div>

              <div class="panel-action-row">
                <el-button
                  class="action-btn btn-create-project"
                  @click="handleCreateProject"
                >
                  <el-icon><Plus /></el-icon>
                  <span>새 프로젝트 생성</span>
                </el-button>

                <el-button
                  class="action-btn btn-copy-project"
                  @click="handleCopyProject"
                >
                  <el-icon><CopyDocument /></el-icon>
                  <span>프로젝트 복사</span>
                </el-button>
              </div>
            </div>

            <div class="side-stack">
              <div class="panel status-card">
                <div class="panel-head">
                  <span class="panel-title">나의 업무 현황</span>
                </div>
                <div class="task-inner" v-loading="loadingMyTaskSummary">
                  <div class="task-summary">
                    <span class="label">총 업무량</span>
                    <span class="value">
                      {{ totalTaskCount }} <small>건</small>
                    </span>
                  </div>
                  <div class="task-list">
                    <div
                      v-for="item in taskStatusList"
                      :key="item.label"
                      class="task-item"
                    >
                      <span class="item-label">{{ item.label }}</span>
                      <span class="item-cnt" :style="{ color: item.color }">
                        {{ item.count }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="panel news-card">
                <div class="panel-head">
                  <span class="panel-title">새 소식</span>
                </div>
                <div class="news-inner">
                  <div
                    v-for="item in newsList"
                    :key="item.label"
                    class="news-item"
                  >
                    <div class="news-info">
                      <i class="dot" :style="{ backgroundColor: item.color }" />
                      <span>{{ item.label }}</span>
                    </div>
                    <span class="news-badge" :class="{ accent: item.accent }">
                      {{ item.count }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="panel project-list-panel">
            <div class="panel-head">
              <div class="flex items-center gap-2">
                <h2 class="panel-title">프로젝트 목록</h2>
                <span class="count-tag">{{ filteredProjectList.length }}</span>
              </div>

              <div class="flex items-center gap-3">
                <div class="my-project-switch">
                  <span class="switch-label">내 프로젝트만 보기</span>
                  <el-switch v-model="showOnlyMyProjects" />
                </div>

                <el-select
                  v-model="projectSort"
                  size="small"
                  style="width: 150px"
                >
                  <el-option label="최신 생성순" value="createdDesc" />
                  <el-option label="오래된 생성순" value="createdAsc" />
                  <el-option label="프로젝트ID순" value="idAsc" />
                </el-select>
              </div>
            </div>

            <el-table
              v-loading="loadingProjects"
              :data="pagedProjectData"
              style="width: 100%"
              @row-click="goProjectDashboard"
              row-class-name="clickable-row"
              :header-cell-style="headerStyle"
              :cell-style="cellStyle"
            >
              <el-table-column
                prop="projectName"
                label="프로젝트명"
                width="260"
                show-overflow-tooltip
              />

              <el-table-column label="공개범위" width="120" align="center">
                <template #default="{ row }">
                  <span :class="['scope-pill', getProjectScopeClass(row)]">
                    {{ getProjectScopeLabel(row) }}
                  </span>
                </template>
              </el-table-column>

              <el-table-column label="진척도" min-width="180">
                <template #default="{ row }">
                  <el-progress
                    :percentage="Number(row.progressRate || 0)"
                    :stroke-width="8"
                    :color="
                      Number(row.progressRate || 0) >= 100
                        ? '#10b981'
                        : '#1B5C9C'
                    "
                  />
                </template>
              </el-table-column>

              <el-table-column
                prop="createdAt"
                label="생성일"
                width="120"
                align="center"
              />

              <el-table-column
                prop="endDate"
                label="마감일"
                width="120"
                align="center"
              />

              <el-table-column
                prop="pmName"
                label="PM"
                width="100"
                align="center"
              />

              <el-table-column label="상태" width="100" align="center">
                <template #default="{ row }">
                  <span :class="['status-pill', statusClass(row)]">
                    {{ statusLabel(row) }}
                  </span>
                </template>
              </el-table-column>
            </el-table>

            <div class="pag-wrap">
              <el-pagination
                v-model:current-page="projectPage"
                :page-size="projectPageSize"
                :total="filteredProjectList.length"
                layout="prev, pager, next"
                background
              />
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>

  <ProjectCreateModal
    v-model="createProjectModalOpen"
    @submitted="handleCreateSubmitted"
  />

  <ProjectCopyModal
    v-model="copyProjectModalOpen"
    @submitted="handleCopySubmitted"
  />
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import { storeToRefs } from "pinia";
import { Plus, CopyDocument } from "@element-plus/icons-vue";
import api from "../utils/api";
import { useAuthStore } from "../stores/auth";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import ProjectCreateModal from "../project/ProjectCreateModal.vue";
import ProjectCopyModal from "../project/ProjectCopyModal.vue";

const router = useRouter();
const authStore = useAuthStore();
const { user } = storeToRefs(authStore);

const sidebarOpen = ref(false);
const createProjectModalOpen = ref(false);
const copyProjectModalOpen = ref(false);

const workPage = ref(1);
const workPageSize = 5;

const projectPage = ref(1);
const projectPageSize = 8;
const projectSort = ref("createdDesc");
const showOnlyMyProjects = ref(false);

const loadingTasks = ref(false);
const loadingProjects = ref(false);
const loadingMyTaskSummary = ref(false);

const taskProjects = ref([]);
const projectList = ref([]);

const myTasks = ref({
  created: 0,
  inProgress: 0,
  done: 0,
  rejected: 0,
  deadline: 0,
});

const taskStatusList = computed(() => [
  { label: "시작 전", count: myTasks.value.created, color: "#6b7280" },
  { label: "진행중", count: myTasks.value.inProgress, color: "#1B5C9C" },
  { label: "완료", count: myTasks.value.done, color: "#10b981" },
  { label: "반려", count: myTasks.value.rejected, color: "#ef4444" },
  { label: "기한 임박", count: myTasks.value.deadline, color: "#f59e0b" },
]);

const totalTaskCount = computed(() =>
  Object.values(myTasks.value).reduce((sum, value) => sum + value, 0),
);

const newsList = [
  { label: "메세지", count: 5, color: "#3b82f6", accent: false },
  { label: "새 공지사항", count: 3, color: "#10b981", accent: false },
  { label: "업무 생성", count: 2, color: "#f59e0b", accent: true },
];

const fetchTaskList = async () => {
  loadingTasks.value = true;
  try {
    const res = await api.get("/TaskListDash");
    taskProjects.value = res.data || [];
  } catch (err) {
    console.error("업무 통합 현황 조회 실패:", err);
  } finally {
    loadingTasks.value = false;
  }
};

const fetchProjectList = async () => {
  const userId = user.value?.userId;

  if (!userId) {
    projectList.value = [];
    return;
  }

  loadingProjects.value = true;
  try {
    const res = await api.get("/ProjectList", {
      params: { userId },
    });
    projectList.value = Array.isArray(res.data) ? res.data : [];
  } catch (err) {
    console.error("프로젝트 목록 조회 실패:", err);
    projectList.value = [];
  } finally {
    loadingProjects.value = false;
  }
};

const fetchMyTaskSummary = async () => {
  const userId = user.value?.userId;

  if (!userId) {
    console.warn("로그인 사용자 userId가 없습니다.", user.value);

    myTasks.value = {
      created: 0,
      inProgress: 0,
      done: 0,
      rejected: 0,
      deadline: 0,
    };
    return;
  }

  loadingMyTaskSummary.value = true;

  try {
    const res = await api.get(`/project/myTaskSummary/${userId}`);

    myTasks.value = {
      created: res.data?.created || 0,
      inProgress: res.data?.inProgress || 0,
      done: res.data?.done || 0,
      rejected: res.data?.rejected || 0,
      deadline: res.data?.deadline || 0,
    };
  } catch (err) {
    console.error("나의 업무 현황 조회 실패:", err);

    myTasks.value = {
      created: 0,
      inProgress: 0,
      done: 0,
      rejected: 0,
      deadline: 0,
    };
  } finally {
    loadingMyTaskSummary.value = false;
  }
};

onMounted(() => {
  fetchProjectList();
  fetchTaskList();
  fetchMyTaskSummary();
});

watch(
  () => user.value?.userId,
  (newUserId) => {
    if (newUserId) {
      fetchProjectList();
      fetchMyTaskSummary();
    } else {
      projectList.value = [];
    }
  },
  { immediate: true },
);

watch([projectSort, showOnlyMyProjects], () => {
  projectPage.value = 1;
});

const pagedTaskData = computed(() => {
  const start = (workPage.value - 1) * workPageSize;
  return taskProjects.value
    .slice(start, start + workPageSize)
    .map((item, index) => ({
      ...item,
      no: start + index + 1,
    }));
});

const filteredProjectList = computed(() => {
  if (!showOnlyMyProjects.value) {
    return projectList.value;
  }

  return projectList.value.filter(
    (project) => String(project.isMyProject || "").toUpperCase() === "Y",
  );
});

const sortedProjectList = computed(() => {
  const sorted = [...filteredProjectList.value];

  sorted.sort((a, b) => {
    if (projectSort.value === "createdDesc") {
      return (
        new Date(b.createdAt || "1900-01-01").getTime() -
        new Date(a.createdAt || "1900-01-01").getTime()
      );
    }

    if (projectSort.value === "createdAsc") {
      return (
        new Date(a.createdAt || "1900-01-01").getTime() -
        new Date(b.createdAt || "1900-01-01").getTime()
      );
    }

    return Number(a.projectId || 0) - Number(b.projectId || 0);
  });

  return sorted;
});

const pagedProjectData = computed(() => {
  const start = (projectPage.value - 1) * projectPageSize;
  return sortedProjectList.value.slice(start, start + projectPageSize);
});

const getProjectScopeValue = (row) => {
  return String(row.isPublic || "").toUpperCase();
};

const getProjectScopeLabel = (row) => {
  const scope = getProjectScopeValue(row);

  if (scope === "P1") return "전체공개";
  if (scope === "P2") return "멤버공개";
  return "-";
};

const getProjectScopeClass = (row) => {
  const scope = getProjectScopeValue(row);

  if (scope === "P1") return "scope-public";
  if (scope === "P2") return "scope-member";
  return "scope-default";
};

const statusLabel = (row) => {
  const progress = Number(row.progressRate || 0);

  if (progress >= 100) return "완료";
  if (progress >= 80) return "마감임박";
  return progress > 0 ? "진행중" : "준비중";
};

const statusClass = (row) => {
  const progress = Number(row.progressRate || 0);

  if (progress >= 100) return "pill-green";
  if (progress >= 80) return "pill-orange";
  return progress > 0 ? "pill-blue" : "pill-gray";
};

const headerStyle = () => ({
  background: "#f9fafb",
  color: "#4b5563",
  fontSize: "12px",
  fontWeight: "600",
  height: "44px",
});

const cellStyle = () => ({
  color: "#1f2937",
  fontSize: "13px",
  padding: "10px 0",
});

const handleCreateProject = () => {
  createProjectModalOpen.value = true;
};

const handleCopyProject = () => {
  copyProjectModalOpen.value = true;
};

const handleCreateSubmitted = async () => {
  createProjectModalOpen.value = false;
  await fetchProjectList();
};

const handleCopySubmitted = async () => {
  copyProjectModalOpen.value = false;
  await fetchProjectList();
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

.top-grid {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 24px;
  align-items: start;
}

.side-stack {
  display: flex;
  flex-direction: column;
  gap: 24px;
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
}

.panel-title {
  font-size: 15px;
  font-weight: 700;
  color: #111827;
}

.count-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 24px;
  height: 24px;
  padding: 0 8px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 12px;
  font-weight: 700;
}

.my-project-switch {
  display: flex;
  align-items: center;
  gap: 8px;
}

.switch-label {
  font-size: 13px;
  color: #374151;
  font-weight: 500;
}

.panel-body {
  background: #fff;
}

.task-inner {
  padding: 20px;
}

.task-summary {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #e5e7eb;
}

.task-summary .label {
  color: #6b7280;
  font-size: 13px;
}

.task-summary .value {
  color: #1b5c9c;
  font-size: 24px;
  font-weight: 800;
}

.task-summary .value small {
  font-size: 14px;
  font-weight: 400;
  color: #9ca3af;
}

.task-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 13px;
}

.item-cnt {
  font-weight: 700;
}

.news-inner {
  padding: 5px 0;
}

.news-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 20px;
  border-bottom: 1px solid #f9fafb;
}

.news-item:last-child {
  border-bottom: none;
}

.news-info {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}

.dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
}

.news-badge {
  background: #f3f4f6;
  padding: 2px 10px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 700;
}

.news-badge.accent {
  background: #fef3c7;
  color: #92400e;
}

.status-pill {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 700;
}

.pill-blue {
  background: #eff6ff;
  color: #1e40af;
}

.pill-green {
  background: #ecfdf5;
  color: #065f46;
}

.pill-orange {
  background: #fff7ed;
  color: #9a3412;
}

.pill-gray {
  background: #f9fafb;
  color: #4b5563;
}

.scope-pill {
  display: inline-block;
  min-width: 72px;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  line-height: 1.2;
}

.scope-public {
  background: #ecfdf5;
  color: #065f46;
}

.scope-member {
  background: #eff6ff;
  color: #1d4ed8;
}

.scope-default {
  background: #f3f4f6;
  color: #6b7280;
}

.num-hi {
  color: #1b5c9c;
  font-weight: 700;
}

:deep(.el-table) {
  --el-table-header-bg-color: #f9fafb;
}

.main-task-panel :deep(.el-table__body tr:hover > td) {
  background-color: #ffffff !important;
  cursor: default !important;
}

.project-list-panel :deep(.el-table__body tr.clickable-row) {
  cursor: pointer !important;
  transition: background-color 0.2s ease;
}

.project-list-panel :deep(.el-table__body tr.clickable-row:hover > td) {
  background-color: #f5f9ff !important;
}

.pag-wrap {
  padding: 16px;
  display: flex;
  justify-content: center;
  border-top: 1px solid #f3f4f6;
  background: #f9fafb;
}

.panel-action-row {
  min-height: auto;
  padding: 20px 24px 24px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  align-items: center;
  background: #fff;
}

.action-btn {
  width: 100%;
  height: 46px !important;
  border-radius: 10px !important;
  font-size: 14px !important;
  font-weight: 700 !important;
  display: flex !important;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition:
    background-color 0.2s ease,
    border-color 0.2s ease,
    color 0.2s ease,
    transform 0.2s ease,
    box-shadow 0.2s ease !important;
  box-shadow: none !important;
}

.action-btn:hover {
  transform: translateY(-1px);
}

.btn-create-project {
  background: #1f4f82 !important;
  border: 1px solid #1f4f82 !important;
  color: #ffffff !important;
}

.btn-create-project:hover {
  background: #183e66 !important;
  border-color: #183e66 !important;
}

.btn-copy-project {
  background: #f8fafc !important;
  border: 1px solid #cbd5e1 !important;
  color: #334155 !important;
}

.btn-copy-project:hover {
  background: #eef2f7 !important;
  border-color: #94a3b8 !important;
  color: #1e293b !important;
}

.action-btn :deep(.el-icon) {
  font-size: 16px;
}

@media (max-width: 1200px) {
  .top-grid {
    grid-template-columns: 1fr;
  }

  .side-stack {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;
  }

  .panel-action-row {
    grid-template-columns: 1fr;
    padding: 20px;
    min-height: auto;
  }

  .action-btn {
    height: 68px !important;
    font-size: 16px !important;
  }
}
</style>
