<template>
  <el-dialog
    v-model="visible"
    :title="isEditMode ? '계정 수정' : '새 계정 추가'"
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
                :key="group.groupId.toString()"
                :label="group.groupName"
                :value="group.groupId.toString()"
              />
            </el-select>
          </div>
        </div>
      </el-form-item>

      <!-- 관리자 계정 여부 -->
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
            {{ isEditMode ? "수정 완료" : "계정 생성" }}
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch, nextTick } from "vue";
import { useGroupStore } from "../stores/groupSJW";
import { useEmpStore } from "../stores/empSJW";
const groupStore = useGroupStore();
const empStore = useEmpStore();

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  editData: { type: Object, default: null },
});
const isEditMode = computed(() => !!props.editData);
const emit = defineEmits(["update:modelValue", "submitted"]);
watch(
  () => props.modelValue,
  (isOpen) => {
    if (isOpen) {
      if (isEditMode.value) {
        // ⭐ 수정 모드: 데이터 덮어쓰기 & 비밀번호 필수 해제
        Object.assign(form, {
          userId: props.editData.userId,
          name: props.editData.name,
          email: props.editData.email,
          userType: props.editData.userType,
          groupId: props.editData.groupId?.toString() || "",
          password: "",
          passwordc: "",
        });
        rules.password[0].required = false; // 필수 아님!
        rules.passwordc[0].required = false; // 필수 아님!
      } else {
        // ⭐ 생성 모드: 데이터 초기화 & 비밀번호 필수로 복구
        Object.assign(form, defaultForm());
        rules.password[0].required = true;
        rules.passwordc[0].required = true;
      }

      // 모달이 열리면서 발생한 쓸데없는 에러 메시지 찌꺼기 즉시 청소
      nextTick(() => {
        formRef.value?.clearValidate();
      });
    }
  },
);
const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit("update:modelValue", v),
});

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
  groupIds: null,
  primaryGroupId: "",
});

const form = reactive(defaultForm());
// 비밀번호 확인 검증 함수
const validatePasswordConfirm = (rule, value, callback) => {
  const pass1 = form.password || ""; // 원본 비밀번호 (null 방지)
  const pass2 = value || ""; // 확인 비밀번호 (null 방지)

  // 1. 수정 모드인데 둘 다 안 건드렸다? -> 무사 통과
  if (isEditMode.value && pass1 === "" && pass2 === "") {
    return callback();
  }

  // 2. 위에 비밀번호는 쳤는데, 아래(확인)를 안 쳤다? -> 에러
  if (pass1 !== "" && pass2 === "") {
    return callback(new Error("비밀번호를 다시 한 번 입력해주세요."));
  }

  // 3. 둘 다 쳤는데 값이 다르다? -> 에러
  if (pass1 !== pass2) {
    return callback(new Error("비밀번호가 일치하지 않습니다!"));
  }

  // 다 통과하면 성공
  callback();
};
const rules = reactive({
  email: [
    { required: true, message: "이메일을 입력하세요", trigger: "blur" },
    {
      pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
      message: "올바른 이메일 형식이 아닙니다. (예: user@compath.com)",
      trigger: "blur", // 포커스를 잃을 때 검사
    },
  ],
  name: [
    {
      required: true,
      message: "이름을 입력하세요",
      trigger: "blur",
    },
  ],
  // 비밀번호 룰
  password: [
    {
      required: !isEditMode.value, // 신규 생성(!isEditMode)일 때만 true(필수)
      message: "비밀번호를 입력하세요",
      trigger: "blur",
    },
  ],

  // 비밀번호 확인 룰
  passwordc: [
    {
      required: !isEditMode.value, // 신규 생성일 때만 필수
      validator: validatePasswordConfirm,
      trigger: "blur",
    },
  ],
});

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
    if (isEditMode.value) {
      form.groupIds = [...form.groupId];
      form.primaryGroupId = form.groupId;
      console.log(form);
      await empStore.updateEmp(form);
      console.log("등록 데이터:", { ...form });
    } else {
      await empStore.registerEmp(form);
      console.log("등록 데이터:", { ...form });
    }
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
