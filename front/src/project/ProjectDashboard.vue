<template>
  <div class="dashboard-page flex h-screen overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden bg-[#f5f6f8]"
    >
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <!-- 브레드크럼 -->
        <div class="sub-header">
          <div class="breadcrumb">
            <span class="bc-home">홈</span>
            <span class="bc-sep">/</span>
            <span class="bc-home">프로젝트</span>
            <span class="bc-sep">/</span>
            <span class="bc-cur">{{ projectInfo.projectName }}</span>
          </div>
        </div>

        <div class="page-container">
          <!-- ── 헤더 카드 ── -->
          <div class="page-title-card">
            <div class="title-section">
              <div class="title-tags">
                <span v-if="projectInfo.identifier" class="proj-id-badge">
                  {{ projectInfo.identifier }}
                </span>
                <span
                  class="milestone-badge"
                  :class="isMilestoneUsed ? 'on' : 'off'"
                >
                  {{ milestoneLabel }}
                </span>
              </div>
              <h1 class="page-title">{{ projectInfo.projectName }}</h1>
              <p class="page-subtitle">
                {{ projectInfo.startDate }} – {{ projectInfo.endDate }}
              </p>
              <p v-if="projectInfo.description" class="proj-desc">
                {{ projectInfo.description }}
              </p>
            </div>

            <div class="action-group">
              <el-button
                class="action-btn btn-primary"
                @click="handleProjectSetting"
              >
                ⚙ 프로젝트 설정
              </el-button>
             <el-button
                v-if="isMilestoneUsed"
                class="action-btn btn-secondary"
                @click="handleGoToMilestone"
              >
                마일스톤 바로가기
              </el-button>
            </div>
          </div>

          <!-- ── 본문 그리드 ── -->
          <div class="top-grid">
            <!-- 좌측 -->
            <div class="left-col">
              <!-- 업무 현황 -->
              <div class="panel">
                <div class="panel-head">
                  <div>
                    <h2 class="panel-title">업무 현황</h2>
                  </div>
                  <el-button class="btn-sub" @click="handleViewTasks">
                    업무 목록 보기
                  </el-button>
                </div>
                <el-table
                  class="task-summary-table"
                  :data="taskSummaryData"
                  style="width: 100%"
                  :header-cell-style="headerStyle"
                  :cell-style="cellStyle"
                >
                  <el-table-column prop="type" label="유형" min-width="110" />
                  <el-table-column
                    prop="total"
                    label="전체"
                    min-width="80"
                    align="center"
                  />
                  <el-table-column
                    prop="inProgress"
                    label="진행중"
                    min-width="90"
                    align="center"
                  >
                    <template #default="{ row }">
                      <span class="num-hi">{{ row.inProgress }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    prop="done"
                    label="완료"
                    min-width="80"
                    align="center"
                  >
                    <template #default="{ row }">
                      <span class="num-done">{{ row.done }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    prop="rejected"
                    label="반려"
                    min-width="80"
                    align="center"
                  >
                    <template #default="{ row }">
                      <span :class="{ 'num-reject': row.rejected > 0 }">{{
                        row.rejected
                      }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column
                    prop="totalSum"
                    label="합계"
                    min-width="80"
                    align="center"
                  >
                    <template #default="{ row }">
                      <span class="num-total">{{ row.totalSum }}</span>
                    </template>
                  </el-table-column>
                </el-table>
              </div>

              <!-- 공지사항 -->
              <div class="panel">
                <div class="panel-head">
                  <h2 class="panel-title">공지사항</h2>
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

              <!-- 하위 프로젝트 목록 -->
              <div class="panel">
                <div class="panel-head">
                  <h2 class="panel-title">하위 프로젝트 목록</h2>
                  <el-button class="btn-sub" @click="handleAddSubProject">
                    + 하위 프로젝트 생성
                  </el-button>
                </div>

                <div class="sub-body">
                  <template v-if="isMilestoneEnabled">
                    <!-- 마일스톤 탭 부분  -->
                    <template v-if="milestoneTabs.length > 0">
                      <el-tabs
                        v-model="activeMilestoneKey"
                        class="milestone-tabs"
                        @tab-click="handleMilestoneTabClick"
                      >
                        <el-tab-pane
                          v-for="(tab, index) in milestoneTabs"
                          :key="tab.milestoneId ?? 'unassigned'"
                          :name="getMilestoneTabKey(tab)"
                          lazy
                        >
                          <template #label>
                            마일스톤 {{ index + 1 }} : {{ tab.milestoneName }}
                          </template>
                        </el-tab-pane>
                      </el-tabs>

                      <el-table
                        :data="pagedSubProjects"
                        style="width: 100%"
                        :show-header="false"
                        :cell-style="cellStyle"
                        row-class-name="clickable-row"
                        @row-click="handleSubProjectRowClick"
                      >
                        <el-table-column min-width="260">
                          <template #default="{ row }">
                            <div class="sub-project-cell">
                              <span class="sub-project-name">{{
                                row.projectName
                              }}</span>
                              <span
                                v-if="row.identifier"
                                class="sub-project-id"
                                >{{ row.identifier }}</span
                              >
                            </div>
                          </template>
                        </el-table-column>
                        <el-table-column min-width="180" align="center">
                          <template #default="{ row }">
                            <span class="sub-pl"
                              >{{ row.startDate || "-" }} ~
                              {{ row.endDate || "-" }}</span
                            >
                          </template>
                        </el-table-column>
                        <el-table-column label="PL" width="110" align="center">
                          <template #default="{ row }">
                            <span class="sub-pl">{{
                              row.userName || "-"
                            }}</span>
                          </template>
                        </el-table-column>
                        <template #empty>
                          <div class="empty-text">
                            하위 프로젝트가 없습니다.
                          </div>
                        </template>
                      </el-table>

                      <div class="pag-wrap">
                        <el-pagination
                          v-model:current-page="subProjectPage"
                          :page-size="subProjectPageSize"
                          :total="currentMilestone?.projects?.length || 0"
                          layout="prev, pager, next"
                          background
                        />
                      </div>
                    </template>
                    <div v-else class="empty-text">
                      하위 프로젝트가 없습니다.
                    </div>
                  </template>

                  <template v-else>
                    <template v-if="subProjects.length > 0">
                      <el-table
                        :data="subProjects"
                        style="width: 100%"
                        :show-header="false"
                        :cell-style="cellStyle"
                        row-class-name="clickable-row"
                        @row-click="handleSubProjectRowClick"
                      >
                        <el-table-column min-width="260">
                          <template #default="{ row }">
                            <div class="sub-project-cell">
                              <span class="sub-project-name">{{
                                row.projectName
                              }}</span>
                              <span
                                v-if="row.identifier"
                                class="sub-project-id"
                                >{{ row.identifier }}</span
                              >
                            </div>
                          </template>
                        </el-table-column>
                        <el-table-column label="PL" width="110" align="center">
                          <template #default="{ row }">
                            <span class="sub-pl">{{
                              row.userName || "-"
                            }}</span>
                          </template>
                        </el-table-column>
                      </el-table>
                    </template>
                    <div v-else class="empty-text">
                      하위 프로젝트가 없습니다.
                    </div>
                  </template>
                </div>
              </div>
            </div>

            <!-- 우측 -->
            <div class="right-col">
              <!-- 구성원 -->
              <div class="panel">
                <div class="panel-head">
                  <h2 class="panel-title">프로젝트 구성원</h2>
                  <span class="count-tag">{{ projectMembers.length }}명</span>
                </div>
                <div class="member-body">
                  <template v-if="projectMembers.length > 0">
                    <div class="member-scroll">
                      <div
                        v-for="group in groupedMembers"
                        :key="group.key"
                        class="member-group"
                      >
                        <button
                          class="member-group-head"
                          type="button"
                          @click="toggleMemberGroup(group.key)"
                        >
                          <div class="member-group-left">
                            <span
                              class="member-group-dot"
                              :style="{
                                backgroundColor: getAvatarColor(group.label),
                              }"
                            ></span>
                            <span class="member-group-name">{{
                              group.label
                            }}</span>
                          </div>
                          <div class="member-group-right">
                            <span class="member-group-count"
                              >{{ group.members.length }}명</span
                            >
                            <span class="member-group-arrow">{{
                              openedMemberGroups[group.key] ? "−" : "+"
                            }}</span>
                          </div>
                        </button>

                        <transition name="accordion">
                          <div
                            v-show="openedMemberGroups[group.key]"
                            class="member-group-list"
                          >
                            <div
                              v-for="member in group.members"
                              :key="member.userId"
                              class="member-item"
                            >
                              <div
                                class="member-avatar"
                                :style="{
                                  backgroundColor: getAvatarColor(
                                    member.roleName,
                                  ),
                                }"
                              >
                                {{ member.userName?.charAt(0) }}
                              </div>
                              <div class="member-info">
                                <span class="member-name">{{
                                  member.userName
                                }}</span>
                                <span
                                  class="member-role"
                                  :class="getRoleClass(member.roleName)"
                                >
                                  {{ member.roleName || "구성원" }}
                                </span>
                              </div>
                            </div>
                          </div>
                        </transition>
                      </div>
                    </div>
                  </template>
                  <div v-else class="empty-text">
                    구성원이 아직 지정되지 않았습니다.
                  </div>
                </div>
              </div>

              <!-- 메모 -->
              <div class="panel">
                <div class="panel-head">
                  <h2 class="panel-title">나의 메모</h2>
                  <button
                    class="btn-memo-add"
                    type="button"
                    @click="handleAddMemo"
                  >
                    +
                  </button>
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
                      <button
                        class="memo-del"
                        type="button"
                        @click="handleDeleteMemo(memo.memoId)"
                      >
                        ✕
                      </button>
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
  <!-- 하위프로젝트 생성 버튼 클릭 -->
  <ProjectSubCreateModal
    v-model="createSubProjectModalOpen"
    :project-id="projectInfo.projectId"
    :parent-project-name="projectInfo.projectName"
    :parent-start-date="projectInfo.startDate"
    :parent-end-date="projectInfo.endDate"
    :parent-use-milestone="projectInfo.useMilestone"
    @submitted="handleSubProjectSubmitted"
  />

  <MilestoneCreateModal
    v-model="milestoneModalVisible"
    :project-id="projectInfo.projectId"
    :project-name="projectInfo.projectName"
    :is-edit-mode="false"
    :redirect-after-save="milestoneRedirectAfterSave"
    @saved="handleMilestoneSaved"
  />
</template>

<script setup>
import { onMounted, ref, computed, watch } from "vue";
import { storeToRefs } from "pinia";
import { useRoute, useRouter } from "vue-router";
import Swal from "sweetalert2";

import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import ProjectMemoModal from "../project/ProjectMemoModal.vue";
import ProjectSubCreateModal from "../project/ProjectSubCreateModal.vue";
import MilestoneCreateModal from "../milestone/MilestoneCreateModal.vue";

import { useAuthStore } from "../stores/auth";
import { useProjectDashboardStore } from "../stores/projectDashboard";

const authStore = useAuthStore();
const dashboardStore = useProjectDashboardStore();

const route = useRoute();
const router = useRouter();

const {
  projectInfo,
  milestoneList,
  taskSummaryData,
  noticeList,
  projectMembers,
  memoList,
  subProjects,
  isMilestoneUsed,
  milestoneLabel,
  isMilestoneEnabled,
  milestoneTabs,
  groupedMembers,
} = storeToRefs(dashboardStore);

const {
  fetchAll,
  fetchProjectDetail,
  fetchMilestoneList,
  fetchSubProject,
  saveMemo,
  deleteMemo,
} = dashboardStore;

/* -------------------- 페이지 UI 상태 -------------------- */
const sidebarOpen = ref(false);
const createSubProjectModalOpen = ref(false);
const milestoneModalVisible = ref(false);
const milestoneRedirectAfterSave = ref(false);

const memoModalVisible = ref(false);
const isMemoEditMode = ref(false);
const editingMemoId = ref(null);
const editingMemoText = ref("");

const activeMilestoneKey = ref(null);
const subProjectPage = ref(1);
const subProjectPageSize = 5;

const openedMemberGroups = ref({});

/* -------------------- 구성원 아코디언 -------------------- */
const setDefaultOpenedGroups = () => {
  const next = {};
  groupedMembers.value.forEach((group, index) => {
    next[group.key] = index === 0;
  });
  openedMemberGroups.value = next;
};

// 구성원토글
const toggleMemberGroup = (groupKey) => {
  const isCurrentlyOpen = openedMemberGroups.value[groupKey];
  const next = {};
  groupedMembers.value.forEach((group) => {
    next[group.key] = false;
  });
  next[groupKey] = !isCurrentlyOpen;
  openedMemberGroups.value = next;
};

watch(
  groupedMembers,
  (groups) => {
    if (!groups.length) {
      openedMemberGroups.value = {};
      return;
    }

    const hasOpened = groups.some((group) => openedMemberGroups.value[group.key]);

    if (!hasOpened) {
      setDefaultOpenedGroups();
    } else {
      const next = {};
      groups.forEach((group) => {
        next[group.key] = !!openedMemberGroups.value[group.key];
      });
      openedMemberGroups.value = next;
    }
  },
  { immediate: true },
);

/* -------------------- 메모 -------------------- */
const handleEditMemo = (memo) => {
  isMemoEditMode.value = true;
  editingMemoId.value = memo.memoId;
  editingMemoText.value = memo.memoContent;
  memoModalVisible.value = true;
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
    await deleteMemo(
      memoId,
      authStore.user?.userId,
      route.params.projectId,
    );

    await Swal.fire({
      title: "삭제되었습니다.",
      icon: "success",
      confirmButtonText: "확인",
    });
  } catch (err) {
    console.error("메모 삭제 불가:", err);
  }
};

