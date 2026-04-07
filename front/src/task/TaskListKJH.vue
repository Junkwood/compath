<template>
  <div class="flex h-screen overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden"
    >
      <!-- Header -->
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
          <!-- projectDashboard.vue와 동일한 제목 영역 -->
          <div class="mb-6 proj-title-row flex justify-between">
            <div class="proj-title-left">
              <h2
                class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
              >
                프로젝트 업무 목록
              </h2>

              <div class="proj-name-row">
                <span class="proj-name">【 {{ name }} 】</span>
                <span class="proj-period">
                  {{ projectStartDate }} ~ {{ projectendDate }}
                </span>
              </div>
            </div>
            <div class="self-end">
              <el-button class="new-project-btn" @click="goResister()">
                + &nbsp; 업무 생성
              </el-button>
            </div>
          </div>
          <div
            class="col-span-full xl:col-span-8 bg-white dark:bg-gray-800 shadow-xs rounded-xl mb-0 p-4"
          >
            <form action="#">
              <div class="grid grid-cols-4 gap-4">
                <div class="mb-4">
                  <label
                    for="taskName"
                    class="block mb-2.5 text-sm font-medium text-heading"
                    >업무명</label
                  >
                  <select
                    class="input border border-default-medium text-heading text-sm rounded-base focus:ring-brand focus:border-brand block w-full px-3 py-2.5 shadow-xs placeholder:text-body"
                    v-model="filteredList.taskId"
                  >
                    <option value="">전체</option>
                    <option
                      :value="title.taskId"
                      v-for="title in filterInfo.taskTitleList"
                    >
                      {{ title.title }}
                    </option>
                  </select>
                </div>
                <div class="mb-4">
                  <label
                    for="password"
                    class="block mb-2.5 text-sm font-medium text-heading"
                    >담당자 이름</label
                  >
                  <select
                    class="input border border-default-medium text-heading text-sm rounded-base focus:ring-brand focus:border-brand block w-full px-3 py-2.5 shadow-xs placeholder:text-body"
                    v-model="filteredList.assigneeUserId"
                  >
                    <option value="">전체</option>
                    <option
                      :value="user.assigneeUserId"
                      v-for="user in filterInfo.userNameList"
                    >
                      {{ user.userName }}
                    </option>
                  </select>
                </div>
                <div class="mb-4">
                  <label
                    for="password"
                    class="block mb-2.5 text-sm font-medium text-heading"
                    >업무 유형</label
                  >
                  <select
                    class="input bg-neutral-secondary-medium border border-default-medium text-heading text-sm rounded-base focus:ring-brand focus:border-brand block w-full px-3 py-2.5 shadow-xs placeholder:text-body"
                    v-model="filteredList.taskTypeId"
                  >
                    <option value="">전체</option>
                    <option
                      :value="type.taskTypeId"
                      v-for="type in filterInfo.taskTypeList"
                    >
                      {{ type.typeName }}
                    </option>
                  </select>
                </div>
                <div>
                  <label
                    for="password"
                    class="block mb-2.5 text-sm font-medium text-heading"
                    >업무 상태</label
                  >
                  <select
                    class="input bg-neutral-secondary-medium border border-default-medium text-heading text-sm rounded-base focus:ring-brand focus:border-brand block w-full px-3 py-2.5 shadow-xs placeholder:text-body"
                    v-model="filteredList.taskStatusId"
                  >
                    <option value="">전체</option>
                    <option
                      :value="status.taskStatusId"
                      v-for="status in filterInfo.taskStatusList"
                    >
                      {{ status.statusName }}
                    </option>
                  </select>
                </div>
              </div>
              <div class="grid grid-cols-4 gap-4">
                <div>
                  <label
                    for="password"
                    class="block mb-2.5 text-sm font-medium text-heading"
                    >시작일</label
                  >
                  <input
                    v-model="filteredList.startDate"
                    type="date"
                    class="input w-full"
                  />
                </div>
                <div>
                  <label
                    for="password"
                    class="block mb-2.5 text-sm font-medium text-heading"
                    >종료일</label
                  >
                  <input
                    v-model="filteredList.endDate"
                    type="date"
                    class="input w-full"
                  />
                </div>
                <div>
                  <label
                    for="password"
                    class="block mb-2.5 text-sm font-medium text-heading"
                    >우선순위</label
                  >
                  <select
                    class="input bg-neutral-secondary-medium border border-default-medium text-heading text-sm rounded-base focus:ring-brand focus:border-brand block w-full px-3 py-2.5 shadow-xs placeholder:text-body"
                    v-model="filteredList.priorityCode"
                  >
                    <option value="">전체</option>
                    <option
                      :value="priority.priorityCode"
                      v-for="priority in filterInfo.taskPriorityList"
                    >
                      {{ priority.codeName }}
                    </option>
                  </select>
                </div>
                <div>
                  <label
                    for="password"
                    class="block mb-2.5 text-sm font-medium text-heading"
                    >하위 프로젝트명</label
                  >
                  <select
                    class="input bg-neutral-secondary-medium border border-default-medium text-heading text-sm rounded-base focus:ring-brand focus:border-brand block w-full px-3 py-2.5 bm-2 shadow-xs placeholder:text-body"
                    v-model="filteredList.parentProjectId"
                  >
                    <option value="">전체</option>
                    <option
                      :value="small.projectId"
                      v-for="small in filterInfo.smallProjectList"
                    >
                      {{ small.projectName }}
                    </option>
                  </select>
                </div>
              </div>
              <div class="flex flex-row-reverse gap-2 mt-2">
                <button type="button" @click="filteringList()" class="btn-navy">
                  검색
                </button>
                <button type="button" @click="resetForm()" class="btn-red">
                  초기화
                </button>
              </div>
            </form>
          </div>
          <div
            class="col-span-full xl:col-span-8 bg-white dark:bg-gray-800 shadow-xs rounded-xl mt-4"
          >
            <div>
              <div class="flex flex-row-reverse">
                <span class="member-role-badge my-2"
                  >총 {{ listLength }}건
                </span>
              </div>
              <!-- Table -->
              <table class="table-auto w-full dark:text-gray-300">
                <!-- Table header -->
                <thead
                  class="text-xs uppercase text-gray-400 dark:text-gray-500 bg-gray-50 dark:bg-gray-700/50 rounded-xs"
                >
                  <tr>
                    <th class="p-2" v-for="th in thList">
                      <div class="text-center">{{ th }}</div>
                    </th>
                  </tr>
                </thead>
                <!-- Table body -->
                <tbody
                  class="text-sm font-medium divide-y divide-gray-100 dark:divide-gray-700/60"
                >
                  <tr v-if="listLoading">
                    <td :colspan="thList.length" class="text-center py-10">
                      <h5 class="text-gray-500">⌛로딩중입니다.</h5>
                    </td>
                  </tr>
                  <template v-if="!listLoading && listLength > 0">
                    <tr
                      v-for="task in taskList"
                      :key="task.id"
                      @click="goDetail(task)"
                    >
                      <td class="p-2 w-80">
                        <div class="text-left">
                          <span
                            v-if="task.parentTaskId"
                            :style="{
                              marginLeft: (task.level - 1) * 20 + 'px',
                            }"
                          >
                            ㄴ [하위] </span
                          >{{ task.title }}
                        </div>
                      </td>
                      <td class="p-2">
                        <div class="text-center">
                          {{ task.userName }}
                        </div>
                      </td>
                      <td class="p-2 w-24">
                        <div class="text-center">
                          {{ task.statusName }}
                        </div>
                      </td>
                      <td class="p-2 w-18">
                        <div class="text-center">
                          {{ task.typeName }}
                        </div>
                      </td>
                      <td class="p-2 w-18">
                        <div class="text-center">
                          {{ task.codeName }}
                        </div>
                      </td>
                      <td class="p-2">
                        <div class="progress-wrap">
                          <el-progress
                            :percentage="task.progressRate"
                            :stroke-width="8"
                            :show-text="false"
                            color="#2563eb"
                            style="flex: 1"
                          />
                          <span class="progress-text"
                            >{{ task.progressRate }}%</span
                          >
                        </div>
                      </td>
                      <td class="p-2">
                        <div class="text-center">
                          {{ task.startDate }}
                        </div>
                      </td>
                      <td class="p-2">
                        <div class="text-center">
                          {{ task.dueDate }}
                        </div>
                      </td>
                      <td class="p-2 w-80">
                        <div class="text-left">
                          {{ task.projectName }}
                        </div>
                      </td>
                    </tr>
                    <!-- <el-popover :visible="visible" placement="top" :width="180">
                  <p>Are you sure to delete this?</p>
                  <div style="text-align: right; margin: 0">
                    <el-button size="small" text @click="visible = false"
                      >cancel</el-button
                    >
                    <el-button
                      size="small"
                      type="primary"
                      @click="visible = false"
                    >
                      confirm
                    </el-button>
                  </div>
                  <template #reference>
                    <el-button @click="visible = true">Delete</el-button>
                  </template>
                </el-popover> -->
                  </template>

                  <tr v-else-if="listLoading == false && listLength == 0">
                    <td :colspan="thList.length" class="text-center py-10">
                      <h5 class="text-gray-500">업무가 존재하지 않습니다</h5>
                    </td>
                  </tr>
                </tbody>
              </table>
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

