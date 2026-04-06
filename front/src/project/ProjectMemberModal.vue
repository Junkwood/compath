<template>
  <el-dialog
    v-model="visible"
    title="구성원 추가"
    width="1200"
    height="100"
    :close-on-click-modal="false"
  >
    <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
      <!-- Grid Layout: Left(생성 폼) / Right(구성원 표) -->
      <div class="grid grid-cols-1 xl:grid-cols-2 gap-6">
        <!-- ===================== LEFT PANEL ===================== -->
        <div
          class="bg-white dark:bg-gray-800 shadow-lg rounded-sm border border-gray-200 dark:border-gray-700/60 p-5 flex flex-col h-[700px]"
        >
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
                <span class="text-gray-700 dark:text-gray-300">직군 그룹</span>
              </label>
            </div>
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
                      <path d="M12.7 10.7L8 6l-4.7 4.7-.6-.6L8 4.6l5.3 5.5z" />
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
                v-if="form.groupType === 'C1'"
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
                    :disabled="role.checked"
                    class="form-radio text-indigo-500 w-3.5 h-3.5"
                  />
                  <span class="text-xs text-gray-700 dark:text-gray-300">{{
                    role.roleName
                  }}</span>
                </label>
              </div>

              <!-- 5. >> 넘기기 버튼 (우측 고정) -->
              <button
                @click="moveUsersToRight"
                class="btn bg-indigo-900 hover:bg-indigo-800 text-white p-2 shrink-0 self-end sm:self-auto"
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
                  <th class="px-2 py-3 text-center">그룹</th>
                  <th class="px-2 py-3 text-center">역할</th>
                  <th class="px-2 py-3 text-center">취소</th>
                </tr>
              </thead>
              <tbody
                class="text-sm divide-y divide-gray-200 dark:divide-gray-700/60"
              >
                <tr v-if="groupMembers.length === 0">
                  <!-- 프로젝트면 5칸, 직군이면 4칸 병합 -->
                  <td :colspan="5" class="px-2 py-8 text-center text-gray-400">
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
                  <td class="px-2 py-3 font-medium text-indigo-500">
                    <span v-for="(role, index) in member.roleName" key="index"
                      >{{ role }}
                    </span>
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
              v-if="groupMembers.length !== 0"
              class="btn bg-indigo-900 hover:bg-indigo-800 text-white min-w-[120px]"
              @click="memberInserting"
            >
              구성원 추가
            </button>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, onMounted, defineProps, defineEmits } from "vue";
import { useRoleStore } from "../stores/roleSJW";
import { useAuthStore } from "../stores/auth";
import { useProjectKJHStore } from "../stores/projectKJH";
import api from "../utils/api";
import Swal from "sweetalert2";

const props = defineProps({
  memberList: Array,
  generalGroupList: Array,
  projectGroupList: Array,
});
const emit = defineEmits(["memberInsert"]);

const roleStore = useRoleStore();
const authStore = useAuthStore();
const projectStore = useProjectKJHStore();

// ── 상태 변수 ──
const form = ref({
  groupName: "",
  groupType: "C1",
  description: "",
});

const userSearchQuery = ref("");
const selectedLeftUsers = ref([]); // 왼쪽 창에서 체크된 유저들
const selectedRole = ref(); // 라디오 버튼 기본값
const roles = ref([
  { roleId: 1, roleName: "PM" },
  { roleId: 2, roleName: "상PL" },
  { roleId: 3, roleName: "하PL" },
  { roleId: 4, roleName: "개발" },
  { roleId: 5, roleName: "QA" },
]);

const groupMembers = ref([]); // 오른쪽 창으로 넘어간 구성원들

// ── 임시 더미 데이터 (백엔드에서 사원 목록을 가져온다고 가정) ──
const allUsers = ref([]); // 직군그룹
const allPjGroupMem = ref([]); // 프로젝트 그룹
let nowGroup = computed(() => {
  return form.value.groupType == "C1" ? allUsers.value : allPjGroupMem.value; // 현재 선택된 그룹 유형
});
// ── Computed ──
// 검색 + 이미 오른쪽에 추가된 멤버는 왼쪽 리스트에서 제외
const filteredUsers = computed(() => {
  const q = userSearchQuery.value.trim().toLowerCase();
  return nowGroup.value.filter((user) => {
    // 이미 구성원에 포함되었는지 확인
    const isAlreadyAdded = groupMembers.value.some((m) => {
      console.log(m);
      console.log(user);
      if (m.userId === user.userId) {
        return true;
      }
    });
    if (isAlreadyAdded) return false;

    // 검색 필터
    if (!q) {
      expandedGroups.value.length = 0;
      return true;
    }

    return (
      user.name.includes(q) ||
      String(user.userId).includes(q) ||
      user.groupName.toLowerCase().includes(q)
    );
  });
});

