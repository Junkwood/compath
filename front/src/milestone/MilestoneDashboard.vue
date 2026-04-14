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
            <span class="bc-sep">></span>
            <span>{{ projectInfo.projectName }}</span>
            <span class="bc-sep">></span>
            <span class="bc-cur">마일스톤</span>

          </div>
        </div>

        <div class="page-container">
          <!-- 프로젝트 제목 카드 -->
          <div class="pg-row">
            <div class="pg-left">
              <h1 class="pg-title">마일스톤 대시보드</h1>
              <div class="proj-meta">
                <span class="proj-name">【 {{ projectInfo.projectName }} 】</span>
                <span class="proj-period">
                  {{ projectInfo.startDate }} - {{ projectInfo.endDate }}
                </span>
              </div>
            </div>
          </div>

          <!-- 마일스톤 메인 패널 -->
          <div class="panel milestone-page">
            <div class="panel-head milestone-top-head">
              <div class="milestone-header-left">
                <div class="flag-icon-wrap">
                  <div class="flag-pole"></div>
                  <div class="flag-box"></div>
                </div>

                <div class="milestone-title-wrap">
                  <div class="milestone-title-row">
                    <h3 class="panel-title milestone-title">마일스톤</h3>
                  </div>

                  <div class="milestone-guide">
                    <p>마일스톤을 생성해 프로젝트를 개발 단계별로 관리할 수 있습니다.</p>
                    <p>해당 마일스톤을 선택하면 마일스톤 상세 페이지로 이동합니다.</p>
                  </div>
                </div>

                <el-button
                  class="btn-highlight-project milestone-create-btn"
                  @click="handleCreateMilestone"
                >
                  마일스톤 생성
                </el-button>
              
              </div>
            </div>

            <div class="milestone-list-wrap">
              <div
                v-for="item in milestoneList"
                :key="item.milestoneId"
                class="milestone-row"
                @click="goMilestoneDetail(item)"
              >
                <div class="milestone-left">
                  <div class="milestone-name">
                    {{ item.milestoneName }}
                  </div>

                  <div class="milestone-date">
                    종료일 {{ item.dueDate }}
                  </div>

                  <div class="milestone-desc">
                    {{ item.description }}
                  </div>
                </div>

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
import api from "../utils/api";

import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import MilestoneCreateModal from "./MilestoneCreateModal.vue";

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
    const res = await api.get(`/MilestoneListByPid/${projectId}`);
    milestoneList.value = Array.isArray(res.data) ? res.data : [];
  } catch (err) {
    console.error("마일스톤 목록 조회 실패:", err);
    milestoneList.value = [];
  }
};

const fetchProjectDetail = async () => {
  try {
    const projectId = route.params.projectId;
    const res = await api.get(`/ProjectDetail/${projectId}`);
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

/* 프로젝트 제목 카드 */
.pg-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.pg-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.pg-title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #111827;
}

.proj-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.proj-name {
  font-size: 15px;
  font-weight: 700;
  color: #1b5c9c;
}

.proj-period {
  font-size: 13px;
  color: #6b7280;
}

/* 메인 대시보드 공통 버튼 톤 */
.btn-highlight-project {
  background: linear-gradient(135deg, #1b5c9c 0%, #144677 100%) !important;
  color: white !important;
  border: none !important;
  padding: 10px 18px !important;
  height: 40px !important;
  border-radius: 8px !important;
  font-weight: 700 !important;
  box-shadow: 0 4px 14px rgba(27, 92, 156, 0.3) !important;
  transition: all 0.3s ease !important;
}

.btn-highlight-project:hover {
  transform: translateY(-2px);
  filter: brightness(1.08);
}

/* 패널 공통 */
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

/* milestone 메인 박스 */
.milestone-page {
  min-height: 560px;
}

.milestone-top-head {
  align-items: flex-start;
}

.milestone-header-left {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  width: 100%;
}

.flag-icon-wrap {
  position: relative;
  width: 32px;
  height: 38px;
  flex-shrink: 0;
  margin-top: 2px;
}

.flag-pole {
  position: absolute;
  left: 12px;
  top: 0;
  width: 2px;
  height: 32px;
  background: #475569;
  border-radius: 999px;
}

.flag-box {
  position: absolute;
  left: 12px;
  top: 4px;
  width: 20px;
  height: 12px;
  border: 2px solid #475569;
  border-radius: 3px;
  background: #eff6ff;
  clip-path: polygon(0 0, 78% 0, 100% 50%, 78% 100%, 0 100%);
}

.milestone-title-wrap {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
  flex: 1;
}

.milestone-title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.milestone-title {
  margin: 0;
}

.milestone-guide {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.milestone-guide p {
  margin: 0;
  font-size: 13px;
  color: #6b7280;
  line-height: 1.6;
}

/* 리스트 래퍼 */
.milestone-list-wrap {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: #fff;
}

/* 각 마일스톤 카드 */
.milestone-row {
  display: grid;
  grid-template-columns: minmax(260px, 1.2fr) minmax(280px, 1.5fr);
  align-items: center;
  gap: 24px;
  padding: 20px 24px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #fff;
  cursor: pointer;
  transition:
    transform 0.18s ease,
    box-shadow 0.18s ease,
    border-color 0.18s ease;
}

.milestone-row:hover {
  transform: translateY(-2px);
  border-color: #d6e4f2;
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.06);
}

.milestone-left,
.milestone-center {
  min-width: 0;
}

.milestone-name {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
  line-height: 1.4;
  margin-bottom: 8px;
}

.milestone-date {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 28px;
  padding: 0 10px;
  margin-bottom: 14px;
  border-radius: 999px;
  background: #f3f4f6;
  font-size: 12px;
  color: #4b5563;
  font-weight: 700;
}

.milestone-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.65;
  white-space: pre-line;
  word-break: keep-all;
}

/* 진행도 */
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
  color: #1b5c9c;
  font-weight: 800;
}

.progress-sub {
  color: #6b7280;
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
  background: #e5e7eb;
  border-radius: 999px;
  overflow: hidden;
}

.progress-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #1b5c9c 0%, #3b82f6 100%);
  border-radius: 999px;
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
  min-height: 40px;
  padding: 0 14px;
  border-radius: 8px;
  background: #f9fafb;
  border: 1px dashed #d1d5db;
  font-size: 13px;
  color: #6b7280;
}

/* empty */
.empty-box {
  padding: 56px 20px;
  text-align: center;
  font-size: 14px;
  color: #9ca3af;
  border: 1px dashed #d1d5db;
  border-radius: 12px;
  background: #f9fafb;
}

@media (max-width: 1024px) {
  .milestone-row {
    grid-template-columns: 1fr;
    gap: 18px;
    align-items: start;
  }
}

@media (max-width: 768px) {
  .page-container {
    padding: 16px;
    gap: 16px;
  }

  .sub-header {
    padding: 12px 16px;
  }

  .pg-row {
    padding: 16px;
  }

  .milestone-list-wrap {
    padding: 16px;
  }

  .milestone-row {
    padding: 16px;
  }

  .milestone-header-left {
    gap: 12px;
  }

  .milestone-title-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .pg-title {
    font-size: 20px;
  }

  .milestone-name {
    font-size: 16px;
  }
}
</style>