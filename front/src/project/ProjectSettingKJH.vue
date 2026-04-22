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
            <span class="bc-sep">›</span>
            <span>프로젝트</span>
            <span class="bc-sep">›</span>
            <span class="bc-cur">설정</span>
          </div>
        </div>

        <div class="page-container">
          <!-- 상단 제목 카드 -->
          <div class="pg-row">
            <div class="pg-left">
              <h1 class="pg-title">프로젝트 설정</h1>
              <div class="proj-meta">
                <span class="proj-name">【 {{ name }} 】</span>
                <span class="proj-desc">
                  프로젝트 기본 정보와 구성원을 관리할 수 있습니다.
                </span>
              </div>
            </div>
          </div>

          <!-- 기본 설정 패널 -->
          <div class="panel">
            <div class="panel-head">
              <span class="panel-title">기본 설정</span>

              <div class="panel-head-actions">
                <button @click="openModfyModal" class="btn-primary">
                  수정
                </button>
                <button @click="handleGoBack" class="btn-line">돌아가기</button>
              </div>
            </div>

            <div class="panel-body setting-body">
              <div class="setting-table-wrap">
                <table class="setting-table">
                  <tbody>
                    <tr>
                      <th>프로젝트 이름</th>
                      <td>{{ projectInfo.projectName || "-" }}</td>
                      <th>식별자</th>
                      <td>{{ projectInfo.identifier || "-" }}</td>
                    </tr>
                    <tr>
                      <th>시작일</th>
                      <td>{{ projectInfo.startDate || "-" }}</td>
                      <th>종료일</th>
                      <td>{{ projectInfo.endDate || "-" }}</td>
                    </tr>
                    <tr>
                      <th>PM</th>
                      <td>{{ projectInfo.pmUserId || "-" }}</td>
                      <th>총괄 PL</th>
                      <td>{{ projectInfo.plUserId || "-" }}</td>
                    </tr>
                    <tr>
                      <th>마일스톤 사용</th>
                      <td>{{ projectInfo.useMilestone || "-" }}</td>
                      <th>공개 여부</th>
                      <td>{{ projectInfo.isPublic || "-" }}</td>
                    </tr>
                    <tr class="desc-row">
                      <th>프로젝트 설명</th>
                      <td colspan="3">
                        <div class="desc-text">
                          {{ projectInfo.description || "설명이 없습니다." }}
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <!-- 구성원 패널 -->
          <div class="panel">
            <div class="panel-head member-panel-head">
              <div class="member-head-left">
                <span class="panel-title">구성원</span>
                <span class="count-badge">{{ memberList.length }}명</span>
              </div>

              <button @click="openMemberModal" class="btn-member-add">
                구성원 추가
              </button>
            </div>

            <div class="panel-body table-body">
              <el-tabs v-model="activeRoleTab" @tab-click="handleClick">
                <el-tab-pane
                  v-for="tab in roleTabs"
                  :key="tab.key"
                  :name="tab.key"
                  lazy
                >
                  <template #label>
                    {{ tab.label }}
                    <span class="tab-count">{{
                      tab.key === "ALL"
                        ? memberList.length
                        : memberList.filter(
                            (member) => (member.roleName || "기타") === tab.key,
                          ).length
                    }}</span>
                  </template>

                  <div class="table-inner-wrap">
                    <el-table
                      :data="filteredMemberList"
                      style="width: 100%"
                      :header-cell-style="headerStyle"
                      :cell-style="cellStyle"
                    >
                      <el-table-column
                        prop="userName"
                        label="이름"
                        width="180"
                        align="center"
                      />
                      <el-table-column
                        prop="userId"
                        label="사번"
                        width="180"
                        align="center"
                      />
                      <el-table-column
                        prop="roleName"
                        label="역할"
                        width="180"
                        align="center"
                        v-if="activeRoleTab === 'ALL'"
                      />
                      <el-table-column
                        prop="email"
                        label="이메일"
                        min-width="360"
                        align="center"
                      />

                      <el-table-column
                        fixed="right"
                        label="삭제"
                        width="140"
                        align="center"
                      >
                        <template #default="scope">
                          <el-button
                            size="small"
                            type="danger"
                            @click="handleDelete(scope.row)"
                          >
                            삭제
                          </el-button>
                        </template>
                      </el-table-column>

                      <template #empty>
                        <div class="table-empty">
                          <el-empty
                            description="해당 역할의 구성원이 없습니다."
                          />
                        </div>
                      </template>
                    </el-table>

                    <div
                      class="pagination-wrap"
                      v-if="filteredMemberList.length > 0"
                    >
                      <el-pagination
                        v-model:current-page="currentPage"
                        :page-size="pageSize"
                        :total="totalCount"
                        layout="prev, pager, next"
                        hide-on-single-page="true"
                        background
                      />
                    </div>
                  </div>
                </el-tab-pane>
              </el-tabs>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>

  <ProjectModifyModal
    v-model="ModifyProjectModalOpen"
    :originInfo="projectInfo"
    @handle-cancel="closeModifyMdoal"
    @modify-Info="modifyProject"
  />

  <ProjectMemberModal
    v-model="MemberModalOpen"
    @member-cancel="closeMemberMdoal"
    @member-insert="memberInsert"
    :memberList="memberList"
    :generalGroupList="generalGroupList"
    :projectGroupList="projectGroupList"
  />
