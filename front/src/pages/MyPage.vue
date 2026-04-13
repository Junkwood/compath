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
            <!-- ===================== LEFT: 프로필 카드 ===================== -->
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

            <!-- ===================== RIGHT: 일반 사용자 ===================== -->
            <div v-if="!authStore.isAdmin" class="right-col">
              <!-- 내 작업 요약 -->
              <div class="summary-card">
                <div class="summary-title">내 작업 요약 (전체)</div>
                <div class="summary-grid">
                  <div class="summary-item">
                    <span class="summary-label">진행 중인 업무</span>
                    <span class="summary-value text-blue-600">{{
                      myPageStore.taskSummary.inProgress
                    }}</span>
                  </div>
                  <div class="summary-item">
                    <span class="summary-label">지연됨</span>
                    <span class="summary-value text-rose-500">{{
                      myPageStore.taskSummary.delayed
                    }}</span>
                  </div>
                  <div class="summary-item">
                    <span class="summary-label">마감 임박</span>
                    <span class="summary-value text-orange-500">{{
                      myPageStore.taskSummary.pendingPR
                    }}</span>
                  </div>
                  <div class="summary-item">
                    <span class="summary-label">완료 처리됨</span>
                    <span class="summary-value text-emerald-500">{{
                      myPageStore.taskSummary.done
                    }}</span>
                  </div>
                </div>
              </div>

              <!-- 업무 목록 -->
              <div class="summary-card">
                <div class="summary-title">
                  나에게 할당된 진행 중인 업무 (마감 임박순)
                </div>

                <div v-if="isLoadingTasks" class="task-loading">
                  <el-skeleton :rows="4" animated />
                </div>

                <div
                  v-else-if="myPageStore.taskList === 0"
                  class="task-empty bg-gray-50 rounded-lg"
                >
                  진행 중인 업무가 없습니다.
                </div>

                <div v-else class="task-list">
                  <div
                    v-for="task in myPageStore.taskList"
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

            <!-- ===================== RIGHT: 관리자 ===================== -->
            <div v-else class="right-col">
              <div class="summary-card">
                <div class="summary-title">시스템 요약 현황</div>
                <div class="summary-grid summary-grid-2">
                  <div
                    class="summary-item"
                    @click="goToUsers()"
                    style="cursor: pointer"
                  >
                    <span class="summary-label">전체 가입자</span>
                    <span class="summary-value text-violet-600"
                      >{{ myPageStore.adminSummary.totalUsers || 0 }}명</span
                    >
                    <div class="flex items-center gap-2 mt-1">
                      <span
                        class="text-[11px] font-bold text-emerald-600 bg-emerald-50 border border-emerald-100 px-2 py-0.5 rounded"
                      >
                        활성 {{ myPageStore.adminSummary.activeUsers || 0 }}
                      </span>
                      <span
                        class="text-[11px] font-bold text-rose-600 bg-rose-50 border border-rose-100 px-2 py-0.5 rounded"
                      >
                        비활성 {{ myPageStore.adminSummary.inactiveUsers || 0 }}
                      </span>
                    </div>
                  </div>

                  <div
                    class="summary-item justify-center"
                    @click="goToProjects()"
                    style="cursor: pointer"
                  >
                    <span class="summary-label">진행중 프로젝트</span>
                    <span class="summary-value text-violet-600"
                      >{{
                        myPageStore.adminSummary.activeProjects || 0
                      }}개</span
                    >
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>

  <!-- 정보 수정 모달 -->
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
        <!-- 새 비밀번호 -->
        <el-form-item label="새 비밀번호" prop="newPassword">
          <div class="relative w-full">
            <el-input
              v-model="editForm.newPassword"
              :type="showPassword.new ? 'text' : 'password'"
              placeholder="변경할 비밀번호 (선택, 6자 이상)"
              @input="onNewPasswordInput"
            >
              <template #suffix>
                <el-icon
                  class="cursor-pointer"
                  @click="showPassword.new = !showPassword.new"
                >
                  <View v-if="!showPassword.new" />
                  <Hide v-else />
                </el-icon>
              </template>
            </el-input>
          </div>

          <!-- 비밀번호 강도 -->
          <div v-if="editForm.newPassword" style="margin-top: 6px; width: 100%">
            <div style="display: flex; gap: 4px; margin-bottom: 4px">
              <div
                v-for="n in 4"
                :key="n"
                :style="{
                  flex: 1,
                  height: '4px',
                  borderRadius: '4px',
                  transition: 'background 0.2s',
                  background:
                    passwordStrength >= n ? strengthColorHex : '#e2e8f0',
                }"
              />
            </div>
            <p :style="{ fontSize: '12px', color: strengthTextHex }">
              {{ strengthLabel }}
            </p>
          </div>
        </el-form-item>

        <!-- 비밀번호 확인 -->
        <el-form-item label="비밀번호 확인" prop="confirmPassword">
          <el-input
            v-model="editForm.confirmPassword"
            :type="showPassword.confirm ? 'text' : 'password'"
            placeholder="새 비밀번호 재입력"
            :class="confirmInputClass"
            @input="onNewPasswordInput"
          >
            <template #suffix>
              <el-icon
                class="cursor-pointer"
                @click="showPassword.confirm = !showPassword.confirm"
              >
                <View v-if="!showPassword.confirm" />
                <Hide v-else />
              </el-icon>
            </template>
          </el-input>
          <!-- 일치 여부 메시지 -->
          <p
            v-if="
              editForm.confirmPassword &&
              editForm.newPassword === editForm.confirmPassword
            "
            style="font-size: 12px; margin-top: 4px; color: #10b981"
          >
            비밀번호가 일치합니다.
          </p>
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
import { ref, reactive, onMounted, nextTick, computed } from "vue";
import { useRouter } from "vue-router";
import Swal from "sweetalert2";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useAuthStore } from "../stores/auth.js";
import { useEmpStore } from "../stores/empSJW.js";
import { userMypageStore } from "../stores/myPageSJW.js";
import api from "../utils/api.js";
import { View, Hide } from "@element-plus/icons-vue";