// 메모 등록
const handleMemoSubmitted = async (payload) => {
  try {
    const projectId = route.params.projectId;
    const userId = authStore.user?.userId;
    if (!userId) return;

    await saveMemo({
      projectId,
      userId,
      memoId: editingMemoId.value,
      memoContent: payload.text,
      isEditMode: isMemoEditMode.value,
    });

    memoModalVisible.value = false;
    isMemoEditMode.value = false;
    editingMemoId.value = null;
    editingMemoText.value = "";
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

/* -------------------- 하위프로젝트 / 마일스톤 탭 -------------------- */
const getMilestoneTabKey = (tab) => String(tab.milestoneId ?? "unassigned");

const currentMilestone = computed(() => {
  if (!milestoneTabs.value.length) return null;

  return (
    milestoneTabs.value.find(
      (tab) => getMilestoneTabKey(tab) === activeMilestoneKey.value,
    ) || milestoneTabs.value[0]
  );
});

// 하위프젝 페이지네이션
const pagedSubProjects = computed(() => {
  if (!currentMilestone.value) return [];
  const start = (subProjectPage.value - 1) * subProjectPageSize;
  return currentMilestone.value.projects.slice(
    start,
    start + subProjectPageSize,
  );
});

//마일스톤 탭클릭
const handleMilestoneTabClick = () => {
  subProjectPage.value = 1;
};


watch(
  milestoneTabs,
  (tabs) => {
    if (!tabs.length) {
      activeMilestoneKey.value = null;
      subProjectPage.value = 1;
      return;
    }

    const exists = tabs.some(
      (tab) => getMilestoneTabKey(tab) === activeMilestoneKey.value,
    );

    if (!exists) {
      activeMilestoneKey.value = getMilestoneTabKey(tabs[0]);
      subProjectPage.value = 1;
    }
  },
  { immediate: true },
);

/* -------------------- 하위프로젝트 생성 -------------------- */
const handleAddSubProject = async () => {
  if (isMilestoneUsed.value && milestoneList.value.length === 0) {
    const result = await Swal.fire({
      icon: "warning",
      title: "마일스톤이 생성되지 않았습니다.",
      text: "하위 프로젝트를 생성하려면 먼저 마일스톤을 생성해야 합니다.",
      confirmButtonText: "마일스톤 생성",
      cancelButtonText: "취소",
      showCancelButton: true,
      reverseButtons: true,
    });

    if (!result.isConfirmed) return;

    milestoneRedirectAfterSave.value = true;
    milestoneModalVisible.value = true;
    return;
  }

  createSubProjectModalOpen.value = true;
};

const handleSubProjectSubmitted = async () => {
  createSubProjectModalOpen.value = false;
  await fetchSubProject(route.params.projectId);
};

//마일스톤 여부 체크해서 하위프로젝트 분기 나누기
//마일스톤 생성 후 하위프로젝트 생성모달 띄우는 분기
const handleMilestoneSaved = async () => {
  milestoneModalVisible.value = false;

  await fetchProjectDetail(route.params.projectId);
  await fetchMilestoneList(route.params.projectId);
  await fetchSubProject(route.params.projectId);

  if (
    milestoneRedirectAfterSave.value &&
    isMilestoneUsed.value &&
    milestoneList.value.length > 0
  ) {
    const result = await Swal.fire({
      icon: "success",
      title: "마일스톤이 생성되었습니다.",
      text: "하위프로젝트 생성으로 이동합니다.",
      confirmButtonText: "확인",
      cancelButtonText: "취소",
      showCancelButton: true,
      reverseButtons: true,
    });

    milestoneRedirectAfterSave.value = false;

    if (result.isConfirmed) {
      createSubProjectModalOpen.value = true;
    }

    return;
  }

  milestoneRedirectAfterSave.value = false;
};

/* -------------------- 라우팅 -------------------- */
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
    params: { projectId: route.params.projectId, subProjectId: row.projectId },
  });

