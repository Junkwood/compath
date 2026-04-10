<template>
  <div class="flex h-screen overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden bg-gray-50 dark:bg-gray-900"
    >
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
          <div class="mb-6">
            <h1
              class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
            >
              마이페이지
            </h1>
          </div>

          <div class="mypage-layout">
            <div class="profile-card">
              <div class="profile-edit-btn-wrap">
                <button
                  class="btn bg-[#2563eb] hover:bg-blue-700 text-white text-xs font-semibold rounded-full px-4 py-1.5 border-none transition-colors"
                  @click="handleEditInfo"
                >
                  정보 수정
                </button>
              </div>

              <div class="avatar-wrap">
                <div class="avatar shadow-inner">
                  <svg
                    viewBox="0 0 24 24"
                    fill="#9ca3af"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      d="M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8v2.4h19.2v-2.4c0-3.2-6.4-4.8-9.6-4.8z"
                    />
                  </svg>
                </div>
              </div>

              <div class="profile-info">
                <div class="info-row">
                  <span class="info-label">아이디</span>
                  <span class="info-value font-medium text-blue-600">{{
                    empStore.user?.userId || "-"
                  }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">이름</span>
                  <span class="info-value">{{
                    empStore.user?.name || "-"
                  }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">이메일</span>
                  <span class="info-value">{{
                    empStore.user?.email || "-"
                  }}</span>
                </div>
                <div class="info-row">
                  <span class="info-label">기본 직군 그룹</span>
                  <el-tag
                    size="small"
                    effect="light"
                    round
                    type="info"
                    class="font-medium"
                  >
                    {{ empStore.user?.primaryGroupName || "-" }}
                  </el-tag>
                </div>
              </div>
            </div>

            <div class="right-col">
              <div class="summary-card">
                <div class="summary-title">내 작업 요약 (전체)</div>
                <div class="summary-grid">
                  <div class="summary-item">
                    <span class="summary-label">진행 중인 업무</span>
                    <span class="summary-value text-blue-600">{{
                      taskSummary.inProgress
                    }}</span>
                  </div>
                  <div class="summary-item">
                    <span class="summary-label">지연됨</span>
                    <span class="summary-value text-rose-500">{{
                      taskSummary.delayed
                    }}</span>
                  </div>
                  <div class="summary-item">
                    <span class="summary-label">마감 임박</span>
                    <span class="summary-value text-orange-500">{{
                      taskSummary.pendingPR
                    }}</span>
                  </div>
                  <div class="summary-item">
                    <span class="summary-label">완료 처리됨</span>
                    <span class="summary-value text-emerald-500">{{
                      taskSummary.done
                    }}</span>
                  </div>
                </div>
              </div>

              <div class="summary-card">
                <div class="summary-title">
                  나에게 할당된 진행 중인 업무 (마감 임박순)
                </div>

                <div v-if="isLoadingTasks" class="task-loading">
                  <el-skeleton :rows="4" animated />
                </div>

                <div
                  v-else-if="taskList.length === 0"
                  class="task-empty bg-gray-50 rounded-lg"
                >
                  진행 중인 업무가 없습니다.
                </div>

                <div v-else class="task-list">
                  <div
                    v-for="task in taskList"
                    :key="task.taskId"
                    @click="
                      goToTask(
                        task.parentProjectId,
                        task.projectId,
                        task.taskId,
                      )
                    "
                    class="hover:bg-gray-50 cursor-pointer transition-colors px-3 py-2 -mx-3 rounded-lg flex flex-col gap-2 border border-transparent hover:border-gray-200"
                  >
                    <div class="flex items-center gap-2 w-full">
                      <el-tag
                        size="small"
                        type="info"
                        effect="plain"
                        class="font-bold shrink-0"
                      >
                        #{{ task.taskId }}
                      </el-tag>
                      <span
                        class="flex-1 text-sm text-gray-800 truncate font-semibold"
                      >
                        {{ task.title }}
                      </span>
                      <span
                        :class="[
                          'text-[11px] font-bold shrink-0 px-2 py-1 rounded bg-gray-100',
                          dDayClass(task.dDay),
                        ]"
                      >
                        {{ formatDDay(task.dDay) }}
                      </span>
                    </div>

                    <div class="flex items-center gap-3 mt-0.5">
                      <span
                        :class="[
                          'flex items-center gap-1 text-[10px] font-bold px-1.5 py-0.5 rounded shrink-0',
                          priorityClass(task.priority),
                        ]"
                      >
                        <svg
                          class="w-2.5 h-2.5 fill-current"
                          viewBox="0 0 16 16"
                        >
                          <path
                            v-if="
                              task.priority === '긴급' || task.priority === '상'
                            "
                            d="M8 2a1 1 0 011 1v5a1 1 0 11-2 0V3a1 1 0 011-1zm0 9a1.5 1.5 0 110 3 1.5 1.5 0 010-3z"
                          />
                          <path
                            v-else-if="task.priority === '중'"
                            d="M3 8h10a1 1 0 110 2H3a1 1 0 110-2z"
                          />
                          <path
                            v-else
                            d="M8 14a1 1 0 01-1-1V8a1 1 0 112 0v5a1 1 0 01-1 1zm0-9a1.5 1.5 0 110-3 1.5 1.5 0 010 3z"
                          />
                        </svg>
                        {{ formatPriority(task.priority) }}
                      </span>

                      <span
                        class="text-[10px] text-gray-500 bg-gray-100 border border-gray-200 px-1.5 py-0.5 rounded font-bold shrink-0"
                      >
                        {{ task.typeName || "유형 미지정" }}
                      </span>

                      <div
                        class="flex-1 px-2 flex flex-col justify-center mt-0.5"
                      >
                        <div
                          class="w-full flex justify-between items-baseline mb-1"
                        >
                          <span
                            class="text-[9px] text-gray-400 font-medium tracking-tight"
                            >진행률</span
                          >
                          <span
                            class="text-[10px] font-bold"
                            :class="
                              task.progressRate === 100
                                ? 'text-emerald-500'
                                : 'text-blue-600'
                            "
                          >
                            {{ task.progressRate || 0 }}%
                          </span>
                        </div>

                        <el-progress
                          :percentage="task.progressRate || 0"
                          :stroke-width="5"
                          :show-text="false"
                          :color="
                            task.progressRate === 100 ? '#10b981' : '#3b82f6'
                          "
                        />
                      </div>

                      <span
                        class="flex items-center gap-1.5 text-[11px] text-gray-600 font-medium shrink-0 ml-auto"
                      >
                        <span
                          :class="[
                            'w-1.5 h-1.5 rounded-full',
                            statusDotClass(task.isFinal),
                          ]"
                        ></span>
                        {{ task.statusName || "상태 없음" }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>

  <el-dialog
    v-model="editModalVisible"
    title="내 정보 수정"
    width="440px"
    :close-on-click-modal="false"
    @close="handleEditModalClose"
  >
    <el-form
      ref="formRef"
      :model="editForm"
      :rules="editRules"
      label-width="110px"
      label-position="left"
      class="mt-2"
    >
      <el-form-item label="이메일 변경" prop="email">
        <el-input
          v-model="editForm.email"
          placeholder="새로운 이메일을 입력하세요"
        />
      </el-form-item>

      <div class="border-t border-gray-100 my-5 pt-5">
        <el-form-item label="현재 비밀번호" prop="currentPassword">
          <el-input
            v-model="editForm.currentPassword"
            type="password"
            placeholder="현재 비밀번호 (필수)"
            show-password
          />
        </el-form-item>

        <el-form-item label="새 비밀번호" prop="newPassword">
          <el-input
            v-model="editForm.newPassword"
            type="password"
            placeholder="변경할 비밀번호 (선택)"
            show-password
            @input="onNewPasswordInput"
          />
        </el-form-item>

        <el-form-item label="비밀번호 확인" prop="confirmPassword">
          <el-input
            v-model="editForm.confirmPassword"
            type="password"
            placeholder="새 비밀번호 재입력"
            show-password
          />
        </el-form-item>
      </div>
    </el-form>

    <template #footer>
      <div class="flex justify-end gap-2 mt-2">
        <button
          class="btn border border-gray-300 text-gray-600 hover:bg-gray-50 px-4 py-2 rounded-md text-sm"
          @click="handleEditModalClose"
        >
          취소
        </button>
        <button
          class="btn bg-[#2563eb] hover:bg-blue-700 text-white px-6 py-2 rounded-md text-sm font-semibold border-none"
          :class="{ 'opacity-50 cursor-not-allowed': submitting }"
          :disabled="submitting"
          @click="handleSubmitEdit"
        >
          {{ submitting ? "저장 중..." : "수정 완료" }}
        </button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from "vue";
import { useRouter } from "vue-router";
import Swal from "sweetalert2";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useAuthStore } from "../stores/auth.js";
import { useEmpStore } from "../stores/empSJW.js";
import api from "../utils/api.js";

