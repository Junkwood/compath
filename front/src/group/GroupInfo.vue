<template>
  <div class="flex h-[100dvh] overflow-hidden">
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
          <div class="sm:flex sm:justify-between sm:items-center mb-8">
            <div class="mb-4 sm:mb-0 flex items-center gap-3">
              <button
                class="p-1.5 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors"
                @click="goBack"
              >
                <svg
                  class="w-6 h-6 fill-current text-gray-500"
                  viewBox="0 0 24 24"
                >
                  <path
                    d="M15.41 16.59L10.83 12l4.58-4.59L14 6l-6 6 6 6 1.41-1.41z"
                  />
                </svg>
              </button>
              <h1
                class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
              >
                그룹 정보 조회
              </h1>
            </div>

            <button
              class="btn bg-[#2563eb] hover:bg-blue-700 text-white border-none rounded-lg font-medium px-4 py-2 flex items-center"
              @click="handleEdit"
            >
              <svg
                class="w-4 h-4 fill-current opacity-70 shrink-0 mr-2"
                viewBox="0 0 32 32"
              >
                <path
                  d="M19.7 8.3c-.4-.4-1-.4-1.4 0l-10 10c-.2.2-.3.4-.3.7v4c0 .6.4 1 1 1h4c.3 0 .5-.1.7-.3l10-10c.4-.4.4-1 0-1.4l-4-4zM12.6 22H10v-2.6l6-6 2.6 2.6-6 6zm7.4-7.4L17.4 12l1.6-1.6 2.6 2.6-1.6 1.6z"
                />
              </svg>
              그룹 수정
            </button>
          </div>

          <div v-if="isLoading" class="flex items-center justify-center py-20">
            <svg
              class="animate-spin w-8 h-8 text-blue-600"
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
          </div>

          <div v-else class="grid grid-cols-1 xl:grid-cols-2 gap-6">
            <div class="flex flex-col gap-6">
              <div
                class="bg-white dark:bg-gray-800 shadow-lg rounded-sm border border-gray-200 dark:border-gray-700/60 p-6"
              >
                <h2
                  class="font-semibold text-gray-800 dark:text-gray-100 text-base mb-5 pb-3 border-b border-gray-200 dark:border-gray-700"
                >
                  기본 정보
                </h2>
                <div class="space-y-4">
                  <div class="flex flex-col sm:flex-row sm:items-center gap-3">
                    <span
                      class="text-sm font-semibold text-gray-500 dark:text-gray-400 min-w-[80px]"
                      >그룹명</span
                    >
                    <div class="flex items-center gap-4 flex-1">
                      <span
                        class="text-gray-800 dark:text-gray-100 font-medium"
                        >{{ group.groupName }}</span
                      >

                      <div class="flex items-center gap-2">
                        <el-switch
                          v-model="group.isActive"
                          active-value="Y"
                          inactive-value="N"
                          @change="handleToggle"
                        />
                        <span
                          class="text-sm font-medium"
                          :class="
                            group.isActive === 'Y'
                              ? 'text-blue-600'
                              : 'text-gray-400'
                          "
                        >
                          {{ group.isActive === "Y" ? "활성화" : "비활성화" }}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div class="flex flex-col sm:flex-row sm:items-center gap-3">
                    <span
                      class="text-sm font-semibold text-gray-500 dark:text-gray-400 min-w-[80px]"
                      >그룹 ID</span
                    >
                    <span class="font-medium text-sky-500"
                      >#{{ group.groupId }}</span
                    >
                  </div>

                  <div class="flex flex-col sm:flex-row sm:items-center gap-3">
                    <span
                      class="text-sm font-semibold text-gray-500 dark:text-gray-400 min-w-[80px]"
                      >그룹유형</span
                    >
                    <el-tag
                      size="small"
                      effect="light"
                      round
                      class="font-medium"
                      :type="group.groupType === 'C2' ? 'primary' : 'success'"
                    >
                      {{
                        group.groupType === "C2" ? "프로젝트 그룹" : "직군 그룹"
                      }}
                    </el-tag>
                  </div>

                  <div class="flex flex-col sm:flex-row sm:items-start gap-3">
                    <span
                      class="text-sm font-semibold text-gray-500 dark:text-gray-400 min-w-[80px] pt-0.5"
                      >그룹설명</span
                    >
                    <span
                      class="text-gray-700 dark:text-gray-300 text-sm leading-relaxed"
                    >
                      {{ group.description || "-" }}
                    </span>
                  </div>
                </div>
              </div>

              <div
                class="bg-white dark:bg-gray-800 shadow-lg rounded-sm border border-gray-200 dark:border-gray-700/60"
              >
                <header
                  class="px-5 py-4 flex items-center justify-between border-b border-gray-200 dark:border-gray-700"
                >
                  <h2 class="font-semibold text-gray-800 dark:text-gray-100">
                    그룹 구성원
                    <span
                      class="text-gray-400 dark:text-gray-500 font-medium ml-1"
                    >
                      {{ group.members?.length ?? 0 }}
                    </span>
                  </h2>
                </header>

                <div class="overflow-x-auto">
                  <table class="table-auto w-full dark:text-gray-300">
                    <thead
                      class="text-xs font-semibold uppercase text-gray-500 dark:text-gray-400 bg-gray-50 dark:bg-gray-900/20 border-b border-gray-200 dark:border-gray-700"
                    >
                      <tr>
                        <th class="px-4 py-3 text-center">사번</th>
                        <th class="px-4 py-3 text-center">이름</th>
                        <th class="px-4 py-3 text-center">직군그룹</th>
                        <th
                          v-if="group.groupType === 'C2'"
                          class="px-4 py-3 text-center"
                        >
                          역할
                        </th>
                      </tr>
                    </thead>
                    <tbody
                      class="text-sm divide-y divide-gray-200 dark:divide-gray-700/60"
                    >
                      <tr v-if="!group.members || group.members.length === 0">
                        <td
                          :colspan="group.groupType === 'C2' ? 4 : 3"
                          class="px-4 py-8 text-center text-gray-400 dark:text-gray-500"
                        >
                          구성원이 없습니다.
                        </td>
                      </tr>
                      <tr
                        v-for="member in group.members"
                        :key="member.userId"
                        class="text-center hover:bg-gray-50 dark:hover:bg-gray-700/20 transition-colors"
                      >
                        <td class="px-4 py-3 font-medium text-sky-500">
                          #{{ member.userId }}
                        </td>
                        <td
                          class="px-4 py-3 font-medium text-gray-800 dark:text-gray-100"
                        >
                          {{ member.name }}
                        </td>
                        <td class="px-4 py-3 text-gray-600 dark:text-gray-400">
                          {{ member.primaryGroupName || "-" }}
                        </td>
                        <td v-if="group.groupType === 'C2'" class="px-4 py-3">
                          <el-tag
                            size="small"
                            type="info"
                            effect="plain"
                            class="font-medium"
                          >
                            {{ member.roleName || "-" }}
                          </el-tag>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>

            <div
              class="bg-white dark:bg-gray-800 shadow-lg rounded-sm border border-gray-200 dark:border-gray-700/60 flex flex-col"
              style="min-height: 500px"
            >
              <header
                class="px-5 py-4 border-b border-gray-200 dark:border-gray-700 shrink-0"
              >
                <h2 class="font-semibold text-gray-800 dark:text-gray-100">
                  그룹 히스토리
                </h2>
              </header>

              <div class="flex-1 overflow-y-auto p-5">
                <div
                  v-if="!group.logs || group.logs.length === 0"
                  class="flex items-center justify-center py-16 text-gray-400 dark:text-gray-500 text-sm"
                >
                  히스토리가 없습니다.
                </div>

                <div v-else class="relative">
                  <div
                    class="absolute left-3 top-0 bottom-0 w-0.5 bg-gray-200 dark:bg-gray-700"
                  ></div>

                  <div class="space-y-6">
                    <div
                      v-for="item in group.logs"
                      :key="item.activityLogId"
                      class="relative flex gap-4 pl-10"
                    >
                      <div
                        :class="[
                          'absolute left-0 w-6 h-6 rounded-full flex items-center justify-center shrink-0 border-2 border-white dark:border-gray-800',
                          historyDotColor(item.actionType),
                        ]"
                      >
                        <svg
                          class="w-3 h-3 text-white"
                          fill="currentColor"
                          viewBox="0 0 20 20"
                        >
                          <path
                            v-if="
                              ['신규 그룹 생성', '신규 구성원 추가'].includes(
                                item.actionType,
                              )
                            "
                            d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z"
                          />
                          <path
                            v-else-if="item.actionType === '구성원 제외'"
                            fill-rule="evenodd"
                            d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                            clip-rule="evenodd"
                          />
                          <path
                            v-else
                            fill-rule="evenodd"
                            d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                            clip-rule="evenodd"
                          />
                        </svg>
                      </div>

                      <div class="flex-1">
                        <div class="flex items-center gap-2 mb-1.5 flex-wrap">
                          <span
                            :class="[
                              'text-xs font-semibold px-2 py-0.5 rounded',
                              historyBadgeColor(item.actionType),
                            ]"
                          >
                            {{ item.actionType }}
                          </span>

                          <span
                            class="text-xs text-gray-400 dark:text-gray-500"
                          >
                            {{ formatDate(item.createdAt) }}
                          </span>

                          <span
                            class="text-xs text-gray-400 dark:text-gray-500"
                          >
                            | 작업자: {{ item.actorName || item.actorUserId }}
                          </span>
                        </div>

                        <p
                          class="text-sm text-gray-700 dark:text-gray-300 leading-relaxed whitespace-pre-line mt-1"
                        >
                          <template
                            v-if="
                              ['신규 그룹 생성', '신규 구성원 추가'].includes(
                                item.actionType,
                              )
                            "
                          >
                            <span
                              class="font-medium text-gray-800 dark:text-gray-200"
                            >
                              {{ item.afterValue }}
                            </span>
                          </template>

                          <template
                            v-else-if="item.actionType === '구성원 제외'"
                          >
                            <span class="font-medium text-red-500">
                              {{ item.beforeValue }}
                            </span>
                          </template>

                          <template v-else>
                            <del class="text-gray-400 mr-1">{{
                              item.beforeValue
                            }}</del>
                            <span class="font-bold text-gray-400 mx-1">➔</span>
                            <span
                              class="font-medium text-gray-800 dark:text-gray-200"
                              >{{ item.afterValue }}</span
                            >
                          </template>
                        </p>
                      </div>
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
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import Swal from "sweetalert2";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useGroupStore } from "../stores/groupSJW";
import { useAuthStore } from "../stores/auth";