const router = useRouter();
const sidebarOpen = ref(false);
const authStore = useAuthStore();
const empStore = useEmpStore();
const myPageStore = userMypageStore();
const isLoadingTasks = ref(false);

// ── D-Day 로직 ──
const formatDDay = (dDay) => {
  if (dDay === undefined || dDay === null) return "기한 없음";
  if (dDay === 0) return "D-Day";
  if (dDay > 0) return `D+${dDay}`;
  return `D${dDay}`;
};

const dDayClass = (dDay) => {
  if (dDay === undefined || dDay === null) return "text-gray-400";
  if (dDay === 0) return "text-rose-500 bg-rose-50";
  if (dDay < 0) return "text-rose-600 bg-rose-100";
  if (dDay <= 3) return "text-orange-500 bg-orange-50";
  return "text-blue-600 bg-blue-50";
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

// ── 상태 dot 색상 ──
const statusDotClass = (isFinal) =>
  isFinal === "O1" ? "bg-emerald-500" : "bg-blue-500";

// ── 업무 상세 이동 ──
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
    router.push({
      name: "taskDetail",
      params: { projectId: parentProjectId, subProjectId: projectId, taskId },
    });
  } else {
    router.push({ name: "taskDetail", params: { projectId, taskId } });
  }
};
const goToUsers = () => {
  router.push({
    name: "emp",
  });
};
const goToProjects = () => {
  router.push({ name: "projectListAll" });
};
// ── 화면 로드 ──
onMounted(async () => {
  try {
    if (authStore.user?.userId) {
      await empStore.getUser(authStore.user.userId);
    }

    if (authStore.isAdmin) {
      // ✅ 관리자용
      myPageStore.getAdminSummary();
    } else {
      // ✅ 일반 사용자용
      isLoadingTasks.value = true;
      myPageStore.getTaskList(authStore.user.userId);
      myPageStore.getTaskSummary(authStore.user.userId);
    }
  } catch (err) {
    console.error("마이페이지 데이터 로드 에러:", err);
  } finally {
    isLoadingTasks.value = false;
  }
});

