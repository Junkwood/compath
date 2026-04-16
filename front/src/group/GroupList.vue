<template>
  <div class="flex h-[100dvh] overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden"
    >
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
          <div class="sm:flex sm:justify-between sm:items-center mb-6">
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
              <form class="relative" @submit.prevent>
                <label for="group-search" class="sr-only">검색</label>
                <input
                  id="group-search"
                  v-model="searchQuery"
                  class="form-input pl-9 bg-white dark:bg-gray-800 text-sm h-full rounded-md border-gray-300"
                  type="search"
                  placeholder="그룹명 또는 ID 검색"
                />
                <button
                  class="absolute inset-0 right-auto group"
                  type="submit"
                  aria-label="Search"
                >
                  <svg
                    class="w-4 h-4 shrink-0 fill-current text-gray-400 ml-3 mr-2"
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

              <el-button class="btn-register h-full" @click="handleCreateGroup">
                + &nbsp; 그룹 추가
              </el-button>
            </div>
          </div>

          <div class="card">
            <div class="card-header">
              <span class="card-title">
                전체 그룹
                <span class="text-gray-400 font-medium ml-1">{{
                  filteredGroups.length
                }}</span>
              </span>
              <div class="flex items-center gap-2 text-sm text-gray-600">
                <select
                  v-model="pageSize"
                  class="form-select text-sm py-1 px-8 bg-white border-gray-200 rounded-md"
                >
                  <option :value="10">10개씩 보기</option>
                  <option :value="20">20개씩 보기</option>
                  <option :value="50">50개씩 보기</option>
                </select>
              </div>
            </div>

            <el-table
              :data="pagedGroups"
              style="width: 100%"
              :header-cell-style="headerStyle"
              :cell-style="cellStyle"
            >
              <el-table-column label="그룹 ID" align="center" width="100">
                <template #default="{ row }">
                  <span
                    class="font-medium text-sky-500 cursor-pointer hover:underline"
                    @click="toGroupInfo(row.groupId)"
                  >
                    #{{ row.groupId }}
                  </span>
                </template>
              </el-table-column>

              <el-table-column label="그룹명" min-width="180">
                <template #default="{ row }">
                  <span
                    class="group-name cursor-pointer hover:text-violet-600 transition-colors"
                    v-html="highlight(row.groupName)"
                    @click="toGroupInfo(row.groupId)"
                  ></span>
                </template>
              </el-table-column>

              <el-table-column label="그룹 유형" align="center" width="140">
                <template #default="{ row }">
                  <el-tag
                    v-if="row.groupType"
                    size="small"
                    effect="light"
                    round
                    class="font-medium"
                  >
                    {{ row.groupType }}
                  </el-tag>
                  <span v-else class="text-gray-400">-</span>
                </template>
              </el-table-column>

              <el-table-column label="그룹 설명" min-width="250">
                <template #default="{ row }">
                  <span
                    class="desc-text truncate block w-full"
                    :title="row.description"
                  >
                    {{ row.description || "-" }}
                  </span>
                </template>
              </el-table-column>

              <el-table-column label="총 인원" align="center" width="100">
                <template #default="{ row }">
                  <span class="font-medium text-gray-800">{{
                    row.memberCount ?? 0
                  }}</span>
                  <span class="text-gray-400 ml-0.5">명</span>
                </template>
              </el-table-column>

              <el-table-column label="활성화 상태" align="center" width="140">
                <template #default="{ row }">
                  <el-switch
                    v-model="row.isActive"
                    active-value="Y"
                    inactive-value="N"
                    @change="handleToggle(row)"
                  />
                </template>
              </el-table-column>

              <el-table-column label="관리" align="center" width="100">
                <template #default="{ row }">
                  <el-button
                    class="btn-edit"
                    @click="handleUpdateGroup(row.groupId)"
                  >
                    수정
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination-wrap">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :total="filteredGroups.length"
                layout="prev, pager, next"
                background
              />
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, computed, watch } from "vue";
import Swal from "sweetalert2";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useGroupStore } from "../stores/groupSJW.js";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth.js";

const router = useRouter();
const sidebarOpen = ref(false);
const groupStore = useGroupStore();
const authStore = useAuthStore();

// ── 테이블 스타일 ──
const headerStyle = () => ({
  background: "#f8fafc",
  color: "#64748b",
  fontSize: "12px",
  fontWeight: "600",
  borderBottom: "1px solid #e2e8f0",
});
const cellStyle = () => ({
  fontSize: "13px",
  color: "#374151",
  borderBottom: "1px solid #f1f5f9",
});

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
    '<mark class="bg-yellow-200 text-inherit rounded px-0.5">$1</mark>',
  );
}

// ── 페이지네이션 ──
const currentPage = ref(1);
const pageSize = ref(10);

watch(pageSize, () => {
  currentPage.value = 1;
});

const pagedGroups = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredGroups.value.slice(start, start + pageSize.value);
});

// ── 액션 ──
onMounted(async () => {
  await groupStore.getGroupList();
});

const handleCreateGroup = () => {
  router.push("../group/register");
};

const toGroupInfo = (id) => {
  router.push({ name: "groupInfo", params: { id: id } });
};

const handleUpdateGroup = (groupId) => {
  router.push(`../group/modify/${groupId}`);
};

// 💡 SweetAlert가 적용된 통일된 토글 로직
const handleToggle = async (row) => {
  const prevStatus = row.isActive === "Y" ? "N" : "Y";
  const action = row.isActive === "Y" ? "활성화" : "비활성화";

  const result = await Swal.fire({
    title: `그룹을 ${action}하시겠습니까?`,
    text: `"${row.groupName}"`,
    icon: "question",
    showCancelButton: true,
    confirmButtonColor: "#2563eb",
    cancelButtonColor: "#6b7280",
    confirmButtonText: "확인",
    cancelButtonText: "취소",
  });

  if (!result.isConfirmed) {
    // 취소 시 스위치 원상복구
    row.isActive = prevStatus;
    return;
  }

  try {
    await groupStore.changeStatus(
      row.groupId,
      row.isActive,
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
  } catch {
    row.isActive = prevStatus;
    Swal.fire({
      icon: "error",
      title: "처리 실패",
      text: "상태 변경에 실패했습니다. 다시 시도해주세요.",
      confirmButtonColor: "#2563eb",
    });
  }
};
</script>

<style scoped>
.card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}
.card-header {
  padding: 14px 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.card-title {
  font-weight: 600;
  font-size: 15px;
  color: #1a1a2e;
}
.group-name {
  font-weight: 600;
  color: #1a1a2e;
}
.desc-text {
  color: #6b7280;
  font-size: 13px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 16px 0;
  border-top: 1px solid #f0f0f0;
}

/* 버튼 스타일 (업무유형과 동일) */
.btn-register {
  background: #2563eb;
  border: none;
  color: #fff;
  font-size: 13px;
  font-weight: 500;
  border-radius: 8px;
}
.btn-register:hover {
  background: #1d4ed8;
}

.btn-edit {
  background: #2563eb;
  border: none;
  color: #fff;
  font-size: 12px;
  font-weight: 500;
  border-radius: 6px;
  padding: 4px 14px;
}
.btn-edit:hover {
  background: #1d4ed8;
}

:deep(.el-table) {
  --el-table-border-color: #f1f5f9;
  --el-table-header-bg-color: #f8fafc;
}
:deep(.el-table__row:hover > td) {
  background: #f0f7ff !important;
}
:deep(.el-pagination.is-background .el-pager li.is-active) {
  background-color: #2563eb;
}
:global(.swal2-container) {
  z-index: 9999 !important;
}
</style>
