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
            업무 수정
          </h1>
          <div class="bg-white dark:bg-gray-800 rounded-xl shadow p-6">
            <!-- 프로젝트명 / 하위프로젝트명 - 원래부터 disabled -->
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

            <!-- 업무유형 / 담당자 -->
            <div class="grid grid-cols-2 gap-6 mb-6">
              <div>
                <label class="block text-sm font-medium mb-1"
                  >업무 유형 <span class="text-red-500">*</span></label
                >
                <div class="flex gap-2">
                  <select
                    v-model="form.taskTypeId"
                    class="input flex-1"
                    :disabled="isTerminated"
                  >
                    <option value="">업무 유형을 선택하세요</option>
                    <option
                      v-for="item in taskTypeList"
                      :key="item.taskTypeId"
                      :value="item.taskTypeId"
                    >
                      {{ item.typeName }}
                    </option>
                  </select>
                  <button class="btn-confirm" :disabled="isTerminated">
                    확인
                  </button>
                </div>
              </div>
              <div>
                <label class="block text-sm font-medium mb-1"
                  >담당자 지정</label
                >
                <div class="flex gap-2">
                  <input
                    v-model="form.assigneeName"
                    disabled
                    class="input flex-1"
                  />
                  <button
                    type="button"
                    @click="!isTerminated && openUserModal()"
                    class="btn-select"
                    :disabled="isTerminated"
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

            <!-- 진척도 / 소요시간 -->
            <div class="grid grid-cols-2 gap-6 mb-6">
              <div>
                <label class="block text-sm font-medium mb-1">진척도 (%)</label>
                <select
                  v-model="form.progressRate"
                  :disabled="isTerminated"
                  class="select w-full border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-400 outline-none p-2"
                  :class="{
                    '!bg-gray-100 !text-[#475569] cursor-not-allowed':
                      isTerminated,
                  }"
                >
                  <option
                    v-for="rate in [0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100]"
                    :key="rate"
                    :value="rate"
                  >
                    {{ rate }}%
                  </option>
                </select>
              </div>
              <div
                v-if="
                  finishedIds.includes(Number(form.taskStatusId)) ||
                  Number(form.taskStatusId) === 3
                "
                class="mt-4 p-3 bg-blue-50 rounded-lg"
              >
                <label class="block text-sm font-medium mb-1 text-blue-700"
                  >소요 시간</label
                >
                <input
                  type="text"
                  v-model="actualHours"
                  :disabled="isTerminated"
                  class="input w-full border-blue-300 bg-white"
                  placeholder="업무가 완료되어 시간이 계산되었습니다"
                />
                <p class="text-[11px] text-blue-500 mt-1">
                  * 개발 완료 시 자동 계산되며, 필요시 직접 수정 가능합니다.
                </p>
              </div>
            </div>

            <!-- 업무명 -->
            <div class="mb-6">
              <label class="block text-sm font-medium mb-1"
                >업무 명 <span class="text-red-500">*</span></label
              >
              <input
                v-model="form.title"
                :disabled="isTerminated"
                placeholder="업무 제목을 적으세요"
                class="input w-full"
              />
            </div>

            <!-- 설명 -->
            <div class="mb-6">
              <label class="block text-sm font-medium mb-1"
                >프로젝트 설명</label
              >
              <textarea
                v-model="form.content"
                :disabled="isTerminated"
                rows="5"
                class="input w-full"
              />
              <button class="btn-select mt-2" :disabled="isTerminated">
                파일 선택
              </button>
            </div>

            <!-- 업무상태 / 우선순위 / 마일스톤 -->
            <div class="grid grid-cols-3 gap-6 mb-6">
              <div>
                <label class="block text-sm font-medium mb-1"
                  >업무 상태 <span class="text-red-500">*</span></label
                >
                <div class="flex gap-2">
                  <select
                    v-model="form.taskStatusId"
                    class="input flex-1"
                    :disabled="isTerminated"
                  >
                    <option value="">업무 상태를 선택하세요</option>
                    <option
                      v-for="item in statusList"
                      :key="item.taskStatusId"
                      :value="item.taskStatusId"
                    >
                      {{ item.statusName }}
                    </option>
                  </select>
                  <button class="btn-confirm" :disabled="isTerminated">
                    확인
                  </button>
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
                    :disabled="isTerminated"
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
                  <button class="btn-confirm" :disabled="isTerminated">
                    확인
                  </button>
                </div>
                <p class="text-xs text-gray-400 mt-1">
                  우선순위 선택 시 마감기한이 자동 설정됩니다.
                </p>
              </div>
              <div>
                <label class="block text-sm font-medium mb-1"
                  >마일스톤 <span class="text-red-500">*</span></label
                >
                <!-- 마일스톤은 원래부터 disabled -->
                <input
                  v-model="form.milestone"
                  disabled
                  class="input w-full bg-gray-100"
                  placeholder="자동 선택됨"
                />
              </div>
              <ProjectSelectModal
                v-model="milestoneModal"
                title="마일스톤 선택"
                :items="milestoneList"
                @select="selectMilestone"
              />
            </div>

            <!-- 시작일 / 종료일 / 추정시간 -->
            <div class="grid grid-cols-3 gap-6 mb-8">
              <div>
                <label class="block text-sm font-medium mb-1">시작 일</label>
                <input
                  type="date"
                  v-model="form.startDate"
                  @change="calcEstTime"
                  class="input w-full"
                  :disabled="isTerminated"
                />
              </div>
              <div>
                <label class="block text-sm font-medium mb-1">종료일</label>
                <input
                  type="date"
                  v-model="form.dueDate"
                  @change="calcEstTime"
                  class="input w-full"
                  :disabled="isTerminated"
                />
              </div>
              <div>
                <label class="block text-sm font-medium mb-1">추정 시간</label>
                <input
                  v-model="form.estTime"
                  :disabled="
                    isTerminated ||
                    !!form.taskId ||
                    (form.estTime && form.estTime !== '0시간')
                  "
                  class="input w-full bg-gray-100 disabled:cursor-not-allowed"
                  placeholder="날짜 선택 시 자동 계산"
                />
              </div>
            </div>

            <!-- 하단 버튼 -->
            <div class="flex justify-between">
              <button @click="goBack" class="btn-navy">← 목록으로</button>
              <div class="flex gap-2">
                <button
                  v-if="canCreateSubTask"
                  @click="goCreateSubTask"
                  class="btn-sub"
                >
                  + 하위업무 생성
                </button>
                <button
                  @click="() => resetForm('edit')"
                  class="btn-red"
                  :disabled="isTerminated"
                >
                  초기화
                </button>
                <button
                  @click="handleSubmit"
                  class="btn-green"
                  :disabled="isTerminated"
                >
                  수정 완료
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
import { computed } from "vue";
import Swal from "sweetalert2";
import { useRouter, useRoute } from "vue-router";
import { storeToRefs } from "pinia";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import ProjectSelectModal from "../components/SelectModal.vue";
import { useTaskStore } from "../stores/useTaskStore";
import { useAuthStore } from "../stores/auth";
const router = useRouter();
const route = useRoute();
const sidebarOpen = ref(false);
const store = useTaskStore();
const authStore = useAuthStore();

