<template>
  <el-dialog
    v-model="visible"
    title="새 계정 추가"
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
      <!-- 사번 -->
      <el-form-item label="사번(계정 ID)" prop="userId">
        <el-input v-model="form.userId" placeholder="" disabled="true" />
      </el-form-item>
      <!-- 이메일 -->
      <el-form-item label="이메일" prop="email">
        <el-input v-model="form.email" placeholder="" type="email" />
      </el-form-item>
      <!-- 비밀번호 -->
      <el-form-item label="비밀번호" prop="password">
        <el-input v-model="form.password" placeholder="" type="password" />
      </el-form-item>
      <!-- 비밀번호 확인 -->
      <el-form-item label="비밀번호 확인" prop="passwordc">
        <el-input v-model="form.passwordc" placeholder="" type="password" />
      </el-form-item>
      <!-- 사용자 이름 -->
      <el-form-item label="사원 명" prop="name">
        <div class="row-fields">
          <el-input v-model="form.name" placeholder="" style="flex: 1" />
          <div class="pl-field">
            <span class="pl-label">직군 그룹</span>
            <el-select
              v-model="form.groupId"
              placeholder="선택"
              style="width: 160px"
            >
              <el-option
                v-for="group in plOptions"
                :key="group.groupId"
                :label="group.groupName"
                :value="group.groupId"
              />
            </el-select>
          </div>
        </div>
      </el-form-item>

      <!-- 공개 여부 -->
      <el-form-item label="관리자 계정 여부">
        <div class="switch-row">
          <el-switch
            v-model="form.userType"
            active-value="ADMIN"
            inactive-value="USER"
            @click="console.log(form)"
          />
          <span class="switch-desc">
            관리자 계정입니까?<br />
            <span class="switch-sub"
              >관리자 계정이라면 토글을 활성화해주세요.</span
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
            계정 생성
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useGroupStore } from "../stores/group";
import { useEmpStore } from "../stores/emp";
const groupStore = useGroupStore();
const empStore = useEmpStore();

const props = defineProps({
  modelValue: { type: Boolean, default: false },
});
const emit = defineEmits(["update:modelValue", "submitted"]);

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit("update:modelValue", v),
});

import { computed } from "vue";

// ── PL 옵션 (백엔드 연결 시 API로 교체) ──
const plOptions = computed(() => groupStore.activeGroupList);

const formRef = ref(null);
const submitting = ref(false);

const defaultForm = () => ({
  userId: "",
  name: "",
  email: "",
  password: "",
  passwordc: "",
  userType: "USER",
  groupId: "",
});

const form = reactive(defaultForm());
// 비밀번호 확인을 위한 커스텀 검증 함수
const validatePasswordConfirm = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("비밀번호를 다시 한 번 입력해주세요."));
  } else if (value !== form.password) {
    // 폼에 입력된 원본 비밀번호와 비교
    callback(new Error("비밀번호가 일치하지 않습니다!"));
  } else {
    callback(); // 성공 시 빈 콜백 호출
  }
};
const rules = {
  email: [{ required: true, message: "이메일을 입력하세요", trigger: "blur" }],
  name: [
    {
      required: true,
      message: "이름을 입력하세요",
      trigger: "blur",
    },
  ],
  password: [
    { required: true, message: "비밀번호를 입력하세요", trigger: "blur" },
  ],
  passwordc: [
    { required: true, validator: validatePasswordConfirm, trigger: "blur" },
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

  submitting.value = true;
  try {
    await empStore.registerEmp(form);
    console.log("등록 데이터:", { ...form });
    emit("submitted", { ...form });
    visible.value = false;
    handleReset();
  } catch (err) {
    console.error("사원 등록 실패:", err);
  } finally {
    submitting.value = false;
    empStore.getEmpList();
  }
};
onMounted(async () => {
  await groupStore.getActiveGroupList();
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
