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
          <!-- 페이지 타이틀 -->
          <div class="mb-6">
            <h1
              class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
            >
              업무 유형 관리
            </h1>
          </div>

          <!-- 카드 -->
          <div class="card">
            <div class="card-header">
              <span class="card-title">유형 목록</span>
              <el-button class="btn-register" @click="handleCreate">
                + &nbsp; 유형 등록
              </el-button>
            </div>

            <el-table
              v-loading="isLoading"
              :data="pagedStatuses"
              style="width: 100%"
              :header-cell-style="headerStyle"
              :cell-style="cellStyle"
            >
              <!-- 유형명 -->
              <el-table-column label="유형명" align="center" width="160">
                <template #default="{ row }">
                  <span class="status-name">{{ row.statusName }}</span>
                </template>
              </el-table-column>

              <!-- 설명 -->
              <el-table-column label="설명" min-width="280">
                <template #default="{ row }">
                  <span class="desc-text">{{ row.description || "-" }}</span>
                </template>
              </el-table-column>

              <!-- 완료 단계 여부 -->
              <el-table-column
                label="완료 단계 여부"
                align="center"
                width="160"
              >
                <template #default="{ row }">
                  <div class="done-cell">
                    <el-checkbox
                      v-model="row.isFinal"
                      :true-value="'Y'"
                      :false-value="'N'"
                      @change="handleCheck(row)"
                    />
                  </div>
                </template>
              </el-table-column>

              <!-- 사용 여부(활성화) -->
              <el-table-column
                label="사용 여부(활성화)"
                align="center"
                width="160"
              >
                <template #default="{ row }">
                  <el-switch
                    v-model="row.isActive"
                    active-value="Y"
                    inactive-value="N"
                    @change="handleToggle(row)"
                  />
                </template>
              </el-table-column>

              <!-- 관리 -->
              <el-table-column label="관리" align="center" width="100">
                <template #default="{ row }">
                  <el-button class="btn-edit" @click="handleEdit(row)"
                    >수정</el-button
                  >
                </template>
              </el-table-column>
            </el-table>

            <!-- 페이지네이션 -->
            <div class="pagination-wrap">
              <el-pagination
                v-model:current-page="currentPage"
                :page-size="pageSize"
                :total="statusStore.statusList.length"
                layout="prev, pager, next"
                background
              />
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>

  <!-- 유형 등록/수정 모달 -->
  <el-dialog
    v-model="modalVisible"
    :title="isEditMode ? '유형 수정' : '유형 등록'"
    width="480px"
    :close-on-click-modal="false"
    @close="handleModalClose"
    @closed="handleModalClosed"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      label-position="left"
    >
      <el-form-item label="유형명" prop="statusName">
        <div style="display: flex; gap: 8px; width: 100%">
          <el-input
            v-model="form.statusName"
            placeholder="유형명을 입력하세요"
            style="flex: 1"
            @input="onStatusNameInput"
          />
          <el-button class="btn-register" @click="checkDuplicate"
            >중복 확인</el-button
          >
        </div>

        <!-- ✅ 인라인 메시지 -->
        <p
          v-if="isOriginalName"
          style="font-size: 12px; margin-top: 4px; color: #9ca3af"
        >
          기존 유형명과 동일합니다.
        </p>
        <p
          v-else-if="dupValid === true"
          style="font-size: 12px; margin-top: 4px; color: #16a34a"
        >
          사용 가능한 유형명입니다.
        </p>
        <p
          v-else-if="dupValid === false"
          style="font-size: 12px; margin-top: 4px; color: #dc2626"
        >
          이미 사용 중인 유형명입니다.
        </p>
        <p
          v-else-if="dupMessage"
          style="font-size: 12px; margin-top: 4px; color: #dc2626"
        >
          {{ dupMessage }}
        </p>
      </el-form-item>

      <!-- 설명 -->
      <el-form-item label="설명" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder="유형에 대한 설명을 입력하세요 (선택)"
        />
      </el-form-item>

      <!-- 완료 단계 여부 -->
      <el-form-item label="완료 단계 여부">
        <div class="switch-row">
          <el-switch
            v-model="form.isFinal"
            active-value="Y"
            inactive-value="N"
          />
          <span class="switch-desc">
            이 유형가 업무의 최종 완료 단계입니까?<br />
            <span class="switch-sub"
              >완료 단계로 설정하면 업무가 종료 처리됩니다.</span
            >
          </span>
        </div>
      </el-form-item>

      <!-- 사용 여부 -->
      <el-form-item label="사용 여부">
        <div class="switch-row">
          <el-switch
            v-model="form.isActive"
            active-value="Y"
            inactive-value="N"
          />
          <span class="switch-desc">
            {{
              form.isActive === "Y" ? "사용 중입니다." : "미사용 유형입니다."
            }}
          </span>
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="modal-footer">
        <el-button class="btn-list" @click="handleModalClose"
          >← 목록으로</el-button
        >
        <div class="footer-right">
          <el-button class="btn-reset" @click="handleReset">↺ 초기화</el-button>
          <el-button
            class="btn-submit"
            :loading="submitting"
            @click="handleSubmit"
          >
            {{ isEditMode ? "수정 완료" : "유형 등록" }}
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from "vue";
import Swal from "sweetalert2";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useStatusStore } from "../stores/status.js";

