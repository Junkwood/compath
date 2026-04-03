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
        <div class="px-4 sm:px-6 lg:px-8 py-6 w-full max-w-9xl mx-auto">
          <!-- 헤더 영역 -->
          <div class="gantt-header">
            <!-- 왼쪽: 제목 + 프로젝트 정보 -->
            <div class="gantt-header-left">
              <div class="gantt-title-row">
                <h1 class="gantt-title">간트 차트</h1>
              </div>
              <ProjectInfo
                :projectName="projectInfo.projectName"
                :startDate="projectInfo.startDate"
                :endDate="projectInfo.endDate"
              />
            </div>

            <!-- 오른쪽: 뷰 토글 + 목록으로 버튼 -->
            <div class="gantt-header-right">
              <div class="view-toggle">
                <button
                  v-for="v in viewOptions"
                  :key="v.value"
                  :class="['toggle-btn', activeView === v.value && 'active']"
                  @click="changeView(v.value)"
                >
                  <span class="toggle-icon">{{ v.icon }}</span>
                  <span>{{ v.label }}</span>
                </button>
              </div>
              <button @click="goBack" class="btn-back">
                <span class="btn-back-arrow">←</span>
                목록으로
              </button>
            </div>
          </div>

          <!-- 간트 차트 본체 -->
          <div class="gantt-card">
            <div ref="ganttContainer" class="gantt-wrapper" />
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useRouter, useRoute } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { Gantt } from "@bryntum/gantt/gantt.module.js";
import "@bryntum/gantt/gantt.css";
import { useGanttChartStore } from "../stores/GantChart";
import { TaskModel } from "@bryntum/gantt/gantt.module.js";
import ProjectInfo from "../components/ProjectName.vue";

const router = useRouter();
const route = useRoute();
const sidebarOpen = ref(false);
const ganttContainer = ref(null);
let ganttInstance = null;
const store = useGanttChartStore();

const activeView = ref("weekAndDay");
const viewOptions = [
  { label: "일별", value: "weekAndDay", icon: "📅" },
  { label: "주별", value: "weekAndMonth", icon: "🗓️" },
  { label: "월별", value: "monthAndYear", icon: "📆" },
];

class CustomTaskModel extends TaskModel {
  static fields = [
    ...TaskModel.fields,
    { name: "priority" },
    { name: "priorityCode" },
    { name: "assignee" },
    { name: "statusCode" },
  ];
}

const projectInfo = ref({
  projectName: "",
  startDate: "",
  endDate: "",
});

const changeView = (preset) => {
  activeView.value = preset;
  if (ganttInstance) {
    ganttInstance.viewPreset = preset;
    // 뷰 변경 후에도 오늘 날짜로 스크롤
    scrollToToday();
  }
};

const getTaskColor = (percentDone) => {
  if (percentDone >= 100) return "linear-gradient(90deg, #93c5fd, #60a5fa)";
  if (percentDone >= 60) return "linear-gradient(90deg, #bae6fd, #7dd3fc)";
  if (percentDone >= 30) return "linear-gradient(90deg, #bfdbfe, #93c5fd)";
  return "#dbeafe";
};

// 오늘 날짜로 스크롤
const scrollToToday = () => {
  if (!ganttInstance) return;
  setTimeout(() => {
    try {
      ganttInstance.scrollToDate(new Date(), {
        block: "start",
        animate: true,
        edgeOffset: 100,
      });
    } catch (e) {
      // fallback: 타임라인 직접 스크롤
      const el = ganttInstance.timeAxisSubGrid?.element;
      if (el) el.scrollLeft = 0;
    }
  }, 200);
};

