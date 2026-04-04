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

      <div class="col-span-full xl:col-span-8 mx-8 mt-3 mb-0">
        <header class="pt-4 border-b border-gray-100 dark:border-gray-700/60">
          <h1 class="text-2xl font-bold text-gray-800 dark:text-gray-100 mb-8">
            [{{ name }}]
            <span class="text-base">
              {{ projectStartDate }} ~ {{ projectendDate }}</span
            >
          </h1>
        </header>
      </div>
      <div class="grid grid-cols-12 gap-4 mx-8 mb-2">
        <div
          class="text-lg font-bold text-gray-800 dark:text-gray-100 col-start-1 col-end-4"
        >
          <h2>프로젝트 전체 업무 목록</h2>
        </div>
        <div class="col col-end-14">
          <el-button class="new-project-btn" @click="goResister()">
            + &nbsp; 업무 생성
          </el-button>
        </div>
      </div>
      <div
        class="col-span-full xl:col-span-8 bg-white dark:bg-gray-800 shadow-xs rounded-xl mx-8 mb-0 p-4"
      >
        <!-- <div
          class="w-full max-w-full bg-neutral-primary-soft p-3 border border-default rounded-base shadow-xs"
        > -->
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
                v-model="filteredList.title"
              >
                <option value="전체">전체</option>
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
                v-model="filteredList.user"
              >
                <option value="전체">전체</option>
                <option
                  :value="userId"
                  v-for="userId in filterInfo.userNameList"
                >
                  {{ userId.userName }}
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
                v-model="filteredList.type"
              >
                <option value="전체">전체</option>
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
                v-model="filteredList.status"
              >
                <option value="전체">전체</option>
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
                v-model="filteredList.start"
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
                v-model="filteredList.end"
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
                v-model="filteredList.priority"
              >
                <option value="전체">전체</option>
                <option
                  :value="priority.PriorityCode"
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
                v-model="filteredList.small"
              >
                <option value="전체">전체</option>
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
        class="col-span-full xl:col-span-8 bg-white dark:bg-gray-800 shadow-xs rounded-xl m-8 mt-4"
      >
        <div>
          <div class="flex flex-row-reverse">
            <span class="member-role-badge my-2">총 {{ listLength }}건 </span>
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
              <template v-if="!listLoading">
                <tr
                  v-for="task in taskList"
                  :key="task.id"
                  @click="goDetail(task.taskId)"
                >
                  <td class="p-2 w-80">
                    <div class="text-left">
                      {{ task.title }}
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
              </template>

              <tr v-else-if="listLoading == false">
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
  </div>
</template>

<script setup>
import { onBeforeMount, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { usetaskKJHStore } from "../stores/taksKJH";
import { changeDate } from "../utils/commonFunc";

const sidebarOpen = ref(false);
const taskStore = usetaskKJHStore();

const route = useRoute();
const router = useRouter();

let taskList = ref(); // 업무 목록
let listLoading = ref(true);
const filterList = ref([]);
let name = ref(); // 프로젝트명
let projectStartDate = ref(); // 프로젝트 날짜
let projectendDate = ref(); // 프로젝트 날짜
let titleList = ref([]); // 업무명 목록
let assigneeUserIdList = ref([]); // 담당자 목록
let statusList = ref([]); // 업무 상태 목록
let taskTypeList = ref([]); // 업무 유형 목록
let priorityList = ref([]); // 우선순위 목록
let smallProjectList = ref([]); // 하위프로젝트 목록

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
  title: "전체",
  user: "전체",
  type: "전체",
  status: "전체",
  start: null,
  end: null,
  priority: "전체",
  small: "전체",
});

const workPage = ref(1);
const listNum = ref(10);
const real = ref(true);
const nowPage = ref(1);

let listLength = ref();

// 필터링 조건들
let id = route.params.projectId;

onBeforeMount(async () => {
  console.log("프로젝트 번호", id);

  // 프로젝트 이름 조회
  await taskStore.getProjectName(id);
  const projectInfo = taskStore.projectName;
  name.value = projectInfo.projectName;
  projectStartDate.value = projectInfo.startDate;
  projectendDate.value = projectInfo.startDate;
  listLength.value = projectInfo.taskCounts;

  // 전체 목록 조회
  let obj = { projectId: id, parentProjectId: id };
  await taskStore.getAllTask(obj);

  listLoading.value = false;

  taskList.value = taskStore.taskAllList;
  changeDateType(taskList.value);
  // 필터링 조건 조회
  await taskStore.getAllFilterInfo(id);
  filterInfo.value = taskStore.filterInfo;
});

// 페이지네이션
const handleCurrentChange = async (val) => {
  if (nowPage.value != val) {
    nowPage.value = val;
    listLoading.value = true;

    let start = (val - 1) * listNum.value + 1;
    let end = val * listNum.value;

    // 페이지 변환 목록 조회
    let obj = {
      projectId: id,
      parentProjectId: id,
      startNum: start,
      endNum: end,
    };
    await taskStore.getAllTask(obj);
    taskList.value = taskStore.taskAllList;

    await changeDateType(taskList.value);
    listLoading.value = false;
  }
};

// 업무생성 버튼
const goResister = () => {
  router.push({ name: "taskRegister" });
};

// 초기화 버튼
const resetForm = async () => {
  await handleCurrentChange(1);

  filteredList.value = {
    title: "전체",
    user: "전체",
    type: "전체",
    status: "전체",
    start: null,
    end: null,
    priority: "전체",
    small: "전체",
  };

  console.log("업무목록", filterList.value.length);
};

// 업무 상세페이지 이동
const goDetail = (val) => {
  console.log(val);
  router.push({ name: "taskDetail", params: { taskId: val } });
};

// 날짜 null 일 경우 형식 변경
const changeDateType = (val) => {
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
</style>
