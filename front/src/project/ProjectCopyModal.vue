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

      <el-form-item label="복사 기간">
        <div class="date-range-row">
          <el-date-picker
            v-model="form.startDate"
            type="date"
            placeholder="시작일 선택"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
            disabled
          />
          <span class="date-separator">~</span>
          <el-date-picker
            v-model="form.endDate"
            type="date"
            placeholder="종료일 선택"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
            disabled
          />
        </div>

        <div class="inherit-help">
          원본 프로젝트 선택 시 오늘 기준으로 기간이 자동 계산됩니다.
        </div>
      </el-form-item>

      <el-form-item label="새 프로젝트 명" prop="projectName">
        <el-input v-model="form.projectName" />
      </el-form-item>

      <el-form-item label="총괄PL" prop="plUserId">
        <el-select
          v-model="form.plUserId"
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

      <el-form-item label="프로젝트 설명">
        <el-input v-model="form.description" type="textarea" :rows="3" />
      </el-form-item>

      <el-form-item label="복사 옵션">
        <div class="copy-option-box">
          <el-checkbox v-model="form.copyMembers">구성원 복사</el-checkbox>
          <span class="copy-help">
            체크하지 않으면 하위프로젝트 담당자와 업무 담당자는 비워집니다.
          </span>

          <el-checkbox
            v-model="form.copySubProjects"
            :disabled="isSubProjectLocked"
          >
            하위프로젝트 복사
          </el-checkbox>
          <span class="copy-help">
            체크하면 원본 프로젝트의 하위프로젝트 구조도 함께 복사됩니다.
          </span>

          <el-checkbox
            v-model="form.copyMilestones"
            :disabled="isMilestoneLocked"
          >
            마일스톤 복사
          </el-checkbox>
          <span class="copy-help">
            하위프로젝트 또는 업무를 복사하면 마일스톤도 함께 복사됩니다.
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
import Swal from "sweetalert2";

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
  plUserId: null,
  description: "",
  startDate: "",
  endDate: "",
  copyMembers: false,
  copySubProjects: false,
  copyMilestones: false,
  copyTasks: false,
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
  plUserId: [
    { required: true, message: "총괄PL을 선택하세요", trigger: "change" },
  ],
  startDate: [
    { required: true, message: "시작일을 선택하세요", trigger: "change" },
  ],
  endDate: [
    { required: true, message: "종료일을 선택하세요", trigger: "change" },
  ],
};

const selectedProject = computed(() => {
  return (
    projectOptions.value.find(
      (item) => item.projectId === form.sourceProjectId
    ) || null
  );
});

const isSubProjectLocked = computed(() => {
  return form.copyTasks;
});

const isMilestoneLocked = computed(() => {
  return form.copyTasks || form.copySubProjects;
});

const parseLocalDate = (dateStr) => {
  if (!dateStr) return null;

  const pureDate = String(dateStr).split("T")[0];
  const parts = pureDate.split("-");

  if (parts.length !== 3) return null;

  const year = Number(parts[0]);
  const month = Number(parts[1]) - 1;
  const day = Number(parts[2]);

  return new Date(year, month, day);
};

const formatDate = (date) => {
  if (!(date instanceof Date) || Number.isNaN(date.getTime())) return "";

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");

  return `${year}-${month}-${day}`;
};

const isWeekday = (date) => {
  const day = date.getDay();
  return day !== 0 && day !== 6;
};

const getWorkdaysBetween = (start, end) => {
  if (!start || !end) return null;

  const s = new Date(start.getFullYear(), start.getMonth(), start.getDate());
  const e = new Date(end.getFullYear(), end.getMonth(), end.getDate());

  if (e < s) return 1;

  let count = 0;
  const cursor = new Date(s);

  while (cursor <= e) {
    if (isWeekday(cursor)) {
      count += 1;
    }
    cursor.setDate(cursor.getDate() + 1);
  }

  return count < 1 ? 1 : count;
};

