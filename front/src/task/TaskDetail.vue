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
          <!-- 페이지 타이틀 -->
          <div class="mb-6 proj-title-row">
            <div class="proj-title-left">
              <h2
                class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
              >
                업무 상세
              </h2>
              <div class="proj-name-row">
                <span class="proj-name">【 {{ taskInfo.projectName }} 】</span>
                <span class="proj-period">
                  {{ taskInfo.estStartDate }} - {{ taskInfo.estEndDate }}
                </span>
              </div>
            </div>
          </div>
          <!-- ────────── 상단: 업무 통합 현황 + 우측 카드 ────────── -->
          <div class="dashboard-top mb-5">
            <!-- 업무 통합 현황 -->
            <div>
              <div class="flex flex-row gap-10">
                <button @click="goBack" type="button" class="btn-navy mb-4">
                  돌아가기
                </button>
              </div>
              <div class="card main-col">
                <div class="card-header">
                  <div>
                    <span class="card-title" v-for="info in taskPjList"
                      >{{ info }} >
                    </span>
                    <span class="card-title">{{ taskInfo.title }}</span>
                  </div>
                  <el-tag size="Default"
                    >#{{ taskInfo.taskId }} {{ taskInfo.typeName }}</el-tag
                  >
                </div>

                <el-descriptions :column="2" border class="table-inner-wrap">
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">상태</div>
                    </template>
                    {{ taskInfo.statusName }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">유형</div>
                    </template>
                    {{ taskInfo.typeName }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">우선 순위</div>
                    </template>
                    {{ taskInfo.priorityName }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">담당자</div>
                    </template>
                    {{ taskInfo.assigneeUserName }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">업무 등록일</div>
                    </template>
                    {{ taskInfo.createdAt }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">추정 시간</div>
                    </template>
                    {{ taskInfo.estimatedHours }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">예정 시작일</div>
                    </template>
                    {{ taskInfo.estStartDate }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">예정 종료일</div>
                    </template>
                    {{ taskInfo.estEndDate }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">시작일</div>
                    </template>
                    {{ taskInfo.startDate }}
                  </el-descriptions-item>
                  <el-descriptions-item>
                    <template #label>
                      <div class="cell-item">완료일</div>
                    </template>
                    {{ taskInfo.dueDate }}
                  </el-descriptions-item>
                </el-descriptions>
              </div>
              <div class="card main-col mt-3">
                <div class="card-header">
                  <span class="card-title">업무 설명</span>
                </div>
                <div class="min-h-48 text-base px-4 py-2">
                  <textarea
                    v-model="taskInfo.content"
                    rows="5"
                    class="input w-full"
                    disabled=""
                    v-if="taskInfo.content != null"
                  />

                  <textarea
                    v-model="taskInfo.content"
                    rows="5"
                    class="input w-full"
                    disabled=""
                    v-else
                  />
                </div>
              </div>
              <!-- ────────── 하단: 프로젝트 목록 ────────── -->
              <div class="card main-col mt-3">
                <el-tabs
                  v-model="activeName"
                  @tab-click="handleClick"
                  default-value="first"
                >
                  <el-tab-pane label="작업이력" name="first">
                    <div class="table-inner-wrap">
                      <el-table
                        v-loading="loadingProjects"
                        :data="pagedTimeData"
                        style="width: 100%"
                        :header-cell-style="headerStyle"
                        :cell-style="cellStyle"
                      >
                        <el-table-column
                          prop="idx"
                          label="번호"
                          width="100"
                          align="center"
                        />
                        <el-table-column
                          prop="createdAt"
                          label="일시"
                          width="200"
                          align="center"
                        />
                        <el-table-column
                          prop="userName"
                          label="작업자"
                          width="200"
                          align="center"
                        />
                        <el-table-column
                          prop="taskDesc"
                          label="내역"
                          min-width="550"
                          align="center"
                        />
                        <template #empty>
                          <div style="padding: 20px; text-align: center">
                            <el-empty description="추가된 구성원이 없습니다." />
                          </div>
                        </template>
                      </el-table>
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
                    </div>
                  </el-tab-pane>
                  <el-tab-pane label="소요시간" name="second">
                    <el-table
                      v-loading="loadingProjects"
                      :data="pagedTimeData"
                      style="width: 100%"
                      :header-cell-style="headerStyle"
                      :cell-style="cellStyle"
                    >
                      <el-table-column
                        prop="no"
                        label="번호"
                        width="100"
                        align="center"
                      />
                      <el-table-column
                        prop="workDate"
                        label="작업 일시"
                        width="200"
                        align="center"
                      />
                      <el-table-column
                        prop="userName"
                        label="작업자"
                        width="200"
                        align="center"
                      />
                      <el-table-column
                        prop="hours"
                        label="소요시간"
                        width="200"
                        align="center"
                      />
                      <el-table-column
                        prop="taskDesc"
                        label="작업 내용"
                        min-width="550"
                        align="left"
                      />
                      <template #empty>
                        <div style="padding: 20px; text-align: center">
                          <el-empty description="추가된 구성원이 없습니다." />
                        </div>
                      </template>
                    </el-table>
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
                  <el-tab-pane label="첨부파일" name="third"></el-tab-pane>
                </el-tabs>
              </div>
            </div>
            <!-- 우측 카드 묶음 -->
            <div class="side-col">
              <!-- 우측 상단 버튼들 -->
              <div class="flex justify-between">
                <button @click="registerActualTime" class="btn-navy">
                  <span class="text-lg">🕒</span>소요시간 등록
                </button>

                <button @click="goModify" class="btn-green">수정</button>
              </div>
              <!-- 진척도 -->
              <div class="card">
                <div class="card-header">
                  <span class="card-title">진척도</span>
                </div>
                <div class="progress-body">
                  <div class="demo-progress">
                    <el-progress
                      type="circle"
                      :percentage="taskInfo.progressRate"
                    />
                  </div>
                </div>
              </div>

              <!-- 소요시간 -->
              <div class="card">
                <div class="card-header">
                  <span class="card-title">소요시간</span>
                </div>
                <div class="task-body">
                  <div class="h-32 place-self-center leading-20">
                    <span class="total-number" v-if="taskInfo.actualHours > 0"
                      >{{ taskInfo.actualHours }} 시간</span
                    ><span class="total-number" v-else>0 시간</span>
                  </div>
                </div>
              </div>

              <!-- 첨부파일 -->
              <div class="card">
                <div class="card-header">
                  <span class="card-title">첨부파일</span>
                </div>
                <div class="news-body"></div>
              </div>
              <!-- 소요시간 -->
              <div class="card">
                <div class="news-btn">
                  <button
                    v-if="canCreateSubTask"
                    @click="goCreateSubTask"
                    class="btn-sub"
                  >
                    + 하위업무 생성
                  </button>
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
  ></TaskActualTimeModal>
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

const taskStore = usetaskKJHStore();
const authStore = useAuthStore();
const sidebarOpen = ref(false);

const route = useRoute();
const router = useRouter();
let taskId = ref(route.params.taskId); // 업무 번호
let projectId = ref(route.params.projectId); // 프로젝트 번호
let subId = ref(route.params.subProjectId);
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
// 업무 상세 정보
let taskPjList = ref([]); // 프로젝트 이름 배열
let activityList = ref([]); // 작업내역
let activeName = ref("first"); // 선택된 탭

const loadingProjects = ref(false);

onBeforeMount(async () => {
  // 업무 상세목록 조회
  await taskStore.getTaskById(taskId.value);

  taskInfo.value = { ...taskStore.taskDetail };
  taskInfo.value.createdAt = changeDate(taskInfo.value.createdAt);

  //time_entries 합계가 있으면 표시
  // if (taskInfo.value.totalTimeEntries > 0) {
  //   taskInfo.value.actualHours = taskInfo.value.totalTimeEntries;
  // }
  taskInfo.value.actualHours =
    taskInfo.value.totalTimeEntries > 0
      ? taskInfo.value.totalTimeEntries
      : (taskInfo.value.actualHours ?? 0);

  // 상위 프로젝트가 없을 때 구분
  if (taskInfo.value.parentProjectName != null) {
    taskPjList.value = [
      taskInfo.value.parentProjectName,
      taskInfo.value.projectName,
    ];
  } else {
    taskPjList.value = [taskInfo.value.projectName];
  }

  // 작업내역 조회
  chageTaskDesc();

  // 소요시간 목록 조회
  await taskStore.getTimeEntries(taskId.value);
  timeEntriesList.value = taskStore.timeEntriesList;
});

// 소요시간 등록 버튼(모달 오픈)
const openTimeModal = ref(false);
const timeRegisterUser = ref({});
const registerActualTime = () => {
  openTimeModal.value = true;

  // 모달창 전달 정보(props)
  timeRegisterUser.value = {
    userId: authStore.user.userId,
    userName: authStore.user.name,
    taskId: taskInfo.value.taskId,
    taskTitle: taskInfo.value.title,
  };
};

// 하위업무 등록
// 현재 로그인 판별
const isAssignee = computed(
  () =>
    Number(taskInfo.value.assigneeUserId) ===
    Number(authStore.user?.userId || authStore.user?.id),
);
// 반려+ 담당자일 때만 하위업무 버튼 노출
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

// 목록으로 버튼
const goBack = () => {
  router.back();
};

// 수정 버튼(업무 수정 페이지로 이동)
const goModify = () => {
  router.push({
    name: "taskModify",
    params: { 
      projectId: taskInfo.value.projectId,  
      taskId: taskId.value 
    },
    query: { subProjectId: subId.value }  
  });
};

// 모달창 등록시
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

  // 소요시간 등록
  await taskStore.registerTimeEntries(obj);
  timeEntriesList.value = taskStore.timeEntriesList;

  // 소요시간 목록 최신화
  // taskInfo.value.actualHours += Number(val.hours);
  // console.log("소요시간 우측: ", taskInfo.value.actualHours);

  //등록 후 합계를 다시 계산해서 반영 => 누적 합계
  taskInfo.value.actualHours = timeEntriesList.value.reduce(
    (sum, entry) => sum + Number(entry.hours ?? 0),
    0,
  );

  chageTaskDesc();
};

// 작업이력 형식 변경
const chageTaskDesc = async () => {
  await taskStore.getActivityLogs(taskId.value);
  activityList.value = taskStore.activityList;

  activityList.value.forEach((el) => {
    if (el.targetType == "time_entries") {
      if (el.actionType == "J1") {
        if (el.beforeValue == null) {
          el.beforeValue = 0;
        }
        el.taskDesc = `소요시간을 ${el.beforeValue}시간에서 ${el.afterValue}시간으로 변경 했습니다.`;
      }
    }
  });

  if (activeName.value == "first") {
    tableList.value = activityList.value;
  }
};

// 탭 선택시
const handleClick = (tab) => {
  let name = tab.props.name;
  if (name == "first") {
    tableList.value = activityList.value;
  } else if (name == "second") {
    tableList.value = timeEntriesList.value;
  } else if (name == "third") {
  }
};

// 페이지네이션
const timeEntriesPage = ref(1);
const pagedtimeEntries = ref(5);
const workPageSize = 5;
const tableList = ref([]);

const pagedTimeData = computed(() => {
  const s = (timeEntriesPage.value - 1) * workPageSize;
  return tableList.value.slice(s, s + workPageSize).map((item, index) => ({
    ...item,
    no: s + index + 1, //번호 칼럼 없다면 no 빼도 됨
  }));
});

// ── 테이블 공통 스타일 ─────────────────────────────
const headerStyle = () => ({
  background: "#f8fafc",
  color: "#64748b",
  fontSize: "12px",
  fontWeight: "600",
  borderBottom: "1px solid #e2e8f0",
});
const cellStyle = () => ({
  fontSize: "13px",
  color: "#374151",
  borderBottom: "1px solid #f1f5f9",
});
</script>

<style scoped>
/* ── 상단 레이아웃 ── */
.dashboard-top {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 230px;
  gap: 20px;
  align-items: start;
}
.side-col {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ── 카드 공통 ── */
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
.toggle-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #555;
  cursor: pointer;
}

/* ── 업무 통합 현황 ── */
.total-cell {
  font-weight: 600;
  color: #2563eb;
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 12px 0;
  border-top: 1px solid #f0f0f0;
}
.new-project-btn-wrap {
  padding: 12px 20px 16px;
}
.new-project-btn {
  width: 100%;
  background: #c7d9f5;
  border: none;
  color: #1a1a2e;
  font-weight: 500;
  font-size: 14px;
  border-radius: 8px;
  height: 40px;
}
.new-project-btn:hover {
  background: #a8c4ef;
}

/* ── 나의 업무 현황 / 새 소식 ── */

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
.progress-body {
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-self: center;
  gap: 16px;
}
.news-body {
  padding: 16px 20px;
}
.new-btn {
  display: flex;
}

.total-number {
  font-size: 22px;
  font-weight: 700;
  color: #1d4ed8;
}
.dot-list {
  list-style: none;
  margin: 0;
  padding: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.dot-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
}
.dot-left {
  display: flex;
  align-items: center;
  gap: 8px;
}
.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
}
.dot-label {
  color: #555;
}
.dot-count {
  font-weight: 600;
  color: #1a1a2e;
}

/* ── 프로젝트 목록 ── */
.progress-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
}
.progress-text {
  font-size: 12px;
  font-weight: 600;
  color: #2563eb;
  min-width: 32px;
}
:deep(.el-table__body-wrapper .el-table__row) {
  cursor: pointer;
}

/* ── ElementPlus 오버라이드 ── */
:deep(.el-table) {
  --el-table-border-color: #f1f5f9;
  --el-table-header-bg-color: #f8fafc;
}
:deep(.el-table__row:hover > td) {
  background: #f0f7ff !important;
}
:deep(.el-progress-bar__outer) {
  background: #e0eaff;
}

/* el-decription */

.cell-item {
  display: flex;
  align-items: center;
}

/* 제일 하부 탭 */
.demo-tabs > .el-tabs__content {
  padding: 32px;
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
  margin: 0px;
}

:deep(.demo-tabs > .el-tabs__header) {
  margin: 0px;
}

:deep(.el-tabs--top > .el-tabs__header) {
  padding-left: 20px;
  padding-right: 20px;
}

:deep(.el-tabs__header) {
  margin: 0px;
}
/* ── 반응형 ── */
@media (max-width: 1024px) {
  .dashboard-top {
    grid-template-columns: minmax(0, 1fr);
  }
  .side-col {
    flex-direction: row;
  }
  .side-col .card {
    flex: 1;
  }
}
@media (max-width: 600px) {
  .side-col {
    flex-direction: column;
  }
}

/* 버튼들 */
.btn-select {
  height: 38px;
  padding: 0 16px;
  font-size: 13px;
  font-weight: 500;
  border-radius: 10px;
  cursor: pointer;
  white-space: nowrap;
  background: #fff;
  border: 1px solid #e2e8f0;
  color: #475569;
  transition: all 0.2s;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}
.btn-select:hover {
  background: #f8fafc;
  border-color: #94a3b8;
  color: #1e293b;
}
.btn-confirm {
  height: 38px;
  padding: 0 16px;
  font-size: 13px;
  font-weight: 500;
  border-radius: 10px;
  cursor: pointer;
  white-space: nowrap;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #475569;
  transition: all 0.2s;
}
.btn-confirm:hover {
  background: #e2e8f0;
  color: #1e293b;
}
.btn-navy {
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  background: #1e3a5f;
  color: #fff;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(30, 58, 95, 0.25);
  line-height: 38px;
}
.btn-navy:hover {
  background: #162d4a;
  box-shadow: 0 4px 10px rgba(30, 58, 95, 0.3);
  transform: translateY(-1px);
}
.btn-green {
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  background: #1882c9;
  color: #fff;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(22, 163, 74, 0.25);
}
.btn-green:hover {
  background: #60aee2;
  box-shadow: 0 4px 10px rgba(22, 163, 74, 0.3);
  transform: translateY(-1px);
}
.btn-sub {
  width: 100%;
  flex: 1;
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  background: #7c3aed;
  color: #fff;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(124, 58, 237, 0.25);
}
.btn-sub:hover {
  background: #6d28d9;
}
/* 진척도 */
:deep(.demo-progress .el-progress--circle) {
  margin-right: 15px;
}
:deep(.el-table thead th:nth-child(5)) {
  padding-left: 15px;
}
:deep(.el-table .el-table__cell:nth-child(5)) {
  padding-left: 15px;
}

/* 테이블 */
.table-inner-wrap {
  padding: 16px 20px 8px;
  background: #ffffff;

  width: 100%;
  overflow-x: auto;
}
:deep(.table-inner-wrap .el-table),
:deep(.table-inner-wrap .el-descriptions__table) {
  border: 1px solid #e9eef5;
  border-radius: 14px;
  overflow: hidden;
  border-spacing: 0;
}

/* --- Table Styles (기존 내용 유지) --- */
:deep(.el-table th.el-table__cell) {
  background: #f8fafc !important;
}
:deep(.el-table td.el-table__cell),
:deep(.el-table th.el-table__cell) {
  border-bottom: 1px solid #eef2f7 !important;
}
:deep(.el-table__body-wrapper .el-table__row) {
  cursor: pointer;
  transition: all 0.2s ease;
}
:deep(.el-table__row:hover > td) {
  background: #f8fbff !important;
}
:deep(.el-table) {
  --el-table-border-color: #edf2f7;
  --el-table-header-bg-color: #f8fafc;
  --el-table-row-hover-bg-color: #f8fbff;
}

/* --- Descriptions Styles (Radius 해결 버전) --- */
/* 내부 테두리가 래퍼의 radius를 가리지 않도록 전체 테두리 제거 */
:deep(.el-descriptions__table.is-bordered) {
  border: none !important;
}

/* 라벨(제목) 배경색 및 폰트 설정 */
:deep(.el-descriptions__label.is-bordered-label) {
  background: #f8fafc !important;
  font-weight: 600;
  color: #475569;
  border: 1px solid #eef2f7 !important; /* 개별 셀에 테두리 부여 */
}

/* 콘텐츠(내용) 테두리 설정 */
:deep(.el-descriptions__content.is-bordered-content) {
  border: 1px solid #eef2f7 !important;
}

:deep(.el-descriptions) {
  --el-descriptions-table-border: 1px solid #eef2f7;
  --el-descriptions-item-label-background: #f8fafc;
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
</style>
