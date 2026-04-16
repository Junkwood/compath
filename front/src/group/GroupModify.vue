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
                그룹 수정
              </h1>
            </div>
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
            <div
              class="bg-white dark:bg-gray-800 shadow-lg rounded-sm border border-gray-200 dark:border-gray-700/60 p-5 flex flex-col h-[700px]"
            >
              <div class="mb-5 flex flex-col sm:flex-row sm:items-center gap-4">
                <label
                  class="font-semibold text-gray-800 dark:text-gray-100 min-w-[80px]"
                  >그룹명</label
                >
                <div class="flex-1">
                  <div class="flex gap-2">
                    <input
                      type="text"
                      v-model="form.groupName"
                      class="form-input w-full bg-white dark:bg-gray-800 focus:border-blue-500 focus:ring-blue-500"
                      placeholder="그룹명을 입력하세요"
                      @input="onGroupNameInput"
                    />
                    <button
                      class="btn bg-[#2563eb] hover:bg-blue-700 text-white shrink-0 border-none"
                      @click="checkDuplicate"
                    >
                      중복 확인
                    </button>
                  </div>
                  <p
                    v-if="isNameChecked"
                    class="text-sm mt-1"
                    :class="isNameValid ? 'text-emerald-500' : 'text-rose-500'"
                  >
                    {{
                      isNameValid
                        ? "해당 그룹명은 사용 가능합니다."
                        : "이미 사용 중인 그룹명입니다."
                    }}
                  </p>
                  <p
                    v-if="isOriginalName && !isNameChecked"
                    class="text-sm mt-1 text-gray-400"
                  >
                    기존 그룹명과 동일합니다.
                  </p>
                </div>
              </div>

              <div class="mb-5 flex items-center gap-4">
                <label
                  class="font-semibold text-gray-800 dark:text-gray-100 min-w-[80px]"
                  >그룹유형</label
                >
                <el-tag
                  size="small"
                  effect="light"
                  round
                  class="font-medium"
                  :type="form.groupType === 'C2' ? 'primary' : 'success'"
                >
                  {{ form.groupType === "C2" ? "프로젝트 그룹" : "직군 그룹" }}
                </el-tag>
                <span class="text-xs text-gray-400 dark:text-gray-500"
                  >※ 그룹 유형은 변경할 수 없습니다.</span
                >
              </div>

              <div class="mb-5 flex flex-col sm:flex-row sm:items-start gap-4">
                <label
                  class="font-semibold text-gray-800 dark:text-gray-100 min-w-[80px] pt-2"
                  >그룹설명</label
                >
                <textarea
                  v-model="form.description"
                  class="form-textarea w-full bg-white dark:bg-gray-800 resize-none focus:border-blue-500 focus:ring-blue-500"
                  rows="2"
                  placeholder="그룹에 대한 설명을 입력하세요 (선택)"
                />
              </div>

              <div
                class="flex-1 flex flex-col border border-gray-200 dark:border-gray-700 rounded-sm overflow-hidden"
              >
                <div
                  class="relative border-b border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-900/50"
                >
                  <input
                    type="text"
                    v-model="userSearchQuery"
                    class="form-input w-full pl-9 bg-transparent border-0 focus:ring-0"
                    placeholder="검색"
                  />
                  <svg
                    class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 fill-current text-gray-400"
                    viewBox="0 0 16 16"
                  >
                    <path
                      d="M7 14c-3.86 0-7-3.14-7-7s3.14-7 7-7 7 3.14 7 7-3.14 7-7 7zM7 2C4.243 2 2 4.243 2 7s2.243 5 5 5 5-2.243-5-5-5z"
                    />
                    <path
                      d="M15.707 14.293L13.314 11.9a8.019 8.019 0 01-1.414 1.414l2.393 2.393a.997.997 0 001.414 0 .999.999 0 000-1.414z"
                    />
                  </svg>
                </div>

                <div class="flex-1 overflow-y-auto p-2 space-y-2">
                  <div
                    v-if="groupedUsers.length === 0"
                    class="text-center py-4 text-sm text-gray-500"
                  >
                    검색 결과가 없습니다.
                  </div>

                  <div
                    v-for="group in groupedUsers"
                    :key="group.groupName"
                    class="mb-1"
                  >
                    <div
                      class="flex items-center justify-between p-2 bg-gray-100 dark:bg-gray-800/80 rounded border border-gray-200 dark:border-gray-700"
                    >
                      <div class="flex items-center gap-3">
                        <input
                          type="checkbox"
                          class="form-checkbox text-blue-600 rounded-sm focus:ring-blue-500"
                          :checked="isGroupChecked(group)"
                          @change="toggleGroupSelection(group, $event)"
                        />
                        <span
                          class="font-semibold text-gray-800 dark:text-gray-100 cursor-pointer select-none text-sm"
                          @click="toggleGroup(group.groupName)"
                        >
                          {{ group.groupName }}
                          <span class="text-gray-400 text-xs ml-1"
                            >({{ group.users.length }})</span
                          >
                        </span>
                      </div>
                      <button
                        class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors"
                        @click="toggleGroup(group.groupName)"
                      >
                        <svg
                          v-if="expandedGroups.includes(group.groupName)"
                          class="w-4 h-4 fill-current"
                          viewBox="0 0 16 16"
                        >
                          <path
                            d="M12.7 10.7L8 6l-4.7 4.7-.6-.6L8 4.6l5.3 5.5z"
                          />
                        </svg>
                        <svg
                          v-else
                          class="w-4 h-4 fill-current"
                          viewBox="0 0 16 16"
                        >
                          <path d="M8 11.4l-5.3-5.5.6-.6L8 10l4.7-4.7.6.6z" />
                        </svg>
                      </button>
                    </div>

                    <div
                      v-show="expandedGroups.includes(group.groupName)"
                      class="pl-8 pr-2 py-1 space-y-1 mt-1 border-l-2 border-gray-100 dark:border-gray-700 ml-3"
                    >
                      <label
                        v-for="user in group.users"
                        :key="user.userId"
                        class="flex items-center justify-between p-2 hover:bg-gray-50 dark:hover:bg-gray-800/50 rounded cursor-pointer transition-colors"
                      >
                        <div class="flex items-center gap-3">
                          <input
                            type="checkbox"
                            :value="user.userId"
                            v-model="selectedLeftUsers"
                            class="form-checkbox text-blue-600 focus:ring-blue-500"
                          />
                          <span
                            class="text-sm text-gray-700 dark:text-gray-300"
                            v-html="highlight(`${user.name} (${user.userId})`)"
                          ></span>
                        </div>
                      </label>
                    </div>
                  </div>
                </div>

                <div
                  class="border-t border-gray-200 dark:border-gray-700 p-3 bg-gray-50 dark:bg-gray-900/50 flex flex-col sm:flex-row items-start sm:items-center justify-end gap-3"
                >
                  <div
                    v-if="form.groupType === 'C2'"
                    class="flex items-center gap-3 flex-wrap mr-auto"
                  >
                    <span
                      class="text-sm font-semibold text-gray-600 dark:text-gray-400"
                      >역할부여:</span
                    >
                    <label
                      v-for="role in roles"
                      :key="role.roleId"
                      class="flex items-center gap-1 cursor-pointer"
                    >
                      <input
                        type="radio"
                        v-model="selectedRole"
                        :value="role.roleId"
                        class="form-radio text-blue-600 focus:ring-blue-500 w-3.5 h-3.5"
                      />
                      <span class="text-xs text-gray-700 dark:text-gray-300">{{
                        role.roleName
                      }}</span>
                    </label>
                  </div>
                  <button
                    class="btn bg-[#2563eb] hover:bg-blue-700 text-white p-2 shrink-0 self-end sm:self-auto border-none"
                    @click="moveUsersToRight"
                    :disabled="selectedLeftUsers.length === 0"
                  >
                    <svg class="w-4 h-4 fill-current" viewBox="0 0 24 24">
                      <path
                        d="M5 13h11.17l-4.88 4.88c-.39.39-.39 1.03 0 1.42.39.39 1.02.39 1.41 0l6.59-6.59c.39-.39.39-1.02 0-1.41l-6.58-6.6a.996.996 0 10-1.41 1.41L16.17 11H5c-.55 0-1 .45-1 1s.45 1 1 1z"
                      />
                    </svg>
                  </button>
                </div>
              </div>
            </div>

            <div
              class="bg-white dark:bg-gray-800 shadow-lg rounded-sm border border-gray-200 dark:border-gray-700/60 p-5 flex flex-col h-[700px]"
            >
              <h2
                class="font-semibold text-xl text-gray-800 dark:text-gray-100 text-center mb-5"
              >
                그룹 구성원
              </h2>

              <div
                class="flex-1 overflow-y-auto border border-gray-200 dark:border-gray-700 rounded-sm"
              >
                <table class="table-auto w-full dark:text-gray-300">
                  <thead
                    class="text-xs font-semibold uppercase text-gray-500 dark:text-gray-400 bg-gray-50 dark:bg-gray-900/20 border-b border-gray-200 dark:border-gray-700"
                  >
                    <tr>
                      <th class="px-2 py-3 text-center">ID</th>
                      <th class="px-2 py-3 text-center">이름</th>
                      <th class="px-2 py-3 text-center">직군그룹</th>
                      <th
                        v-if="form.groupType === 'C2'"
                        class="px-2 py-3 text-center"
                      >
                        역할
                      </th>
                      <th class="px-2 py-3 text-center">취소</th>
                    </tr>
                  </thead>
                  <tbody
                    class="text-sm divide-y divide-gray-200 dark:divide-gray-700/60"
                  >
                    <tr v-if="groupMembers.length === 0">
                      <td
                        :colspan="form.groupType === 'C2' ? 5 : 4"
                        class="px-2 py-8 text-center text-gray-400"
                      >
                        추가된 구성원이 없습니다.
                      </td>
                    </tr>
                    <tr
                      v-for="member in groupMembers"
                      :key="member.userId"
                      class="text-center"
                    >
                      <td class="px-2 py-3 font-medium text-sky-500">
                        #{{ member.userId }}
                      </td>
                      <td class="px-2 py-3">{{ member.name }}</td>
                      <td class="px-2 py-3 text-gray-500 dark:text-gray-400">
                        {{ member.primaryGroupName || member.groupName || "-" }}
                      </td>
                      <td v-if="form.groupType === 'C2'" class="px-2 py-3">
                        <select
                          v-model="member.roleId"
                          @change="onRoleChange(member)"
                          class="form-select text-xs py-1 bg-white dark:bg-gray-800 border-gray-200 dark:border-gray-700 text-blue-600 focus:border-blue-500 focus:ring-blue-500 font-medium"
                        >
                          <option
                            v-for="role in roles"
                            :key="role.roleId"
                            :value="role.roleId"
                          >
                            {{ role.roleName }}
                          </option>
                        </select>
                      </td>
                      <td class="px-2 py-3">
                        <button
                          class="text-rose-500 hover:text-rose-600"
                          @click="removeMember(member.userId)"
                        >
                          <svg class="w-4 h-4 fill-current" viewBox="0 0 16 16">
                            <path
                              d="M14.293 2.293a1 1 0 011.414 1.414L10.414 8l5.293 5.293a1 1 0 11-1.414 1.414L9 9.414l-5.293 5.293a1 1 0 01-1.414-1.414L7.586 8 2.293 2.707a1 1 0 011.414-1.414L9 6.586l5.293-5.293z"
                            />
                          </svg>
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <div class="mt-5 flex justify-end gap-2">
                <button
                  class="btn border border-gray-300 dark:border-gray-600 text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700"
                  @click="goBack"
                >
                  취소
                </button>
                <button
                  class="btn bg-[#2563eb] hover:bg-blue-700 text-white min-w-[120px] border-none"
                  @click="submitUpdate"
                >
                  수정 완료
                </button>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import Swal from "sweetalert2";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useEmpStore } from "../stores/empSJW";
