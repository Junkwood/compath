<template>
  <div class="flex h-screen overflow-hidden bg-gray-50 dark:bg-gray-900">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />
    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden"
    >
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="page">
        <!-- 서브헤더 -->
        <div class="sub-header">
          <div class="sub-header-left">
            <div class="breadcrumb">
              <span>홈</span><span class="bc-sep">›</span> <span>프로젝트</span
              ><span class="bc-sep">›</span> <span>타임 리포트</span
              ><span class="bc-sep">›</span>
              <span class="bc-cur">타임 엔트리</span>
            </div>
          </div>

          <div class="sub-header-right">
            <button class="btn-back" @click="goBack">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
                <path
                  d="M19 12H5M11 6l-6 6 6 6"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
              목록으로
            </button>
          </div>
        </div>
        <div class="page-inner">
          <!-- ── ① 프로젝트 헤더 바 ── -->
          <div class="proj-header-bar">
            <div class="proj-info">
              <span class="proj-label">프로젝트 명</span>
              <div class="select-wrap">
                <select
                  v-model="selectedProject"
                  @change="selectProject(selectedProject)"
                >
                  <option
                    v-for="p in projectOptions"
                    :key="p.value"
                    :value="p.value"
                  >
                    {{ p.label }}
                  </option>
                </select>
                <span class="select-arrow">▾</span>
              </div>
            </div>
            <div class="period-wrap">
              <svg
                width="13"
                height="13"
                viewBox="0 0 24 24"
                fill="none"
                style="color: #64748b"
              >
                <rect
                  x="3"
                  y="4"
                  width="18"
                  height="18"
                  rx="2"
                  stroke="currentColor"
                  stroke-width="2"
                />
                <path
                  d="M16 2v4M8 2v4M3 10h18"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                />
              </svg>
              <span class="period-text">{{ periodLabel }}</span>
            </div>
          </div>

          <!-- ── 메인 대시보드 그리드 ── -->
          <div class="dashboard-grid">
            <div class="charts-left">
              <div class="chart-row">
                <div class="chart-card">
                  <div class="chart-card-header">
                    <span class="chart-card-title">소요시간 프로젝트</span>
                    <span class="chart-unit"
                      >US (Hour) · {{ selectedProjectLabel }}</span
                    >
                  </div>
                  <div class="chart-canvas-wrap">
                    <canvas ref="barChart1"></canvas>
                  </div>
                  <div class="chart-legend">
                    <span
                      v-for="item in barLegend"
                      :key="item.label"
                      class="legend-item"
                    >
                      <span
                        class="legend-sq"
                        :style="{ background: item.color }"
                      ></span>
                      {{ item.label }}
                    </span>
                  </div>
                </div>

                <div class="chart-card">
                  <div class="chart-card-header">
                    <span class="chart-card-title">담당자별 투입 현황</span>
                    <span class="chart-unit"
                      >US (Hour) · {{ selectedProjectLabel }}</span
                    >
                  </div>
                  <div class="chart-canvas-wrap">
                    <canvas ref="hbarChart1"></canvas>
                  </div>
                  <div class="hbar-footer">
                    <span class="hbar-count"
                      >담당자 합계 = {{ filteredUniqueAssignees }}명</span
                    >
                    <div class="mini-pager">
                      <button
                        class="mpg"
                        @click="hPage1 = Math.max(1, hPage1 - 1)"
                      >
                        ‹
                      </button>
                      <button
                        v-for="p in hTotalPages1"
                        :key="p"
                        class="mpg"
                        :class="{ on: p === hPage1 }"
                        @click="hPage1 = p"
                      >
                        {{ p }}
                      </button>
                      <button
                        class="mpg"
                        @click="hPage1 = Math.min(hTotalPages1, hPage1 + 1)"
                      >
                        ›
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <div class="chart-row">
                <div class="chart-card">
                  <div class="chart-card-header">
                    <span class="chart-card-title">날짜별 소요시간 추이</span>
                    <span class="chart-unit"
                      >US (Hour) · {{ selectedProjectLabel }}</span
                    >
                  </div>
                  <div class="chart-canvas-wrap">
                    <canvas ref="lineChart"></canvas>
                  </div>
                  <div class="chart-legend">
                    <span class="legend-item">
                      <span
                        class="legend-sq"
                        style="background: #3b82f6; border-radius: 50%"
                      ></span>
                      일별 투입시간
                    </span>
                  </div>
                </div>

                <div class="chart-card">
                  <div class="chart-card-header">
                    <span class="chart-card-title">업무명별 소요시간</span>
                    <span class="chart-unit"
                      >US (Hour) · {{ selectedProjectLabel }}</span
                    >
                  </div>
                  <div class="chart-canvas-wrap">
                    <canvas ref="hbarChart2"></canvas>
                  </div>
                  <div class="hbar-footer">
                    <span class="hbar-count"
                      >업무 합계 = {{ chartEntries.length }}건</span
                    >
                    <div class="mini-pager">
                      <button
                        class="mpg"
                        @click="hPage2 = Math.max(1, hPage2 - 1)"
                      >
                        ‹
                      </button>
                      <button
                        v-for="p in hTotalPages2"
                        :key="p"
                        class="mpg"
                        :class="{ on: p === hPage2 }"
                        @click="hPage2 = p"
                      >
                        {{ p }}
                      </button>
                      <button
                        class="mpg"
                        @click="hPage2 = Math.min(hTotalPages2, hPage2 + 1)"
                      >
                        ›
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 오른쪽 도넛+통계 -->
            <div class="charts-right">
              <div class="chart-card donut-card">
                <div
                  class="chart-card-header"
                  style="
                    justify-content: center;
                    flex-direction: column;
                    align-items: center;
                    gap: 2px;
                  "
                >
                  <span class="chart-card-title">업무 유형별 비중</span>
                  <span style="font-size: 11px; color: #94a3b8">{{
                    selectedProjectLabel
                  }}</span>
                </div>
                <div class="donut-canvas-wrap">
                  <canvas ref="donutChart"></canvas>
                  <div class="donut-center">
                    <span class="donut-total">{{ chartTotalHours }}h</span>
                    <span class="donut-total-lbl">총시간</span>
                  </div>
                </div>
                <div class="donut-legend">
                  <span
                    v-for="item in donutData"
                    :key="item.label"
                    class="donut-leg-item"
                  >
                    <span
                      class="legend-sq"
                      :style="{ background: item.color }"
                    ></span>
                    <span>{{ item.label }}</span>
                  </span>
                </div>
              </div>

              <div class="stat-grid">
                <div class="stat-card">
                  <div class="stat-val">{{ chartTotalHours }}h</div>
                  <div class="stat-lbl">총 소요시간</div>
                </div>
                <div class="stat-card">
                  <div class="stat-val">{{ chartEntries.length }}</div>
                  <div class="stat-lbl">총 엔트리</div>
                </div>
                <div class="stat-card">
                  <div class="stat-val">{{ filteredUniqueAssignees }}</div>
                  <div class="stat-lbl">참여 담당자</div>
                </div>
                <div class="stat-card">
                  <div class="stat-val">{{ filteredUniqueProjects }}</div>
                  <div class="stat-lbl">프로젝트</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import {
  defineComponent,
  ref,
  computed,
  watch,
  onMounted,
  nextTick,
} from "vue";
import { useRoute, useRouter } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import Chart from "chart.js/auto";
import api from "../utils/api";

