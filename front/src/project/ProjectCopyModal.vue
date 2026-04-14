<template>
  <el-dialog
    v-model="visible"
    title="프로젝트 복사"
    width="620px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      label-position="left"
    >
      <!-- 원본 프로젝트 -->
      <el-form-item label="원본 프로젝트" prop="sourceProjectId">
        <el-select
          v-model="form.sourceProjectId"
          placeholder="복사할 프로젝트를 선택하세요"
          style="width: 100%"
          filterable
        >
          <el-option
            v-for="project in projectOptions"
            :key="project.projectId"
            :label="project.projectName"
            :value="project.projectId"
          />
        </el-select>
      </el-form-item>

      <!-- 새 프로젝트 명 -->
      <el-form-item label="새 프로젝트 명" prop="projectName">
        <el-input v-model="form.projectName" />
      </el-form-item>

      <!-- 프로젝트 식별자 + 총괄PL -->
      <el-form-item label="프로젝트 식별자" prop="projectCode">
        <div class="row-fields">
          <el-input v-model="form.projectCode" style="flex: 1" />
          <div class="pl-field">
            <span class="pl-label">총괄PL</span>
            <el-select
              v-model="form.plUserId"
              placeholder="선택"
              style="width: 140px"
            >
              <el-option
                v-for="user in plOptions"
                :key="user.userId"
                :label="user.userName"
                :value="user.userId"
              />
            </el-select>
          </div>
        </div>
      </el-form-item>

      <!-- 프로젝트 설명 -->
      <el-form-item label="프로젝트 설명">
        <el-input v-model="form.description" type="textarea" :rows="3" />
      </el-form-item>

      <!-- 원본 설정 안내 -->
      <el-form-item label="복사 기준">
        <div class="inherit-box">
          <div class="inherit-row">
            <span class="inherit-label">프로젝트 기간</span>
            <span class="inherit-value">원본 프로젝트 기간을 따릅니다.</span>
          </div>
          <div class="inherit-row">
            <span class="inherit-label">공개 여부</span>
            <span class="inherit-value">원본 프로젝트 설정을 따릅니다.</span>
          </div>
          <div class="inherit-row">
            <span class="inherit-label">마일스톤 사용 여부</span>
            <span class="inherit-value">원본 프로젝트 설정을 따릅니다.</span>
          </div>
        </div>
      </el-form-item>

      <!-- 복사 옵션 -->
      <el-form-item label="복사 옵션">
        <div class="copy-option-box">
          <el-checkbox v-model="form.copyMembers">구성원 복사</el-checkbox>
          <span class="copy-help">
            체크하지 않으면 하위프로젝트 담당자와 업무 담당자는 비워집니다.
          </span>

          <el-checkbox v-model="form.copyMilestones">
            마일스톤 복사
          </el-checkbox>
          <span class="copy-help">
            체크하지 않으면 마일스톤과 매핑 정보는 복사되지 않습니다.
          </span>

          <el-checkbox v-model="form.copyTasks">업무 복사</el-checkbox>
          <span class="copy-help">
            체크하면 상위/하위프로젝트 업무와 부모-하위업무 관계까지 복사됩니다.
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
            프로젝트 복사
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from "vue";
import { useAuthStore } from "../stores/auth";
import api from "../utils/api";

const authStore = useAuthStore();

const props = defineProps({
  modelValue: { type: Boolean, default: false },
});

const emit = defineEmits(["update:modelValue", "submitted"]);

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit("update:modelValue", v),
});

const formRef = ref(null);
const submitting = ref(false);

const plOptions = ref([]);
const projectOptions = ref([]);

const defaultForm = () => ({
  sourceProjectId: null,
  projectName: "",
  projectCode: "",
  plUserId: null,
  description: "",
  copyMembers: false,
  copyMilestones: true,
  copyTasks: true,
});

const form = reactive(defaultForm());

const rules = {
  sourceProjectId: [
    {
      required: true,
      message: "원본 프로젝트를 선택하세요",
      trigger: "change",
    },
  ],
  projectName: [
    { required: true, message: "프로젝트 명을 입력하세요", trigger: "blur" },
  ],
  projectCode: [
    {
      required: true,
      message: "프로젝트 식별자를 입력하세요",
      trigger: "blur",
    },
  ],
  plUserId: [
    { required: true, message: "총괄PL을 선택하세요", trigger: "change" },
  ],
};

const fetchPlList = async () => {
  try {
    const res = await api.get("/ProjectPlList");
    plOptions.value = res.data || [];
  } catch (err) {
    console.error("PL 목록 조회 실패:", err);
  }
};

const fetchProjectList = async () => {
  try {
    const res = await api.get("/ProjectList");
    projectOptions.value = res.data || [];
  } catch (err) {
    console.error("프로젝트 목록 조회 실패:", err);
  }
};

onMounted(() => {
  fetchPlList();
  fetchProjectList();
});

watch(
  () => form.copyTasks,
  (val) => {
    if (val) {
      form.copyMilestones = true;
    }
  },
);

const handleClose = () => {
  visible.value = false;
};

const handleReset = () => {
  Object.assign(form, defaultForm());
  formRef.value?.clearValidate();
};

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  submitting.value = true;

  try {
    const selectedProject = projectOptions.value.find(
      (item) => item.projectId === form.sourceProjectId,
    );

    const payload = {
      sourceProjectId: form.sourceProjectId,
      projectName: form.projectName,
      identifier: form.projectCode,
      description: form.description,
      startDate: selectedProject?.startDate ?? null,
      endDate: selectedProject?.endDate ?? null,
      isPublic: selectedProject?.isPublic ?? selectedProject?.is_public ?? null,
      useMilestone:
        selectedProject?.useMilestone ?? selectedProject?.use_milestone ?? null,
      pmUserId: selectedProject?.pmUserId ?? null,
      plUserId: form.plUserId,
      createdBy: authStore.user.userId,
      copyMembers: form.copyMembers ? "Y" : "N",
      copyMilestones: form.copyMilestones ? "Y" : "N",
      copyTasks: form.copyTasks ? "Y" : "N",
    };

    await api.post("/ProjectCopy", payload);

    visible.value = false;
    handleReset();
    emit("submitted");
  } catch (err) {
    console.error("프로젝트 복사 실패:", err);
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
.row-fields {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.pl-field {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.pl-label {
  font-size: 13px;
  color: #374151;
  white-space: nowrap;
}

.copy-option-box {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: #f8fafc;
}

.copy-help {
  margin: -2px 0 8px 24px;
  font-size: 12px;
  color: #94a3b8;
  line-height: 1.5;
}

.inherit-box {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.inherit-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.inherit-label {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
}

.inherit-value {
  font-size: 13px;
  color: #6b7280;
  text-align: right;
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

:deep(.el-checkbox) {
  margin-right: 0;
}
</style>
