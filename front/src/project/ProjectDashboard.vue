<!-- project dashboard -->
<template>
<div class="dashboard-page flex h-screen overflow-hidden">
      <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

<div class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden bg-gray-50">
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <!-- 서브 헤더 -->
        <div class="sub-header">
          <div class="breadcrumb">
            <span>홈</span>
            <span class="bc-sep">›</span>
            <span>프로젝트</span>
            <span class="bc-sep">›</span>
            <span class="bc-cur">{{ projectInfo.projectName }}</span>
          </div>
        </div>

        <div class="page-container">
          <!-- 페이지 타이틀 -->
          <div class="pg-row">
            <div class="pg-left">
              <h1 class="pg-title">프로젝트 대시보드</h1>
              <div class="proj-meta">
                <span class="proj-name">{{ projectInfo.projectName }}</span>
                <span class="proj-period"
                  >{{ projectInfo.startDate }} ~ {{ projectInfo.endDate }}</span
                >
              </div>
            </div>
            <el-button class="btn-setting" @click="handleProjectSetting">
              ⚙ 프로젝트 설정
            </el-button>
          </div>

          <!-- 상단 2컬럼 -->
          <div class="top-grid">
            <!-- 좌측 -->
            <div class="left-col">
              <!-- 업무 현황 -->
              <div class="panel">
                <div class="panel-head">
                  <span class="panel-title">업무 현황</span>
                  <el-button class="btn-sub" @click="handleViewTasks"
                    >업무 목록 보기</el-button
                  >
                </div>
                <el-table
                  :data="taskSummaryData"
                  style="width: 100%"
                  :header-cell-style="headerStyle"
                  :cell-style="cellStyle"
                >
                  <el-table-column prop="type" label="유형" min-width="90" />
                  <el-table-column
                    prop="total"
                    label="전체"
                    min-width="70"
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
                    min-width="70"
                    align="center"
                  />
                  <el-table-column
                    prop="rejected"
                    label="반려"
                    min-width="70"
                    align="center"
                  />
                  <el-table-column
                    prop="totalSum"
                    label="합계"
                    min-width="70"
                    align="center"
                  />
                </el-table>
              </div>

              <!-- 공지사항 -->
              <div class="panel">
                <div class="panel-head">
                  <span class="panel-title">공지사항</span>
                </div>
                <div class="notice-body">
                  <ul v-if="noticeList.length > 0" class="notice-list">
                    <li
                      v-for="item in noticeList"
                      :key="item.noticeId"
                      class="notice-item"
                      @click="handleNoticeClick(item)"
                    >
                      <div class="notice-left">
                        <span class="notice-title">{{ item.title }}</span>
                        <span
                          v-if="item.pinStatusCode === 'B1'"
                          class="badge-pin"
                          >고정</span
                        >
                        <span v-if="item.isNew" class="badge-new">NEW</span>
                      </div>
                      <span class="notice-date">{{ item.createdAt }}</span>
                    </li>
                  </ul>
                  <div v-else class="empty-text">
                    등록된 공지사항이 없습니다.
                  </div>
                </div>
              </div>

              <!-- 하위 프로젝트 -->
              <div class="panel">
                <div class="panel-head">
                  <span class="panel-title">하위 프로젝트 목록</span>
                  <el-button class="btn-sub" @click="handleAddSubProject"
                    >+ 하위 프로젝트 생성</el-button
                  >
                </div>
                <div class="sub-body">
                  <template v-if="currentMilestone">
                    <div class="sub-milestone-title">
                      마일스톤 {{ currentMilestone?.milestoneName }}
                    </div>
                    <el-table
                      :data="currentMilestone.projects || []"
                      style="width: 100%"
                      :show-header="false"
                      :cell-style="cellStyle"
                      @row-click="handleSubProjectRowClick"
                    >
                      <el-table-column prop="projectName" min-width="220" />
                      <el-table-column label="PL" width="140" align="right">
                        <template #default="{ row }">
                          <span class="sub-pl">PL {{ row.userName }}</span>
                        </template>
                      </el-table-column>
                    </el-table>
                    <div class="pag-wrap" v-if="pagedMilestones.length > 1">
                      <el-pagination
                        v-model:current-page="milestonePage"
                        :page-size="1"
                        :total="pagedMilestones.length"
                        layout="prev, pager, next"
                        background
                      />
                    </div>
                  </template>
                  <div v-else class="empty-text">하위 프로젝트가 없습니다.</div>
                </div>
              </div>
            </div>

            <!-- 우측 -->
            <div class="right-col">
              <!-- 프로젝트 구성원 -->
              <div class="panel">
                <div class="panel-head">
                  <span class="panel-title">프로젝트 구성원</span>
                  <span class="member-count-badge"
                    >{{ projectMembers.length }}명</span
                  >
                </div>
                <div class="member-body">
                  <template v-if="projectMembers.length > 0">
                    <div
                      v-for="member in projectMembers"
                      :key="member.userId"
                      class="member-item"
                    >
                      <div
                        class="member-avatar"
                        :style="{
                          backgroundColor: getAvatarColor(member.roleName),
                        }"
                      >
                        {{ member.userName?.charAt(0) }}
                      </div>
                      <div class="member-info">
                        <span class="member-name">{{ member.userName }}</span>
                        <span
                          class="member-role"
                          :class="getRoleClass(member.roleName)"
                          >{{ member.roleName }}</span
                        >
                      </div>
                    </div>
                  </template>
                  <div v-else class="empty-text">
                    구성원이 아직 지정되지 않았습니다.
                  </div>
                </div>
              </div>

              <!-- 나의 메모 -->
              <div class="panel">
                <div class="panel-head">
                  <span class="panel-title">나의 메모</span>
                  <el-button class="btn-memo-add" @click="handleAddMemo" circle
                    >+</el-button
                  >
                </div>
                <div class="memo-body">
                  <template v-if="memoList.length > 0">
                    <div
                      v-for="(memo, index) in memoList"
                      :key="memo.memoId"
                      class="memo-card"
                      :class="getMemoColorClass(index)"
                      @dblclick="handleEditMemo(memo)"
                    >
                      <div class="memo-content">
                        <div class="memo-date">{{ memo.createdAt }}</div>
                        <div class="memo-text">{{ memo.memoContent }}</div>
                      </div>
                      <el-button
                        class="memo-del"
                        @click="handleDeleteMemo(memo.memoId)"
                        text
                        >✕</el-button
                      >
                    </div>
                  </template>
                  <div v-else class="empty-text">등록된 메모가 없습니다.</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>

  <ProjectMemoModal
    v-model="memoModalVisible"
    :initialMemoText="editingMemoText"
    :isEditMode="isMemoEditMode"
    @submitted="handleMemoSubmitted"
  />

  <ProjectSubCreateModal
    v-model="createSubProjectModalOpen"
    :project-id="projectInfo.projectId"
    :parent-project-name="projectInfo.projectName"
    :parent-start-date="projectInfo.startDate"
    :parent-end-date="projectInfo.endDate"
  />
