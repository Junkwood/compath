<template>
  <div class="flex h-screen overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden"
    >
      <!-- Header -->
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
          <!-- projectDashboard.vue와 동일한 제목 영역 -->
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

          <!-- 검색 필터 영역 -->
          <div class="filter-card mt-4 mb-0">
            <div class="filter-row">
              <!-- 업무명 -->
              <div class="filter-item">
                <label class="filter-label">작성자</label>
                <div class="select-wrap">
                  <select v-model="filteredList.title">
                    <option value="전체">전체</option>
                    <option
                      :value="title"
                      v-for="title in titleList"
                      :key="title"
                    >
                      {{ title }}
                    </option>
                  </select>
                  <span class="select-arrow">▾</span>
                </div>
              </div>

              <!-- 담당자 -->
              <div class="filter-item">
                <label class="filter-label">카테고리</label>
                <div class="select-wrap">
                  <select v-model="filteredList.user">
                    <option value="전체">전체</option>
                    <option
                      :value="userName"
                      v-for="userName in assigneeUserIdList"
                      :key="userName"
                    >
                      {{ userName }}
                    </option>
                  </select>
                  <span class="select-arrow">▾</span>
                </div>
              </div>

              <!-- 시작일 -->
              <div class="filter-item">
                <label class="filter-label">시작일</label>
                <input
                  v-model="filteredList.start"
                  type="date"
                  class="filter-input"
                />
              </div>

              <!-- 종료일 -->
              <div class="filter-item">
                <label class="filter-label">종료일</label>
                <input
                  v-model="filteredList.end"
                  type="date"
                  class="filter-input"
                />
              </div>
            </div>
            <!-- 검색어 -->
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
                  v-model="searchKeyword"
                  type="text"
                  placeholder="프로젝트명을 입력해주세요."
                  class="search-input"
                  @keyup.enter="filteringList()"
                />
              </div>
              <!-- 버튼 -->
              <div class="filter-actions flex flex-row-reverse">
                <button
                  type="button"
                  @click="filteringList()"
                  class="btn-search"
                >
                  검색
                </button>
                <button type="button" @click="resetForm()" class="btn-reset">
                  초기화
                </button>
              </div>
            </div>
          </div>
          <!-- 목록 영역 -->
          <div
            class="col-span-full xl:col-span-8 bg-white dark:bg-gray-800 shadow-xs rounded-xl mt-4"
          >
            <!-- 내보내기 버튼 + 총 건수 -->
            <div class="flex flex-row-reverse items-center px-5 pt-2 pb-2">
              <span class="count-badge flex flex" v-if="filterList.length > 0"
                >총 {{ listLength }}건</span
              >
              <span v-else></span>
            </div>
            <!-- 테이블 -->
            <table class="table-auto w-full dark:text-gray-300">
              <thead
                class="text-xs uppercase text-gray-400 dark:text-gray-500 bg-gray-50 dark:bg-gray-700/50 rounded-xs"
              >
                <tr>
                  <th class="p-2" v-for="th in thList" :key="th">
                    <div class="text-center">{{ th }}</div>
                  </th>
                </tr>
              </thead>

              <tbody
                class="text-sm font-medium divide-y divide-gray-100 dark:divide-gray-700/60"
              >
                <!-- 로딩 -->
                <tr v-if="listLoading">
                  <td :colspan="thList.length + 1" class="text-center py-10">
                    <h5 class="text-gray-500">⌛ 로딩중입니다.</h5>
                  </td>
                </tr>

                <!-- 데이터 있을 때 -->
                <template v-if="filterList.length > 0 && !listLoading">
                  <tr
                    v-for="task in filterList"
                    :key="task.taskId"
                    class="cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-700/30"
                  >
                    <td class="p-2 w-30">
                      <div class="text-center"></div>
                    </td>
                    <td class="p-2">
                      <div class="text-center">{{ task.projectName }}</div>
                    </td>
                    <td class="p-2 w-70">
                      <div class="text-center cursor-pointer">
                        {{ task.userName }}
                      </div>
                    </td>

                    <td class="p-2 w-70">
                      <div class="text-center">
                        {{ formatDate(task.startDate) }}
                      </div>
                    </td>
                  </tr>
                </template>

                <!-- 데이터 없을 때 -->
                <tr v-else-if="filterList.length === 0 && !listLoading">
                  <td :colspan="thList.length + 1" class="text-center py-10">
                    <h5 class="text-gray-500">업무가 존재하지 않습니다.</h5>
                  </td>
                </tr>
              </tbody>
            </table>

            <!-- 페이지네이션 -->
            <div class="pagination-wrap" v-if="workPageSize / listNum > 1">
              <el-pagination
                v-model:current-page="workPage"
                :current-page="nowPage"
                :page-size="listNum"
                :total="workPageSize"
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
import { ref, computed } from "vue";
import { useTaskReport } from "../stores/TaskReport";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { usetaskKJHStore } from "../stores/taksKJH";

const route = useRoute();
const router = useRouter();
const taskReportStore = useTaskReport();
const taskStore = usetaskKJHStore();