const sidebarOpen = ref(false);
const statusStore = useStatusStore();

// ── 페이지네이션 ──
const currentPage = ref(1);
const pageSize = 10;
const isLoading = ref(false);

const pagedStatuses = computed(() => {
  const s = (currentPage.value - 1) * pageSize;
  return statusStore.statusList.slice(s, s + pageSize);
});

// ── 테이블 스타일 ──
const headerStyle = () => ({
  background: "#f8fafc",
  color: "#64748b",
  fontSize: "12px",
  fontWeight: "600",
  borderBottom: "1px solid #e2e8f0",
});
const cellStyle = () => ({
  fontSize: "13px",
  color: "#374151",
  borderBottom: "1px solid #f1f5f9",
});

// ── 모달 유형 ──
const modalVisible = ref(false);
const submitting = ref(false);
const formRef = ref(null);
const isEditMode = ref(false);

const defaultForm = () => ({
  taskStatusId: null,
  statusName: "",
  description: "",
  isFinal: "N",
  isActive: "Y",
});

const form = reactive(defaultForm());

const rules = reactive({
  statusName: [
    { required: true, message: "유형명을 입력하세요", trigger: "blur" },
    { max: 100, message: "유형명은 100자 이하로 입력하세요", trigger: "blur" },
  ],
});

// ── 등록 모달 열기 ──
const handleCreate = () => {
  isEditMode.value = false;
  modalVisible.value = true;
};

// ── 수정 모달 열기 ──
const handleEdit = (row) => {
  isEditMode.value = true;
  Object.assign(form, {
    taskStatusId: row.taskStatusId,
    statusName: row.statusName,
    description: row.description ?? "",
    isFinal: row.isFinal,
    isActive: row.isActive,
  });
  originalStatusName.value = row.statusName;
  modalVisible.value = true;
};

// ── 활성화 토글 ──
const handleToggle = async (row) => {
  const prevStatus = row.isActive === "Y" ? "N" : "Y";
  const action = row.isActive === "Y" ? "활성화" : "비활성화";

  const result = await Swal.fire({
    title: `유형를 ${action}하시겠습니까?`,
    text: `"${row.statusName}"`,
    icon: "question",
    showCancelButton: true,
    confirmButtonColor: "#2563eb",
    cancelButtonColor: "#6b7280",
    confirmButtonText: "확인",
    cancelButtonText: "취소",
  });

  if (!result.isConfirmed) {
    row.isActive = prevStatus;
    return;
  }

  try {
    // TODO: PUT /api/task-status/:taskStatusId/status
    await statusStore.changeStatus(row);
    Swal.fire({
      toast: true,
      position: "top-end",
      icon: "success",
      title: `${action} 처리되었습니다.`,
      showConfirmButton: false,
      timer: 2000,
      timerProgressBar: true,
    });
  } catch {
    row.isActive = prevStatus;
    Swal.fire({
      icon: "error",
      title: "처리 실패",
      text: "유형 변경에 실패했습니다. 다시 시도해주세요.",
      confirmButtonColor: "#2563eb",
    });
    await statusStore.getStatusList();
  }
};