export default defineComponent({
  name: "TimeEntries",
  components: { Sidebar, Header },

  setup() {
    const route = useRoute();
    const router = useRouter();
    const taskId = route.params.taskId;
    const projectId = route.params.projectId;

    /* ─── canvas refs ─── */
    const barChart1 = ref(null);
    const hbarChart1 = ref(null);
    const lineChart = ref(null);
    const hbarChart2 = ref(null);
    const donutChart = ref(null);

    /* ─── state ─── */
    const sidebarOpen = ref(false);
    const allEntries = ref([]);
    const taskInfo = ref(null);
    const projectList = ref([]);

    const selectedProject = ref("");
    const hPage1 = ref(1);
    const hPage2 = ref(1);
    const HB_PER = 5;

    const donutColors = {
      개발: "#3b82f6",
      기획: "#f59e0b",
      디자인: "#f97316",
      기타: "#94a3b8",
    };

    function goBack() {
      router.push({ name: "TimeReport", params: { projectId } });
    }

    /* ─── fetchData ─── */
    const fetchData = async () => {
      try {
        const { data } = await api.get("/task-total-info", {
          params: { taskId },
        });
        taskInfo.value = data.taskDetail?.[0]
          ? {
              ...data.taskDetail[0],
              startDate: data.taskDetail[0].estStartDate,
              dueDate: data.taskDetail[0].estEndDate,
            }
          : null;
        projectList.value = data.projectList ?? [];

        const { data: reportData } = await api.get("/report/time-entry", {
          params: { projectId },
        });
        // 소요시간이 있는 업무만 표시
        allEntries.value = (reportData.reportList ?? [])
          .filter((r) => Number(r.totalHours ?? 0) > 0)
          .map((r) => ({
            id: r.taskId,
            project: r.parentProjectName ?? r.projectName ?? "-",
            projectId: String(r.projectId ?? ""),
            name: r.title ?? "-",
            date: formatDate(r.lastWorkDate),
            type: r.typeName ?? "-",
            assignee: r.userName ?? "-",
            hours: Number(r.totalHours ?? 0),
          }));
      } catch (e) {
        console.error("타임 엔트리 조회 실패", e);
      }
    };

    function formatDate(val) {
      if (!val) return "-";
      return String(val).substring(0, 10).replace(/-/g, ".");
    }

    /* ─── computed: 프로젝트 옵션 ─── */
    const projectOptions = computed(() => {
      const opts = [{ value: "", label: "전체 프로젝트" }];
      projectList.value.forEach((p) =>
        opts.push({ value: String(p.projectId), label: p.projectName }),
      );
      return opts;
    });

    const selectedProjectLabel = computed(() => {
      if (!selectedProject.value) return "전체 프로젝트";
      return (
        projectOptions.value.find((p) => p.value === selectedProject.value)
          ?.label ?? "전체 프로젝트"
      );
    });

    const barLegend = computed(() =>
      projectOptions.value.slice(1).map((p, i) => ({
        label: p.label,
        color: ["#3b82f6", "#94a3b8", "#cbd5e1"][i] ?? "#cbd5e1",
      })),
    );

    const periodLabel = computed(() => {
      if (!taskInfo.value) return "-";
      const s = taskInfo.value.startDate
        ? formatDate(taskInfo.value.startDate)
        : "?";
      const e = taskInfo.value.dueDate
        ? formatDate(taskInfo.value.dueDate)
        : "?";
      return `${s} ~ ${e}`;
    });

    /* ─── computed: 차트용 ─── */
    const chartEntries = computed(() => {
      if (!selectedProject.value) return allEntries.value;
      return allEntries.value.filter(
        (e) => e.projectId === selectedProject.value,
      );
    });

    const chartTotalHours = computed(() =>
      chartEntries.value.reduce((s, e) => s + Number(e.hours || 0), 0),
    );
    const filteredUniqueAssignees = computed(
      () => new Set(chartEntries.value.map((e) => e.assignee)).size,
    );
    const filteredUniqueProjects = computed(
      () => new Set(chartEntries.value.map((e) => e.projectId)).size,
    );

    const projectHours = computed(() => {
      const map = {};
      chartEntries.value.forEach((e) => {
        map[e.project] = (map[e.project] || 0) + Number(e.hours || 0);
      });
      return Object.entries(map);
    });

    const allAssigneeHours = computed(() => {
      const map = {};
      chartEntries.value.forEach((e) => {
        map[e.assignee] = (map[e.assignee] || 0) + Number(e.hours || 0);
      });
      return Object.entries(map).sort((a, b) => b[1] - a[1]);
    });
    const hTotalPages1 = computed(() =>
      Math.max(1, Math.ceil(allAssigneeHours.value.length / HB_PER)),
    );
    const pagedAssigneeHours = computed(() => {
      const s = (hPage1.value - 1) * HB_PER;
      return allAssigneeHours.value.slice(s, s + HB_PER);
    });

    const allDateHours = computed(() => {
      const map = {};
      chartEntries.value.forEach((e) => {
        map[e.date] = (map[e.date] || 0) + Number(e.hours || 0);
      });
      return Object.entries(map).sort((a, b) => a[0].localeCompare(b[0]));
    });

    const allTaskHours = computed(() => {
      const map = {};
      chartEntries.value.forEach((e) => {
        map[e.name] = (map[e.name] || 0) + Number(e.hours || 0);
      });
      return Object.entries(map).sort((a, b) => b[1] - a[1]);
    });
    const hTotalPages2 = computed(() =>
      Math.max(1, Math.ceil(allTaskHours.value.length / HB_PER)),
    );
    const pagedTaskHours = computed(() => {
      const s = (hPage2.value - 1) * HB_PER;
      return allTaskHours.value.slice(s, s + HB_PER);
    });

    const donutData = computed(() => {
      const typeHours = {};
      chartEntries.value.forEach((e) => {
        typeHours[e.type] = (typeHours[e.type] || 0) + Number(e.hours || 0);
      });
      return Object.entries(typeHours).map(([label, val]) => ({
        label,
        val,
        color: donutColors[label] || "#94a3b8",
      }));
    });

    /* ─── 차트 인스턴스 ─── */
    let chartInstances = {};
    function destroyChart(key) {
      if (chartInstances[key]) {
        chartInstances[key].destroy();
        delete chartInstances[key];
      }
    }
    const CD = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
    };
    const AX = {
      ticks: { color: "#94a3b8", font: { size: 10 } },
      grid: { color: "rgba(0,0,0,0.05)" },
      border: { display: false },
    };

    function buildBarChart() {
      destroyChart("bar1");
      if (!barChart1.value) return;
      const colors = ["#3b82f6", "#94a3b8", "#cbd5e1"];
      chartInstances.bar1 = new Chart(barChart1.value, {
        type: "bar",
        data: {
          labels: projectHours.value.map(([p]) => p),
          datasets: [
            {
              data: projectHours.value.map(([, h]) => h),
              backgroundColor: projectHours.value.map(
                (_, i) => colors[i] || "#cbd5e1",
              ),
              borderRadius: 3,
              barThickness: 36,
            },
          ],
        },
        options: {
          ...CD,
          scales: {
            x: AX,
            y: {
              ...AX,
              min: 0,
              ticks: { ...AX.ticks, callback: (v) => v + "h" },
            },
          },
        },
      });
    }
    function buildHBar1() {
      destroyChart("hbar1");
      if (!hbarChart1.value) return;
      const data = pagedAssigneeHours.value;
      chartInstances.hbar1 = new Chart(hbarChart1.value, {
        type: "bar",
        data: {
          labels: data.map(([a]) => a),
          datasets: [
            {
              data: data.map(([, h]) => h),
              backgroundColor: "#3b82f6",
              borderRadius: 3,
              barThickness: 16,
            },
          ],
        },
        options: {
          ...CD,
          indexAxis: "y",
          scales: {
            x: {
              ...AX,
              min: 0,
              ticks: { ...AX.ticks, callback: (v) => v + "h" },
            },
            y: { ...AX, grid: { display: false } },
          },
        },
      });
    }
    function buildLineChart() {
      destroyChart("line");
      if (!lineChart.value) return;
      const data = allDateHours.value;
      chartInstances.line = new Chart(lineChart.value, {
        type: "line",
        data: {
          labels: data.map(([d]) => d),
          datasets: [
            {
              data: data.map(([, h]) => h),
              borderColor: "#3b82f6",
              backgroundColor: "rgba(59,130,246,0.08)",
              pointBackgroundColor: "#3b82f6",
              pointRadius: 4,
              tension: 0.3,
              fill: true,
            },
          ],
        },
        options: {
          ...CD,
          scales: {
            x: AX,
            y: {
              ...AX,
              min: 0,
              ticks: { ...AX.ticks, callback: (v) => v + "h" },
            },
          },
        },
      });
    }
    function buildHBar2() {
      destroyChart("hbar2");
      if (!hbarChart2.value) return;
      const data = pagedTaskHours.value;
      chartInstances.hbar2 = new Chart(hbarChart2.value, {
        type: "bar",
        data: {
          labels: data.map(([n]) => (n.length > 8 ? n.slice(0, 8) + "…" : n)),
          datasets: [
            {
              data: data.map(([, h]) => h),
              backgroundColor: "#6366f1",
              borderRadius: 3,
              barThickness: 16,
            },
          ],
        },
        options: {
          ...CD,
          indexAxis: "y",
          scales: {
            x: {
              ...AX,
              min: 0,
              ticks: { ...AX.ticks, callback: (v) => v + "h" },
            },
            y: { ...AX, grid: { display: false } },
          },
        },
      });
    }
    function buildDonut() {
      destroyChart("donut");
      if (!donutChart.value) return;
      const data = donutData.value;
      chartInstances.donut = new Chart(donutChart.value, {
        type: "doughnut",
        data: {
          labels: data.map((d) => d.label),
          datasets: [
            {
              data: data.map((d) => d.val),
              backgroundColor: data.map((d) => d.color),
              borderWidth: 2,
              borderColor: "#ffffff",
            },
          ],
        },
        options: {
          ...CD,
          cutout: "58%",
          plugins: {
            legend: { display: false },
            tooltip: {
              callbacks: { label: (ctx) => ` ${ctx.label}: ${ctx.parsed}h` },
            },
          },
        },
      });
    }
    function rebuildAllCharts() {
      nextTick(() => {
        buildBarChart();
        buildHBar1();
        buildLineChart();
        buildHBar2();
        buildDonut();
      });
    }

    /* ─── watchers ─── */
    watch(allEntries, () => nextTick(rebuildAllCharts));
    watch(selectedProject, () => {
      hPage1.value = 1;
      hPage2.value = 1;
      nextTick(rebuildAllCharts);
    });
    watch(hPage1, () => nextTick(buildHBar1));
    watch(hPage2, () => nextTick(buildHBar2));

    /* ─── lifecycle ─── */
    onMounted(async () => {
      await fetchData();
      console.log("taskInfo", taskInfo.value);

      rebuildAllCharts();
    });

    function selectProject(val) {
      selectedProject.value = val;
    }

    return {
      barChart1,
      hbarChart1,
      lineChart,
      hbarChart2,
      donutChart,
      sidebarOpen,
      taskInfo,
      periodLabel,
      projectOptions,
      selectedProject,
      selectedProjectLabel,
      barLegend,
      chartEntries,
      chartTotalHours,
      filteredUniqueAssignees,
      filteredUniqueProjects,
      donutData,
      hPage1,
      hPage2,
      hTotalPages1,
      hTotalPages2,
      goBack,
      selectProject,
    };
  },
});
</script>

