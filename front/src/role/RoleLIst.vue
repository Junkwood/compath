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
          <div class="mb-6">
            <h1
              class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
            >
              역할 관리
            </h1>
          </div>

          <div class="card">
            <div class="card-header">
              <span class="card-title">역할 목록</span>
              <el-button class="btn-register" @click="handleCreate">
                + &nbsp; 역할 등록
              </el-button>
            </div>

            <el-table
              v-loading="isLoading"
              :data="pagedRoles"
              style="width: 100%"
              :header-cell-style="headerStyle"
              :cell-style="cellStyle"
            >
              <el-table-column label="역할" align="center" min-width="160">
                <template #default="{ row }">
                  <span class="role-name">{{ row.roleName }}</span>
                </template>
              </el-table-column>

              <el-table-column label="보유 권한" align="center" width="120">
                <template #default="{ row }">
                  <el-tag size="small" type="info" effect="plain">
                    {{ row.permissions ? row.permissions.length : 0 }}개
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column label="상태(활성화)" align="center" width="160">
                <template #default="{ row }">
                  <el-switch
                    v-model="row.isActive"
                    active-value="Y"
                    inactive-value="N"
                    @change="handleToggle(row)"
                  />
                </template>
              </el-table-column>

              <el-table-column
                label="관리"
                align="center"
                width="100"
                fixed="right"
              >
                <template #default="{ row }">
                  <el-button class="btn-edit" @click="handleEdit(row)"
                    >수정</el-button
                  >
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination-wrap">
              <el-pagination
                v-model:current-page="currentPage"
                :page-size="pageSize"
                :total="roleStore.roleList.length"
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
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router"; // 💡 페이지 이동을 위한 라우터 추가
import Swal from "sweetalert2";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useRoleStore } from "../stores/roleSJW";

const router = useRouter();
const sidebarOpen = ref(false);
const roleStore = useRoleStore();

// ── 페이지네이션 ──
const currentPage = ref(1);
const pageSize = 8;
const isLoading = ref(false);

const pagedRoles = computed(() => {
  const s = (currentPage.value - 1) * pageSize;
  return roleStore.roleList.slice(s, s + pageSize);
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

// ── 페이지 이동 로직 (모달 띄우기 -> 라우팅으로 변경) ──
const handleCreate = () => {
  // 등록 페이지 경로로 이동 (유저님의 실제 라우터 경로에 맞게 수정해주세요)
  router.push("/admin/role/register");
};

const handleEdit = (row) => {
  // 수정 페이지 경로로 이동 (roleId를 파라미터로 넘김)
  router.push(`/admin/role/modify/${row.roleId}`);
};

// ── 활성화 토글 (기존 유지) ──
const handleToggle = async (row) => {
  const prevStatus = row.isActive === "Y" ? "N" : "Y";
  const action = row.isActive === "Y" ? "활성화" : "비활성화";

  const result = await Swal.fire({
    title: `역할을 ${action}하시겠습니까?`,
    text: `"${row.roleName}"`,
    icon: "question",
    showCancelButton: true,
    confirmButtonColor: "#2563eb",
    cancelButtonColor: "#6b7280",
    confirmButtonText: "확인",
    cancelButtonText: "취소",
  });

  if (!result.isConfirmed) {
    row.isActive = prevStatus;
    return;
  }

  try {
    const success = await roleStore.changeRoleStatus(row.roleId, row.isActive);
    if (!success) throw new Error("서버 반환값 false");

    Swal.fire({
      toast: true,
      position: "top-end",
      icon: "success",
      title: `${action} 처리되었습니다.`,
      showConfirmButton: false,
      timer: 2000,
      timerProgressBar: true,
    });
  } catch (err) {
    row.isActive = prevStatus;
    Swal.fire({
      icon: "error",
      title: "처리 실패",
      text: "상태 변경에 실패했습니다. 다시 시도해주세요.",
      confirmButtonColor: "#2563eb",
    });
  }
};

// ── 초기 데이터 로드 ──
onMounted(async () => {
  isLoading.value = true;
  try {
    // 💡 별도 페이지에서 권한을 세팅할 것이므로, 여기서는 롤 리스트만 가볍게 가져옵니다.
    await roleStore.getRoleList();
  } catch {
    Swal.fire({
      icon: "error",
      title: "조회 실패",
      text: "데이터를 불러오는 데 실패했습니다.",
      confirmButtonColor: "#2563eb",
    });
  } finally {
    isLoading.value = false;
  }
});
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
  font-size: 14px;
  color: #1a1a2e;
}
.role-name {
  font-weight: 500;
  color: #1a1a2e;
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 12px 0;
  border-top: 1px solid #f0f0f0;
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