const addWorkdays = (start, days) => {
  if (!start) return null;

  const result = new Date(
    start.getFullYear(),
    start.getMonth(),
    start.getDate()
  );

  if (!days || days <= 1) {
    return result;
  }

  let added = 1;

  while (added < days) {
    result.setDate(result.getDate() + 1);

    if (isWeekday(result)) {
      added += 1;
    }
  }

  return result;
};

const applyAutoDateRange = () => {
  const source = selectedProject.value;

  if (!source) {
    form.startDate = "";
    form.endDate = "";
    return;
  }

  const originalStart = parseLocalDate(source.startDate);
  const originalEnd = parseLocalDate(source.endDate);

  const today = new Date();
  const todayOnly = new Date(
    today.getFullYear(),
    today.getMonth(),
    today.getDate()
  );

  const workdays = getWorkdaysBetween(originalStart, originalEnd);

  form.startDate = formatDate(todayOnly);

  if (!workdays) {
    form.endDate = "";
    return;
  }

  const newEndDate = addWorkdays(todayOnly, workdays);
  form.endDate = formatDate(newEndDate);
};

const fetchPlList = async () => {
  try {
    const res = await api.get("/ProjectPlList");
    plOptions.value = res.data || [];
  } catch (err) {
    console.error("PL 목록 조회 실패:", err);
    plOptions.value = [];
  }
};

const fetchProjectList = async () => {
  try {
    const userId = authStore.user?.userId;

    if (!userId) {
      projectOptions.value = [];
      return;
    }

    const res = await api.get("/ProjectList", {
      params: { userId },
    });

    projectOptions.value = Array.isArray(res.data) ? res.data : [];
  } catch (err) {
    console.error("프로젝트 목록 조회 실패:", err);
    projectOptions.value = [];
  }
};

onMounted(() => {
  fetchPlList();
  fetchProjectList();
});

watch(
  () => form.sourceProjectId,
  (newVal) => {
    if (!newVal) {
      form.startDate = "";
      form.endDate = "";
      form.projectName = "";
      return;
    }

    const source = selectedProject.value;

    if (source) {
      form.projectName = `${source.projectName}_COPY`;
    }

    applyAutoDateRange();

    if (form.copyTasks || form.copySubProjects) {
      form.copyMilestones = true;
    }
  }
);

watch(
  () => form.copyTasks,
  (val) => {
    if (val) {
      form.copySubProjects = true;
      form.copyMilestones = true;
    }
  }
);

watch(
  () => form.copySubProjects,
  (val) => {
    if (val) {
      form.copyMilestones = true;
    }
  }
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
    const payload = {
      sourceProjectId: form.sourceProjectId,
      projectName: form.projectName,
      description: form.description,
      startDate: form.startDate || null,
      endDate: form.endDate || null,
      isPublic:
        selectedProject.value?.isPublic ??
        selectedProject.value?.is_public ??
        null,
      useMilestone:
        selectedProject.value?.useMilestone ??
        selectedProject.value?.use_milestone ??
        null,
      pmUserId:
        selectedProject.value?.pmUserId ??
        selectedProject.value?.pm_user_id ??
        null,
      plUserId: form.plUserId,
      createdBy: authStore.user?.userId,
      copyMembers: form.copyMembers ? "Y" : "N",
      copySubProjects: form.copySubProjects ? "Y" : "N",
      copyMilestones: form.copyMilestones ? "Y" : "N",
      copyTasks: form.copyTasks ? "Y" : "N",
    };

    await api.post("/ProjectCopy", payload);

    await Swal.fire({
      title: "복사되었습니다.",
      icon: "success",
      confirmButtonText: "확인",
    });

    visible.value = false;
    handleReset();
    emit("submitted");
  } catch (err) {
    console.error("프로젝트 복사 실패:", err);

    await Swal.fire({
      title: "복사 실패",
      text: "처리 중 오류가 발생했습니다.",
      icon: "error",
      confirmButtonText: "확인",
    });
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
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

.date-range-row {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-separator {
  flex-shrink: 0;
  font-size: 14px;
  font-weight: 600;
  color: #6b7280;
}

.inherit-help {
  margin-top: 8px;
  margin-left: 2px;
  font-size: 12px;
  color: #94a3b8;
  line-height: 1.5;
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