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

              <el-table-column label="관리" align="center" width="120">
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

  <el-dialog
    v-model="modalVisible"
    :title="isEditMode ? '역할 수정' : '역할 등록'"
    width="500px"
    :close-on-click-modal="false"
    @close="handleModalClose"
  >
    <div v-loading="modalLoading">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="역할 명" prop="roleName">
          <el-input v-model="form.roleName" placeholder="역할명을 입력하세요" />
        </el-form-item>

        <el-form-item label="역할 설명" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="역할에 대한 간단한 설명을 입력하세요 (선택)"
          />
        </el-form-item>

        <el-form-item label="권한">
          <div class="perm-table">
            <div class="perm-header">권한</div>

            <div v-if="!groupedPermissions?.length" class="perm-empty">
              등록된 권한이 없습니다.
            </div>

            <div
              v-for="category in groupedPermissions"
              :key="category.prefix"
              class="perm-group"
            >
              <div
                class="perm-parent-row"
                @click="toggleCategory(category.prefix)"
              >
                <el-checkbox
                  :model-value="isCategoryChecked(category)"
                  :indeterminate="isCategoryIndeterminate(category)"
                  @change="(v) => toggleAllInCategory(category, v)"
                  @click.stop
                />
                <span class="perm-parent-label">{{ category.label }}</span>
                <span class="perm-arrow">{{
                  expandedCategories.includes(category.prefix) ? "∧" : "∨"
                }}</span>
              </div>

              <div v-show="expandedCategories.includes(category.prefix)">
                <div
                  v-for="perm in category.perms"
                  :key="perm.permissionId"
                  class="perm-child-row"
                >
                  <el-checkbox
                    :model-value="
                      form.selectedPermissionIds.includes(perm.permissionId)
                    "
                    @change="(v) => togglePerm(perm.permissionId, v)"
                  />
                  <span class="perm-child-label">{{
                    perm.permissionName
                  }}</span>
                  <span
                    class="perm-method"
                    :class="methodColor(perm.permissionMethod)"
                  >
                    {{ perm.permissionMethod }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <div class="modal-footer">
        <el-button class="btn-list" @click="handleModalClose"
          >← 목록으로</el-button
        >
        <div class="footer-right">
          <el-button class="btn-reset" @click="handleReset">↺ 초기화</el-button>
          <el-button
            class="btn-submit"
            :loading="submitting"
            @click="handleSubmit"
          >
            {{ isEditMode ? "수정 완료" : "역할 등록" }}
          </el-button>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted, reactive, nextTick } from "vue";
import Swal from "sweetalert2"; // 💡 SweetAlert2 Import
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useRoleStore } from "../stores/roleSJW";
import api from "../utils/api";

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

// ── 전체 권한 목록 (permissions 테이블) ──
const allPermissions = ref([]);

// 💡 [수정됨] 유저님의 실제 DB 코드(a1 ~ a20)에 맞춘 카테고리 매핑
const categoryGroups = [
  { label: "상위프로젝트", codes: ["a1", "a2", "a3", "a4"] },
  { label: "하위프로젝트", codes: ["a5", "a6", "a7", "a8"] },
  { label: "업무", codes: ["a9", "a10", "a11", "a12"] },
  { label: "문서", codes: ["a13", "a14", "a15", "a16"] },
  { label: "회의록", codes: ["a17", "a18", "a19", "a20"] },
];

//  flat 권한 목록 → 카테고리별 그룹핑 로직
const groupedPermissions = computed(() => {
  // allPermissions.value가 배열이 아니거나 비어있으면 안전하게 빈 배열 반환!
  if (!allPermissions.value || !Array.isArray(allPermissions.value)) {
    return [];
  }

  const groups = {};

  // 1. 화면에 보여질 순서를 보장하기 위해 미리 빈 방을 만들어 둡니다.
  categoryGroups.forEach((cg) => {
    groups[cg.label] = { prefix: cg.label, label: cg.label, perms: [] };
  });
  groups["ETC"] = { prefix: "ETC", label: "기타", perms: [] };

  // 2. 백엔드에서 받아온 권한들을 알맞은 방에 분배합니다.
  allPermissions.value.forEach((perm) => {
    const foundGroup = categoryGroups.find((cg) =>
      cg.codes.includes(perm.permissionCode),
    );
    const key = foundGroup ? foundGroup.label : "ETC";

    groups[key].perms.push(perm);
  });

  // 3. 권한이 하나도 없는 빈 그룹은 숨기고, 데이터가 있는 그룹만 배열로 반환합니다.
  return Object.values(groups).filter((g) => g.perms.length > 0);
});