let taskList = ref(); // 업무 목록
let listLoading = ref(false);
const filterList = ref([]);
let name = ref(); // 프로젝트명
let projectStartDate = ref(); // 프로젝트 날짜
let projectendDate = ref(); // 프로젝트 날짜

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

let filterInfo = ref([]);

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

let listLength = ref();

// 필터링 조건들
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
  // 프로젝트 이름 조회
  await taskStore.getProjectName(id);
  const projectInfo = taskStore.projectName;
  name.value = projectInfo.projectName;
  projectStartDate.value = projectInfo.startDate;
  projectendDate.value = projectInfo.startDate;

  // url에 subProjectId가 있을 경우
  console.log(subId);
  if (subId) {
    filteredList.value.parentProjectId = subId;
  }
  // 전체 목록 조회
  await handleCurrentChange(1);

  // 필터링 조건 조회
  await taskStore.getAllFilterInfo(id);
  filterInfo.value = taskStore.filterInfo;

  Swal.close();
});

// 페이지네이션
const handleCurrentChange = async (val) => {
  console.log("페이징", val);
  nowPage.value = val;

  let start = (val - 1) * listNum.value + 1;
  let end = val * listNum.value;

  // 페이지 변환 목록 조회
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
    Swal.close();

    taskList.value = taskStore.taskAllList;

    listLength.value =
      taskList.value.length == 0 ? 0 : taskList.value[0].taskCounts;
  } catch (err) {
    Swal.fire({
      icon: "error",
      title: "알수 없는 이유로 데이터를 가져오지 못했습니다.",
    });
  } finally {
    Swal.close();
  }

  if (listLength.value > 0) {
    await changeDateType(taskList.value);
  }
};