</template>

<script setup>
import { onBeforeMount, ref, watch, computed } from "vue";
import { usetaskKJHStore } from "../stores/taksKJH";
import { useProjectKJHStore } from "../stores/projectKJH";
import { useAuthStore } from "../stores/auth";
import { useRoute, useRouter } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import ProjectModifyModal from "./ProjectModifyModal.vue";
import ProjectMemberModal from "./ProjectMemberModal.vue";
import Swal from "sweetalert2";
import { nextTick } from "vue";

const route = useRoute();
const taskStore = usetaskKJHStore();
const authStore = useAuthStore();
const projectStore = useProjectKJHStore();
const router = useRouter();
const currentPage = ref(1);
const pageSize = ref(5); // 추가
const totalCount = computed(() =>
  activeRoleTab.value === "ALL"
    ? memberList.value.length
    : filteredMemberList.length,
); // 추가

// 페이지네이션
const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return memberList.value.slice(start, end);
});

const filteredMemberList = computed(() => {
  if (activeRoleTab.value === "ALL") return paginatedData.value;
  return memberList.value.filter(
    (member) => (member.roleName || "기타") === activeRoleTab.value,
  );
});

const sidebarOpen = ref(false);

const name = ref();
const projectInfo = ref({
  createdAt: null,
  createdBy: null,
  description: null,
  editorUserId: null,
  endDate: null,
  identifier: null,
  isPublic: null,
  parentProjectId: null,
  plUserId: null,
  pmUserId: null,
  projectId: null,
  projectName: null,
  startDate: null,
  status: null,
  updatedAt: null,
  useMilestone: null,
});

const id = route.params.projectId;

const ModifyProjectModalOpen = ref(false);
const MemberModalOpen = ref(false);
const memberList = ref([]);
const generalGroupList = ref([]);
const projectGroupList = ref([]);

const activeRoleTab = ref("ALL");

const roleTabs = computed(() => {
  const tabs = [{ key: "ALL", label: "전체" }];
  const roleMap = new Map();

  memberList.value.forEach((member) => {
    const roleName = member.roleName || "기타";
    if (!roleMap.has(roleName)) {
      roleMap.set(roleName, {
        key: roleName,
        label: roleName,
      });
    }
  });

  return [...tabs, ...Array.from(roleMap.values())];
});

// 돌아가기버튼
const handleGoBack = () => {
  router.back();
};

const headerStyle = () => ({
  background: "#f9fafb",
  color: "#4b5563",
  fontSize: "12px",
  fontWeight: "600",
  height: "44px",
  borderBottom: "1px solid #eef2f7",
});

const cellStyle = () => ({
  color: "#1f2937",
  fontSize: "13px",
  padding: "10px 0",
});

onBeforeMount(async () => {
  await taskStore.getProjectName(id);
  name.value = taskStore.projectName.projectName;

  await projectStore.getProjectInfo(id);
  projectInfo.value = projectStore.projectInfo;

  await projectStore.getAllMembers(id);
  memberList.value = projectStore.memberList;

  await projectStore.getGeneralGroupMem();
  generalGroupList.value = projectStore.generalGroupMem;

  await projectStore.getProjectGroupMem();
  projectGroupList.value = projectStore.projectGroupMem;
});

const openModfyModal = () => {
  ModifyProjectModalOpen.value = true;
};

const closeModifyMdoal = () => {
  ModifyProjectModalOpen.value = false;
};

