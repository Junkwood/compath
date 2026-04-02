<template>
  <main class="bg-white dark:bg-slate-900">
    <div class="relative md:flex">
      <!-- 왼쪽 배경 패널 -->
      <div
        class="hidden md:flex md:w-1/2 bg-slate-800 flex-col justify-between p-10"
      >
        <!-- 로고 -->
        <div class="flex items-center gap-2">
          <div
            class="w-8 h-8 bg-indigo-500 rounded-lg flex items-center justify-center"
          >
            <svg
              class="w-5 h-5 text-white"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"
              />
            </svg>
          </div>
          <span class="text-white font-semibold text-lg tracking-tight"
            >ComPath</span
          >
        </div>

        <!-- 중앙 카피 -->
        <div>
          <h1 class="text-3xl font-bold text-white mb-4 leading-snug">
            팀의 모든 업무를<br />한 곳에서 관리하세요
          </h1>
          <p class="text-slate-400 text-sm leading-relaxed">
            진척률 추적, 마일스톤 관리, 팀 협업까지.<br />
            프로젝트 성공을 위한 모든 도구가 준비되어 있습니다.
          </p>
        </div>
        <div></div>
        <div></div>
        <div></div>
      </div>

      <!-- 오른쪽 로그인 폼 -->
      <div class="md:w-1/2 min-h-screen flex items-center justify-center p-6">
        <div class="w-full max-w-sm">
          <!-- 모바일 로고 -->
          <div class="flex items-center gap-2 mb-8 md:hidden">
            <div class="w-15 h-10 rounded-lg flex items-center justify-center">
              <img src="../images/comPath.png" alt="ComPath" />
            </div>
            <span class="text-slate-800 dark:text-white font-semibold text-lg"
              >ComPath</span
            >
          </div>

          <h2 class="text-2xl font-bold text-slate-800 dark:text-white mb-1">
            어서오세요 👋
          </h2>
          <p class="text-sm text-slate-500 dark:text-slate-400 mb-8">
            계정에 로그인하여 프로젝트를 관리하세요
          </p>

          <!-- 오류 메시지 -->
          <div
            v-if="errorMsg"
            class="mb-5 bg-rose-50 dark:bg-rose-900/20 border border-rose-200 dark:border-rose-700 rounded-lg px-4 py-3 flex items-center gap-2"
          >
            <svg
              class="w-4 h-4 text-rose-500 shrink-0"
              fill="currentColor"
              viewBox="0 0 20 20"
            >
              <path
                fill-rule="evenodd"
                d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
                clip-rule="evenodd"
              />
            </svg>
            <p class="text-sm text-rose-600 dark:text-rose-400">
              {{ errorMsg }}
            </p>
          </div>

          <!-- 폼 -->
          <form @submit.prevent="handleSubmit" class="space-y-4">
            <!-- 사번 -->
            <div>
              <label
                class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1.5"
                for="id"
              >
                사번
              </label>
              <input
                id="id"
                v-model="form.id"
                type="text"
                autocomplete="ID"
                placeholder="사번을 입력해주세요(ex:1000)"
                :class="[
                  'w-full px-3 py-2.5 rounded-lg border text-sm transition-colors',
                  'bg-white dark:bg-slate-800 text-slate-800 dark:text-white placeholder-slate-400',
                  fieldError.id
                    ? 'border-rose-400 focus:ring-2 focus:ring-rose-300 focus:outline-none'
                    : 'border-slate-300 dark:border-slate-600 focus:ring-2 focus:ring-indigo-400 focus:border-transparent focus:outline-none',
                ]"
              />
              <p v-if="fieldError.id" class="mt-1 text-xs text-rose-500">
                {{ fieldError.id }}
              </p>
            </div>

            <!-- 비밀번호 -->
            <div>
              <div class="flex items-center justify-between mb-1.5">
                <label
                  class="text-sm font-medium text-slate-700 dark:text-slate-300"
                  for="password"
                >
                  비밀번호
                </label>
                <router-link
                  to="/resetPassword"
                  class="text-xs text-indigo-500 hover:text-indigo-600 font-medium"
                >
                  비밀번호 재설정
                </router-link>
              </div>
              <div class="relative">
                <input
                  id="password"
                  v-model="form.password"
                  :type="showPassword ? 'text' : 'password'"
                  autocomplete="current-password"
                  placeholder="••••••••"
                  :class="[
                    'w-full px-3 py-2.5 pr-10 rounded-lg border text-sm transition-colors',
                    'bg-white dark:bg-slate-800 text-slate-800 dark:text-white placeholder-slate-400',
                    fieldError.password
                      ? 'border-rose-400 focus:ring-2 focus:ring-rose-300 focus:outline-none'
                      : 'border-slate-300 dark:border-slate-600 focus:ring-2 focus:ring-indigo-400 focus:border-transparent focus:outline-none',
                  ]"
                />
                <button
                  type="button"
                  class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-300"
                  @click="showPassword = !showPassword"
                >
                  <!-- 눈 아이콘 (보이기) -->
                  <svg
                    v-if="!showPassword"
                    class="w-4 h-4"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                    />
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
                    />
                  </svg>
                  <!-- 눈 닫힘 아이콘 (숨기기) -->
                  <svg
                    v-else
                    class="w-4 h-4"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"
                    />
                  </svg>
                </button>
              </div>
              <p v-if="fieldError.password" class="mt-1 text-xs text-rose-500">
                {{ fieldError.password }}
              </p>
            </div>

            <!-- 로그인 상태 유지 -->
            <div class="flex items-center gap-2">
              <input
                id="remember"
                v-model="form.remember"
                type="checkbox"
                class="w-4 h-4 rounded border-slate-300 text-indigo-500 focus:ring-indigo-400 cursor-pointer"
              />
              <label
                for="remember"
                class="text-sm text-slate-600 dark:text-slate-400 cursor-pointer select-none"
              >
                로그인 상태 유지
              </label>
            </div>

            <!-- 로그인 버튼 -->
            <button
              type="submit"
              :disabled="isLoading"
              class="w-full py-2.5 px-4 rounded-lg text-sm font-semibold text-white transition-all bg-indigo-500 hover:bg-indigo-600 active:scale-[0.98] disabled:opacity-60 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:ring-offset-2"
            >
              <span
                v-if="isLoading"
                class="flex items-center justify-center gap-2"
              >
                <svg
                  class="animate-spin w-4 h-4 text-white"
                  fill="none"
                  viewBox="0 0 24 24"
                >
                  <circle
                    class="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    stroke-width="4"
                  />
                  <path
                    class="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
                  />
                </svg>
                로그인 중...
              </span>
              <span v-else>로그인</span>
            </button>
          </form>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";
