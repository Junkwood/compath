<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="modelValue" class="modal-backdrop" @click.self="handleClose">
        <div class="modal-box">

          <!-- ── 헤더 ── -->
          <div class="modal-header">
            <div class="modal-header-left">
              <span class="modal-icon">✏️</span>
              <h2 class="modal-title">
                {{ isSubTask ? "하위업무 생성" : "업무 생성" }}
              </h2>
            </div>
            <button class="modal-close" @click="handleClose">✕</button>
          </div>

          <!-- ── 바디 ── -->
          <div class="modal-body">

            <!-- 프로젝트명 / 하위프로젝트명 -->
            <div class="field-row">
              <div class="field">
                <label class="field-label">프로젝트 명 <span class="req">*</span></label>
                <input v-model="form.projectName" disabled class="field-input" />
              </div>
              <div class="field">
                <label class="field-label">하위 프로젝트 명</label>
                <div v-if="!form.subProjectId" class="input-group">
                  <select v-model="form.subProjectName" class="field-input">
                    <option value="">하위프로젝트를 선택하세요</option>
                    <option
                      v-for="item in subProjectList"
                      :key="item.projectId"
                      :value="item.projectName"
                    >
                      {{ item.displaySubProjectName || item.projectName }}
                    </option>
                  </select>
                  <button class="btn-confirm" @click="confirmSubProject">확인</button>
                </div>
                <input v-else v-model="form.subProjectName" disabled class="field-input" />
              </div>
            </div>

            <!-- 업무 유형 / 담당자 -->
            <div class="field-row">
              <div class="field">
                <label class="field-label">업무 유형 <span class="req">*</span></label>
                <div class="input-group">
                  <select v-model="form.taskTypeId" class="field-input">
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
              <div class="field">
                <label class="field-label">담당자 지정 <span class="req">*</span></label>
                <div class="input-group">
                  <input v-model="form.assigneeName" disabled class="field-input" />
                  <button class="btn-select" @click="openUserModal">선택</button>
                </div>
              </div>
            </div>

            <!-- 업무명 -->
            <div class="field">
              <label class="field-label">업무 명 <span class="req">*</span></label>
              <input
                v-model="form.title"
                placeholder="업무 제목을 적으세요"
                class="field-input"
              />
            </div>

            <!-- 프로젝트 설명 -->
            <div class="field">
              <label class="field-label">업무 설명</label>
              <textarea v-model="form.content" rows="3" class="field-input field-textarea" />
            </div>

            <!-- 업무상태 / 우선순위 / 마일스톤 -->
            <div class="field-row field-row-3">
              <div class="field">
                <label class="field-label">업무 상태 <span class="req">*</span></label>
                <select v-model="form.taskStatusId" class="field-input">
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
              <div class="field">
                <label class="field-label">우선순위 <span class="req">*</span></label>
                <select v-model="form.priorityCode" @change="onPriorityChange" class="field-input">
                  <option value="">우선순위를 선택하세요</option>
                  <option
                    v-for="item in priorityList"
                    :key="item.codeValue"
                    :value="item.codeValue"
                  >
                    {{ item.codeName }}
                  </option>
                </select>
                <p class="field-hint">우선순위 선택 시 마감기한이 자동 설정됩니다.</p>
              </div>
              <div v-if="hasMilestone" class="field">
                <label class="field-label">마일스톤 <span class="req">*</span></label>
                <input
                  v-model="form.milestone"
                  disabled
                  class="field-input"
                  placeholder="자동 선택됨"
                />
              </div>
            </div>

            <!-- 예정 시작일 / 예정 종료일 / 추정 시간 -->
            <div class="field-row field-row-3">
              <div class="field">
                <label class="field-label">예정 시작일</label>
                <input
                  v-model="form.estStartDate"
                  type="date"
                  class="field-input"
                  @change="calcEstTime(true)"
                />
              </div>
              <div class="field">
                <label class="field-label">예정 종료일</label>
                <input
                  v-model="form.estEndDate"
                  type="date"
                  class="field-input"
                  @change="calcEstTime(true)"
                />
                <p class="field-hint">우선순위 선택 시 마감기한이 자동 설정됩니다.</p>
              </div>
              <div class="field">
                <label class="field-label">추정 시간</label>
                <input v-model="form.estTime" class="field-input" readonly />
              </div>
            </div>

            <!-- 에러 메시지 -->
            <p v-if="errorMsg" class="error-msg">⚠️ {{ errorMsg }}</p>

          </div>

          <!-- ── 푸터 ── -->
          <div class="modal-footer">
            <button class="btn-reset" @click="resetForm">초기화</button>
            <div class="footer-right">
              <button class="btn-cancel" @click="handleClose">취소</button>
              <button class="btn-submit" :disabled="loading" @click="handleSubmit">
                <span v-if="loading" class="spinner" />
                {{ loading ? "등록 중..." : "업무 등록" }}
              </button>
            </div>
          </div>

        </div>
      </div>
    </Transition>

    <!-- 담당자 선택 모달 (기존 SelectModal 재사용) -->
    <ProjectSelectModal
      v-model="userModal"
      title="담당자 선택"
      :items="userList"
      @select="selectUser"
    />

    <!-- 마일스톤 선택 모달 -->
    <ProjectSelectModal
      v-if="hasMilestone"
      v-model="milestoneModal"
      title="마일스톤 선택"
      :items="milestoneList"
      @select="selectMilestone"
    />
  </Teleport>