<style scoped>
.sub-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  padding: 12px 32px;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 30;
}
.sub-header-left {
  display: flex;
  align-items: center;
  gap: 14px;
}
.btn-back {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  height: 30px;
  padding: 0 12px;
  font-size: 13px;
  font-weight: 600;
  background: #fff;
  color: #334155;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.15s;
}
.btn-back:hover {
  background: #f1f5f9;
  border-color: #94a3b8;
  color: #0f172a;
}

*,
*::before,
*::after {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

.page {
  padding: 0;
  color: #0f172a;
  font-family: "Pretendard", "Noto Sans KR", sans-serif;
}

/* ── 서브헤더 ── */
.sub-header {
  background: #ffffff;
  padding: 14px 32px;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 30;
}
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #475569;
}
.bc-sep {
  color: #cbd5e1;
}
.bc-cur {
  color: #0f172a;
  font-weight: 700;
}
.bc-link {
  color: #475569;
  cursor: pointer;
  transition: color 0.15s;
}
.bc-link:hover {
  color: #2563eb;
}
.sub-header-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
}
.meta-title {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
}
.meta-sub {
  font-size: 13px;
  color: #64748b;
}

/* ── 페이지 내부 ── */
.page-inner {
  padding: 20px 28px 24px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.proj-header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 12px 18px;
}
.proj-info {
  display: flex;
  align-items: center;
  gap: 14px;
}
.proj-label {
  font-size: 13px;
  font-weight: 700;
  color: #334155;
  white-space: nowrap;
}
.select-wrap {
  position: relative;
}
.select-wrap select {
  appearance: none;
  padding: 6px 28px 6px 10px;
  font-size: 13px;
  font-weight: 500;
  color: #1e293b;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  cursor: pointer;
  outline: none;
  min-width: 160px;
  transition: border-color 0.15s;
}
.select-wrap select:focus {
  border-color: #3b82f6;
  background: #fff;
}
.select-arrow {
  position: absolute;
  right: 9px;
  top: 50%;
  transform: translateY(-50%);
  color: #64748b;
  font-size: 10px;
  pointer-events: none;
}
.period-wrap {
  display: flex;
  align-items: center;
  gap: 6px;
}
.period-text {
  font-size: 14px;
  color: #1e293b;
  font-weight: 600;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 14px;
  align-items: start;
}
.charts-left {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}
.chart-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 14px 16px;
}
.chart-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.chart-card-title {
  font-size: 14px;
  font-weight: 700;
  color: #0f172a;
}
.chart-unit {
  font-size: 12px;
  color: #64748b;
}
.chart-canvas-wrap {
  position: relative;
  height: 150px;
}
.chart-canvas-wrap canvas {
  width: 100% !important;
}
.chart-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 8px;
}
.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #475569;
}
.legend-sq {
  width: 8px;
  height: 8px;
  border-radius: 1px;
  flex-shrink: 0;
}
.hbar-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
  padding-top: 6px;
  border-top: 1px solid #f1f5f9;
}
.hbar-count {
  font-size: 12px;
  color: #64748b;
}
.mini-pager {
  display: flex;
  gap: 3px;
}
.mpg {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  border: 1px solid #e2e8f0;
  border-radius: 3px;
  background: #fff;
  color: #475569;
  cursor: pointer;
}
.mpg:hover {
  background: #f1f5f9;
}
.mpg.on {
  background: #1e40af;
  color: #fff;
  border-color: #1e40af;
}

.charts-right {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.donut-card {
  display: flex;
  flex-direction: column;
}
.donut-canvas-wrap {
  position: relative;
  height: 180px;
  margin: 4px 0;
}
.donut-center {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}
.donut-total {
  font-size: 20px;
  font-weight: 800;
  color: #0f172a;
}
.donut-total-lbl {
  font-size: 12px;
  color: #64748b;
}
.donut-legend {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
  margin-top: 8px;
}
.donut-leg-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #475569;
}
.stat-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}
.stat-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 12px 14px;
  text-align: center;
}
.stat-val {
  font-size: 22px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.3px;
}
.stat-lbl {
  font-size: 12px;
  color: #475569;
  margin-top: 2px;
}
</style>
