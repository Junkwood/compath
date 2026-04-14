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
          <!-- 제목 -->
          <div class="mb-6 proj-title-row flex justify-between">
            <div class="proj-title-left">
              <h2
                class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
              >
                프로젝트 공지사항
              </h2>

              <div class="proj-name-row">
                <span class="proj-name">【 {{ name }} 】</span>
                <span class="proj-period">
                  {{ projectStartDate }} ~ {{ projectendDate }}
                </span>
              </div>
            </div>

            <div class="self-end">
              <el-button class="new-project-btn" @click="goResister()">
                + &nbsp; 공지 생성
              </el-button>
            </div>
          </div>

          <!-- 검색 필터 -->
          <div class="filter-card mt-4 mb-0">
            <div class="filter-row">
              <div class="filter-item">
                <label class="filter-label">작성자</label>
                <div class="select-wrap">
                  <select v-model="filteredList.userId">
                    <option value="">전체</option>
                    <option
                      :value="user.userId"
                      v-for="user in filterList.userList"
                      :key="user.userId"
                    >
                      {{ user.userName }}
                    </option>
                  </select>
                  <span class="select-arrow">▾</span>
                </div>
              </div>

              <div class="filter-item">
                <label class="filter-label">카테고리</label>
                <div class="select-wrap">
                  <select v-model="filteredList.category">
                    <option value="">전체</option>
                    <option
                      :value="category.roleId"
                      v-for="category in filterList.categoryList"
                      :key="category.roleId"
                    >
                      {{ category.roleName }}
                    </option>
                  </select>
                  <span class="select-arrow">▾</span>
                </div>
              </div>

              <div class="filter-item">
                <label class="filter-label">시작일</label>
                <input
                  v-model="filteredList.startDate"
                  type="date"
                  class="filter-input"
                />
              </div>

              <div class="filter-item">
                <label class="filter-label">종료일</label>
                <input
                  v-model="filteredList.endDate"
                  type="date"
                  class="filter-input"
                />
              </div>
            </div>

            <div class="filter-item filter-item--wide mt-3">
              <label class="filter-label">검색어</label>
              <div class="search-wrap">
                <svg class="search-icon" viewBox="0 0 20 20" fill="none">
                  <circle
                    cx="9"
                    cy="9"
                    r="6"
                    stroke="#9ca3af"
                    stroke-width="1.8"
                  />
                  <path
                    d="M14 14l3 3"
                    stroke="#9ca3af"
                    stroke-width="1.8"
                    stroke-linecap="round"
                  />
                </svg>
                <input
                  v-model="filteredList.search"
                  type="text"
                  placeholder="제목을 입력해주세요."
                  class="search-input"
                  @keyup.enter="searchInfo()"
                />
              </div>

              <div class="filter-actions flex flex-row-reverse">
                <button type="button" @click="searchInfo()" class="btn-search">
                  검색
                </button>
                <button type="button" @click="resetForm()" class="btn-reset">
                  초기화
                </button>
              </div>
            </div>
          </div>

          <!-- 목록 -->
          <div
            class="col-span-full xl:col-span-8 bg-white dark:bg-gray-800 shadow-xs rounded-xl mt-4 notice-board-wrap"
          >
            <div class="flex flex-row-reverse items-center px-5 pt-3 pb-2">
              <span class="count-badge flex flex">총 {{ listLength }}건</span>
            </div>

            <table class="notice-table w-full dark:text-gray-300">
              <thead
                class="text-xs uppercase text-gray-400 dark:text-gray-500 bg-gray-50 dark:bg-gray-700/50 rounded-xs"
              >
                <tr>
                  <th class="col-num">번호</th>
                  <th class="col-title">제목</th>
                  <th class="col-writer">작성자</th>
                  <th class="col-date">등록일</th>
                </tr>
              </thead>

              <tbody
                class="text-sm font-medium divide-y divide-gray-100 dark:divide-gray-700/60"
              >
                <tr v-if="listLoading">
                  <td colspan="4" class="text-center py-10">
                    <h5 class="text-gray-500">⌛ 로딩중입니다.</h5>
                  </td>
                </tr>

                <template v-else-if="!listLoading && listLength > 0">
                  <tr
                    v-for="notice in pagingList"
                    :key="notice.noticeId"
                    @click="goDetail(notice)"
                    class="notice-row cursor-pointer"
                    :class="[
                      notice.isPinned === 'B1' ? 'pinned-row' : '',
                      notice.isDeleted === 'O1'
                        ? 'grayscale blur-[4px] opacity-60'
                        : '',
                    ]"
                  >
                    <td class="col-num">
                      <div class="num-cell">
                        <span
                          v-if="notice.isPinned === 'B1'"
                          class="pin-badge"
                        >
                          알림
                        </span>
                        <span v-else>{{ notice.num }}</span>
                      </div>
                    </td>

                    <td class="col-title">
                      <div class="title-cell">
                        <div class="title-line">
                          <span
                            v-if="notice.roleName"
                            class="category-badge"
                          >
                            {{ notice.roleName }}
                          </span>

                          <span class="notice-title-text">
                            {{ notice.title }}
                          </span>

                          <span
                            v-if="notice.isPinned === 'B1'"
                            class="fixed-badge"
                          >
                            중요
                          </span>

                          <span
                            class="lock-icon"
                            v-if="notice.isDeleted == 'B1'"
                          >
                            <el-icon><Lock /></el-icon>
                          </span>
                        </div>
                      </div>
                    </td>

                    <td class="col-writer">
                      <div class="writer-cell">{{ notice.userName }}</div>
                    </td>

                    <td class="col-date">
                      <div class="date-cell">{{ notice.createdAt }}</div>
                    </td>
                  </tr>
                </template>

                <tr v-else>
                  <td colspan="4" class="text-center py-10">
                    <h5 class="text-gray-500">공지사항이 존재하지 않습니다.</h5>
                  </td>
                </tr>
              </tbody>
            </table>

            <div class="pagination-wrap">
              <el-pagination
                :current-page="nowPage"
                :page-size="listNum"
                :total="listLength"
                :hide-on-single-page="real"
                @current-change="handleCurrentChange"
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
import { onBeforeMount } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ref } from "vue";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { usetaskKJHStore } from "../stores/taksKJH";
import { useNoticeStore } from "../stores/notice";
import Swal from "sweetalert2";
import { Lock } from "@element-plus/icons-vue";

