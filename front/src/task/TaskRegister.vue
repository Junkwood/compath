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
        <div class="sub-header">
          <div class="breadcrumb">
            <span>홈</span><span class="bc-sep">›</span> <span>프로젝트</span
            ><span class="bc-sep">›</span> <span>{{ form.projectName }}</span
            ><span class="bc-sep">›</span>
            <span class="bc-cur">{{
              isSubTask ? "하위업무 생성" : "업무 생성"
            }}</span>
          </div>
          <button class="btn-back" @click="goBack">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
              <path
                d="M19 12H5M11 6l-6 6 6 6"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
            목록으로
          </button>
        </div>

        <div class="page-wrap">
          <div class="form-card">
            <div class="form-section grid-2">
              <div>
                <label class="field-label"
                  >프로젝트 명 <span class="required">*</span></label
                >
                <input
                  v-model="form.projectName"
                  disabled
                  class="input w-full"
                />
              </div>

              <div>
                <label class="field-label">하위 프로젝트 명</label>

                <div v-if="!form.subProjectId" class="input-group">
                  <select v-model="form.subProjectId" class="input">
                    <option value="">하위프로젝트를 선택하세요</option>
                    <option
                      v-for="item in subProjectList"
                      :key="item.projectId"
                      :value="item.projectId"
                    >
                      {{ item.displaySubProjectName || item.projectName }}
                    </option>
                  </select>
                </div>

                <input
                  v-else
                  v-model="form.subProjectName"
                  disabled
                  class="input w-full"
                />
              </div>
            </div>

            <div class="form-section grid-2">
              <div>
                <label class="field-label"
                  >업무 유형 <span class="required">*</span></label
                >
                <div class="input-group">
                  <select v-model="form.taskTypeId" class="input">
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

              <div>
                <label class="field-label"
                  >담당자 지정 <span class="required">*</span></label
                >
                <div class="input-group">
                  <input v-model="form.assigneeName" disabled class="input" />
                  <button
                    type="button"
                    @click="openUserModal"
                    class="btn btn-select"
                  >
                    선택
                  </button>
                </div>
              </div>

              <ProjectSelectModal
                v-model="userModal"
                title="담당자 선택"
                :items="assigneeItems"
                @select="selectUser"
              />
            </div>

            <div class="form-section">
              <label class="field-label"
                >업무 명 <span class="required">*</span></label
              >
              <input
                v-model="form.title"
                placeholder="업무 제목을 입력하세요"
                class="input w-full"
              />
            </div>

            <div class="form-section">
              <label class="field-label">프로젝트 설명</label>
              <textarea v-model="form.content" rows="4" class="input w-full" />

              <input
                type="file"
                ref="fileInputRef"
                multiple
                style="display: none"
                @change="handleFileChange"
              />

              <button
                type="button"
                class="btn btn-select mt-2"
                @click="fileInputRef.click()"
              >
                파일 선택
              </button>

              <div
                v-for="(file, idx) in fileList"
                :key="idx"
                class="flex justify-between mt-2 text-sm text-gray-600"
              >
                <span>{{ file.name }}</span>
                <button
                  type="button"
                  @click="fileList.splice(idx, 1)"
                  class="text-red-400 hover:text-red-600"
                >
                  삭제
                </button>
              </div>
            </div>

            <div class="form-section grid-3">
              <div>
                <label class="field-label"
                  >업무 상태 <span class="required">*</span></label
                >
                <select v-model="form.taskStatusId" class="input w-full">
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

              <div>
                <label class="field-label"
                  >우선순위 <span class="required">*</span></label
                >
                <select
                  v-model="form.priorityCode"
                  @change="onPriorityChange"
                  class="input w-full"
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
                <p class="hint">우선순위 선택 시 마감기한이 자동 설정됩니다.</p>
              </div>

              <div v-if="hasMilestone">
                <label class="field-label"
                  >마일스톤 <span class="required">*</span></label
                >
                <select v-model="form.milestoneId" class="input w-full">
                  <option value="">마일스톤 선택</option>
                  <option
                    v-for="item in visibleMilestoneList"
                    :key="item.value"
                    :value="item.value"
                  >
                    {{ item.name }}
                  </option>
                </select>
              </div>

              <ProjectSelectModal
                v-if="hasMilestone"
                v-model="milestoneModal"
                title="마일스톤 선택"
                :items="milestoneList"
                @select="selectMilestone"
              />
            </div>

            <div class="form-section grid-3 no-border">
              <div>
                <label class="field-label"
                  >예정 시작일<span class="required">*</span></label
                >
                <TaskDatePicker
                  v-model="form.estStartDate"
                  @change="calcEstTime(true)"
                />
              </div>

              <div>
                <label class="field-label"
                  >예정 종료일<span class="required">*</span></label
                >
                <TaskDatePicker
                  v-model="form.estEndDate"
                  @change="calcEstTime(true)"
                />
                <p class="hint">우선순위 선택 시 마감기한이 자동 설정됩니다.</p>
              </div>

              <div>
                <label class="field-label">추정 시간</label>
                <input
                  v-model="form.estTime"
                  readonly
                  class="input w-full"
                  placeholder="시작일/종료일 선택 시 자동 계산"
                />
                <p class="hint">* 워킹데이 기준 자동 계산</p>
              </div>
            </div>

            <div class="form-footer">
              <div class="form-footer-right">
                <button @click="resetForm" class="btn btn-reset">초기화</button>
                <button @click="handleSubmit" class="btn btn-submit">
                  업무 등록
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
import { ref, onMounted, computed, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { storeToRefs } from "pinia";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useAuthStore } from "../stores/auth";
import ProjectSelectModal from "../components/SelectModal.vue";
import { useTaskStore } from "../stores/useTaskStore";
import TaskDatePicker from "../components/TaskDatePicker.vue";
import Swal from "sweetalert2";

