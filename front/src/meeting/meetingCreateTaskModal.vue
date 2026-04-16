<template>
  <el-dialog
    v-model="visible"
    title=""
    width="1000px"
    height="100"
    :close-on-click-modal="false"
    class="custom-dialog"
    @close="handleClose"
  >
    <main class="grow">
      <h1 class="text-2xl font-bold text-gray-800 dark:text-gray-100 mb-4 p-3">
        업무 생성
      </h1>
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow p-6">
        <div class="grid grid-cols-3 gap-6 mb-4">
          <div>
            <label class="block text-sm font-medium mb-1"
              >프로젝트 명 <span class="text-red-500">*</span></label
            >
            <input v-model="form.projectName" disabled class="input w-full" />
          </div>
          <div>
            <label class="block text-sm font-medium mb-1"
              >하위 프로젝트 명</label
            >

            <!-- 상위 프로젝트에서 바로 업무 생성 시 -->
            <div v-if="!form.subProjectId" class="flex gap-2">
              <select v-model="form.subProjectName" class="input flex-1">
                <option value="">하위프로젝트를 선택하세요</option>
                <option
                  v-for="item in subProjectList"
                  :key="item.projectId"
                  :value="item.projectName"
                >
                  {{ item.displaySubProjectName }}
                </option>
              </select>
            </div>

            <!-- 하위 프로젝트 있을 때-->
            <input
              v-else
              v-model="form.subProjectName"
              disabled
              class="input w-full"
            />
          </div>
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
            </div>
          </div>
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
          <label class="block text-sm font-medium mb-1">프로젝트 설명</label>
          <textarea v-model="form.content" rows="5" class="input w-full" />
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
                  v-for="item in filteredStatusList"
                  :key="item.taskStatusId"
                  :value="item.taskStatusId"
                >
                  {{ item.statusName }}
                </option>
              </select>
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
            <label class="block text-sm font-medium mb-1">예정 시작 일</label>
            <TaskDatePicker
              v-model="form.estStartDate"
              @change="calcEstTime(true)"
            />
          </div>
          <div>
            <label class="block text-sm font-medium mb-1">예정 종료일</label>
            <TaskDatePicker
              v-model="form.estEndDate"
              @change="calcEstTime(true)"
            />
            <p class="text-xs text-gray-400 mt-1">
              우선순위 선택 시 마감기한이 자동 설정됩니다.
            </p>
          </div>
          <div>
            <label class="block text-sm font-medium mb-1">추정 시간</label>
            <div class="flex gap-2">
              <input v-model="form.estTime" class="input flex-1" />
            </div>
          </div>
        </div>
        <div class="flex justify-between">
          <div></div>
          <div class="flex gap-2">
            <button @click="closeModal()" class="btn-navy">취소</button>
            <button @click="handleSubmit" class="btn-green">
              프로젝트 등록
            </button>
          </div>
        </div>
      </div>
    </main>
  </el-dialog>
</template>

<script setup>
import { onMounted, defineProps, defineEmits, watch } from "vue";
import { useRoute } from "vue-router";
import { storeToRefs } from "pinia";
import { useAuthStore } from "../stores/auth";
import ProjectSelectModal from "../components/SelectModal.vue";
import { useTaskStore } from "../stores/useTaskStore";
import { useMeetingStore } from "../stores/meeting";
import TaskDatePicker from "../components/TaskDatePicker.vue";

const props = defineProps({
  taskInfo: Array,
});

const emit = defineEmits(["closeCreateModal", "registerTask"]);

const route = useRoute();
const store = useTaskStore();
const meetingStore = useMeetingStore();
const parentTaskId = route.query.parentTaskId;
const authStore = useAuthStore();

const {
  form,
  taskTypeList,
  priorityList,
  filteredStatusList,
  milestoneList,
  milestoneModal,
  hasMilestone,
  subProjectList,
} = storeToRefs(store);
const { selectMilestone, onPriorityChange, calcEstTime, resetForm } = store;

onMounted(async () => {
  await store.initCreate(
    route.params.projectId || route.query.projectId,
    parentTaskId,
  );
});

const handleSubmit = async () => {
  let obj = { ...form.value, createdBy: Number(authStore.user?.userId) };
  console.log(obj);
  try {
    await meetingStore.registerRecommandTask(obj);
    emit("registerTask");
  } catch (e) {
    alert(e.message || "등록에 실패했습니다. 입력값을 확인해 주세요.");
  }
};

const closeModal = () => {
  emit("closeCreateModal");
  resetForm();
  form.value.title = Object.keys(props.taskInfo)[0];
  form.value.content = Object.values(props.taskInfo)[0];
};

watch(
  () => props.taskInfo,
  (newVal) => {
    form.value.title = Object.keys(newVal)[0];
    form.value.content = Object.values(newVal)[0];
    form.value.meetingLogId = newVal.meetingLogId;
  },
);
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
