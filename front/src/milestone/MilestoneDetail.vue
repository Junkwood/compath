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
          <!-- projectDashboard.vue 스타일 제목 영역 -->
          <div class="mb-6 proj-title-row">
            <div class="proj-title-left">
              <h2
                class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
              >
                마일스톤 상세 페이지
              </h2>
              <div class="proj-name-row">
                <span class="proj-name">【 {{ projectInfo.projectName }} 】</span>
                <span class="proj-period">
                  {{ projectInfo.startDate }} - {{ projectInfo.endDate }}
                </span>
              </div>
            </div>
          </div>

          <div class="milestone-detail-page">
            <!-- 상단 마일스톤 탭 영역 -->
            <div class="milestone-top-wrap">
              <div class="milestone-top-list">
                <div
                  v-for="item in milestoneList"
                  :key="item.milestoneId"
                  class="top-milestone-item"
                  :class="{
                    active: Number(item.milestoneId) === Number(selectedMilestoneId),
                  }"
                  @click="handleMilestoneTabClick(item)"
                >
                  <div class="flag-icon-wrap">
                    <div class="flag-pole"></div>
                    <div class="flag-box"></div>
                  </div>

                  <div class="top-milestone-text">
                    <div class="top-milestone-name">
                      {{ item.milestoneName }}
                    </div>
                    <div class="top-milestone-date">
                      {{ item.startDate }} - {{ item.dueDate }}
                    </div>
                  </div>
                </div>
              </div>

              <div class="top-btn-wrap">
                <el-button class="list-btn" @click="goMilestoneList">
                  ← 목록으로
                </el-button>
              </div>
            </div>

            <!-- 1. 하위 프로젝트 목록 -->
            <section class="detail-section">
              <div class="section-card">
                <div class="section-header">하위 프로젝트 목록</div>

                <div class="subproject-table-wrap">
                  <table class="detail-table subproject-table">
                    <tbody>
                        <tr
                          v-for="item in currentSubProjectList"
                          :key="item.projectId"
                          class="clickable-row"
                          @click="goSubProjectDashboard(item)"
                        >
                          <td>{{ item.projectName }}</td>
                          <td>{{ item.startDate }} - {{ item.endDate }}</td>
                          <td>{{item.userName}}</td>

                        </tr>
                        <tr v-if="currentSubProjectList.length ===0">
                          <td class="empty-cell">등록된 하위 프로젝트가 없습니다.</td>
                        </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </section>

            <!-- 2. 개별 업무 -->
            <section class="detail-section">

              <div class="section-card">
                <div class="section-header">개별 업무</div>

                <div class="task-table-wrap">
                  <table class="detail-table task-table">
                    <thead>
                      <tr>
                        <th>업무명</th>
                        <th>담당자</th>
                        <th>유형</th>
                        <th>우선순위</th>
                        <th>진행률</th>
                        <th>시작일</th>
                        <th>종료일</th>
                      </tr>
                    </thead>

                    <tbody>
                      <tr
                        v-for="task in currentTaskList"
                        :key="task.taskId"
                        class="clickable-row"
                        @click="goTaskDetail(task)"
                      >
                        <td>{{ task.title }}</td>
                        <td>{{ task.userName }}</td>
                        <td>{{ task.typeName }}</td>
                        <td>{{ task.codeName }}</td> 
                        <td>
                          <div
                            v-if="typeof task.progressRate === 'number'"
                            class="progress-cell"
                          >
                            <div class="mini-progress-bar">
                              <div
                                class="mini-progress-fill"
                                :style="{ width: Number(task.progressRate) + '%' }"
                              ></div> 
                             </div>
                            <span class="mini-progress-text"
                              >{{ Number(task.progressRate) }}%</span
                            >
                          </div> 
                          <span v-else class="not-started-text">-</span> 
                        </td> 
                        <td>{{ task.startDate }}</td>
                        <td>{{ task.estEndDate }}</td>
                      </tr>

                      <tr v-if="currentTaskList.length === 0">
                        <td colspan="7" class="empty-cell">
                          등록된 개별 업무가 없습니다.
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </section>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import axios from "axios";

const route = useRoute();
const router = useRouter();
const sidebarOpen = ref(false);

//상단 프로젝트명
const projectInfo = ref({
  projectId: null,
  projectName: "",
  startDate: "",
  endDate: "",
});

const fetchProjectDetail = async () => {
  try {
    const projectId = route.params.projectId;
    const res = await axios.get(`/api/ProjectDetail/${projectId}`);
    projectInfo.value = res.data;
  } catch (err) {
    console.error("프로젝트 상세 조회 실패:", err);
  }
};

// 상단 탭용 마일스톤 목록
const milestoneList = ref([]);

const fetchMlistTab = async () => {
  try {
    const projectId = route.params.projectId;
    const res = await axios.get(`/api/MilestoneTab/${projectId}`);

    milestoneList.value = res.data;
  } catch (err) {
    console.error("마일스톤 목록 조회 실패:", err);
    milestoneList.value = [];
  }
}

