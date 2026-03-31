<template>
  <main class="bg-white dark:bg-slate-900">
    <div class="relative md:flex">
      <!-- 왼쪽 배경 패널 -->
      <div
        class="hidden md:flex md:w-1/2 bg-slate-800 flex-col justify-between p-10"
      >
        <!-- 로고 -->
        <div class="flex items-center gap-2">
          <div class="w-15 h-10 rounded-lg flex items-center justify-center">
            <img src="../images/comPath.png" alt="ComPath" />
          </div>
          <span class="text-white font-semibold text-lg tracking-tight"
            >ComPath</span
          >
        </div>

        <!-- 중앙 카피 -->
        <div>
          <h1 class="text-3xl font-bold text-white mb-4 leading-snug">
            비밀번호를<br />재설정할게요
          </h1>
          <p class="text-slate-400 text-sm leading-relaxed">
            가입하신 이메일로 인증번호를 보내드립니다.<br />
            인증번호 확인 후 새 비밀번호를 설정하세요.
          </p>

          <!-- 단계 안내 -->
          <div class="mt-10 space-y-4">
            <div
              v-for="(step, i) in steps"
              :key="i"
              class="flex items-start gap-3"
            >
              <div
                :class="[
                  'w-7 h-7 rounded-full flex items-center justify-center shrink-0 mt-0.5',
                  currentStep > i
                    ? 'bg-indigo-400'
                    : currentStep === i
                      ? 'bg-indigo-500'
                      : 'bg-slate-600',
                ]"
              >
                <svg
                  v-if="currentStep > i"
                  class="w-3.5 h-3.5 text-white"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="3"
                    d="M5 13l4 4L19 7"
                  />
                </svg>
                <span
                  v-else
                  :class="[
                    'text-xs font-semibold',
                    currentStep === i ? 'text-white' : 'text-slate-300',
                  ]"
                >
                  {{ i + 1 }}
                </span>
              </div>
              <div>
                <p
                  :class="[
                    'text-sm font-medium',
                    currentStep >= i ? 'text-white' : 'text-slate-400',
                  ]"
                >
                  {{ step.title }}
                </p>
                <p class="text-slate-500 text-xs mt-0.5">{{ step.desc }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 하단 보안 안내 -->
        <div class="bg-slate-700/50 rounded-xl p-5 border border-slate-600/50">
          <div class="flex items-center gap-2 mb-2">
            <svg
              class="w-4 h-4 text-indigo-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
              />
            </svg>
            <p class="text-white text-sm font-medium">보안 안내</p>
          </div>
          <p class="text-slate-400 text-xs leading-relaxed">
            인증번호는 발송 후 5분간만 유효합니다. ComPath는 이메일로 비밀번호를
            직접 요청하지 않습니다.
          </p>
        </div>
      </div>

      <!-- 오른쪽 폼 영역 -->
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

          <!-- 공통 에러 메시지 -->
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

          <!-- ── Step 0: 사번 + 이메일 입력 ── -->
          <div v-if="currentStep === 0">
            <h2 class="text-2xl font-bold text-slate-800 dark:text-white mb-1">
              비밀번호 재설정
            </h2>
            <p class="text-sm text-slate-500 dark:text-slate-400 mb-8">
              사번과 가입 시 사용한 이메일을 입력하시면 인증번호를 보내드립니다.
            </p>

            <form @submit.prevent="handleSendCode" class="space-y-4">
              <div>
                <label
                  class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1.5"
                  for="userId"
                  >사번</label
                >
                <input
                  id="userId"
                  v-model="form.userId"
                  type="text"
                  autocomplete="username"
                  placeholder="사번을 입력하세요"
                  :class="inputClass(fieldError.userId)"
                />
                <p v-if="fieldError.userId" class="mt-1 text-xs text-rose-500">
                  {{ fieldError.userId }}
                </p>
              </div>

              <div>
                <label
                  class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1.5"
                  for="email"
                  >이메일</label
                >
                <input
                  id="email"
                  v-model="form.email"
                  type="email"
                  autocomplete="email"
                  placeholder="your@company.com"
                  :class="inputClass(fieldError.email)"
                />
                <p v-if="fieldError.email" class="mt-1 text-xs text-rose-500">
                  {{ fieldError.email }}
                </p>
              </div>

              <button type="submit" :disabled="isLoading" :class="btnPrimary">
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
                  전송 중...
                </span>
                <span v-else>인증번호 받기</span>
              </button>
            </form>

            <div class="mt-6 text-center">
              <router-link
                to="/login"
                class="inline-flex items-center gap-1.5 text-sm text-indigo-500 hover:text-indigo-600 font-medium"
              >
                <svg
                  class="w-4 h-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M15 19l-7-7 7-7"
                  />
                </svg>
                로그인으로 돌아가기
              </router-link>
            </div>
          </div>

          <!-- ── Step 1: 인증번호 입력 ── -->
          <div v-else-if="currentStep === 1">
            <h2 class="text-2xl font-bold text-slate-800 dark:text-white mb-1">
              인증번호 입력
            </h2>
            <p class="text-sm text-slate-500 dark:text-slate-400 mb-8">
              <span class="font-medium text-slate-700 dark:text-slate-300">{{
                form.email
              }}</span
              >으로 발송된 6자리 인증번호를 입력하세요.
            </p>

            <form @submit.prevent="handleVerifyCode" class="space-y-4">
              <div>
                <label
                  class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-3"
                  >인증번호</label
                >
                <div class="flex gap-2">
                  <input
                    v-for="(_, i) in codeDigits"
                    :key="i"
                    :ref="(el) => (codeRefs[i] = el)"
                    v-model="codeDigits[i]"
                    type="text"
                    inputmode="numeric"
                    maxlength="1"
                    @input="onCodeInput(i, $event)"
                    @keydown="onCodeKeydown(i, $event)"
                    @paste="onCodePaste($event)"
                    :class="[
                      'w-full aspect-square text-center text-lg font-semibold rounded-lg border transition-colors',
                      'bg-white dark:bg-slate-800 text-slate-800 dark:text-white',
                      hasCodeError
                        ? 'border-rose-400 focus:ring-2 focus:ring-rose-300 focus:outline-none'
                        : 'border-slate-300 dark:border-slate-600 focus:ring-2 focus:ring-indigo-400 focus:border-transparent focus:outline-none',
                    ]"
                  />
                </div>
                <p v-if="hasCodeError" class="mt-2 text-xs text-rose-500">
                  올바른 인증번호를 입력해주세요.
                </p>
              </div>

              <!-- 타이머 + 재전송 -->
              <div class="flex items-center justify-between text-xs">
                <span
                  :class="[
                    'font-medium tabular-nums',
                    codeTimer <= 60
                      ? 'text-rose-500'
                      : 'text-slate-500 dark:text-slate-400',
                  ]"
                >
                  남은 시간 {{ formattedTimer }}
                </span>
                <button
                  type="button"
                  :disabled="resendCooldown > 0"
                  @click="handleResend"
                  class="text-indigo-500 hover:text-indigo-600 font-medium disabled:text-slate-400 disabled:cursor-not-allowed transition-colors"
                >
                  {{
                    resendCooldown > 0
                      ? `${resendCooldown}초 후 재전송`
                      : "인증번호 재전송"
                  }}
                </button>
              </div>

              <button
                type="submit"
                :disabled="isLoading || codeDigits.join('').length < 6"
                :class="btnPrimary"
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
                  확인 중...
                </span>
                <span v-else>인증번호 확인</span>
              </button>
            </form>

            <div class="mt-6 text-center">
              <button
                @click="
                  currentStep = 0;
                  errorMsg = '';
                "
                class="inline-flex items-center gap-1.5 text-sm text-indigo-500 hover:text-indigo-600 font-medium"
              >
                <svg
                  class="w-4 h-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M15 19l-7-7 7-7"
                  />
                </svg>
                이메일 다시 입력
              </button>
            </div>
          </div>

          <!-- ── Step 2: 새 비밀번호 입력 ── -->
          <div v-else-if="currentStep === 2">
            <h2 class="text-2xl font-bold text-slate-800 dark:text-white mb-1">
              새 비밀번호 설정
            </h2>
            <p class="text-sm text-slate-500 dark:text-slate-400 mb-8">
              새로 사용할 비밀번호를 입력해주세요.
            </p>

            <form @submit.prevent="handleResetPassword" class="space-y-4">
              <!-- 새 비밀번호 -->
              <div>
                <label
                  class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1.5"
                  for="newPassword"
                  >새 비밀번호</label
                >
                <div class="relative">
                  <input
                    id="newPassword"
                    v-model="form.newPassword"
                    :type="showPassword.new ? 'text' : 'password'"
                    autocomplete="new-password"
                    placeholder="8자 이상 입력"
                    :class="inputClass(fieldError.newPassword, 'pr-10')"
                  />
                  <button
                    type="button"
                    class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-300"
                    @click="showPassword.new = !showPassword.new"
                  >
                    <svg
                      v-if="!showPassword.new"
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
                <p
                  v-if="fieldError.newPassword"
                  class="mt-1 text-xs text-rose-500"
                >
                  {{ fieldError.newPassword }}
                </p>

                <!-- 비밀번호 강도 -->
                <div v-if="form.newPassword" class="mt-2">
                  <div class="flex gap-1 mb-1">
                    <div
                      v-for="n in 4"
                      :key="n"
                      :class="[
                        'h-1 flex-1 rounded-full transition-colors',
                        passwordStrength >= n
                          ? strengthColor
                          : 'bg-slate-200 dark:bg-slate-700',
                      ]"
                    ></div>
                  </div>
                  <p :class="['text-xs', strengthTextColor]">
                    {{ strengthLabel }}
                  </p>
                </div>
              </div>

              <!-- 비밀번호 확인 -->
              <div>
                <label
                  class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1.5"
                  for="confirmPassword"
                  >비밀번호 확인</label
                >
                <div class="relative">
                  <input
                    id="confirmPassword"
                    v-model="form.confirmPassword"
                    :type="showPassword.confirm ? 'text' : 'password'"
                    autocomplete="new-password"
                    placeholder="비밀번호를 다시 입력하세요"
                    :class="[
                      'w-full px-3 py-2.5 pr-10 rounded-lg border text-sm transition-colors',
                      'bg-white dark:bg-slate-800 text-slate-800 dark:text-white placeholder-slate-400',
                      fieldError.confirmPassword
                        ? 'border-rose-400 focus:ring-2 focus:ring-rose-300 focus:outline-none'
                        : form.confirmPassword &&
                            form.newPassword === form.confirmPassword
                          ? 'border-emerald-400 focus:ring-2 focus:ring-emerald-300 focus:outline-none'
                          : 'border-slate-300 dark:border-slate-600 focus:ring-2 focus:ring-indigo-400 focus:border-transparent focus:outline-none',
                    ]"
                  />
                  <button
                    type="button"
                    class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-300"
                    @click="showPassword.confirm = !showPassword.confirm"
                  >
                    <svg
                      v-if="!showPassword.confirm"
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
                <p
                  v-if="fieldError.confirmPassword"
                  class="mt-1 text-xs text-rose-500"
                >
                  {{ fieldError.confirmPassword }}
                </p>
                <p
                  v-else-if="
                    form.confirmPassword &&
                    form.newPassword === form.confirmPassword
                  "
                  class="mt-1 text-xs text-emerald-500"
                >
                  비밀번호가 일치합니다.
                </p>
              </div>

              <button type="submit" :disabled="isLoading" :class="btnPrimary">
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
                  변경 중...
                </span>
                <span v-else>비밀번호 변경</span>
              </button>
            </form>
          </div>

          <!-- ── Step 3: 완료 ── -->
          <div v-else class="text-center">
            <div
              class="flex items-center justify-center w-16 h-16 bg-emerald-50 dark:bg-emerald-900/30 rounded-full mb-6 mx-auto"
            >
              <svg
                class="w-8 h-8 text-emerald-500"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M5 13l4 4L19 7"
                />
              </svg>
            </div>
            <h2 class="text-2xl font-bold text-slate-800 dark:text-white mb-2">
              변경 완료!
            </h2>
            <p class="text-sm text-slate-500 dark:text-slate-400 mb-8">
              비밀번호가 성공적으로 변경되었습니다.<br />새 비밀번호로
              로그인해주세요.
            </p>
            <router-link
              to="/login"
              class="w-full inline-block py-2.5 px-4 rounded-lg text-sm font-semibold text-white text-center bg-indigo-500 hover:bg-indigo-600 active:scale-[0.98] transition-all focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:ring-offset-2"
            >
              로그인하러 가기
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, reactive, computed, onUnmounted } from "vue";
import axios from "axios";