const route = useRoute();
const router = useRouter();
const taskStore = usetaskKJHStore();
const noticeStore = useNoticeStore();

const projectId = route.params.projectId;
const subId = route.params.subProjectId;
const sidebarOpen = ref(false);
const listLoading = ref(false);

const filteredList = ref({
  category: "",
  userId: "",
  startDate: "",
  endDate: "",
  search: "",
});

const pagingList = ref([]);
const searchKeyword = ref("");
const filterList = ref([]);
const listLength = ref(0);
const workPage = ref(1);
const listNum = ref(10);
const nowPage = ref(1);
const real = ref(true);

let name = ref();
let projectStartDate = ref();
let projectendDate = ref();

const searchInfo = async () => {
  let search =
    filteredList.value.category == "" &&
    filteredList.value.userId == "" &&
    filteredList.value.startDate == "" &&
    filteredList.value.endDate == "" &&
    filteredList.value.search == ""
      ? false
      : true;

  if (search) {
    handleCurrentChange();
    return;
  } else {
    const result = await Swal.fire({
      title: "검색조건이 입력되지 않았습니다.",
      text: "조건을 선택 또는 입력해주세요",
      icon: "warning",
      showCancelButton: false,
      confirmButtonText: "확인",
      reverseButtons: true,
    });

    if (!result.isConfirmed) return;
  }
};

