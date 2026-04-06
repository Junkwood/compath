<template>
  <div class="flex h-screen overflow-hidden">
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
          <!-- 페이지 타이틀 -->
          <div class="mb-6">
            <h1
              class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
            >
              계정 관리
            </h1>
          </div>

          <!-- 카드 -->
          <div class="card">
            <div class="card-header">
              <span class="card-title">
                전체 계정
                <span class="count-badge">{{ filteredAccounts.length }}</span>
              </span>

              <div class="header-actions">
                <!-- 검색 -->
                <el-input
                  v-model="searchQuery"
                  placeholder="이름, 아이디, 이메일 검색"
                  clearable
                  style="width: 240px"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>

                <!-- 계정 추가 -->
                <el-button class="btn-register" @click="handleCreateEmp">
                  + &nbsp; 계정 추가
                </el-button>
              </div>
            </div>

            <!-- 테이블 -->
            <el-table
              v-loading="isLoading"
              :data="pagedAccounts"
              style="width: 100%"
              :header-cell-style="headerStyle"
              :cell-style="cellStyle"
            >
              <!-- 사번 -->
              <el-table-column label="사번(ID)" align="center" width="100">
                <template #default="{ row }">
                  <span class="text-sky-500 font-medium"
                    >#{{ row.userId }}</span
                  >
                </template>
              </el-table-column>

              <!-- 이름 -->
              <el-table-column label="이름" align="center" min-width="120">
                <template #default="{ row }">
                  <span v-html="highlight(row.name)" class="font-medium" />
                </template>
              </el-table-column>

              <!-- 이메일 -->
              <el-table-column label="이메일" align="center" min-width="200">
                <template #default="{ row }">
                  <span v-html="highlight(row.email)" />
                </template>
              </el-table-column>

              <!-- 그룹 -->
              <el-table-column label="그룹" align="center" min-width="180">
                <template #default="{ row }">
                  <div class="tag-wrap">
                    <template v-if="row.groupList && row.groupList.length > 0">
                      <el-tag
                        v-for="(group, i) in row.groupList"
                        :key="i"
                        size="small"
                        type="primary"
                        effect="light"
                        style="margin: 2px"
                      >
                        {{ group }}
                      </el-tag>
                    </template>
                    <span v-else class="text-gray-400">-</span>
                  </div>
                </template>
              </el-table-column>

              <!-- 권한 -->
              <el-table-column label="권한" align="center" width="120">
                <template #default="{ row }">
                  <el-tag
                    :type="row.userType === 'ADMIN' ? 'danger' : 'info'"
                    size="small"
                    effect="light"
                  >
                    {{ row.userType === "ADMIN" ? "관리자" : "일반 사용자" }}
                  </el-tag>
                </template>
              </el-table-column>

              <!-- 활성화 상태 -->
              <el-table-column label="활성화 상태" align="center" width="130">
                <template #default="{ row }">
                  <el-switch
                    v-model="row.isActive"
                    active-value="Y"
                    inactive-value="N"
                    @change="handleToggle(row)"
                  />
                </template>
              </el-table-column>

              <!-- 관리 -->
              <el-table-column label="관리" align="center" width="100">
                <template #default="{ row }">
                  <el-button
                    class="btn-edit"
                    @click="updateUserModal(row.userId)"
                  >
                    수정
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <!-- 페이지네이션 -->
            <div class="pagination-wrap">
              <span class="pagination-info">
                전체 <b>{{ filteredAccounts.length }}</b
                >명 중 <b>{{ rangeStart }}</b> - <b>{{ rangeEnd }}</b
                >번째
              </span>
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50]"
                :total="filteredAccounts.length"
                layout="sizes, prev, pager, next"
                background
                @size-change="currentPage = 1"
              />
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
  <EmpCreateModal
    ref="empModalRef"
    v-model="CreateEmpOpen"
    :edit-data="selectedEmp"
  />
</template>

<script>
import { onMounted, ref, computed, watch, nextTick } from "vue";
import { Search } from "@element-plus/icons-vue";
import Swal from "sweetalert2";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useEmpStore } from "../stores/empSJW.js";
import EmpCreateModal from "./EmpCreateModal.vue";

