<template>
  <div class="flex h-screen overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden">
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
          <!-- 제목 영역 -->
          <div class="mb-6 proj-title-row">
            <div class="proj-title-left">
              <h2 class="text-2xl md:text-3xl font-bold text-[#000000]">
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

          <!-- 흰 박스 바깥: 마일스톤 탭 카드 -->
          <div class="milestone-outside-tabs">
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
          </div>

          <!-- 흰 박스 -->
          <div class="milestone-detail-page">
            <!-- 박스 안에는 목록 버튼만 -->
            <div class="detail-top-bar">
              <div class="top-btn-left">
                  <el-button class="list-btn" @click="goMilestoneList">
                    ← 목록으로
                  </el-button>
                </div>

                <div class="top-btn-right">
                  <el-button class="setting-btn" @click="openEditModal">
                    ⚙ 마일스톤 수정
                  </el-button>
                  <el-button
                      class="delete-milestone-btn"
                      @click.stop="handleDeleteMilestone"
                    >
                      삭제
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
                        <td>{{ item.userName }}</td>
                      </tr>
                      <tr v-if="currentSubProjectList.length === 0">
                        <td colspan="3" class="empty-cell">
                          등록된 하위 프로젝트가 없습니다.
                        </td>
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
                            <span class="mini-progress-text">
                              {{ Number(task.progressRate) }}%
                            </span>
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
    <MilestoneCreateModal
      v-model="editModalVisible"
      :project-id="route.params.projectId"
      :project-name="projectInfo.projectName"
      :is-edit-mode="true"
      :milestone-data="editMilestoneData"
      @saved="handleMilestoneUpdated"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import api from "../utils/api.js";
import MilestoneCreateModal from "../milestone/MilestoneCreateModal.vue";
import Swal from "sweetalert2";
import { useAuthStore } from "../stores/auth";

const route = useRoute();
const router = useRouter();
const sidebarOpen = ref(false);
const editModalVisible = ref(false);
const editMilestoneData = ref(null);

// 상단 프로젝트명
const projectInfo = ref({
  projectId: null,
  projectName: "",
  startDate: "",
  endDate: "",
});

const fetchProjectDetail = async () => {
  try {
    const projectId = route.params.projectId;
    const res = await api.get(`/ProjectDetail/${projectId}`);
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
    const res = await api.get(`/MilestoneTab/${projectId}`);
    milestoneList.value = res.data;
  } catch (err) {
    console.error("마일스톤 목록 조회 실패:", err);
    milestoneList.value = [];
  }
};

// 현재 선택된 milestoneId
const selectedMilestoneId = computed(() => Number(route.params.milestoneId));
const currentProjectId = computed(() => Number(route.params.projectId));

// 하위프로젝트 리스트
const subProjectList = ref([]);

const fetchSubProjectList = async () => {
  try {
    const projectId = route.params.projectId;
    const milestoneId = route.params.milestoneId;

    const res = await api.get(
      `/MilestoneSubProjects/${projectId}/${milestoneId}`
    );

    subProjectList.value = Array.isArray(res.data) ? res.data : [];
  } catch (err) {
    console.error("하위 프로젝트 목록 조회 실패:", err);
    subProjectList.value = [];
  }
};

const currentSubProjectList = computed(() => subProjectList.value);
const currentTaskList = ref([]);

// 마일스톤은 있고 하위프로젝트는 없는 개별업무 조회
const fetchTaskWnoSubP = async () => {
  try {
    const projectId = route.params.projectId;
    const milestoneId = route.params.milestoneId;

    const res = await api.get(
      `/MilestoneWnoSubPidList/${projectId}/${milestoneId}`
    );
    currentTaskList.value = res.data;
  } catch (err) {
    console.error("하위프로젝트 없는 개별업무 조회 실패 : ", err);
    currentTaskList.value = [];
  }
};

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

