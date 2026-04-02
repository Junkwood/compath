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
      <el-form-item label="업무명" prop="taskTitle">
        <el-input v-model="form.taskTitle" placeholder="" disabled />
      </el-form-item>

      <!-- 프로젝트 식별자 + 총괄PL -->
      <el-form-item label="작업 일자" prop="workDate">
        <div class="row-fields">
          <el-date-picker
            v-model="form.workDate"
            type="date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="flex: 1"
            disabled
          />
          <div class="pl-field">
            <span class="pl-label">작업시간</span>
            <el-input
              v-model="form.hours"
              placeholder=""
              style="width: 140px"
              prop="hours"
            />
          </div>
        </div>
      </el-form-item>

      <!-- 프로젝트 설명 -->
      <el-form-item label="작업 내용" prop="description">
        <el-input
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
          <el-button class="btn-reset" @click="handleReset">취소</el-button>
          <el-button
            class="btn-submit"
            :loading="submitting"
            @click="handleSubmit"
          >
            등록
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from "vue";

const props = defineProps({
  timeRegisterUser: { type: Object, default: false },
});
const emit = defineEmits(["update:modelValue", "submitted"]);

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit("update:modelValue", v),
});

const form = ref({});
const submitting = ref(false);

const rules = {
  hours: [{ required: true, message: "총괄PL을 선택하세요", trigger: "blur" }],
  description: [
    { required: true, message: "작업내용을 입력해주세요", trigger: "blur" },
  ],
};

const handleClose = () => {
  visible.value = false;
};

// props 변경내역 확인
watch(
  () => props.timeRegisterUser,
  (newVal) => {
    let date = new Date();
    let today = `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;

    if (date.getMonth() < 9) {
      today = `${date.getFullYear()}-0${date.getMonth() + 1}-${date.getDate()}`;
      if (date.getDate() < 10) {
        today = `${date.getFullYear()}-0${date.getMonth() + 1}-0${date.getDate()}`;
        console.log(today);
      }
    } else {
      if (date.getDate() < 10) {
        today = `${date.getFullYear()}-${date.getMonth() + 1}-0${date.getDate()}`;
      }
    }

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
</style>
