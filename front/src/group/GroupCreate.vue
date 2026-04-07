<template>
  <div class="flex h-[100dvh] overflow-hidden">
    <!-- 1. 사이드바 -->
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden bg-gray-50 dark:bg-gray-900"
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
                그룹 생성
              </h1>
            </div>
          </div>

          <!-- Grid Layout: Left(생성 폼) / Right(구성원 표) -->
          <div class="grid grid-cols-1 xl:grid-cols-2 gap-6">
            <!-- ===================== LEFT PANEL ===================== -->
            <div
              class="bg-white dark:bg-gray-800 shadow-lg rounded-sm border border-gray-200 dark:border-gray-700/60 p-5 flex flex-col h-[700px]"
            >
              <!-- 1. 그룹명 -->
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
                    />
                    <button
                      class="btn bg-indigo-500 hover:bg-indigo-600 text-white shrink-0"
                      @click="checkDuplicate"
                    >
                      중복 확인
                    </button>
                  </div>
                  <!-- 성공/실패 메시지 -->
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
                </div>
              </div>

              <!-- 2. 그룹 유형 -->
              <div class="mb-5 flex items-center gap-4">
                <label
                  class="font-semibold text-gray-800 dark:text-gray-100 min-w-[80px]"
                  >그룹유형</label
                >
                <div class="flex gap-4">
                  <label class="flex items-center gap-2 cursor-pointer">
                    <input
                      type="radio"
                      v-model="form.groupType"
                      value="C2"
                      class="form-radio text-indigo-500"
                    />
                    <span class="text-gray-700 dark:text-gray-300"
                      >프로젝트 그룹</span
                    >
                  </label>
                  <label class="flex items-center gap-2 cursor-pointer">
                    <input
                      type="radio"
                      v-model="form.groupType"
                      value="C1"
                      class="form-radio text-indigo-500"
                    />
                    <span class="text-gray-700 dark:text-gray-300"
                      >직군 그룹</span
                    >
                  </label>
                </div>
              </div>
              <!-- 그룹 설명 -->
              <div class="mb-5 flex flex-col sm:flex-row sm:items-start gap-4">
                <label
                  class="font-semibold text-gray-800 dark:text-gray-100 min-w-[80px] pt-2"
                >
                  그룹설명
                </label>
                <textarea
                  v-model="form.description"
                  class="form-textarea w-full bg-white dark:bg-gray-800 resize-none"
                  rows="2"
                  placeholder="그룹에 대한 설명을 입력하세요 (선택)"
                />
              </div>
              <!-- 3. 검색 및 조직도 영역 -->
              <div
                class="flex-1 flex flex-col border border-gray-200 dark:border-gray-700 rounded-sm overflow-hidden"
              >
                <!-- Search -->
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
                      d="M7 14c-3.86 0-7-3.14-7-7s3.14-7 7-7 7 3.14 7 7-3.14 7-7 7zM7 2C4.243 2 2 4.243 2 7s2.243 5 5 5 5-2.243 5-5-2.243-5-5-5z"
                    />
                    <path
                      d="M15.707 14.293L13.314 11.9a8.019 8.019 0 01-1.414 1.414l2.393 2.393a.997.997 0 001.414 0 .999.999 0 000-1.414z"
                    />
                  </svg>
                </div>

                <!-- User List -->
                <!-- User List (계층형 트리 구조) -->
                <div class="flex-1 overflow-y-auto p-2 space-y-2">
                  <div
                    v-if="groupedUsers.length === 0"
                    class="text-center py-4 text-sm text-gray-500"
                  >
                    검색 결과가 없습니다.
                  </div>

                  <!-- 그룹 반복 -->
                  <div
                    v-for="group in groupedUsers"
                    :key="group.groupName"
                    class="mb-1"
                  >
                    <!-- 부모(직군) 행 -->
                    <div
                      class="flex items-center justify-between p-2 bg-gray-100 dark:bg-gray-800/80 rounded border border-gray-200 dark:border-gray-700"
                    >
                      <div class="flex items-center gap-3">
                        <!-- 부모 체크박스 (전체 선택/해제) -->
                        <input
                          type="checkbox"
                          class="form-checkbox text-indigo-500 rounded-sm"
                          :checked="isGroupChecked(group)"
                          @change="toggleGroupSelection(group, $event)"
                        />
                        <!-- 텍스트 클릭 시 접기/펼치기 -->
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
                      <!-- 화살표 아이콘 -->
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
                          <!-- 위로 화살표 (접기) -->
                        </svg>
                        <svg
                          v-else
                          class="w-4 h-4 fill-current"
                          viewBox="0 0 16 16"
                        >
                          <path d="M8 11.4l-5.3-5.5.6-.6L8 10l4.7-4.7.6.6z" />
                          <!-- 아래로 화살표 (펼치기) -->
                        </svg>
                      </button>
                    </div>

                    <!-- 자식(사원) 리스트 (펼쳐졌을 때만 보임) -->
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

                <!-- 4. 역할 선택 및 추가 버튼 (직군 그룹일 땐 역할 숨김, 화살표는 우측 정렬) -->
                <div
                  class="border-t border-gray-200 dark:border-gray-700 p-3 bg-gray-50 dark:bg-gray-900/50 flex flex-col sm:flex-row items-start sm:items-center justify-end gap-3"
                >
                  <!-- 프로젝트 그룹일 때만 역할 선택 보이기 (mr-auto로 왼쪽 밀착) -->
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

                  <!-- 5. >> 넘기기 버튼 (우측 고정) -->
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

            <!-- ===================== RIGHT PANEL ===================== -->
            <div
              class="bg-white dark:bg-gray-800 shadow-lg rounded-sm border border-gray-200 dark:border-gray-700/60 p-5 flex flex-col h-[700px]"
            >
              <h2
                class="font-semibold text-xl text-gray-800 dark:text-gray-100 text-center mb-5"
              >
                그룹 구성원
              </h2>

              <!-- 6. 선택된 멤버 테이블 -->
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
                      <!-- 프로젝트 그룹일 때만 역할 헤더 표시 -->
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
                      <!-- 프로젝트면 5칸, 직군이면 4칸 병합 -->
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
                      <td class="px-2 py-3">{{ member.userId }}</td>
                      <td class="px-2 py-3">{{ member.name }}</td>
                      <td class="px-2 py-3">{{ member.groupName }}</td>
                      <!-- 프로젝트 그룹일 때만 역할 데이터 표시 -->
                      <td
                        v-if="form.groupType === 'C2'"
                        class="px-2 py-3 font-medium text-indigo-500"
                      >
                        {{ member.roleName }}
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

              <!-- 8. 최종 그룹 생성 버튼 -->
              <div class="mt-5 flex justify-end">
                <button
                  class="btn bg-indigo-900 hover:bg-indigo-800 text-white min-w-[120px]"
                  @click="submitGroup"
                >
                  그룹 생성
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
import { useRouter } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { useEmpStore } from "../stores/empSJW";
import { useRoleStore } from "../stores/roleSJW";
import { useAuthStore } from "../stores/auth";
import api from "../utils/api";
export default {
  name: "GroupCreate",
  components: { Header, Sidebar },
  setup() {
    const router = useRouter();
    const sidebarOpen = ref(false);
    const empStore = useEmpStore();
    const roleStore = useRoleStore();
    const authStore = useAuthStore();
    // ── 상태 변수 ──
    const form = ref({
      groupName: "",
      groupType: "C2",
      description: "",
    });

    const isNameChecked = ref(false);
    const isNameValid = ref(false);

    const userSearchQuery = ref("");
    const selectedLeftUsers = ref([]); // 왼쪽 창에서 체크된 유저들
    const selectedRole = ref("PM"); // 라디오 버튼 기본값
    const roles = ref([
      { roleId: 1, roleName: "PM" },
      { roleId: 2, roleName: "상PL" },
      { roleId: 3, roleName: "하PL" },
      { roleId: 4, roleName: "개발" },
      { roleId: 5, roleName: "QA" },
    ]);

    const groupMembers = ref([]); // 오른쪽 창으로 넘어간 구성원들

    // ── 임시 더미 데이터 (백엔드에서 사원 목록을 가져온다고 가정) ──
    const allUsers = ref([]);

    // ── Computed ──
    // 검색 + 이미 오른쪽에 추가된 멤버는 왼쪽 리스트에서 제외
    const filteredUsers = computed(() => {
      const q = userSearchQuery.value.trim().toLowerCase();
      return allUsers.value.filter((user) => {
        // 이미 구성원에 포함되었는지 확인
        const isAlreadyAdded = groupMembers.value.some(
          (m) => m.userId === user.userId,
        );
        if (isAlreadyAdded) return false;

        // 검색 필터
        if (!q) return true;
        return (
          user.name.includes(q) ||
          String(user.userId).includes(q) ||
          user.groupName.toLowerCase().includes(q)
        );
      });
    });

    // ── 함수 ──
    const checkDuplicate = async () => {
      if (!form.value.groupName.trim()) {
        alert("그룹명을 입력해주세요.");
        return;
      }
      const name = form.value.groupName;
      const response = await api.get(`/admin/group/dup/${name}`);
      const result = response.data;
      console.log(result);
      if (result == "Y") {
        isNameValid.value = true;
      } else {
        isNameValid.value = false;
      }
      isNameChecked.value = true;
    };

    watch(
      () => form.value.groupType,
      () => {
        groupMembers.value = [];
        selectedLeftUsers.value = [];
      },
    );
    const moveUsersToRight = () => {
      if (selectedLeftUsers.value.length === 0) return;

      const newMembers = selectedLeftUsers.value
        .map((userId) => {
          const user = allUsers.value.find((u) => u.userId === userId);
          if (!user) return null;

          // ✅ roleId로 roleName 찾아서 같이 저장
          const roleObj = roles.value.find(
            (r) => r.roleId === selectedRole.value,
          );

          return {
            userId: user.userId,
            name: user.name,
            groupName: user.groupName,
            roleId: form.value.groupType === "C2" ? selectedRole.value : null,
            roleName:
              form.value.groupType === "C2" ? (roleObj?.roleName ?? "-") : null,
          };
        })
        .filter(Boolean);

      groupMembers.value.push(...newMembers);
      selectedLeftUsers.value = [];
    };

    const removeMember = (userId) => {
      // 오른쪽 리스트에서 삭제 (삭제되면 자동으로 왼쪽 리스트에 다시 나타남)
      groupMembers.value = groupMembers.value.filter(
        (m) => m.userId !== userId,
      );
    };

    const submitGroup = async () => {
      if (!isNameValid.value) {
        alert("그룹명 중복 확인을 해주세요.");
        return;
      }
      if (form.value.groupType === "C2" && groupMembers.value.length === 0) {
        alert("그룹 구성원을 최소 1명 이상 추가해주세요.");
        return;
      }

      const payload = {
        groupName: form.value.groupName,
        groupType: form.value.groupType,
        description: form.value.description,
        members: groupMembers.value.map((m) => ({
          userId: m.userId,
          roleId: m.roleId,
        })),
        createdBy: authStore.user.userId,
      };

      console.log("Submit Data:", payload);
      const response = await api.post("/admin/group", payload);
      const result = response.data;
      if (result == "Y") {
        alert("그룹이 성공적으로 생성되었습니다.");
        goBack();
      } else {
        alert("그룹생성에 실패했습니다.");
      }
    };

    const goBack = () => {
      router.back(); // 라우터 설정에 맞게 변경하세요
    };
    // 1. 데이터를 직군(groupName)별로 묶어주는 Computed
    const groupedUsers = computed(() => {
      const groups = {};
      filteredUsers.value.forEach((user) => {
        if (!groups[user.groupName]) {
          groups[user.groupName] = [];
        }
        groups[user.groupName].push(user);
      });
      // 객체를 배열 형태로 변환 (템플릿에서 v-for 돌리기 쉽게)
      return Object.keys(groups).map((key) => ({
        groupName: key,
        users: groups[key],
      }));
    });

    // 2. 현재 펼쳐진 그룹 목록 관리
    const expandedGroups = ref([]); // 기본으로 열어둘 그룹 이름 지정 (빈 배열 [] 이면 모두 닫힘)

    const toggleGroup = (groupName) => {
      if (expandedGroups.value.includes(groupName)) {
        expandedGroups.value = expandedGroups.value.filter(
          (g) => g !== groupName,
        );
      } else {
        expandedGroups.value.push(groupName);
      }
    };

    // 💡 꿀팁: 사용자가 검색어를 입력하면 숨겨진 그룹을 자동으로 다 펼쳐줍니다!
    watch(userSearchQuery, (newVal) => {
      if (newVal.trim() !== "") {
        // 검색어 입력 시 매칭된 모든 그룹 펼치기
        expandedGroups.value = groupedUsers.value.map((g) => g.groupName);
      }
    });

    // 3. 부모 체크박스 (전체 선택/해제) 로직
    // 해당 직군의 '모든' 유저가 선택되었는지 확인
    const isGroupChecked = (group) => {
      if (group.users.length === 0) return false;
      return group.users.every((u) =>
        selectedLeftUsers.value.includes(u.userId),
      );
    };

    // 부모 체크박스를 눌렀을 때 실행
    const toggleGroupSelection = (group, event) => {
      const isChecked = event.target.checked;
      if (isChecked) {
        group.users.forEach((u) => {
          if (!selectedLeftUsers.value.includes(u.userId)) {
            selectedLeftUsers.value.push(u.userId); // ← userId만 push
          }
        });
      } else {
        const groupUserIds = group.users.map((u) => u.userId);
        selectedLeftUsers.value = selectedLeftUsers.value.filter(
          (userId) => !groupUserIds.includes(userId),
        );
      }
    };
    // ── 검색어 하이라이트 기능 ──
    const highlight = (text) => {
      const q = userSearchQuery.value.trim();
      if (!q || !text) return text; // 검색어가 없으면 그냥 원본 반환

      // 대소문자 구분 없이 검색어와 일치하는 부분을 <mark> 태그로 감싸줌
      const regex = new RegExp(`(${q})`, "gi");
      return String(text).replace(
        regex,
        '<mark class="bg-yellow-100 dark:bg-yellow-800 text-inherit rounded px-0.5">$1</mark>',
      );
    };
    onMounted(async () => {
      await empStore.getEmpList4Group();
      allUsers.value = empStore.empList4Group;
      await roleStore.getActiveRoleList();
      roles.value = roleStore.activeRoleList;
    });
    return {
      sidebarOpen,
      form,
      isNameChecked,
      isNameValid,
      userSearchQuery,
      filteredUsers,
      selectedLeftUsers,
      selectedRole,
      roles,
      groupMembers,
      checkDuplicate,
      moveUsersToRight,
      removeMember,
      submitGroup,
      goBack,
      groupedUsers,
      expandedGroups,
      toggleGroup,
      isGroupChecked,
      toggleGroupSelection,
      highlight,
    };
  },
};
</script>