// 업무생성 버튼
const goResister = () => {
  router.push({ name: "taskRegister" });
};

// 검색 버튼
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
      confirmButtonText: "활성",
      reverseButtons: true,
    });

    if (!result.isConfirmed) return;
  }
};

// 초기화 버튼
const resetForm = async () => {
  // 필터링 조건 초기화
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

  console.log("업무목록", filterList.value.length);
};

// 업무 상세페이지 이동
const goDetail = (task) => {
  console.log(task);
  subId = task.parentProjectId != null && task.projectId != null ? subId : null;

  console.log(subId);
  router.push({
    name: "taskDetail",
    params: { projectId: id, subProjectId: subId, taskId: task.taskId },
  });
};

// 날짜 null 일 경우 형식 변경
const changeDateType = (val) => {
  console.log(val);
  for (let i = 0; i < val.length; i++) {
    // 날짜 형식 변경
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
/* 상단 */
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
/* 하단 버튼 */
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
  letter-spacing: 0.01em;
}
.btn-navy:hover {
  background: #162d4a;
  box-shadow: 0 4px 10px rgba(30, 58, 95, 0.3);
  transform: translateY(-1px);
}

.btn-red {
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  background: #dc2626;
  color: #fff;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(220, 38, 38, 0.25);
  letter-spacing: 0.01em;
}
.btn-red:hover {
  background: #b91c1c;
  box-shadow: 0 4px 10px rgba(220, 38, 38, 0.3);
  transform: translateY(-1px);
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
.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 12px 0;
  border-top: 1px solid #f0f0f0;
}
.new-project-btn {
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
.member-role-badge {
  font-size: 15px;
  font-weight: 600;
  padding: 2px 7px;
  border-radius: 99px;
  letter-spacing: 0.03em;
  margin-right: 15px;
  background: #dbeafe;
  color: #1d4ed8;
}

/* 인풋 전체 라운드 */
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
  color: #94a3b8 !important;
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
:deep(.input:disabled) {
  background: #f1f5f9 !important;
  color: #475569 !important; /* #94a3b8 → #475569 으로 변경! */
}
:deep(.text-left) {
  text-align: left;
  padding-left: 15px;
}
:deep(th:nth-child(1) .text-center) {
  text-align: left;
  padding-left: 15px;
}
:deep(th:nth-child(9) .text-center) {
  text-align: left;
  padding-left: 15px;
}
tbody tr:hover {
  background-color: #f9fafb; /* 살짝 밝은 회색으로 강조 */
  cursor: pointer;
  transition: background-color 0.2s;
}
</style>
