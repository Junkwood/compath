<template>
  <el-dialog
    :model-value="true"
    title=""
    width="600px"
    height="100"
    :close-on-click-modal="false"
    class="custom-dialog"
    @close="reset()"
  >
    <!-- ===================== LEFT PANEL ===================== -->
    <div
      class="bg-white dark:bg-gray-800 shadow-lg rounded-sm border border-gray-200 dark:border-gray-700/60 p-5 flex flex-col h-[700px]"
    >
      <!-- 2. 그룹 유형 -->

      <h2
        class="font-semibold text-xl text-gray-800 dark:text-gray-100 text-center mb-5"
      >
        알림 발송 대상
      </h2>

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
                  @click="toggleGroup(group.roleName)"
                >
                  {{ group.roleName }}
                  <span class="text-gray-400 text-xs ml-1"
                    >({{ group.users.length }})</span
                  >
                </span>
              </div>
              <!-- 화살표 아이콘 -->
              <button
                class="p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300 transition-colors"
                @click="toggleGroup(group.roleName)"
              >
                <svg
                  v-if="expandedGroups.includes(group.roleName)"
                  class="w-4 h-4 fill-current"
                  viewBox="0 0 16 16"
                >
                  <path d="M12.7 10.7L8 6l-4.7 4.7-.6-.6L8 4.6l5.3 5.5z" />
                  <!-- 위로 화살표 (접기) -->
                </svg>
                <svg v-else class="w-4 h-4 fill-current" viewBox="0 0 16 16">
                  <path d="M8 11.4l-5.3-5.5.6-.6L8 10l4.7-4.7.6.6z" />
                  <!-- 아래로 화살표 (펼치기) -->
                </svg>
              </button>
            </div>

            <!-- 자식(사원) 리스트 (펼쳐졌을 때만 보임) -->
            <div
              v-show="expandedGroups.includes(group.roleName)"
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
                    v-html="highlight(`${user.userName} (${user.userId})`)"
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

          <!-- 5. >> 넘기기 버튼 (우측 고정) -->
          <button
            @click="insertAlarmMem"
            class="btn bg-indigo-900 hover:bg-indigo-800 text-white p-2 shrink-0 self-end sm:self-auto"
          >
            추가
          </button>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, onMounted, defineProps, defineEmits } from "vue";
import { useRoleStore } from "../stores/roleSJW";

const props = defineProps({
  memberList: Array,
  alarmList: Array,
});
const emit = defineEmits(["memberInsert"]);

const roleStore = useRoleStore();

const userSearchQuery = ref("");
const selectedLeftUsers = ref([]); // 왼쪽 창에서 체크된 유저들
const roles = ref([
  { roleId: 1, roleName: "PM" },
  { roleId: 2, roleName: "상PL" },
  { roleId: 3, roleName: "하PL" },
  { roleId: 4, roleName: "개발" },
  { roleId: 5, roleName: "QA" },
]);

// ── 임시 더미 데이터 (백엔드에서 사원 목록을 가져온다고 가정) ──
const allUsers = ref([]); // 직군그룹

// ── Computed ──
// 검색 + 이미 오른쪽에 추가된 멤버는 왼쪽 리스트에서 제외
const filteredUsers = computed(() => {
  const q = userSearchQuery.value.trim().toLowerCase();
  return allUsers.value.filter(user => {
    // 검색 필터
    if (!q) {
      expandedGroups.value.length = 0;
      return true;
    }

    return user.userName.includes(q) || String(user.userId).includes(q);
  });
});

// ── 함수 ──

watch(
  () => props.memberList,
  newVal => {
    allUsers.value = newVal;
  },
);

watch(
  () => props.alarmList,
  newVal => {
    console.log("변화감지");
    allUsers.value = props.memberList;

    allUsers.value = allUsers.value.filter(
      user => !newVal.some(alarm => alarm.userId === user.userId),
    );
  },
  { deep: true },
);

// 1. 데이터를 직군(groupName)별로 묶어주는 Computed
const groupedUsers = computed(() => {
  const groups = {};

  filteredUsers.value.forEach(user => {
    if (!groups[user.roleName]) {
      groups[user.roleName] = [];
    }
    groups[user.roleName].push(user);
  });
  // 객체를 배열 형태로 변환 (템플릿에서 v-for 돌리기 쉽게)
  return Object.keys(groups).map(key => ({
    roleName: key,
    users: groups[key],
  }));
});

// 2. 현재 펼쳐진 그룹 목록 관리
const expandedGroups = ref([]); // 기본으로 열어둘 그룹 이름 지정 (빈 배열 [] 이면 모두 닫힘)

const toggleGroup = groupName => {
  if (expandedGroups.value.includes(groupName)) {
    expandedGroups.value = expandedGroups.value.filter(g => g !== groupName);
  } else {
    expandedGroups.value.push(groupName);
  }
};

// 💡 꿀팁: 사용자가 검색어를 입력하면 숨겨진 그룹을 자동으로 다 펼쳐줍니다!
watch(userSearchQuery, newVal => {
  if (newVal.trim() !== "") {
    // 검색어 입력 시 매칭된 모든 그룹 펼치기
    expandedGroups.value = groupedUsers.value.map(g => g.roleName);
  }
});

// 3. 부모 체크박스 (전체 선택/해제) 로직
// 해당 직군의 '모든' 유저가 선택되었는지 확인
const isGroupChecked = group => {
  if (group.users.length === 0) return false;
  return group.users.every(u => selectedLeftUsers.value.includes(u.userId));
};

// 부모 체크박스를 눌렀을 때 실행
const toggleGroupSelection = (group, event) => {
  const isChecked = event.target.checked;

  if (isChecked) {
    group.users.forEach(u => {
      if (!selectedLeftUsers.value.includes(u.userId)) {
        selectedLeftUsers.value.push(u.userId); // ← userId만 push
      }
    });
  } else {
    const groupUserIds = group.users.map(u => u.userId);
    selectedLeftUsers.value = selectedLeftUsers.value.filter(
      userId => !groupUserIds.includes(userId),
    );
  }
};

// ── 검색어 하이라이트 기능 ──
const highlight = text => {
  const q = userSearchQuery.value.trim();
  if (!q || !text) return text; // 검색어가 없으면 그냥 원본 반환

  // 대소문자 구분 없이 검색어와 일치하는 부분을 <mark> 태그로 감싸줌
  const regex = new RegExp(`(${q})`, "gi");
  return String(text).replace(
    regex,
    '<mark class="bg-yellow-100 dark:bg-yellow-800 text-inherit rounded px-0.5">$1</mark>',
  );
};

// 추가버튼
const insertAlarmMem = () => {
  let alarmMember = [];
  props.memberList.filter(m => {
    selectedLeftUsers.value.forEach(num => {
      if (m.userId == num) {
        alarmMember.push(m);
      }
    });
  });

  emit("memberInsert", alarmMember);
};

// 초기화
const reset = () => {
  selectedLeftUsers.value.length = 0;
};

onMounted(async () => {
  await roleStore.getActiveRoleList();
  roles.value = roleStore.activeRoleList;
});
</script>
<style>
.swal2-container {
  z-index: 9999 !important;
}
.custom-dialog {
  --el-dialog-padding-primary: 0px !important;
}
</style>
