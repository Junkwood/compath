<template>
  <el-dialog
    v-model="visible"
    :title="isEditMode ? '하위 프로젝트 수정' : '하위 프로젝트 생성'"
    width="560px"
    :close-on-click-modal="false"
    :append-to-body="true"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      label-position="left"
    >
      <el-form-item label="상위프로젝트">
        <el-input :model-value="parentProjectName" readonly disabled />
      </el-form-item>

      <el-form-item label="마일스톤" prop="milestoneId">
        <el-select
          v-model="form.milestoneId"
          placeholder="마일스톤 선택"
          style="width: 100%"
        >
          <el-option
            v-for="milestone in milestoneOptions"
            :key="milestone.milestoneId"
            :label="milestone.milestoneName"
            :value="milestone.milestoneId"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="하위프로젝트 명" prop="projectName">
        <el-input v-model="form.projectName" />
      </el-form-item>

      <!-- 수정 모드일 때만 식별자 표시 -->
      <el-form-item v-if="isEditMode" label="하위프로젝트 식별자">
        <el-input v-model="form.identifier" readonly disabled />
      </el-form-item>

      <el-form-item label="하위PL" prop="subPlUserId">
        <el-select
          v-model="form.subPlUserId"
          placeholder="선택"
          style="width: 100%"
        >
          <el-option
            v-for="user in plOptions"
            :key="user.userId"
            :label="user.userName"
            :value="user.userId"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="하위프로젝트 기간" prop="startDate">
        <div class="date-row">
          <el-date-picker
            v-model="form.startDate"
            type="date"
            placeholder="시작일"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="flex: 1"
          />
          <span class="date-sep">~</span>
          <el-date-picker
            v-model="form.endDate"
            type="date"
            placeholder="종료일"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="flex: 1"
          />
        </div>
      </el-form-item>

      <el-form-item label="하위프로젝트 설명">
        <el-input v-model="form.description" type="textarea" :rows="3" />
      </el-form-item>

      <el-form-item label="공개 여부">
        <div class="switch-row">
          <el-switch v-model="form.isPublic" />
          <span class="switch-desc">
            모든 사용자에게 공개<br />
            <span class="switch-sub">
              공개된 프로젝트는 누구나 조회할 수 있습니다.
            </span>
          </span>
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="modal-footer">
        <el-button class="btn-list" @click="handleClose">← 목록으로</el-button>
        <div class="footer-right">
          <el-button class="btn-reset" @click="handleReset">↺ 초기화</el-button>
          <el-button
            class="btn-submit"
            :loading="submitting"
            @click="handleSubmit"
          >
            {{ isEditMode ? "프로젝트 수정" : "프로젝트 생성" }}
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from "vue";
import api from "../utils/api";
import { useAuthStore } from "../stores/auth";
import Swal from "sweetalert2";

const authStore = useAuthStore();

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  projectId: { type: Number, required: true },
  parentProjectName: { type: String, default: "" },
  parentStartDate: { type: String, default: "" },
  parentEndDate: { type: String, default: "" },

  isEditMode: { type: Boolean, default: false },
  editData: {
    type: Object,
    default: () => null,
  },
});

const emit = defineEmits(["update:modelValue", "submitted"]);

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit("update:modelValue", v),
});

const milestoneOptions = ref([]);
const plOptions = ref([]);
const formRef = ref(null);
const submitting = ref(false);

const fetchMilestoneList = async () => {
  try {
    const res = await api.get(`/MilestoneTab/${props.projectId}`);
    milestoneOptions.value = res.data;
  } catch (err) {
    console.error("마일스톤 목록 조회 실패:", err);
    milestoneOptions.value = [];
  }
};

const fetchPlList = async () => {
  try {
    const res = await api.get(`/ProjectRolePlList/${props.projectId}`);
    plOptions.value = res.data;
  } catch (err) {
    console.error("PL 목록 조회 실패:", err);
    console.error("응답 데이터:", err.response?.data);
    plOptions.value = [];
  }
};