import { useRoleStore } from "../stores/roleSJW";
import { useGroupStore } from "../stores/groupSJW";
import { useAuthStore } from "../stores/auth";
import api from "../utils/api";

export default {
  name: "GroupEdit",
  components: { Header, Sidebar },
  setup() {
    const router = useRouter();
    const route = useRoute();
    const sidebarOpen = ref(false);
    const isLoading = ref(true);
    const empStore = useEmpStore();
    const roleStore = useRoleStore();
    const groupStore = useGroupStore();
    const authStore = useAuthStore();

    // ── 폼 상태 ──
    const form = ref({
      groupId: null,
      groupName: "",
      groupType: "",
      description: "",
      isActive: "Y",
    });

    const originalGroupName = ref("");
    const isNameChecked = ref(false);
    const isNameValid = ref(false);

    // 💡 [추가] 변경사항 체크를 위한 원본 스냅샷 변수
    let originalFormSnapshot = "";
    let originalMembersSnapshot = "";

    const isOriginalName = computed(
      () => form.value.groupName === originalGroupName.value,
    );

    const onGroupNameInput = () => {
      if (!isOriginalName.value) {
        isNameChecked.value = false;
        isNameValid.value = false;
      }
    };

    // ── 사원 목록 ──
    const allUsers = ref([]);
    const userSearchQuery = ref("");
    const selectedLeftUsers = ref([]);
    const groupMembers = ref([]);

    // ── 역할 ──
    const roles = ref([]);
    const selectedRole = ref(null);

    // ── 검색 필터 ──
    const filteredUsers = computed(() => {
      const q = userSearchQuery.value.trim().toLowerCase();
      return allUsers.value.filter((user) => {
        const isAlreadyAdded = groupMembers.value.some(
          (m) => m.userId === user.userId,
        );
        if (isAlreadyAdded) return false;
        if (!q) return true;
        return (
          user.name.includes(q) ||
          String(user.userId).includes(q) ||
          user.groupName?.toLowerCase().includes(q)
        );
      });
    });

    // ── 트리 구조 ──
    const groupedUsers = computed(() => {
      const groups = {};
      filteredUsers.value.forEach((user) => {
        const key = user.groupName || "미배정";
        if (!groups[key]) groups[key] = [];
        groups[key].push(user);
      });
      return Object.keys(groups).map((key) => ({
        groupName: key,
        users: groups[key],
      }));
    });

    const expandedGroups = ref([]);

    const toggleGroup = (groupName) => {
      expandedGroups.value = expandedGroups.value.includes(groupName)
        ? expandedGroups.value.filter((g) => g !== groupName)
        : [...expandedGroups.value, groupName];
    };

    watch(userSearchQuery, (val) => {
      if (val.trim())
        expandedGroups.value = groupedUsers.value.map((g) => g.groupName);
    });

    const isGroupChecked = (group) => {
      if (group.users.length === 0) return false;
      return group.users.every((u) =>
        selectedLeftUsers.value.includes(u.userId),
      );
    };

    const toggleGroupSelection = (group, event) => {
      if (event.target.checked) {
        group.users.forEach((u) => {
          if (!selectedLeftUsers.value.includes(u.userId))
            selectedLeftUsers.value.push(u.userId);
        });
      } else {
        const ids = group.users.map((u) => u.userId);
        selectedLeftUsers.value = selectedLeftUsers.value.filter(
          (id) => !ids.includes(id),
        );
      }
    };

    // ── 구성원 이동 ──
    const moveUsersToRight = () => {
      if (selectedLeftUsers.value.length === 0) return;

      const roleObj = roles.value.find((r) => r.roleId === selectedRole.value);
      const newMembers = selectedLeftUsers.value
        .map((userId) => {
          const user = allUsers.value.find((u) => u.userId === userId);
          if (!user) return null;
          return {
            userId: user.userId,
            name: user.name,
            groupName: user.groupName,
            roleId: form.value.groupType === "C2" ? selectedRole.value : null,
            roleName:
              form.value.groupType === "C2" ? (roleObj?.roleName ?? "-") : null,
            isNew: true,
          };
        })
        .filter(Boolean);

      groupMembers.value.push(...newMembers);
      selectedLeftUsers.value = [];
    };

    const removeMember = (userId) => {
      groupMembers.value = groupMembers.value.filter(
        (m) => m.userId !== userId,
      );
    };

    const onRoleChange = (member) => {
      const roleObj = roles.value.find((r) => r.roleId === member.roleId);
      member.roleName = roleObj?.roleName ?? "-";
    };

    const highlight = (text) => {
      const q = userSearchQuery.value.trim();
      if (!q || !text) return text;
      const regex = new RegExp(`(${q})`, "gi");
      return String(text).replace(
        regex,
        '<mark class="bg-yellow-200 text-inherit rounded px-0.5">$1</mark>',
      );
    };

    // ── 중복 확인 ──
    const checkDuplicate = async () => {
      if (!form.value.groupName.trim()) {
        Swal.fire({
          icon: "warning",
          title: "그룹명을 입력해주세요.",
          confirmButtonColor: "#2563eb",
        });
        return;
      }
      if (isOriginalName.value) {
        isNameValid.value = true;
        isNameChecked.value = true;
        return;
      }
      const res = await api.get(`/group/dup/${form.value.groupName}`);
      isNameValid.value = res.data === "Y";
      isNameChecked.value = true;
    };

    // ── 💡 수정 제출 (변경 사항 체크 추가) ──
    const submitUpdate = async () => {
      // 💡 [핵심] 현재 상태와 스냅샷 비교 (기본 정보 + 멤버 목록)
      const currentFormStr = JSON.stringify(form.value);
      const currentMembersStr = JSON.stringify(
        // 멤버 배열에서 비교에 필요한 핵심 데이터(userId, roleId)만 뽑아서 비교
        groupMembers.value.map((m) => ({ userId: m.userId, roleId: m.roleId })),
      );

      if (
        currentFormStr === originalFormSnapshot &&
        currentMembersStr === originalMembersSnapshot
      ) {
        Swal.fire({
          icon: "info",
          title: "변경 사항 없음",
          text: "수정된 내용이 없습니다.",
          confirmButtonColor: "#6b7280",
        });
        return; // 변경된 게 없으면 함수 강제 종료!
      }

      // 기존 유효성 검사
      if (!isOriginalName.value && !isNameChecked.value) {
        Swal.fire({
          icon: "warning",
          title: "그룹명 중복 확인을 해주세요.",
          confirmButtonColor: "#2563eb",
        });
        return;
      }
      if (!isOriginalName.value && !isNameValid.value) {
        Swal.fire({
          icon: "warning",
          title: "이미 사용 중인 그룹명입니다.",
          confirmButtonColor: "#2563eb",
        });
        return;
      }
      if (form.value.groupType === "C2" && groupMembers.value.length === 0) {
        Swal.fire({
          icon: "warning",
          title: "그룹 구성원을 최소 1명 이상 추가해주세요.",
          confirmButtonColor: "#2563eb",
        });
        return;
      }

      const payload = {
        groupId: form.value.groupId,
        groupName: form.value.groupName,
        groupType: form.value.groupType,
        description: form.value.description,
        editorUserId: authStore.user.userId,
        members: groupMembers.value.map((m) => ({
          userId: m.userId,
          roleId: form.value.groupType === "C2" ? m.roleId : null,
        })),
      };

      try {
        await api.put(`/group/${form.value.groupId}`, payload);

        Swal.fire({
          toast: true,
          position: "top-end",
          icon: "success",
          title: "그룹이 성공적으로 수정되었습니다.",
          showConfirmButton: false,
          timer: 2000,
        });

        router.push(`/admin/group/info/${form.value.groupId}`);
      } catch {
        Swal.fire({
          icon: "error",
          title: "수정 실패",
          text: "그룹 수정에 실패했습니다. 잠시 후 다시 시도해주세요.",
          confirmButtonColor: "#2563eb",
        });
      }
    };

    const goBack = () => router.back();

    // ── 💡 초기 데이터 로드 (스냅샷 저장 추가) ──
    onMounted(async () => {
      const groupId = route.params.id || route.params.groupId;
      try {
        await Promise.all([
          empStore.getEmpList4Group(),
          roleStore.getActiveRoleList(),
        ]);

        allUsers.value = empStore.empList4Group;
        roles.value = roleStore.activeRoleList;
        if (roles.value.length > 0) selectedRole.value = roles.value[0].roleId;

        const data = await groupStore.getGroupInfo(groupId);

        form.value = {
          groupId: data.groupId,
          groupName: data.groupName,
          groupType: data.groupType,
          description: data.description || "", // null 방지
          isActive: data.isActive,
        };
        originalGroupName.value = data.groupName;

        groupMembers.value = (data.members || []).map((m) => ({
          ...m,
          isNew: false,
        }));

        // 💡 [핵심] 데이터 바인딩이 모두 끝난 후 스냅샷 찰칵!
        originalFormSnapshot = JSON.stringify(form.value);
        // 멤버는 이름이나 기타 UI용 데이터가 섞여있으므로 서버에 보낼 핵심 데이터만 추출해서 스냅샷 찍기
        originalMembersSnapshot = JSON.stringify(
          groupMembers.value.map((m) => ({
            userId: m.userId,
            roleId: m.roleId,
          })),
        );
      } catch {
        Swal.fire({
          icon: "error",
          title: "조회 실패",
          text: "그룹 정보를 불러오는 데 실패했습니다.",
          confirmButtonColor: "#2563eb",
        });
      } finally {
        isLoading.value = false;
      }
    });

    return {
      sidebarOpen,
      isLoading,
      form,
      isOriginalName,
      isNameChecked,
      isNameValid,
      onGroupNameInput,
      userSearchQuery,
      filteredUsers,
      groupedUsers,
      expandedGroups,
      toggleGroup,
      isGroupChecked,
      toggleGroupSelection,
      selectedLeftUsers,
      selectedRole,
      roles,
      groupMembers,
      moveUsersToRight,
      removeMember,
      onRoleChange,
      highlight,
      checkDuplicate,
      submitUpdate,
      goBack,
    };
  },
};
</script>

<style scoped>
/* SweetAlert2 모달 Z-index 방어용 */
:global(.swal2-container) {
  z-index: 9999 !important;
}

/* 💡 Tailwind Forms 플러그인 보라색 강제 오버라이드 */
input[type="checkbox"].form-checkbox:checked,
input[type="radio"].form-radio:checked {
  background-color: #2563eb !important; /* 체크 시 파란색 배경 */
  border-color: #2563eb !important; /* 테두리 파란색 */
  color: #2563eb !important; /* SVG 내부 채움색 파란색 */
}

/* 💡 클릭(포커스) 할 때 겉에 퍼지는 링(그림자) 색상도 파란색으로 통일 */
input[type="checkbox"].form-checkbox:focus,
input[type="radio"].form-radio:focus {
  --tw-ring-color: #bfdbfe !important; /* Tailwind의 blue-200 색상 */
  border-color: #2563eb !important;
}
</style>
