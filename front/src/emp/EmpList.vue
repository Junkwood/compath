<template>
  <div class="flex h-[100dvh] overflow-hidden">
    <!-- 1. 사이드바 -->
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden"
    >
      <!-- 2. 헤더 -->
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <!-- 3. 메인 콘텐츠 -->
      <main class="grow">
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
          <!-- Page header -->
          <div class="sm:flex sm:justify-between sm:items-center mb-8">
            <!-- Left: Title -->
            <div class="mb-4 sm:mb-0">
              <h1
                class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
              >
                계정 관리
              </h1>
            </div>

            <!-- Right: Actions -->
            <div
              class="grid grid-flow-col sm:auto-cols-max justify-start sm:justify-end gap-2"
            >
              <!-- 검색 -->
              <form class="relative" @submit.prevent>
                <label for="action-search" class="sr-only">검색</label>
                <input
                  id="action-search"
                  v-model="searchQuery"
                  class="form-input pl-9 bg-white dark:bg-gray-800"
                  type="search"
                  placeholder="이름 또는 아이디 검색"
                />
                <button
                  class="absolute inset-0 right-auto group"
                  type="submit"
                  aria-label="Search"
                >
                  <svg
                    class="w-4 h-4 shrink-0 fill-current text-gray-400 dark:text-gray-500 group-hover:text-gray-500 dark:group-hover:text-gray-400 ml-3 mr-2"
                    viewBox="0 0 16 16"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      d="M7 14c-3.86 0-7-3.14-7-7s3.14-7 7-7 7 3.14 7 7-3.14 7-7 7zM7 2C4.243 2 2 4.243 2 7s2.243 5 5 5 5-2.243 5-5-2.243-5-5-5z"
                    />
                    <path
                      d="M15.707 14.293L13.314 11.9a8.019 8.019 0 01-1.414 1.414l2.393 2.393a.997.997 0 001.414 0 .999.999 0 000-1.414z"
                    />
                  </svg>
                </button>
              </form>

              <!-- Add button -->
              <button
                class="btn bg-violet-500 hover:bg-violet-600 text-white"
                @click="handleCtreateEmp"
              >
                <svg
                  class="w-4 h-4 fill-current opacity-50 shrink-0"
                  viewBox="0 0 16 16"
                >
                  <path
                    d="M15 7H9V1c0-.6-.4-1-1-1S7 .4 7 1v6H1c-.6 0-1 .4-1 1s.4 1 1 1h6v6c0 .6.4 1 1 1s1-.4 1-1V9h6c.6 0 1-.4 1-1s-.4-1-1-1z"
                  />
                </svg>
                <span class="hidden xs:block ml-2">계정 추가</span>
              </button>
            </div>
          </div>

          <!-- Table -->
          <div
            class="bg-white dark:bg-gray-800 shadow-lg rounded-sm border border-gray-200 dark:border-gray-700/60 relative"
          >
            <header class="px-5 py-4 flex items-center justify-between">
              <h2 class="font-semibold text-gray-800 dark:text-gray-100">
                전체 계정
                <span class="text-gray-400 dark:text-gray-500 font-medium">{{
                  filteredAccounts.length
                }}</span>
              </h2>
              <!-- 페이지당 항목 수 -->
              <div
                class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400"
              >
                <span>페이지당</span>
                <select
                  v-model="pageSize"
                  class="form-select text-sm py-1 bg-white dark:bg-gray-800 border-gray-200 dark:border-gray-700"
                >
                  <option :value="10">10개</option>
                  <option :value="20">20개</option>
                  <option :value="50">50개</option>
                </select>
              </div>
            </header>

            <div>
              <div class="overflow-x-auto">
                <table class="table-auto w-full dark:text-gray-300">
                  <!-- Table header -->
                  <thead
                    class="text-xs font-semibold uppercase text-gray-500 dark:text-gray-400 bg-gray-50 dark:bg-gray-900/20 border-t border-b border-gray-200 dark:border-gray-700/60"
                  >
                    <tr>
                      <th
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div class="font-semibold text-center">사번(ID)</div>
                      </th>
                      <th
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div class="font-semibold text-center">이름</div>
                      </th>
                      <th
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div class="font-semibold text-center">이메일</div>
                      </th>
                      <th
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div class="font-semibold text-center">그룹</div>
                      </th>
                      <th
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div class="font-semibold text-center">권한</div>
                      </th>
                      <th
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div class="font-semibold text-center">활성화 상태</div>
                      </th>
                      <th
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div class="font-semibold text-center">관리</div>
                      </th>
                    </tr>
                  </thead>

                  <!-- Table body -->
                  <tbody
                    class="text-sm divide-y divide-gray-200 dark:divide-gray-700/60"
                  >
                    <!-- 검색 결과 없을 때 -->
                    <tr v-if="pagedAccounts.length === 0">
                      <td
                        colspan="7"
                        class="px-5 py-10 text-center text-gray-400 dark:text-gray-500"
                      >
                        검색 결과가 없습니다.
                      </td>
                    </tr>

                    <tr v-for="account in pagedAccounts" :key="account.userId">
                      <!-- ID -->
                      <td
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div class="font-medium text-sky-500">
                          #{{ account.userId }}
                        </div>
                      </td>
                      <!-- Name -->
                      <td
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <!-- 검색어 하이라이트 -->
                        <div
                          class="font-medium text-gray-800 dark:text-gray-100"
                          v-html="highlight(account.name)"
                        ></div>
                      </td>
                      <!-- Email -->
                      <td
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div v-html="highlight(account.email)"></div>
                      </td>
                      <!-- Group -->
                      <td
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-normal"
                      >
                        <div class="flex flex-wrap gap-1">
                          <template
                            v-if="
                              account.groupList && account.groupList.length > 0
                            "
                          >
                            <span
                              v-for="(group, index) in account.groupList"
                              :key="index"
                              class="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-indigo-100 text-indigo-800 dark:bg-indigo-800 dark:text-indigo-100"
                            >
                              {{ group }}
                            </span>
                          </template>
                          <template v-else>
                            <span class="text-gray-400 dark:text-gray-500"
                              >-</span
                            >
                          </template>
                        </div>
                      </td>
                      <!-- Role -->
                      <td
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div class="text-center">
                          {{
                            account.userType === "ADMIN"
                              ? "관리자"
                              : "일반 사용자"
                          }}
                        </div>
                      </td>
                      <!-- Status -->
                      <td
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div
                          class="text-center flex justify-center items-center"
                        >
                          <label
                            class="relative inline-flex items-center cursor-pointer"
                          >
                            <input
                              type="checkbox"
                              class="sr-only peer"
                              :checked="account.isActive === 'Y'"
                              @change="handleToggle(account)"
                            />
                            <div
                              class="w-11 h-6 bg-gray-200 rounded-full peer peer-focus:ring-4 peer-focus:ring-emerald-300 dark:peer-focus:ring-emerald-800 dark:bg-gray-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-0.5 after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-gray-600 peer-checked:bg-emerald-500"
                            ></div>
                          </label>
                        </div>
                      </td>
                      <!-- Actions -->
                      <td
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap w-px"
                      >
                        <div class="space-x-1 text-center">
                          <button
                            class="text-gray-400 hover:text-gray-500 dark:text-gray-500 dark:hover:text-gray-400 rounded-full"
                            @click="updateUserModal(account.userId)"
                          >
                            <span class="sr-only">Edit</span>
                            <svg
                              class="w-8 h-8 fill-current"
                              viewBox="0 0 32 32"
                            >
                              <path
                                d="M19.7 8.3c-.4-.4-1-.4-1.4 0l-10 10c-.2.2-.3.4-.3.7v4c0 .6.4 1 1 1h4c.3 0 .5-.1.7-.3l10-10c.4-.4.4-1 0-1.4l-4-4zM12.6 22H10v-2.6l6-6 2.6 2.6-6 6zm7.4-7.4L17.4 12l1.6-1.6 2.6 2.6-1.6 1.6z"
                              />
                            </svg>
                          </button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- Pagination -->
            <div
              class="px-5 py-4 border-t border-gray-200 dark:border-gray-700/60"
            >
              <div
                class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3"
              >
                <!-- 현재 표시 범위 -->
                <p
                  class="text-sm text-gray-500 dark:text-gray-400 text-center sm:text-left"
                >
                  전체
                  <span class="font-medium text-gray-700 dark:text-gray-300">{{
                    filteredAccounts.length
                  }}</span
                  >명 중
                  <span class="font-medium text-gray-700 dark:text-gray-300">{{
                    rangeStart
                  }}</span>
                  -
                  <span class="font-medium text-gray-700 dark:text-gray-300">{{
                    rangeEnd
                  }}</span
                  >번째
                </p>

                <!-- 페이지 버튼 -->
                <div class="flex items-center justify-center gap-1">
                  <!-- 이전 -->
                  <button
                    :disabled="currentPage === 1"
                    @click="currentPage--"
                    class="px-2.5 py-1.5 rounded text-sm border border-gray-200 dark:border-gray-700 text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
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
                  </button>

                  <!-- 페이지 번호 -->
                  <button
                    v-for="page in visiblePages"
                    :key="page"
                    @click="page !== '...' && (currentPage = page)"
                    :class="[
                      'px-3 py-1.5 rounded text-sm border transition-colors',
                      page === currentPage
                        ? 'bg-violet-500 border-violet-500 text-white font-medium'
                        : page === '...'
                          ? 'border-transparent text-gray-400 cursor-default'
                          : 'border-gray-200 dark:border-gray-700 text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700',
                    ]"
                  >
                    {{ page }}
                  </button>

                  <!-- 다음 -->
                  <button
                    :disabled="currentPage === totalPages"
                    @click="currentPage++"
                    class="px-2.5 py-1.5 rounded text-sm border border-gray-200 dark:border-gray-700 text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
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
                        d="M9 5l7 7-7 7"
                      />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
  <EmpCreateModal v-model="CreateEmpOpen" :edit-data="selectedEmp" />
