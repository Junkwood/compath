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
          <!-- 페이지 타이틀 -->
          <div class="mb-6">
            <h1
              class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
            >
              업무 상세
            </h1>
          </div>

          <!-- ────────── 상단: 업무 통합 현황 + 우측 카드 ────────── -->
          <div class="dashboard-top mb-5">
            <!-- 업무 통합 현황 -->
            <div>
              <div class="card main-col">
                <div class="card-header">
                  <div>
                    <span class="card-title" v-for="info in taskPjList"
                      >{{ info }} >
                    </span>
                    <span class="card-title">{{ taskInfo.title }}</span>
                  </div>
                  <el-tag size="Default"
                    >#{{ taskInfo.taskId }} {{ taskInfo.typeName }}</el-tag
                  >
                </div>

                <el-descriptions :column="2" border>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">상태</div>
                    </template>
                    {{ taskInfo.statusName }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">유형</div>
                    </template>
                    {{ taskInfo.typeName }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">우선 순위</div>
                    </template>
                    {{ taskInfo.priorityName }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">담당자</div>
                    </template>
                    {{ taskInfo.assigneeUserName }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">업무 등록일</div>
                    </template>
                    {{ taskInfo.createdAt }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">추정 시간</div>
                    </template>
                    {{ taskInfo.estimatedHours }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">예정 시작일</div>
                    </template>
                    {{ taskInfo.estStartDate }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">예정 종료일</div>
                    </template>
                    {{ taskInfo.estEndDate }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">시작일</div>
                    </template>
                    {{ taskInfo.startDate }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">완료일</div>
                    </template>
                    {{ taskInfo.dueDate }}
                  </el-descriptions-item>
                </el-descriptions>
              </div>
              <div class="card main-col mt-3">
                <div class="card-header">
                  <span class="card-title">업무 설명</span>
                </div>
                <div class="min-h-48">
                  <span>{{ taskInfo.content }}</span>
                </div>
              </div>
              <!-- ────────── 하단: 프로젝트 목록 ────────── -->
              <div class="card main-col mt-3">
                <el-tabs
                  v-model="activeName"
                  @tab-click="handleClick"
                  default-value="작업이력"
                >
                  <el-tab-pane label="작업이력" name="작업이력">
                    <el-table
                      v-loading="loadingProjects"
                      :data="pagedProjectData"
                      style="width: 100%"
                      :header-cell-style="headerStyle"
                      :cell-style="cellStyle"
                      @row-click="goProjectDashboard"
                    >
                      <el-table-column
                        prop="parentProjectName"
                        label="번호"
                        width="100"
                        align="center"
                      />
                      <el-table-column
                        prop="endDate"
                        label="일시"
                        width="200"
                        align="center"
                      />
                      <el-table-column
                        prop="pmUserId"
                        label="작업자"
                        width="200"
                        align="center"
                      />
                      <el-table-column
                        prop="pmUserId"
                        label="내역"
                        min-width="550"
                        align="center"
                      />
                    </el-table>
                  </el-tab-pane>
                  <el-tab-pane label="소요시간" name="second">
                    <el-table
                      v-loading="loadingProjects"
                      :data="pagedProjectData"
                      style="width: 100%"
                      :header-cell-style="headerStyle"
                      :cell-style="cellStyle"
                      @row-click="goProjectDashboard"
                    >
                      <el-table-column
                        prop="parentProjectName"
                        label="번호"
                        width="100"
                        align="center"
                      />
                      <el-table-column
                        prop="endDate"
                        label="일시"
                        width="200"
                        align="center"
                      />
                      <el-table-column
                        prop="pmUserId"
                        label="작업자"
                        width="200"
                        align="center"
                      />
                      <el-table-column
                        prop="pmUserId"
                        label="소요시간"
                        width="200"
                        align="center"
                      />
                      <el-table-column
                        prop="pmUserId"
                        label="내역"
                        min-width="550"
                        align="center"
                      />
                    </el-table>
                  </el-tab-pane>
                  <el-tab-pane label="첨부파일" name="third"></el-tab-pane>
                </el-tabs>
              </div>
            </div>
            <!-- 우측 카드 묶음 -->
            <div class="side-col">
              <!-- 우측 상단 버튼들 -->
              <div class="flex justify-between">
                <button @click="registerActualTime" class="btn-navy">
                  <el-icon :size="20"><Clock /></el-icon>소요시간 등록
                </button>

                <button @click="goModify" class="btn-green">수정</button>
              </div>
              <!-- 진척도 -->
              <div class="card">
                <div class="card-header">
                  <span class="card-title">진척도</span>
                </div>
                <div class="progress-body">
                  <div class="demo-progress">
                    <el-progress
                      type="circle"
                      :percentage="taskInfo.progressRate"
                    />
                  </div>
                </div>
              </div>

              <!-- 소요시간 -->
              <div class="card">
                <div class="card-header">
                  <span class="card-title">소요시간</span>
                </div>
                <div class="task-body">
                  <div class="h-20 place-self-center leading-20">
                    <span class="total-number"
                      >{{ taskInfo.actualHours }} 시간</span
                    >
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

              <!-- 첨부파일 -->
              <div class="card">
                <div class="card-header">
                  <span class="card-title">첨부파일</span>
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
        </div>
      </main>
    </div>
  </div>
  <TaskActualTimeModal
    v-model="openTimeModal"
    :timeRegisterUser="timeRegisterUser"
    @submitted="submitted"
  ></TaskActualTimeModal>
</template>

<script setup>
import { ref, onBeforeMount } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { usetaskKJHStore } from "../stores/taksKJH";
import { useAuthStore } from "../stores/auth";
import { Clock } from "@element-plus/icons-vue";
import TaskActualTimeModal from "./TaskActualTimeModal.vue";

const taskStore = usetaskKJHStore();
const authStore = useAuthStore();
const sidebarOpen = ref(false);

const route = useRoute();
const router = useRouter();
let taskId = ref(route.params.taskId); // 업무 번호
let projectId = ref(route.params.projectId); // 프로젝트 번호
let taskInfo = ref({
  actualHours: "",
  assigneeUserId: "",
  assigneeUserName: "",
  content: "",
  dueDate: "",
  estEndDate: "",
  estStartDate: "",
  estimatedHours: "",
  parentProjectName: "",
  priorityName: "",
  progressRate: "",
  projectName: "",
  startDate: "",
  statusName: "",
  taskId: "",
  title: "",
  typeName: "",
}); // 업무 상세 정보
let taskPjList = ref([]); // 프로젝트 이름 배열

const loadingProjects = ref(false);

onBeforeMount(async () => {
  // 업무 상세목록 조회
  await taskStore.getTaskById(taskId.value);

  taskInfo.value = { ...taskStore.taskDetail };

  // 상위 프로젝트가 없을 때 구분
  if (taskInfo.value.parentProjectName != null) {
    taskPjList.value = [taskInfo.value.parentProjectName, taskInfo.projectName];
  } else {
    taskPjList.value = [taskInfo.value.projectName];
  }
});

// 소요시간 등록 버튼(모달 오픈)
const openTimeModal = ref(false);
const timeRegisterUser = ref({});
const registerActualTime = () => {
  console.log();
  openTimeModal.value = true;

  // 모달창 전달 정보(props)
  timeRegisterUser.value = {
    userId: authStore.user.userId,
    userName: authStore.user.name,
    taskId: taskInfo.value.taskId,
    taskTitle: taskInfo.value.title,
  };
};

// 수정 버튼(업무 수정 페이지로 이동)
const goModify = () => {
  router.push({ name: "taskModify", params: { taskId: taskId.value } });
};

// 모달창 등록시
const submitted = async (val) => {};

// ── 테이블 공통 스타일 ─────────────────────────────
const headerStyle = () => ({
  background: "#f8fafc",
  color: "#64748b",
  fontSize: "12px",
  fontWeight: "600",
  borderBottom: "1px solid #e2e8f0",
});
const cellStyle = () => ({
  fontSize: "13px",
  color: "#374151",
  borderBottom: "1px solid #f1f5f9",
});
</script>

<style scoped>
/* ── 상단 레이아웃 ── */
.dashboard-top {
  display: grid;
  grid-template-columns: 1fr 230px;
  gap: 20px;
  align-items: start;
}
.side-col {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ── 카드 공통 ── */
.card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}
.card-header {
  padding: 14px 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.card-title {
  font-weight: 600;
  font-size: 14px;
  color: #1a1a2e;
}
.toggle-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #555;
  cursor: pointer;
}

/* ── 업무 통합 현황 ── */
.total-cell {
  font-weight: 600;
  color: #2563eb;
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 12px 0;
  border-top: 1px solid #f0f0f0;
}
.new-project-btn-wrap {
  padding: 12px 20px 16px;
}
.new-project-btn {
  width: 100%;
  background: #c7d9f5;
  border: none;
  color: #1a1a2e;
  font-weight: 500;
  font-size: 14px;
  border-radius: 8px;
  height: 40px;
}
.new-project-btn:hover {
  background: #a8c4ef;
}

/* ── 나의 업무 현황 / 새 소식 ── */
.progress-body {
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-self: center;
  gap: 16px;
}
.news-body {
  padding: 16px 20px;
}

.total-number {
  font-size: 22px;
  font-weight: 700;
  color: #1d4ed8;
}
.dot-list {
  list-style: none;
  margin: 0;
  padding: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.dot-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
}
.dot-left {
  display: flex;
  align-items: center;
  gap: 8px;
}
.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}
.dot-label {
  color: #555;
}
.dot-count {
  font-weight: 600;
  color: #1a1a2e;
}

/* ── 프로젝트 목록 ── */
.progress-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
}
.progress-text {
  font-size: 12px;
  font-weight: 600;
  color: #2563eb;
  min-width: 32px;
}
:deep(.el-table__body-wrapper .el-table__row) {
  cursor: pointer;
}

/* ── ElementPlus 오버라이드 ── */
:deep(.el-table) {
  --el-table-border-color: #f1f5f9;
  --el-table-header-bg-color: #f8fafc;
}
:deep(.el-table__row:hover > td) {
  background: #f0f7ff !important;
}
:deep(.el-progress-bar__outer) {
  background: #e0eaff;
}

/* el-decription */

.cell-item {
  display: flex;
  align-items: center;
}

/* 제일 하부 탭 */
.demo-tabs > .el-tabs__content {
  padding: 32px;
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
  margin: 0px;
}

:deep(.demo-tabs > .el-tabs__header) {
  margin: 0px;
}

:deep(.el-tabs--top > .el-tabs__header) {
  padding-left: 20px;
  padding-right: 20px;
}

:deep(.el-tabs__header) {
  margin: 0px;
}
/* ── 반응형 ── */
@media (max-width: 1024px) {
  .dashboard-top {
    grid-template-columns: 1fr;
  }
  .side-col {
    flex-direction: row;
  }
  .side-col .card {
    flex: 1;
  }
}
@media (max-width: 600px) {
  .side-col {
    flex-direction: column;
  }
}

/* 버튼들 */
.btn-select {
  height: 38px;
  padding: 0 16px;
  font-size: 13px;
  font-weight: 500;
  border-radius: 10px;
  cursor: pointer;
  white-space: nowrap;
  background: #fff;
  border: 1px solid #e2e8f0;
  color: #475569;
  transition: all 0.2s;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}
.btn-select:hover {
  background: #f8fafc;
  border-color: #94a3b8;
  color: #1e293b;
}
.btn-confirm {
  height: 38px;
  padding: 0 16px;
  font-size: 13px;
  font-weight: 500;
  border-radius: 10px;
  cursor: pointer;
  white-space: nowrap;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #475569;
  transition: all 0.2s;
}
.btn-confirm:hover {
  background: #e2e8f0;
  color: #1e293b;
}
.btn-navy {
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  background: #1e3a5f;
  color: #fff;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(30, 58, 95, 0.25);
  line-height: 38px;
}
.btn-navy:hover {
  background: #162d4a;
  box-shadow: 0 4px 10px rgba(30, 58, 95, 0.3);
  transform: translateY(-1px);
}
.btn-green {
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  background: #1882c9;
  color: #fff;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(22, 163, 74, 0.25);
}
.btn-green:hover {
  background: #60aee2;
  box-shadow: 0 4px 10px rgba(22, 163, 74, 0.3);
  transform: translateY(-1px);
}
/* 진척도 */
:depp(.demo-progress .el-progress--circle) {
  margin-right: 15px;
}
</style>
