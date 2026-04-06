<template>
  <div class="flex h-screen overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />
    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden"
    >
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />
      <main class="grow">
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
          <h1 class="text-2xl font-bold text-gray-800 dark:text-gray-100 mb-8">
            업무 생성
          </h1>
          <div class="bg-white dark:bg-gray-800 rounded-xl shadow p-6">
            <div class="grid grid-cols-2 gap-6 mb-6">
              <div>
                <label class="block text-sm font-medium mb-1"
                  >프로젝트 명 <span class="text-red-500">*</span></label
                >
                <input
                  v-model="form.projectName"
                  disabled
                  class="input w-full"
                />
              </div>
              <div>
                <label class="block text-sm font-medium mb-1"
                  >하위 프로젝트 명</label
                >
                <input
                  v-model="form.subProjectName"
                  disabled
                  class="input w-full"
                />
              </div>
            </div>
            <div class="grid grid-cols-2 gap-6 mb-6">
              <div>
                <label class="block text-sm font-medium mb-1"
                  >업무 유형 <span class="text-red-500">*</span></label
                >
                <div class="flex gap-2">
                  <select v-model="form.taskTypeId" class="input flex-1">
                    <option value="">업무 유형을 선택하세요</option>
                    <option
                      v-for="item in taskTypeList"
                      :key="item.taskTypeId"
                      :value="item.taskTypeId"
                    >
                      {{ item.typeName }}
                    </option>
                  </select>
                  <button class="btn-confirm">확인</button>
                </div>
              </div>
              <div>
                <label class="block text-sm font-medium mb-1"
                  >담당자 지정<span class="text-red-500">*</span></label
                >
                <div class="flex gap-2">
                  <input
                    v-model="form.assigneeName"
                    disabled
                    class="input flex-1"
                  />
                  <button
                    type="button"
                    @click="openUserModal"
                    class="btn-select"
                  >
                    선택
                  </button>
                </div>
              </div>
              <ProjectSelectModal
                v-model="userModal"
                title="담당자 선택"
                :items="userList"
                @select="selectUser"
              />
            </div>
            <div class="mb-6">
              <label class="block text-sm font-medium mb-1"
                >업무 명 <span class="text-red-500">*</span></label
              >
              <input
                v-model="form.title"
                placeholder="업무 제목을 적으세요"
                class="input w-full"
              />
            </div>
            <div class="mb-6">
              <label class="block text-sm font-medium mb-1"
                >프로젝트 설명</label
              >
              <textarea v-model="form.content" rows="5" class="input w-full" />
              <button class="btn-select mt-2">파일 선택</button>
            </div>
            <div class="grid grid-cols-3 gap-6 mb-6">
              <div>
                <label class="block text-sm font-medium mb-1"
                  >업무 상태 <span class="text-red-500">*</span></label
                >
                <div class="flex gap-2">
                  <select v-model="form.taskStatusId" class="input flex-1">
                    <option value="">업무 상태를 선택하세요</option>
                    <option
                      v-for="item in statusList"
                      :key="item.codeValue"
                      :value="item.codeValue"
                    >
                      {{ item.codeName }}
                    </option>
                  </select>
                  <button class="btn-confirm">확인</button>
                </div>
              </div>
              <div>
                <label class="block text-sm font-medium mb-1"
                  >우선순위 <span class="text-red-500">*</span></label
                >
                <div class="flex gap-2">
                  <select
                    v-model="form.priorityCode"
                    @change="onPriorityChange"
                    class="input flex-1"
                  >
                    <option value="">우선순위를 선택하세요</option>
                    <option
                      v-for="item in priorityList"
                      :key="item.codeValue"
                      :value="item.codeValue"
                    >
                      {{ item.codeName }}
                    </option>
                  </select>
                  <button class="btn-confirm">확인</button>
                </div>
                <p class="text-xs text-gray-400 mt-1">
                  우선순위 선택 시 마감기한이 자동 설정됩니다.
                </p>
              </div>
              <div v-if="hasMilestone">
                <label class="block text-sm font-medium mb-1"
                  >마일스톤 <span class="text-red-500">*</span></label
                >
                <input
                  v-model="form.milestone"
                  disabled
                  class="input w-full bg-gray-100"
                  placeholder="자동 선택됨"
                />
              </div>
              <ProjectSelectModal
                v-if="hasMilestone"
                v-model="milestoneModal"
                title="마일스톤 선택"
                :items="milestoneList"
                @select="selectMilestone"
              />
            </div>
            <div class="grid grid-cols-3 gap-6 mb-8">
              <div>
                <label class="block text-sm font-medium mb-1"
                  >예정 시작 일</label
                >
                <input
                  type="date"
                  v-model="form.estStartDate"
                  @change="calcEstTime"
                  class="input w-full"
                />
              </div>
              <div>
                <label class="block text-sm font-medium mb-1"
                  >예정 종료일</label
                >
                <input
                  type="date"
                  v-model="form.estEndDate"
                  @change="calcEstTime"
                  class="input w-full"
                />
                <p class="text-xs text-gray-400 mt-1">
                  우선순위 선택 시 마감기한이 자동 설정됩니다.
                </p>
              </div>
              <div>
                <label class="block text-sm font-medium mb-1">추정 시간</label>
                <div class="flex gap-2">
                  <input v-model="form.estTime" class="input flex-1" />
                  <button class="btn-confirm">확인</button>
                </div>
              </div>
            </div>
            <div class="flex justify-between">
              <button @click="goBack" class="btn-navy">← 목록으로</button>
              <div class="flex gap-2">
                <button @click="resetForm" class="btn-red">초기화</button>
                <button @click="handleSubmit" class="btn-green">
                  프로젝트 등록
                </button>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { storeToRefs } from "pinia";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import ProjectSelectModal from "../components/SelectModal.vue";
import { useTaskStore } from "../stores/useTaskStore";

const router = useRouter();
const route = useRoute();
const sidebarOpen = ref(false);
const store = useTaskStore();
const id = route.params.projectId;

const {
  form,
  taskTypeList,
  priorityList,
  statusList,
  milestoneList,
  userList,
  userModal,
  milestoneModal,
  hasMilestone,
} = storeToRefs(store);
const {
  openUserModal,
  selectUser,
  selectMilestone,
  onPriorityChange,
  calcEstTime,
  resetForm,
} = store;

onMounted(async () => {
  await store.initCreate(route.params.projectId || route.query.projectId);
});

const handleSubmit = async () => {
  try {
    await store.createTask();
    alert("등록 완료!");
   router.push({
      name: "taskList",
      params: { projectId: id }, 
    });
  } catch (e) {
    alert(e.message || "등록에 실패했습니다. 입력값을 확인해 주세요.");
  }
};

const goBack = () =>
  router.push({
    name: "taskList",
    params: { projectId: id },
  });
</script>

<style scoped>
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
}
.btn-red:hover {
  background: #b91c1c;
  box-shadow: 0 4px 10px rgba(220, 38, 38, 0.3);
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
</style>