// ── 완료단계 체크박스 ──
const handleCheck = async (row) => {
  const prevStatus = row.isFinal === "Y" ? "N" : "Y";
  const action = row.isFinal === "Y" ? "활성화" : "비활성화";

  const result = await Swal.fire({
    title: `완료 단계를 ${action}하시겠습니까?`,
    text: `"${row.statusName}"`,
    icon: "question",
    showCancelButton: true,
    confirmButtonColor: "#2563eb",
    cancelButtonColor: "#6b7280",
    confirmButtonText: "확인",
    cancelButtonText: "취소",
  });

  if (!result.isConfirmed) {
    row.isFinal = prevStatus;
    return;
  }

  try {
    // TODO: PUT /api/task-status/:taskStatusId/status
    await statusStore.changeFinal(row);
    Swal.fire({
      toast: true,
      position: "top-end",
      icon: "success",
      title: `${action} 처리되었습니다.`,
      showConfirmButton: false,
      timer: 2000,
      timerProgressBar: true,
    });
  } catch {
    row.isActive = prevStatus;
    Swal.fire({
      icon: "error",
      title: "처리 실패",
      text: "유형 변경에 실패했습니다. 다시 시도해주세요.",
      confirmButtonColor: "#2563eb",
    });
    await statusStore.getStatusList();
  }
};
// ── 모달 닫기 ──
const handleModalClose = () => {
  modalVisible.value = false;
};

// ── 초기화 ──
const handleReset = () => {
  Object.assign(
    form,
    isEditMode.value
      ? { statusName: "", description: "", isFinal: "N", isActive: "Y" }
      : defaultForm(),
  );
  isNameChecked.value = false;
  isNameValid.value = false;
  formRef.value?.clearValidate();
};

// ── 제출 ──
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  // ✅ 중복 확인 체크
  if (!isOriginalName.value && !isNameChecked.value) {
    Swal.fire({
      icon: "warning",
      title: "유형명 중복 확인을 해주세요.",
      confirmButtonColor: "#2563eb",
    });
    return;
  }
  if (!isOriginalName.value && !isNameValid.value) {
    Swal.fire({
      icon: "warning",
      title: "이미 사용 중인 유형명입니다.",
      confirmButtonColor: "#2563eb",
    });
    return;
  }
  submitting.value = true;
  try {
    if (isEditMode.value) {
      // TODO: PUT /api/task-status/:taskStatusId
      await statusStore.updateStatus(form);
    } else {
      // TODO: POST /api/task-status
      await statusStore.createStatus(form);
    }
    modalVisible.value = false;
    await statusStore.getStatusList();

    Swal.fire({
      toast: true,
      position: "top-end",
      icon: "success",
      title: isEditMode.value ? "수정되었습니다." : "등록되었습니다.",
      showConfirmButton: false,
      timer: 2000,
      timerProgressBar: true,
    });
  } catch {
    Swal.fire({
      icon: "error",
      title: isEditMode.value ? "수정 실패" : "등록 실패",
      text: "처리에 실패했습니다. 다시 시도해주세요.",
      confirmButtonColor: "#2563eb",
    });
  } finally {
    submitting.value = false;
  }
};
// ── 중복 확인 유형 ──
const isNameChecked = ref(false);
const isNameValid = ref(false);
const originalStatusName = ref("");
const dupMessage = ref(""); // 메시지 텍스트
const dupValid = ref(null); // null = 미확인, true = 가능, false = 중복
const isOriginalName = computed(
  () => isEditMode.value && form.statusName === originalStatusName.value,
);

