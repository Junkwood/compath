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
        <el-input v-model="form.projectName" placeholder="" />
      </el-form-item>

      <!-- 프로젝트 식별자 + 총괄PL -->
      <el-form-item label="프로젝트 식별자" prop="projectCode">
        <div class="row-fields">
          <el-input v-model="form.projectCode" placeholder="" style="flex: 1" />
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

      <!-- 프로젝트 기간 -->
      <el-form-item label="프로젝트 기간" prop="startDate">
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

      <!-- 프로젝트 설명 -->
      <el-form-item label="프로젝트 설명">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder=""
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
            <span class="switch-sub"
              >공개된 프로젝트는 누구나 조회할 수 있습니다.</span
            >
          </span>
        </div>
      </el-form-item>
    </el-form>

    <!-- 푸터 버튼 -->
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

// ── PL 옵션 (백엔드 연결 시 API로 교체) ──
const plOptions = ref([]);

const fetchPlList = async () => {
  const res = await api.get("/ProjectPlList");
  console.log(res.data);
  plOptions.value = res.data;
};

const formRef = ref(null);
const submitting = ref(false);

//폼의 초기값
const defaultForm = () => ({
  projectName: "",
  projectCode: "",
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
  projectCode: [
    {
      required: true,
      message: "프로젝트 식별자를 입력하세요",
      trigger: "blur",
    },
  ],
  userId: [{ required: true, message: "총괄PL을 선택하세요", trigger: "blur" }],
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

  submitting.value = true;
  try {
    const payload = {
      projectName: form.projectName,
      identifier: form.projectCode,
      plUserId: form.plUserId,
      startDate: form.startDate,
      endDate: form.endDate,
      description: form.description,
      useMilestone: form.useMilestone ? "O1" : "O2",
      isPublic: form.isPublic ? "O1" : "O2",
      createdBy: authStore.user.userId,
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
/* 인라인 필드 묶음 */
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

/* 날짜 행 */
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

/* 스위치 행 */
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

/* 푸터 */
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

/* el-dialog 오버라이드 */
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