const router = useRouter();
const route = useRoute();
const sidebarOpen = ref(false);
const isLoading = ref(false);
const groupStore = useGroupStore();
const authStore = useAuthStore();

const group = ref({
  groupId: null,
  groupName: "",
  groupType: "",
  description: "",
  isActive: "Y",
  members: [],
  logs: [],
});

// ── 💡 새 포맷에 맞춘 날짜 변환 함수 ──
function formatDate(dateString) {
  if (!dateString) return "";
  const date = new Date(dateString);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")} ${String(date.getHours()).padStart(2, "0")}:${String(date.getMinutes()).padStart(2, "0")}`;
}

function historyDotColor(actionType) {
  if (["신규 그룹 생성", "신규 구성원 추가"].includes(actionType))
    return "bg-blue-500";
  if (actionType === "구성원 제외") return "bg-red-500";
  return "bg-green-500"; // 그룹명 변경, 상태 변경, 그룹 설명 변경, 역할 변경 등
}

function historyBadgeColor(actionType) {
  if (["신규 그룹 생성", "신규 구성원 추가"].includes(actionType))
    return "bg-blue-100 text-blue-700 dark:bg-blue-900/40 dark:text-blue-400";
  if (actionType === "구성원 제외")
    return "bg-red-100 text-red-700 dark:bg-red-900/40 dark:text-red-400";
  return "bg-green-100 text-green-700 dark:bg-green-900/40 dark:text-green-400";
}