const currentStep = ref(0);
const isLoading = ref(false);
const errorMsg = ref("");
const hasCodeError = ref(false);
const resendCooldown = ref(0);
const emailId = ref(0);
const form = reactive({
  userId: "",
  email: "",
  newPassword: "",
  confirmPassword: "",
});

const fieldError = reactive({
  userId: "",
  email: "",
  newPassword: "",
  confirmPassword: "",
});

const showPassword = reactive({ new: false, confirm: false });

// ── 공통 스타일 헬퍼 ──
const btnPrimary =
  "w-full py-2.5 px-4 rounded-lg text-sm font-semibold text-white transition-all " +
  "bg-indigo-500 hover:bg-indigo-600 active:scale-[0.98] " +
  "disabled:opacity-60 disabled:cursor-not-allowed " +
  "focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:ring-offset-2";

function inputClass(hasError, extra = "") {
  const base =
    "w-full px-3 py-2.5 rounded-lg border text-sm transition-colors " +
    "bg-white dark:bg-slate-800 text-slate-800 dark:text-white placeholder-slate-400 ";
  const err =
    "border-rose-400 focus:ring-2 focus:ring-rose-300 focus:outline-none";
  const normal =
    "border-slate-300 dark:border-slate-600 focus:ring-2 focus:ring-indigo-400 focus:border-transparent focus:outline-none";
  return base + (extra ? extra + " " : "") + (hasError ? err : normal);
}