const initGantt = async () => {
  if (ganttInstance) ganttInstance.destroy();

  await store.fetchGanttData(route.params.projectId);

  if (store.rawProjects && store.rawProjects.length > 0) {
    const rootProject =
      store.rawProjects.find((p) => !p.parentProjectId) || store.rawProjects[0];
    if (rootProject) {
      projectInfo.value = {
        projectName: rootProject.projectName,
        startDate: rootProject.startDate?.split("T")[0] || "2026-04-01",
        endDate: rootProject.endDate?.split("T")[0] || "2026-11-21",
      };
    }
  }

  const tasksData = store.tasksData;

  const allDates = store.rawTasks
    .flatMap((t) => [t.estStartDate ?? t.startDate, t.estEndDate ?? t.dueDate])
    .filter(Boolean)
    .map((d) => new Date(d));

  const minDate = new Date(Math.min(...allDates));
  const maxDate = new Date(Math.max(...allDates));
  minDate.setMonth(minDate.getMonth() - 1);
  maxDate.setMonth(maxDate.getMonth() + 1);

  ganttInstance = new Gantt({
    appendTo: ganttContainer.value,
    startDate: minDate,
    endDate: maxDate,
    viewPreset: activeView.value,
    tbar: null,
    readOnly: true,

    features: {
      timeRanges: { showCurrentTimeLine: true },
      labels: {
        left: { field: "name", editor: false },
      },
    },

    columns: [
      { type: "name", text: "작업명", width: 180, htmlEncode: false },
      {
        text: "시작",
        field: "startDate",
        type: "date",
        format: "YYYY-MM-DD",
        width: 100,
      },
      {
        text: "종료",
        field: "endDate",
        type: "date",
        format: "YYYY-MM-DD",
        width: 100,
      },
      { text: "담당자", field: "assignee", width: 90 },
      {
        text: "우선순위",
        field: "priority",
        width: 80,
        htmlEncode: false,
        renderer({ record }) {
          const map = {
            H1: "background:#fee2e2;color:#b91c1c",
            H2: "background:#fef3c7;color:#b45309",
            H3: "background:#dcfce7;color:#166534",
            H4: "background:#f1f5f9;color:#475569",
          };
          const s = map[record.priorityCode];
          if (!s) return "-";
          return `<span style="font-size:10px;font-weight:700;padding:2px 7px;border-radius:4px;letter-spacing:0.3px;${s}">${record.priority}</span>`;
        },
      },
      {
        text: "진척도",
        field: "percentDone",
        width: 100,
        htmlEncode: false,
        renderer({ record }) {
          const p = Math.round(record.percentDone ?? 0);
          let style, icon;
          if (p >= 100) {
            style = "background:#dbeafe;color:#1d4ed8";
            icon = "✓";
          } else if (p >= 60) {
            style = "background:#dcfce7;color:#15803d";
            icon = "▶";
          } else if (p >= 30) {
            style = "background:#fef9c3;color:#a16207";
            icon = "◐";
          } else {
            style = "background:#f1f5f9;color:#475569";
            icon = "○";
          }
          return `<span style="display:inline-flex;align-items:center;gap:4px;font-size:11px;font-weight:600;padding:3px 9px;border-radius:20px;${style}">${icon} ${p}%</span>`;
        },
      },
      {
        text: "추가",
        width: 50,
        htmlEncode: false,
        renderer({ record }) {
          const id = String(record.id);
          if (id.startsWith("p_")) {
            return `<button
              onclick="window.__ganttAdd('${id.replace("p_", "")}')"
              title="업무 추가"
              style="width:22px;height:22px;border-radius:50%;border:1px solid #bfdbfe;
                     background:#eff6ff;color:#3b82f6;font-size:15px;line-height:1;
                     cursor:pointer;display:flex;align-items:center;justify-content:center;">+</button>`;
          }
          if (!id.startsWith("root_") && record.statusCode === "REJECTED") {
            return `<button
              onclick="window.__ganttAdd('${record.id}')"
              title="업무 재생성"
              style="width:22px;height:22px;border-radius:50%;border:1px solid #fecaca;
                     background:#fef2f2;color:#dc2626;font-size:15px;line-height:1;
                     cursor:pointer;display:flex;align-items:center;justify-content:center;">+</button>`;
          }
          return "";
        },
      },
    ],

    taskRenderer({ taskRecord, renderData }) {
      if (taskRecord.isParent) {
        renderData.style = `background: #475569; border-radius: 8px;`;
      } else {
        renderData.style = `background: ${getTaskColor(taskRecord.percentDone)}; border-radius: 8px;`;
      }
      return "";
    },

    project: {
      tasksData,
      taskModelClass: CustomTaskModel,
      autoCalculatePercentDoneForParentTasks: true,
    },
  });

  // 렌더링 완료 후 오늘 날짜로 스크롤
  ganttInstance.on("render", () => {
    scrollToToday();
  });

  window.__ganttAdd = (parentId) => {
    router.push({ path: "/tasks/create", query: { parentId } });
  };
};