const router = useRouter();
const route = useRoute();
const sidebarOpen = ref(false);
const store = useTaskStore();
const authStore = useAuthStore();

const rootProjectId = Number(route.params.projectId);
const subProjectIdFromQuery = route.query.subProjectId
  ? Number(route.query.subProjectId)
  : null;
const parentTaskId = route.query.parentTaskId;
const from = route.query.from;

const fileList = ref([]);
const fileInputRef = ref(null);

const isSubTask = computed(() => !!route.query.parentTaskId);

const {
  form,
  taskTypeList,
  priorityList,
  filteredStatusList,
  milestoneList,
  userList,
  userModal,
  milestoneModal,
  hasMilestone,
  subProjectList,
} = storeToRefs(store);

const {
  openUserModal,
  selectUser,
  selectMilestone,
  onPriorityChange,
  calcEstTime,
  resetForm,
} = store;

const assigneeItems = computed(() => {
  const rawUsers = Array.isArray(userList.value) ? userList.value : [];

  return rawUsers
    .map((user) => ({
      id: user.id ?? user.userId ?? user.value ?? null,
      name: user.name ?? user.userName ?? "",
      userType: user.userType ?? user.roleName ?? "",
    }))
    .filter((user) => user.id !== null && user.name);
});

onMounted(async () => {
  await store.initCreate(rootProjectId, parentTaskId);

  if (subProjectIdFromQuery) {
    form.value.subProjectId = subProjectIdFromQuery;

    const found = subProjectList.value.find(
      (p) => Number(p.projectId) === Number(subProjectIdFromQuery),
    );

    if (found) {
      form.value.subProjectName =
        found.displaySubProjectName || found.projectName || "";
    }
  }
});

watch(
  () => form.value.subProjectId,
  (selectedId) => {
    const found = subProjectList.value.find(
      (p) => Number(p.projectId) === Number(selectedId),
    );

    form.value.subProjectName = found
      ? found.displaySubProjectName || found.projectName || ""
      : "";
  },
  { immediate: true },
);

const visibleMilestoneList = computed(() =>
  milestoneList.value.filter((m) => m.statusCode !== "e3"),
);

