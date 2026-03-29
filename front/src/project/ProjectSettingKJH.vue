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
            설정 <span class="text-base"> [{{ name }}]</span>
          </h1>
        </header>
      </div>
      <div class="grid grid-cols-12 gap-4 mx-8 mb-2">
        <div
          class="text-lg font-bold text-gray-800 dark:text-gray-100 col-start-1 col-end-4"
        >
          <h2>기본 설정</h2>
        </div>
      </div>
      <div
        class="col-span-full xl:col-span-8 bg-white dark:bg-gray-800 shadow-xs rounded-xl mx-8 mb-0 p-4"
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
                v-model="filteredList.title"
              >
                <option value="전체">전체</option>
                <option :value="title" v-for="title in titleList">
                  {{ title }}
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
                <option :value="userId" v-for="userId in assigneeUserIdList">
                  {{ userId }}
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
                <option :value="type" v-for="type in taskTypeList">
                  {{ type }}
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
                <option :value="status" v-for="status in statusList">
                  {{ status }}
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
                <option :value="priority" v-for="priority in priorityList">
                  {{ priority }}
                </option>
              </select>
            </div>
            <div>
              <label
                for="password"
                class="block mb-2.5 text-sm font-medium text-heading"
                >프로젝트명</label
              >
              <select
                class="input bg-neutral-secondary-medium border border-default-medium text-heading text-sm rounded-base focus:ring-brand focus:border-brand block w-full px-3 py-2.5 bm-2 shadow-xs placeholder:text-body"
                v-model="filteredList.small"
              >
                <option value="전체">전체</option>
                <option :value="small" v-for="small in smallProjectList">
                  {{ small }}
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
          <div class="flex flex-row-reverse" v-if="filterList.length > 0">
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
              <template v-if="filterList.length > 0">
                <tr v-for="task in filterList" :key="task.id">
                  <td class="p-2 w-80">
                    <div class="text-center">
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
                    <div class="text-center">
                      {{ task.projectName }}
                    </div>
                  </td>
                </tr>
              </template>

              <tr v-else-if="filterList.length == 0 && listLoading == false">
                <td :colspan="thList.length" class="text-center py-10">
                  <h5 class="text-gray-500">업무가 존재하지 않습니다</h5>
                </td>
              </tr>
            </tbody>
          </table>
          <el-table
            :data="pagedTaskData"
            style="width: 100%"
            :header-cell-style="headerStyle"
            :cell-style="cellStyle"
          >
            <el-table-column prop="no" label="번호" width="70" align="center" />
            <el-table-column prop="projectName" label="이름" width="80" />
            <el-table-column
              prop="created"
              label="사번"
              width="70"
              align="center"
            />
            <el-table-column
              prop="inProgress"
              label="이메일"
              width="70"
              align="center"
            />
            <el-table-column
              prop="devDone"
              label="역할"
              width="90"
              align="center"
            />
            <el-table-column
              prop="rejected"
              label="삭제"
              width="70"
              align="center"
            />
          </el-table>
          <div class="pagination-wrap" v-if="workPageSize / listNum > 1">
            <el-pagination
              v-model:current-page="workPage"
              :current-page="nowPage"
              :page-size="listNum"
              :total="workPageSize"
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
import axios from "axios";

import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { usetaskKJHStore } from "../stores/taksKJH";

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
const workPageSize = ref(0);
const real = ref(true);
const nowPage = ref(1);

let listLength = ref();

// 필터링 조건들
let id = route.params.id;

onBeforeMount(async () => {
  console.log("프로젝트 번호", id);

  // 프로젝트 이름 조회
  await taskStore.getProjectName(id);

  name.value = taskStore.projectName.projectName;
  projectStartDate.value = changeDateType(taskStore.projectName.startDate);
  projectendDate.value = changeDateType(taskStore.projectName.startDate);
  console.log("프로젝트명 ", name.value);

  // 전체 목록 조회
  await taskStore.getAllTask(id);

  listLoading.value = false;

  taskList.value = taskStore.taskAllList;

  for (let i = 0; i < taskList.value.length; i++) {
    // 날짜 형식 변경
    taskList.value[i].startDate = changeDateType(taskList.value[i].startDate);
    taskList.value[i].dueDate = changeDateType(taskList.value[i].dueDate);

    // 필터링 조건들 구분
    // 업무명
    if (titleList.value.length < 1) {
      titleList.value.push(taskList.value[i].title);
    } else if (titleList.value.indexOf(taskList.value[i].title) == -1) {
      titleList.value.push(taskList.value[i].title);
    }

    // 담당자명
    if (taskList.value[i].userName != null) {
      if (assigneeUserIdList.value.length < 1) {
        assigneeUserIdList.value.push(taskList.value[i].userName);
      } else if (
        assigneeUserIdList.value.indexOf(taskList.value[i].userName) == -1
      ) {
        assigneeUserIdList.value.push(taskList.value[i].userName);
      }
    }

    // 업무유형 목록
    if (taskTypeList.value.length < 1) {
      taskTypeList.value.push(taskList.value[i].typeName);
    } else if (taskTypeList.value.indexOf(taskList.value[i].typeName) == -1) {
      taskTypeList.value.push(taskList.value[i].typeName);
    }

    // 업무상태 목록
    if (statusList.value.length < 1) {
      statusList.value.push(taskList.value[i].statusName);
    } else if (statusList.value.indexOf(taskList.value[i].statusName) == -1) {
      statusList.value.push(taskList.value[i].statusName);
    }

    // 업무상태 목록
    if (priorityList.value.length < 1) {
      priorityList.value.push(taskList.value[i].codeName);
    } else if (priorityList.value.indexOf(taskList.value[i].codeName) == -1) {
      priorityList.value.push(taskList.value[i].codeName);
    }

    // 하위 프로젝트 목록
    if (
      smallProjectList.value.length < 1 &&
      taskList.value[i].projectName != name
    ) {
      smallProjectList.value.push(taskList.value[i].projectName);
    } else if (
      smallProjectList.value.indexOf(taskList.value[i].projectName) == -1 &&
      taskList.value[i].projectName != name
    ) {
      smallProjectList.value.push(taskList.value[i].projectName);
    }
  }
  console.log("업무 목록", taskList.value);

  listLength.value = taskList.value.length;

  await paging(taskList);
});

