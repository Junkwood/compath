<template>
  <el-dialog
    v-model="visible"
    title="새 프로젝트 생성"
    width="560px"
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
      <!-- 프로젝트 명 -->
      <el-form-item label="프로젝트 명" prop="projectName">
        <el-input
          v-model="form.projectName"
          placeholder="프로젝트명을 입력하세요"
        />
      </el-form-item>

      <!-- 프로젝트 식별자 -->
      <el-form-item label="프로젝트 식별자">
        <el-input
          model-value="저장 시 자동 생성"
          disabled
          placeholder="저장 시 자동 생성"
        />
      </el-form-item>

      <!-- PL -->
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

      <!-- 프로젝트 기간 -->
      <el-form-item label="프로젝트 기간">
        <div class="date-row">
          <el-form-item prop="startDate" class="date-item">
            <el-date-picker
              v-model="form.startDate"
              type="date"
              placeholder="시작일"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>

          <span class="date-sep">~</span>

          <el-form-item prop="endDate" class="date-item">
            <el-date-picker
              v-model="form.endDate"
              type="date"
              placeholder="종료일"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </div>
      </el-form-item>

      <!-- 프로젝트 설명 -->
      <el-form-item label="프로젝트 설명">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder="프로젝트 설명을 입력하세요"
        />
      </el-form-item>

      <!-- 마일스톤 사용 여부 -->
      <el-form-item label="마일스톤 사용 여부">
        <div class="switch-row">
          <el-switch v-model="form.useMilestone" />
          <span class="switch-desc">마일스톤 기능을 사용하시겠습니까?</span>
        </div>
      </el-form-item>

      <!-- 공개 여부 -->
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
            프로젝트 생성
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
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

const fetchPlList = async () => {
  try {
    const res = await api.get("/ProjectPlList");
    plOptions.value = res.data || [];
  } catch (err) {
    console.error("PL 목록 조회 실패:", err);
    plOptions.value = [];
  }
};

const defaultForm = () => ({
  projectName: "",
  plUserId: null,
  startDate: "",
  endDate: "",
  description: "",
  useMilestone: true,
  isPublic: true,
});

const form = reactive(defaultForm());

const rules = {
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

  if (form.startDate && form.endDate && form.startDate > form.endDate) {
    alert("종료일은 시작일보다 빠를 수 없습니다.");
    return;
  }

  submitting.value = true;

  try {
    const payload = {
      projectName: form.projectName,
      description: form.description,
      startDate: form.startDate,
      endDate: form.endDate,
      isPublic: form.isPublic ? "P1" : "P2",
      createdBy: authStore.user.userId,
      plUserId: form.plUserId,
      useMilestone: form.useMilestone ? "O2" : "O1",
    };

    await api.post("/ProjectRegister", payload);

    visible.value = false;
    handleReset();
    emit("submitted");
  } catch (err) {
    console.error("프로젝트 등록 실패:", err);
  } finally {
    submitting.value = false;
  }
};

onMounted(() => {
  fetchPlList();
});
</script>

<style scoped>
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

:deep(.date-item .el-form-item__content) {
  margin-left: 0 !important;
}
</style>