// HTTP 메서드 색상
const methodColor = (method) => {
  const map = {
    GET: "method-get",
    POST: "method-post",
    PUT: "method-put",
    DELETE: "method-delete",
  };
  return map[method?.toUpperCase()] ?? "";
};

// ── 모달 상태 ──
const modalVisible = ref(false);
const modalLoading = ref(false);
const submitting = ref(false);
const formRef = ref(null);
const isEditMode = ref(false);
const expandedCategories = ref([]);

const defaultForm = () => ({
  roleId: null,
  roleName: "",
  description: "",
  isActive: "Y",
  selectedPermissionIds: [], // 선택된 permission_id 배열
});

const form = reactive(defaultForm());

// 💡 기존의 rules 객체를 이렇게 덮어씌워 주세요!
const rules = reactive({
  roleName: [
    { required: true, message: "역할명을 입력하세요", trigger: "blur" },
    { max: 100, message: "역할명은 100자 이하로 입력하세요", trigger: "blur" },
    {
      // 💡 [추가] 중복 체크 커스텀 검증 로직
      validator: (rule, value, callback) => {
        if (!value) {
          callback(); // 빈 값은 required 룰에서 잡히므로 패스
          return;
        }

        // store에 있는 목록을 뒤져서 똑같은 이름이 있는지 확인
        const isDuplicate = roleStore.roleList.some((role) => {
          // 수정 모드일 때: 자기 자신의 원래 이름은 중복으로 치면 안 됨!
          if (isEditMode.value && role.roleId === form.roleId) {
            return false;
          }
          // 공백 제거 후 정확히 일치하는지 검사
          return role.roleName.trim() === value.trim();
        });

        if (isDuplicate) {
          callback(new Error("이미 존재하는 역할명입니다."));
        } else {
          callback(); // 통과!
        }
      },
      trigger: "blur", // 입력창에서 포커스가 벗어날 때 검사
    },
  ],
  description: [
    { max: 85, message: "설명은 85자 이하로 입력하세요", trigger: "blur" },
  ],
});

// ── 카테고리 접기/펼치기 ──
const toggleCategory = (prefix) => {
  expandedCategories.value = expandedCategories.value.includes(prefix)
    ? expandedCategories.value.filter((k) => k !== prefix)
    : [...expandedCategories.value, prefix];
};

// ── 부모 체크박스 상태 ──
const isCategoryChecked = (category) =>
  category.perms.length > 0 &&
  category.perms.every((p) =>
    form.selectedPermissionIds.includes(p.permissionId),
  );

const isCategoryIndeterminate = (category) => {
  const checked = category.perms.filter((p) =>
    form.selectedPermissionIds.includes(p.permissionId),
  ).length;
  return checked > 0 && checked < category.perms.length;
};

// ── 부모 체크박스 → 카테고리 전체 선택/해제 ──
const toggleAllInCategory = (category, val) => {
  const ids = category.perms.map((p) => p.permissionId);
  if (val) {
    ids.forEach((id) => {
      if (!form.selectedPermissionIds.includes(id))
        form.selectedPermissionIds.push(id);
    });
    // 체크하면 자동 펼치기
    if (!expandedCategories.value.includes(category.prefix))
      expandedCategories.value.push(category.prefix);
  } else {
    form.selectedPermissionIds = form.selectedPermissionIds.filter(
      (id) => !ids.includes(id),
    );
  }
};

// ── 개별 권한 체크/해제 ──
const togglePerm = (permissionId, checked) => {
  if (checked) {
    if (!form.selectedPermissionIds.includes(permissionId))
      form.selectedPermissionIds.push(permissionId);
  } else {
    form.selectedPermissionIds = form.selectedPermissionIds.filter(
      (id) => id !== permissionId,
    );
  }
};

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

// ── 역할 등록 모달 열기 ──
const handleCreate = () => {
  isEditMode.value = false;
  Object.assign(form, defaultForm());
  expandedCategories.value = [];
  modalVisible.value = true;
  nextTick(() => formRef.value?.clearValidate());
};

// ── 역할 수정 모달 열기 ──
const handleEdit = (row) => {
  isEditMode.value = true;
  Object.assign(form, {
    ...defaultForm(),
    roleId: row.roleId,
    roleName: row.roleName,
    description: row.description,
    isActive: row.isActive,
  });
  expandedCategories.value = [];

  // API 호출을 할 필요 없이, 이미 row 안에 있는 permissions 배열에서 ID만 쏙 뽑아옵니다
  form.selectedPermissionIds = row.permissions
    ? row.permissions.map((p) => p.permissionId)
    : [];

  modalVisible.value = true;
  nextTick(() => formRef.value?.clearValidate());
};

