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
        <div class="sub-header">
          <div class="breadcrumb">
            <span class="bc-home">홈</span>
            <span class="bc-sep">›</span>
            <span>{{ name }}</span>
            <span class="bc-sep">›</span>
            <span class="bc-cur">문서 목록</span>
          </div>
        </div>

        <div class="page-container">
          <div class="pg-row">
            <div class="pg-left">
              <div class="proj-meta">
                <span class="proj-name">{{ name }}</span>
                <span class="proj-period">
                  {{ projectStartDate }} ~ {{ projectendDate }}
                </span>
              </div>
            </div>
            <div class="self-end">
              <el-button class="btn-create-task" @click="goResister()">
                + 문서 생성
              </el-button>
            </div>
          </div>

          <div class="panel-body search-body">
            <div class="search-layout">
              <div class="search-row primary-row">
                <div class="form-item">
                  <label>작성자</label>
                  <select class="input w-full" v-model="filteredList.createdBy">
                    <option value="">전체</option>
                    <option
                      :value="user.createdBy"
                      v-for="user in filterList.userList"
                      :key="user.userId"
                    >
                      {{ user.userName }}
                    </option>
                  </select>
                </div>

                <div class="form-item">
                  <label>카테고리</label>
                  <select
                    class="input w-full"
                    v-model="filteredList.categoryList"
                  >
                    <option value="">전체</option>
                    <option
                      :value="category.meetingTypeCode"
                      v-for="category in filterList.typeList"
                      :key="category.meetingTypeCode"
                    >
                      {{ category.roleName }}
                    </option>
                  </select>
                </div>

                <div class="form-item">
                  <label>시작일</label>
                  <input
                    v-model="filteredList.startDate"
                    type="date"
                    class="input w-full"
                  />
                </div>

                <div class="form-item">
                  <label>종료일</label>
                  <input
                    v-model="filteredList.endDate"
                    type="date"
                    class="input w-full"
                  />
                </div>

                <div class="form-item">
                  <label>검색어</label>
                  <input
                    v-model="filteredList.search"
                    type="text"
                    placeholder="검색어 입력"
                    class="input w-full"
                    @keyup.enter="handleCurrentChange()"
                  />
                </div>

                <div class="form-item search-btn-group">
                  <div class="search-actions">
                    <button
                      type="button"
                      @click="resetForm()"
                      class="btn-reset"
                    >
                      초기화
                    </button>
                    <button
                      type="button"
                      @click="handleCurrentChange()"
                      class="btn-search"
                    >
                      검색
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- 목록 영역 -->
          <div class="panel">
            <div class="panel-head list-head">
              <span class="panel-title">문서 목록</span>
              <span class="count-badge">총 {{ listLength }}건</span>
            </div>

            <div class="panel-body list-body">
              <div class="table-wrap">
                <table class="task-table">
                  <thead>
                    <tr>
                      <th class="p-2" v-for="th in thList" :key="th">
                        <div class="text-center">{{ th }}</div>
                      </th>
                    </tr>
                  </thead>

                  <tbody>
                    <!-- 로딩 -->
                    <tr v-if="listLoading">
                      <td
                        :colspan="thList.length + 1"
                        class="text-center py-10"
                      >
                        <h5 class="text-gray-500">⌛ 로딩중입니다.</h5>
                      </td>
                    </tr>

                    <!-- 데이터 있을 때 -->
                    <template v-else-if="!listLoading && listLength > 0">
                      <tr
                        @click="goDetail(document)"
                        v-for="document in pagingList"
                        :key="document.num"
                        class="cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-700/30"
                        :class="
                          document.isDeleted == 'O1'
                            ? 'grayscale-[100%] blur-[4px] opacity-60'
                            : ''
                        "
                      >
                        <td class="p-2 w-30">
                          <div class="text-center">{{ document.num }}</div>
                        </td>
                        <td class="p-2">
                          <div class="text-center">
                            [{{ document.roleName }}]{{ document.title
                            }}<span
                              class="text-base"
                              v-if="document.isPinned == 'O1' ? true : false"
                              >📌</span
                            ><span
                              class="text-base"
                              v-if="document.isComment == 'O2' ? true : false"
                              >🔒</span
                            >
                          </div>
                        </td>
                        <td class="p-2 w-70">
                          <div class="text-center cursor-pointer">
                            {{ document.userName }}
                          </div>
                        </td>
                        <td class="p-2 w-70">
                          <div class="text-center cursor-pointer">
                            {{ document.count }}
                          </div>
                        </td>

                        <td class="p-2 w-70">
                          <div class="text-center">
                            {{ document.createdAt }}
                          </div>
                        </td>
                      </tr>
                    </template>

                    <!-- 데이터 없을 때 -->
                    <tr v-else>
                      <td
                        :colspan="thList.length + 1"
                        class="text-center py-10"
                      >
                        <h5 class="text-gray-500">업무가 존재하지 않습니다.</h5>
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
          </div>

          <!-- 테이블 -->
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
import { useDocumentStore } from "../stores/document";
import Swal from "sweetalert2";

