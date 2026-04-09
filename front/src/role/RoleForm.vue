<template>
  <div class="flex h-screen overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden bg-gray-50"
    >
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-5xl mx-auto">
          <div class="flex items-center gap-2 mb-6">
            <button
              class="flex items-center justify-center w-10 h-10 rounded-full text-gray-400 hover:text-gray-800 hover:bg-gray-200 transition-colors cursor-pointer border-none bg-transparent"
              @click="handleGoBack"
              title="목록으로 돌아가기"
            >
              <span class="text-2xl font-bold leading-none -mt-1">←</span>
            </button>
            <h1 class="text-2xl md:text-3xl text-gray-800 font-bold">
              {{ isEditMode ? "역할 수정" : "새 역할 등록" }}
            </h1>
          </div>

          <div class="card" v-loading="isLoading">
            <el-form
              ref="formRef"
              :model="form"
              :rules="rules"
              label-position="top"
              @submit.prevent
            >
              <div class="section-title">기본 정보</div>
              <div class="form-grid">
                <el-form-item label="역할 명" prop="roleName">
                  <el-input
                    v-model="form.roleName"
                    placeholder="역할명을 입력하세요"
                  />
                </el-form-item>

                <el-form-item label="사용 여부 (활성화)">
                  <el-switch
                    v-model="form.isActive"
                    active-value="Y"
                    inactive-value="N"
                  />
                </el-form-item>
              </div>

              <el-form-item label="역할 설명" prop="description">
                <el-input
                  v-model="form.description"
                  type="textarea"
                  :rows="3"
                  placeholder="역할에 대한 간단한 설명을 입력하세요 (선택)"
                />
              </el-form-item>

              <el-divider />

              <div class="flex justify-between items-end mb-4">
                <div class="section-title mb-0">권한 설정</div>

                <div class="flex items-center gap-6">
                  <el-checkbox
                    :model-value="isGlobalAllChecked"
                    :indeterminate="isGlobalIndeterminate"
                    @change="handleGlobalToggle"
                  >
                    <span class="text-sm font-bold text-blue-700"
                      >시스템 전체 권한 선택</span
                    >
                  </el-checkbox>

                  <div class="text-sm text-gray-500 font-medium border-l pl-6">
                    총 선택된 권한:
                    <span class="text-blue-600 font-bold">{{
                      form.selectedPermissionIds.length
                    }}</span
                    >개
                  </div>
                </div>
              </div>

              <div class="split-container">
                <div class="split-left">
                  <div class="split-left-header">권한 그룹</div>
                  <ul class="group-list">
                    <li
                      v-for="group in groupedPermissions"
                      :key="group.groupId"
                      class="group-item"
                      :class="{
                        'is-active': activeGroupId === group.groupId,
                      }"
                      @click="activeGroupId = group.groupId"
                    >
                      <span class="group-name">{{ group.groupName }}</span>
                      <span
                        class="group-count-badge"
                        :class="{ 'has-selected': getSelectedCount(group) > 0 }"
                      >
                        {{ getSelectedCount(group) }} /
                        {{ group.allIds.length }}
                      </span>
                    </li>
                  </ul>
                </div>

                <div class="split-right">
                  <div v-if="activeGroupData" class="split-right-header">
                    <span class="font-bold text-gray-800"
                      >{{ activeGroupData.groupName }} 권한 목록</span
                    >

                    <el-checkbox
                      :model-value="isCurrentGroupAllChecked"
                      :indeterminate="isCurrentGroupIndeterminate"
                      @change="handleCurrentGroupToggle"
                    >
                      그룹 내 전체 선택
                    </el-checkbox>
                  </div>

                  <div v-if="activeGroupData" class="perm-grid">
                    <div
                      v-for="perm in activeGroupData.perms"
                      :key="perm.permissionId"
                      class="perm-item-card"
                      :class="{
                        'is-checked': form.selectedPermissionIds.includes(
                          perm.permissionId,
                        ),
                      }"
                      @click="togglePerm(perm.permissionId)"
                    >
                      <div class="flex items-center gap-3">
                        <el-checkbox
                          :model-value="
                            form.selectedPermissionIds.includes(
                              perm.permissionId,
                            )
                          "
                          @change="() => togglePerm(perm.permissionId)"
                          @click.stop
                        />
                        <div class="flex flex-col">
                          <span class="font-medium text-gray-800 text-[13px]">{{
                            perm.permissionName
                          }}</span>
                          <span
                            class="text-[11px] text-gray-400 font-mono mt-0.5"
                            >{{ perm.permissionUrl }}</span
                          >
                        </div>
                      </div>
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

              <div class="form-footer">
                <el-button class="btn-cancel" @click="handleGoBack"
                  >취소</el-button
                >
                <el-button
                  class="btn-submit"
                  :loading="submitting"
                  @click="handleSubmit"
                >
                  {{ isEditMode ? "수정 완료" : "등록하기" }}
                </el-button>
              </div>
            </el-form>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import Swal from "sweetalert2";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useRoleStore } from "../stores/roleSJW";
