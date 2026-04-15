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

      <!-- 페이지 타이틀 -->
      <div class="col-span-full xl:col-span-8 mx-8 mt-3 mb-0">
        <header class="pt-4 border-b border-gray-100 dark:border-gray-700/60">
          <h1 class="text-2xl font-bold text-gray-800 dark:text-gray-100 mb-8">
            업무 보고서
          </h1>
        </header>
      </div>

      <!-- 검색 필터 영역 -->
      <div class="filter-card mx-8 mt-4 mb-0">
        <div class="filter-row">
          <!-- 업무명 -->
          <div class="filter-item">
            <label class="filter-label">업무명</label>
            <div class="select-wrap">
              <select v-model="filteredList.title">
                <option value="전체">전체</option>
                <option :value="title" v-for="title in titleList" :key="title">
                  {{ title }}
                </option>
              </select>
              <span class="select-arrow">▾</span>
            </div>
          </div>

          <!-- 담당자 -->
          <div class="filter-item">
            <label class="filter-label">담당자</label>
            <div class="select-wrap">
              <select v-model="filteredList.user">
                <option value="전체">전체</option>
                <option
                  :value="item.assigneeUserId"
                  v-for="item in assigneeList"
                  :key="item.assigneeUserId"
                >
                  {{ item.userName }}
                </option>
              </select>
              <span class="select-arrow">▾</span>
            </div>
          </div>

          <!-- 업무 유형 -->
          <div class="filter-item">
            <label class="filter-label">업무 유형</label>
            <div class="select-wrap">
              <select v-model="filteredList.type">
                <option value="전체">전체</option>
                <option
                  :value="item.typeName"
                  v-for="item in taskTypeList"
                  :key="item.typeName"
                >
                  {{ item.typeName }}
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

          <!-- 검색어 -->
          <div class="filter-item filter-item--wide">
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
                placeholder="검색어를 입력하세요"
                class="search-input"
                @keyup.enter="filteringList()"
              />
            </div>
          </div>

          <!-- 버튼 -->
          <div class="filter-actions">
            <button type="button" @click="resetForm()" class="btn-reset">
              초기화
            </button>
            <button type="button" @click="filteringList()" class="btn-search">
              검색
            </button>
          </div>
        </div>
      </div>

      <!-- 목록 영역 -->
      <div
        class="col-span-full xl:col-span-8 bg-white dark:bg-gray-800 shadow-xs rounded-xl m-8 mt-4"
      >
        <!-- 내보내기 버튼 + 총 건수 -->
        <div class="flex items-center justify-between px-5 pt-4 pb-2">
          <span class="count-badge" v-if="filterList.length > 0"
            >총 {{ listLength }}건</span
          >
          <span v-else></span>
          <div class="flex gap-2">
            <button
              type="button"
              @click="goStats()"
              class="btn-export btn-export--stats"
            >
              <svg
                width="14"
                height="14"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
              >
                <path d="M3 3v18h18M7 16l4-4 4 4 4-4" />
              </svg>
              통계 보기
            </button>
            <button
              type="button"
              @click="exportExcel()"
              class="btn-export btn-export--excel"
            >
              <svg
                width="14"
                height="14"
                viewBox="0 0 24 24"
                fill="currentColor"
              >
                <path
                  d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zm-1 1.5L18.5 9H13V3.5zM8.5 17l-1.5-2.5L5.5 17H4l2.2-3.3L4.1 10.5h1.5l1.4 2.3 1.4-2.3H10L8 13.7 10.1 17H8.5zm5.5 0h-1.3l-2.5-6.5H11l1.8 4.8 1.8-4.8H16L13.5 17h.5z"
                />
              </svg>
              Excel
            </button>
            <button
              type="button"
              @click="exportPdf()"
              class="btn-export btn-export--pdf"
            >
              <svg
                width="14"
                height="14"
                viewBox="0 0 24 24"
                fill="currentColor"
              >
                <path
                  d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zm-1 1.5L18.5 9H13V3.5zM7 15.5c0 .8-.6 1.5-1.5 1.5H5v1.5H3.5V12H5.5c.9 0 1.5.7 1.5 1.5v2zm4.5 1c0 .8-.6 1.5-1.5 1.5H8V12h2c.9 0 1.5.7 1.5 1.5v3zm4-3H14v1h1.5v1H14v1.5H12.5V12H15.5v1.5z"
                />
                <path d="M5 13.5v2h.5v-2H5zm3 0v3h.5v-3H8z" />
              </svg>
              PDF
            </button>
          </div>
        </div>

        <!-- 테이블 -->
        <table class="table-auto w-full dark:text-gray-300">
          <thead
            class="text-xs uppercase text-gray-400 dark:text-gray-500 bg-gray-50 dark:bg-gray-700/50 rounded-xs"
          >
            <tr>
              <th class="p-2 w-10">
                <input
                  type="checkbox"
                  @change="toggleAllCheckbox"
                  :checked="isAllChecked"
                />
              </th>
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
                <td class="p-2 text-center w-10">
                  <input
                    type="checkbox"
                    :value="task.taskId"
                    v-model="selectedTaskIds"
                  />
                </td>
                <td class="p-2">
                  <div class="text-center">{{ task.userName }}</div>
                </td>
                <td class="p-2 w-40">
                  <div class="text-center">{{ task.projectName }}</div>
                </td>
                <td class="p-2 w-80">
                  <div
                    class="text-center text-blue-600 dark:text-blue-400 cursor-pointer hover:underline"
                    @click="goDetail(task.taskId)"
                  >
                    {{ task.title }}
                  </div>
                </td>
                <td class="p-2 w-24">
                  <div class="text-center">{{ task.typeName }}</div>
                </td>
                <td class="p-2">
                  <div class="text-center">
                    {{ formatDate(task.startDate) }}
                  </div>
                </td>
                <td class="p-2">
                  <div class="text-center">{{ formatDate(task.dueDate) }}</div>
                </td>
                <td class="p-2 w-28">
                  <div class="text-center">
                    {{
                      task.actualHours != null ? task.actualHours + "시간" : "-"
                    }}
                  </div>
                </td>
                <td class="p-2 w-48">
                  <div class="progress-wrap">
                    <el-progress
                      :percentage="task.progressRate"
                      :stroke-width="8"
                      :show-text="false"
                      color="#2563eb"
                      style="flex: 1"
                    />
                    <span class="progress-text">{{ task.progressRate }}%</span>
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
  </div>