import api from "../utils/api";
const authStore = useAuthStore();
const router = useRouter();

const form = reactive({
  id: "",
  password: "",
  remember: false,
});

const fieldError = reactive({
  id: "",
  password: "",
});

const errorMsg = ref("");
const isLoading = ref(false);
const showPassword = ref(false);

function validate() {
  fieldError.id = "";
  fieldError.password = "";

  if (!form.id) {
    fieldError.id = "사번을 입력해주세요.";
  } else if (/[^0-9]/g.test(form.id) && /^0/.test(form.id)) {
    fieldError.id = "올바른 사번 형식이 아닙니다.";
  }

  if (!form.password) {
    fieldError.password = "비밀번호를 입력해주세요.";
  } /* else if (form.password.length < 6) {
    fieldError.password = '비밀번호는 6자 이상이어야 합니다.'
  } */

  return !fieldError.id && !fieldError.password;
}

async function handleSubmit() {
  errorMsg.value = "";

  if (!validate()) return;

  isLoading.value = true;

  try {
    const response = await api.post("/login", {
      userId: form.id, // 사번
      password: form.password,
    });

    if (!response.data) {
      errorMsg.value = "사번 또는 비밀번호가 올바르지 않습니다.";
      return;
    }

    authStore.login(response.data, form.remember);
    router.push("/");
  } catch (err) {
    errorMsg.value = "사번 또는 비밀번호가 올바르지 않습니다.";
  } finally {
    isLoading.value = false;
  }
}
</script>
