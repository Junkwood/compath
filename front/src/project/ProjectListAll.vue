<template>
  <div class="flex h-screen overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden">
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
          <!-- 상단 -->
          <div class="mb-6 proj-title-row flex justify-between">
            <div class="proj-title-left">
              <h2 class="text-2xl md:text-3xl text-gray-800 font-bold">
                전체 프로젝트 리스트
              </h2>
            </div>

            <div class="self-end">
              <el-button class="new-project-btn" @click="goRegister">
                + &nbsp; 프로젝트 생성
              </el-button>
            </div>
          </div>

          <!-- 검색 영역 -->
          <div class="col-span-full xl:col-span-8 bg-white shadow-xs rounded-xl mb-0 p-4">
            <form @submit.prevent="searchProjects">
              <div class="grid grid-cols-4 gap-4">
                <div class="mb-4">
                  <label class="block mb-2.5 text-sm font-medium text-heading">
                    프로젝트명
                  </label>
                  <input
                    v-model="searchForm.projectName"
                    type="text"
                    class="input w-full"
                    placeholder="프로젝트명 검색"
                  />
                </div>

                <div class="mb-4">
                  <label class="block mb-2.5 text-sm font-medium text-heading">
                    PM
                  </label>
                  <input
                    v-model="searchForm.pmName"
                    type="text"
                    class="input w-full"
                    placeholder="PM 이름 검색"
                  />
                </div>

                <div class="mb-4">
                  <label class="block mb-2.5 text-sm font-medium text-heading">
                    시작일
                  </label>
                  <input
                    v-model="searchForm.startDate"
                    type="date"
                    class="input w-full"
                  />
                </div>

                <div class="mb-4">
                  <label class="block mb-2.5 text-sm font-medium text-heading">
                    종료일
                  </label>
                  <input
                    v-model="searchForm.endDate"
                    type="date"
                    class="input w-full"
                  />
                </div>
              </div>

              <div class="flex flex-row-reverse gap-2 mt-2">
                <button type="submit" class="btn-navy">검색</button>
                <button type="button" @click="resetForm" class="btn-red">
                  초기화
                </button>
              </div>
            </form>
          </div>

          <!-- 테이블 -->
          <div class="col-span-full xl:col-span-8 bg-white shadow-xs rounded-xl mt-4">
            <div>
              <div class="flex flex-row-reverse">
                <span class="member-role-badge my-2">총 {{ filteredProjects.length }}건</span>
              </div>

              <table class="table-auto w-full dark:text-gray-300">
                <thead
                  class="text-xs uppercase text-gray-400 bg-gray-50 rounded-xs"
                >
                  <tr>
                    <th class="p-3"><div class="text-left pl-4">프로젝트명</div></th>
                    <th class="p-3"><div class="text-center">식별자</div></th>
                    <th class="p-3"><div class="text-center">시작일</div></th>
                    <th class="p-3"><div class="text-center">종료일</div></th>
                    <th class="p-3"><div class="text-center">PM</div></th>
                    <th class="p-3"><div class="text-center">PL</div></th>
                    <th class="p-3"><div class="text-center">진행률</div></th>
                  </tr>
                </thead>

                <tbody class="text-sm font-medium divide-y divide-gray-100">
                  <tr v-if="listLoading">
                    <td colspan="7" class="text-center py-10">
                      <h5 class="text-gray-500">⌛ 로딩중입니다.</h5>
                    </td>
                  </tr>

                  <template v-else-if="pagedProjects.length > 0">
                    <tr
                      v-for="project in pagedProjects"
                      :key="project.projectId"
                      @click="goDetail(project)"
                      class="table-row"
                    >
                      <td class="p-3">
                        <div class="text-left pl-4 font-semibold text-gray-800">
                          {{ project.projectName }}
                        </div>
                      </td>
                      <td class="p-3">
                        <div class="text-center">
                          {{ project.identifier || "-" }}
                        </div>
                      </td>
                      <td class="p-3">
                        <div class="text-center">
                          {{ project.startDate || "-" }}
                        </div>
                      </td>
                      <td class="p-3">
                        <div class="text-center">
                          {{ project.endDate || "-" }}
                        </div>
                      </td>
                      <td class="p-3">
                        <div class="text-center">
                          {{ project.pmName || "-" }}
                        </div>
                      </td>
                      <td class="p-3">
                        <div class="text-center">
                          {{ project.plName || "-" }}
                        </div>
                      </td>
                      <td class="p-3">
                        <div class="progress-wrap">
                          <el-progress
                            :percentage="project.progressRate || 0"
                            :stroke-width="8"
                            :show-text="false"
                            style="flex: 1"
                          />
                          <span class="progress-text">
                            {{ project.progressRate || 0 }}%
                          </span>
                        </div>
                      </td>
                    </tr>
                  </template>

                  <tr v-else>
                    <td colspan="7" class="text-center py-10">
                      <h5 class="text-gray-500">프로젝트가 존재하지 않습니다</h5>
                    </td>
                  </tr>
                </tbody>
              </table>

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

.btn-navy {
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  background: #1e3a5f;
  color: #fff;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(30, 58, 95, 0.25);
}
.btn-navy:hover {
  background: #162d4a;
}

.btn-red {
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  background: #dc2626;
  color: #fff;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(220, 38, 38, 0.25);
}
.btn-red:hover {
  background: #b91c1c;
}

.progress-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
}
.progress-text {
  font-size: 12px;
  font-weight: 600;
  color: #2563eb;
  min-width: 32px;
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 12px 0;
  border-top: 1px solid #f0f0f0;
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
.member-role-badge {
  font-size: 15px;
  font-weight: 600;
  padding: 2px 7px;
  border-radius: 99px;
  letter-spacing: 0.03em;
  margin-right: 15px;
  background: #dbeafe;
  color: #1d4ed8;
}

:deep(.input) {
  border-radius: 10px !important;
  border: 1px solid #e2e8f0 !important;
  background: #f8fafc !important;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
  font-size: 13px;
  padding: 10px 12px;
}
:deep(.input:focus) {
  border-color: #94a3b8 !important;
  box-shadow: 0 0 0 3px rgba(148, 163, 184, 0.15) !important;
  background: #fff !important;
  outline: none;
}

.table-row:hover {
  background-color: #f9fafb;
  cursor: pointer;
  transition: background-color 0.2s;
}
</style>