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
          <!-- ── 헤더 ── -->
          <div class="sm:flex sm:justify-between sm:items-center mb-6">
            <div class="mb-4 sm:mb-0 flex items-center gap-3">
              <button
                class="p-1.5 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors border-none bg-transparent cursor-pointer"
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
                {{ isEditMode ? "그룹 수정" : "그룹 생성" }}
              </h1>
            </div>
          </div>

          <!-- ── 로딩 ── -->
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

          <div v-else>
            <!-- ── 탭 ── -->
            <div class="mb-6 border-b border-gray-200 dark:border-gray-700">
              <ul
                class="flex flex-wrap -mb-px text-sm font-medium text-center text-gray-500 dark:text-gray-400"
              >
                <li class="mr-2">
                  <a
                    href="#"
                    @click.prevent="activeTab = 'info'"
                    class="inline-flex p-4 rounded-t-lg border-b-2 transition-colors duration-200"
                    :class="
                      activeTab === 'info'
                        ? 'text-blue-600 border-blue-600'
                        : 'border-transparent hover:text-gray-600 hover:border-gray-300'
                    "
                  >
                    기본 정보 및 구성원
                  </a>
                </li>
                <!-- 직군그룹(C2 아님)일 때만 권한탭 표시 -->
                <li
                  v-if="form.groupType && form.groupType !== 'C2' && isAdmin"
                  class="mr-2"
                >
                  <a
                    href="#"
                    @click.prevent="activeTab = 'perms'"
                    class="inline-flex p-4 rounded-t-lg border-b-2 transition-colors duration-200"
                    :class="
                      activeTab === 'perms'
                        ? 'text-blue-600 border-blue-600'
                        : 'border-transparent hover:text-gray-600 hover:border-gray-300'
                    "
                  >
                    권한 설정
                  </a>
                </li>
              </ul>
            </div>

            <!-- ══════════════════════════════════
                 탭 1: 기본 정보 및 구성원
            ══════════════════════════════════ -->
            <div
              v-show="activeTab === 'info'"
              class="grid grid-cols-1 xl:grid-cols-2 gap-6"
            >
              <!-- 좌측 패널 -->
              <div
                class="bg-white dark:bg-gray-800 shadow-lg rounded-sm border border-gray-200 dark:border-gray-700/60 p-5 flex flex-col h-[650px]"
              >
                <!-- 그룹명 -->
                <div
                  class="mb-5 flex flex-col sm:flex-row sm:items-center gap-4"
                >
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
                      :class="
                        isNameValid ? 'text-emerald-500' : 'text-rose-500'
                      "
                    >
                      {{
                        isNameValid
                          ? "해당 그룹명은 사용 가능합니다."
                          : "이미 사용 중인 그룹명입니다."
                      }}
                    </p>
                    <!-- 수정 모드: 기존 이름과 동일할 때 안내 -->
                    <p
                      v-if="isEditMode && isOriginalName && !isNameChecked"
                      class="text-sm mt-1 text-gray-400"
                    >
                      기존 그룹명과 동일합니다.
                    </p>
                  </div>
                </div>

                <!-- 그룹유형 -->
                <div class="mb-5 flex items-center gap-4">
                  <label
                    class="font-semibold text-gray-800 dark:text-gray-100 min-w-[80px]"
                    >그룹유형</label
                  >

                  <!-- 수정 모드: 뱃지로 표시 (변경 불가) -->
                  <template v-if="isEditMode">
                    <el-tag
                      size="small"
                      effect="light"
                      round
                      class="font-medium"
                      :type="form.groupType === 'C2' ? 'primary' : 'warning'"
                    >
                      {{
                        form.groupType === "C2" ? "프로젝트 그룹" : "직군 그룹"
                      }}
                    </el-tag>
                    <span class="text-xs text-gray-400 dark:text-gray-500"
                      >※ 그룹 유형은 변경할 수 없습니다.</span
                    >
                  </template>

                  <!-- 생성 모드: 라디오 버튼으로 선택 -->
                  <template v-else>
                    <div class="flex items-center gap-4">
                      <label class="flex items-center gap-2 cursor-pointer">
                        <input
                          type="radio"
                          v-model="form.groupType"
                          value="C2"
                          class="form-radio text-blue-600 focus:ring-blue-500"
                        />
                        <el-tag
                          size="small"
                          effect="light"
                          round
                          type="primary"
                          class="font-medium cursor-pointer"
                        >
                          프로젝트 그룹
                        </el-tag>
                      </label>
                      <!-- ADMIN만 직군그룹 선택 가능 -->
                      <label
                        class="flex items-center gap-2"
                        :class="
                          isAdmin
                            ? 'cursor-pointer'
                            : 'opacity-40 cursor-not-allowed'
                        "
                      >
                        <input
                          type="radio"
                          v-model="form.groupType"
                          value="C1"
                          :disabled="!isAdmin"
                          class="form-radio text-orange-500 focus:ring-orange-400"
                        />
                        <el-tag
                          size="small"
                          effect="light"
                          round
                          type="warning"
                          class="font-medium"
                          :class="
                            isAdmin ? 'cursor-pointer' : 'cursor-not-allowed'
                          "
                        >
                          직군 그룹
                        </el-tag>
                      </label>
                      <span v-if="!isAdmin" class="text-xs text-gray-400"
                        >※ 직군 그룹은 ADMIN만 생성 가능합니다.</span
                      >
                    </div>
                  </template>
                </div>

                <!-- 그룹설명 -->
                <div
                  class="mb-5 flex flex-col sm:flex-row sm:items-start gap-4"
                >
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

                <!-- 사원 검색 패널 -->
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
                          class="p-1 text-gray-400 hover:text-gray-600 transition-colors border-none bg-transparent cursor-pointer"
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
                              v-html="
                                highlight(`${user.name} (${user.userId})`)
                              "
                            ></span>
                          </div>
                        </label>
                      </div>
                    </div>
                  </div>

                  <!-- 역할 부여 + 추가 버튼 -->
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
                        <span
                          class="text-xs text-gray-700 dark:text-gray-300"
                          >{{ role.roleName }}</span
                        >
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

              <!-- 우측 패널: 그룹 구성원 -->
              <div
                class="bg-white dark:bg-gray-800 shadow-lg rounded-sm border border-gray-200 dark:border-gray-700/60 p-5 flex flex-col h-[650px]"
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
                        <td class="px-2 py-3 text-gray-500">
                          {{
                            member.primaryGroupName || member.groupName || "-"
                          }}
                        </td>
                        <td v-if="form.groupType === 'C2'" class="px-2 py-3">
                          <select
                            v-model="member.roleId"
                            @change="onRoleChange(member)"
                            class="form-select text-xs py-1 bg-white border-gray-200 text-blue-600 focus:border-blue-500 font-medium"
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
                            class="text-rose-500 hover:text-rose-600 border-none bg-transparent cursor-pointer"
                            @click="removeMember(member.userId)"
                          >
                            <svg
                              class="w-4 h-4 fill-current"
                              viewBox="0 0 16 16"
                            >
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
              </div>
            </div>

            <!-- ══════════════════════════════════
                 탭 2: 권한 설정 (직군그룹 전용)
            ══════════════════════════════════ -->
            <div
              v-show="activeTab === 'perms'"
              class="bg-white dark:bg-gray-800 shadow-lg rounded-sm border border-gray-200 p-5 min-h-[650px]"
            >
              <div class="flex justify-between items-end mb-4">
                <div class="text-lg font-bold text-gray-800">
                  그룹 권한 세부 설정
                </div>
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
                  <div class="split-left-header">권한 그룹 분류</div>
                  <ul class="group-list">
                    <li
                      v-for="group in groupedPermissions"
                      :key="group.groupId"
                      class="group-item"
                      :class="{ 'is-active': activeGroupId === group.groupId }"
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
            </div>

            <!-- ── 하단 버튼 ── -->
            <div class="form-footer mt-8">
              <button
                class="btn btn-cancel px-6 py-2 rounded-lg"
                @click="goBack"
              >
                취소
              </button>
              <button
                class="btn btn-submit px-6 py-2 rounded-lg"
                @click="submitForm"
              >
                {{ isEditMode ? "수정 완료" : "그룹 생성" }}
              </button>
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
  name: "GroupForm",
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

    // ── 모드 판별 ──
    // route.params.id 가 있으면 수정, 없으면 생성
    const isEditMode = computed(() => !!route.params.id);
    const isAdmin = computed(() => authStore.user?.userType === "ADMIN");

    // ── 탭 ──
    const activeTab = ref("info");

    // ── 폼 상태 ──
    const form = ref({
      groupId: null,
      groupName: "",
      groupType: "C2", // 생성 모드 기본값: 프로젝트 그룹
      description: "",
      isActive: "Y",
      selectedPermissionIds: [],
    });

    // ── 수정 모드 전용: 중복 확인 / 스냅샷 ──
    const originalGroupName = ref("");
    const isNameChecked = ref(false);
    const isNameValid = ref(false);

    let originalFormSnapshot = "";
    let originalMembersSnapshot = "";
    let originalPermsSnapshot = "";

    const isOriginalName = computed(
      () =>
        isEditMode.value && form.value.groupName === originalGroupName.value,
    );

    const onGroupNameInput = () => {
      // 이름이 바뀌면 중복 확인 초기화
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
    const roles = ref([]);
    const selectedRole = ref(null);

    const filteredUsers = computed(() => {
      const q = userSearchQuery.value.trim().toLowerCase();
      return allUsers.value.filter((user) => {
        if (groupMembers.value.some((m) => m.userId === user.userId))
          return false;
        if (!q) return true;
        return (
          user.name.includes(q) ||
          String(user.userId).includes(q) ||
          user.groupName?.toLowerCase().includes(q)
        );
      });
    });

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
      // 수정 모드에서 기존 이름과 동일하면 통과 처리
      if (isEditMode.value && isOriginalName.value) {
        isNameValid.value = true;
        isNameChecked.value = true;
        return;
      }
      const res = await api.get(`/group/dup/${form.value.groupName}`);
      isNameValid.value = res.data === "Y";
      isNameChecked.value = true;
    };

    // ── 권한 스플릿 뷰 ──
    const allPermissions = ref([]);
    const activeGroupId = ref(null);

    const groupedPermissions = computed(() => {
      if (!Array.isArray(allPermissions.value)) return [];
      const groups = {};
      allPermissions.value.forEach((perm) => {
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
      return Object.values(groups).sort((a, b) => {
        if (a.groupId === "etc_id") return 1;
        if (b.groupId === "etc_id") return -1;
        return Number(a.groupId) - Number(b.groupId);
      });
    });

    const activeGroupData = computed(
      () =>
        groupedPermissions.value.find(
          (g) => g.groupId === activeGroupId.value,
        ) || null,
    );

    const getSelectedCount = (group) =>
      group.allIds.filter((id) => form.value.selectedPermissionIds.includes(id))
        .length;

    const totalIds = computed(() =>
      allPermissions.value.map((p) => p.permissionId),
    );

    const isGlobalAllChecked = computed(
      () =>
        totalIds.value.length > 0 &&
        totalIds.value.every((id) =>
          form.value.selectedPermissionIds.includes(id),
        ),
    );

    const isGlobalIndeterminate = computed(() => {
      const count = form.value.selectedPermissionIds.length;
      return count > 0 && count < totalIds.value.length;
    });

    const handleGlobalToggle = (isChecked) => {
      form.value.selectedPermissionIds = isChecked ? [...totalIds.value] : [];
    };

    const isCurrentGroupAllChecked = computed(() => {
      if (!activeGroupData.value) return false;
      const ids = activeGroupData.value.allIds;
      return (
        ids.length > 0 &&
        ids.every((id) => form.value.selectedPermissionIds.includes(id))
      );
    });

    const isCurrentGroupIndeterminate = computed(() => {
      if (!activeGroupData.value) return false;
      const ids = activeGroupData.value.allIds;
      const count = ids.filter((id) =>
        form.value.selectedPermissionIds.includes(id),
      ).length;
      return count > 0 && count < ids.length;
    });

    const handleCurrentGroupToggle = (isChecked) => {
      if (!activeGroupData.value) return;
      const ids = activeGroupData.value.allIds;
      if (isChecked) {
        ids.forEach((id) => {
          if (!form.value.selectedPermissionIds.includes(id))
            form.value.selectedPermissionIds.push(id);
        });
      } else {
        form.value.selectedPermissionIds =
          form.value.selectedPermissionIds.filter((id) => !ids.includes(id));
      }
    };

    const togglePerm = (permissionId) => {
      if (form.value.selectedPermissionIds.includes(permissionId)) {
        form.value.selectedPermissionIds =
          form.value.selectedPermissionIds.filter((id) => id !== permissionId);
      } else {
        form.value.selectedPermissionIds.push(permissionId);
      }
    };

    const methodColor = (method) => {
      const map = {
        GET: "method-get",
        POST: "method-post",
        PUT: "method-put",
        DELETE: "method-delete",
      };
      return map[method?.toUpperCase()] ?? "method-default";
    };

    // ── groupType 변경 시 권한 탭 자동 해제 ──
    watch(
      () => form.value.groupType,
      (type) => {
        if (type === "C2" && activeTab.value === "perms") {
          activeTab.value = "info";
        }
        // 구성원 역할 초기화 (프로젝트 → 직군 전환 시 roleId 불필요)
        if (type !== "C2") {
          groupMembers.value = groupMembers.value.map((m) => ({
            ...m,
            roleId: null,
            roleName: null,
          }));
        }
      },
    );

    // ── 제출 (생성 / 수정 통합) ──
    const submitForm = async () => {
      // 공통 유효성 검사
      if (!form.value.groupName.trim()) {
        Swal.fire({
          icon: "warning",
          title: "그룹명을 입력해주세요.",
          confirmButtonColor: "#2563eb",
        });
        return;
      }

      // 중복 확인 여부 체크
      // - 생성 모드: 반드시 확인 필요
      // - 수정 모드: 기존 이름이 아닌 경우에만 확인 필요
      const needsDupCheck = !isOriginalName.value;
      if (needsDupCheck && !isNameChecked.value) {
        Swal.fire({
          icon: "warning",
          title: "그룹명 중복 확인을 해주세요.",
          confirmButtonColor: "#2563eb",
        });
        return;
      }
      if (needsDupCheck && !isNameValid.value) {
        Swal.fire({
          icon: "warning",
          title: "이미 사용 중인 그룹명입니다.",
          confirmButtonColor: "#2563eb",
        });
        return;
      }

      // 프로젝트 그룹(C2)은 구성원 필수
      if (form.value.groupType === "C2" && groupMembers.value.length === 0) {
        Swal.fire({
          icon: "warning",
          title: "그룹 구성원을 최소 1명 이상 추가해주세요.",
          confirmButtonColor: "#2563eb",
        });
        return;
      }

      // 수정 모드: 변경 사항 없으면 조기 리턴
      if (isEditMode.value) {
        const currentFormStr = JSON.stringify({
          groupName: form.value.groupName,
          groupType: form.value.groupType,
          description: form.value.description,
        });
        const currentMembersStr = JSON.stringify(
          groupMembers.value.map((m) => ({
            userId: m.userId,
            roleId: m.roleId,
          })),
        );
        const currentPermsStr = JSON.stringify(
          [...form.value.selectedPermissionIds].sort(),
        );

        if (
          currentFormStr === originalFormSnapshot &&
          currentMembersStr === originalMembersSnapshot &&
          currentPermsStr === originalPermsSnapshot
        ) {
          Swal.fire({
            icon: "info",
            title: "변경 사항 없음",
            text: "수정된 내용이 없습니다.",
            confirmButtonColor: "#6b7280",
          });
          return;
        }
      }

      const payload = {
        groupName: form.value.groupName,
        groupType: form.value.groupType,
        description: form.value.description,
        editorUserId: authStore.user.userId,
        members: groupMembers.value.map((m) => ({
          userId: m.userId,
          roleId: form.value.groupType === "C2" ? m.roleId : null,
        })),
        permissionIds: form.value.selectedPermissionIds,
      };

      try {
        if (isEditMode.value) {
          // ── 수정 ──
          await api.put(`/group/${form.value.groupId}`, {
            ...payload,
            groupId: form.value.groupId,
          });
          Swal.fire({
            toast: true,
            position: "top-end",
            icon: "success",
            title: "그룹이 성공적으로 수정되었습니다.",
            showConfirmButton: false,
            timer: 2000,
          });
          router.push(`../../group/info/${form.value.groupId}`);
        } else {
          // ── 생성 ──
          const res = await api.post("/group", payload);
          const newGroupId = res.data?.groupId;
          Swal.fire({
            toast: true,
            position: "top-end",
            icon: "success",
            title: "그룹이 성공적으로 생성되었습니다.",
            showConfirmButton: false,
            timer: 2000,
          });
          router.push(newGroupId ? `../info/${newGroupId}` : "../group");
        }
      } catch {
        Swal.fire({
          icon: "error",
          title: isEditMode.value ? "수정 실패" : "생성 실패",
          text: `그룹 ${isEditMode.value ? "수정" : "생성"}에 실패했습니다. 잠시 후 다시 시도해주세요.`,
          confirmButtonColor: "#2563eb",
        });
      }
    };

    const goBack = () => router.back();

    // ── 초기 데이터 로드 ──
    onMounted(async () => {
      try {
        const [, , permRes] = await Promise.all([
          empStore.getEmpList4Group(),
          roleStore.getActiveRoleList(),
          // ADMIN인 경우에만 권한 리스트를 가져오고, 아니면 빈 배열 반환
          isAdmin.value ? roleStore.getPermissionList() : Promise.resolve([]),
        ]);

        allUsers.value = empStore.empList4Group;
        roles.value = roleStore.activeRoleList;
        if (roles.value.length > 0) selectedRole.value = roles.value[0].roleId;

        // ADMIN일 경우에만 권한 데이터를 매핑
        if (isAdmin.value) {
          allPermissions.value = permRes.data || permRes || [];
          if (groupedPermissions.value.length > 0) {
            activeGroupId.value = groupedPermissions.value[0].groupId;
          }
        }

        if (isEditMode.value) {
          // ── 수정 모드: 기존 데이터 로드 ──
          const data = await groupStore.getGroupInfo(route.params.id);

          // 🚨 [추가된 로직] ADMIN이 아닌 사용자가 직군그룹(C1)에 접근 시 403 차단
          if (data.groupType === "C1" && !isAdmin.value) {
            await Swal.fire({
              icon: "error",
              title: "403 Forbidden",
              text: "접근 권한이 없습니다. 직군 그룹은 ADMIN만 수정할 수 있습니다.",
              confirmButtonColor: "#2563eb",
            });
            router.back(); // 접근을 차단하고 이전 페이지로 돌려보냄
            return; // 렌더링을 중단
          }

          form.value = {
            groupId: data.groupId,
            groupName: data.groupName,
            groupType: data.groupType,
            description: data.description || "",
            isActive: data.isActive,
            selectedPermissionIds:
              data.permissions?.map((p) => p.permissionId) || [],
          };
          originalGroupName.value = data.groupName;
          groupMembers.value = (data.members || []).map((m) => ({
            ...m,
            isNew: false,
          }));

          // 스냅샷
          originalFormSnapshot = JSON.stringify({
            groupName: form.value.groupName,
            groupType: form.value.groupType,
            description: form.value.description,
          });
          originalMembersSnapshot = JSON.stringify(
            groupMembers.value.map((m) => ({
              userId: m.userId,
              roleId: m.roleId,
            })),
          );
          originalPermsSnapshot = JSON.stringify(
            [...form.value.selectedPermissionIds].sort(),
          );
        } else {
          // ── 생성 모드: ADMIN이 아니면 C2(프로젝트) 고정 ──
          form.value.groupType = "C2";
        }
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
    return {
      sidebarOpen,
      isLoading,
      isEditMode,
      isAdmin,
      form,
      activeTab,
      isOriginalName,
      isNameChecked,
      isNameValid,
      onGroupNameInput,
      checkDuplicate,
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
      submitForm,
      goBack,
      allPermissions,
      activeGroupId,
      groupedPermissions,
      activeGroupData,
      getSelectedCount,
      isGlobalAllChecked,
      isGlobalIndeterminate,
      handleGlobalToggle,
      isCurrentGroupAllChecked,
      isCurrentGroupIndeterminate,
      handleCurrentGroupToggle,
      togglePerm,
      methodColor,
    };
  },
};
</script>

<style scoped>
:global(.swal2-container) {
  z-index: 9999 !important;
}

input[type="checkbox"].form-checkbox:checked,
input[type="radio"].form-radio:checked {
  background-color: #2563eb !important;
  border-color: #2563eb !important;
  color: #2563eb !important;
}
input[type="checkbox"].form-checkbox:focus,
input[type="radio"].form-radio:focus {
  --tw-ring-color: #bfdbfe !important;
  border-color: #2563eb !important;
}

/* 권한 스플릿 뷰 */
.split-container {
  display: flex;
  height: 480px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}
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

.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
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
}
.btn-submit:hover {
  background: #1d4ed8;
}
</style>