const goBack = () => router.back();

onMounted(() => initGantt());
onBeforeUnmount(() => {
  delete window.__ganttAdd;
  if (ganttInstance) ganttInstance.destroy();
});
</script>

<style scoped>
/* ───────────────────────────────
   헤더 레이아웃
─────────────────────────────── */
.gantt-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.gantt-header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.gantt-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.gantt-icon {
  font-size: 20px;
  line-height: 1;
}

.gantt-title {
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.dark .gantt-title {
  color: #f1f5f9;
}

.gantt-header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* ───────────────────────────────
   뷰 토글 버튼
─────────────────────────────── */
.view-toggle {
  display: flex;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 4px;
  gap: 2px;
}

.toggle-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  height: 32px;
  padding: 0 14px;
  font-size: 13px;
  font-weight: 500;
  border-radius: 9px;
  border: none;
  background: transparent;
  color: #64748b;
  cursor: pointer;
  transition: all 0.18s ease;
  white-space: nowrap;
}

.toggle-btn .toggle-icon {
  font-size: 13px;
}

.toggle-btn:hover {
  background: #e2e8f0;
  color: #1e293b;
}

.toggle-btn.active {
  background: #fff;
  color: #2563eb;
  font-weight: 700;
  box-shadow:
    0 2px 8px rgba(37, 99, 235, 0.15),
    0 1px 3px rgba(0, 0, 0, 0.08);
}

/* ───────────────────────────────
   목록으로 버튼
─────────────────────────────── */
.btn-back {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 36px;
  padding: 0 16px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 10px;
  border: 1.5px solid #cbd5e1;
  background: #fff;
  color: #475569;
  cursor: pointer;
  transition: all 0.18s ease;
  white-space: nowrap;
}

.btn-back:hover {
  background: #f8fafc;
  border-color: #94a3b8;
  color: #1e293b;
  transform: translateX(-2px);
}

.btn-back-arrow {
  font-size: 15px;
  line-height: 1;
  transition: transform 0.18s ease;
}

.btn-back:hover .btn-back-arrow {
  transform: translateX(-3px);
}

/* ───────────────────────────────
   간트 카드
─────────────────────────────── */
.gantt-card {
  background: #fff;
  border-radius: 14px;
  box-shadow:
    0 1px 4px rgba(0, 0, 0, 0.06),
    0 4px 16px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  border: 1px solid #e8edf2;
}

.dark .gantt-card {
  background: #1e293b;
  border-color: #334155;
}

/* ───────────────────────────────
   간트 래퍼
─────────────────────────────── */
.gantt-wrapper {
  height: calc(100vh - 200px);
  min-height: 400px;
}

/* ───────────────────────────────
   Bryntum 간트 커스텀
─────────────────────────────── */
:deep(.b-toolbar) {
  display: none !important;
}
:deep(.b-gantt) {
  font-family: inherit;
  font-size: 13px !important;
}
:deep(.b-grid-header) {
  background: #f8fafc !important;
  color: #475569 !important;
  font-size: 12px !important;
  font-weight: 600 !important;
  border-bottom: 1px solid #e2e8f0 !important;
}
:deep(.b-grid-cell) {
  font-size: 12px !important;
}
:deep(.b-grid-row:hover .b-grid-cell) {
  background: #f1f5f9 !important;
}
:deep(.b-gantt-task) {
  border-radius: 8px !important;
  font-size: 11px !important;
}
:deep(.b-tree-cell-value) {
  font-size: 12px !important;
}
:deep(.b-sch-header-timeaxis-cell) {
  font-size: 12px !important;
}
:deep(.b-sch-current-time) {
  border-left: 2px dashed #f97316 !important;
}
:deep(.b-sch-current-time-indicator) {
  background: #f97316 !important;
}
</style>