// ── 인증번호 6칸 ──
const codeDigits = reactive(["", "", "", "", "", ""]);
const codeRefs = ref([]);

function onCodeInput(i, e) {
  const val = e.target.value.replace(/\D/g, "");
  codeDigits[i] = val.slice(-1);
  hasCodeError.value = false;
  if (val && i < 5) codeRefs.value[i + 1]?.focus();
}

function onCodeKeydown(i, e) {
  if (e.key === "Backspace" && !codeDigits[i] && i > 0) {
    codeRefs.value[i - 1]?.focus();
  }
}

function onCodePaste(e) {
  const text = e.clipboardData.getData("text").replace(/\D/g, "").slice(0, 6);
  text.split("").forEach((ch, i) => {
    codeDigits[i] = ch;
  });
  codeRefs.value[Math.min(text.length, 5)]?.focus();
  e.preventDefault();
}

// ── 타이머 (5분) ──
const codeTimer = ref(300);
let timerInterval = null;

function startTimer() {
  codeTimer.value = 300;
  clearInterval(timerInterval);
  timerInterval = setInterval(() => {
    if (codeTimer.value > 0) codeTimer.value--;
    else clearInterval(timerInterval);
  }, 1000);
}

const formattedTimer = computed(() => {
  const m = Math.floor(codeTimer.value / 60)
    .toString()
    .padStart(2, "0");
  const s = (codeTimer.value % 60).toString().padStart(2, "0");
  return `${m}:${s}`;
});