const router = useRouter();
const sidebarOpen = ref(false);
const authStore = useAuthStore();
const empStore = useEmpStore();

const isLoadingTasks = ref(false);

// ── 작업 요약 상태 ──
const taskSummary = reactive({
  inProgress: 0,
  delayed: 0,
  pendingPR: 0,
  done: 0,
});

// ── 업무 목록 ──
const taskList = ref([]);

// ── D-Day 로직 ──
const formatDDay = (dDay) => {
  if (dDay === undefined || dDay === null) return "기한 없음";
  if (dDay === 0) return "D-Day";
  if (dDay > 0) return `D+${dDay}`;
  return `D${dDay}`; // 음수 표기 (지연)
};

const dDayClass = (dDay) => {
  if (dDay === undefined || dDay === null) return "text-gray-400";
  if (dDay === 0) return "text-rose-500 bg-rose-50";
  if (dDay < 0) return "text-rose-600 bg-rose-100"; // 지연됨
  if (dDay <= 3) return "text-orange-500 bg-orange-50"; // 임박
  return "text-blue-600 bg-blue-50"; // 여유
};

// ── 우선순위 로직 ──
const formatPriority = (priority) => priority || "하";
const priorityClass = (priority) => {
  if (priority === "긴급")
    return "text-rose-600 bg-rose-50 border border-rose-200";
  if (priority === "상")
    return "text-orange-600 bg-orange-50 border border-orange-200";
  if (priority === "중")
    return "text-blue-600 bg-blue-50 border border-blue-200";
  if (priority === "하")
    return "text-gray-600 bg-gray-50 border border-gray-200";
  return "text-gray-500 bg-gray-50 border border-gray-200";
};