const handleCurrentChange = async (val) => {
  val = val == undefined ? 1 : val;
  nowPage.value = val;

  let start = (val - 1) * listNum.value + 1;
  let end = val * listNum.value;

  let obj = {
    projectId: subId ? subId : projectId,
    startNum: start,
    endNum: end,
    ...filteredList.value,
  };

  Swal.fire({
    title: "잠시만 기다려주세요...",
    html: "데이터를 불러오는 중입니다.",
    allowOutsideClick: false,
    showConfirmButton: false,
    showCancelButton: false,
    didOpen: () => {
      Swal.showLoading();
    },
  });

  try {
    await noticeStore.getPagingList(obj);
    Swal.close();

    pagingList.value = noticeStore.pagingList;
    listLength.value =
      pagingList.value.length == 0 ? 0 : pagingList.value[0].taskCounts;
  } catch (err) {
    Swal.fire({
      icon: "error",
      title: "알 수 없는 이유로 데이터를 가져오지 못했습니다.",
    });
  } finally {
    Swal.close();
  }
};

const goResister = () => {
  router.push({
    name: "noticeRegister",
    params: { projectId: projectId, subProjectId: subId },
  });
};

const goDetail = (tr) => {
  router.push({
    name: "noticeDetail",
    params: {
      projectId: projectId,
      subProjectId: subId,
      noticeId: tr.noticeId,
    },
  });
};

onBeforeMount(async () => {
  Swal.fire({
    title: "잠시만 기다려주세요...",
    html: "데이터를 불러오는 중입니다.",
    allowOutsideClick: false,
    showConfirmButton: false,
    showCancelButton: false,
    didOpen: () => {
      Swal.showLoading();
    },
  });

  let id = subId ? subId : projectId;
  await taskStore.getProjectName(id);
  const projectInfo = taskStore.projectName;
  name.value = projectInfo.projectName;
  projectStartDate.value = projectInfo.startDate;
  projectendDate.value = projectInfo.endDate;

  let obj = {
    projectId: subId ? subId : projectId,
    parentProjectId: subId ? subId : projectId,
  };

  await noticeStore.getFilterList(obj);
  filterList.value = noticeStore.filterList;
  pagingList.value = noticeStore.filterList.noticeList;
  listLength.value =
    filterList.value.noticeList.length > 0
      ? filterList.value.noticeList[0].taskCounts
      : 0;

  Swal.close();
});

const resetForm = () => {
  filteredList.value = {
    category: "",
    userId: "",
    startDate: "",
    endDate: "",
    search: "",
  };
  searchKeyword.value = "";
  workPage.value = 1;
  handleCurrentChange(1);
};
</script>

<style scoped>
.proj-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}

.proj-title-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.proj-name-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.proj-name {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
  letter-spacing: -0.02em;
}

.proj-period {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}

.new-project-btn {
  background: #c7d9f5;
  border: none;
  color: #1a1a2e;
  font-weight: 500;
  font-size: 14px;
  border-radius: 8px;
  height: 40px;
}

.new-project-btn:hover {
  background: #a8c4ef;
}

.filter-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.filter-row {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
  min-width: 120px;
  flex: 1;
}

.filter-item--wide {
  flex: 2;
  min-width: 180px;
}

.filter-label {
  font-size: 0.72rem;
  font-weight: 600;
  color: #6b7280;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.select-wrap {
  position: relative;
}

.select-wrap select {
  appearance: none;
  width: 100%;
  padding: 8px 32px 8px 10px;
  border: 1px solid #d1d5db;
  border-radius: 7px;
  font-size: 0.85rem;
  color: #374151;
  background: #f9fafb;
  cursor: pointer;
  transition:
    border-color 0.15s,
    box-shadow 0.15s;
  outline: none;
}

.select-wrap select:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
  background: #fff;
}

.select-arrow {
  position: absolute;
  right: 9px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
  font-size: 0.75rem;
  pointer-events: none;
}