onUnmounted(() => clearInterval(timerInterval));

// ── 비밀번호 강도 ──
const passwordStrength = computed(() => {
  const p = form.newPassword;
  let score = 0;
  if (p.length >= 8) score++;
  if (/[A-Z]/.test(p)) score++;
  if (/[0-9]/.test(p)) score++;
  if (/[^A-Za-z0-9]/.test(p)) score++;
  return score;
});

const strengthColor = computed(
  () =>
    ["bg-rose-400", "bg-orange-400", "bg-yellow-400", "bg-emerald-400"][
      passwordStrength.value - 1
    ] || "bg-slate-200",
);
const strengthTextColor = computed(
  () =>
    ["text-rose-500", "text-orange-500", "text-yellow-500", "text-emerald-500"][
      passwordStrength.value - 1
    ] || "",
);
const strengthLabel = computed(
  () => ["매우 약함", "약함", "보통", "강함"][passwordStrength.value - 1] || "",
);

// ── 단계 정의 ──
const steps = [
  { title: "이메일 입력", desc: "가입 시 사용한 이메일을 입력하세요" },
  { title: "인증번호 확인", desc: "메일로 발송된 6자리 코드를 입력하세요" },
  { title: "새 비밀번호 설정", desc: "새로 사용할 비밀번호를 입력하세요" },
];