</template>

<script>
import { onMounted, ref, computed, watch } from "vue";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useEmpStore } from "../stores/empSJW.js";
import EmpCreateModal from "./EmpCreateModal.vue";

export default {
  name: "AccountList",
  components: { Header, Sidebar, EmpCreateModal },
  setup() {
    const CreateEmpOpen = ref(false);
    const selectedEmp = ref(null);
    const sidebarOpen = ref(false);
    const empStore = useEmpStore();

    // ── 검색 ──
    const searchQuery = ref("");

    const filteredAccounts = computed(() => {
      const q = searchQuery.value.trim().toLowerCase();
      if (!q) return empStore.empList;

      return empStore.empList.filter((acc) => {
        return (
          String(acc.userId).includes(q) || // 사번
          acc.name?.toLowerCase().includes(q) || // 이름
          acc.email?.toLowerCase().includes(q) || // 이메일
          acc.groupList?.some((g) => g.toLowerCase().includes(q)) || // 직군 그룹
          (acc.userType === "ADMIN" ? "관리자" : "일반 사용자").includes(q) || // 권한
          (acc.isActive === "Y" ? "활성" : "비활성").includes(q) // 활성화 상태
        );
      });
    });

    // 검색어 바뀌면 1페이지로 이동
    watch(searchQuery, () => {
      currentPage.value = 1;
    });

    // 검색어 하이라이트
    function highlight(text) {
      const q = searchQuery.value.trim();
      if (!q || !text) return text;
      const regex = new RegExp(`(${q})`, "gi");
      return String(text).replace(
        regex,
        '<mark class="bg-yellow-100 dark:bg-yellow-800 text-inherit rounded px-0.5">$1</mark>',
      );
    }

    // ── 페이지네이션 ──
    const currentPage = ref(1);
    const pageSize = ref(10);

    const totalPages = computed(() =>
      Math.max(1, Math.ceil(filteredAccounts.value.length / pageSize.value)),
    );

    // pageSize 바뀌면 1페이지로 이동
    watch(pageSize, () => {
      currentPage.value = 1;
    });

    const rangeStart = computed(() => {
      if (filteredAccounts.value.length === 0) return 0;
      return (currentPage.value - 1) * pageSize.value + 1;
    });

    const rangeEnd = computed(() =>
      Math.min(
        currentPage.value * pageSize.value,
        filteredAccounts.value.length,
      ),
    );

    const pagedAccounts = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value;
      return filteredAccounts.value.slice(start, start + pageSize.value);
    });

    // 페이지 번호 목록 (... 말줄임 포함)
    const visiblePages = computed(() => {
      const total = totalPages.value;
      const cur = currentPage.value;
      if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1);

      const pages = [];
      if (cur <= 4) {
        pages.push(1, 2, 3, 4, 5, "...", total);
      } else if (cur >= total - 3) {
        pages.push(1, "...", total - 4, total - 3, total - 2, total - 1, total);
      } else {
        pages.push(1, "...", cur - 1, cur, cur + 1, "...", total);
      }
      return pages;
    });

    // ── 기존 로직 ──
    onMounted(async () => {
      await empStore.getEmpList();
    });

    const handleCtreateEmp = () => {
      selectedEmp.value = null;
      CreateEmpOpen.value = true;
    };

    const handleToggle = async (account) => {
      try {
        const newStatus = await empStore.chageStatus(account.userId);
        account.isActive = newStatus;
      } catch {
        alert("실패");
        await empStore.getEmpList();
      }
    };

    const updateUserModal = async (userId) => {
      const account = await empStore.getEmp(userId);
      selectedEmp.value = { ...account };
      CreateEmpOpen.value = true;
    };

    return {
      sidebarOpen,
      empStore,
      searchQuery,
      filteredAccounts,
      highlight,
      currentPage,
      pageSize,
      totalPages,
      rangeStart,
      rangeEnd,
      pagedAccounts,
      visiblePages,
      handleToggle,
      handleCtreateEmp,
      CreateEmpOpen,
      selectedEmp,
      updateUserModal,
    };
  },
};
</script>