// ── 활성화 토글 ──
const handleToggle = async (row) => {
  const result = await roleStore.changeRoleStatus(row.roleId, row.isActive);
  if (!result) {
    row.isActive = row.isActive === "Y" ? "N" : "Y";
    // 💡 에러 알림 SweetAlert2 적용
    Swal.fire({
      icon: "error",
      title: "변경 실패",
      text: "상태 변경에 실패했습니다.",
      confirmButtonColor: "#2563eb",
    });
  }
};

// ── 모달 닫기 ──
const handleModalClose = () => {
  modalVisible.value = false;
};

// ── 초기화 ──
const handleReset = () => {
  form.roleName = "";
  form.selectedPermissionIds = [];
  expandedCategories.value = [];
  formRef.value?.clearValidate();
};

// ── 제출 ──
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  submitting.value = true;
  try {
    let payload = {
      roleName: form.roleName,
      description: form.description,
      isActive: form.isActive,
      permissionIds: form.selectedPermissionIds, // 선택된 ID 배열만 전송
    };

    if (isEditMode.value) {
      // PUT /api/role/:roleId → { roleName, isActive, permissionIds }
      payload.roleId = form.roleId;
      await api.put("/role", payload);
    } else {
      // POST /api/role → { roleName, isActive, permissionIds }
      console.log(payload);
      await api.post("/role", payload);
    }

    modalVisible.value = false;
    await roleStore.getRoleList();

    // 💡 성공 알림 SweetAlert2 적용
    Swal.fire({
      icon: "success",
      title: isEditMode.value ? "수정 완료" : "등록 완료",
      text: "정상적으로 처리되었습니다.",
      confirmButtonColor: "#2563eb",
    });
  } catch {
    // 💡 에러 알림 SweetAlert2 적용
    Swal.fire({
      icon: "error",
      title: "처리 실패",
      text: isEditMode.value
        ? "역할 수정에 실패했습니다."
        : "역할 등록에 실패했습니다.",
      confirmButtonColor: "#2563eb",
    });
  } finally {
    submitting.value = false;
  }
};

// ── 초기 데이터 로드 ──
onMounted(async () => {
  isLoading.value = true;
  try {
    // 역할 목록 + 전체 권한 목록 병렬 조회
    const [, permsRes] = await Promise.all([
      roleStore.getRoleList(),
      roleStore.getPermissionList(),
    ]);
    allPermissions.value = permsRes.data;
  } catch {
    // 💡 로드 실패 알림 SweetAlert2 적용
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

/* ── 권한 트리 ── */
.perm-table {
  width: 100%;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  overflow: hidden;
}
.perm-header {
  background: #f1f5f9;
  padding: 8px 14px;
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  border-bottom: 1px solid #e2e8f0;
}
.perm-empty {
  padding: 16px;
  text-align: center;
  font-size: 13px;
  color: #9ca3af;
}
.perm-parent-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 14px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  cursor: pointer;
  user-select: none;
}
.perm-parent-label {
  flex: 1;
  font-size: 13px;
  font-weight: 500;
  color: #374151;
}
.perm-arrow {
  font-size: 11px;
  color: #9ca3af;
}
.perm-child-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 7px 14px 7px 36px;
  border-bottom: 1px solid #f1f5f9;
  background: #fff;
}
.perm-child-row:last-child {
  border-bottom: none;
}
.perm-child-label {
  flex: 1;
  font-size: 13px;
  color: #374151;
}
.perm-method {
  font-size: 10px;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 4px;
}
.method-get {
  background: #dcfce7;
  color: #16a34a;
}
.method-post {
  background: #dbeafe;
  color: #2563eb;
}
.method-put {
  background: #fef9c3;
  color: #b45309;
}
.method-delete {
  background: #fee2e2;
  color: #dc2626;
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

/* 모달 푸터 */
.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.footer-right {
  display: flex;
  gap: 8px;
}
.btn-list {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #374151;
  font-size: 13px;
}
.btn-reset {
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  color: #374151;
  font-size: 13px;
}
.btn-submit {
  background: #1d4ed8;
  border: none;
  color: #fff;
  font-size: 13px;
  font-weight: 600;
}
.btn-submit:hover {
  background: #1e40af;
}

:deep(.el-dialog__title) {
  font-weight: 700;
  font-size: 15px;
  color: #1a1a2e;
}
:deep(.el-form-item__label) {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
}
:deep(.el-table) {
  --el-table-border-color: #f1f5f9;
  --el-table-header-bg-color: #f8fafc;
}
:deep(.el-table__row:hover > td) {
  background: #f0f7ff !important;
}
</style>