// ── 재전송 쿨다운 ──
function startResendCooldown() {
  resendCooldown.value = 60;
  const t = setInterval(() => {
    resendCooldown.value--;
    if (resendCooldown.value <= 0) clearInterval(t);
  }, 1000);
}

// ── Step 0: 이메일 전송 ──
async function handleSendCode() {
  errorMsg.value = "";
  fieldError.userId = "";
  fieldError.email = "";

  if (!form.userId) return (fieldError.userId = "사번을 입력해주세요.");
  if (!form.email) return (fieldError.email = "이메일을 입력해주세요.");
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email))
    return (fieldError.email = "올바른 이메일 형식이 아닙니다.");

  isLoading.value = true;
  try {
    const response = await axios.post("/api/email/sendCode", form);
    console.log(response);
    emailId.value = response.data.emailId;
    if (emailId.value <= 0) {
      //에러 일으키기
      throw new Error("Invalid Email ID"); // 여기서 바로 catch로 점프!
    }
    currentStep.value = 1;
    startTimer();
    startResendCooldown();
  } catch {
    errorMsg.value = "사번 또는 이메일 정보가 일치하지 않습니다.";
  } finally {
    isLoading.value = false;
  }
}

// ── Step 1: 재전송 ──
async function handleResend() {
  if (resendCooldown.value > 0) return;
  try {
    const response = await axios.post("/api/email/sendCode", form);
    emailId.value = response.data.emailId;
    if (emailId.value <= 0) {
      //에러 일으키기
      throw new Error("Invalid Email ID"); // 여기서 바로 catch로 점프!
    }
    await new Promise((r) => setTimeout(r, 800));
    startTimer();
    startResendCooldown();
    codeDigits.forEach((_, i) => (codeDigits[i] = ""));
    codeRefs.value[0]?.focus();
  } catch {
    errorMsg.value = "재전송에 실패했습니다.";
  }
}

// ── Step 1: 인증번호 확인 ──
async function handleVerifyCode() {
  errorMsg.value = "";
  hasCodeError.value = false;
  const code = codeDigits.join("");
  if (code.length < 6) return (hasCodeError.value = true);

  isLoading.value = true;
  try {
    const response = await axios.post("/api/email/verifyCode", {
      emailId: emailId.value,
      code: code,
    });
    console.log(response);
    let success = response.data.success;
    if (!success) {
      throw new Error("Invalid code");
    }
    await new Promise((r) => setTimeout(r, 1000));
    clearInterval(timerInterval);
    currentStep.value = 2;
  } catch {
    hasCodeError.value = true;
    errorMsg.value = "인증번호가 올바르지 않습니다.";
  } finally {
    isLoading.value = false;
  }
}

// ── Step 2: 비밀번호 재설정 ──
async function handleResetPassword() {
  errorMsg.value = "";
  fieldError.newPassword = "";
  fieldError.confirmPassword = "";

  if (!form.newPassword)
    return (fieldError.newPassword = "새 비밀번호를 입력해주세요.");
  if (form.newPassword.length < 6)
    return (fieldError.newPassword = "비밀번호는 6자 이상이어야 합니다.");
  if (!form.confirmPassword)
    return (fieldError.confirmPassword = "비밀번호 확인을 입력해주세요.");
  if (form.newPassword !== form.confirmPassword)
    return (fieldError.confirmPassword = "비밀번호가 일치하지 않습니다.");

  isLoading.value = true;
  try {
    // TODO: POST /api/auth/reset-password { email: form.email, password: form.newPassword }
    const response = await axios.put("/api/emp", {
      userId: form.userId,
      password: form.newPassword,
    });
    console.log(response.data);
    if (!response.data) {
      throw new Error("password change failed.");
    }
    await new Promise((r) => setTimeout(r, 1000));
    currentStep.value = 3;
  } catch {
    errorMsg.value = "비밀번호 변경에 실패했습니다. 잠시 후 다시 시도해주세요.";
  } finally {
    isLoading.value = false;
  }
}
</script>