// ── 상태 컬러 로직 (isFinal 기준) ──
const statusDotClass = (isFinal) => {
  return isFinal === "O1" ? "bg-emerald-500" : "bg-blue-500";
};

// ── 💡 상세 페이지 이동 로직 (상위/하위 프로젝트 구분) ──
const goToTask = (parentProjectId, projectId, taskId) => {
  if (!projectId || !taskId) {
    Swal.fire({
      icon: "error",
      title: "이동 불가",
      text: "업무 정보가 올바르지 않습니다.",
      confirmButtonColor: "#2563eb",
    });
    return;
  }

  if (parentProjectId) {
    // 💡 하위 프로젝트인 경우 (예: /project/1/sub/5/task/1024)
    router.push({
      name: "taskDetail",
      params: {
        projectId: parentProjectId,
        subProjectId: projectId,
        taskId: taskId,
      },
    });
  } else {
    // 💡 최상위 프로젝트인 경우 (예: /project/1/task/1028)
    router.push({
      name: "taskDetail",
      params: {
        projectId: projectId,
        taskId: taskId,
      },
    });
  }
};

// ── 정보 수정 모달 상태 ──
const editModalVisible = ref(false);
const submitting = ref(false);
const formRef = ref(null);

const defaultEditForm = () => ({
  email: empStore.user?.email || "",
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const editForm = reactive(defaultEditForm());

const validateConfirmPassword = (rule, value, callback) => {
  if (editForm.newPassword && value !== editForm.newPassword) {
    callback(new Error("새 비밀번호가 서로 일치하지 않습니다."));
  } else {
    callback();
  }
};

const editRules = reactive({
  email: [
    { required: true, message: "이메일을 입력하세요", trigger: "blur" },
    {
      pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
      message: "올바른 이메일 형식이 아닙니다.",
      trigger: "blur",
    },
  ],
  currentPassword: [
    {
      required: true,
      message: "정보 수정을 위해 현재 비밀번호를 입력하세요.",
      trigger: "blur",
    },
  ],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: "blur" }],
});