// ── 함수 ──

watch(
  () => form.value.groupType,
  () => {
    selectedRole.value = null;
    selectedLeftUsers.value = [];
  },
);

watch(
  () => [props.generalGroupList, props.projectGroupList, props.memberList],
  (newVal) => {
    console.log("새 그룹들: ", newVal);
    allUsers.value = newVal[0];
    allPjGroupMem.value = newVal[1];

    props.memberList.forEach((mem) => {
      // 이미 있는 구성원 제외
      allUsers.value = allUsers.value.filter((user) => {
        return user.userId != mem.userId;
      });

      allPjGroupMem.value = allPjGroupMem.value.filter((user) => {
        return !(user.userId == mem.userId && user.roleId == mem.roleId);
      });
    });
  },
);

// 우측 컴포넌트로 이동 함수
const moveUsersToRight = () => {
  let id = 0;
  if (selectedLeftUsers.value.length === 0) {
    alert("그룹 구성원을 최소 1명 이상 선택해주세요.");
    return;
  }

  if (form.value.groupType == "C1") {
    if (selectedRole.value == null || selectedRole.value == "") {
      alert("역할을 선택해주세요");
      return;
    }
  }

  let newMembers = selectedLeftUsers.value.map((userId) => {
    let user = null;
    let roleObj = null;

    user = nowGroup.value.find((u) => u.userId === userId);
    console.log(user);
    roleObj = roles.value.find(
      (r) =>
        r.roleId ==
        (form.value.groupType == "C1" ? selectedRole.value : user.roleId),
    );

    if (!user) return null;

    let count = 0;

    return {
      userId: user.userId,
      name: user.name,
      groupName: user.groupName,
      roleId: form.value.groupType === "C1" ? selectedRole.value : user.roleId,
      roleName:
        form.value.groupType === "C1" ? roleObj.roleName : user.roleName,

      groupType: user.groupType,
    };
  });

  selectedLeftUsers.value = [];
  selectedRole.value = null;
  newMembers = newMembers.filter((n) => n !== null);
  newMembers.length > 0 ? groupMembers.value.push(...newMembers) : "";
};

const removeMember = (userId) => {
  // 오른쪽 리스트에서 삭제 (삭제되면 자동으로 왼쪽 리스트에 다시 나타남)
  groupMembers.value = groupMembers.value.filter((m) => m.userId !== userId);
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
    expandedGroups.value = expandedGroups.value.filter((g) => g !== groupName);
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
  return group.users.every((u) => selectedLeftUsers.value.includes(u.userId));
};

// 부모 체크박스를 눌렀을 때 실행
const toggleGroupSelection = (group, event) => {
  console.log(group);
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

// 초기화
const reset = () => {
  roles.value.forEach((ro) => {
    ro.checked = false;
  });

  groupMembers.value.length = 0;
};

// 구성원 추가
const memberInserting = async () => {
  const result = await Swal.fire({
    title: "정말 구성원을 추가하시겠습니까?",
    text: "추가된 구성원은 구성원 목록에서 확인 가능합니다.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "추가",
    cancelButtonText: "취소",
    reverseButtons: true,
  });

  if (!result.isConfirmed) return;

  emit("memberInsert", groupMembers.value);
  reset();
};
onMounted(async () => {
  await projectStore.getGeneralGroupMem();
  allUsers.value = projectStore.generalGroupMem;

  await roleStore.getRoleList();
  roles.value = roleStore.roleList;

  // 프로젝트 그룹 멤버들 조회
  await projectStore.getProjectGroupMem();
  allPjGroupMem.value = projectStore.projectGroupMem;

  props.memberList.forEach((mem) => {
    // 이미 있는 구성원 제외
    allUsers.value = allUsers.value.filter((user) => {
      return user.userId != mem.userId;
    });

    allPjGroupMem.value = allPjGroupMem.value.filter((user) => {
      return !(user.userId == mem.userId && user.roleId == mem.roleId);
    });
  });
});
</script>
<style>
.swal2-container {
  z-index: 9999 !important;
}
</style>
