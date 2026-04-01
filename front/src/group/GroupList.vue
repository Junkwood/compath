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
            <div class="mb-4 sm:mb-0">
              <h1
                class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
              >
                그룹 관리
              </h1>
            </div>

            <div
              class="grid grid-flow-col sm:auto-cols-max justify-start sm:justify-end gap-2"
            >
              <!-- 검색 -->
              <form class="relative" @submit.prevent>
                <label for="group-search" class="sr-only">검색</label>
                <input
                  id="group-search"
                  v-model="searchQuery"
                  class="form-input pl-9 bg-white dark:bg-gray-800"
                  type="search"
                  placeholder="그룹명 또는 ID 검색"
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
                @click="handleCreateGroup()"
              >
                <svg
                  class="w-4 h-4 fill-current opacity-50 shrink-0"
                  viewBox="0 0 16 16"
                >
                  <path
                    d="M15 7H9V1c0-.6-.4-1-1-1S7 .4 7 1v6H1c-.6 0-1 .4-1 1s.4 1 1 1h6v6c0 .6.4 1 1 1s1-.4 1-1V9h6c.6 0 1-.4 1-1s-.4-1-1-1z"
                  />
                </svg>
                <span class="hidden xs:block ml-2">그룹 추가</span>
              </button>
            </div>
          </div>

          <!-- Table -->
          <div
            class="bg-white dark:bg-gray-800 shadow-lg rounded-sm border border-gray-200 dark:border-gray-700/60 relative"
          >
            <header class="px-5 py-4 flex items-center justify-between">
              <h2 class="font-semibold text-gray-800 dark:text-gray-100">
                전체 그룹
                <span class="text-gray-400 dark:text-gray-500 font-medium">{{
                  filteredGroups.length
                }}</span>
              </h2>
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
                  <thead
                    class="text-xs font-semibold uppercase text-gray-500 dark:text-gray-400 bg-gray-50 dark:bg-gray-900/20 border-t border-b border-gray-200 dark:border-gray-700/60"
                  >
                    <tr>
                      <th
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div class="font-semibold text-center">그룹 ID</div>
                      </th>
                      <th
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div class="font-semibold text-center">그룹명</div>
                      </th>
                      <th
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div class="font-semibold text-center">그룹 유형</div>
                      </th>
                      <th
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div class="font-semibold text-center">그룹 설명</div>
                      </th>
                      <th
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div class="font-semibold text-center">총 인원</div>
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

                  <tbody
                    class="text-sm divide-y divide-gray-200 dark:divide-gray-700/60"
                  >
                    <tr v-if="pagedGroups.length === 0">
                      <td
                        colspan="7"
                        class="px-5 py-10 text-center text-gray-400 dark:text-gray-500"
                      >
                        검색 결과가 없습니다.
                      </td>
                    </tr>

                    <tr v-for="group in pagedGroups" :key="group.groupId">
                      <!-- Group ID -->
                      <td
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                        @click="toGroupInfo(group.groupId)"
                      >
                        <div class="font-medium text-sky-500">
                          #{{ group.groupId }}
                        </div>
                      </td>
                      <!-- Group Name -->

                      <td
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                        @click="toGroupInfo(group.groupId)"
                      >
                        <div
                          class="font-medium text-gray-800 dark:text-gray-100"
                          v-html="highlight(group.groupName)"
                        ></div>
                      </td>
                      <!-- Group Type -->
                      <td
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div
                          class="text-center text-gray-700 dark:text-gray-300"
                        >
                          {{ group.groupType || "-" }}
                        </div>
                      </td>
                      <!-- Description -->
                      <td class="px-2 first:pl-5 last:pr-5 py-3 max-w-xs">
                        <div
                          class="text-gray-500 dark:text-gray-400 truncate"
                          :title="group.description"
                        >
                          {{ group.description || "-" }}
                        </div>
                      </td>
                      <!-- Member Count -->
                      <td
                        class="px-2 first:pl-5 last:pr-5 py-3 whitespace-nowrap"
                      >
                        <div class="text-center">
                          <span
                            class="font-medium text-gray-800 dark:text-gray-100"
                            >{{ group.memberCount ?? 0 }}</span
                          >
                          <span class="text-gray-400 dark:text-gray-500 ml-1"
                            >명</span
                          >
                        </div>
                      </td>
                      <!-- Status Toggle -->
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
                              :checked="group.isActive === 'Y'"
                              @change="handleToggle(group)"
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
                            @click="handleUpdateGroup(group.groupId)"
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
                <p
                  class="text-sm text-gray-500 dark:text-gray-400 text-center sm:text-left"
                >
                  전체
                  <span class="font-medium text-gray-700 dark:text-gray-300">{{
                    filteredGroups.length
                  }}</span
                  >개 중
                  <span class="font-medium text-gray-700 dark:text-gray-300">{{
                    rangeStart
                  }}</span>
                  -
                  <span class="font-medium text-gray-700 dark:text-gray-300">{{
                    rangeEnd
                  }}</span
                  >번째
                </p>

                <div class="flex items-center justify-center gap-1">
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
</template>