const {
  form,
  taskTypeList,
  priorityList,
  statusList,
  milestoneList,
  userList,
  userModal,
  milestoneModal,
  actualHours,
  finishedIds,
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
  await store.initEdit(route.params.taskId);
});

// 현재 로그인 판별
const isAssignee = computed(
  () =>
    Number(form.value.assigneeUserId) ===
    Number(authStore.user?.userId || authStore.user?.id),
);

// 반려+ 담당자일 때만 하위업무 버튼 노출
const canCreateSubTask = computed(
  () => Number(form.value.taskStatusId) === 4 && isAssignee.value,
);

// 종료 시 전체 잠금
const { isOriginallyTerminated } = storeToRefs(store);
const isTerminated = computed(() => isOriginallyTerminated.value);

const goCreateSubTask = () => {
  router.push({
    name: "taskRegister",
    params: { projectId: form.value.subProjectId || form.value.projectId },
    query: { parentTaskId: form.value.taskId },
  });
};

const handleSubmit = async () => {
  const taskId = route.params.taskId;
  const editorUserId = authStore.user?.userId || authStore.user?.id;
  const projectId = form.value.projectId;
  try {
    const isSuccess = await store.updateTask(taskId, editorUserId);

    if (isSuccess) {
      router.push({
        name: "taskDetail",
        params: { projectId: projectId, taskId: taskId },
      });
    }
  } catch (e) {
    Swal.fire("실패", e.message, "error");
  }
};

const goBack = () => router.back();
</script>

<style scoped>
:deep(.input) {
  border-radius: 10px !important;
  border: 1px solid #e2e8f0 !important;
  background: #ffffff !important;
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
.btn-sub {
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
