<template>
  <div class="dashboard-page flex h-screen overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden bg-gray-50"
    >
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="sub-header">
          <div class="breadcrumb">
            <span class="bc-home">홈</span>
            <span class="bc-sep">›</span>
            <span class="bc-cur">전체 프로젝트 리스트</span>
          </div>
        </div>

        <div class="page-container">
          <!-- 상단 제목 카드 -->
          <div class="pg-row">
            <div class="pg-left">
              <h1 class="pg-title">전체 프로젝트 리스트</h1>
              <div class="proj-meta">
                <span class="proj-desc">
                  프로젝트를 검색하고 상세 대시보드로 이동할 수 있습니다.
                </span>
              </div>
            </div>
          </div>

          <!-- 검색 패널 -->
          <div class="panel">
            <div class="panel-head">
              <span class="panel-title">검색 조건</span>
            </div>

            <div class="panel-body search-body">
              <form @submit.prevent="searchProjects">
                <div class="search-grid">
                  <div class="form-item">
                    <label>프로젝트명</label>
                    <input
                      v-model="searchForm.projectName"
                      type="text"
                      class="input w-full"
                      placeholder="프로젝트명 검색"
                    />
                  </div>

                  <div class="form-item">
                    <label>PM</label>
                    <input
                      v-model="searchForm.pmName"
                      type="text"
                      class="input w-full"
                      placeholder="PM 이름 검색"
                    />
                  </div>

                  <div class="form-item">
                    <label>시작일</label>
                    <input
                      v-model="searchForm.startDate"
                      type="date"
                      class="input w-full"
                    />
                  </div>

                  <div class="form-item">
                    <label>종료일</label>
                    <input
                      v-model="searchForm.endDate"
                      type="date"
                      class="input w-full"
                    />
                  </div>
                </div>

                <div class="search-action-row">
                  <button type="button" @click="resetForm" class="btn-reset">
                    초기화
                  </button>
                  <button type="submit" class="btn-search">검색</button>
                </div>
              </form>
            </div>
          </div>

          <!-- 목록 패널 -->
          <div class="panel">
            <div class="panel-head list-head">
              <span class="panel-title">프로젝트 목록</span>
              <span class="count-badge">총 {{ filteredProjects.length }}건</span>
            </div>

            <div class="panel-body list-body">
              <div class="table-wrap">
                <table class="project-table">
                  <thead>
                    <tr>
                      <th class="text-left pl-4">프로젝트명</th>
                      <th class="text-center">식별자</th>
                      <th class="text-center">시작일</th>
                      <th class="text-center">종료일</th>
                      <th class="text-center">PM</th>
                      <th class="text-center">PL</th>
                      <th class="text-center">진행률</th>
                    </tr>
                  </thead>

                  <tbody>
                    <tr v-if="listLoading">
                      <td colspan="7" class="empty-cell">
                        ⌛ 로딩중입니다.
                      </td>
                    </tr>

                    <template v-else-if="pagedProjects.length > 0">
                      <tr
                        v-for="project in pagedProjects"
                        :key="project.projectId"
                        @click="goDetail(project)"
                        class="table-row"
                      >
                        <td>
                          <div class="project-name-cell">
                            {{ project.projectName }}
                          </div>
                        </td>
                        <td class="text-center">
                          {{ project.identifier || "-" }}
                        </td>
                        <td class="text-center">
                          {{ project.startDate || "-" }}
                        </td>
                        <td class="text-center">
                          {{ project.endDate || "-" }}
                        </td>
                        <td class="text-center">
                          {{ project.pmName || "-" }}
                        </td>
                        <td class="text-center">
                          {{ project.plName || "-" }}
                        </td>
                        <td>
                          <div class="progress-wrap">
                            <el-progress
                              :percentage="project.progressRate || 0"
                              :stroke-width="8"
                              :show-text="false"
                              style="flex: 1"
                              :color="
                                (project.progressRate || 0) >= 100
                                  ? '#10b981'
                                  : '#1B5C9C'
                              "
                            />
                            <span class="progress-text">
                              {{ project.progressRate || 0 }}%
                            </span>
                          </div>
                        </td>
                      </tr>
                    </template>

                    <tr v-else>
                      <td colspan="7" class="empty-cell">
                        프로젝트가 존재하지 않습니다.
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <div class="pagination-wrap">
                <el-pagination
                  :current-page="nowPage"
                  :page-size="pageSize"
                  :total="filteredProjects.length"
                  :hide-on-single-page="true"
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
import { computed, onBeforeMount, ref } from "vue";
import { useRouter } from "vue-router";
import Swal from "sweetalert2";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import api from "../utils/api";

const sidebarOpen = ref(false);
const router = useRouter();

const listLoading = ref(false);
const projectList = ref([]);

const nowPage = ref(1);
const pageSize = ref(10);

const searchForm = ref({
  projectName: "",
  pmName: "",
  startDate: "",
  endDate: "",
});

onBeforeMount(async () => {
  await fetchProjectList();
});

