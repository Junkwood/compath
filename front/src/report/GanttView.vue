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
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
          <div class="flex items-center justify-between mb-6">
            <h1 class="text-2xl font-bold text-gray-800 dark:text-gray-100">
              간트 차트
            </h1>
            <div class="flex items-center gap-3">
              <div class="view-toggle">
                <button
                  v-for="v in viewOptions"
                  :key="v.value"
                  :class="['toggle-btn', activeView === v.value && 'active']"
                  @click="changeView(v.value)"
                >
                  {{ v.label }}
                </button>
              </div>
              <button @click="goBack" class="btn-navy">← 목록으로</button>
            </div>
          </div>

          <div
            class="bg-white dark:bg-gray-800 rounded-xl shadow overflow-hidden"
          >
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

const router = useRouter();
const route = useRoute();
const sidebarOpen = ref(false);
const ganttContainer = ref(null);
let ganttInstance = null;
const store = useGanttChartStore();

const activeView = ref("weekAndDay");
const viewOptions = [
  { label: "일별", value: "weekAndDay" },
  { label: "주별", value: "weekAndMonth" },
  { label: "월별", value: "monthAndYear" },
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

const changeView = (preset) => {
  activeView.value = preset;
  if (ganttInstance) ganttInstance.viewPreset = preset;
};

const getTaskColor = (percentDone) => {
  if (percentDone >= 100) return "linear-gradient(90deg, #93c5fd, #60a5fa)";
  if (percentDone >= 60) return "linear-gradient(90deg, #bae6fd, #7dd3fc)";
  if (percentDone >= 30) return "linear-gradient(90deg, #bfdbfe, #93c5fd)";
  return "#dbeafe";
};

const initGantt = async () => {
  if (ganttInstance) ganttInstance.destroy();

  await store.fetchGanttData(route.params.projectId);
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

          // 하위프로젝트
          if (id.startsWith("p_")) {
            return `<button
              onclick="window.__ganttAdd('${id.replace("p_", "")}')"
              title="업무 추가"
              style="width:22px;height:22px;border-radius:50%;border:1px solid #bfdbfe;
                     background:#eff6ff;color:#3b82f6;font-size:15px;line-height:1;
                     cursor:pointer;display:flex;align-items:center;justify-content:center;">
              +
            </button>`;
          }

          // 반려된 업무
          if (!id.startsWith("root_") && record.statusCode === "REJECTED") {
            return `<button
              onclick="window.__ganttAdd('${record.id}')"
              title="업무 재생성"
              style="width:22px;height:22px;border-radius:50%;border:1px solid #fecaca;
                     background:#fef2f2;color:#dc2626;font-size:15px;line-height:1;
                     cursor:pointer;display:flex;align-items:center;justify-content:center;">
              +
            </button>`;
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
.gantt-wrapper {
  height: calc(100vh - 180px);
  min-height: 400px;
}
.view-toggle {
  display: flex;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  padding: 3px;
  gap: 2px;
}
.toggle-btn {
  height: 30px;
  padding: 0 14px;
  font-size: 13px;
  font-weight: 500;
  border-radius: 8px;
  border: none;
  background: transparent;
  color: #475569;
  cursor: pointer;
  transition: all 0.2s;
}
.toggle-btn:hover {
  background: #f1f5f9;
  color: #1e293b;
}
.toggle-btn.active {
  background: #3b82f6;
  color: #fff;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
  border: none;
}
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
  border-left: 2px solid #f97316 !important;
}
</style>
