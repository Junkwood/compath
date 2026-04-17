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
        <!-- 서브헤더 -->
        <div class="sub-header">
          <div class="breadcrumb">
            <span>홈</span><span class="bc-sep">›</span> <span>프로젝트</span
            ><span class="bc-sep">›</span> <span>{{ form.projectName }}</span
            ><span class="bc-sep">›</span>
            <span class="bc-cur">{{
              isSubTask ? "하위업무 수정" : "업무 수정"
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
            <!-- 잠금 배너 -->
            <div v-if="isTerminated" class="locked-banner">
              ⚠ 종료된 업무는 수정할 수 없습니다.
            </div>

            <!-- 섹션 1: 프로젝트 / 하위프로젝트 -->
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
                <input
                  v-model="form.subProjectName"
                  disabled
                  class="input w-full"
                />
              </div>
            </div>

            <!-- 섹션 2: 업무유형 / 담당자 -->
            <div class="form-section grid-2">
              <div>
                <label class="field-label"
                  >업무 유형 <span class="required">*</span></label
                >
                <div class="input-group">
                  <select
                    v-model="form.taskTypeId"
                    class="input"
                    :disabled="isTerminated"
                  >
                    <option value="">선택하세요</option>
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
                <label class="field-label">담당자</label>
                <div class="input-group">
                  <input v-model="form.assigneeName" disabled class="input" />
                  <button
                    type="button"
                    @click="!isTerminated && openUserModal()"
                    class="btn btn-select"
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

            <!-- 섹션 3: 진척도 / 소요시간 -->
            <div class="form-section grid-2">
              <div>
                <label class="field-label">진척도 (%)</label>
                <select
                  v-model="form.progressRate"
                  :disabled="isTerminated"
                  class="input w-full"
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
              >
                <div class="actual-hours-box">
                  <span class="actual-hours-label">소요 시간</span>
                  <input
                    type="text"
                    v-model="actualHours"
                    :disabled="isTerminated"
                    class="input w-full"
                    placeholder="자동 계산됩니다"
                  />
                  <p class="actual-hours-hint">
                    * 개발 완료 시 자동 계산, 직접 수정 가능
                  </p>
                </div>
              </div>
            </div>

            <!-- 섹션 4: 업무명 -->
            <div class="form-section">
              <label class="field-label"
                >업무 명 <span class="required">*</span></label
              >
              <input
                v-model="form.title"
                :disabled="isTerminated"
                placeholder="업무 제목을 입력하세요"
                class="input w-full"
              />
            </div>

            <!-- 섹션 5: 설명 -->
            <div class="form-section">
              <label class="field-label">업무 설명</label>
              <textarea
                v-model="form.content"
                :disabled="isTerminated"
                rows="4"
                class="input w-full"
              />

              <input
                type="file"
                ref="fileInputRef"
                multiple
                style="display: none"
                @change="onFileChange"
              />

              <button
                type="button"
                class="btn btn-select mt-2"
                :disabled="isTerminated"
                @click="fileInputRef.click()"
              >
                파일 선택
              </button>

              <!-- 기존 첨부파일 -->
              <div class="file-list">
                <div
                  v-for="file in attachmentList"
                  :key="file.attachmentId"
                  class="file-item"
                >
                  <span class="file-name">📎 {{ file.fileName }}</span>
                  <button
                    v-if="!isTerminated"
                    class="file-remove"
                    @click="removeExistingFile(file.attachmentId)"
                  >
                    ✕
                  </button>
                </div>
              </div>

              <!-- 새로 추가한 파일 -->
              <div class="file-list new-files">
                <div
                  v-for="(file, idx) in selectedFiles"
                  :key="idx"
                  class="file-item"
                >
                  <span class="file-name">🆕 {{ file.raw.name }}</span>
                  <button
                    class="file-remove"
                    @click="selectedFiles.splice(idx, 1)"
                  >
                    ✕
                  </button>
                </div>
              </div>
            </div>
            <!-- 섹션 6: 상태 / 우선순위 / 마일스톤 -->
            <div class="form-section grid-3">
              <div>
                <label class="field-label"
                  >업무 상태 <span class="required">*</span></label
                >
                <select
                  v-model="form.taskStatusId"
                  class="input w-full"
                  :disabled="isTerminated"
                >
                  <option value="">선택하세요</option>
                  <option
                    v-for="item in statusList"
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
                  :disabled="isTerminated"
                >
                  <option value="">선택하세요</option>
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
              <div>
                <label class="field-label"
                  >마일스톤 <span class="required">*</span></label
                >
                <input
                  v-model="form.milestone"
                  disabled
                  class="input w-full"
                  placeholder="자동 선택됨"
                />
              </div>
            </div>

            <!-- 섹션 7: 날짜 / 추정시간 -->
            <div class="form-section grid-3 no-border">
              <div>
                <label class="field-label">시작일</label>
                <TaskDatePicker
                  v-model="form.startDate"
                  :disabled="isTerminated"
                  @change="calcEstTime"
                />
              </div>
              <div>
                <label class="field-label">종료일</label>
                <TaskDatePicker
                  v-model="form.dueDate"
                  :disabled="isTerminated"
                  @change="calcEstTime"
                />
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

            <!-- 하단 버튼 -->
            <div class="form-footer">
              <div class="form-footer-right">
                <button
                  @click="() => resetForm('edit')"
                  class="btn btn-reset"
                  :disabled="isTerminated"
                >
                  초기화
                </button>
                <button
                  @click="handleSubmit"
                  class="btn btn-submit"
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
import TaskDatePicker from "../components/TaskDatePicker.vue";

const router = useRouter();
const route = useRoute();
const sidebarOpen = ref(false);
const store = useTaskStore();
const authStore = useAuthStore();

const fileInputRef = ref(null);
const selectedFiles = ref([]);

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
  attachmentList,
} = storeToRefs(store);
const {
  openUserModal,
  selectUser,
  selectMilestone,
  onPriorityChange,
  calcEstTime,
  resetForm,
  removeExistingFile,
} = store;

onMounted(async () => {
  await store.initEdit(route.params.taskId);
});

const isSubTask = computed(() => !!form.value.parentTaskId);

const isAssignee = computed(
  () =>
    Number(form.value.assigneeUserId) ===
    Number(authStore.user?.userId || authStore.user?.id),
);

const canCreateSubTask = computed(
  () => Number(form.value.taskStatusId) === 4 && isAssignee.value,
);

const { isOriginallyTerminated } = storeToRefs(store);
const isTerminated = computed(() => isOriginallyTerminated.value);

const goCreateSubTask = () => {
  router.push({
    name: "taskRegister",
    params: { projectId: form.value.subProjectId || form.value.projectId },
    query: { parentTaskId: form.value.taskId },
  });
};

const onFileChange = (e) => {
  const newFiles = Array.from(e.target.files).map((f) => ({ raw: f }));
  selectedFiles.value = [...selectedFiles.value, ...newFiles];
  e.target.value = "";
};

const handleSubmit = async () => {
  const taskId = route.params.taskId;
  const editorUserId = authStore.user?.userId || authStore.user?.id;
  const projectId = form.value.projectId;
  try {
    const isSuccess = await store.updateTask(
      taskId,
      editorUserId,
      selectedFiles.value,
    );
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
:root {
  font-size: 14px;
}

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
.file-list {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.new-files {
  margin-top: 4px;
}

.file-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 7px 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 13px;
  color: #334155;
}

.file-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-remove {
  flex-shrink: 0;
  margin-left: 10px;
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  font-size: 13px;
  padding: 0 4px;
  line-height: 1;
}

.file-remove:hover {
  color: #ef4444;
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
.btn-select:disabled {
  opacity: 0.45;
  cursor: not-allowed;
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

.btn-submit:disabled,
.btn-reset:disabled {
  opacity: 0.45;
  cursor: not-allowed;
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

.actual-hours-box {
  padding: 16px;
  background: #f0f7ff;
  border: 1px solid #bfdbfe;
  border-radius: 10px;
}

.actual-hours-label {
  font-size: 12px;
  font-weight: 600;
  color: #1b5c9c;
  margin-bottom: 6px;
  display: block;
}

.actual-hours-hint {
  font-size: 12px;
  color: #60a5fa;
  margin-top: 6px;
}

.locked-banner {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: #fef3c7;
  border: 1px solid #fcd34d;
  border-radius: 10px;
  font-size: 13px;
  color: #92400e;
  font-weight: 500;
  margin-bottom: 20px;
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
