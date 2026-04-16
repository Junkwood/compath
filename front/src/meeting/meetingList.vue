<template>
  <div class="dashboard-page flex h-screen overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden bg-gray-50"
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
            <span class="bc-sep">›</span
            ><span v-for="info in taskPjList" :key="info">{{ info }} › </span>
            <span class="bc-cur">회의록 목록</span>
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
                + 회의록 생성
              </el-button>
            </div>
          </div>

          <!-- 검색 필터 영역 -->
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
                      :key="user.createdBy"
                    >
                      {{ user.userName }}
                    </option>
                  </select>
                </div>

                <div class="form-item">
                  <label>회의유형</label>
                  <select
                    class="input w-full"
                    v-model="filteredList.meetingTypeCode"
                  >
                    <option value="">전체</option>
                    <option
                      :value="category.meetingTypeCode"
                      v-for="category in filterList.typeList"
                      :key="category.meetingTypeCode"
                    >
                      {{ category.typeName }}
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
              <span class="panel-title">회의록 목록</span>
              <span class="count-badge">총 {{ listLength }}건</span>
            </div>

            <div class="panel-body list-body">
              <div class="table-wrap">
                <table class="task-table">
                  <thead>
                    <tr>
                      <th v-for="th in thList" :key="th">
                        <div class="th-text">{{ th }}</div>
                      </th>
                    </tr>
                  </thead>

                  <tbody>
                    <!-- 로딩 -->
                    <tr v-if="listLoading">
                      <td :colspan="thList.length" class="empty-cell">
                        ⌛ 로딩중입니다.
                      </td>
                    </tr>

                    <!-- 데이터 있을 때 -->
                    <template v-else-if="!listLoading && listLength > 0">
                      <tr
                        @click="goDetail(meeting)"
                        v-for="meeting in pagingList"
                        :key="meeting.num"
                        class="table-row"
                        :class="
                          meeting.isDeleted == 'O1'
                            ? 'grayscale-[100%] blur-[4px] opacity-60'
                            : ''
                        "
                      >
                        <td class="text-center">{{ meeting.num }}</td>
                        <td class="text-left">
                          <div
                            style="
                              display: inline-flex;
                              align-items: center;
                              justify-content: center;
                              gap: 4px;
                            "
                          >
                            [{{ meeting.typeName }}]{{ meeting.title
                            }}<el-icon
                              class="text-lg"
                              v-if="
                                meeting.attachmentGroupId !== null
                                  ? true
                                  : false
                              "
                              ><Paperclip
                            /></el-icon>
                          </div>
                        </td>
                        <td class="text-center">{{ meeting.userName }}</td>
                        <td class="text-center">{{ meeting.createdAt }}</td>
                      </tr>
                    </template>

                    <!-- 데이터 없을 때 -->
                    <tr v-else>
                      <td :colspan="thList.length" class="empty-cell">
                        회의록이 존재하지 않습니다.
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>

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
import Swal from "sweetalert2";
import { useMeetingStore } from "../stores/meeting";
import { Paperclip } from "@element-plus/icons-vue";

const route = useRoute();
const router = useRouter();
const taskStore = usetaskKJHStore();
const meetingStore = useMeetingStore();

const projectId = route.params.projectId;
const subId = route.params.subProjectId || "";
let taskPjList = ref([]);

const sidebarOpen = ref(false);
const listLoading = ref(false);

