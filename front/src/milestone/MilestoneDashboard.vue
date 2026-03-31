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
                    <el-button class="create-milestone-btn" @click="handleCreateMilestone">
                      + 마일스톤 생성
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
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";

const route = useRoute();
const router = useRouter();
const sidebarOpen = ref(false);

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

    // 백엔드 연결 전 임시값
    projectInfo.value = {
      projectId: route.params.projectId,
      projectName: "삼성라이온즈 베리즈샵 구축",
      startDate: "2026/03/19",
      endDate: "2026/12/24",
    };
  }
};


const handleCreateMilestone = () => {
  router.push({
    name: "milestoneCreate",
    params: { projectId: route.params.projectId },
  });
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

const getStatusClass = (statusName) => {
  if (statusName === "완료") return "status-done";
  if (statusName === "진행중") return "status-progress";
  if (statusName === "시작 전") return "status-wait";
  return "status-default";
};

onMounted(() => {
  fetchProjectDetail();
  fetchMilestoneList();
});
</script>

<style scoped>
/* ────────────────────────────────────────────
   projectDashboard.vue와 동일한 제목 영역
──────────────────────────────────────────── */
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
  gap: 12px;
  flex-wrap: wrap;
}

.proj-name {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
}

.proj-period {
  font-size: 13px;
  color: #64748b;
}

/* ────────────────────────────────────────────
   마일스톤 페이지 전체
──────────────────────────────────────────── */
.milestone-page {
  background: #ffffff;
  border: 1px solid #d1d5db;
  border-radius: 16px;
  box-shadow: 0 2px 10px rgba(15, 23, 42, 0.04);
  overflow: hidden;
  min-height: 720px;
}

/* ────────────────────────────────────────────
   마일스톤 상단 헤더
──────────────────────────────────────────── */
.milestone-header {
  padding: 24px 28px 18px;
  border-bottom: 1px solid #e5e7eb;
  background: linear-gradient(to bottom, #ffffff, #fcfdff);
}

.milestone-header-left {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.flag-icon-wrap {
  position: relative;
  width: 30px;
  height: 38px;
  flex-shrink: 0;
  margin-top: 2px;
}

.flag-pole {
  position: absolute;
  left: 12px;
  top: 0;
  width: 2px;
  height: 34px;
  background: #111827;
  border-radius: 999px;
}

.flag-box {
  position: absolute;
  left: 12px;
  top: 4px;
  width: 24px;
  height: 13px;
  border: 2px solid #111827;
  border-radius: 2px;
  background: #fff;
  clip-path: polygon(0 0, 76% 0, 100% 50%, 76% 100%, 0 100%);
}

.milestone-title-wrap {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.milestone-title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.milestone-title {
  font-size: 22px;
  font-weight: 800;
  color: #1f2937;
  margin: 0;
  line-height: 1.2;
  letter-spacing: -0.02em;
}

.create-milestone-btn {
  height: 34px;
  padding: 0 14px;
  border: none;
  border-radius: 10px;
  background: #4f9cf9;
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  box-shadow: 0 4px 10px rgba(79, 156, 249, 0.18);
}

.create-milestone-btn:hover {
  background: #3d8cf0;
}

.milestone-guide {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.milestone-guide p {
  margin: 0;
  font-size: 13px;
  color: #6b7280;
  line-height: 1.55;
}

/* ────────────────────────────────────────────
   목록 영역
──────────────────────────────────────────── */
.milestone-list-wrap {
  margin: 18px;
  border: 1px solid #d1d5db;
  border-radius: 14px;
  overflow: hidden;
  background: #ffffff;
}

.milestone-row {
  display: grid;
  grid-template-columns: minmax(280px, 1.4fr) minmax(300px, 2fr) 110px;
  align-items: center;
  gap: 24px;
  padding: 22px 24px;
  border-bottom: 1px solid #e5e7eb;
  cursor: pointer;
  transition: background 0.16s ease;
}

.milestone-row:last-child {
  border-bottom: none;
}

.milestone-row:hover {
  background: #f5f9ff;
}

.milestone-left,
.milestone-center {
  min-width: 0;
}

.milestone-name {
  font-size: 25px;
  font-weight: 800;
  color: #111827;
  line-height: 1.3;
  margin-bottom: 8px;
  letter-spacing: -0.02em;
}

.milestone-date {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 14px;
  font-weight: 500;
}

.milestone-date span {
  color: #475569;
}

.milestone-desc {
  font-size: 13px;
  color: #475569;
  line-height: 1.6;
  white-space: pre-line;
  word-break: keep-all;
}

/* ────────────────────────────────────────────
   진행률
──────────────────────────────────────────── */
.progress-text {
  font-size: 12px;
  margin-bottom: 10px;
  line-height: 1.5;
}

.progress-done {
  color: #3b82f6;
  font-weight: 700;
}

.progress-sub {
  color: #64748b;
  font-weight: 600;
  margin-left: 4px;
}

.progress-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-bar-bg {
  flex: 1;
  height: 18px;
  background: #eceff3;
  border-radius: 999px;
  overflow: hidden;
}

.progress-bar-fill {
  height: 100%;
  background: #bedfb8;
  border-radius: 999px;
  transition: width 0.25s ease;
}

.progress-rate {
  width: 32px;
  text-align: right;
  font-size: 12px;
  font-weight: 700;
  color: #475569;
}

.no-task-text {
  font-size: 13px;
  color: #6b7280;
  text-align: center;
  padding: 8px 0;
}

/* ────────────────────────────────────────────
   상태 뱃지
──────────────────────────────────────────── */
.milestone-right {
  display: flex;
  justify-content: flex-end;
}

.status-badge {
  min-width: 86px;
  height: 40px;
  padding: 0 18px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 800;
  color: #fff;
  letter-spacing: -0.01em;
}

.status-done {
  background: #666666;
}

.status-progress {
  background: #2f7d38;
}

.status-wait {
  background: #9ca3af;
}

.status-default {
  background: #6b7280;
}

.empty-box {
  padding: 48px 20px;
  text-align: center;
  font-size: 14px;
  color: #94a3b8;
  background: #ffffff;
}

/* ────────────────────────────────────────────
   반응형
──────────────────────────────────────────── */
@media (max-width: 1024px) {
  .milestone-row {
    grid-template-columns: 1fr;
    gap: 16px;
    align-items: start;
  }

  .milestone-right {
    justify-content: flex-start;
  }

  .milestone-name {
    font-size: 21px;
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
    padding: 18px 16px 14px;
  }

  .milestone-list-wrap {
    margin: 12px;
  }

  .milestone-row {
    padding: 16px;
  }

  .milestone-title {
    font-size: 18px;
  }

  .milestone-name {
    font-size: 18px;
  }

  .status-badge {
    min-width: 78px;
    height: 36px;
    font-size: 14px;
  }
}
</style>