</template>

<script setup>
import { ref, computed, watch, onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useTaskStore } from "../stores/useTaskStore";
import { useAuthStore } from "../stores/auth";
import ProjectSelectModal from "../components/SelectModal.vue";

const props = defineProps({
  modelValue:  { type: Boolean, default: false },   // v-model (open/close)
  projectId:   { type: [String, Number], default: null },
  parentId:    { type: [String, Number], default: null }, // 하위 프로젝트 ID (p_ 에서 추출된 값)
});

const emit = defineEmits(["update:modelValue", "submitted"]);

const taskStore  = useTaskStore();
const authStore  = useAuthStore();

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
} = storeToRefs(taskStore);

const {
  openUserModal,
  selectUser,
  selectMilestone,
  onPriorityChange,
  calcEstTime,
  resetForm,
} = taskStore;

const loading  = ref(false);
const errorMsg = ref("");

// 하위업무 여부: parentId가 숫자 형태(task id)일 때
const isSubTask = computed(() => {
  const pid = props.parentId;
  if (!pid) return false;
  // p_ 접두사 없이 숫자만 넘어오면 task parentId
  return !String(pid).match(/^\d+$/) === false;
});

// 모달 열릴 때마다 스토어 초기화
watch(
  () => props.modelValue,
  async (val) => {
    if (!val) return;
    errorMsg.value = "";

    if (!props.projectId) return;

    // ★ parentTaskId는 절대 넘기지 않음 (하위 프로젝트 ID를 task ID로 오해함)
    await taskStore.initCreate(props.projectId, null);

    // 하위 프로젝트 ID가 있으면 폼에 직접 세팅
    if (props.parentId) {
      const subProj = subProjectList.value.find(
        (p) => String(p.projectId) === String(props.parentId)
      );
      if (subProj) {
        form.value.subProjectId   = subProj.projectId;
        form.value.subProjectName = subProj.projectName;
      }
    }
  }
);

// 하위 프로젝트 확인 버튼
const confirmSubProject = () => {
  const selected = subProjectList.value.find(
    (p) => p.projectName === form.value.subProjectName,
  );
  if (selected) {
    form.value.subProjectId = selected.projectId;
  }
};

const handleClose = () => {
  emit("update:modelValue", false);
};

const handleSubmit = async () => {
  errorMsg.value = "";
  loading.value = true;
  try {
    await taskStore.createTask(authStore.user?.userId);
    emit("submitted");
    handleClose();
  } catch (e) {
    errorMsg.value = e.message || "등록에 실패했습니다. 입력값을 확인해 주세요.";
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
/* ── 오버레이 ── */
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  backdrop-filter: blur(3px);
}

/* ── 박스 ── */
.modal-box {
  background: #fff;
  border-radius: 18px;
  width: 100%;
  max-width: 700px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

/* ── 헤더 ── */
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px 18px;
  border-bottom: 1px solid #e2e8f0;
  flex-shrink: 0;
}
.modal-header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}
.modal-icon {
  font-size: 18px;
}
.modal-title {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}
.modal-close {
  background: none;
  border: none;
  font-size: 15px;
  color: #94a3b8;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.15s;
  line-height: 1;
}
.modal-close:hover {
  background: #f1f5f9;
  color: #475569;
}

/* ── 바디 ── */
.modal-body {
  padding: 20px 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
  flex: 1;
}