const modifyProject = async (form) => {
  const payload = {
    projectId: form.projectId,
    projectName: form.projectName,
    identifier: form.identifier,
    plUserId: form.plUserId,
    startDate: form.startDate,
    endDate: form.endDate,
    description: form.description,
    useMilestone: form.useMilestone ? "O1" : "O2",
    isPublic: form.isPublic ? "P1" : "P2",
    editorUserId: authStore.user.userId,
  };

  await projectStore.modifyProject(payload);
  projectInfo.value = projectStore.modifiedInfo;
  closeModifyMdoal();
};

const openMemberModal = () => {
  MemberModalOpen.value = true;
};

const closeMemberMdoal = () => {
  MemberModalOpen.value = false;
};

const memberInsert = async (value) => {
  const list = [];
  value.forEach((val) => {
    list.push({
      userId: val.userId,
      projectId: id,
      roleId: val.roleId,
    });
  });

  await projectStore.registerProjectMem(list);
  closeMemberMdoal();
};

const handleDelete = async (val) => {
  const result = await Swal.fire({
    title: "정말 구성원을 삭제하시겠습니까?",
    text: "삭제된 구성원은 목록에서 확인 불가능합니다.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "삭제",
    cancelButtonText: "취소",
    reverseButtons: true,
  });

  if (!result.isConfirmed) return;

  await projectStore.removeMem(val);

  memberList.value = projectStore.remainMem;
  if (paginatedData.value.length == 0) {
    currentPage.value = 1;
  }
};

// 스크롤 위치를 고정하는 함수
const handleBeforeLeave = () => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop;

  // 다음 틱에서 스크롤 위치를 다시 복구
  nextTick(() => {
    window.scrollTo(0, scrollTop);
  });

  return true; // true를 반환해야 탭이 바뀝니다.
};

const handleClick = (pane) => {
  // 1. 현재 스크롤 위치 저장
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop;

  // 2. 페이지 초기화 (변수가 선언되어 있어야 함)
  if (typeof currentPage !== "undefined") {
    currentPage.value = 1;
  }

  // 3. 탭 전환 후 스크롤 복구
  nextTick(() => {
    window.scrollTo(0, scrollTop);
  });
};

watch(
  () => projectStore.insertedList,
  () => {
    memberList.value = projectStore.insertedList;
  },
);
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

/* 상단 제목 카드 */
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

.proj-desc {
  font-size: 13px;
  color: #6b7280;
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

.panel-head-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto;
}

.panel-title {
  font-size: 15px;
  font-weight: 700;
  color: #111827;
}

.panel-body {
  background: #fff;
}

/* 버튼 */
.btn-primary {
  height: 36px;
  padding: 0 16px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #1b5c9c 0%, #144677 100%);
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 12px rgba(27, 92, 156, 0.22);
}

.btn-line {
  height: 36px;
  padding: 0 18px;
  border: 1px solid #d6e4f2;
  border-radius: 10px;
  background: #fff;
  color: #1b5c9c;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-line:hover {
  background: #eff6ff;
  border-color: #bfd3f6;
}

.btn-primary:hover {
  transform: translateY(-1px);
  filter: brightness(1.05);
}

.btn-member-add {
  height: 36px;
  padding: 0 16px;
  border: 1px solid #d6e4f2;
  border-radius: 10px;
  background: #fff;
  color: #1b5c9c;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-member-add:hover {
  background: #eff6ff;
  border-color: #bfd3f6;
}

/* 기본설정 */
.setting-body {
  padding: 16px 20px 18px;
}

.setting-table-wrap {
  width: 100%;
  overflow-x: auto;
}

.setting-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
  border-top: 1px solid #e5e7eb;
  border-bottom: 1px solid #e5e7eb;
}

.setting-table th,
.setting-table td {
  padding: 13px 16px;
  border-bottom: 1px solid #eef2f7;
  vertical-align: middle;
  font-size: 13px;
}

.setting-table th {
  width: 140px;
  background: #f9fafb;
  color: #475569;
  font-weight: 700;
  text-align: left;
}

.setting-table td {
  background: #fff;
  color: #111827;
  font-weight: 500;
  word-break: break-word;
}

.setting-table tr:last-child th,
.setting-table tr:last-child td {
  border-bottom: none;
}

.desc-row th,
.desc-row td {
  padding-top: 15px;
  padding-bottom: 15px;
  vertical-align: top;
}

.desc-text {
  min-height: 84px;
  line-height: 1.7;
  color: #334155;
  white-space: pre-line;
  word-break: break-word;
}

/* 구성원 */
.member-panel-head {
  border-bottom: 1px solid #f3f4f6;
}

