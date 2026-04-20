<!-- 마일스톤 생성 / 수정 : 한 모달로 분기처리해서 사용 -->
<template>
  <el-dialog
    :model-value="modelValue"
    :title="isEditMode ? '마일스톤 수정' : '마일스톤 생성'"
    width="760px"
    :close-on-click-modal="false"
    :append-to-body="true"
    @close="handleClose"
  >
    <div class="milestone-modal-body">
      <div class="milestone-form-wrap">
        <el-form label-width="100px" class="milestone-form">
          <el-form-item label="번호">
            <el-input v-model="form.milestoneId" readonly disabled />
          </el-form-item>

          <el-form-item label="프로젝트 이름">
            <el-input :model-value="projectName" readonly disabled />
          </el-form-item>

          <el-form-item label="마일스톤 이름">
            <el-input
              v-model="form.milestoneName"
              placeholder="마일스톤 이름을 입력하세요"
            />
          </el-form-item>

          <div class="date-row">
            <el-form-item label="시작일" class="date-item">
              <el-date-picker
                v-model="form.startDate"
                type="date"
                placeholder="시작일 선택"
                value-format="YYYY-MM-DD"
                format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="종료일" class="date-item">
              <el-date-picker
                v-model="form.dueDate"
                type="date"
                placeholder="종료일 선택"
                value-format="YYYY-MM-DD"
                format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </div>

          <el-form-item label="설명">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="6"
              resize="none"
              placeholder="설명을 입력하세요"
            />
          </el-form-item>
        </el-form>
      </div>
    </div>

    <template #footer>
      <div class="modal-footer-btns">
        <el-button class="cancel-btn" @click="handleClose">취소</el-button>
        <el-button class="save-btn" type="primary" @click="handleSave">
          저장
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { reactive, watch, nextTick } from "vue";
import { useRouter } from "vue-router";
import api from "../utils/api";
import { ElMessage } from "element-plus";
import { useAuthStore } from "../stores/auth";
import Swal from "sweetalert2";

const authStore = useAuthStore();
const router = useRouter();

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  projectId: {
    type: [String, Number],
    required: true,
  },
  projectName: {
    type: String,
    default: "",
  },
  isEditMode: {
    type: Boolean,
    default: false,
  },
  milestoneData: {
    type: Object,
    default: () => null,
  },
  redirectAfterSave: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["update:modelValue", "saved"]);

const form = reactive({
  milestoneId: "자동으로 생성됩니다",
  milestoneName: "",
  startDate: "",
  dueDate: "",
  description: "",
  editorUserId: "",
});

watch(
  () => props.modelValue,
  (newVal) => {
    if (!newVal) return;

    const userId = authStore.user?.userId ?? "";

    if (props.isEditMode && props.milestoneData) {
      form.milestoneId = props.milestoneData.milestoneId ?? "";
      form.milestoneName = props.milestoneData.milestoneName ?? "";
      form.startDate = props.milestoneData.startDate ?? "";
      form.dueDate = props.milestoneData.dueDate ?? "";
      form.description = props.milestoneData.description ?? "";
      form.editorUserId = userId;
    } else {
      resetForm();
    }
  },
);

const resetForm = () => {
  form.milestoneId = "자동으로 생성됩니다";
  form.milestoneName = "";
  form.startDate = "";
  form.dueDate = "";
  form.description = "";
  form.editorUserId = "";
};

const handleClose = () => {
  emit("update:modelValue", false);
};