</template>

<script setup>
import { onMounted, ref, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import Swal from "sweetalert2";
import api from "../utils/api";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import ProjectMemoModal from "../project/ProjectMemoModal.vue";
import { useAuthStore } from "../stores/auth";
import ProjectSubCreateModal from "../project/ProjectSubCreateModal.vue";

const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();
const sidebarOpen = ref(false);
const createSubProjectModalOpen = ref(false);

const handleAddSubProject = () => {
  createSubProjectModalOpen.value = true;
};

// ── 업무 현황
const taskSummaryData = ref([]);
const fetchTaskSummary = async () => {
  try {
    const res = await api.get(`/TaskSummary/${route.params.projectId}`);
    taskSummaryData.value = res.data;
  } catch (err) {
    console.error("업무 현황 조회 실패:", err);
    taskSummaryData.value = [];
  }
};

// ── 공지사항
const noticeList = ref([]);
const formatDate = (value) => {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value).slice(0, 10);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
};
const isWithin7Days = (value) => {
  if (!value) return false;
  const target = new Date(value);
  if (Number.isNaN(target.getTime())) return false;
  const diff =
    (new Date().getTime() - target.getTime()) / (1000 * 60 * 60 * 24);
  return diff >= 0 && diff <= 7;
};
const fetchNoticeList = async () => {
  try {
    const res = await api.get(
      `/notices/getNoticeLists/${Number(route.params.projectId)}`,
    );
    const rawList = Array.isArray(res.data) ? res.data : [];
    noticeList.value = rawList
      .map((item, index) => ({
        noticeId: item.noticeId ?? item.id ?? index,
        title: item.title,
        createdAt: formatDate(item.createdAt),
        rawCreatedAt: item.createdAt,
        pinStatusCode: item.pinStatusCode ?? "B2",
        isNew: isWithin7Days(item.createdAt),
      }))
      .sort((a, b) => {
        const diff =
          (b.pinStatusCode === "B1" ? 1 : 0) -
          (a.pinStatusCode === "B1" ? 1 : 0);
        return diff !== 0
          ? diff
          : new Date(b.rawCreatedAt) - new Date(a.rawCreatedAt);
      })
      .slice(0, 7);
  } catch (err) {
    console.error("공지사항 목록 조회 실패:", err);
    noticeList.value = [];
  }
};