import api from "../utils/api";
import admin from "../utils/admin";

const route = useRoute();
const router = useRouter();
const roleStore = useRoleStore();

const sidebarOpen = ref(false);
const isLoading = ref(false);
const submitting = ref(false);
const formRef = ref(null);

const isEditMode = computed(() => !!route.params.id);

const allPermissions = ref([]);
// 💡 Name 대신 ID로 상태를 관리합니다.
const activeGroupId = ref(null);

const defaultForm = () => ({
  roleId: null,
  roleName: "",
  description: "",
  isActive: "Y",
  selectedPermissionIds: [],
});
const form = reactive(defaultForm());

const rules = reactive({
  roleName: [
    { required: true, message: "역할명을 입력하세요", trigger: "blur" },
    { max: 85, message: "최대 85자까지 입력 가능합니다.", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback();
          return;
        }

        const isDuplicate = roleStore.roleList.some((role) => {
          if (isEditMode.value && role.roleId === form.roleId) {
            return false;
          }
          return role.roleName.trim() === value.trim();
        });

        if (isDuplicate) {
          callback(new Error("이미 존재하는 역할명입니다."));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  description: [
    { max: 85, message: "설명은 85자 이하로 입력하세요", trigger: "blur" },
  ],
});

// ── 데이터 분류 (ID 기준 그룹핑) ──
const groupedPermissions = computed(() => {
  if (!allPermissions.value || !Array.isArray(allPermissions.value)) return [];

  const groups = {};
  allPermissions.value.forEach((perm) => {
    // 💡 무조건 변하지 않는 ID를 기준으로 묶습니다.
    const gId = perm.permissionGroupId || "etc_id";

    if (!groups[gId]) {
      groups[gId] = {
        groupId: gId,
        groupName: perm.permissionGroupName || "기타",
        perms: [],
        allIds: [],
      };
    }
    groups[gId].perms.push(perm);
    groups[gId].allIds.push(perm.permissionId);
  });

  // 💡 그룹 ID 순서대로 예쁘게 정렬해서 보여주기
  return Object.values(groups).sort((a, b) => {
    if (a.groupId === "etc_id") return 1;
    if (b.groupId === "etc_id") return -1;
    return Number(a.groupId) - Number(b.groupId);
  });
});

const activeGroupData = computed(() => {
  return (
    groupedPermissions.value.find((g) => g.groupId === activeGroupId.value) ||
    null
  );
});

const getSelectedCount = (group) => {
  return group.allIds.filter((id) => form.selectedPermissionIds.includes(id))
    .length;
};

// ── 시스템 전체 선택 로직 ──
const totalIds = computed(() =>
  allPermissions.value.map((p) => p.permissionId),
);

const isGlobalAllChecked = computed(() => {
  return (
    totalIds.value.length > 0 &&
    totalIds.value.every((id) => form.selectedPermissionIds.includes(id))
  );
});

const isGlobalIndeterminate = computed(() => {
  const count = form.selectedPermissionIds.length;
  return count > 0 && count < totalIds.value.length;
});

const handleGlobalToggle = (isChecked) => {
  if (isChecked) {
    form.selectedPermissionIds = [...totalIds.value];
  } else {
    form.selectedPermissionIds = [];
  }
};

// ── 현재 그룹 전체 선택 로직 ──
const isCurrentGroupAllChecked = computed(() => {
  if (!activeGroupData.value) return false;
  const ids = activeGroupData.value.allIds;
  return (
    ids.length > 0 && ids.every((id) => form.selectedPermissionIds.includes(id))
  );
});

const isCurrentGroupIndeterminate = computed(() => {
  if (!activeGroupData.value) return false;
  const ids = activeGroupData.value.allIds;
  const count = ids.filter((id) =>
    form.selectedPermissionIds.includes(id),
  ).length;
  return count > 0 && count < ids.length;
});

const handleCurrentGroupToggle = (isChecked) => {
  if (!activeGroupData.value) return;
  const ids = activeGroupData.value.allIds;

  if (isChecked) {
    ids.forEach((id) => {
      if (!form.selectedPermissionIds.includes(id))
        form.selectedPermissionIds.push(id);
    });
  } else {
    form.selectedPermissionIds = form.selectedPermissionIds.filter(
      (id) => !ids.includes(id),
    );
  }
};

// ── 개별 권한 체크 로직 ──
const togglePerm = (permissionId) => {
  if (form.selectedPermissionIds.includes(permissionId)) {
    form.selectedPermissionIds = form.selectedPermissionIds.filter(
      (id) => id !== permissionId,
    );
  } else {
    form.selectedPermissionIds.push(permissionId);
  }
};

// HTTP 뱃지 색상
const methodColor = (method) => {
  const map = {
    GET: "method-get",
    POST: "method-post",
    PUT: "method-put",
    DELETE: "method-delete",
  };
  return map[method?.toUpperCase()] ?? "method-default";
};

// ── 액션 로직 ──
const handleGoBack = () => router.push("/role");

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  submitting.value = true;
  try {
    const payload = {
      roleName: form.roleName,
      description: form.description,
      isActive: form.isActive,
      permissionIds: form.selectedPermissionIds,
    };

    console.log(payload);
    if (isEditMode.value) {
      payload.roleId = form.roleId;
      await admin.put(`/role/${form.roleId}`, payload);
    } else {
      await admin.post("/role", payload);
    }

    Swal.fire({
      icon: "success",
      title: isEditMode.value ? "수정 완료" : "등록 완료",
      text: "정상적으로 저장되었습니다.",
      timer: 1500,
      showConfirmButton: false,
    });

    router.push("/role");
  } catch (err) {
    Swal.fire({
      icon: "error",
      title: "처리 실패",
      text: "저장에 실패했습니다.",
    });
  } finally {
    submitting.value = false;
  }
};

onMounted(async () => {
  roleStore.getRoleList();
  isLoading.value = true;
  try {
    const permRes = await roleStore.getPermissionList();
    // 💡 스토어 반환값 구조에 맞춤
    allPermissions.value = permRes.data || permRes;

    // 💡 데이터 로드 후 첫 번째 탭(ID 기준)을 활성화
    if (groupedPermissions.value.length > 0) {
      activeGroupId.value = groupedPermissions.value[0].groupId;
    }

    if (isEditMode.value) {
      const roleId = route.params.id;
      const res = await admin.get(`/role/${roleId}`);
      const roleData = res.data;

      Object.assign(form, {
        roleId: roleData.roleId,
        roleName: roleData.roleName,
        description: roleData.description,
        isActive: roleData.isActive,
        selectedPermissionIds:
          roleData.permissions?.map((p) => p.permissionId) || [],
      });
    }
  } catch (err) {
    Swal.fire({
      icon: "error",
      title: "데이터 로드 실패",
      text: "정보를 불러오지 못했습니다.",
    });
    handleGoBack();
  } finally {
    isLoading.value = false;
  }
});
</script>

<style scoped>
.card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  padding: 32px;
}
.section-title {
  font-size: 16px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 16px;
}
.form-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