</template>

<script setup>
import { onBeforeMount, ref, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { storeToRefs } from "pinia";
import { useTaskReport } from "../stores/TaskReport";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import * as XLSX from "xlsx";
import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";
import NanumGothicBase64 from "../assets/fonts/NanumGothic.js";

const route = useRoute();
const router = useRouter();
const taskReportStore = useTaskReport();

const {
  allData,
  filterList,
  listLength,
  workPageSize,
  titleList,
  assigneeList,
  taskTypeList,
} = storeToRefs(taskReportStore);

const projectId = route.params.projectId;
const sidebarOpen = ref(false);
const listLoading = ref(false);
const workPage = ref(1);
const listNum = ref(10);
const nowPage = ref(1);
const real = ref(true);
const selectedTaskIds = ref([]);
const searchKeyword = ref("");

const filteredList = ref({
  title: "전체",
  user: "전체",
  type: "전체",
  start: "",
  end: "",
});

const thList = [
  "담당자",
  "프로젝트명",
  "업무명",
  "업무유형",
  "시작일",
  "마감일",
  "업무 기간",
  "진척도",
];

const formatDate = (dateStr) => {
  if (!dateStr) return "-";
  return dateStr.toString().substring(0, 10);
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
    typeName:
      filteredList.value.type !== "전체" ? filteredList.value.type : null,
    startDate: filteredList.value.start || null,
    dueDate: filteredList.value.end || null,
  };
  await taskReportStore.fetchReportList(params, workPage.value, listNum.value);
  listLoading.value = false;
};

onBeforeMount(async () => {
  listLoading.value = true;
  await taskReportStore.fetchReportList({ projectId }, 1, listNum.value, true);
  listLoading.value = false;
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

const handleCurrentChange = (page) => {
  workPage.value = page;
  taskReportStore.slicePage(page, listNum.value);
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
  router.push({
    name: "taskDetail",
    params: { taskId: taskId, projectId: projectId },
  });
};

// 체크된 것만 or 전체
const exportData = computed(() => {
  if (selectedTaskIds.value.length === 0) return allData.value;
  return allData.value.filter((task) =>
    selectedTaskIds.value.includes(task.taskId),
  );
});

///통계
const goStats = () => {
  router.push({ name: "TaskReportStats", params: { projectId } });
};

const exportExcel = () => {
  const rows = exportData.value.map((task) => ({
    담당자: task.userName,
    프로젝트명: task.projectName,
    업무명: task.title,
    업무유형: task.typeName,
    시작일: formatDate(task.startDate),
    마감일: formatDate(task.dueDate),
    "업무 기간(시간)": task.actualHours ?? task.estimatedHours ?? "-",
    "진척도(%)": task.progressRate,
  }));
  const ws = XLSX.utils.json_to_sheet(rows);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, "업무보고서");
  XLSX.writeFile(wb, "업무보고서.xlsx");
};

const exportPdf = () => {
  const doc = new jsPDF({ orientation: "landscape" });

  doc.addFileToVFS("NanumGothic.ttf", NanumGothicBase64);
  doc.addFont("NanumGothic.ttf", "NanumGothic", "normal");
  doc.setFont("NanumGothic", "normal");
  doc.setFontSize(14);
  doc.text("업무 보고서", 14, 15);

  const headers = [
    [
      "담당자",
      "프로젝트명",
      "업무명",
      "업무유형",
      "시작일",
      "마감일",
      "기간(h)",
      "진척도",
    ],
  ];
  const body = exportData.value.map((task) => [
    task.userName,
    task.projectName,
    task.title,
    task.typeName,
    formatDate(task.startDate),
    formatDate(task.dueDate),
    task.actualHours ?? task.estimatedHours ?? "-",
    `${task.progressRate}%`,
  ]);

  autoTable(doc, {
    head: headers,
    body: body,
    startY: 20,
    styles: { fontSize: 9, font: "NanumGothic" },
    headStyles: {
      fillColor: [51, 65, 85],
      font: "NanumGothic",
      fontStyle: "normal",
    },
    didParseCell: (data) => {
      data.cell.styles.font = "NanumGothic";
    },
  });

  doc.save("업무보고서.pdf");
};
</script>

<style scoped>
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
.btn-export--stats {
  background: #eff6ff;
  color: #2563eb;
  border: 1px solid #bfdbfe;
}
.btn-export--stats:hover {
  background: #dbeafe;
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
.progress-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
}
.progress-text {
  font-size: 0.8rem;
  font-weight: 700;
  color: #2563eb;
  white-space: nowrap;
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 16px 0;
}
</style>