// ── 💡 SweetAlert가 적용된 토글 액션 ──
const handleToggle = async () => {
  const prevStatus = group.value.isActive === "Y" ? "N" : "Y";
  const action = group.value.isActive === "Y" ? "활성화" : "비활성화";

  const result = await Swal.fire({
    title: `그룹을 ${action}하시겠습니까?`,
    text: `"${group.value.groupName}"`,
    icon: "question",
    showCancelButton: true,
    confirmButtonColor: "#2563eb",
    cancelButtonColor: "#6b7280",
    confirmButtonText: "확인",
    cancelButtonText: "취소",
  });

  if (!result.isConfirmed) {
    // 취소 시 스위치 원상복구
    group.value.isActive = prevStatus;
    return;
  }

  try {
    await groupStore.changeStatus(
      group.value.groupId,
      group.value.isActive,
      authStore.user.userId,
    );

    Swal.fire({
      toast: true,
      position: "top-end",
      icon: "success",
      title: `${action} 처리되었습니다.`,
      showConfirmButton: false,
      timer: 2000,
    });

    await loadGroupInfo();
  } catch {
    group.value.isActive = prevStatus;
    Swal.fire({
      icon: "error",
      title: "처리 실패",
      text: "상태 변경에 실패했습니다. 다시 시도해주세요.",
      confirmButtonColor: "#2563eb",
    });
  }
};

const handleEdit = () => {
  router.push(`/admin/group/modify/${group.value.groupId}`);
};

const goBack = () => router.back();

const loadGroupInfo = async () => {
  try {
    const groupId = route.params.id;
    const data = await groupStore.getGroupInfo(groupId);
    group.value = data;
  } catch {
    Swal.fire({
      icon: "error",
      title: "오류",
      text: "그룹 정보를 불러오는데 실패했습니다.",
      confirmButtonColor: "#2563eb",
    });
  } finally {
    isLoading.value = false;
  }
};

onMounted(async () => {
  isLoading.value = true;
  loadGroupInfo();
});
</script>

<style scoped>
/* SweetAlert 모달 z-index 방어용 */
:global(.swal2-container) {
  z-index: 9999 !important;
}
</style>