/* 좌우 분할 컨테이너 */
.split-container {
  display: flex;
  height: 480px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}

/* 왼쪽 탭 영역 */
.split-left {
  width: 220px;
  background: #f8fafc;
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
}
.split-left-header {
  padding: 14px 16px;
  font-size: 13px;
  font-weight: 700;
  color: #475569;
  border-bottom: 1px solid #e2e8f0;
  background: #f1f5f9;
}
.group-list {
  flex: 1;
  overflow-y: auto;
  list-style: none;
  padding: 0;
  margin: 0;
}
.group-item {
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  border-bottom: 1px solid #f1f5f9;
  transition: all 0.2s;
}
.group-item:hover {
  background: #f1f5f9;
}
.group-item.is-active {
  background: #eff6ff;
  border-right: 3px solid #2563eb;
}
.group-name {
  font-size: 13px;
  font-weight: 600;
  color: #334155;
}
.group-item.is-active .group-name {
  color: #1d4ed8;
}
.group-count-badge {
  font-size: 11px;
  font-weight: 600;
  color: #94a3b8;
  background: #e2e8f0;
  padding: 2px 6px;
  border-radius: 12px;
}
.group-count-badge.has-selected {
  background: #dbeafe;
  color: #2563eb;
}

/* 오른쪽 상세 영역 */
.split-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
}
.split-right-header {
  padding: 12px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e2e8f0;
  background: #fff;
}
.perm-grid {
  padding: 20px;
  overflow-y: auto;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  align-content: start;
}
.perm-item-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  height: fit-content;
}
.perm-item-card:hover {
  border-color: #cbd5e1;
  background: #f8fafc;
}
.perm-item-card.is-checked {
  border-color: #bfdbfe;
  background: #eff6ff;
}

/* HTTP 뱃지 */
.perm-method {
  font-size: 10px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 4px;
}
.method-get {
  background: #dcfce7;
  color: #15803d;
}
.method-post {
  background: #dbeafe;
  color: #1d4ed8;
}
.method-put {
  background: #fef9c3;
  color: #a16207;
}
.method-delete {
  background: #fee2e2;
  color: #b91c1c;
}
.method-default {
  background: #f3f4f6;
  color: #4b5563;
}

/* 푸터 버튼 */
.form-footer {
  margin-top: 32px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}
.btn-cancel {
  background: #fff;
  border: 1px solid #d1d5db;
  color: #374151;
}
.btn-submit {
  background: #2563eb;
  border: none;
  color: #fff;
  font-weight: 600;
  padding: 0 24px;
}
.btn-submit:hover {
  background: #1d4ed8;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #374151;
  padding-bottom: 4px;
}
:deep(.el-checkbox__label) {
  vertical-align: middle;
}
</style>