<script>
import { onMounted, ref, computed, watch } from "vue";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useGroupStore } from "../stores/groupSJW.js";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth.js";
export default {
  name: "GroupList",
  components: { Header, Sidebar },
  setup() {
    const router = useRouter();
    const sidebarOpen = ref(false);
    const createGroupOpen = ref(false);
    const selectedGroup = ref(null);
    const groupStore = useGroupStore();
    const authStore = useAuthStore();
    // ── 검색 ──
    const searchQuery = ref("");

    const filteredGroups = computed(() => {
      const q = searchQuery.value.trim().toLowerCase();
      if (!q) return groupStore.groupList;
      return groupStore.groupList.filter(
        (g) =>
          String(g.groupId).includes(q) ||
          g.groupName?.toLowerCase().includes(q) ||
          g.groupType?.toLowerCase().includes(q) ||
          g.description?.toLowerCase().includes(q) ||
          (g.isActive === "Y" ? "활성" : "비활성").includes(q),
      );
    });

    watch(searchQuery, () => {
      currentPage.value = 1;
    });

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

    watch(pageSize, () => {
      currentPage.value = 1;
    });

    const totalPages = computed(() =>
      Math.max(1, Math.ceil(filteredGroups.value.length / pageSize.value)),
    );

    const rangeStart = computed(() => {
      if (filteredGroups.value.length === 0) return 0;
      return (currentPage.value - 1) * pageSize.value + 1;
    });

    const rangeEnd = computed(() =>
      Math.min(currentPage.value * pageSize.value, filteredGroups.value.length),
    );

    const pagedGroups = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value;
      return filteredGroups.value.slice(start, start + pageSize.value);
    });

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

    // ── 액션 ──
    onMounted(async () => {
      await groupStore.getGroupList();
    });

    const handleCreateGroup = () => {
      router.push({ name: "groupRegister" });
    };
    const toGroupInfo = (id) => {
      router.push({ name: "groupInfo", params: { id: id } });
    };
    const handleUpdateGroup = async (groupId) => {
      const group = await groupStore.getGroup(groupId);
      selectedGroup.value = { ...group };
      createGroupOpen.value = true;
    };

    const handleToggle = async (group) => {
      try {
        const newStatus = await groupStore.changeStatus(group.groupId);
        group.isActive = newStatus; // 백엔드가 'Y'/'N' 반환 기준
      } catch {
        alert("상태 변경에 실패했습니다.");
        await groupStore.getGroupList();
      }
    };

    return {
      sidebarOpen,
      createGroupOpen,
      selectedGroup,
      groupStore,
      searchQuery,
      filteredGroups,
      highlight,
      currentPage,
      pageSize,
      totalPages,
      rangeStart,
      rangeEnd,
      pagedGroups,
      visiblePages,
      handleCreateGroup,
      handleUpdateGroup,
      handleToggle,
      toGroupInfo,
    };
  },
};
</script>