.filter-input {
  padding: 8px 10px;
  border: 1px solid #d1d5db;
  border-radius: 7px;
  font-size: 0.85rem;
  color: #374151;
  background: #f9fafb;
  outline: none;
  width: 100%;
  transition:
    border-color 0.15s,
    box-shadow 0.15s;
}

.filter-input:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
  background: #fff;
}

.search-wrap {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 10px;
  width: 16px;
  height: 16px;
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: 8px 10px 8px 32px;
  border: 1px solid #d1d5db;
  border-radius: 7px;
  font-size: 0.85rem;
  color: #374151;
  background: #f9fafb;
  outline: none;
  transition:
    border-color 0.15s,
    box-shadow 0.15s;
}

.search-input:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
  background: #fff;
}

.filter-actions {
  display: flex;
  gap: 8px;
  align-items: flex-end;
  padding-bottom: 1px;
}

.btn-reset {
  padding: 8px 16px;
  background: #f3f4f6;
  color: #6b7280;
  font-size: 0.85rem;
  font-weight: 600;
  border-radius: 7px;
  border: 1px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.15s;
  white-space: nowrap;
}

.btn-reset:hover {
  background: #e5e7eb;
  color: #374151;
}

.btn-search {
  padding: 8px 20px;
  background: #334155;
  color: #fff;
  font-size: 0.85rem;
  font-weight: 600;
  border-radius: 7px;
  border: none;
  cursor: pointer;
  transition: background 0.15s;
  white-space: nowrap;
}

.btn-search:hover {
  background: #1e293b;
}

.count-badge {
  display: inline-flex;
  align-items: center;
  padding: 3px 12px;
  background: #ede9fe;
  color: #6d28d9;
  font-size: 0.78rem;
  font-weight: 700;
  border-radius: 999px;
}

.notice-board-wrap {
  overflow: hidden;
  border: 1px solid #e5e7eb;
}

.notice-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.notice-table thead th {
  font-size: 12px;
  font-weight: 700;
  color: #4b5563;
  background: #f8fafc;
  border-top: 1px solid #d1d5db;
  border-bottom: 1px solid #d1d5db;
  padding: 12px 10px;
  text-align: center;
}

.notice-table tbody td {
  padding: 13px 10px;
  font-size: 14px;
  color: #374151;
  border-bottom: 1px solid #eceff3;
  vertical-align: middle;
}

.notice-row {
  transition: background 0.15s ease;
}

.notice-row:hover {
  background: #f8fbff;
}

.pinned-row {
  background: #fcfcfc;
}

.pinned-row td {
  background: rgba(255, 153, 102, 0.06);
}

.pinned-row:hover td {
  background: rgba(255, 153, 102, 0.1);
}

.col-num {
  width: 110px;
  text-align: center;
}

.col-title {
  width: auto;
}

.col-writer {
  width: 160px;
  text-align: center;
}

.col-date {
  width: 160px;
  text-align: center;
}

.num-cell,
.writer-cell,
.date-cell {
  text-align: center;
}

.title-cell {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.title-line {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  line-height: 1.45;
}

.notice-title-text {
  color: #111827;
  font-weight: 600;
}

.category-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 44px;
  height: 22px;
  padding: 0 8px;
  border-radius: 4px;
  background: #2563eb;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  line-height: 1;
}

.pin-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 44px;
  height: 22px;
  padding: 0 10px;
  border-radius: 3px;
  background: #b91c1c;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  line-height: 1;
}

.fixed-badge {
  display: inline-flex;
  align-items: center;
  height: 22px;
  padding: 0 8px;
  border-radius: 999px;
  background: #fff1f2;
  border: 1px solid #fecdd3;
  color: #be123c;
  font-size: 11px;
  font-weight: 700;
}

.lock-icon {
  display: inline-flex;
  align-items: center;
  color: #6b7280;
  font-size: 14px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 16px 0 18px;
}
</style>