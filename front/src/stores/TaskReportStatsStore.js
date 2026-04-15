import { defineStore } from "pinia";
import { ref, computed } from "vue";
import api from "../utils/api";

const TYPE_COLORS = {
  개발: "#3b82f6",
  기획: "#f59e0b",
  디자인: "#f97316",
  테스트: "#10b981",
  운영: "#8b5cf6",
  기타: "#94a3b8",
};

const PROGRESS_BANDS = [
  { label: "완료 (100%)", min: 100, max: 100, color: "#10b981" },
  { label: "진행중 (50~99%)", min: 50, max: 99, color: "#3b82f6" },
  { label: "시작 (1~49%)", min: 1, max: 49, color: "#f59e0b" },
  { label: "미시작 (0%)", min: 0, max: 0, color: "#e5e7eb" },
];

export const useTaskReportStatsStore = defineStore("taskReportStats", () => {
  // ── state ──
  const loading = ref(false);
  const allTasks = ref([]);
  const projectList = ref([]);
  const selectedProject = ref("");
  const filterStart = ref("");
  const filterEnd = ref("");

  // ── fetch ──
  const fetchData = async (projectId) => {
    loading.value = true;
    try {
      const { data } = await api.get("/taskReport", { params: { projectId } });

      allTasks.value = data.map((r) => ({
        taskId: r.taskId,
        title: r.title ?? "-",
        projectId: String(r.projectId ?? ""),
        projectName: r.projectName ?? "-",
        typeName: r.typeName ?? "기타",
        assignee: r.assigneeName ?? r.userName ?? "-",
        progressRate: Number(r.progressRate ?? 0),
        startDate: r.startDate ?? null,
        dueDate: r.dueDate ?? null,
        status: r.taskStatusId ?? "-",
      }));

      const seen = new Set();
      projectList.value = data
        .filter((r) => {
          if (!r.projectId || seen.has(r.projectId)) return false;
          if (String(r.projectId) === String(projectId)) return false; // 상위 제외
          seen.add(r.projectId);
          return true;
        })
        .map((r) => ({
          projectId: String(r.projectId),
          projectName: r.projectName,
        }));

      selectedProject.value = "";
    } catch (e) {
      console.error("업무 통계 조회 실패", e);
      allTasks.value = [];
      projectList.value = [];
    } finally {
      loading.value = false;
    }
  };

  // ── reset ──
  const reset = () => {
    allTasks.value = [];
    projectList.value = [];
    selectedProject.value = "";
    filterStart.value = "";
    filterEnd.value = "";
  };

  // ── computed ──
  const projectOptions = computed(() =>
    projectList.value.map((p) => ({
      value: String(p.projectId),
      label: p.projectName,
    })),
  );

  const selectedProjectLabel = computed(() => {
    if (!selectedProject.value) return "전체 프로젝트";
    return (
      projectOptions.value.find((p) => p.value === selectedProject.value)
        ?.label ?? "전체"
    );
  });

  const periodLabel = computed(() => {
    if (filterStart.value && filterEnd.value)
      return `${filterStart.value} ~ ${filterEnd.value}`;
    return "전체 기간";
  });

  const filteredTasks = computed(() => {
    let list = allTasks.value;
    if (selectedProject.value)
      list = list.filter((t) => t.projectId === selectedProject.value);
    if (filterStart.value)
      list = list.filter((t) => t.dueDate && t.dueDate >= filterStart.value);
    if (filterEnd.value)
      list = list.filter((t) => t.dueDate && t.dueDate <= filterEnd.value);
    return list;
  });

  const uniqueAssignees = computed(
    () => new Set(filteredTasks.value.map((t) => t.assignee)).size,
  );

  const completedCount = computed(
    () => filteredTasks.value.filter((t) => t.progressRate === 100).length,
  );

  const avgProgress = computed(() => {
    if (!filteredTasks.value.length) return 0;
    const sum = filteredTasks.value.reduce((s, t) => s + t.progressRate, 0);
    return Math.round(sum / filteredTasks.value.length);
  });

  const progressBands = computed(() =>
    PROGRESS_BANDS.map((b) => ({
      ...b,
      count: filteredTasks.value.filter((t) =>
        b.min === b.max
          ? t.progressRate === b.min
          : t.progressRate >= b.min && t.progressRate <= b.max,
      ).length,
    })),
  );

  const typeCountMap = computed(() => {
    const map = {};
    filteredTasks.value.forEach((t) => {
      map[t.typeName] = (map[t.typeName] || 0) + 1;
    });
    return Object.entries(map).sort((a, b) => b[1] - a[1]);
  });

  const typeDonutData = computed(() =>
    typeCountMap.value.map(([label, val]) => ({
      label,
      val,
      color: TYPE_COLORS[label] || "#94a3b8",
    })),
  );

  const allAssigneeCounts = computed(() => {
    const map = {};
    filteredTasks.value.forEach((t) => {
      map[t.assignee] = (map[t.assignee] || 0) + 1;
    });
    return Object.entries(map).sort((a, b) => b[1] - a[1]);
  });

  const dateCounts = computed(() => {
    const map = {};
    filteredTasks.value.forEach((t) => {
      const d = t.dueDate ? String(t.dueDate).substring(0, 10) : null;
      if (d) map[d] = (map[d] || 0) + 1;
    });
    return Object.entries(map).sort((a, b) => a[0].localeCompare(b[0]));
  });

  const projectProgressData = computed(() => {
    const map = {};
    allTasks.value.forEach((t) => {
      if (!map[t.projectName]) map[t.projectName] = [];
      map[t.projectName].push(t.progressRate);
    });
    return Object.entries(map)
      .map(([name, arr]) => ({
        name,
        avg: Math.round(arr.reduce((s, v) => s + v, 0) / arr.length),
      }))
      .sort((a, b) => b.avg - a.avg);
  });

  const kpiCards = computed(() => [
    {
      label: "전체 업무",
      value: filteredTasks.value.length + "건",
      sub: "조회 결과",
      bg: "#eff6ff",
      trendUp: true,
      icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none"><rect x="3" y="3" width="7" height="7" rx="1.5" fill="#3b82f6"/><rect x="14" y="3" width="7" height="7" rx="1.5" fill="#3b82f6"/><rect x="3" y="14" width="7" height="7" rx="1.5" fill="#3b82f6"/><rect x="14" y="14" width="7" height="7" rx="1.5" fill="#3b82f6"/></svg>',
    },
    {
      label: "완료 업무",
      value: completedCount.value + "건",
      sub:
        Math.round(
          (completedCount.value / (filteredTasks.value.length || 1)) * 100,
        ) + "% 완료율",
      bg: "#f0fdf4",
      trendUp: true,
      icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="9" fill="#10b981"/><path d="M8 12l3 3 5-5" stroke="#fff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
    },
    {
      label: "평균 진척도",
      value: avgProgress.value + "%",
      sub: "전체 업무 기준",
      bg: "#fef3c7",
      trendUp: avgProgress.value >= 50,
      icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M3 17l4-4 4 4 4-6 4-4" stroke="#f59e0b" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
    },
    {
      label: "참여 담당자",
      value: uniqueAssignees.value + "명",
      sub: "활성 담당자",
      bg: "#faf5ff",
      trendUp: true,
      icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none"><circle cx="9" cy="7" r="4" fill="#8b5cf6"/><path d="M3 21v-2a4 4 0 014-4h4a4 4 0 014 4v2" stroke="#8b5cf6" stroke-width="2" stroke-linecap="round"/><path d="M16 3c1.66 0 3 1.34 3 3s-1.34 3-3 3" stroke="#8b5cf6" stroke-width="1.8" stroke-linecap="round"/><path d="M21 21v-2a4 4 0 00-3-3.87" stroke="#8b5cf6" stroke-width="1.8" stroke-linecap="round"/></svg>',
    },
  ]);

  const statusStats = computed(() => [
    {
      label: "미시작",
      value:
        filteredTasks.value.filter((t) => t.progressRate === 0).length + "건",
      color: "#94a3b8",
    },
    {
      label: "진행중",
      value:
        filteredTasks.value.filter(
          (t) => t.progressRate > 0 && t.progressRate < 100,
        ).length + "건",
      color: "#3b82f6",
    },
    { label: "완료", value: completedCount.value + "건", color: "#10b981" },
    {
      label: "지연",
      value:
        filteredTasks.value.filter(
          (t) =>
            t.dueDate &&
            String(t.dueDate).substring(0, 10) <
              new Date().toISOString().substring(0, 10) &&
            t.progressRate < 100,
        ).length + "건",
      color: "#ef4444",
    },
  ]);

  const progressColor = (pct) => {
    if (pct >= 80) return "#10b981";
    if (pct >= 50) return "#3b82f6";
    if (pct >= 20) return "#f59e0b";
    return "#ef4444";
  };

  return {
    // state
    loading,
    allTasks,
    projectList,
    selectedProject,
    filterStart,
    filterEnd,
    // actions
    fetchData,
    reset,
    // computed
    projectOptions,
    selectedProjectLabel,
    periodLabel,
    filteredTasks,
    uniqueAssignees,
    completedCount,
    avgProgress,
    progressBands,
    typeCountMap,
    typeDonutData,
    allAssigneeCounts,
    dateCounts,
    projectProgressData,
    kpiCards,
    statusStats,
    progressColor,
    // constants
    TYPE_COLORS,
  };
});