export default {
  name: "AccountList",
  components: { Header, Sidebar, EmpCreateModal, Search },
  setup() {
    const CreateEmpOpen = ref(false);
    const selectedEmp = ref(null);
    const sidebarOpen = ref(false);
    const isLoading = ref(false);
    const empStore = useEmpStore();
    const empModalRef = ref(null);
    // ── 검색 ──
    const searchQuery = ref("");

    const filteredAccounts = computed(() => {
      const q = searchQuery.value.trim().toLowerCase();
      if (!q) return empStore.empList;
      return empStore.empList.filter(
        (acc) =>
          String(acc.userId).includes(q) ||
          acc.name?.toLowerCase().includes(q) ||
          acc.email?.toLowerCase().includes(q) ||
          acc.groupList?.some((g) => g.toLowerCase().includes(q)) ||
          (acc.userType === "ADMIN" ? "관리자" : "일반 사용자").includes(q) ||
          (acc.isActive === "Y" ? "활성" : "비활성").includes(q),
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
        '<mark style="background:#fef08a;border-radius:2px;padding:0 2px;">$1</mark>',
      );
    }

    // ── 페이지네이션 ──
    const currentPage = ref(1);
    const pageSize = ref(10);

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

    // ── 초기 로드 ──
    onMounted(async () => {
      isLoading.value = true;
      try {
        await empStore.getEmpList();
      } finally {
        isLoading.value = false;
      }
    });

    // ── 계정 추가 ──
    const handleCreateEmp = () => {
      selectedEmp.value = null;
      CreateEmpOpen.value = true;

      // 💡 [추가] 모달이 렌더링된 직후에 빨간 줄 지우기 함수 호출!
      nextTick(() => {
        if (empModalRef.value && empModalRef.value.clearValidation) {
          empModalRef.value.clearValidation();
        }
      });
    };

    // ... (handleToggle 코드는 그대로 둠) ...

    // ── 활성화 토글 ──
    const handleToggle = async (account) => {
      // 토글 전 원래 상태 저장 (확인 취소 시 원복용)
      const prevStatus = account.isActive === "Y" ? "N" : "Y";
      const action = account.isActive === "Y" ? "활성화" : "비활성화";

      const result = await Swal.fire({
        title: `계정을 ${action}하시겠습니까?`,
        text: `${account.name} (#${account.userId})`,
        icon: "question",
        showCancelButton: true,
        confirmButtonColor: "#2563eb",
        cancelButtonColor: "#6b7280",
        confirmButtonText: "확인",
        cancelButtonText: "취소",
      });

      if (!result.isConfirmed) {
        // 취소 시 원복
        account.isActive = prevStatus;
        return;
      }

      try {
        await empStore.chageStatus(account.userId);
        Swal.fire({
          toast: true,
          position: "top-end",
          icon: "success",
          title: `${action} 처리되었습니다.`,
          showConfirmButton: false,
          timer: 2000,
          timerProgressBar: true,
        });
      } catch {
        account.isActive = prevStatus;
        Swal.fire({
          icon: "error",
          title: "처리 실패",
          text: "상태 변경에 실패했습니다. 다시 시도해주세요.",
          confirmButtonColor: "#2563eb",
        });
        await empStore.getEmpList();
      }
    };

    // ── 계정 수정 ──
    const updateUserModal = async (userId) => {
      try {
        const account = await empStore.getEmp(userId);
        selectedEmp.value = { ...account };
        CreateEmpOpen.value = true;

        // 💡 [추가] 수정 모달을 열 때도 깔끔하게 비워주기
        nextTick(() => {
          if (empModalRef.value && empModalRef.value.clearValidation) {
            empModalRef.value.clearValidation();
          }
        });
      } catch {
        Swal.fire({
          icon: "error",
          title: "조회 실패",
          text: "계정 정보를 불러오는 데 실패했습니다.",
          confirmButtonColor: "#2563eb",
        });
      }
    };

    return {
      sidebarOpen,
      isLoading,
      empStore,
      searchQuery,
      filteredAccounts,
      highlight,
      currentPage,
      pageSize,
      rangeStart,
      rangeEnd,
      pagedAccounts,
      headerStyle,
      cellStyle,
      handleToggle,
      handleCreateEmp,
      CreateEmpOpen,
      selectedEmp,
      updateUserModal,
    };
  },
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
  flex-wrap: wrap;
  gap: 12px;
}
.card-title {
  font-weight: 600;
  font-size: 14px;
  color: #1a1a2e;
  display: flex;
  align-items: center;
  gap: 6px;
}
.count-badge {
  font-size: 13px;
  font-weight: 400;
  color: #9ca3af;
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
.tag-wrap {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 2px;
}
.pagination-wrap {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  border-top: 1px solid #f0f0f0;
  flex-wrap: wrap;
  gap: 8px;
}
.pagination-info {
  font-size: 13px;
  color: #6b7280;
}

/* 버튼 */
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
</style>
