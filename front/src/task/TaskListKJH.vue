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
            <span>{{name}}</span>
            <span class="bc-sep">›</span>
            <span class="bc-cur">업무 목록</span>
          </div>
        </div>

        <div class="page-container">
          <div class="pg-row">
            <div class="pg-left">
              <div class="proj-meta">
                <span class="proj-name">{{ name }}</span>
                <span class="proj-period">
                  {{ projectStartDate }} ~ {{ projectendDate }}
                </span>
              </div>
            </div>

            <el-button class="btn-create-task" @click="goResister()">
              + 업무 생성
            </el-button>
          </div>

          <div class="panel">
            <div class="panel-head search-panel-head">
              <span class="panel-title">검색 조건</span>

              <div class="search-head-actions">
                <button type="button" @click="resetForm()" class="btn-reset">
                  초기화
                </button>
                <button type="button" @click="filteringList()" class="btn-search">
                  검색
                </button>
              </div>
            </div>

            <div class="panel-body search-body">
              <div class="search-layout">
                <!-- 1행 -->
                <div class="search-row primary-row">
                  <div class="form-item">
                    <label>업무명</label>
                    <select class="input w-full" v-model="filteredList.taskId">
                      <option value="">전체</option>
                      <option
                        v-for="title in filterInfo.taskTitleList"
                        :key="title.taskId"
                        :value="title.taskId"
                      >
                        {{ title.title }}
                      </option>
                    </select>
                  </div>

                  <div class="form-item">
                    <label>담당자 이름</label>
                    <select
                      class="input w-full"
                      v-model="filteredList.assigneeUserId"
                    >
                      <option value="">전체</option>
                      <option
                        v-for="user in filterInfo.userNameList"
                        :key="user.assigneeUserId"
                        :value="user.assigneeUserId"
                      >
                        {{ user.userName }}
                      </option>
                    </select>
                  </div>

                  <div class="form-item">
                    <label>업무 상태</label>
                    <select
                      class="input w-full"
                      v-model="filteredList.taskStatusId"
                    >
                      <option value="">전체</option>
                      <option
                        v-for="status in filterInfo.taskStatusList"
                        :key="status.taskStatusId"
                        :value="status.taskStatusId"
                      >
                        {{ status.statusName }}
                      </option>
                    </select>
                  </div>

                  <div class="form-item">
                    <label>하위 프로젝트명</label>
                    <select
                      class="input w-full"
                      v-model="filteredList.parentProjectId"
                    >
                      <option value="">전체</option>
                      <option
                        v-for="small in filterInfo.smallProjectList"
                        :key="small.projectId"
                        :value="small.projectId"
                      >
                        {{ small.projectName }}
                      </option>
                    </select>
                  </div>
                </div>

                <!-- 2행 -->
                <div class="search-row secondary-row">
                  <div class="form-item">
                    <label>업무 유형</label>
                    <select
                      class="input w-full"
                      v-model="filteredList.taskTypeId"
                    >
                      <option value="">전체</option>
                      <option
                        v-for="type in filterInfo.taskTypeList"
                        :key="type.taskTypeId"
                        :value="type.taskTypeId"
                      >
                        {{ type.typeName }}
                      </option>
                    </select>
                  </div>

                  <div class="form-item">
                    <label>우선순위</label>
                    <select
                      class="input w-full"
                      v-model="filteredList.priorityCode"
                    >
                      <option value="">전체</option>
                      <option
                        v-for="priority in filterInfo.taskPriorityList"
                        :key="priority.priorityCode"
                        :value="priority.priorityCode"
                      >
                        {{ priority.codeName }}
                      </option>
                    </select>
                  </div>

                  <div class="form-item date-range-item">
                    <label>기간</label>
                    <div class="date-range-row">
                      <input
                        v-model="filteredList.startDate"
                        type="date"
                        class="input w-full"
                      />
                      <span class="range-sep">~</span>
                      <input
                        v-model="filteredList.endDate"
                        type="date"
                        class="input w-full"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="panel">
            <div class="panel-head list-head">
              <span class="panel-title">업무 목록</span>
              <span class="count-badge">총 {{ listLength }}건</span>
            </div>

            <div class="panel-body list-body">
              <div class="table-wrap">
                <table class="task-table">
                  <thead>
                    <tr>
                      <th v-for="th in thList" :key="th">
                        <div
                          class="th-text"
                          :class="{
                            left: th === '업무명' || th === '프로젝트명',
                          }"
                        >
                          {{ th }}
                        </div>
                      </th>
                    </tr>
                  </thead>

                  <tbody>
                    <tr v-if="listLoading">
                      <td :colspan="thList.length" class="empty-cell">
                        ⌛ 로딩중입니다.
                      </td>
                    </tr>

                    <template v-else-if="listLength > 0">
                      <tr
                        v-for="task in taskList"
                        :key="task.taskId"
                        class="table-row"
                        @click="goDetail(task)"
                      >
                        <td class="task-title-cell">
                          <div class="text-left">
                            <span
                              v-if="task.parentTaskId"
                              :style="{ marginLeft: (task.level - 1) * 20 + 'px' }"
                              class="sub-task-prefix"
                            >
                              ㄴ [하위]
                            </span>
                            {{ task.title }}
                          </div>
                        </td>

                        <td class="text-center">{{ task.userName }}</td>
                        <td class="text-center">{{ task.statusName }}</td>
                        <td class="text-center">{{ task.typeName }}</td>

                        <td class="text-center">
                          <span
                            class="priority-pill"
                            :class="getPriorityClass(task.codeName)"
                          >
                            {{ task.codeName }}
                          </span>
                        </td>

                        <td>
                          <div class="progress-wrap">
                            <el-progress
                              :percentage="task.progressRate"
                              :stroke-width="8"
                              :show-text="false"
                              style="flex: 1"
                              :color="
                                task.progressRate >= 100 ? '#10b981' : '#1B5C9C'
                              "
                            />
                            <span class="progress-text">{{ task.progressRate }}%</span>
                          </div>
                        </td>

                        <td class="text-center">{{ task.startDate }}</td>
                        <td class="text-center">{{ task.dueDate }}</td>
                        <td class="project-name-cell">{{ task.projectName }}</td>
                      </tr>
                    </template>

                    <tr v-else>
                      <td :colspan="thList.length" class="empty-cell">
                        업무가 존재하지 않습니다.
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <div class="pagination-wrap">
                <el-pagination
                  :current-page="nowPage"
                  :page-size="listNum"
                  :total="listLength"
                  :hide-on-single-page="real"
                  @current-change="handleCurrentChange"
                  layout="prev, pager, next"
                  background
                />
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { onBeforeMount, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import Swal from "sweetalert2";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { usetaskKJHStore } from "../stores/taksKJH";
import { changeDate } from "../utils/commonFunc";

const sidebarOpen = ref(false);
const taskStore = usetaskKJHStore();

const route = useRoute();
const router = useRouter();

let taskList = ref([]);
let listLoading = ref(false);
const filterList = ref([]);
let name = ref();
let projectStartDate = ref();
let projectendDate = ref();

let thList = ref([
  "업무명",
  "담당자",
  "업무상태",
  "업무유형",
  "우선순위",
  "진척도",
  "시작일",
  "종료일",
  "프로젝트명",
]);

let filterInfo = ref({
  taskTitleList: [],
  userNameList: [],
  taskTypeList: [],
  taskStatusList: [],
  taskPriorityList: [],
  smallProjectList: [],
});

let filteredList = ref({
  taskId: "",
  assigneeUserId: "",
  taskTypeId: "",
  taskStatusId: "",
  startDate: "",
  endDate: "",
  priorityCode: "",
  parentProjectId: "",
});

const listNum = ref(10);
const real = ref(true);
const nowPage = ref(1);

let listLength = ref(0);

let id = route.params.projectId;
let subId = route.params.subProjectId;

onBeforeMount(async () => {
  Swal.fire({
    title: "잠시만 기다려주세요...",
    html: "데이터를 불러오는 중입니다.",
    allowOutsideClick: false,
    showConfirmButton: false,
    showCancelButton: false,
    didOpen: () => {
      Swal.showLoading();
    },
  });

  await taskStore.getProjectName(id);
  const projectInfo = taskStore.projectName;
  name.value = projectInfo.projectName;
  projectStartDate.value = projectInfo.startDate;
  projectendDate.value = projectInfo.startDate;

  if (subId) {
    filteredList.value.parentProjectId = subId;
  }

  await handleCurrentChange(1);

  await taskStore.getAllFilterInfo(id);
  filterInfo.value = taskStore.filterInfo || filterInfo.value;

  Swal.close();
});

const handleCurrentChange = async (val) => {
  nowPage.value = val;

  let start = (val - 1) * listNum.value + 1;
  let end = val * listNum.value;

  let obj = {
    projectId: id,
    startNum: start,
    endNum: end,
    ...filteredList.value,
  };

  Swal.fire({
    title: "잠시만 기다려주세요...",
    html: "데이터를 불러오는 중입니다.",
    allowOutsideClick: false,
    showConfirmButton: false,
    showCancelButton: false,
    didOpen: () => {
      Swal.showLoading();
    },
  });

  try {
    await taskStore.getAllTask(obj);
    taskList.value = taskStore.taskAllList || [];
    listLength.value =
      taskList.value.length === 0 ? 0 : taskList.value[0].taskCounts;

    if (listLength.value > 0) {
      await changeDateType(taskList.value);
    }
  } catch (err) {
    Swal.fire({
      icon: "error",
      title: "알수 없는 이유로 데이터를 가져오지 못했습니다.",
    });
  } finally {
    Swal.close();
  }
};

const goResister = () => {
  router.push({ name: "taskRegister" });
};

const filteringList = async () => {
  let count = 0;
  let bool = Object.values(filteredList.value).filter((el) => {
    count++;
    return "" == el;
  }).length;
  let search = bool == count ? false : true;

  if (search) {
    await handleCurrentChange(1);
  } else {
    const result = await Swal.fire({
      title: "검색조건이 입력되지 않았습니다.",
      text: "조건을 선택 또는 입력해주세요",
      icon: "warning",
      showCancelButton: false,
      confirmButtonText: "확인",
      reverseButtons: true,
    });

    if (!result.isConfirmed) return;
  }
};

const resetForm = async () => {
  filteredList.value = {
    taskId: "",
    assigneeUserId: "",
    taskTypeId: "",
    taskStatusId: "",
    startDate: null,
    endDate: null,
    priorityCode: "",
    parentProjectId: "",
  };

  await handleCurrentChange(1);
};

const goDetail = (task) => {
  subId =
    task.parentProjectId != null && task.projectId != null ? subId : null;

  router.push({
    name: "taskDetail",
    params: { projectId: id, subProjectId: subId, taskId: task.taskId },
  });
};

const getPriorityClass = (priorityName) => {
  const value = String(priorityName || "").trim();

  if (
    value.includes("긴급") ||
    value.includes("최우선") ||
    value.toUpperCase() === "URGENT"
  ) {
    return "priority-urgent";
  }

  if (
    value === "상" ||
    value.includes("높음") ||
    value.toUpperCase() === "HIGH"
  ) {
    return "priority-high";
  }

  if (
    value === "중" ||
    value.includes("보통") ||
    value.toUpperCase() === "MEDIUM"
  ) {
    return "priority-medium";
  }

  if (
    value === "하" ||
    value.includes("낮음") ||
    value.toUpperCase() === "LOW"
  ) {
    return "priority-low";
  }

  return "priority-default";
};

const changeDateType = (val) => {
  for (let i = 0; i < val.length; i++) {
    if (val[i].userName == null) {
      val[i].userName = "미지정";
    }
    if (val[i].startDate != null) {
      val[i].startDate = changeDate(val[i].startDate);
    } else {
      val[i].startDate = "-";
    }
    if (val[i].dueDate != null) {
      val[i].dueDate = changeDate(val[i].dueDate);
    } else {
      val[i].dueDate = "-";
    }
  }
};
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

.btn-create-task {
  background: linear-gradient(135deg, #1b5c9c 0%, #144677 100%) !important;
  color: #fff !important;
  border: none !important;
  padding: 10px 18px !important;
  height: 40px !important;
  border-radius: 8px !important;
  font-weight: 700 !important;
  box-shadow: 0 4px 14px rgba(27, 92, 156, 0.3) !important;
  transition: all 0.3s ease !important;
}

.btn-create-task:hover {
  transform: translateY(-2px);
  filter: brightness(1.08);
}

.search-panel-head {
  align-items: center;
}

.search-head-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-body {
  padding: 16px 18px;
}

.search-layout {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.search-row {
  display: grid;
  gap: 12px;
  align-items: end;
}

.primary-row {
  grid-template-columns: 1.6fr 1fr 1fr 1.2fr;
}

.secondary-row {
  grid-template-columns: 1fr 1fr 1.8fr;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.form-item label {
  font-size: 12px;
  font-weight: 700;
  color: #4b5563;
  line-height: 1.2;
}

.date-range-row {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 8px;
  align-items: center;
}

.range-sep {
  color: #94a3b8;
  font-size: 12px;
  font-weight: 700;
}

.btn-search,
.btn-reset {
  height: 36px;
  padding: 0 16px;
  font-size: 12px;
  font-weight: 700;
  border-radius: 8px;
  cursor: pointer;
}

.btn-search {
  border: none;
  background: #1b5c9c;
  color: #fff;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(27, 92, 156, 0.18);
}

.btn-search:hover {
  background: #144677;
}

.btn-reset {
  border: none;
  background: #ef4444;
  color: #fff;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.16);
}

.btn-reset:hover {
  background: #dc2626;
}

.list-head {
  align-items: center;
}

.list-body {
  padding: 0;
}

.count-badge {
  font-size: 13px;
  font-weight: 700;
  padding: 4px 12px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
}

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

.th-text.left {
  text-align: left;
  padding-left: 12px;
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

.task-title-cell {
  width: 320px;
}

.task-title-cell .text-left {
  text-align: left;
}

.project-name-cell {
  text-align: left;
  min-width: 220px;
}

.sub-task-prefix {
  color: #64748b;
  font-weight: 700;
  margin-right: 4px;
}

.progress-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 130px;
}

.progress-text {
  font-size: 12px;
  font-weight: 700;
  color: #1b5c9c;
  min-width: 40px;
  text-align: right;
}

.priority-pill {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 44px;
  height: 26px;
  padding: 0 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
  line-height: 1;
}

.priority-urgent {
  background: #fee2e2;
  color: #b91c1c;
}

.priority-high {
  background: #ffedd5;
  color: #c2410c;
}

.priority-medium {
  background: #dbeafe;
  color: #1d4ed8;
}

.priority-low {
  background: #dcfce7;
  color: #166534;
}

.priority-default {
  background: #f3f4f6;
  color: #4b5563;
}

.empty-cell {
  text-align: center;
  padding: 48px 20px !important;
  font-size: 14px;
  color: #9ca3af !important;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 16px;
  border-top: 1px solid #f3f4f6;
  background: #f9fafb;
}

:deep(.input) {
  height: 36px;
  border-radius: 8px !important;
  border: 1px solid #e2e8f0 !important;
  background: #f8fafc !important;
  transition:
    border-color 0.2s,
    box-shadow 0.2s,
    background 0.2s;
  font-size: 12px;
  padding: 0 10px;
  color: #111827;
}

:deep(.input:focus) {
  border-color: #93c5fd !important;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.12) !important;
  background: #fff !important;
  outline: none;
}

:deep(select.input) {
  appearance: auto !important;
  -webkit-appearance: auto !important;
  padding-right: 24px !important;
}

:deep(.el-pagination.is-background .btn-next),
:deep(.el-pagination.is-background .btn-prev),
:deep(.el-pagination.is-background .el-pager li) {
  border-radius: 8px;
}

@media (max-width: 1200px) {
  .search-row.primary,
  .search-row.secondary {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .search-head-actions {
    margin-left: auto;
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

  .pg-title {
    font-size: 20px;
  }

  .panel-head {
    padding: 14px 16px;
  }

  .search-panel-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .search-head-actions {
    width: 100%;
    justify-content: flex-start;
  }

  .search-body {
    padding: 14px 12px;
  }

  .search-row.primary,
  .search-row.secondary {
    grid-template-columns: 1fr;
  }

  .date-range-row {
    grid-template-columns: 1fr;
  }

  .range-sep {
    display: none;
  }

  .btn-search,
  .btn-reset,
  .btn-create-task {
    width: 100%;
  }

  .task-table th,
  .task-table td {
    white-space: nowrap;
  }
}
</style>