const route = useRoute();
const router = useRouter();
const taskStore = usetaskKJHStore();
const noticeStore = useNoticeStore();
const documentStore = useDocumentStore();

const projectId = route.params.projectId;
let subId = route.params.subProjectId;
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

let name = ref(); // 프로젝트명
let projectStartDate = ref(); // 프로젝트 날짜
let projectendDate = ref(); // 프로젝트 날짜

const thList = ["번호", "제목", "작성자", "댓글수", "등록일"];

// 페이지네이션
const handleCurrentChange = async (val) => {
  val = val == undefined ? 1 : val;
  console.log("페이징", val);
  nowPage.value = val;

  let start = (val - 1) * listNum.value + 1;
  let end = val * listNum.value;

  // 페이지 변환 목록 조회
  let obj = {
    projectId: projectId,
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
    await documentStore.getPagingList(obj);
    Swal.close();

    pagingList.value = documentStore.pagingList;
    pagingList.value.forEach((li) => {
      li.roleName = li.roleName == null ? "전체" : li.roleName;
    });

    listLength.value =
      pagingList.value.length == 0 ? 0 : pagingList.value[0].taskCounts;
  } catch (err) {
    Swal.fire({
      icon: "error",
      title: "알수 없는 이유로 데이터를 가져오지 못했습니다.",
    });
  } finally {
    Swal.close();
  }
};

// 공지 생성 버튼
const goResister = () => {
  router.push({
    name: "documentRegister",
    params: { projectId: projectId, subProjectId: subId },
  });
};

// 테이블 열 클릭시
const goDetail = (tr) => {
  console.log(tr);
  router.push({
    name: "documentDetail",
    params: {
      projectId: projectId,
      subProjectId: subId,
      documentId: tr.documentId,
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
  if (subId) {
    await taskStore.getProjectName(subId);
    let obj = { projectId: subId, parentProjectId: subId };
    await documentStore.getFilterList(obj);
  } else {
    await taskStore.getProjectName(projectId);
    let obj = { projectId: projectId, parentProjectId: projectId };
    await documentStore.getFilterList(obj);
  }
  const projectInfo = taskStore.projectName;
  name.value = projectInfo.projectName; // 프로젝트 이름
  projectStartDate.value = projectInfo.startDate;
  projectendDate.value = projectInfo.endDate;

  Swal.close();

  filterList.value = documentStore.filterList;
  pagingList.value = documentStore.filterList.documentList;

  pagingList.value.forEach((li) => {
    li.roleName = li.roleName == null ? "전체" : li.roleName;
  });
  listLength.value =
    filterList.value.documentList.length > 0
      ? filterList.value.documentList[0].taskCounts
      : 0;
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

.page-container {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.pg-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.pg-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.proj-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.proj-name {
  font-size: 15px;
  font-weight: 700;
  color: #1b5c9c;
}

.proj-period {
  font-size: 13px;
  color: #6b7280;
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

.sub-header {
  background: #fff;
  padding: 12px 24px;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 10;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.bc-home {
  color: #9ca3af;
}

.bc-sep {
  color: #d1d5db;
}

.bc-cur {
  color: #111827;
  font-weight: 600;
}

.panel-body {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.search-body {
  padding: 16px 18px;
}

.search-layout {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.search-row {
  display: grid;
  gap: 12px;
  align-items: end;
}

.primary-row {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr)) auto;
  gap: 12px;
  align-items: end;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.form-item label {
  font-size: 12px;
  font-weight: 700;
  color: #4b5563;
  line-height: 1.2;
}

.list-head {
  align-items: center;
}

.list-body {
  padding: 0;
}

:deep(.input) {
  height: 36px;
  border-radius: 8px !important;
  border: 1px solid #e2e8f0 !important;
  background: #f8fafc !important;
  transition:
    border-color 0.2s,
    box-shadow 0.2s,
    background 0.2s;
  font-size: 12px;
  padding: 0 10px;
  color: #111827;
}

:deep(.input:focus) {
  border-color: #93c5fd !important;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.12) !important;
  background: #fff !important;
  outline: none;
}

:deep(select.input) {
  appearance: auto !important;
  -webkit-appearance: auto !important;
  padding-right: 24px !important;
}

/* 버튼들을 감싸는 영역 */
.search-btn-group {
  display: flex;
  justify-content: flex-end;
}

.search-actions {
  display: flex;
  gap: 8px;
  width: 100%;
}

.panel {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

.panel-head {
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  border-bottom: 1px solid #f3f4f6;
}

.panel-title {
  font-size: 15px;
  font-weight: 700;
  color: #111827;
}

.panel-body {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.table-wrap {
  width: 100%;
  overflow-x: auto;
}

.task-table {
  width: 100%;
  border-collapse: collapse;
}
</style>