// 유형명 입력 시 중복 확인 초기화
const onStatusNameInput = () => {
  if (!isOriginalName.value) {
    isNameChecked.value = false;
    isNameValid.value = false;
    dupValid.value = null;
    dupMessage.value = "";
  }
};

// 중복 확인
const checkDuplicate = async () => {
  // 수정 모드에서 기존 이름 그대로면 스킵
  if (isOriginalName.value) {
    isNameValid.value = true;
    isNameChecked.value = true;
    return;
  }
  try {
    const res = await statusStore.checkDuplicate(form.statusName);
    isNameValid.value = res; // true = 사용가능, false = 중복
    dupValid.value = res;
    isNameChecked.value = true;
  } catch {
    dupMessage.value = "중복 확인 중 오류가 발생했습니다.";
    dupValid.value = null;
  }
};
// 💡 모달이 화면에서 완전히 사라질 때 모든 유형를 '백지화' 합니다.
const handleModalClosed = () => {
  // 1. 폼 데이터 완전 초기화
  Object.assign(form, defaultForm());

  // 2. Element Plus 기본 빨간줄(검증 에러) 초기화
  formRef.value?.clearValidate();

  // 3. 커스텀 중복 확인 관련 변수들 싹 다 초기화
  isNameChecked.value = false;
  isNameValid.value = false;
  dupValid.value = null;
  dupMessage.value = "";
  originalStatusName.value = "";
};
// ── 초기 로드 ──
onMounted(async () => {
  isLoading.value = true;
  try {
    await statusStore.getStatusList();
  } finally {
    isLoading.value = false;
  }
});
</script>

<style scoped>
.card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}
.card-header {
  padding: 14px 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.card-title {
  font-weight: 600;
  font-size: 14px;
  color: #1a1a2e;
}
.status-name {
  font-weight: 600;
  color: #1a1a2e;
}
.desc-text {
  color: #6b7280;
  font-size: 13px;
}

/* 완료 단계 셀 */
.done-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}
.done-label {
  font-size: 12px;
  font-weight: 500;
}
.done-yes {
  color: #2563eb;
}
.done-no {
  color: #9ca3af;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 12px 0;
  border-top: 1px solid #f0f0f0;
}

/* 버튼 */
.btn-register {
  background: #2563eb;
  border: none;
  color: #fff;
  font-size: 13px;
  font-weight: 500;
  border-radius: 8px;
}
.btn-register:hover {
  background: #1d4ed8;
}

.btn-edit {
  background: #2563eb;
  border: none;
  color: #fff;
  font-size: 12px;
  font-weight: 500;
  border-radius: 6px;
  padding: 4px 14px;
}
.btn-edit:hover {
  background: #1d4ed8;
}

/* 모달 */
.switch-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}
.switch-desc {
  font-size: 13px;
  color: #374151;
  line-height: 1.6;
}
.switch-sub {
  font-size: 12px;
  color: #9ca3af;
}

.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.footer-right {
  display: flex;
  gap: 8px;
}
.btn-list {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #374151;
  font-size: 13px;
}
.btn-reset {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #374151;
  font-size: 13px;
}
.btn-submit {
  background: #1d4ed8;
  border: none;
  color: #fff;
  font-size: 13px;
  font-weight: 600;
}
.btn-submit:hover {
  background: #1e40af;
}

:deep(.el-dialog__title) {
  font-weight: 700;
  font-size: 15px;
  color: #1a1a2e;
}
:deep(.el-form-item__label) {
  font-size: 13px;
  color: #374151;
}
:deep(.el-table) {
  --el-table-border-color: #f1f5f9;
  --el-table-header-bg-color: #f8fafc;
}
:deep(.el-table__row:hover > td) {
  background: #f0f7ff !important;
}
:deep(.el-pagination.is-background .el-pager li.is-active) {
  background-color: #2563eb;
}
:global(.swal2-container) {
  z-index: 9999 !important;
}
</style>
