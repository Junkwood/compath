<template>
  <div class="flex h-screen overflow-hidden">
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
            <span v-for="info in taskPjList" :key="info">{{ info }} › </span>
            <span class="bc-cur">업무 상세</span>
          </div>
        </div>

        <div class="page-container">
          <div class="pg-row">
            <div class="pg-left">
              <div class="proj-meta">
                <span class="proj-name">{{ taskInfo.projectName }}</span>
                <span class="proj-period">
                  {{ taskInfo.estStartDate }} ~ {{ taskInfo.estEndDate }}
                </span>
              </div>
            </div>
            <div class="flex gap-2 self-end">
              <button
                v-if="taskInfo.taskStatusId == 4"
                @click="goCreateSubTask"
                class="btn-action btn-sub"
              >
                + 하위업무 생성
              </button>
              <button @click="goModify" class="btn-modify">수정</button>
              <button
                class="btn-lock"
                @click="lockTask"
                v-if="taskInfo.taskStatusId == 1"
              >
                삭제
              </button>
              <button @click="goBack" type="button" class="btn-back">
                ← 돌아가기
              </button>
            </div>
          </div>

          <div class="detail-layout">
            <div class="main-col">
              <div class="panel">
                <div class="panel-head">
                  <div class="task-title-wrap">
                    <span class="panel-title">
                      <span
                        v-for="info in taskPjList"
                        :key="info"
                        class="breadcrumb-path"
                        >{{ info }} ›
                      </span>
                      {{ taskInfo.title }}
                    </span>
                    <el-tag size="Default" class="task-type-tag"
                      >#{{ taskInfo.taskId }} {{ taskInfo.typeName }}</el-tag
                    >
                  </div>
                </div>

                <div class="panel-body desc-body">
                  <el-descriptions :column="2" border class="task-descriptions">
                    <el-descriptions-item>
                      <template #label
                        ><div class="cell-item">상태</div></template
                      >
                      {{ taskInfo.statusName }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                      <template #label
                        ><div class="cell-item">유형</div></template
                      >
                      {{ taskInfo.typeName }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                      <template #label
                        ><div class="cell-item">우선 순위</div></template
                      >
                      {{ taskInfo.priorityName }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                      <template #label
                        ><div class="cell-item">담당자</div></template
                      >
                      {{ taskInfo.assigneeUserName }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                      <template #label
                        ><div class="cell-item">업무 등록일</div></template
                      >
                      {{ taskInfo.createdAt }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                      <template #label
                        ><div class="cell-item">추정 시간</div></template
                      >
                      {{ taskInfo.estimatedHours }}시간
                    </el-descriptions-item>
                    <el-descriptions-item>
                      <template #label
                        ><div class="cell-item">예정 시작일</div></template
                      >
                      {{ taskInfo.estStartDate }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                      <template #label
                        ><div class="cell-item">예정 종료일</div></template
                      >
                      {{ taskInfo.estEndDate }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                      <template #label
                        ><div class="cell-item">시작일</div></template
                      >
                      {{ taskInfo.startDate }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                      <template #label
                        ><div class="cell-item">완료일</div></template
                      >
                      {{ taskInfo.dueDate }}
                    </el-descriptions-item>
                  </el-descriptions>
                </div>
              </div>

              <div class="panel">
                <div class="panel-head">
                  <span class="panel-title">업무 설명</span>
                </div>
                <div class="panel-body content-body">
                  <textarea
                    v-model="taskInfo.content"
                    rows="5"
                    class="input w-full"
                    disabled
                  />
                </div>
              </div>

              <div class="panel">
                <div class="panel-body tab-body">
                  <el-tabs v-model="activeName" @tab-click="handleClick">
                    <el-tab-pane label="작업이력" name="first">
                      <div class="table-wrap">
                        <table class="task-table">
                          <thead>
                            <tr>
                              <th><div class="th-text">번호</div></th>
                              <th><div class="th-text">일시</div></th>
                              <th><div class="th-text">작업자</div></th>
                              <th><div class="th-text">내역</div></th>
                            </tr>
                          </thead>
                          <tbody>
                            <tr v-if="loadingProjects">
                              <td colspan="4" class="empty-cell">
                                ⌛ 로딩중입니다.
                              </td>
                            </tr>
                            <template v-else-if="pagedTimeData.length > 0">
                              <tr
                                v-for="item in pagedTimeData"
                                :key="item.idx"
                                class="table-row"
                              >
                                <td class="text-center">{{ item.idx }}</td>
                                <td class="text-center">
                                  {{ item.createdAt }}
                                </td>
                                <td class="text-center">{{ item.userName }}</td>
                                <td class="text-left">{{ item.taskDesc }}</td>
                              </tr>
                            </template>
                            <tr v-else>
                              <td colspan="4" class="empty-cell">
                                작업이력이 없습니다.
                              </td>
                            </tr>
                          </tbody>
                        </table>
                      </div>
                      <div
                        class="pagination-wrap"
                        v-if="pagedTimeData.length > 0"
                      >
                        <el-pagination
                          v-model:current-page="timeEntriesPage"
                          :page-size="pagedtimeEntries"
                          :hide-on-single-page="real"
                          :total="timeEntriesList.length"
                          layout="prev, pager, next"
                          background
                        />
                      </div>
                    </el-tab-pane>

                    <el-tab-pane label="소요시간" name="second">
                      <div class="table-wrap">
                        <table class="task-table">
                          <thead>
                            <tr>
                              <th><div class="th-text">번호</div></th>
                              <th><div class="th-text">작업 일시</div></th>
                              <th><div class="th-text">작업자</div></th>
                              <th><div class="th-text">소요시간</div></th>
                              <th><div class="th-text">작업 내용</div></th>
                            </tr>
                          </thead>
                          <tbody>
                            <tr v-if="loadingProjects">
                              <td colspan="5" class="empty-cell">
                                ⌛ 로딩중입니다.
                              </td>
                            </tr>
                            <template v-else-if="pagedTimeData.length > 0">
                              <tr
                                v-for="item in pagedTimeData"
                                :key="item.no"
                                class="table-row"
                              >
                                <td class="text-center">{{ item.no }}</td>
                                <td class="text-center">{{ item.workDate }}</td>
                                <td class="text-center">{{ item.userName }}</td>
                                <td class="text-center">{{ item.hours }}</td>
                                <td class="text-left">{{ item.taskDesc }}</td>
                              </tr>
                            </template>
                            <tr v-else>
                              <td colspan="5" class="empty-cell">
                                소요시간 기록이 없습니다.
                              </td>
                            </tr>
                          </tbody>
                        </table>
                      </div>
                      <div
                        class="pagination-wrap"
                        v-if="pagedTimeData.length > 0"
                      >
                        <el-pagination
                          v-model:current-page="timeEntriesPage"
                          :hide-on-single-page="real"
                          :page-size="pagedtimeEntries"
                          :total="timeEntriesList.length"
                          layout="prev, pager, next"
                          background
                        />
                      </div>
                    </el-tab-pane>
                  </el-tabs>
                </div>
              </div>
            </div>

            <div class="side-col">
              <div class="panel">
                <div class="panel-head">
                  <span class="panel-title">진척도</span>
                </div>
                <div class="panel-body side-panel-body">
                  <el-progress
                    type="circle"
                    :percentage="
                      taskInfo.progressRate == null ? 0 : taskInfo.progressRate
                    "
                    color="#1b5c9c"
                  />
                </div>
              </div>

              <div class="panel">
                <div class="panel-head">
                  <span class="panel-title">소요시간</span>
                  <button
                    v-if="taskInfo.taskStatusId != 5"
                    @click="registerActualTime"
                    class="btn-mini-add"
                  >
                    +
                  </button>
                </div>
                <div class="panel-body side-panel-body">
                  <span class="total-hours">
                    {{ taskInfo.actualHours > 0 ? taskInfo.actualHours : 0 }}
                    시간
                  </span>
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
  />
</template>

<script setup>
import { ref, onBeforeMount, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { usetaskKJHStore } from "../stores/taksKJH";
import { useAuthStore } from "../stores/auth";
import TaskActualTimeModal from "./TaskActualTimeModal.vue";
import { changeDate } from "../utils/commonFunc";
import Swal from "sweetalert2";

const taskStore = usetaskKJHStore();
const authStore = useAuthStore();
const sidebarOpen = ref(false);

const route = useRoute();
const router = useRouter();
let taskId = ref(route.params.taskId);
let subId = ref(route.params.subProjectId);
let projectId = ref(route.params.projectId);
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
});

let taskPjList = ref([]);
let activityList = ref([]);
let activeName = ref("first");
const loadingProjects = ref(false);

onBeforeMount(async () => {
  await taskStore.getTaskById(taskId.value);
  taskInfo.value = { ...taskStore.taskDetail };
  taskInfo.value.createdAt = changeDate(taskInfo.value.createdAt);
  taskInfo.value.actualHours =
    taskInfo.value.totalTimeEntries > 0
      ? taskInfo.value.totalTimeEntries
      : (taskInfo.value.actualHours ?? 0);

  if (taskInfo.value.parentProjectName != null) {
    taskPjList.value = [
      taskInfo.value.parentProjectName,
      taskInfo.value.projectName,
    ];

    changeDateType(taskInfo.value);
  } else {
    taskPjList.value = [taskInfo.value.projectName];
  }

  chageTaskDesc();

  await taskStore.getTimeEntries(taskId.value);
  timeEntriesList.value = taskStore.timeEntriesList;

  let obj = { projectId: projectId.value, subProjectId: subId.value };
  await taskStore.getProjectRole(obj);
});

const openTimeModal = ref(false);
const timeRegisterUser = ref({});
const registerActualTime = () => {
  openTimeModal.value = true;
  timeRegisterUser.value = {
    userId: authStore.user.userId,
    userName: authStore.user.name,
    taskId: taskInfo.value.taskId,
    taskTitle: taskInfo.value.title,
  };
};

const isAssignee = computed(() => {
  const currentUserId = authStore.user?.userId || authStore.user?.id;
  if (!currentUserId) return false;
  const isDirectAssignee =
    Number(taskInfo.value?.assigneeUserId) === Number(currentUserId);
  const isPmPl = (taskStore.plPmList?.projectRoleList || []).some(
    (item) => Number(item.userId) === Number(currentUserId),
  );
  const isManager = (taskStore.plPmList?.empList || []).some(
    (item) => Number(item.userId) === Number(currentUserId),
  );
  return isDirectAssignee || isPmPl || isManager;
});

const canCreateSubTask = computed(
  () => Number(taskInfo.value.taskStatusId) === 4 && isAssignee.value,
);
const goCreateSubTask = () => {
  router.push({
    name: "taskRegister",
    params: {
      projectId: taskInfo.value.parentProjectId || taskInfo.value.projectId,
    },
    query: { parentTaskId: taskId.value },
  });
};

const goBack = () => {
  router.back();
};
const goModify = () => {
  router.push({
    name: "taskModify",
    params: { projectId: taskInfo.value.projectId, taskId: taskId.value },
    query: { subProjectId: subId.value },
  });
};

const lockTask = async () => {
  const result = await Swal.fire({
    title: "정말 삭제하시겠습니까?",
    text: "삭제된 업무는 목록에서 확인 불가능합니다.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "삭제",
    cancelButtonText: "취소",
    reverseButtons: true,
  });

  if (!result.isConfirmed) return;

  await taskStore.modifyTaskStatus(taskId.value);

  if (taskStore.modifyResult > 0) {
    router.push({
      name: "taskList",
      params: {
        projectId: projectId.value,
        subProjectId: subId.value,
      },
    });
  }
};

const timeEntriesList = ref([]);
const submitted = async (val) => {
  openTimeModal.value = false;
  let obj = {
    taskId: taskInfo.value.taskId,
    userId: authStore.user.userId,
    workDate: val.workDate,
    hours: val.hours,
    taskDesc: val.taskDesc,
    beforeValue:
      taskInfo.value.actualHours == null ? 0 : taskInfo.value.actualHours,
  };
  await taskStore.registerTimeEntries(obj);
  timeEntriesList.value = taskStore.timeEntriesList;
  taskInfo.value.actualHours = timeEntriesList.value.reduce(
    (sum, entry) => sum + Number(entry.hours ?? 0),
    0,
  );
  chageTaskDesc();
};

const chageTaskDesc = async () => {
  await taskStore.getActivityLogs(taskId.value);
  activityList.value = taskStore.activityList;
  activityList.value.forEach((el) => {
    if (el.targetType == "time_entries" && el.actionType == "J1") {
      if (el.beforeValue == null) el.beforeValue = 0;
      el.taskDesc = `소요시간을 ${el.beforeValue}시간에서 ${el.afterValue}시간으로 변경 했습니다.`;
    }
  });
  if (activeName.value == "first") tableList.value = activityList.value;
};

const handleClick = (tab, ev) => {
  if (ev && ev instanceof Event) {
    ev.preventDefault();
    ev.stopPropagation();
  }
  let name = tab.props.name;
  if (name == "first") tableList.value = activityList.value;
  else if (name == "second") tableList.value = timeEntriesList.value;
};

const timeEntriesPage = ref(1);
const pagedtimeEntries = ref(5);
const workPageSize = 5;
const tableList = ref([]);
const real = ref(true);

const pagedTimeData = computed(() => {
  const s = (timeEntriesPage.value - 1) * workPageSize;
  return tableList.value.slice(s, s + workPageSize).map((item, index) => ({
    ...item,
    no: s + index + 1,
  }));
});

// 날짜 없는 경우 형식 변경
const changeDateType = (val) => {
  if (val.startDate == null) {
    val.startDate = "-";
  }

  if (val.dueDate == null) {
    val.dueDate = "-";
  }

  if (val.estStartDate == null) {
    val.estStartDate = "-";
  }

  if (val.estEndDate == null) {
    val.estEndDate = "-";
  }
};
</script>

<style scoped>
/* ── 기본 레이아웃 ── */
.dashboard-page {
  font-family: "Pretendard", sans-serif;
  background-color: #f3f4f6;
}

/* 서브 헤더 */
.sub-header {
  background: #fff;
  padding: 12px 24px;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 30;
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

/* 페이지 컨테이너 */
.page-container {
  padding: 24px 30px 24px 30px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 타이틀 행 */
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

/* ── 메인 레이아웃 ── */
.detail-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  gap: 24px;
  align-items: start;
}
.main-col {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.side-col {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ── 패널 공통 ── */
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
.panel-body {
  background: #fff;
}

/* 업무 제목 wrap */
.task-title-wrap {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex: 1;
  flex-wrap: wrap;
}
.breadcrumb-path {
  font-size: 13px;
  color: #6b7280;
  font-weight: 500;
}
.task-type-tag {
  white-space: nowrap;
}

/* Descriptions 패널 */
.desc-body {
  padding: 16px 20px;
}
.cell-item {
  display: flex;
  align-items: center;
  font-size: 12px;
  font-weight: 700;
  color: #4b5563;
}

/* 업무 설명 */
.content-body {
  padding: 16px 20px;
}

/* 탭 패널 */
.tab-body {
  padding: 0;
}
:deep(.el-tabs__header) {
  margin: 0;
  padding: 0 20px;
  border-bottom: 1px solid #f3f4f6;
}
:deep(.el-tabs__item) {
  font-size: 13px;
  font-weight: 600;
  color: #6b7280;
  outline: none !important;
  box-shadow: none !important;
}
:deep(.el-tabs__item.is-active) {
  color: #1b5c9c;
}
:deep(.el-tabs__active-bar) {
  background-color: #1b5c9c;
}
:deep(.el-tabs__content) {
  padding: 0;
  min-height: 100px;
}

/* ── 테이블 ── */
.table-wrap {
  width: 100%;
  overflow-x: auto;
}
.task-table {
  width: 100%;
  border-collapse: collapse;
}
.task-table thead {
  background: #f9fafb;
}
.task-table th {
  height: 46px;
  padding: 0 12px;
  font-size: 12px;
  font-weight: 600;
  color: #4b5563;
  border-bottom: 1px solid #eef2f7;
  white-space: nowrap;
}
.th-text {
  text-align: center;
}
.task-table td {
  padding: 14px 12px;
  font-size: 13px;
  color: #1f2937;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: middle;
}
.table-row {
  cursor: pointer;
  transition: background-color 0.2s ease;
}
.table-row:hover {
  background: #f9fbff;
}
.empty-cell {
  text-align: center;
  padding: 48px 20px !important;
  font-size: 14px;
  color: #9ca3af !important;
}

/* 페이지네이션 */
.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 16px;
  border-top: 1px solid #f3f4f6;
  background: #f9fafb;
}

/* ── 사이드 패널 ── */
/* 액션 패널 */
.action-panel {
  overflow: visible;
}
.action-body {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.btn-time {
  background: linear-gradient(135deg, #1b5c9c 0%, #144677 100%);
  color: #fff;
  box-shadow: 0 4px 14px rgba(27, 92, 156, 0.3);
}
.btn-time:hover {
  transform: translateY(-2px);
  filter: brightness(1.08);
}

/* 진척도 / 소요시간 패널 바디 */
.side-panel-body {
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.total-hours {
  font-size: 24px;
  font-weight: 700;
  color: #1b5c9c;
}

/* ── Descriptions 오버라이드 ── */
:deep(.task-descriptions) {
  --el-descriptions-table-border: 1px solid #eef2f7;
  --el-descriptions-item-label-background: #f9fafb;
}
:deep(.el-descriptions__label.is-bordered-label) {
  background: #f9fafb !important;
  font-weight: 600;
  color: #4b5563;
  font-size: 12px;
  border: 1px solid #eef2f7 !important;
}
:deep(.el-descriptions__content.is-bordered-content) {
  border: 1px solid #eef2f7 !important;
  font-size: 13px;
  color: #1f2937;
}
:deep(.el-descriptions__table.is-bordered) {
  border: none !important;
  border-radius: 8px;
  overflow: hidden;
}

/* ── Input 오버라이드 ── */
:deep(.input) {
  height: 40px;
  border-radius: 8px !important;
  border: 1px solid #e2e8f0 !important;
  background: #f8fafc !important;
  transition:
    border-color 0.2s,
    box-shadow 0.2s,
    background 0.2s;
  font-size: 13px;
  padding: 0 10px;
  color: #111827;
  resize: vertical;
}
:deep(textarea.input) {
  min-height: 120px;
  height: auto;
  padding: 12px 14px;
  resize: vertical;
}
:deep(.input:focus) {
  border-color: #93c5fd !important;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.12) !important;
  background: #fff !important;
  outline: none;
}
:deep(.input:disabled) {
  background: #f8fafc !important;
  color: #374151 !important;
  cursor: default;
}

/* el-progress 색상 */
:deep(.el-progress__text) {
  font-size: 16px !important;
  font-weight: 700;
  color: #1b5c9c;
}

/* el-pagination 오버라이드 */
:deep(.el-pagination.is-background .btn-next),
:deep(.el-pagination.is-background .btn-prev),
:deep(.el-pagination.is-background .el-pager li) {
  border-radius: 8px;
}
:deep(.el-pagination.is-background .el-pager li.is-active) {
  background-color: #1b5c9c;
}

/* ── 반응형 ── */
@media (max-width: 1100px) {
  .detail-layout {
    grid-template-columns: minmax(0, 1fr);
  }
  .side-col {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 768px) {
  .sub-header {
    padding: 12px 16px;
  }
  .page-container {
    padding: 16px;
    gap: 16px;
  }
  .pg-row {
    padding: 16px;
    flex-direction: column;
    align-items: flex-start;
  }
  .detail-layout {
    grid-template-columns: 1fr;
  }
  .side-col {
    grid-template-columns: 1fr;
  }
  .task-table th,
  .task-table td {
    white-space: nowrap;
  }
}
:deep(.task-table th:nth-child(1), .task-table td:nth-child(1)) {
  width: 10%;
}

.btn-top-action {
  height: 40px;
  padding: 0 18px;
  font-size: 13px;
  font-weight: 700;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

/* 소요시간 패널 내 미니 플러스 버튼 */
.btn-mini-add {
  background: linear-gradient(135deg, #1b5c9c 0%, #144677 100%) !important;
  color: #fff !important;
  border: none !important;
  width: 30px !important;
  height: 30px !important;
  font-size: 18px !important;
  font-weight: 700 !important;
  border-radius: 50% !important;
  cursor: pointer;
  padding: 0 !important;
  box-shadow: 0 4px 10px rgba(27, 92, 156, 0.22) !important;
}
.btn-mini-add:hover {
  filter: brightness(1.05);
}

:deep(.btn-modify) {
  background: linear-gradient(135deg, #1b5c9c 0%, #144677 100%) !important;
  color: white !important;
  height: 40px;
  padding: 0 18px;
  font-size: 13px;
  font-weight: 700;
  border-radius: 8px;
  cursor: pointer;
  border: 1px solid #e5e7eb;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.btn-modify:hover {
  transform: translateY(-2px);
  filter: brightness(1.08);
}
/* 돌아가기 버튼 */
.btn-back {
  height: 40px;
  padding: 0 18px;
  font-size: 13px;
  font-weight: 700;
  border-radius: 8px;
  cursor: pointer;
  border: 1px solid #e5e7eb;
  background: #fff;
  color: #374151;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}
.btn-back:hover {
  background: #f9fafb;
  border-color: #d1d5db;
  color: #111827;
}

.btn-lock {
  height: 40px;
  padding: 0 18px;
  font-size: 13px;
  font-weight: 700;
  border-radius: 8px;
  cursor: pointer;
  border: 1px solid #e5e7eb;
  transition: all 0.2s ease;
  background: #ef4444;
  color: #fff;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.2);
}

.btn-lock:hover {
  transform: translateY(-2px);
  filter: brightness(1.08);
}

.btn-sub {
  height: 40px;
  padding: 0 18px;
  font-size: 13px;
  font-weight: 700;
  border-radius: 8px;
  cursor: pointer;
  border: 1px solid #e5e7eb;
  transition: all 0.2s ease;
  background: linear-gradient(135deg, #7c3aed 0%, #5b21b6 100%);
  color: #fff;
  box-shadow: 0 4px 14px rgba(124, 58, 237, 0.3);
}
.btn-sub:hover {
  transform: translateY(-2px);
  filter: brightness(1.08);
}
</style>
