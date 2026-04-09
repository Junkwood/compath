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
          <!-- 페이지 타이틀 + 프로젝트명 + 기간 -->
          <div class="mb-6 proj-title-row">
            <div class="proj-title-left">
              <h2
                class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
              >
                프로젝트 대시보드
              </h2>
              <div class="proj-name-row">
                <span class="proj-name"
                  >【 {{ projectInfo.projectName }} 】</span
                >
                <span class="proj-period"
                  >{{ projectInfo.startDate }} - {{ projectInfo.endDate }}</span
                >
              </div>
            </div>

            <div class="proj-title-right">
              <el-button class="setting-btn" @click="handleProjectSetting">
                ⚙ 프로젝트 설정
              </el-button>
            </div>
          </div>

          <!-- ────────── 상단: 업무현황 + 프로젝트 구성원 ────────── -->
          <div class="proj-top mb-5">
            <!-- 좌측: 업무 현황 + 공지사항 + 하위 프로젝트 -->
            <div class="proj-left-col">
              <!-- 업무 현황 -->
              <div class="card mb-5">
                <div class="card-header">
                  <span class="card-title">업무 현황</span>
                  <el-button class="view-task-btn" @click="handleViewTasks">
                    업무 목록 보기
                  </el-button>
                </div>
                <div class="task-table-wrap">
                  <el-table
                    :data="taskSummaryData"
                    class="task-status-table"
                    style="width: 100%"
                    :header-cell-style="taskHeaderStyle"
                    :cell-style="taskCellStyle"
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
              </div>

              <!-- 공지사항 -->
              <div class="card mb-5">
                <div class="card-header">
                  <span class="card-title">공지사항</span>
                </div>
                <div class="notice-body">
                  <ul class="notice-list">
                    <li
                      v-for="item in noticeList"
                      :key="item.id"
                      class="notice-item"
                      @click="handleNoticeClick(item)"
                    >
                      <div class="notice-left">
                        <span class="notice-title">{{ item.title }}</span>
                        <span v-if="item.isNew" class="notice-badge">NEW</span>
                      </div>
                      <span class="notice-date">{{ item.date }}</span>
                    </li>
                  </ul>
                </div>
              </div>

              <!-- 하위 프로젝트 -->
              <div class="card">
                <div class="card-header">
                  <span class="card-title">하위 프로젝트 목록</span>
                  <el-button class="add-sub-btn" @click="handleAddSubProject">
                    + 하위 프로젝트 생성
                  </el-button>
                </div>
                <div class="sub-project-body">
                  <template v-if="currentMilestone">
                    <div class="sub-project-group">
                      <div class="sub-project-stage-title">
                        마일스톤
                        {{ currentMilestone?.milestoneName }}
                      </div>

                      <div class="sub-project-table-wrap">
                        <el-table
                          :data="currentMilestone.projects || []"
                          class="sub-project-table"
                          style="width: 100%"
                          :show-header="false"
                          :cell-style="subCellStyle"
                          @row-click="handleSubProjectRowClick"
                        >
                          <el-table-column prop="projectName" min-width="220" />
                          <el-table-column label="PL" width="140" align="right">
                            <template #default="{ row }">
                              <span class="sub-pl">PL {{ row.userName }}</span>
                            </template>
                          </el-table-column>
                        </el-table>
                      </div>

                      <div
                        class="pagination-wrap"
                        v-if="pagedMilestones.length > 1"
                      >
                        <el-pagination
                          v-model:current-page="milestonePage"
                          :page-size="1"
                          :total="pagedMilestones.length"
                          layout="prev, pager, next"
                          background
                        />
                      </div>
                    </div>
                  </template>

                  <div v-else class="sub-empty">하위 프로젝트가 없습니다.</div>
                </div>
              </div>
            </div>
            <!-- 우측: 프로젝트 구성원 + 나의 메모 -->
            <div class="proj-right-col">
              <!-- 프로젝트 구성원 -->
              <div class="card mb-5">
                <div class="card-header">
                  <span class="card-title">프로젝트 구성원</span>
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
                          class="member-role-badge"
                          :class="getRoleClass(member.roleName)"
                        >
                          {{ member.roleName }}
                        </span>
                      </div>
                    </div>
                  </template>

                  <div v-else class="member-empty-row">
                    <span class="member-empty-text"
                      >구성원이 아직 지정되지 않았습니다.</span
                    >
                  </div>
                </div>
              </div>

              <!-- 나의 메모 -->
              <div class="card">
                <div class="card-header">
                  <span class="card-title">나의 메모</span>
                  <el-button class="memo-add-btn" @click="handleAddMemo" circle>
                    +
                  </el-button>
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
                        class="memo-del-btn"
                        @click="handleDeleteMemo(memo.memoId)"
                        text
                      >
                        ✕
                      </el-button>
                    </div>
                  </template>

                  <div v-else class="memo-empty">등록된 메모가 없습니다.</div>
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

