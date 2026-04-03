<template>
  <div class="flex h-screen overflow-hidden">
    <!-- Sidebar -->
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <!-- Content area -->
    <div class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden">
      <!-- Site header -->
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
          <!-- projectDashboard.vue와 동일한 제목 영역 -->
          <div class="mb-6 proj-title-row">
            <div class="proj-title-left">
              <h2
                class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
              >
                프로젝트 대시보드
              </h2>
              <div class="proj-name-row">
                <span class="proj-name">【 {{ projectInfo.projectName }} 】</span>
                <span class="proj-period">
                  {{ projectInfo.startDate }} - {{ projectInfo.endDate }}
                </span>
              </div>
            </div>
          </div>

          <!-- 마일스톤 페이지 -->
          <div class="milestone-page">
            <!-- 상단 마일스톤 헤더 -->
            <div class="milestone-header">
              <div class="milestone-header-left">
                <div class="flag-icon-wrap">
                  <div class="flag-pole"></div>
                  <div class="flag-box"></div>
                </div>

                <div class="milestone-title-wrap">
                  <div class="milestone-title-row">
                    <h3 class="milestone-title">마일스톤</h3>
                    <el-button @click="handleCreateMilestone">
                      마일스톤 생성
                    </el-button>

                  </div>

                  <div class="milestone-guide">
                    <p>마일스톤을 생성해 프로젝트를 개발 단계별로 관리할 수 있습니다.</p>
                    <p>해당 마일스톤을 선택하면 마일스톤 상세 페이지로 이동합니다.</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- 마일스톤 목록 -->
            <div class="milestone-list-wrap">
              <div
                v-for="item in milestoneList"
                :key="item.milestoneId"
                class="milestone-row"
                @click="goMilestoneDetail(item)"
              >
                <!-- 왼쪽 -->
                <div class="milestone-left">
                  <div class="milestone-name">
                    {{ item.milestoneName }} 
                  </div>

                  <div class="milestone-date">
                    종료일 {{ item.dueDate }}
                    <!-- <span v-if="item.dDayText">({{ item.dDayText }})</span> -->
                  </div>

                  <div class="milestone-desc">
                    {{ item.description }}
                  </div>
                </div>

                <!-- 가운데 -->
                <div class="milestone-center">
                  <template v-if="item.totalTasks > 0">
                    <div class="progress-text">
                      <span class="progress-done">{{ item.completedTasks }} 완료</span>
                      <span class="progress-sub">
                        (총 {{ item.totalTasks }}건 — {{ item.inprogressTasks }} 건 진행 중)
                      </span>
                    </div>

                    <div class="progress-row">
                      <div class="progress-bar-bg">
                        <div
                          class="progress-bar-fill"
                          :style="{ width: item.progressRate + '%' }"
                        ></div>
                      </div>
                      <div class="progress-rate">{{ item.progressRate }}%</div>
                    </div>
                  </template>

                  <template v-else>
                    <div class="no-task-text">
                      해당 마일스톤에 등록 된 업무가 존재하지 않음
                    </div>
                  </template>
                </div>

                <!-- 오른쪽 -->
                <!-- <div class="milestone-right">
                  <span
                    class="status-badge"
                    :class="getStatusClass(item.statusName)"
                  >
                    {{ item.statusName }}
                  </span>
                </div> -->
              </div>

              <div v-if="milestoneList.length === 0" class="empty-box">
                등록된 마일스톤이 없습니다.
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
    <MilestoneCreateModal
        v-model="createModalVisible"
        :project-id="route.params.projectId"
        :project-name="projectInfo.projectName"
        @saved="handleMilestoneSaved"
      />
    </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import MilestoneCreateModal from "./MilestoneCreateModal.vue";
import Swal from "sweetalert2";

const route = useRoute();
const router = useRouter();
const sidebarOpen = ref(false);
const createModalVisible = ref(false);

// 프로젝트 정보
const projectInfo = ref({
  projectId: null,
  projectName: "",
  startDate: "",
  endDate: "",
});

// 마일스톤 목록
const milestoneList = ref([]);

const fetchMilestoneList = async () => {
  try {
    const projectId = route.params.projectId;
    const res = await axios.get(`/api/MilestoneListByPid/${projectId}`);

    milestoneList.value = Array.isArray(res.data) ? res.data : [];
  } catch (err) {
    console.error("마일스톤 목록 조회 실패:", err);
    milestoneList.value = [];
  }
};

const fetchProjectDetail = async () => {
  try {
    const projectId = route.params.projectId;
    const res = await axios.get(`/api/ProjectDetail/${projectId}`);
    projectInfo.value = res.data;
  } catch (err) {
    console.error("프로젝트 상세 조회 실패:", err);
  }
};

// 마일스톤 생성 모달
const handleCreateMilestone = () => {
  createModalVisible.value = true;
};

const handleMilestoneSaved = async () => {
  createModalVisible.value = false;
  await fetchMilestoneList();
};

const goMilestoneDetail = (item) => {
  router.push({
    name: "milestoneDetail",
    params: {
      projectId: route.params.projectId,
      milestoneId: item.milestoneId,
    },
  });
};



onMounted(() => {
  fetchProjectDetail();
  fetchMilestoneList();
});
</script>

<style scoped>
/* =========================
   Title Area
========================= */
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
  gap: 8px;
}

.proj-name-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.proj-name {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
  letter-spacing: -0.02em;
}