// ── 구성원
const projectMembers = ref([]);
const fetchPmemList = async () => {
  try {
    const res = await api.get(`/GroupMemList/${route.params.projectId}`);
    projectMembers.value = res.data;
  } catch (err) {
    console.error("구성원 목록 조회 실패:", err);
  }
};

// ── 메모
const memoList = ref([]);
const memoModalVisible = ref(false);
const isMemoEditMode = ref(false);
const editingMemoId = ref(null);
const editingMemoText = ref("");

const handleEditMemo = (memo) => {
  isMemoEditMode.value = true;
  editingMemoId.value = memo.memoId;
  editingMemoText.value = memo.memoContent;
  memoModalVisible.value = true;
};
const fetchMemoList = async () => {
  try {
    const res = await api.get(`/MemoList/${route.params.projectId}`, {
      params: { userId: authStore.user?.userId },
    });
    memoList.value = res.data;
  } catch (err) {
    console.error("메모 목록 조회 실패:", err);
  }
};
const getMemoColorClass = (index) =>
  ["memo-blue", "memo-yellow", "memo-pink", "memo-green"][index % 4];
const handleDeleteMemo = async (memoId) => {
  const result = await Swal.fire({
    title: "메모를 삭제할까요?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "삭제",
    cancelButtonText: "취소",
    reverseButtons: true,
  });
  if (!result.isConfirmed) return;
  try {
    await api.post("/MemoStatUpdate", {
      memoId,
      userId: authStore.user?.userId,
    });
    await fetchMemoList();
    await Swal.fire({
      title: "삭제되었습니다.",
      icon: "success",
      confirmButtonText: "확인",
    });
  } catch (err) {
    console.error("메모 삭제 불가:", err);
  }
};
const handleMemoSubmitted = async (payload) => {
  try {
    const projectId = route.params.projectId;
    const userId = authStore.user?.userId;
    if (!userId) return;
    if (isMemoEditMode.value) {
      await api.post("/MemoContentUpdate", {
        memoId: editingMemoId.value,
        projectId,
        userId,
        memoContent: payload.text,
      });
    } else {
      await api.post("/MemoRegister", {
        projectId,
        userId,
        memoContent: payload.text,
      });
    }
    memoModalVisible.value = false;
    isMemoEditMode.value = false;
    editingMemoId.value = null;
    editingMemoText.value = "";
    await fetchMemoList();
  } catch (err) {
    console.error("메모 저장 실패:", err);
  }
};
const handleAddMemo = () => {
  isMemoEditMode.value = false;
  editingMemoId.value = null;
  editingMemoText.value = "";
  memoModalVisible.value = true;
};

// ── 프로젝트 정보
const projectInfo = ref({
  projectId: null,
  projectName: "",
  startDate: "",
  endDate: "",
});
const fetchProjectDetail = async () => {
  try {
    const res = await api.get(`/ProjectDetail/${route.params.projectId}`);
    projectInfo.value = res.data;
  } catch (err) {
    console.error("프로젝트 상세 조회 실패:", err);
  }
};

// ── 하위 프로젝트
const subProjects = ref([]);
const milestonePage = ref(1);
const fetchSubProject = async () => {
  try {
    const res = await api.get(`/ProjectSubList/${route.params.projectId}`);
    subProjects.value = res.data;
    milestonePage.value = 1;
  } catch (err) {
    console.error("하위프로젝트 조회 실패:", err);
    subProjects.value = [];
  }
};
const pagedMilestones = computed(() => {
  const map = new Map();
  subProjects.value.forEach((item) => {
    if (!map.has(item.milestoneId))
      map.set(item.milestoneId, {
        milestoneId: item.milestoneId,
        milestoneName: item.milestoneName,
        projects: [],
      });
    map
      .get(item.milestoneId)
      .projects.push({
        projectId: item.projectId,
        projectName: item.projectName,
        userName: item.userName,
      });
  });
  return Array.from(map.values());
});
const currentMilestone = computed(() => {
  if (!pagedMilestones.value.length) return null;
  return pagedMilestones.value[
    Math.min(milestonePage.value - 1, pagedMilestones.value.length - 1)
  ];
});