const openEditModal = () => {
  const selected = milestoneList.value.find(
    (item) => Number(item.milestoneId) === Number(selectedMilestoneId.value)
  );

  if (!selected) return;

  editMilestoneData.value = {
    milestoneId: selected.milestoneId,
    milestoneName: selected.milestoneName,
    startDate: selected.startDate,
    dueDate: selected.dueDate,
    description: selected.description ?? "",
  };

  editModalVisible.value = true;
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

//마일스톤 삭제 (상태값 업데이트)
const handleDeleteMilestone = async () => {
  try {
    const result = await Swal.fire({
      icon: "warning",
      title: "마일스톤을 삭제할까요?",
      text: "하위 프로젝트나 업무가 없을 때만 삭제할 수 있습니다.",
      showCancelButton: true,
      confirmButtonText: "삭제",
      cancelButtonText: "취소",
      reverseButtons: true,
    });

    if (!result.isConfirmed) return;

    await api.put(
      `/MilestoneDelete/${route.params.projectId}/${route.params.milestoneId}`,
      {editUserId : useAuthStore.user?.userId,}
    );

    await Swal.fire({
      icon: "success",
      title: "마일스톤이 삭제되었습니다.",
      confirmButtonText: "확인",
    });

    router.push({
        name: "milestoneDashboard",
        params: {
          projectId: Number(route.params.projectId),
        },
      });
  } catch (err) {
    console.error("마일스톤 삭제 실패:", err);

     const message =
        err?.response?.data?.message ||
        err?.response?.data ||
        "하위 프로젝트 또는 업무가 존재하여 삭제할 수 없습니다.";

      await Swal.fire({
        icon: "error",
        title: "삭제 실패",
        text: message,
        confirmButtonText: "확인",
      });
    }
  };

const handleMilestoneUpdated = async () => {
  editModalVisible.value = false;
  await fetchMlistTab();
  await fetchSubProjectList();
  await fetchTaskWnoSubP();
};

onMounted(async () => {
  await fetchProjectDetail();
  await fetchMlistTab();
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

<style>
:root {
  --ink-900: #1a2d4a;
  --ink-600: #3d5472;
  --ink-400: #7a95b0;
  --ink-200: #b8cede;

  --surface: #ffffff;
  --surface-2: #f0f5fb;
  --surface-3: #e2ecf7;

  --border: #cddcee;
  --border-str: #b3c9e3;

  --accent: #2253b0;
  --accent-dim: #deeaf8;
  --accent-mid: #7aaad9;

  --r-xs: 4px;
  --r-sm: 6px;
  --r-md: 10px;
  --r-lg: 16px;
}

/* 제목 */
.proj-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
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
  gap: 10px;
  flex-wrap: wrap;
}
.proj-name {
  font-size: 15px;
  font-weight: 700;
  color: var(--ink-900);
  letter-spacing: -0.025em;
}
.proj-period {
  font-size: 12px;
  color: var(--ink-400);
  font-weight: 500;
  padding: 3px 10px;
  border: 1px solid var(--border-str);
  border-radius: var(--r-xs);
  background: var(--surface-2);
  letter-spacing: 0.02em;
  font-variant-numeric: tabular-nums;
}

/* 흰 박스 바깥 탭 */
.milestone-outside-tabs {
  margin-bottom: 14px;
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
  gap: 11px;
  min-width: 205px;
  padding: 12px 15px;
  border-radius: var(--r-md);
  cursor: pointer;
  transition: border-color 0.14s, background 0.14s, box-shadow 0.14s;
  border: 1px solid var(--border);
  background: var(--surface);
}

.top-milestone-item:hover {
  border-color: var(--border-str);
  border-left-color: var(--accent-mid);
  background: var(--surface-2);
}

.top-milestone-item.active {
  border-color: var(--border-str);
  background: var(--accent-dim);
}

/* 깃발 */
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
  width: 1.5px;
  height: 30px;
  background: var(--ink-200);
  border-radius: 999px;
  transition: background 0.14s;
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
  background: var(--surface-3);
  border-radius: 1px;
  clip-path: polygon(0 0, 70% 0, 100% 50%, 70% 100%, 0 100%);
  transition: background 0.14s;
}
.top-milestone-item.active .flag-box {
  background: var(--accent);
}

.top-milestone-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}
.top-milestone-name {
  font-size: 13.5px;
  font-weight: 700;
  color: var(--ink-900);
  line-height: 1.3;
  letter-spacing: -0.015em;
  transition: color 0.14s;
}
.top-milestone-item.active .top-milestone-name {
  color: var(--accent);
}
.top-milestone-date {
  font-size: 11px;
  color: var(--ink-400);
  font-weight: 500;
  font-variant-numeric: tabular-nums;
  letter-spacing: 0.01em;
}

/* 흰 박스 */
.milestone-detail-page {
  background: var(--surface);
  border: none;
  border-radius: 10px;
  box-shadow: none;
  padding: 30px 28px 42px;
  min-height: 720px;
}

/* 박스 안 상단 바 */
.detail-top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 26px;
  padding-bottom: 18px;
}

.top-btn-wrap {
  display: flex;
  align-items: center;
}