// ── 업무 현황  ────────────────────────────
const taskSummaryData = ref([]);

const fetchTaskSummary = async () => {
  try {
    const projectId = route.params.projectId;
    const res = await api.get(`/TaskSummary/${projectId}`);
    taskSummaryData.value = res.data;
  } catch (err) {
    console.error("업무 현황 조회 실패:", err);
    taskSummaryData.value = [];
  }
};

// ── 공지사항 (하드코딩) ──────────────────────────────
const noticeList = ref([
  { id: 1, title: "서비스 현황성 적용 안내", date: "2026-03-19", isNew: true },
  { id: 2, title: "서비스 현황성 적용 안내", date: "2026-06-18", isNew: false },
  {
    id: 3,
    title: "공지사항 등록 및 수정 안내",
    date: "2026-06-18",
    isNew: false,
  },
  {
    id: 4,
    title: "공지사항 등록 및 수정 안내",
    date: "2026-06-18",
    isNew: false,
  },
]);

// ── 프로젝트 구성원 ──────────────────────────────
const projectMembers = ref([]);

const fetchPmemList = async () => {
  try {
    const projectId = route.params.projectId;
    const res = await api.get(`/GroupMemList/${projectId}`);
    projectMembers.value = res.data;
  } catch (err) {
    console.error("구성원 목록 조회 실패:", err);
  }
};

// ── 나의 메모 ──────────────────────────────────
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

//메모 목록조회
const fetchMemoList = async () => {
  try {
    const projectId = route.params.projectId;
    const userId = authStore.user?.userId;

    const res = await api.get(`/MemoList/${projectId}`, {
      params: { userId },
    });

    memoList.value = res.data;
  } catch (err) {
    console.error("메모 목록 조회 실패 :", err);
  }
};

//메모클래스 (색상)
const getMemoColorClass = (index) => {
  const colorClasses = ["memo-blue", "memo-yellow", "memo-pink", "memo-green"];
  return colorClasses[index % colorClasses.length];
};

//메모삭제
const handleDeleteMemo = async (memoId) => {
  const result = await Swal.fire({
    title: "메모를 삭제할까요?",
    text: "삭제된 메모는 목록에서 보이지 않습니다.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "삭제",
    cancelButtonText: "취소",
    reverseButtons: true,
  });

  if (!result.isConfirmed) return;

  try {
    const userId = authStore.user?.userId;

    await api.post("/MemoStatUpdate", {
      memoId,
      userId,
    });

    await fetchMemoList();

    await Swal.fire({
      title: "삭제되었습니다.",
      icon: "success",
      confirmButtonText: "확인",
    });
  } catch (err) {
    console.error("메모 삭제 불가:", err);

    await Swal.fire({
      title: "삭제 실패",
      text: "메모 삭제 중 오류가 발생했습니다.",
      icon: "error",
      confirmButtonText: "확인",
    });
  }
};