const onNewPasswordInput = () => {
  if (editForm.confirmPassword) {
    formRef.value?.validateField("confirmPassword");
  }
};

const handleEditInfo = () => {
  Object.assign(editForm, defaultEditForm());
  editModalVisible.value = true;
  nextTick(() => {
    formRef.value?.clearValidate();
  });
};

const handleEditModalClose = () => {
  editModalVisible.value = false;
};

// ── 정보 수정 제출 로직 ──
const handleSubmitEdit = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  submitting.value = true;
  try {
    await api.put(`/api/mypage/edit/${authStore.user.userId}`, {
      email: editForm.email,
      currentPassword: editForm.currentPassword,
      newPassword: editForm.newPassword || null,
    });

    if (empStore.user) {
      empStore.user.email = editForm.email;
    }

    editModalVisible.value = false;
    Swal.fire({
      toast: true,
      position: "top-end",
      icon: "success",
      title: "내 정보가 안전하게 수정되었습니다.",
      showConfirmButton: false,
      timer: 2000,
    });
  } catch (err) {
    const msg =
      err.response?.data?.message ||
      "수정에 실패했습니다. 현재 비밀번호를 확인해주세요.";
    Swal.fire({
      icon: "error",
      title: "정보 수정 실패",
      text: msg,
      confirmButtonColor: "#2563eb",
    });
  } finally {
    submitting.value = false;
  }
};

// ── 화면 로드 시 실행 ──
onMounted(async () => {
  try {
    if (authStore.user?.userId) {
      await empStore.getUser(authStore.user.userId);
    }

    isLoadingTasks.value = true;

    //  💡 실제 백엔드 연동 시 주석 해제
    const [summaryRes, listRes] = await Promise.all([
      api.get(`/mypage/taskSummary/${authStore.user.userId}`),
      api.get(`/mypage/taskList/${authStore.user.userId}`),
    ]);
    Object.assign(taskSummary, summaryRes.data);
    taskList.value = listRes.data;

    // // --- 더미 데이터 영역 (화면 테스트용) ---
    // taskSummary.inProgress = 5;
    // taskSummary.delayed = 1;
    // taskSummary.pendingPR = 2; // 마감 임박으로 사용
    // taskSummary.done = 18;

    // taskList.value = [
    //   {
    //     taskId: 1024,
    //     projectId: 1,
    //     title: "메인 대시보드 UI 퍼블리싱",
    //     dDay: -2,
    //     priority: "긴급",
    //     typeName: "프론트엔드",
    //     statusName: "이슈 수정 중",
    //     isFinal: "O2",
    //     progressRate: 40,
    //   },
    //   {
    //     taskId: 1028,
    //     projectId: 1,
    //     title: "로그인 시큐리티 필터 버그 픽스",
    //     dDay: 0,
    //     priority: "상",
    //     typeName: "버그 픽스",
    //     statusName: "코드 리뷰 중",
    //     isFinal: "O2",
    //     progressRate: 85,
    //   },
    //   {
    //     taskId: 1033,
    //     projectId: 2,
    //     title: "AWS 배포 파이프라인 구축",
    //     dDay: 2,
    //     priority: "중",
    //     typeName: "데브옵스",
    //     statusName: "인프라 세팅",
    //     isFinal: "O2",
    //     progressRate: 20,
    //   },
    //   {
    //     taskId: 1041,
    //     projectId: 3,
    //     title: "마이페이지 비밀번호 암호화 연동",
    //     dDay: null,
    //     priority: "하",
    //     typeName: "백엔드",
    //     statusName: "개발 중",
    //     isFinal: "O2",
    //     progressRate: 50,
    //   },
    // ];
    // ------------------------------------------
  } catch (err) {
    console.error("마이페이지 데이터 로드 에러:", err);
  } finally {
    isLoadingTasks.value = false;
  }
});
</script>