const handleGoToMilestone = () =>   
    router.push({
    name: "milestoneDashboard",
    params: { projectId: route.params.projectId},
  });

/* -------------------- 테이블 스타일 -------------------- */
const headerStyle = () => ({
  background: "#f9fafb",
  color: "#6b7280",
  fontSize: "12px",
  fontWeight: "600",
  letterSpacing: "0.02em",
  height: "42px",
  borderBottom: "1px solid #e5e7eb",
});

const cellStyle = () => ({
  color: "#374151",
  fontSize: "13.5px",
  padding: "12px 0",
  borderBottom: "1px solid #f3f4f6",
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

/* -------------------- 초기 로딩 -------------------- */
onMounted(async () => {
  await fetchAll(route.params.projectId, authStore.user?.userId);
  setDefaultOpenedGroups();
});
</script>

<style scoped>
.dashboard-page {
  font-family: "Pretendard", sans-serif;
  background-color: #f3f4f6;
}

/* ── 서브헤더 ── */
.sub-header {
  background: #fff;
  padding: 11px 28px;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 10;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 12.5px;
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

/* ── 페이지 컨테이너 ── */
.page-container {
  padding: 24px 28px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 1280px;
  margin: 0 auto;
  width: 100%;
}

/* ── 헤더 카드 ── */
.page-title-card {
  background: #fff;
  border-radius: 14px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  padding: 22px 28px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.title-section {
  display: flex;
  flex-direction: column;
  gap: 7px;
  min-width: 0;
}

.title-tags {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.proj-id-badge {
  display: inline-flex;
  align-items: center;
  height: 22px;
  padding: 0 10px;
  border-radius: 999px;
  background: #eff6ff;
  border: 1px solid #dbeafe;
  color: #1d4ed8;
  font-size: 11px;
  font-weight: 700;
}

.milestone-badge {
  display: inline-flex;
  align-items: center;
  height: 22px;
  padding: 0 10px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
}

.milestone-badge.on {
  background: #ecfdf5;
  color: #059669;
}
.milestone-badge.off {
  background: #f3f4f6;
  color: #9ca3af;
}

.page-title {
  font-size: 26px;
  font-weight: 800;
  color: #0f172a;
  line-height: 1.2;
  letter-spacing: -0.02em;
}

.page-subtitle {
  font-size: 13px;
  color: #9ca3af;
  font-weight: 500;
}

.proj-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.6;
  white-space: pre-line;
  word-break: break-word;
  margin-top: 2px;
}

/* ── 버튼 ── */
.action-group {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.action-btn {
  height: 38px !important;
  padding: 0 18px !important;
  border-radius: 9px !important;
  font-size: 13px !important;
  font-weight: 600 !important;
  transition: all 0.15s ease !important;
}

.action-btn:hover {
  transform: translateY(-1px);
}

.btn-primary {
  background: linear-gradient(135deg, #1b5c9c 0%, #144677 100%) !important;
  border: none !important;
  color: #fff !important;
  box-shadow: 0 4px 12px rgba(27, 92, 156, 0.25) !important;
}

.btn-primary:hover {
  filter: brightness(1.08);
}

.btn-sub {
  background: #fff !important;
  color: #1b5c9c !important;
  border: 1px solid #d6e4f2 !important;
  height: 32px !important;
  padding: 0 12px !important;
  border-radius: 8px !important;
  font-size: 12px !important;
  font-weight: 600 !important;
  transition: all 0.15s ease !important;
}

.btn-sub:hover {
  background: #eff6ff !important;
  border-color: #bcd2ea !important;
}

/* ── 그리드 ── */
.top-grid {
  display: grid;
  grid-template-columns: 1fr 288px;
  gap: 20px;
  align-items: start;
}

.left-col,
.right-col {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* ── 패널 ── */
.panel {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.panel-head {
  padding: 14px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  border-bottom: 1px solid #f3f4f6;
}

.panel-title {
  font-size: 14.5px;
  font-weight: 700;
  color: #111827;
}

/* count tag */
.count-tag {
  min-width: 22px;
  height: 20px;
  padding: 0 8px;
  border-radius: 999px;
  background: #f3f4f6;
  color: #4b5563;
  font-size: 11.5px;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

/* ── 숫자 강조 ── */
.num-hi {
  color: #1b5c9c;
  font-weight: 700;
}
.num-done {
  color: #059669;
  font-weight: 600;
}
.num-reject {
  color: #dc2626;
  font-weight: 700;
}
.num-total {
  color: #374151;
  font-weight: 700;
}

/* ── 공지사항 ── */
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
  padding: 12px 20px;
  cursor: pointer;
  border-bottom: 1px solid #f9fafb;
  transition: background 0.15s;
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
  font-size: 13px;
  color: #1f2937;
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
  padding: 2px 7px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
}
.badge-new {
  background: #fee2e2;
  color: #b91c1c;
  padding: 2px 7px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
}

/* ── 하위 프로젝트 ── */
.sub-body {
  padding: 0;
}

:deep(.milestone-tabs > .el-tabs__header) {
  margin: 0;
  padding: 0 20px;
  border-bottom: 1px solid #e5e7eb;
  background: #fff;
}

:deep(.milestone-tabs > .el-tabs__header .el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: #e5e7eb;
}

:deep(.milestone-tabs > .el-tabs__header .el-tabs__item) {
  height: 42px;
  padding: 0 16px !important;
  font-size: 12.5px;
  font-weight: 700;
  color: #374151;
}

:deep(.milestone-tabs > .el-tabs__header .el-tabs__item:hover) {
  color: #3b82f6;
}
:deep(.milestone-tabs > .el-tabs__header .el-tabs__item.is-active) {
  color: #3b82f6;
}

:deep(.milestone-tabs > .el-tabs__header .el-tabs__active-bar) {
  background-color: #60a5fa;
  height: 2px;
}

:deep(.milestone-tabs > .el-tabs__content) {
  display: none;
}

.sub-project-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.sub-project-name {
  font-size: 13px;
  font-weight: 600;
  color: #111827;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sub-project-id {
  display: inline-flex;
  align-items: center;
  padding: 2px 7px;
  border-radius: 999px;
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  color: #6b7280;
  font-size: 11px;
  font-weight: 700;
  white-space: nowrap;
  flex-shrink: 0;
}

.sub-pl {
  font-size: 12.5px;
  color: #6b7280;
  font-weight: 600;
  white-space: nowrap;
}

/* ── 페이지네이션 ── */
.pag-wrap {
  padding: 14px;
  display: flex;
  justify-content: center;
  border-top: 1px solid #f3f4f6;
  background: #f9fafb;
}

/* ── 구성원 ── */
.member-body {
  padding: 14px;
}

.member-scroll {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.member-group {
  border: 1px solid #eef2f7;
  border-radius: 10px;
  overflow: hidden;
  background: #fff;
}

.member-group-head {
  width: 100%;
  border: none;
  background: #f9fafb;
  padding: 10px 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  cursor: pointer;
  transition: background 0.15s;
}

.member-group-head:hover {
  background: #f3f4f6;
}

.member-group-left,
.member-group-right {
  display: flex;
  align-items: center;
  gap: 9px;
}

.member-group-dot {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  flex-shrink: 0;
}

.member-group-name {
  font-size: 13px;
  font-weight: 700;
  color: #111827;
}

.member-group-count {
  font-size: 11.5px;
  color: #6b7280;
  font-weight: 700;
}

.member-group-arrow {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #fff;
  border: 1px solid #e5e7eb;
  color: #374151;
  font-size: 13px;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.member-group-list {
  padding: 4px 12px 6px;
  background: #fff;
  max-height: 240px;
  overflow-y: auto;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 4px;
  border-bottom: 1px solid #f3f4f6;
}

.member-item:last-child {
  border-bottom: none;
}

.member-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 700;
  font-size: 12px;
  flex-shrink: 0;
}

.member-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex: 1;
  gap: 8px;
  min-width: 0;
}

.member-name {
  font-size: 13px;
  font-weight: 600;
  color: #111827;
  min-width: 0;
  word-break: break-word;
}

.member-role {
  font-size: 11px;
  font-weight: 700;
  padding: 3px 8px;
  border-radius: 999px;
  flex-shrink: 0;
  white-space: nowrap;
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

/* ── 메모 ── */
.memo-body {
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: 400px;
  overflow-y: auto;
}

.btn-memo-add {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #1b5c9c 0%, #144677 100%);
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 3px 8px rgba(27, 92, 156, 0.22);
  transition: filter 0.15s;
  line-height: 1;
  padding: 0;
  flex-shrink: 0;
}

.btn-memo-add:hover {
  filter: brightness(1.1);
}

.memo-card {
  border-radius: 10px;
  padding: 11px 13px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
  border: 1px solid #e5e7eb;
  cursor: pointer;
  transition:
    transform 0.15s,
    box-shadow 0.15s;
}

.memo-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.06);
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
  margin-bottom: 5px;
}
.memo-text {
  font-size: 12.5px;
  color: #1f2937;
  white-space: pre-line;
  line-height: 1.6;
  word-break: break-word;
}

.memo-del {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 12px;
  color: #9ca3af;
  padding: 0;
  line-height: 1;
  flex-shrink: 0;
  transition: color 0.15s;
}

.memo-del:hover {
  color: #dc2626;
}

/* ── empty ── */
.empty-text {
  padding: 22px 20px;
  text-align: center;
  font-size: 13px;
  color: #9ca3af;
}

/* ── 테이블 공통 ── */
:deep(.el-table) {
  --el-table-header-bg-color: #f9fafb;
}

:deep(.el-table__inner-wrapper::before),
:deep(.el-table::before) {
  display: none;
}

:deep(.el-table td.el-table__cell),
:deep(.el-table th.el-table__cell) {
  border-bottom: 1px solid #f3f4f6 !important;
}

:deep(.el-table .cell) {
  padding-left: 14px !important;
  padding-right: 14px !important;
}

:deep(.el-table__row:hover > td) {
  background: #f8fbff !important;
}

:deep(.clickable-row) {
  cursor: pointer;
}

:deep(.sub-body .el-table__inner-wrapper) {
  min-height: 200px;
}
:deep(.sub-body .el-table__body-wrapper) {
  min-height: 200px;
}
:deep(.sub-body .el-table__empty-block) {
  min-height: 200px !important;
}

:deep(.task-summary-table .el-table__row:hover > td) {
  background: #fff !important;
}

/* accordion */
.accordion-enter-active,
.accordion-leave-active {
  transition: opacity 0.2s ease;
}
.accordion-enter-from,
.accordion-leave-to {
  opacity: 0;
}

/* ── 반응형 ── */
@media (max-width: 1200px) {
  .top-grid {
    grid-template-columns: 1fr;
  }
  .right-col {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .page-container {
    padding: 16px;
  }
  .sub-header {
    padding: 11px 16px;
  }
  .page-title-card {
    flex-direction: column;
    align-items: stretch;
  }
  .right-col {
    grid-template-columns: 1fr;
  }
  .page-title {
    font-size: 22px;
  }
}
</style>
