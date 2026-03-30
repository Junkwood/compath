<template>
  <el-dialog
    v-model="visible"
    title="메모 작성"
    width="560px"
    :close-on-click-modal="false"
    :append-to-body="true"
    @close="handleClose"
  >
    <div class="memo-subtitle">
      프로젝트 대시보드에 표시될 메모를 작성하세요.
    </div>

    <div class="memo-body">
      <el-input
        v-model="memoText"
        type="textarea"
        :rows="8"
        resize="none"
        maxlength="300"
        show-word-limit
        placeholder="메모를 입력하세요."
        class="memo-textarea"
      />
    </div>

    <template #footer>
      <div class="memo-footer">
        <el-button class="cancel-btn" @click="handleClose">취소</el-button>
        <el-button class="save-btn" @click="saveMemo">등록</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'submitted'])

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const memoText = ref('')

const handleClose = () => {
  visible.value = false
  memoText.value = ''
}

const saveMemo = () => {
  const text = memoText.value.trim()

  if (!text) return

  emit('submitted', {
    text
  })

  handleClose()
}
</script>

<style scoped>
.memo-subtitle {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 18px;
}

.memo-body {
  padding-top: 4px;
}

.memo-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 버튼 */
.cancel-btn {
  min-width: 84px;
  height: 38px;
  border-radius: 10px;
  border: 1px solid #dbe2ea;
  background: #ffffff;
  color: #475569;
}

.cancel-btn:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

.save-btn {
  min-width: 84px;
  height: 38px;
  border-radius: 10px;
  border: none;
  background: #3b82f6;
  color: #fff;
  font-weight: 600;
}

.save-btn:hover {
  background: #2563eb;
}

/* dialog */
:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.14);
}

:deep(.el-dialog__header) {
  margin: 0;
  padding: 24px 24px 14px;
  border-bottom: 1px solid #eef2f7;
}

:deep(.el-dialog__title) {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
}

:deep(.el-dialog__body) {
  padding: 20px 24px;
}

:deep(.el-dialog__footer) {
  padding: 16px 24px 24px;
}

:deep(.el-dialog__headerbtn) {
  top: 22px;
  right: 20px;
}

:deep(.el-dialog__close) {
  color: #94a3b8;
  font-size: 18px;
}

:deep(.el-dialog__close:hover) {
  color: #475569;
}

/* textarea */
.memo-textarea :deep(.el-textarea__inner) {
  min-height: 180px !important;
  border-radius: 12px;
  padding: 14px 16px;
  font-size: 14px;
  line-height: 1.6;
  color: #334155;
  box-shadow: none;
  border: 1px solid #dbe2ea;
}

.memo-textarea :deep(.el-textarea__inner:focus) {
  border-color: #93c5fd;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.08);
}

.memo-textarea :deep(.el-input__count) {
  color: #94a3b8;
  font-size: 12px;
  right: 12px;
  bottom: 8px;
}

@media (max-width: 640px) {
  :deep(.el-dialog) {
    width: calc(100vw - 24px) !important;
  }

  :deep(.el-dialog__header),
  :deep(.el-dialog__body),
  :deep(.el-dialog__footer) {
    padding-left: 16px;
    padding-right: 16px;
  }

  :deep(.el-dialog__title) {
    font-size: 18px;
  }

  .memo-footer {
    flex-direction: column;
  }

  .cancel-btn,
  .save-btn {
    width: 100%;
  }
}
</style>