const handleSave = async () => {
  const userId = authStore.user?.userId;

  if (!form.milestoneName.trim()) {
    ElMessage.warning("마일스톤 이름을 입력하세요.");
    return;
  }

  if (!form.startDate) {
    ElMessage.warning("시작일을 선택하세요.");
    return;
  }

  if (!form.dueDate) {
    ElMessage.warning("종료일을 선택하세요.");
    return;
  }

  if (!userId) {
    ElMessage.warning("로그인 사용자 정보가 없습니다.");
    return;
  }

  if (form.startDate > form.dueDate) {
    ElMessage.warning("종료일은 시작일보다 빠를 수 없습니다.");
    return;
  }

  try {
    if (props.isEditMode) {
      const payload = {
        milestoneId: form.milestoneId,
        milestoneName: form.milestoneName,
        startDate: form.startDate,
        dueDate: form.dueDate,
        description: form.description,
        projectId: props.projectId,
        editorUserId: userId,
      };

      await api.put(
        `/MilestoneUpdate/${props.projectId}/${form.milestoneId}`,
        payload,
      );

      emit("update:modelValue", false);
      emit("saved");
      await nextTick();

      await Swal.fire({
        icon: "success",
        title: "마일스톤이 수정되었습니다.",
        confirmButtonText: "확인",
      });
    } else {
      const payload = {
        createdBy: userId,
        milestoneName: form.milestoneName,
        startDate: form.startDate,
        dueDate: form.dueDate,
        description: form.description,
        projectId: props.projectId,
      };

      await api.post(`/MilestoneCreate/${props.projectId}`, payload);

      emit("update:modelValue", false);
      emit("saved");
      await nextTick();

      await Swal.fire({
        icon: "success",
        title: "마일스톤이 생성되었습니다.",
        confirmButtonText: "확인",
      });

      if (props.redirectAfterSave) {
        router.replace({
          name: "projectDash",
          params: { projectId: props.projectId },
        });
      }
    }
  } catch (err) {
    console.error("마일스톤 저장 실패:", err);
    console.error("응답 데이터:", err.response?.data);

    await Swal.fire({
      icon: "error",
      title: props.isEditMode
        ? "마일스톤 수정에 실패했습니다."
        : "마일스톤 생성에 실패했습니다.",
      confirmButtonText: "확인",
    });
  }
};
</script>

<style scoped>
.swal2-container {
  z-index: 99999 !important;
}

.milestone-modal-body {
  padding: 2px 2px 0;
}

.milestone-form-wrap {
  width: 100%;
}

.milestone-form {
  width: 100%;
}

.date-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.date-item {
  flex: 1;
  margin-bottom: 0;
}

.modal-footer-btns {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.cancel-btn {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #374151;
  font-size: 13px;
  font-weight: 500;
}

.cancel-btn:hover {
  background: #e2e8f0;
  border-color: #cbd5e1;
  color: #1f2937;
}

.save-btn {
  background: #1d4ed8;
  border: none;
  color: #fff;
  font-size: 13px;
  font-weight: 600;
}

.save-btn:hover {
  background: #1e40af;
  color: #fff;
}

:deep(.el-dialog) {
  border-radius: 12px;
}

:deep(.el-dialog__header) {
  padding: 20px 20px 10px;
  margin-right: 0;
}

:deep(.el-dialog__body) {
  padding: 8px 20px 16px;
}

:deep(.el-dialog__footer) {
  padding: 12px 20px 20px;
  border-top: none;
}

:deep(.el-dialog__title) {
  font-weight: 700;
  font-size: 15px;
  color: #1a1a2e;
}

:deep(.el-form-item) {
  margin-bottom: 18px;
}

:deep(.el-form-item__label) {
  font-size: 13px;
  color: #374151;
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #d1d5db inset;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #9ca3af inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #3b82f6 inset;
}

:deep(.el-input.is-disabled .el-input__wrapper) {
  background: #f8fafc;
  color: #6b7280;
  box-shadow: 0 0 0 1px #e5e7eb inset;
}

:deep(.el-textarea__inner) {
  border-radius: 8px;
  min-height: 96px !important;
  box-shadow: 0 0 0 1px #d1d5db inset;
}

:deep(.el-textarea__inner:hover) {
  box-shadow: 0 0 0 1px #9ca3af inset;
}

:deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 1px #3b82f6 inset;
}

:deep(.el-date-editor.el-input),
:deep(.el-date-editor.el-input__wrapper) {
  width: 100%;
}

:deep(.el-button) {
  height: 32px;
  padding: 0 14px;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .date-row {
    flex-direction: column;
    gap: 0;
  }

  .modal-footer-btns {
    justify-content: stretch;
  }

  .modal-footer-btns :deep(.el-button) {
    flex: 1;
  }

  :deep(.el-dialog) {
    width: calc(100vw - 24px) !important;
    max-width: 560px;
  }
}
</style>
