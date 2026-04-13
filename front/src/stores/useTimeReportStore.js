import { defineStore } from "pinia";
import { ref, computed } from "vue";
import api from "../utils/api";

export const useTimeReportStore = defineStore("timeReport", () => {
  // ───────────── 상태 ─────────────
  const loading = ref(false);
  const currentPage = ref(1);
  const itemsPerPage = ref(10);
  const selectAll = ref(false);

  const reportList = ref([]);
  const assigneeList = ref([]);
  const taskTypeList = ref([]);
  const subProjectList = ref([]);

  const rootProjectId = ref(null);

  const filters = ref({
    projectId: "",
    assigneeUserId: "",
    taskTypeId: "",
    keyword: "",
    dateFrom: "2026-03-01",
    dateTo: "2026-06-30",
  });

  // ───────────── computed ─────────────
  const paginatedList = computed(() => {
    const s = (currentPage.value - 1) * itemsPerPage.value;
    return reportList.value.slice(s, s + itemsPerPage.value);
  });

  const totalPages = computed(
    () => Math.ceil(reportList.value.length / itemsPerPage.value) || 1,
  );

  const totalHours = computed(() =>
    reportList.value.reduce((sum, t) => sum + (t.totalHours || 0), 0),
  );

  // ───────────── 메서드 ─────────────
  const fetchSubProjectList = async () => {
    try {
      const { data } = await api.get("/project/list");
      const all = (data ?? []).map((p) => ({
        projectId: Number(p.projectId ?? p.PROJECT_ID),
        projectName: p.projectName ?? p.PROJECT_NAME,
        parentProjectId:
          (p.parentProjectId ?? p.PARENT_PROJECT_ID)
            ? Number(p.parentProjectId ?? p.PARENT_PROJECT_ID)
            : null,
      }));
      subProjectList.value = all.filter(
        (p) => p.parentProjectId === rootProjectId.value,
      );
    } catch (e) {
      console.error("프로젝트 목록 조회 실패", e);
    }
  };

  const fetchReport = async () => {
    loading.value = true;
    try {
      const { data } = await api.get("/report/time-entry", {
        params: {
          projectId: filters.value.projectId || rootProjectId.value || null,
          assigneeUserId: filters.value.assigneeUserId || null,
          taskTypeId: filters.value.taskTypeId || null,
          keyword: filters.value.keyword || null,
          dateFrom: filters.value.dateFrom || null,
          dateTo: filters.value.dateTo || null,
        },
      });

      reportList.value = data.reportList ?? [];
      assigneeList.value = data.assigneeList ?? [];
      taskTypeList.value = data.taskTypeList ?? [];
      currentPage.value = 1;
    } catch (e) {
      console.error("타임 리포트 조회 실패", e);
    } finally {
      loading.value = false;
    }
  };

  const resetFilter = () => {
    filters.value = {
      projectId: "",
      assigneeUserId: "",
      taskTypeId: "",
      keyword: "",
      dateFrom: "2026-03-01",
      dateTo: "2026-06-30",
    };
    fetchReport();
  };

  const formatDate = (val) => {
    if (!val) return "-";
    return String(val).substring(0, 10).replace(/-/g, ".");
  };

  const goToPage = (p) => {
    currentPage.value = p;
  };
  const prevPage = () => {
    if (currentPage.value > 1) currentPage.value--;
  };
  const nextPage = () => {
    if (currentPage.value < totalPages.value) currentPage.value++;
  };

  const init = async (projectId) => {
    rootProjectId.value = projectId ? Number(projectId) : null;
    await fetchSubProjectList();
    await fetchReport();
  };

  return {
    loading,
    currentPage,
    itemsPerPage,
    selectAll,
    reportList,
    assigneeList,
    taskTypeList,
    subProjectList,
    rootProjectId,
    filters,
    paginatedList,
    totalPages,
    totalHours,
    init,
    fetchReport,
    fetchSubProjectList,
    resetFilter,
    formatDate,
    goToPage,
    prevPage,
    nextPage,
  };
});