// 현재 선택된 milestoneId
const selectedMilestoneId = computed(() => Number(route.params.milestoneId));
const currentProjectId = computed(() => Number(route.params.projectId));

//하위프로젝트 리스트
const subProjectList = ref([]);

const fetchSubProjectList = async () => {
  try {
    const projectId = route.params.projectId;
    const milestoneId = route.params.milestoneId;

    const res = await axios.get(
      `/api/MilestoneSubProjects/${projectId}/${milestoneId}`
    );

    subProjectList.value = Array.isArray(res.data) ? res.data : [];
    console.log("하위프로젝트 응답:", res.data);
  } catch (err) {
    console.error("하위 프로젝트 목록 조회 실패:", err);
    console.error("status:", err?.response?.status);
    console.error("data:", err?.response?.data);
    subProjectList.value = [];
  }
};

const currentSubProjectList = computed(() => subProjectList.value);
const currentTaskList = ref([]);

//마일스톤은 있고 하위프로젝트는 없는 개별업무 조회
const fetchTaskWnoSubP = async ()=>{
  try{
    const projectId = route.params.projectId;
    const milestoneId = route.params.milestoneId;

    const res = await axios.get(`/api/MilestoneWnoSubPidList/${projectId}/${milestoneId}`);
    currentTaskList.value = res.data;
  } catch(err){
    console.error("하위프로젝트 없는 개별업무 조회 실패 : ", err)
  }
}


const handleMilestoneTabClick = (item) => {
  if (Number(item.milestoneId) === Number(selectedMilestoneId.value)) return;

  router.push({
    name: "milestoneDetail",
    params: {
      projectId: currentProjectId.value,
      milestoneId: item.milestoneId,
    },
  });
};

const goMilestoneList = () => {
  router.push({
    name: "milestoneDashboard",
    params: {
      projectId: currentProjectId.value,
    },
  });
};

const goSubProjectDashboard = (item) => {
  console.log("하위 프로젝트 대시보드로 이동:", item);

  // 나중에 실제 라우터 연결
  // router.push({
  //   name: "projectDashboard",
  //   params: { projectId: item.projectId },
  // });
};

const goTaskDetail = (task) => {
  console.log("업무 상세 페이지로 이동:", task);

  // 나중에 실제 라우터 연결
  // router.push({
  //   name: "taskDetail",
  //   params: {
  //     projectId: currentProjectId.value,
  //     milestoneId: selectedMilestoneId.value,
  //     taskId: task.taskId,
  //   },
  // });
};

onMounted(async() => {
  await fetchProjectDetail();
  fetchMlistTab();
  await fetchSubProjectList();
  await fetchTaskWnoSubP();
});

watch(
  () => route.params.milestoneId,
  async () => {
    await fetchSubProjectList();
    await fetchTaskWnoSubP();
  }
);
</script>

<style scoped>
/* =========================
   Variables
========================= */
.milestone-detail-page,
.proj-title-row,
.milestone-top-wrap {
  --accent: #4f46e5;
  --accent-bg: #eef2ff;
  --accent-text: #3730a3;
}

/* =========================
   Title Area
========================= */
.proj-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 24px;
}
.proj-title-left {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.proj-name-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.proj-name {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
  letter-spacing: -0.02em;
}
.proj-period {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 999px;
  padding: 2px 10px;
}

/* =========================
   Page Shell
========================= */
.milestone-detail-page {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  box-shadow: 0 1px 4px rgba(15, 23, 42, 0.04);
  padding: 28px 26px 36px;
  min-height: 720px;
}

/* =========================
   Top Milestone Tabs
========================= */
.milestone-top-wrap {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 28px;
  padding-bottom: 22px;
  border-bottom: 1px solid #e2e8f0;
}
.milestone-top-list {
  display: flex;
  align-items: stretch;
  gap: 8px;
  flex-wrap: wrap;
}
.top-milestone-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  min-width: 200px;
  padding: 11px 14px;
  border-radius: 12px;
  cursor: pointer;
  transition: border-color 0.15s, background 0.15s;
  border: 1px solid #e2e8f0;
  background: #ffffff;
}
.top-milestone-item:hover {
  border-color: #c7d2fe;
}
.top-milestone-item.active {
  border-color: var(--accent);
  background: var(--accent-bg);
}

/* Flag */
.flag-icon-wrap {
  position: relative;
  width: 26px;
  height: 34px;
  flex-shrink: 0;
  margin-top: 1px;
}
.flag-pole {
  position: absolute;
  left: 10px;
  top: 0;
  width: 2px;
  height: 30px;
  background: #cbd5e1;
  border-radius: 999px;
}
.top-milestone-item.active .flag-pole {
  background: var(--accent);
}
.flag-box {
  position: absolute;
  left: 10px;
  top: 3px;
  width: 16px;
  height: 10px;
  background: #e2e8f0;
  border-radius: 2px;
  clip-path: polygon(0 0, 70% 0, 100% 50%, 70% 100%, 0 100%);
}
.top-milestone-item.active .flag-box {
  background: var(--accent);
}