/* 버튼 */
.list-btn {
  height: 34px !important;
  padding: 0 14px !important;
  border: 1px solid var(--border-str) !important;
  border-radius: var(--r-sm) !important;
  background: var(--surface) !important;
  color: var(--ink-600) !important;
  font-size: 12.5px !important;
  font-weight: 600 !important;
  letter-spacing: 0.01em !important;
  box-shadow: none !important;
  transition: border-color 0.14s, color 0.14s, background 0.14s !important;
}
.list-btn:hover {
  border-color: var(--accent) !important;
  color: var(--accent) !important;
  background: var(--accent-dim) !important;
}

/* 섹션 */
.detail-section {
  margin-bottom: 18px;
}
.section-card {
  border: 1px solid var(--border-str);
  border-radius: var(--r-md);
  background: var(--surface);
  overflow: hidden;
}
.section-header {
  min-height: 46px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background: var(--surface-2);
  border-bottom: 1px solid var(--border);
  font-size: 15px;
  color: var(--ink-900);
  letter-spacing: 0.02em;
  font-weight: 700;
}

/* 테이블 */
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
  border-bottom: 1px solid var(--border);
  padding: 0 18px;
  height: 50px;
  font-size: 13px;
  color: #1a1b1c;
  vertical-align: middle;
  text-align: left;
}
.detail-table tr:last-child td {
  border-bottom: none;
}
.detail-table th {
  background: var(--surface-2);
  color: #1a1b1c;
  font-size: 13px;
  letter-spacing: 0.02em;
  font-weight: 700;
  height: 40px;
}

/* 하위 프로젝트 테이블 */
.subproject-table td {
  height: 52px;
}
.subproject-table td:nth-child(1) {
  width: 36%;
  color: #1a1b1c;
}
.subproject-table td:nth-child(2) {
  width: 38%;
  color: #1a1b1c;
  font-variant-numeric: tabular-nums;
}
.subproject-table td:nth-child(3) {
  width: 26%;
  color: #1a1b1c;
}

/* 업무 테이블 */
.task-table th:nth-child(1),
.task-table td:nth-child(1) {
  width: 28%;
}
.task-table th:nth-child(2),
.task-table td:nth-child(2) {
  width: 12%;
}
.task-table th:nth-child(3),
.task-table td:nth-child(3) {
  width: 10%;
}
.task-table th:nth-child(4),
.task-table td:nth-child(4) {
  width: 10%;
}
.task-table th:nth-child(5),
.task-table td:nth-child(5) {
  width: 18%;
}
.task-table th:nth-child(6),
.task-table td:nth-child(6) {
  width: 11%;
}
.task-table th:nth-child(7),
.task-table td:nth-child(7) {
  width: 11%;
}
.task-table td:nth-child(1) {
  color: #1a1b1c;
}

/* 진행률 */
.progress-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}
.mini-progress-bar {
  width: 84px;
  height: 4px;
  background: var(--surface-3);
  border-radius: 999px;
  overflow: hidden;
}
.mini-progress-fill {
  height: 100%;
  background: var(--accent);
  border-radius: 999px;
  transition: width 0.4s ease;
}
.mini-progress-text {
  font-size: 11.5px;
  font-weight: 700;
  color: var(--ink-600);
  font-variant-numeric: tabular-nums;
  min-width: 32px;
  letter-spacing: 0.01em;
}
.not-started-text {
  font-size: 13px;
  color: var(--ink-200);
}
.empty-cell {
  text-align: center;
  color: var(--ink-400);
  font-size: 13px;
  padding: 36px 16px !important;
  height: auto !important;
  letter-spacing: 0.01em;
}

/* hover */
.clickable-row {
  cursor: pointer;
  transition: background 0.1s;
}
.clickable-row:hover td {
  background: var(--surface-2);
}

/* 반응형 */
@media (max-width: 1024px) {
  .top-milestone-item {
    min-width: 190px;
  }
}

@media (max-width: 768px) {
  .proj-title-row {
    flex-direction: column;
    gap: 6px;
  }
  .proj-name-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  .proj-name {
    font-size: 13.5px;
  }
  .milestone-detail-page {
    padding: 18px 14px 24px;
  }
  .top-milestone-item {
    width: 100%;
    min-width: 100%;
  }
  .detail-table td,
  .detail-table th {
    font-size: 12px;
    padding: 0 10px;
  }
  .section-header {
    min-height: 42px;
    padding: 0 14px;
  }
}
</style>