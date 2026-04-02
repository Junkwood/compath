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
              class="animate-spin w-8 h-8 text-indigo-500"
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
                      class="form-input w-full bg-white dark:bg-gray-800"
                      placeholder="그룹명을 입력하세요"
                      @input="onGroupNameInput"
                    />
                    <button
                      class="btn bg-indigo-500 hover:bg-indigo-600 text-white shrink-0"
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
                <span
                  :class="[
                    'inline-flex items-center px-2.5 py-0.5 rounded text-xs font-medium',
                    form.groupType === 'C2'
                      ? 'bg-violet-100 text-violet-800 dark:bg-violet-800/30 dark:text-violet-300'
                      : 'bg-blue-100 text-blue-800 dark:bg-blue-800/30 dark:text-blue-300',
                  ]"
                >
                  {{ form.groupType === "C2" ? "프로젝트 그룹" : "직군 그룹" }}
                </span>
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
                  class="form-textarea w-full bg-white dark:bg-gray-800 resize-none"
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
                          class="form-checkbox text-indigo-500 rounded-sm"
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
                            class="form-checkbox text-indigo-500"
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
                        class="form-radio text-indigo-500 w-3.5 h-3.5"
                      />
                      <span class="text-xs text-gray-700 dark:text-gray-300">{{
                        role.roleName
                      }}</span>
                    </label>
                  </div>
                  <button
                    class="btn bg-indigo-900 hover:bg-indigo-800 text-white p-2 shrink-0 self-end sm:self-auto"
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
                          class="form-select text-xs py-1 bg-white dark:bg-gray-800 border-gray-200 dark:border-gray-700 text-indigo-500 font-medium"
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
                  class="btn bg-indigo-900 hover:bg-indigo-800 text-white min-w-[120px]"
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
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useEmpStore } from "../stores/empSJW";
import { useRoleStore } from "../stores/roleSJW";
import { useGroupStore } from "../stores/groupSJW"; // 💡 GroupStore 추가
import { useAuthStore } from "../stores/auth"; // 💡 AuthStore 추가
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
    const groupStore = useGroupStore(); // 💡 초기화
    const authStore = useAuthStore(); // 💡 초기화

    // ── 폼 상태 ──
    const form = ref({
      groupId: null,
      groupName: "",
      groupType: "",
      description: "",
      isActive: "Y", // 기존 상태 보존용
    });

    const originalGroupName = ref("");
    const isNameChecked = ref(false);
    const isNameValid = ref(false);

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
        '<mark class="bg-yellow-100 dark:bg-yellow-800 text-inherit rounded px-0.5">$1</mark>',
      );
    };

    // ── 중복 확인 ──
    const checkDuplicate = async () => {
      if (!form.value.groupName.trim()) {
        alert("그룹명을 입력해주세요.");
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

    // ── 수정 제출 ──
    const submitUpdate = async () => {
      if (!isOriginalName.value && !isNameChecked.value) {
        alert("그룹명 중복 확인을 해주세요.");
        return;
      }
      if (!isOriginalName.value && !isNameValid.value) {
        alert("이미 사용 중인 그룹명입니다.");
        return;
      }
      if (form.value.groupType === "C2" && groupMembers.value.length === 0) {
        alert("그룹 구성원을 최소 1명 이상 추가해주세요.");
        return;
      }

      // 💡 백엔드 GroupDTOSJW 구조에 맞춘 페이로드 조립
      const payload = {
        groupId: form.value.groupId,
        groupName: form.value.groupName,
        groupType: form.value.groupType, // 타입 유지
        description: form.value.description,
        editorUserId: authStore.user.userId, // 💡 로그 기록용 작업자 ID 필수!
        members: groupMembers.value.map((m) => ({
          userId: m.userId,
          roleId: form.value.groupType === "C2" ? m.roleId : null,
        })),
      };

      try {
        await api.put(`/group`, payload);
        alert("그룹이 성공적으로 수정되었습니다.");
        router.push(`/admin/group/info/${form.value.groupId}`); // 상세 페이지로 복귀
      } catch {
        alert("그룹 수정에 실패했습니다. 잠시 후 다시 시도해주세요.");
      }
    };

    const goBack = () => router.back();

    // ── 초기 데이터 로드 ──
    onMounted(async () => {
      const groupId = route.params.id || route.params.groupId; // 라우터 파라미터 매칭
      try {
        // 1. 공통 코드성 데이터 로드 (사원 목록, 역할 목록)
        await Promise.all([
          empStore.getEmpList4Group(),
          roleStore.getRoleList(),
        ]);

        allUsers.value = empStore.empList4Group;
        roles.value = roleStore.roleList;
        if (roles.value.length > 0) selectedRole.value = roles.value[0].roleId;

        // 2. 💡 더미 데이터 대신 실제 DB에서 그룹 상세 정보 로드
        const data = await groupStore.getGroupInfo(groupId);

        // 3. 불러온 데이터 폼에 바인딩
        form.value = {
          groupId: data.groupId,
          groupName: data.groupName,
          groupType: data.groupType,
          description: data.description,
          isActive: data.isActive, // 기존 상태값 살려두기
        };
        originalGroupName.value = data.groupName;

        // 기존 멤버 목록 바인딩
        groupMembers.value = (data.members || []).map((m) => ({
          ...m,
          isNew: false,
        }));
      } catch {
        alert("그룹 정보를 불러오는 데 실패했습니다.");
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