.top-milestone-text {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}
.top-milestone-name {
  font-size: 13.5px;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.3;
  letter-spacing: -0.01em;
}
.top-milestone-item.active .top-milestone-name {
  color: var(--accent-text);
}
.top-milestone-date {
  font-size: 11px;
  color: #94a3b8;
  font-weight: 500;
  font-variant-numeric: tabular-nums;
}

.top-btn-wrap {
  display: flex;
  align-items: center;
}
.list-btn {
  height: 34px !important;
  padding: 0 14px !important;
  border: 1px solid #e2e8f0 !important;
  border-radius: 8px !important;
  background: #ffffff !important;
  color: #64748b !important;
  font-size: 12.5px !important;
  font-weight: 600 !important;
  box-shadow: none !important;
  transition: border-color 0.15s, color 0.15s !important;
}
.list-btn:hover {
  border-color: var(--accent) !important;
  color: var(--accent) !important;
  background: #ffffff !important;
}

/* =========================
   Section
========================= */
.detail-section {
  margin-bottom: 20px;
}
.section-card {
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  background: #ffffff;
  overflow: hidden;
}
.section-header {
  min-height: 46px;
  display: flex;
  align-items: center;
  padding: 0 18px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  font-size: 15px;
  font-weight: 500;
  color: #202122;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}

/* =========================
   Table
========================= */
.subproject-table-wrap,
.task-table-wrap {
  width: 100%;
  overflow-x: auto;
}
.detail-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}
.detail-table td,
.detail-table th {
  border-bottom: 1px solid #f1f5f9;
  padding: 0 16px;
  height: 50px;
  font-size: 13px;
  color: #0f172a;
  vertical-align: middle;
  text-align: left;
}
.detail-table tr:last-child td {
  border-bottom: none;
}
.detail-table th {
  background: #f8fafc;
  font-size: 11px;
  /* font-weight: 600; */
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

/* Sub Project Table */
.subproject-table td { height: 52px; }
.subproject-table td:nth-child(1) { width: 36%; color: #000000; }
.subproject-table td:nth-child(2) { width: 38%; color: #000000; font-variant-numeric: tabular-nums; }
.subproject-table td:nth-child(3) { width: 26%; color: #000000; }

/* Task Table */
.task-table th:nth-child(1), .task-table td:nth-child(1) { width: 28%; }
.task-table th:nth-child(2), .task-table td:nth-child(2) { width: 12%; }
.task-table th:nth-child(3), .task-table td:nth-child(3) { width: 10%; }
.task-table th:nth-child(4), .task-table td:nth-child(4) { width: 10%; }
.task-table th:nth-child(5), .task-table td:nth-child(5) { width: 18%; }
.task-table th:nth-child(6), .task-table td:nth-child(6) { width: 11%; }
.task-table th:nth-child(7), .task-table td:nth-child(7) { width: 11%; }
.task-table td:nth-child(1) { font-size:13px; color: #000000; }

/* =========================
   Progress
========================= */
.progress-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
.mini-progress-bar {
  width: 80px;
  height: 5px;
  background: #e2e8f0;
  border-radius: 999px;
  overflow: hidden;
}
.mini-progress-fill {
  height: 100%;
  background: #4f46e5;
  border-radius: 999px;
  transition: width 0.4s ease;
}
.mini-progress-text {
  font-size: 11.5px;
  font-weight: 700;
  color: #64748b;
  font-variant-numeric: tabular-nums;
  min-width: 30px;
}
.not-started-text {
  font-size: 13px;
  color: #cbd5e1;
}
.empty-cell {
  text-align: center;
  color: #94a3b8;
  font-size: 13px;
  padding: 32px 16px !important;
  height: auto !important;
}

/* =========================
   Hover
========================= */
.clickable-row {
  cursor: pointer;
  transition: background 0.12s;
}
.clickable-row:hover td {
  background: #f8fafc;
}

/* =========================
   Responsive
========================= */
@media (max-width: 1024px) {
  .milestone-top-wrap { flex-direction: column; align-items: stretch; }
  .top-milestone-item { min-width: 190px; }
}
@media (max-width: 768px) {
  .proj-title-row { flex-direction: column; gap: 6px; }
  .proj-name-row { flex-direction: column; align-items: flex-start; gap: 4px; }
  .proj-name { font-size: 13.5px; }
  .milestone-detail-page { padding: 18px 14px 24px; border-radius: 16px; }
  .top-milestone-item { width: 100%; min-width: 100%; }
  .detail-table td, .detail-table th { font-size: 12px; padding: 0 10px; }
  .section-header { min-height: 42px; padding: 0 14px; }
}
</style>