// ── 정보 수정 모달 ──
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
// showPassword 추가
const showPassword = reactive({ new: false, confirm: false });
// ── 비밀번호 강도 ──
const passwordStrength = computed(() => {
  const p = editForm.newPassword;
  let score = 0;
  if (p.length >= 6) score++;
  if (/[A-Z]/.test(p)) score++;
  if (/[0-9]/.test(p)) score++;
  if (/[^A-Za-z0-9]/.test(p)) score++;
  return score;
});

const strengthColorHex = computed(
  () =>
    ["#f87171", "#fb923c", "#facc15", "#34d399"][passwordStrength.value - 1] ||
    "#e2e8f0",
);
const strengthTextHex = computed(
  () =>
    ["#ef4444", "#f97316", "#eab308", "#10b981"][passwordStrength.value - 1] ||
    "",
);
const strengthLabel = computed(
  () => ["매우 약함", "약함", "보통", "강함"][passwordStrength.value - 1] || "",
);

// 비밀번호 확인 input 테두리 색상
const confirmInputClass = computed(() => {
  if (!editForm.confirmPassword) return "";
  return editForm.newPassword === editForm.confirmPassword
    ? "confirm-match"
    : "confirm-mismatch";
});
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
  newPassword: [
    {
      validator: (rule, value, callback) => {
        if (value && value.length < 6) {
          callback(new Error("비밀번호는 6자 이상이어야 합니다."));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  confirmPassword: [
    {
      validator: (rule, value, callback) => {
        // 새 비밀번호를 입력했을 때만 일치 확인
        if (editForm.newPassword && value !== editForm.newPassword) {
          callback(new Error("비밀번호가 일치하지 않습니다."));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: "blur" }],
});

const onNewPasswordInput = () => {
  if (editForm.confirmPassword) formRef.value?.validateField("confirmPassword");
};

const handleEditInfo = () => {
  Object.assign(editForm, defaultEditForm());
  editModalVisible.value = true;
  nextTick(() => formRef.value?.clearValidate());
};

const handleEditModalClose = () => {
  editModalVisible.value = false;
};

const handleSubmitEdit = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;
  // 새 비밀번호 입력했는데 6자 미만이면 차단
  if (editForm.newPassword && editForm.newPassword.length < 6) {
    Swal.fire({
      icon: "warning",
      title: "비밀번호는 6자 이상이어야 합니다.",
      confirmButtonColor: "#2563eb",
    });
    return;
  }
  submitting.value = true;
  try {
    const res = await api.put(`/mypage/modify/${authStore.user.userId}`, {
      email: editForm.email,
      currentPassword: editForm.currentPassword,
      newPassword: editForm.newPassword || null,
    });
    if (res.data) {
      if (empStore.user) empStore.user.email = editForm.email;
    } else {
      throw new Exception();
    }

    editModalVisible.value = false;
    Swal.fire({
      toast: true,
      position: "top-end",
      icon: "success",
      title: "내 정보가 수정되었습니다.",
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
.summary-grid-2 {
  grid-template-columns: repeat(2, 1fr);
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
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 4px;
}
.task-row {
  min-width: 0;
}
.task-name {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
}
.task-list::-webkit-scrollbar {
  width: 6px;
  height: 0px;
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

/* ── 모달 ── */
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
:deep(.confirm-match .el-input__wrapper) {
  box-shadow: 0 0 0 1px #10b981 inset !important;
}
:deep(.confirm-mismatch .el-input__wrapper) {
  box-shadow: 0 0 0 1px #ef4444 inset !important;
}
</style>
