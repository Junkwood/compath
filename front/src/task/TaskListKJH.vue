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

      <div
        class="col-span-full xl:col-span-8 bg-white dark:bg-gray-800 shadow-xs rounded-xl m-2"
      >
        <header
          class="px-5 py-4 border-b border-gray-100 dark:border-gray-700/60"
        >
          <h2 class="font-semibold text-gray-800 dark:text-gray-100">
            {{ name }}
          </h2>
        </header>

        <div
          class="w-full max-w-full mb-2 bg-neutral-primary-soft p-3 border border-default rounded-base shadow-xs"
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
                  class="bg-neutral-secondary-medium border border-default-medium text-heading text-sm rounded-base focus:ring-brand focus:border-brand block w-full px-3 py-2.5 shadow-xs placeholder:text-body"
                  v-model="selectedName"
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
                  class="bg-neutral-secondary-medium border border-default-medium text-heading text-sm rounded-base focus:ring-brand focus:border-brand block w-full px-3 py-2.5 shadow-xs placeholder:text-body"
                  v-model="selectedId"
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
                  class="bg-neutral-secondary-medium border border-default-medium text-heading text-sm rounded-base focus:ring-brand focus:border-brand block w-full px-3 py-2.5 shadow-xs placeholder:text-body"
                  v-model="selectedStatus"
                >
                  <option value="전체">전체</option>
                  <option :value="status" v-for="status in statusList">
                    {{ status }}
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
                  class="bg-neutral-secondary-medium border border-default-medium text-heading text-sm rounded-base focus:ring-brand focus:border-brand block w-full px-3 py-2.5 shadow-xs placeholder:text-body"
                  v-model="selectedType"
                >
                  <option value="전체">전체</option>
                  <option :value="type" v-for="type in taskTypeList">
                    {{ type }}
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
                  v-model="selectedStart"
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
                <input v-model="selectedEnd" type="date" class="input w-full" />
              </div>
              <div>
                <label
                  for="password"
                  class="block mb-2.5 text-sm font-medium text-heading"
                  >우선순위</label
                >
                <select
                  class="bg-neutral-secondary-medium border border-default-medium text-heading text-sm rounded-base focus:ring-brand focus:border-brand block w-full px-3 py-2.5 shadow-xs placeholder:text-body"
                  v-model="selectedpriority"
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
                  >하위 프로젝트명</label
                >
                <select
                  class="bg-neutral-secondary-medium border border-default-medium text-heading text-sm rounded-base focus:ring-brand focus:border-brand block w-full px-3 py-2.5 bm-2 shadow-xs placeholder:text-body"
                  v-model="selectedSmall"
                >
                  <option value="전체">전체</option>
                  <option :value="small" v-for="small in smallProjectList">
                    {{ small }}
                  </option>
                </select>
              </div>
            </div>
            <div class="flex flex-row-reverse">
              <button
                type="button"
                class="text-white bg-blue-500 box-border border border-transparent hover:bg-brand-strong"
                @click="changeList"
              >
                검색
              </button>
            </div>
          </form>
        </div>

        <div class="mt-3">
          <!-- Table -->
          <div class="overflow-x-auto">
            <table class="table-auto w-full dark:text-gray-300">
              <!-- Table header -->
              <thead
                class="text-xs uppercase text-gray-400 dark:text-gray-500 bg-gray-50 dark:bg-gray-700/50 rounded-xs"
              >
                <tr>
                  <th class="p-2">
                    <div class="font-semibold text-center">업무명</div>
                  </th>
                  <th class="p-2">
                    <div class="font-semibold text-center">담당자</div>
                  </th>
                  <th class="p-2">
                    <div class="font-semibold text-center">업무상태</div>
                  </th>
                  <th class="p-2">
                    <div class="font-semibold text-center">업무유형</div>
                  </th>
                  <th class="p-2">
                    <div class="font-semibold text-center">우선순위</div>
                  </th>
                  <th class="p-2">
                    <div class="font-semibold text-center">진척도</div>
                  </th>
                  <th class="p-2">
                    <div class="font-semibold text-center">시작일</div>
                  </th>
                  <th class="p-2">
                    <div class="font-semibold text-center">종료일</div>
                  </th>
                  <th class="p-2">
                    <div class="font-semibold text-center">프로젝트명</div>
                  </th>
                </tr>
              </thead>
              <!-- Table body -->
              <tbody
                class="text-sm font-medium divide-y divide-gray-100 dark:divide-gray-700/60"
              >
                <!-- Row -->
                <tr v-for="task in taskList">
                  <td class="p-2">
                    <div class="font-semibold text-center">
                      {{ task.title }}
                    </div>
                  </td>
                  <td class="p-2">
                    <div class="font-semibold text-center">
                      {{ task.assigneeUserId }}
                    </div>
                  </td>
                  <td class="p-2">
                    <div class="font-semibold text-center">
                      {{ task.statusName }}
                    </div>
                  </td>
                  <td class="p-2">
                    <div class="font-semibold text-center">
                      {{ task.typeName }}
                    </div>
                  </td>
                  <td class="p-2">
                    <div class="font-semibold text-center">
                      {{ task.codeName }}
                    </div>
                  </td>
                  <td class="p-2">
                    <div class="font-semibold text-center">
                      {{ task.progressRate }}
                    </div>
                  </td>
                  <td class="p-2">
                    <div class="font-semibold text-center">
                      {{ task.startDate }}
                    </div>
                  </td>
                  <td class="p-2">
                    <div class="font-semibold text-center">
                      {{ task.dueDate }}
                    </div>
                  </td>
                  <td class="p-2">
                    <div class="font-semibold text-center">
                      {{ task.projectName }}
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onBeforeMount, ref } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";

import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";

const sidebarOpen = ref(false);

const route = useRoute();

let taskList = ref(); // 업무 목록
let name = ref(); // 프로젝트명
let titleList = ref([]); // 업무명 목록
let assigneeUserIdList = ref([]); // 담당자 목록
let statusList = ref([]); // 업무 상태 목록
let taskTypeList = ref([]); // 업무 유형 목록
let priorityList = ref([]); // 우선순위 목록
let smallProjectList = ref([]); // 하위프로젝트 목록

let selectedList = ref([]);
let selectedId = ref("전체");
let selectedName = ref("전체");
let selectedSmall = ref("전체");
let selectedStatus = ref("전체");
let selectedType = ref("전체");
let selectedpriority = ref("전체");
let selectedStart = ref();
let selectedEnd = ref();

// 필터링 조건들

onBeforeMount(async () => {
  let id = route.params.id;
  console.log("프로젝트 번호", id);

  // 프로젝트 이름 조회
  name.value = (
    await axios.get("/api/tasks/projectname/" + id)
  ).data.projectName;
  console.log("프로젝트명 ", name);

  // 전체 목록 조회
  taskList.value = (await axios.get("/api/tasks/" + id)).data;
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
    if (taskList.value[i].assigneeUserId != null) {
      if (assigneeUserIdList.value.length < 1) {
        assigneeUserIdList.value.push(taskList.value[i].assigneeUserId);
      } else if (
        assigneeUserIdList.value.indexOf(
          taskList.value[i].assigneeUserIdList,
        ) == -1
      ) {
        assigneeUserIdList.value.push(taskList.value[i].assigneeUserId);
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
});

// 날짜 변경 함수
const changeDateType = (day) => {
  let date = new Date(day);
  let realDay = `${date.getFullYear(day)}-${date.getMonth(day) + 1}-${date.getDate(day)}`;
  return realDay;
};

// 검색버튼
// const changeList = computed(() => {});
</script>
