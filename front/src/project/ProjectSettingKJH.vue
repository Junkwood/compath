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
        <div
          class="col-span-full xl:col-span-8 bg-white dark:bg-gray-800 shadow-xs rounded-xl m-8 mt-4"
        >
          <!-- 예정시작일 / 예정종료일 / 추정시간 -->
          <div class="grid grid-cols-3 gap-6 mb-8">
            <div class="flex flex-row gap-10">
              <label class="block text-base font-medium mb-1"
                >프로젝트 명</label
              >
              <span>{{ name }}</span>
            </div>
            <div class="flex flex-row gap-10">
              <label class="block text-sm font-medium mb-1">시작일</label>
              <span>{{ name }}</span>
            </div>
            <div class="flex flex-row gap-10">
              <label class="block text-sm font-medium mb-1">종료일</label>
              <span>{{ name }}</span>
            </div>
          </div>

          <!-- 예정시작일 / 예정종료일 / 추정시간 -->
          <div class="grid grid-cols-3 gap-6 mb-8">
            <div>
              <label class="block text-sm font-medium mb-1">식별자</label>
              <input type="date" class="input w-full" />
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">PM</label>
              <input type="date" class="input w-full" />
            </div>
            <div>
              <label class="block text-sm font-medium mb-1">총괄 PL</label>
              <div class="flex gap-2">
                <input class="input flex-1" />
              </div>
            </div>
          </div>
          <div class="mb-6">
            <label class="block text-sm font-medium mb-1">프로젝트 설명</label>
            <textarea rows="5" class="input w-full" />
          </div>
          <div class="grid grid-cols-2 gap-6 mb-8">
            <div>
              <label class="block text-sm font-medium mb-1">마일스톤</label>
              <input type="date" class="input w-full" />
            </div>
          </div>
          <div class="grid grid-cols-1 gap-6 mb-8">
            <label class="block text-sm font-medium mb-1">공개 프로젝트</label>
            <input type="date" class="input w-full" />
          </div>
        </div>
        <div
          class="col-span-full xl:col-span-8 bg-white dark:bg-gray-800 shadow-xs rounded-xl m-8 mt-4"
        >
          <!-- Table -->

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
            <el-table-column label="삭제">
              <template #default="scope">
                <el-button
                  size="small"
                  type="danger"
                  @click="handleDelete(scope.$index, scope.row)"
                >
                  Delete
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onBeforeMount, ref } from "vue";
import { usetaskKJHStore } from "../stores/taksKJH";
import { useRoute } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";

const route = useRoute();
const taskStore = usetaskKJHStore();

const name = ref();
const id = route.params.id;

onBeforeMount(async () => {
  // 프로젝트 이름 조회
  await taskStore.getProjectName(id);
  name.value = taskStore.projectName.projectName;
});
</script>
<style scoped>
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