// ── 이벤트
const handleProjectSetting = () =>
  router.push({
    name: "projectSetting",
    params: { id: route.params.projectId },
  });
const handleViewTasks = () =>
  router.push({
    name: "taskList",
    params: { projectId: route.params.projectId },
  });
const handleNoticeClick = (item) =>
  router.push({
    name: "noticeDetail",
    params: { projectId: route.params.projectId, noticeId: item.noticeId },
  });
const handleSubProjectRowClick = (row) =>
  router.push({
    name: "subProjectDashboard",
    params: {
      subProjectId: row.projectId,
      rootProjectId: route.params.projectId,
    },
  });

// ── 스타일 함수
const headerStyle = () => ({
  background: "var(--el-fill-color-light)",
  color: "var(--el-text-color-secondary)",
  fontSize: "11px",
  fontWeight: "500",
  borderBottom: "1px solid var(--el-border-color)",
});
const cellStyle = () => ({
  fontSize: "12px",
  color: "var(--el-text-color-primary)",
  borderBottom: "1px solid var(--el-border-color-lighter)",
});

const getRoleClass = (roleName) => {
  if (!roleName) return "role-dev";
  if (roleName.includes("PM")) return "role-pm";
  if (roleName.includes("PL")) return "role-pl";
  if (roleName.includes("QA")) return "role-qa";
  if (roleName.includes("관리")) return "role-mgr";
  return "role-dev";
};
const getAvatarColor = (roleName) => {
  if (!roleName) return "#10b981";
  if (roleName.includes("PM")) return "#1B5C9C";
  if (roleName.includes("PL")) return "#8b5cf6";
  if (roleName.includes("QA")) return "#ef4444";
  if (roleName.includes("관리")) return "#6366f1";
  return "#10b981";
};