/* ── 필드 공통 ── */
.field {
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.field-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}
.field-row-3 {
  grid-template-columns: 1fr 1fr 1fr;
}
.field-label {
  font-size: 12px;
  font-weight: 600;
  color: #475569;
}
.req {
  color: #ef4444;
  margin-left: 2px;
}
.field-input {
  border: 1.5px solid #e2e8f0;
  border-radius: 10px;
  padding: 8px 11px;
  font-size: 13px;
  color: #1e293b;
  background: #f8fafc;
  outline: none;
  width: 100%;
  box-sizing: border-box;
  transition: border-color 0.15s, background 0.15s;
  font-family: inherit;
}
.field-input:focus {
  border-color: #3b82f6;
  background: #fff;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}
.field-input:disabled {
  background: #f1f5f9;
  color: #64748b;
  cursor: not-allowed;
}
select.field-input {
  appearance: auto;
  -webkit-appearance: auto;
}
.field-textarea {
  resize: vertical;
  min-height: 72px;
}
.field-hint {
  font-size: 11px;
  color: #94a3b8;
  margin: 2px 0 0;
}

/* ── input + 버튼 그룹 ── */
.input-group {
  display: flex;
  gap: 8px;
}
.input-group .field-input {
  flex: 1;
}

/* ── 버튼들 ── */
.btn-confirm,
.btn-select {
  height: 38px;
  padding: 0 14px;
  font-size: 12px;
  font-weight: 600;
  border-radius: 9px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.15s;
  flex-shrink: 0;
}
.btn-confirm {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #475569;
}
.btn-confirm:hover {
  background: #e2e8f0;
  color: #1e293b;
}
.btn-select {
  background: #fff;
  border: 1px solid #e2e8f0;
  color: #475569;
  box-shadow: 0 1px 2px rgba(0,0,0,0.04);
}
.btn-select:hover {
  background: #f8fafc;
  border-color: #94a3b8;
  color: #1e293b;
}

/* ── 에러 ── */
.error-msg {
  font-size: 12px;
  color: #ef4444;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 8px;
  padding: 8px 12px;
  margin: 0;
}

/* ── 푸터 ── */
.modal-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
  flex-shrink: 0;
  background: #fafbfc;
}
.footer-right {
  display: flex;
  gap: 8px;
}
.btn-reset {
  height: 36px;
  padding: 0 16px;
  font-size: 12px;
  font-weight: 600;
  border-radius: 9px;
  cursor: pointer;
  background: #fff;
  border: 1px solid #fca5a5;
  color: #dc2626;
  transition: all 0.15s;
}
.btn-reset:hover {
  background: #fef2f2;
}
.btn-cancel {
  height: 36px;
  padding: 0 16px;
  font-size: 12px;
  font-weight: 600;
  border-radius: 9px;
  cursor: pointer;
  background: #fff;
  border: 1.5px solid #e2e8f0;
  color: #64748b;
  transition: all 0.15s;
}
.btn-cancel:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
}
.btn-submit {
  height: 36px;
  padding: 0 20px;
  font-size: 12px;
  font-weight: 700;
  border-radius: 9px;
  cursor: pointer;
  border: none;
  background: #2563eb;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: background 0.15s;
}
.btn-submit:hover:not(:disabled) {
  background: #1d4ed8;
}
.btn-submit:disabled {
  background: #93c5fd;
  cursor: not-allowed;
}

/* ── 스피너 ── */
.spinner {
  width: 12px;
  height: 12px;
  border: 2px solid rgba(255,255,255,0.35);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
  flex-shrink: 0;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ── 트랜지션 ── */
.modal-fade-enter-active { transition: opacity 0.2s ease; }
.modal-fade-leave-active { transition: opacity 0.18s ease; }
.modal-fade-enter-active .modal-box,
.modal-fade-leave-active .modal-box { transition: transform 0.2s ease; }
.modal-fade-enter-from,
.modal-fade-leave-to { opacity: 0; }
.modal-fade-enter-from .modal-box { transform: translateY(-10px) scale(0.98); }
.modal-fade-leave-to .modal-box   { transform: scale(0.98); }

/* ── 반응형 ── */
@media (max-width: 640px) {
  .modal-box { max-width: 100%; max-height: 100vh; border-radius: 0; }
  .field-row, .field-row-3 { grid-template-columns: 1fr; }
}
</style>