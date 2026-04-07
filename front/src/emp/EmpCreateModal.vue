<template>
  <el-dialog
    v-model="visible"
    :title="isEditMode ? '계정 수정' : '새 계정 추가'"
    width="560px"
    :close-on-click-modal="false"
    @close="handleClose"
    @closed="handleClosed"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      label-position="left"
    >
      <el-form-item label="사번(계정 ID)" prop="userId">
        <el-input v-model="form.userId" placeholder="" disabled />
      </el-form-item>
      <el-form-item label="이메일" prop="email">
        <el-input v-model="form.email" placeholder="" type="email" />
      </el-form-item>
      <el-form-item label="비밀번호" prop="password">
        <el-input v-model="form.password" placeholder="" type="password" />
      </el-form-item>
      <el-form-item label="비밀번호 확인" prop="passwordc">
        <el-input v-model="form.passwordc" placeholder="" type="password" />
      </el-form-item>
      <el-form-item label="사원 명" prop="name">
        <div class="row-fields">
          <el-input v-model="form.name" placeholder="" style="flex: 1" />
          <div class="pl-field">
            <span class="pl-label">직군 그룹</span>
            <el-select
              v-model="form.primaryGroupId"
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

      <el-form-item label="관리자 계정 여부">
        <div class="switch-row">
          <el-switch
            v-model="form.userType"
            active-value="ADMIN"
            inactive-value="USER"
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
const emit = defineEmits(["update:modelValue", "submitted"]);

const isEditMode = computed(() => !!props.editData);
const plOptions = computed(() => groupStore.activeGroupList);

const formRef = ref(null);
const submitting = ref(false);

// 💡 [수정됨] form 변수를 먼저 선언해서 undefined 에러를 막습니다!
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
  const pass1 = form.password || "";
  const pass2 = value || "";

  if (isEditMode.value && pass1 === "" && pass2 === "") {
    return callback();
  }
  if (pass1 !== "" && pass2 === "") {
    return callback(new Error("비밀번호를 다시 한 번 입력해주세요."));
  }
  if (pass1 !== pass2) {
    return callback(new Error("비밀번호가 일치하지 않습니다!"));
  }
  callback();
};

// 💡 [수정됨] rules 변수도 위로 올렸습니다.
const rules = reactive({
  email: [
    { required: true, message: "이메일을 입력하세요", trigger: "blur" },
    {
      pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
      message: "올바른 이메일 형식이 아닙니다. (예: user@compath.com)",
      trigger: "blur",
    },
  ],
  name: [{ required: true, message: "이름을 입력하세요", trigger: "blur" }],
  password: [
    {
      required: true, // 초기값 설정 (watch에서 바꿈)
      message: "비밀번호를 입력하세요",
      trigger: "blur",
    },
  ],
  passwordc: [
    {
      required: true, // 초기값 설정
      validator: validatePasswordConfirm,
      trigger: "blur",
    },
  ],
});

// 💡 [수정됨] watch 로직을 변수 선언들 아래로 배치했습니다.
watch(
  () => props.modelValue,
  async (isOpen) => {
    if (isOpen) {
      console.log("1. 넘어온 그룹ID:", props.editData.primaryGroupId);
      console.log(
        "2. 현재 옵션 목록:",
        plOptions.value.map((g) => g.groupId),
      );
      if (isEditMode.value) {
        // ⭐ 수정 모드
        Object.assign(form, {
          userId: props.editData.userId,
          name: props.editData.name,
          email: props.editData.email,
          userType: props.editData.userType,
          primaryGroupId: props.editData.primaryGroupId?.toString() || "",
          password: "",
          passwordc: "",
        });
        rules.password[0].required = false;
        rules.passwordc[0].required = false;
      } else {
        // ⭐ 생성 모드
        Object.assign(form, defaultForm());
        rules.password[0].required = true;
        rules.passwordc[0].required = true;
      }

      // 💡 [핵심] 폼 값과 룰이 완전히 적용된 것을 기다린 후 빨간 불 지우기!
      await nextTick();
      formRef.value?.clearValidate();
    }
  },
);

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit("update:modelValue", v),
});

const handleClose = () => {
  visible.value = false;
};

// 💡 [추가됨] 모달이 화면에서 완전히 사라졌을 때 백지 상태로 세탁
const handleClosed = () => {
  Object.assign(form, defaultForm());
  rules.password[0].required = true;
  rules.passwordc[0].required = true;
  formRef.value?.clearValidate();
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
      form.groupIds = [...form.primaryGroupId];
      form.primaryGroupId = form.primaryGroupId;
      await empStore.updateEmp(form);
    } else {
      await empStore.registerEmp(form);
    }
    emit("submitted", { ...form });
    visible.value = false; // 성공하면 모달 닫기 -> 자동으로 handleClosed 실행됨
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