.member-head-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.count-badge {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 12px;
  font-weight: 700;
}

.member-tab-row {
  display: flex;
  align-items: flex-end;
  gap: 0;
  padding: 14px 20px 0;
  border-bottom: 1px solid #d7dee8;
  background: #f8fafc;
  overflow-x: visible;
  overflow-y: hidden;
  min-height: 50px;
}

.member-tab {
  position: relative;
  height: 42px;
  padding: 0 18px;
  margin-right: 4px;
  border: 1px solid #bfd3f6;
  border-bottom: none;
  border-radius: 0;
  background: linear-gradient(to bottom, #dbeafe 0%, #bfdbfe 100%);
  color: #1d4ed8;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
  transition: all 0.18s ease;
}

.member-tab:hover {
  background: linear-gradient(to bottom, #e8f1ff 0%, #d6e8ff 100%);
  color: #1b5c9c;
}

.member-tab.active {
  background: #ffffff;
  color: #1b5c9c;
  border-color: #bfd3f6;
  z-index: 2;
  margin-bottom: -1px;
}

.member-tab.active::after {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  bottom: -1px;
  height: 2px;
  background: #ffffff;
}

.tab-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 10px;
  background: rgba(29, 78, 216, 0.1);
  color: inherit;
  font-size: 11px;
  font-weight: 800;
}

.table-body {
  padding: 0;
  background: #fff;
}

.table-empty {
  padding: 20px;
  text-align: center;
}

/* element table */
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

:deep(.input) {
  border-radius: 10px !important;
  border: 1px solid #e2e8f0 !important;
  background: #f8fafc !important;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
  font-size: 13px;
}

:deep(.input:focus) {
  border-color: #94a3b8 !important;
  box-shadow: 0 0 0 3px rgba(148, 163, 184, 0.15) !important;
  background: #fff !important;
  outline: none;
}

:deep(.input:disabled) {
  background: #f1f5f9 !important;
  color: #475569 !important;
}

:deep(select.input) {
  border-radius: 10px !important;
  appearance: auto !important;
  -webkit-appearance: auto !important;
  padding-right: 28px !important;
}

:deep(textarea.input) {
  border-radius: 10px !important;
}

@media (max-width: 768px) {
  .sub-header {
    padding: 12px 16px 0;
  }

  .page-container {
    padding: 16px;
    gap: 16px;
  }

  .pg-row {
    padding: 16px;
  }

  .pg-title {
    font-size: 20px;
  }

  .panel-head {
    padding: 14px 16px;
    flex-direction: column;
    align-items: flex-start;
  }

  .setting-body {
    padding: 14px 16px 16px;
  }

  .setting-table,
  .setting-table tbody,
  .setting-table tr,
  .setting-table th,
  .setting-table td {
    display: block;
    width: 100%;
  }

  .setting-table tr {
    border-bottom: 1px solid #e5e7eb;
  }

  .setting-table th {
    border-bottom: none;
    padding: 12px 14px 4px;
    background: #fff;
    font-size: 12px;
  }

  .setting-table td {
    border-bottom: none;
    padding: 0 14px 12px;
    font-size: 13px;
  }

  .desc-row td {
    padding-bottom: 14px;
  }

  .member-head-left {
    width: 100%;
  }

  .btn-primary,
  .btn-member-add {
    width: 100%;
  }

  .member-tab-row {
    padding: 12px 16px;
  }

  .member-tab {
    height: 40px;
    padding: 0 14px;
    font-size: 12px;
  }
}
:deep(.el-tabs__item) {
  outline: none !important;
  box-shadow: none !important;
}
:deep(.el-tabs__content) {
  min-height: 50px; /* 테이블 5줄+페이지네이션이 들어갈 정도의 높이 */
  overflow: auto; /* 내부 요소가 잘리지 않도록 설정 */
}

/* 테이블이 로딩되거나 데이터가 바뀔 때 높이가 튀는 것 방지 */
:deep(.el-table) {
  min-height: 50px;
}
:deep(
  .el-tabs--top > .el-tabs__header .el-tabs__item:nth-child(2),
  .el-tabs--bottom > .el-tabs__header .el-tabs__item:nth-child(2)
) {
  padding-left: 20px;
}

:deep(.el-tabs__header) {
  margin: 0px;
}

/* 페이지네이션 */
.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 16px;
  border-top: 1px solid #f3f4f6;
  background: #f9fafb;
}
</style>