const handleFileChange = (e) => {
  Array.from(e.target.files).forEach((file) => {
    fileList.value.push({ raw: file, name: file.name });
  });
};

const handleSubmit = async () => {
  try {
    await store.createTask(authStore.user?.userId, fileList.value);

    await Swal.fire({
      icon: "success",
      title: "등록 완료!",
      confirmButtonText: "확인",
    });

    if (from === "dashboard" && subProjectIdFromQuery) {
      router.push({
        name: "subProjectDashboard",
        params: {
          projectId: rootProjectId,
          subProjectId: subProjectIdFromQuery,
        },
      });
    } else {
      router.push({
        name: "taskList",
        params: { projectId: rootProjectId },
      });
    }
  } catch (e) {
    Swal.fire({
      icon: "error",
      title: "등록 실패",
      text: e.message || "등록에 실패했습니다. 입력값을 확인해 주세요.",
      confirmButtonText: "확인",
    });
  }
};

const goBack = () => router.back();
</script>

<style scoped>
main {
  background: #f1f5f9;
  border-left: 1px solid #e5e7eb;
  padding: 0;
}

.sub-header {
  background: #ffffff;
  padding: 12px 32px;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 30;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.sub-header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #64748b;
}

.bc-sep {
  color: #cbd5e1;
}

.bc-cur {
  color: #0f172a;
  font-weight: 600;
}

.page-wrap {
  width: 100%;
  max-width: 100%;
  margin: 0;
  padding: 24px 32px;
}

.form-card {
  width: 100%;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 28px 32px;
  box-shadow: none;
}

.form-section {
  padding-bottom: 24px;
  margin-bottom: 24px;
  border-bottom: 1px solid #e5e7eb;
}

.form-section:last-of-type,
.form-section.no-border {
  padding-bottom: 0;
  margin-bottom: 0;
  border-bottom: none;
}

.grid-2 {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 24px;
}

.grid-3 {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 24px;
}

.field-label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  margin-bottom: 8px;
}

.required {
  color: #ef4444;
  margin-left: 2px;
}

:deep(.input) {
  height: 40px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  font-size: 14px;
  color: #0f172a;
  padding: 0 14px;
  transition:
    border-color 0.15s,
    box-shadow 0.15s;
}

:deep(.input:focus) {
  background: #ffffff;
  border-color: #94a3b8;
  box-shadow: 0 0 0 3px rgba(148, 163, 184, 0.18);
  outline: none;
}

:deep(.input:disabled) {
  background: #f1f5f9;
  color: #94a3b8;
  cursor: not-allowed;
}

:deep(select.input) {
  appearance: auto;
  padding-right: 28px;
  cursor: pointer;
}

:deep(textarea.input) {
  min-height: 120px;
  height: auto;
  padding: 12px 14px;
  resize: vertical;
}

.input-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.input-group :deep(.input) {
  flex: 1;
}

.btn {
  height: 40px;
  padding: 0 18px;
  font-size: 13.5px;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
  white-space: nowrap;
  border: 1px solid transparent;
  transition:
    background 0.15s,
    border-color 0.15s;
}

.btn-select {
  background: #ffffff;
  border-color: #e2e8f0;
  color: #334155;
}

.btn-select:hover {
  background: #f8fafc;
  border-color: #94a3b8;
}

.btn-back {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  height: 30px;
  padding: 0 12px;
  font-size: 13px;
  font-weight: 600;
  background: #ffffff;
  color: #334155;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.15s;
  flex-shrink: 0;
}

.btn-back:hover {
  background: #f1f5f9;
  border-color: #94a3b8;
  color: #0f172a;
}

.btn-reset {
  background: #ffffff;
  color: #b91c1c;
  border-color: #fca5a5;
}

.btn-reset:hover {
  background: #fef2f2;
}

.btn-submit {
  background: #1b5c9c;
  color: #ffffff;
}

.btn-submit:hover {
  background: #164d87;
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 28px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.form-footer-right {
  display: flex;
  gap: 10px;
}

.hint {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 6px;
}

.mt-2 {
  margin-top: 8px;
}
</style>
