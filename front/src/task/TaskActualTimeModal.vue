<template>
  <el-dialog
    v-model="visible"
    title="소요시간 등록"
    width="560px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="ruleFormRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      label-position="left"
    >
      <!-- 프로젝트 명 -->
      <el-form-item label="업무명">
        <el-input
          v-model="form.taskTitle"
          placeholder=""
          disabled
          class="w-full rounded-lg text-sm text-gray-700 bg-white border rounded-[10px] outline-none transition focus:border-slate-400 focus:shadow-[0_0_0_3px_rgba(148,163,184,0.15)] disabled:bg-slate-100 disabled:text-slate-400 cursor-pointer"
        />
      </el-form-item>
      <!-- 작업일자 작업자 -->
      <div class="date-row mb-6">
        <el-form-item label="작업일" class="date-item">
          <TaskDatePicker
            placeholder="시작일 선택"
            v-model="form.workDate"
            @change="calcEstTime(true)"
          />
        </el-form-item>

        <el-form-item label="작업시간(시간)" class="date-item" prop="hours">
          <el-input
            class="w-full rounded-lg text-sm text-gray-700 bg-white rounded-[10px] outline-none transition focus:border-slate-400 focus:shadow-[0_0_0_3px_rgba(148,163,184,0.15)] disabled:bg-slate-100 disabled:text-slate-400 cursor-pointer"
            v-model="form.hours"
            placeholder=""
            type="number"
            min="1"
          />
        </el-form-item>
      </div>

      <!-- 소요시간 작업 내역 설명 -->
      <el-form-item label="작업 내용" prop="description">
        <el-input
          class="w-full rounded-lg text-sm text-gray-700 bg-white rounded-[10px] outline-none transition focus:border-slate-400 focus:shadow-[0_0_0_3px_rgba(148,163,184,0.15)] disabled:bg-slate-100 disabled:text-slate-400 cursor-pointer"
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder=""
        />
      </el-form-item>
    </el-form>

    <!-- 푸터 버튼 -->
    <template #footer>
      <div class="modal-footer">
        <div></div>
        <div class="footer-right">
          <el-button class="btn-submit" @click="submitForm(ruleFormRef)">
            등록
          </el-button>
          <el-button class="btn-reset" @click="handleClose">취소</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { changeDate } from "../utils/commonFunc";
import TaskDatePicker from "../components/TaskDatePicker.vue";

const props = defineProps({
  timeRegisterUser: { type: Object, default: false },
});
const emit = defineEmits(["submitted"]);

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit("update:modelValue", v),
});

const form = ref({});

const rules = {
  hours: [
    { required: true, message: "작업시간을 입력해주세요", trigger: "blur" },
  ],
  description: [
    { required: true, message: "작업내용을 입력해주세요", trigger: "blur" },
  ],
};

// 취소, x버튼
const handleClose = () => {
  visible.value = false;
};

// 등록버튼
const submitForm = async (formEl) => {
  await formEl.validate((valid, fields) => {
    if (valid) {
      let obj = {
        hours: form.value.hours,
        taskDesc: form.value.description,
        workDate: form.value.workDate,
      };

      emit("submitted", obj);
    } else {
      // 안내 메세지 나옴
    }
  });
};

// 유효성 체크
const ruleFormRef = ref();

// props 변경내역 확인
watch(
  () => props.timeRegisterUser,
  (newVal) => {
    let date = new Date();
    let today = changeDate(date);

    form.value = { ...newVal, workDate: today, hours: "", description: "" };
  },
);
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
:deep(.el-input__wrapper) {
  padding-top: 5px;
  padding-bottom: 5px;
  border-radius: 10px;
}
:deep(.el-input.is-disabled .el-input__wrapper) {
  box-shadow: 0 0 0 0px var(--el-disabled-border-color);
}
:deep(.el-textarea__inner) {
  border-radius: 10px;
}
</style>