const projectId = route.params.projectId;
const sidebarOpen = ref(false);
const listLoading = ref(false);

const filteredList = ref({
  title: "전체",
  user: "전체",
  type: "전체",
  start: "",
  end: "",
});
const searchKeyword = ref("");

const allData = ref([]);
const filterList = ref([]);
const listLength = ref(0);
const workPageSize = ref(0);
const workPage = ref(1);
const listNum = ref(10);
const selectedTaskIds = ref([]);
const nowPage = ref(1);
const real = ref(true);
const titleList = ref([]);
const assigneeUserIdList = ref([]);
const taskTypeList = ref([]);

let name = ref(); // 프로젝트명
let projectStartDate = ref(); // 프로젝트 날짜
let projectendDate = ref(); // 프로젝트 날짜

const formatDate = (dateStr) => {
  if (!dateStr) return "-";
  return dateStr.toString().substring(0, 10);
};

const thList = ["번호", "제목", "작성자", "등록일"];

// 공지 생성 버튼
const goResister = () => {
  router.push({
    name: "noticeRegister",
    params: { projectId: projectId },
  });
};

// 페이지에 맞게 slice
const slicePage = (data) => {
  const start = (workPage.value - 1) * listNum.value;
  const end = start + listNum.value;
  filterList.value = data.slice(start, end);
};

const filteringList = async () => {
  listLoading.value = true;

  const params = {
    projectId: projectId,
    title:
      filteredList.value.title !== "전체"
        ? filteredList.value.title
        : searchKeyword.value || null,
    assigneeUserId:
      filteredList.value.user !== "전체" ? filteredList.value.user : null,
    taskTypeId:
      filteredList.value.type !== "전체" ? filteredList.value.type : null,
    startDate: filteredList.value.start || null,
    dueDate: filteredList.value.end || null,
  };

  try {
    const data = await taskReportStore.fetchReportList(params);
    if (data) {
      allData.value = data;
      listLength.value = data.length;
      workPageSize.value = data.length;
      slicePage(data);

      titleList.value = [...new Set(data.map((t) => t.title).filter(Boolean))];
      assigneeUserIdList.value = [
        ...new Set(data.map((t) => t.userName).filter(Boolean)),
      ];
      taskTypeList.value = [
        ...new Set(data.map((t) => t.typeName).filter(Boolean)),
      ];
    }
  } catch (error) {
    console.error("데이터 조회 중 오류 발생:", error);
  } finally {
    listLoading.value = false;
  }
};

onBeforeMount(async () => {
  await filteringList();

  await taskStore.getProjectName(projectId);
  const projectInfo = taskStore.projectName;
  name.value = projectInfo.projectName; // 프로젝트 이름
  projectStartDate.value = projectInfo.startDate;
  projectendDate.value = projectInfo.startDate;
});

const resetForm = () => {
  filteredList.value = {
    title: "전체",
    user: "전체",
    type: "전체",
    start: "",
    end: "",
  };
  searchKeyword.value = "";
  workPage.value = 1;
  filteringList();
};

// ✅ 페이지 변경 시 API 재호출 없이 slice만
const handleCurrentChange = (page) => {
  workPage.value = page;
  slicePage(allData.value);
};

const isAllChecked = computed(
  () =>
    filterList.value.length > 0 &&
    selectedTaskIds.value.length === filterList.value.length,
);

const toggleAllCheckbox = (e) => {
  selectedTaskIds.value = e.target.checked
    ? filterList.value.map((t) => t.taskId)
    : [];
};

const goDetail = (taskId) => {
  router.push({ name: "TaskDetail", params: { taskId } });
};

const exportExcel = () => {};
const exportPdf = () => {};
</script>
<style scoped>
/*  상단 */
/* 상단 */
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
/* 공지 생성 버튼 */
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
/* ── 필터 카드 ── */
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

/* ── Select ── */
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

/* ── Date input ── */
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

/* ── 검색어 ── */
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

/* ── 버튼 ── */
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

/* ── Excel / PDF 버튼 ── */
.btn-export {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 6px 14px;
  font-size: 0.78rem;
  font-weight: 600;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  transition: all 0.15s;
}
.btn-export--excel {
  background: #f0fdf4;
  color: #16a34a;
  border: 1px solid #bbf7d0;
}
.btn-export--excel:hover {
  background: #dcfce7;
}
.btn-export--pdf {
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}
.btn-export--pdf:hover {
  background: #fee2e2;
}

/* ── 총 건수 배지 ── */
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

/* ── 테이블 ── */
table {
  border-collapse: collapse;
}
thead th {
  font-size: 0.72rem;
  font-weight: 700;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  padding: 10px 8px;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}
tbody tr {
  border-bottom: 1px solid #f3f4f6;
  transition: background 0.1s;
}
tbody tr:hover {
  background: #f8faff;
}
tbody td {
  padding: 10px 8px;
  font-size: 0.875rem;
  color: #374151;
}

/* ── 페이지네이션 ── */
.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 10px 0;
}
</style>