const fetchProjectList = async () => {
  listLoading.value = true;

  // Swal.fire({
  //   title: "잠시만 기다려주세요...",
  //   html: "데이터를 불러오는 중입니다.",
  //   allowOutsideClick: false,
  //   showConfirmButton: false,
  //   didOpen: () => {
  //     Swal.showLoading();
  //   },
  // });

  try {
    const res = await api.get("/ProjectList");
    projectList.value = res.data || [];
  } catch (err) {
    console.error("프로젝트 목록 조회 실패:", err);
    Swal.fire({
      icon: "error",
      title: "프로젝트 목록 조회 실패",
      text: "데이터를 불러오지 못했습니다.",
    });
  } finally {
    listLoading.value = false;
    Swal.close();
  }
};

const filteredProjects = computed(() => {
  return projectList.value.filter((project) => {
    const matchProjectName =
      !searchForm.value.projectName ||
      (project.projectName || "")
        .toLowerCase()
        .includes(searchForm.value.projectName.toLowerCase());

    const matchPmName =
      !searchForm.value.pmName ||
      (project.pmName || "")
        .toLowerCase()
        .includes(searchForm.value.pmName.toLowerCase());

    const matchStartDate =
      !searchForm.value.startDate ||
      (project.startDate && project.startDate >= searchForm.value.startDate);

    const matchEndDate =
      !searchForm.value.endDate ||
      (project.endDate && project.endDate <= searchForm.value.endDate);

    return matchProjectName && matchPmName && matchStartDate && matchEndDate;
  });
});

const pagedProjects = computed(() => {
  const start = (nowPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredProjects.value.slice(start, end);
});

const handleCurrentChange = (page) => {
  nowPage.value = page;
};

const searchProjects = () => {
  nowPage.value = 1;
};

const resetForm = () => {
  searchForm.value = {
    projectName: "",
    pmName: "",
    startDate: "",
    endDate: "",
  };
  nowPage.value = 1;
};

const goRegister = () => {
  router.push({ name: "projectRegister" });
};

const goDetail = (project) => {
  router.push({
    name: "projectDash",
    params: { projectId: project.projectId },
  });
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

/* 상단 제목 카드 */
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

.pg-title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #111827;
}

.proj-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.proj-desc {
  font-size: 13px;
  color: #6b7280;
}

/* 버튼 */
.btn-setting {
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

.btn-setting:hover {
  transform: translateY(-2px);
  filter: brightness(1.08);
}

.btn-search {
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 700;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  background: #1b5c9c;
  color: #fff;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(27, 92, 156, 0.22);
}

.btn-search:hover {
  background: #144677;
}

.btn-reset {
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 700;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  background: #ef4444;
  color: #fff;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.2);
}

.btn-reset:hover {
  background: #dc2626;
}

/* 패널 공통 */
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
}

/* 검색 */
.search-body {
  padding: 20px;
}

.search-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 13px;
  font-weight: 700;
  color: #4b5563;
}

.search-action-row {
  margin-top: 18px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 목록 */
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

.project-table {
  width: 100%;
  border-collapse: collapse;
}

.project-table thead {
  background: #f9fafb;
}

.project-table th {
  height: 46px;
  padding: 0 12px;
  font-size: 12px;
  font-weight: 600;
  color: #4b5563;
  border-bottom: 1px solid #eef2f7;
  white-space: nowrap;
}

.project-table td {
  padding: 14px 12px;
  font-size: 13px;
  color: #1f2937;
  border-bottom: 1px solid #f3f4f6;
  vertical-align: middle;
}

.project-name-cell {
  padding-left: 4px;
  font-weight: 700;
  color: #111827;
}

.table-row {
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.table-row:hover {
  background: #f9fbff;
}

.progress-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 150px;
}

.progress-text {
  font-size: 12px;
  font-weight: 700;
  color: #1b5c9c;
  min-width: 40px;
  text-align: right;
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

/* input */
:deep(.input) {
  height: 42px;
  border-radius: 10px !important;
  border: 1px solid #e2e8f0 !important;
  background: #f8fafc !important;
  transition:
    border-color 0.2s,
    box-shadow 0.2s,
    background 0.2s;
  font-size: 13px;
  padding: 0 12px;
  color: #111827;
}

:deep(.input:focus) {
  border-color: #93c5fd !important;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.12) !important;
  background: #fff !important;
  outline: none;
}

:deep(.el-pagination.is-background .btn-next),
:deep(.el-pagination.is-background .btn-prev),
:deep(.el-pagination.is-background .el-pager li) {
  border-radius: 8px;
}

@media (max-width: 1024px) {
  .search-grid {
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

  .pg-title {
    font-size: 20px;
  }

  .search-body {
    padding: 16px;
  }

  .search-grid {
    grid-template-columns: 1fr;
    gap: 14px;
  }

  .panel-head {
    padding: 14px 16px;
  }

  .search-action-row {
    justify-content: stretch;
    flex-direction: column;
  }

  .btn-search,
  .btn-reset {
    width: 100%;
  }

  .project-table th,
  .project-table td {
    white-space: nowrap;
  }
}
</style>