const filteredList = ref({
  meetingTypeCode: "",
  createdBy: "",
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

const thList = ["번호", "제목", "작성자", "등록일"];

// 페이지네이션
const handleCurrentChange = async (val) => {
  val = val == null ? 1 : val;
  nowPage.value = val;

  let start = (val - 1) * listNum.value + 1;
  let end = val * listNum.value;

  // 페이지 변환 목록 조회
  let obj = {
    projectId: subId != "" ? subId : projectId,
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
    await meetingStore.getPagingList(obj);
    Swal.close();

    pagingList.value = meetingStore.pagingList;

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

// 회의록 생성 버튼
const goResister = () => {
  router.push({
    name: "meetingRegister",
    params: { projectId: projectId, subProjectId: subId },
  });
};

// 테이블 열 클릭시
const goDetail = (tr) => {
  console.log(tr);
  router.push({
    name: "meetingDetail",
    params: {
      projectId: projectId,
      subProjectId: subId,
      meetingId: tr.meetingLogId,
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
  console.log(subId == "");
  let id = subId != "" ? subId : projectId;
  console.log(id);
  await taskStore.getProjectName(id);
  const projectInfo = taskStore.projectName;
  if (projectInfo.parentProjectName != null) {
    taskPjList.value = [projectInfo.parentProjectName, projectInfo.projectName];
  } else {
    taskPjList.value = [projectInfo.projectName];
  }
  name.value = projectInfo.projectName;
  projectStartDate.value = projectInfo.startDate;
  projectendDate.value = projectInfo.endDate;

  let obj = { projectId: subId != "" ? subId : projectId };
  await meetingStore.getFilterList(obj);

  filterList.value = meetingStore.filterList;
  pagingList.value = meetingStore.filterList.meetingList.listLength;
  filterList.value.meetingList.length > 0
    ? filterList.value.meetingList[0].taskCounts
    : 0;
  await handleCurrentChange(1);
  Swal.close();
});

const resetForm = () => {
  filteredList.value = {
    meetingTypeCode: "",
    createdBy: "",
    startDate: "",
    endDate: "",
    search: "",
  };
  searchKeyword.value = "";
  workPage.value = 1;
};
</script>

<style scoped>
.dashboard-page {
  font-family: "Pretendard", sans-serif;
  background-color: #f3f4f6;
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
  padding: 16px 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.btn-create-task {
  background: linear-gradient(135deg, #1b5c9c 0%, #144677 100%) !important;
  color: #fff !important;
  border: none !important;
  padding: 10px 18px !important;
  height: 40px !important;
  border-radius: 8px !important;
  font-weight: 700 !important;
  box-shadow: 0 4px 14px rgba(27, 92, 156, 0.3) !important;
  transition: all 0.3s ease !important;
}

.btn-create-task:hover {
  transform: translateY(-2px);
  filter: brightness(1.08);
}

.search-panel-head {
  align-items: center;
}

.search-head-actions {
  display: flex;
  align-items: center;
  gap: 8px;
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
  grid-template-columns: 1fr 1fr 1fr 1fr 1fr;
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

.count-badge {
  font-size: 13px;
  font-weight: 700;
  padding: 4px 12px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
}

.table-wrap {
  width: 100%;
  overflow-x: auto;
}

.task-table {
  width: 100%;
  border-collapse: collapse;
}

.task-table thead {
  background: #f9fafb;
}

.task-table th {
  height: 46px;
  padding: 0 12px;
  font-size: 12px;
  font-weight: 600;
  color: #4b5563;
  border-bottom: 1px solid #eef2f7;
  white-space: nowrap;
}

.th-text {
  text-align: center;
}

.task-table td {
  padding: 14px 12px;
  font-size: 13px;
  color: #1f2937;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: middle;
}

.table-row {
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.table-row:hover {
  background: #f9fbff;
}

.empty-cell {
  text-align: center;
  padding: 48px 20px !important;
  font-size: 14px;
  color: #9ca3af !important;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 16px;
  border-top: 1px solid #f3f4f6;
  background: #f9fafb;
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

:deep(.el-pagination.is-background .btn-next),
:deep(.el-pagination.is-background .btn-prev),
:deep(.el-pagination.is-background .el-pager li) {
  border-radius: 8px;
}

@media (max-width: 1200px) {
  .primary-row {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .sub-header {
    padding: 12px 16px;
  }

  .page-container {
    padding: 16px;
    gap: 16px;
  }

  .pg-row {
    padding: 16px;
    flex-direction: column;
    align-items: flex-start;
  }

  .panel-head {
    padding: 14px 16px;
  }

  .search-panel-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .search-head-actions {
    width: 100%;
    justify-content: flex-start;
  }

  .search-body {
    padding: 14px 12px;
  }

  .primary-row {
    grid-template-columns: repeat(5, 1fr);
  }

  .btn-search,
  .btn-reset,
  .btn-create-task {
    width: 100%;
  }

  .task-table th,
  .task-table td {
    white-space: nowrap;
  }
}

.primary-row {
  display: grid;
  /* 필터 5개 + 버튼 영역 1개 = 총 6열 */
  grid-template-columns: repeat(5, 1fr) 160px;
  gap: 12px;
  align-items: end; /* 중요: 라벨 아래 입력창과 버튼의 바닥선 일치 */
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

.btn-search,
.btn-reset {
  flex: 1;
  height: 36px; /* input과 동일한 높이 */
  white-space: nowrap;
}

/* 1200px 이하에서도 한 줄 유지를 원하시면 이 부분을 수정하세요 */
@media (max-width: 1200px) {
  .primary-row {
    grid-template-columns: repeat(
      3,
      1fr
    ); /* 너무 좁아지면 3열씩 2줄로 자동 변경 */
  }
}

@media (max-width: 768px) {
  .primary-row {
    grid-template-columns: 1fr; /* 모바일은 1줄 */
  }
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
</style>