//메모등록
const handleMemoSubmitted = async (payload) => {
  try {
    const projectId = route.params.projectId;
    const userId = authStore.user?.userId;

    if (!userId) {
      console.warn("로그인 사용자 정보가 없습니다.");
      return;
    }

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

// ── 프로젝트 정보 ──────────────────────────────
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

// ── 하위 프로젝트 ──────────────────────────────
const subProjects = ref([]);
const milestonePage = ref(1);

//하위 프로젝트 목록
const fetchSubProject = async () => {
  try {
    const projectId = route.params.projectId;
    const res = await api.get(`/ProjectSubList/${projectId}`);
    subProjects.value = res.data;
    milestonePage.value = 1;
  } catch (err) {
    console.error("하위프로젝트 조회 실패:", err);
    subProjects.value = [];
  }
};

//하위 프로젝트 목록 - 페이지네이션
const pagedMilestones = computed(() => {
  const map = new Map();

  subProjects.value.forEach((item) => {
    const key = item.milestoneId;

    if (!map.has(key)) {
      map.set(key, {
        milestoneId: item.milestoneId,
        milestoneName: item.milestoneName,
        projects: [],
      });
    }

    map.get(key).projects.push({
      projectId: item.projectId,
      projectName: item.projectName,
      userName: item.userName,
    });
  });

  return Array.from(map.values());
});

const currentMilestone = computed(() => {
  if (pagedMilestones.value.length === 0) return null;
  const safeIndex = Math.min(
    milestonePage.value - 1,
    pagedMilestones.value.length - 1,
  );
  return pagedMilestones.value[safeIndex];
});

// ── 이벤트 핸들러 ──────────────────────────────
const handleProjectSetting = () => {
  router.push({
    name: "projectSetting",
    params: { id: route.params.projectId },
  });
};

const handleViewTasks = () => {
  router.push({
    name: "taskList",
    params: {
      projectId: route.params.projectId,
    },
  });
};
const handleNoticeClick = () => {};

//하위프로젝트 테이블 행 클릭시 하위프로젝트 대쉬보드로 진입
const handleSubProjectRowClick = (row) => {
  router.push({
    name: "subProjectDashboard",
    params: {
      subProjectId: row.projectId,
      rootProjectId: route.params.projectId,
    },
  });
};

const handleAddMemo = () => {
  isMemoEditMode.value = false;
  editingMemoId.value = null;
  editingMemoText.value = "";
  memoModalVisible.value = true;
};

// ── 스타일용 함수 ──────────────────────────────
const subCellStyle = () => ({
  fontSize: "13px",
  color: "#374151",
  borderBottom: "1px solid #dcdfe6",
  padding: "7px 12px",
  height: "36px",
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
  if (roleName.includes("PM")) return "#3b82f6";
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
});
</script>

<style scoped>
/* ────────────────────────────────────────────
   프로젝트 타이틀 행
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

.setting-btn {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #475569;
  font-size: 13px;
  font-weight: 500;
  border-radius: 8px;
  height: 36px;
  padding: 0 14px;
}

.setting-btn:hover {
  background: #e2e8f0;
}

/* ────────────────────────────────────────────
   상단 2단 레이아웃
──────────────────────────────────────────── */
.proj-top {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 20px;
  align-items: start;
  min-width: 0;
}

.proj-left-col {
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
}

.proj-right-col {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

/* ────────────────────────────────────────────
   카드 공통
──────────────────────────────────────────── */
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

/* ────────────────────────────────────────────
   업무 현황
──────────────────────────────────────────── */
.view-task-btn {
  background: #c7d9f5;
  border: none;
  color: #1a1a2e;
  font-size: 13px;
  font-weight: 500;
  border-radius: 8px;
  height: 32px;
  padding: 0 12px;
}

.view-task-btn:hover {
  background: #a8c4ef;
}

/* ────────────────────────────────────────────
   공지사항
──────────────────────────────────────────── */
.notice-body {
  padding: 8px 0;
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
  padding: 10px 20px;
  cursor: pointer;
  transition: background 0.15s;
  border-bottom: 1px solid #f8fafc;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-item:hover {
  background: #f0f7ff;
}

.notice-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.notice-title {
  font-size: 13px;
  color: #374151;
}

.notice-title::before {
  content: "·";
  color: #94a3b8;
  margin-right: 6px;
}

.notice-badge {
  background: #ef4444;
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  padding: 1px 5px;
  border-radius: 4px;
  letter-spacing: 0.03em;
}

.notice-date {
  font-size: 12px;
  color: #94a3b8;
}

/* ────────────────────────────────────────────
   하위 프로젝트
──────────────────────────────────────────── */
.add-sub-btn {
  background: #c7d9f5;
  border: none;
  color: #000;
  font-size: 13px;
  font-weight: 500;
  border-radius: 8px;
  height: 32px;
  padding: 0 12px;
}

.add-sub-btn:hover {
  background: #a8c4ef;
}

.sub-project-body {
  padding: 12px 16px 0;
}

.sub-project-group {
  border-radius: 14px;
  background: #fff;
  overflow: hidden;
}

.sub-project-stage-title {
  padding: 10px 14px 6px;
  font-size: 16px;
  line-height: 1.35;
  color: #000;
  white-space: pre-line;
}

.sub-project-table-wrap {
  max-height: 216px;
  overflow-y: auto;
}

.sub-project-table :deep(.el-table) {
  border: none !important;
  font-size: 12px;
}

.sub-project-table :deep(.el-table__inner-wrapper::before),
.sub-project-table :deep(.el-table::before) {
  display: none;
}

.sub-project-table :deep(td.el-table__cell) {
  padding: 0 !important;
  height: 36px;
}

.sub-project-table :deep(.cell) {
  padding: 0 12px !important;
  line-height: 36px;
  color: #111827;
}

.sub-project-table :deep(tr:last-child td.el-table__cell) {
  border-bottom: none !important;
}

.sub-pl {
  font-size: 12px;
  color: #111827;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 12px 0;
  border-top: 1px solid #f0f0f0;
}

.sub-empty {
  padding: 24px 0;
  text-align: center;
  font-size: 13px;
  color: #9ca3af;
}

/* ────────────────────────────────────────────
   프로젝트 구성원
──────────────────────────────────────────── */
.member-body {
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 12px;
  /* padding: 10px 12px; */
}

.member-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 700;
  font-size: 15px;
  flex-shrink: 0;
}

.member-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.member-name {
  font-size: 13px;
  color: #000000;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.member-role-badge {
  flex-shrink: 0;
  font-size: 11px;
  font-weight: 700;
  padding: 4px 9px;
  border-radius: 999px;
  letter-spacing: 0.02em;
  border: 1px solid transparent;
}

.role-pm {
  background: #dbeafe;
  color: #1d4ed8;
  border-color: #bfdbfe;
}

.role-pl {
  background: #ede9fe;
  color: #6d28d9;
  border-color: #ddd6fe;
}

.role-dev {
  background: #d1fae5;
  color: #065f46;
  border-color: #a7f3d0;
}

.role-qa {
  background: #fee2e2;
  color: #b91c1c;
  border-color: #fecaca;
}

.role-mgr {
  background: #e0e7ff;
  color: #3730a3;
  border-color: #c7d2fe;
}

.member-empty-row {
  padding: 18px 12px;
  text-align: center;
  border: 1px dashed #dbe2ea;
  border-radius: 12px;
  background: #fafcff;
}

.member-empty-text {
  font-size: 12px;
  color: #94a3b8;
}

/* ────────────────────────────────────────────
   나의 메모
──────────────────────────────────────────── */
.memo-add-btn {
  background: #3b82f6;
  color: #fff;
  border: none;
  width: 30px;
  height: 30px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  line-height: 1;
  box-shadow: 0 4px 10px rgba(59, 130, 246, 0.2);
  transition:
    background 0.2s,
    transform 0.2s;
}

.memo-add-btn:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

.memo-body {
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.memo-card {
  border-radius: 5px;
  padding: 14px 14px 12px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
  border: 1px solid rgba(148, 163, 184, 0.14);
  /* box-shadow: 0 4px 12px rgba(15, 23, 42, 0.04); */
  /* transition: transform 0.18s, box-shadow 0.18s, border-color 0.18s; */
  cursor: pointer;
}

.memo-blue {
  background: linear-gradient(180deg, #eff6ff 0%, #e0ecff 100%);
}

.memo-yellow {
  background: linear-gradient(180deg, #fffce8 0%, #fff6c7 100%);
}

.memo-pink {
  background: linear-gradient(180deg, #fdf2f8 0%, #fce7f3 100%);
}

.memo-green {
  background: linear-gradient(180deg, #f3f0ff 0%, #e9e2ff 100%);
}

.memo-content {
  flex: 1;
  min-width: 0;
}

.memo-date {
  display: inline-flex;
  align-items: center;
  margin-bottom: 8px;
  padding: 3px 8px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.55);
  font-size: 11px;
  font-weight: 600;
  color: #64748b;
}

.memo-text {
  font-size: 13px;
  color: #334155;
  white-space: pre-line;
  line-height: 1.6;
  word-break: break-word;
}

.memo-del-btn {
  font-size: 14px;
  color: #94a3b8;
  padding: 0;
  min-height: auto;
  height: 24px;
  width: 24px;
  flex-shrink: 0;
  border-radius: 50%;
  transition:
    background 0.18s,
    color 0.18s;
}

.memo-del-btn:hover {
  color: #ef4444;
  background: rgba(255, 255, 255, 0.65);
}

.memo-empty {
  padding: 18px 12px;
  text-align: center;
  font-size: 13px;
  color: #9ca3af;
  border: 1px dashed #dbe2ea;
  border-radius: 12px;
  background: #fafcff;
}

/* ────────────────────────────────────────────
   Element Plus 공통
──────────────────────────────────────────── */
:deep(.el-table) {
  --el-table-border-color: #f1f5f9;
  --el-table-header-bg-color: #f8fafc;
}

:deep(.el-table__row:hover > td) {
  background: #f0f7ff !important;
}

:deep(.el-table .el-table__inner-wrapper) {
  min-width: 0;
}

/* ────────────────────────────────────────────
   반응형
──────────────────────────────────────────── */
@media (max-width: 1024px) {
  .proj-top {
    grid-template-columns: 1fr;
  }

  .proj-right-col {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }

  .proj-right-col .card {
    margin-bottom: 0 !important;
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

  .proj-right-col {
    grid-template-columns: 1fr;
  }

  .notice-date {
    display: none;
  }

  .setting-btn {
    width: 100%;
    justify-content: center;
  }

  .card-header {
    flex-wrap: wrap;
    gap: 8px;
  }

  .view-task-btn,
  .add-sub-btn {
    font-size: 12px;
    padding: 0 10px;
  }
}

@media (max-width: 480px) {
  .proj-name {
    font-size: 13px;
    word-break: keep-all;
  }

  .member-role-badge {
    display: none;
  }

  .sub-pl {
    font-size: 11px;
  }

  .memo-body {
    padding: 10px 12px;
  }
}

.sub-project-table :deep(.el-table__row) {
  cursor: pointer;
}

.sub-project-table :deep(.el-table__row:hover > td) {
  background: #f5f9ff !important;
}
</style>