.proj-period {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

/* =========================
   Page Shell
========================= */
.milestone-page {
  background:
    radial-gradient(circle at top right, rgba(99, 102, 241, 0.06), transparent 28%),
    linear-gradient(to bottom, #ffffff, #fbfcff);
  border: 1px solid #e5e7eb;
  border-radius: 24px;
  box-shadow:
    0 12px 32px rgba(15, 23, 42, 0.06),
    0 2px 8px rgba(15, 23, 42, 0.03);
  overflow: hidden;
  min-height: 720px;
}

/* =========================
   Header
========================= */
.milestone-header {
  padding: 30px 32px 22px;
  border-bottom: 1px solid #eef2f7;
  background: transparent;
}

.milestone-header-left {
  display: flex;
  align-items: flex-start;
  gap: 18px;
}

.flag-icon-wrap {
  position: relative;
  width: 34px;
  height: 40px;
  flex-shrink: 0;
  margin-top: 2px;
}

.flag-pole {
  position: absolute;
  left: 13px;
  top: 0;
  width: 2px;
  height: 34px;
  background: #334155;
  border-radius: 999px;
}

.flag-box {
  position: absolute;
  left: 13px;
  top: 4px;
  width: 22px;
  height: 13px;
  border: 2px solid #334155;
  border-radius: 3px;
  background: #eef2ff;
  clip-path: polygon(0 0, 78% 0, 100% 50%, 78% 100%, 0 100%);
}

.milestone-title-wrap {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
}

.milestone-title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.milestone-title {
  font-size: 24px;
  font-weight: 800;
  color: #0f172a;
  margin: 0;
  line-height: 1.2;
  letter-spacing: -0.03em;
}

.create-milestone-btn {
  height: 38px;
  padding: 0 16px;
  border: 1px solid transparent;
  border-radius: 12px;
  background: #2563eb;
  color: #ffffff;
  font-size: 13px;
  font-weight: 700;
}

.create-milestone-btn:hover {
  background: linear-gradient(135deg, #4338ca, #2563eb);
  transform: translateY(-1px);
}

.milestone-guide {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.milestone-guide p {
  margin: 0;
  font-size: 13px;
  color: #64748b;
  line-height: 1.6;
}

/* =========================
   List Wrap
========================= */
.milestone-list-wrap {
  margin: 20px;
  padding: 10px;
  border: 1px solid #eef2f7;
  border-radius: 20px;
  background: rgba(248, 250, 252, 0.72);
  backdrop-filter: blur(8px);
}

/* =========================
   Item Row
========================= */
.milestone-row {
  display: grid;
  grid-template-columns: minmax(260px, 1.35fr) minmax(280px, 1.8fr);
  align-items: center;
  gap: 28px;
  padding: 22px 24px;
  margin-bottom: 10px;
  border: 1px solid #edf2f7;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.92);
  cursor: pointer;
  transition:
    transform 0.18s ease,
    box-shadow 0.18s ease,
    border-color 0.18s ease,
    background-color 0.18s ease;
}

.milestone-row:last-child {
  margin-bottom: 0;
}

.milestone-row:hover {
  transform: translateY(-2px);
  border-color: #dbe4ff;
  box-shadow:
    0 10px 24px rgba(15, 23, 42, 0.07),
    0 2px 8px rgba(59, 130, 246, 0.08);
  background: #ffffff;
}

.milestone-left,
.milestone-center {
  min-width: 0;
}

/* =========================
   Left Content
========================= */
.milestone-name {
  font-size: 20px;
  font-weight: 750;
  color: #0f172a;
  line-height: 1.35;
  margin-bottom: 8px;
  letter-spacing: -0.02em;
}

.milestone-date {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 28px;
  padding: 0 10px;
  margin-bottom: 14px;
  border-radius: 999px;
  background: #f1f5f9;
  font-size: 12px;
  color: #475569;
  font-weight: 700;
}

.milestone-date span {
  color: #64748b;
}

.milestone-desc {
  font-size: 13px;
  color: #64748b;
  line-height: 1.65;
  white-space: pre-line;
  word-break: keep-all;
}

/* =========================
   Progress
========================= */
.progress-text {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  margin-bottom: 10px;
  line-height: 1.5;
}

.progress-done {
  color: #2563eb;
  font-weight: 800;
}

.progress-sub {
  color: #64748b;
  font-weight: 600;
}

.progress-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-bar-bg {
  position: relative;
  flex: 1;
  height: 12px;
  background: #e9eef5;
  border-radius: 999px;
  overflow: hidden;
}

.progress-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #6366f1 0%, #60a5fa 100%);
  border-radius: 999px;
  box-shadow: 0 2px 8px rgba(99, 102, 241, 0.25);
  transition: width 0.3s ease;
}

.progress-rate {
  min-width: 42px;
  text-align: right;
  font-size: 13px;
  font-weight: 800;
  color: #334155;
}

.no-task-text {
  display: inline-flex;
  align-items: center;
  min-height: 42px;
  padding: 0 14px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px dashed #dbe2ea;
  font-size: 13px;
  color: #64748b;
}

/* =========================
   Empty
========================= */
.empty-box {
  padding: 56px 20px;
  text-align: center;
  font-size: 14px;
  color: #94a3b8;
  background: transparent;
}

/* =========================
   Responsive
========================= */
@media (max-width: 1024px) {
  .milestone-row {
    grid-template-columns: 1fr;
    gap: 18px;
    align-items: start;
  }

  .milestone-name {
    font-size: 18px;
  }
}

@media (max-width: 768px) {
  .proj-title-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .proj-name-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .proj-name {
    font-size: 15px;
  }

  .milestone-header {
    padding: 20px 18px 16px;
  }

  .milestone-list-wrap {
    margin: 12px;
    padding: 8px;
    border-radius: 16px;
  }

  .milestone-row {
    padding: 18px 16px;
    border-radius: 16px;
  }

  .milestone-title {
    font-size: 20px;
  }

  .milestone-name {
    font-size: 17px;
  }

  .create-milestone-btn {
    height: 36px;
    font-size: 12px;
  }
}

</style>