onMounted(() => {
  fetchProjectDetail();
  fetchSubProject();
  fetchMemoList();
  fetchPmemList();
  fetchTaskSummary();
  fetchNoticeList();
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

/* 프로젝트 헤더 카드 */
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

/* 메인 대시보드 버튼 톤 맞춤 */
.btn-setting {
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

.btn-setting:hover {
  transform: translateY(-2px);
  filter: brightness(1.08);
}

.btn-sub {
  background: #fff !important;
  color: #1b5c9c !important;
  border: 1px solid #d6e4f2 !important;
  padding: 8px 14px !important;
  height: 34px !important;
  border-radius: 8px !important;
  font-size: 12px !important;
  font-weight: 700 !important;
  transition: all 0.2s ease !important;
}

.btn-sub:hover {
  background: #eff6ff !important;
  border-color: #bcd2ea !important;
}

/* 메인 대시보드 grid 체계 맞춤 */
.top-grid {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 24px;
  align-items: start;
}

.left-col,
.right-col {
  display: flex;
  flex-direction: column;
  gap: 24px;
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
  gap: 12px;
  border-bottom: 1px solid #f3f4f6;
}

.panel-title {
  font-size: 15px;
  font-weight: 700;
  color: #111827;
}

/* 공지사항 */
.notice-body {
  padding: 4px 0;
}

.notice-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.notice-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 20px;
  cursor: pointer;
  border-bottom: 1px solid #f9fafb;
  transition: background 0.2s ease;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-item:hover {
  background: #f9fafb;
}

.notice-left {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.notice-title {
  color: #1f2937;
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-date {
  font-size: 12px;
  color: #9ca3af;
  flex-shrink: 0;
}

.badge-pin {
  background: #eff6ff;
  color: #1e40af;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
}

.badge-new {
  background: #fee2e2;
  color: #b91c1c;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
}

/* 하위 프로젝트 */
.sub-body {
  padding: 0;
}

.sub-milestone-title {
  padding: 16px 20px 10px;
  font-size: 13px;
  font-weight: 700;
  color: #1b5c9c;
  border-bottom: 1px solid #f3f4f6;
}

.sub-pl {
  font-size: 12px;
  color: #6b7280;
  font-weight: 600;
}

/* 구성원 */
.member-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.member-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 700;
  font-size: 14px;
  flex-shrink: 0;
}

.member-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex: 1;
  gap: 8px;
}

.member-name {
  font-size: 13px;
  font-weight: 600;
  color: #111827;
}

.member-count-badge {
  background: #f3f4f6;
  color: #4b5563;
  padding: 3px 10px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
}

.member-role {
  font-size: 11px;
  font-weight: 700;
  padding: 4px 10px;
  border-radius: 999px;
  flex-shrink: 0;
}

.role-pm {
  background: #dbeafe;
  color: #1d4ed8;
}

.role-pl {
  background: #ede9fe;
  color: #6d28d9;
}

.role-dev {
  background: #dcfce7;
  color: #166534;
}

.role-qa {
  background: #fee2e2;
  color: #b91c1c;
}

.role-mgr {
  background: #e0e7ff;
  color: #4338ca;
}

/* 메모 */
.memo-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.btn-memo-add {
  background: linear-gradient(135deg, #1b5c9c 0%, #144677 100%) !important;
  color: #fff !important;
  border: none !important;
  width: 30px !important;
  height: 30px !important;
  font-size: 18px !important;
  font-weight: 700 !important;
  border-radius: 50% !important;
  padding: 0 !important;
  box-shadow: 0 4px 10px rgba(27, 92, 156, 0.22) !important;
}

.btn-memo-add:hover {
  filter: brightness(1.05);
}

.memo-card {
  border-radius: 10px;
  padding: 12px 14px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
  border: 1px solid #e5e7eb;
  cursor: pointer;
  transition:
    transform 0.15s ease,
    box-shadow 0.15s ease;
}

.memo-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.memo-blue {
  background: #eff6ff;
}

.memo-yellow {
  background: #fefce8;
}

.memo-pink {
  background: #fdf2f8;
}

.memo-green {
  background: #ecfdf5;
}

.memo-content {
  flex: 1;
  min-width: 0;
}

.memo-date {
  font-size: 11px;
  font-weight: 600;
  color: #9ca3af;
  margin-bottom: 6px;
}

.memo-text {
  font-size: 13px;
  color: #1f2937;
  white-space: pre-line;
  line-height: 1.6;
  word-break: break-word;
}

.memo-del {
  font-size: 13px;
  color: #9ca3af;
  padding: 0 !important;
  min-height: auto !important;
  height: 22px !important;
  width: 22px !important;
  flex-shrink: 0;
}

.memo-del:hover {
  color: #dc2626 !important;
}

/* 빈 상태 */
.empty-text {
  padding: 24px 20px;
  text-align: center;
  font-size: 13px;
  color: #9ca3af;
}

/* 페이지네이션 */
.pag-wrap {
  padding: 16px;
  display: flex;
  justify-content: center;
  border-top: 1px solid #f3f4f6;
  background: #f9fafb;
}

/* 테이블 */
:deep(.el-table) {
  --el-table-header-bg-color: #f9fafb;
  --el-table-row-hover-bg-color: #f9fbff;
}

:deep(.el-table th.el-table__cell) {
  background: #f9fafb !important;
  color: #4b5563 !important;
  font-size: 12px !important;
  font-weight: 600 !important;
  height: 44px;
  border-bottom: 1px solid #eef2f7 !important;
}

:deep(.el-table td.el-table__cell) {
  color: #1f2937;
  font-size: 13px;
  padding: 10px 0;
  border-bottom: 1px solid #f3f4f6 !important;
}

:deep(.el-table__body-wrapper .el-table__row) {
  cursor: pointer;
}

/* 기존 script의 headerStyle / cellStyle 과도 맞춤 */
:deep(.el-pagination.is-background .btn-next),
:deep(.el-pagination.is-background .btn-prev),
:deep(.el-pagination.is-background .el-pager li) {
  border-radius: 8px;
}

@media (max-width: 1200px) {
  .top-grid {
    grid-template-columns: 1fr;
  }

  .right-col {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;
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
    flex-direction: column;
    align-items: flex-start;
  }

  .right-col {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .panel-head {
    padding: 14px 16px;
  }

  .memo-body,
  .member-body {
    padding: 16px;
  }

  .notice-item {
    padding: 12px 16px;
  }
}
</style>