<style scoped>
/* ── 레이아웃 ── */
.mypage-layout {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 24px;
  align-items: start;
}
@media (max-width: 1024px) {
  .mypage-layout {
    grid-template-columns: 1fr;
  }
}

/* ── 프로필 카드 ── */
.profile-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  padding: 24px 28px 28px;
  position: relative;
}
.profile-edit-btn-wrap {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
}

.avatar-wrap {
  display: flex;
  justify-content: center;
  margin-bottom: 32px;
}
.avatar {
  width: 110px;
  height: 110px;
  border-radius: 50%;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border: 4px solid #fff;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}
.avatar svg {
  width: 70px;
  height: 70px;
  margin-top: 10px;
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #f1f5f9;
  padding-bottom: 12px;
}
.info-row:last-child {
  border-bottom: none;
  padding-bottom: 0;
}
.info-label {
  font-size: 13px;
  color: #6b7280;
  font-weight: 500;
}
.info-value {
  font-size: 14px;
  color: #1f2937;
}

/* ── 우측 컬럼 ── */
.right-col {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* ── 요약 카드 ── */
.summary-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  padding: 24px;
}
.summary-title {
  font-size: 16px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
}
.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.summary-item {
  background: #f8fafc;
  border: 1px solid #f1f5f9;
  border-radius: 10px;
  padding: 20px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  transition: transform 0.2s;
}
.summary-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}
.summary-label {
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
}
.summary-value {
  font-size: 32px;
  font-weight: 800;
}

/* ── 업무 목록 ── */
.task-loading {
  padding: 16px 0;
}
.task-empty {
  text-align: center;
  font-size: 14px;
  color: #9ca3af;
  padding: 40px 0;
}
.task-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-height: 380px;

  /* 💡 세로 스크롤은 유지하고, 가로 스크롤은 강제로 숨깁니다! */
  overflow-y: auto;
  overflow-x: hidden;

  padding-right: 4px;
}
/* 💡 혹시라도 내부 요소가 카드를 뚫고 나가지 않도록 방어하는 CSS 추가 */
.task-row {
  /* 내부 요소들이 지정된 너비를 넘어가지 못하게 막습니다 */
  min-width: 0;
}

/* 💡 제목이 길어지면 줄바꿈하지 않고 말줄임표(...) 처리 (이미 HTML에 truncate 클래스가 있지만 확실히 보장하기 위해) */
.task-name {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0; /* flex 자식 요소가 부모 밖으로 밀려나지 않게 하는 마법의 속성 */
}
/* ── 커스텀 스크롤바 (가로 스크롤 방지를 위해 height도 명시) ── */
.task-list::-webkit-scrollbar {
  width: 6px;
  height: 0px; /* 💡 가로 스크롤바 자체의 두께를 0으로 만들어 완전히 숨김 */
}
.task-list::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}
.task-list::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}
.task-list::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
/* ── 모달 & 공통 요소 ── */
:deep(.el-dialog__title) {
  font-weight: 700;
  font-size: 16px;
  color: #1f2937;
}
:deep(.el-form-item__label) {
  font-size: 13px;
  font-weight: 600;
  color: #4b5563;
}
:global(.swal2-container) {
  z-index: 9999 !important;
}
</style>