const defaultForm = () => ({
  projectId: null,
  parentProjectId: props.projectId,
  milestoneId: null,
  milestoneMappingId: null,
  projectName: "",
  identifier: "",
  subPlUserId: null,
  startDate: "",
  endDate: "",
  description: "",
  useMilestone: true,
  isPublic: true,
  userId: authStore.user?.userId ?? null,
});

const form = reactive(defaultForm());

const rules = {
  projectName: [
    { required: true, message: "프로젝트 명을 입력하세요", trigger: "blur" },
  ],
  subPlUserId: [
    { required: true, message: "하위PL을 선택하세요", trigger: "change" },
  ],
  milestoneId: [
    { required: true, message: "마일스톤을 선택하세요", trigger: "change" },
  ],
  startDate: [
    { required: true, message: "시작일을 선택하세요", trigger: "change" },
  ],
  endDate: [
    { required: true, message: "종료일을 선택하세요", trigger: "change" },
  ],
};

const handleClose = () => {
  visible.value = false;
};

const handleReset = () => {
  Object.assign(form, defaultForm());

  if (props.isEditMode && props.editData) {
    fillForm();
  }

  formRef.value?.clearValidate();
};

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  const payload = {
    projectId: form.projectId,
    parentProjectId: props.projectId,
    milestoneId: form.milestoneId,
    milestoneMappingId: form.milestoneMappingId,
    projectName: form.projectName,
    subPlUserId: form.subPlUserId,
    startDate: form.startDate,
    endDate: form.endDate,
    description: form.description,
    useMilestone: form.useMilestone,
    isPublic: form.isPublic ? "P1" : "P2",
    userId: authStore.user?.userId,
  };

  try {
    if (props.isEditMode) {
      const result = await Swal.fire({
        title: "수정하시겠습니까?",
        text: "하위프로젝트 정보를 수정합니다.",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "수정",
        cancelButtonText: "취소",
        reverseButtons: true,
        customClass: {
          popup: "subproject-swal-popup",
        },
      });

      if (!result.isConfirmed) return;
    }

    submitting.value = true;

    if (props.isEditMode) {
      await api.post("/ProjectSubModify", payload);

      await Swal.fire({
        title: "수정되었습니다.",
        icon: "success",
        confirmButtonText: "확인",
      });
    } else {
      await api.post("/ProjectSubRegister", payload);

      await Swal.fire({
        title: "생성되었습니다.",
        icon: "success",
        confirmButtonText: "확인",
      });
    }

    visible.value = false;
    handleReset();
    emit("submitted");
  } catch (err) {
    console.error(
      props.isEditMode ? "프로젝트 수정 실패:" : "프로젝트 등록 실패:",
      err,
    );

    await Swal.fire({
      title: props.isEditMode ? "수정 실패" : "생성 실패",
      text: "처리 중 오류가 발생했습니다.",
      icon: "error",
      confirmButtonText: "확인",
    });
  } finally {
    submitting.value = false;
  }
};

const fillForm = () => {
  if (props.isEditMode && props.editData) {
    Object.assign(form, {
      projectId: props.editData.projectId ?? null,
      parentProjectId: props.projectId,
      milestoneId: props.editData.milestoneId ?? null,
      milestoneMappingId: props.editData.milestoneMappingId ?? null,
      projectName: props.editData.projectName ?? "",
      identifier: props.editData.identifier ?? "",
      subPlUserId: props.editData.subPlUserId ?? null,
      startDate: props.editData.startDate ?? "",
      endDate: props.editData.endDate ?? "",
      description: props.editData.description ?? "",
      useMilestone: props.editData.useMilestone ?? true,
      isPublic: props.editData.isPublic === "P1",
      userId: authStore.user?.userId ?? null,
    });
  } else {
    Object.assign(form, defaultForm());
  }
};

watch(
  () => visible.value,
  (newVal) => {
    if (newVal) {
      fetchPlList();
      fetchMilestoneList();
      fillForm();
    }
  },
);
</script>

<style>
.swal2-container {
  z-index: 99999 !important;
}

.swal2-popup {
  z-index: 100000 !important;
}

.date-row {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.date-sep {
  color: #9ca3af;
  flex-shrink: 0;
}

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
</style>