// 날짜 변경 함수
const changeDateType = day => {
  let date = new Date(day);

  if (date.getMonth() < 9) {
    let realDay = `${date.getFullYear()}-0${date.getMonth() + 1}-${date.getDate()}`;
    if (date.getDate() < 10) {
      realDay = `${date.getFullYear()}-0${date.getMonth() + 1}-0${date.getDate()}`;
    }
    return realDay;
  } else {
    let realDay = `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
    if (date.getDate() < 10) {
      realDay = `${date.getFullYear()}-0${date.getMonth() + 1}-0${date.getDate()}`;
    }
    return realDay;
  }
};

// 업무명 필터링
let filterFinishList = ref([]);

const filteringList = () => {
  console.log(taskList.value);

  filterFinishList.value = taskList.value;

  console.log(filterList.value);

  // 업무명
  if (filteredList.value.title != "전체") {
    filterFinishList.value = filterFinishList.value.filter(li => {
      return li.title === filteredList.value.title;
    });
  }

  // 담당자
  if (filteredList.value.user != "전체") {
    filterFinishList.value = filterFinishList.value.filter(li => {
      return li.userName === filteredList.value.user;
    });
  }

  // 업무유형
  if (filteredList.value.type != "전체") {
    filterFinishList.value = filterFinishList.value.filter(li => {
      return li.typeName === filteredList.value.type;
    });
  }

  // 업무상태
  if (filteredList.value.status != "전체") {
    filterFinishList.value = filterFinishList.value.filter(li => {
      return li.statusName === filteredList.value.status;
    });
  }

  // 우선순위
  if (filteredList.value.priority != "전체") {
    filterFinishList.value = filterFinishList.value.filter(li => {
      return li.codeName === filteredList.value.priority;
    });
  }

  // 프로젝트 명
  if (filteredList.value.small != "전체") {
    filterFinishList.value = filterFinishList.value.filter(li => {
      return li.projectName === filteredList.value.small;
    });
  }

  // 시작일
  if (filteredList.value.start != null && filteredList.value.end == null) {
    filterFinishList.value = filterFinishList.value.filter(li => {
      return li.startDate >= filteredList.value.start;
    });
  } else if (
    filteredList.value.start != null &&
    filteredList.value.end != null
  ) {
    filterFinishList.value = filterFinishList.value.filter(li => {
      return (
        li.startDate >= filteredList.value.start &&
        li.dueDate <= filteredList.value.end
      );
    });
  } else if (
    filteredList.value.start == null &&
    filteredList.value.end != null
  ) {
    filterFinishList.value = filterFinishList.value.filter(li => {
      return li.dueDate <= filteredList.value.end;
    });
  }

  console.log(filterFinishList.value);

  listLength.value = filterFinishList.value.length;
  // 페이지 네이션
  paging(filterFinishList);
};

// 페이지네이션
const handleCurrentChange = val => {
  let selectedList = ref([]);
  let answer = ref([]);
  if (filterFinishList.value.length > 0) {
    answer.value = filterFinishList.value;
    listLength.value = filterFinishList.value.length;
  } else {
    answer.value = taskList.value;
    listLength.value = taskList.value.length;
  }
  if (val > 1) {
    let startNum = (val - 1) * listNum.value;
    let endNum = val * listNum.value;
    for (let i = startNum; i < endNum; i++) {
      if (answer.value[i] == null) {
        break;
      }
      selectedList.value.push(answer.value[i]);
    }
    filterList.value = selectedList.value;
    console.log(filterList.value);
  } else {
    for (let i = 0; i < listNum.value; i++) {
      console.log(answer.value[i]);
      selectedList.value.push(answer.value[i]);
    }
    filterList.value = selectedList.value;
  }
};

const paging = a => {
  let paginglist = ref([]);
  for (let i = 0; i < listNum.value; i++) {
    if (a.value[i] == null) {
      break;
    }
    paginglist.value.push(a.value[i]);
  }
  workPageSize.value = a.value.length;

  console.log(paginglist.value);

  filterList.value = paginglist.value;
  // 페이지 네이션

  if (workPageSize.value / listNum > 1) {
    real.value = false;
  }
};

// 업무생성 버튼
const goResister = () => {
  router.push({ name: "taskRegister" });
};

// 초기화 버튼
const resetForm = () => {
  filterList.value = taskList.value;

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
</style>
