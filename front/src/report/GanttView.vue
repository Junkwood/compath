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
        <div class="gantt-page">
          <!-- 서브헤더 -->
          <div class="sub-header">
            <div class="sub-header-left">
              <div class="breadcrumb">
                <span>홈</span><span class="bc-sep">›</span>
                <span>{{ projectInfo.projectName }}</span
                ><span class="bc-sep">›</span>
                <span class="bc-cur">간트 차트</span>
              </div>
              <div class="project-meta">
                <span class="meta-name">{{ projectInfo.projectName }}</span>
                <span class="meta-sep">·</span>
                <span class="meta-date"
                  >{{ projectInfo.startDate }} – {{ projectInfo.endDate }}</span
                >
              </div>
            </div>

            <div class="sub-header-right">
              <!-- 뷰 토글 -->
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
              <!-- 목록으로 -->
              <button @click="goBack" class="btn-back">← 목록으로</button>
            </div>
          </div>

          <!-- 간트 카드 -->
          <div class="gantt-card">
            <div ref="ganttContainer" class="gantt-wrapper" />
          </div>

          <!-- 업무 생성 모달 -->
          <TaskCreateModal
            v-model="taskModalOpen"
            :parentId="taskModalParentId"
            :projectId="rootProjectId"
            @submitted="onTaskSubmitted"
          />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { useRouter, useRoute } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { Gantt } from "@bryntum/gantt/gantt.module.js";
import "@bryntum/gantt/gantt.css";
import { useGanttChartStore } from "../stores/GantChart";
import { TaskModel } from "@bryntum/gantt/gantt.module.js";
import TaskCreateModal from "../task/TaskCreatemodal.vue";

const router = useRouter();
const route = useRoute();
const sidebarOpen = ref(false);
const ganttContainer = ref(null);
let ganttInstance = null;
const store = useGanttChartStore();

const taskModalOpen = ref(false);
const taskModalParentId = ref(null);
const rootProjectId = computed(() => {
  if (!store.rawProjects?.length) return route.params.projectId;
  const root = store.rawProjects.find((p) => !p.parentProjectId);
  return root?.projectId ?? route.params.projectId;
});

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
    { name: "projectId" },
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
    scrollToToday();
  }
};

const getTaskColor = (percentDone) => {
  if (percentDone >= 100) return "#2563eb";
  if (percentDone >= 60) return "#3b82f6";
  if (percentDone >= 30) return "#60a5fa";
  return "#93c5fd";
};

const scrollToToday = () => {
  if (!ganttInstance) return;
  setTimeout(() => {
    try {
      ganttInstance.scrollToDate(new Date(), {
        block: "center",
        animate: false,
        edgeOffset: 150,
      });
    } catch (e) {
      const el = ganttInstance.timeAxisSubGrid?.element;
      if (el) el.scrollLeft = 0;
    }
  }, 300);
};

const onTaskSubmitted = async () => {
  taskModalOpen.value = false;
  await store.fetchGanttData(route.params.projectId);
  if (ganttInstance) {
    ganttInstance.project.tasksData = store.tasksData;
    await ganttInstance.project.loadInlineData({ tasks: store.tasksData });
  }
};

const initGantt = async () => {
  if (ganttInstance) ganttInstance.destroy();

  await store.fetchGanttData(route.params.projectId);

  if (store.rawProjects?.length) {
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
    .filter((d) => d && !isNaN(new Date(d).getTime()))
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
    rowHeight: 36,

    features: {
      timeRanges: { showCurrentTimeLine: true },
      labels: {
        left: { field: "name", editor: false },
      },
    },

    columns: [
      { type: "name", text: "작업명", width: 200, htmlEncode: false },
      {
        text: "시작",
        field: "startDate",
        type: "date",
        format: "MM-DD",
        width: 80,
      },
      {
        text: "종료",
        field: "endDate",
        type: "date",
        format: "MM-DD",
        width: 80,
      },
      { text: "담당자", field: "assignee", width: 80 },
      {
        text: "우선순위",
        field: "priority",
        width: 80,
        htmlEncode: false,
        renderer({ record }) {
          const map = {
            H1: "background:#fee2e2;color:#b91c1c",
            H2: "background:#fef3c7;color:#92400e",
            H3: "background:#f0fdf4;color:#166534",
            H4: "background:#f8fafc;color:#64748b",
          };
          const s = map[record.priorityCode];
          if (!s) return `<span style="color:#cbd5e1;">—</span>`;
          return `<span style="font-size:10px;font-weight:600;padding:2px 7px;border-radius:4px;letter-spacing:0.2px;${s}">${record.priority}</span>`;
        },
      },
      {
        text: "진척도",
        field: "percentDone",
        width: 90,
        htmlEncode: false,
        renderer({ record }) {
          const p = Math.round(record.percentDone ?? 0);
          const fill =
            p >= 100
              ? "#2563eb"
              : p >= 60
                ? "#3b82f6"
                : p >= 30
                  ? "#60a5fa"
                  : "#cbd5e1";
          return `
            <div style="display:flex;align-items:center;gap:7px;">
              <div style="flex:1;height:5px;background:#e2e8f0;border-radius:99px;overflow:hidden;">
                <div style="width:${p}%;height:100%;background:${fill};border-radius:99px;"></div>
              </div>
              <span style="font-size:11px;color:#94a3b8;width:26px;text-align:right;">${p}%</span>
            </div>`;
        },
      },
      {
        text: "",
        width: 44,
        htmlEncode: false,
        renderer({ record }) {
          const id = String(record.id);
          if (id.startsWith("p_")) {
            return `<button onclick="window.__ganttAdd('${id.replace("p_", "")}')" title="업무 추가"
              style="width:24px;height:24px;border-radius:6px;border:1px solid #bfdbfe;
                     background:#eff6ff;color:#3b82f6;font-size:14px;line-height:1;
                     cursor:pointer;display:inline-flex;align-items:center;justify-content:center;">+</button>`;
          }
          if (!id.startsWith("root_") && record.statusCode === "REJECTED") {
            return `<button onclick="window.__ganttAdd('${record.id}')" title="업무 재생성"
              style="width:24px;height:24px;border-radius:6px;border:1px solid #fecaca;
                     background:#fef2f2;color:#dc2626;font-size:14px;line-height:1;
                     cursor:pointer;display:inline-flex;align-items:center;justify-content:center;">↺</button>`;
          }
          return "";
        },
      },
    ],

    taskRenderer({ taskRecord, renderData }) {
      const p = taskRecord.percentDone ?? 0;
      if (taskRecord.isParent) {
        renderData.style = `background:#475569;border-radius:4px;`;
      } else {
        renderData.style = `background:${getTaskColor(p)};border-radius:4px;`;
      }
      return "";
    },
    listeners: {
      cellClick({ record }) {
        const id = String(record.id);

        if (id.startsWith("root_") || id.startsWith("p_")) return;

        const taskProjectId = record.projectId;
        const rootId = route.params.projectId;

        const isSub = String(taskProjectId) !== String(rootId);

        const path = isSub
          ? `/project/${rootId}/${taskProjectId}/tasklist/${record.id}`
          : `/project/${rootId}/tasklist/${record.id}`;

        router.push(path);
      },
    },
    project: {
      tasksData,
      taskModelClass: CustomTaskModel,
      autoCalculatePercentDoneForParentTasks: true,
    },
  });

  ganttInstance.project.commitAsync().then(() => {
    scrollToToday();
  });
  window.__ganttAdd = (parentId) => {
    taskModalParentId.value = String(parentId);
    taskModalOpen.value = true;
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
/* ─────────────────────────────────
   페이지 래퍼
───────────────────────────────── */
.gantt-page {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #f1f5f9;
}

/* ─────────────────────────────────
   서브헤더
───────────────────────────────── */
.sub-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 28px;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 30;
  flex-shrink: 0;
}

.sub-header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #94a3b8;
}

.bc-sep {
  color: #cbd5e1;
}
.bc-cur {
  color: #0f172a;
  font-weight: 600;
}

.project-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}

.meta-name {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
}

.meta-sep {
  color: #cbd5e1;
  font-size: 12px;
}

.meta-date {
  font-size: 12px;
  color: #94a3b8;
}

.sub-header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* ─────────────────────────────────
   뷰 토글
───────────────────────────────── */
.view-toggle {
  display: flex;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 3px;
  gap: 2px;
}

.toggle-btn {
  height: 30px;
  padding: 0 14px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 6px;
  border: none;
  background: transparent;
  color: #64748b;
  cursor: pointer;
  transition: all 0.15s;
  white-space: nowrap;
}

.toggle-btn:hover {
  background: #e2e8f0;
  color: #1e293b;
}

.toggle-btn.active {
  background: #ffffff;
  color: #2563eb;
  font-weight: 700;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

/* ─────────────────────────────────
   목록으로 버튼
───────────────────────────────── */
.btn-back {
  height: 32px;
  padding: 0 14px;
  font-size: 12px;
  font-weight: 600;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  color: #475569;
  cursor: pointer;
  transition: all 0.15s;
  white-space: nowrap;
}

.btn-back:hover {
  background: #f8fafc;
  border-color: #94a3b8;
  color: #1e293b;
}

/* ─────────────────────────────────
   간트 카드
───────────────────────────────── */
.gantt-card {
  flex: 1;
  margin: 16px 20px 16px;
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  overflow: hidden;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.05);
}

.gantt-wrapper {
  height: calc(100vh - 160px);
  min-height: 400px;
}

/* ─────────────────────────────────
   Bryntum 커스텀
───────────────────────────────── */
:deep(.b-toolbar) {
  display: none !important;
}

:deep(.b-gantt) {
  font-family: inherit !important;
  font-size: 13px !important;
  border: none !important;
}

/* 헤더 */
:deep(.b-grid-header-container) {
  background: #f8fafc !important;
  border-bottom: 1px solid #e2e8f0 !important;
}

:deep(.b-grid-header) {
  background: #f8fafc !important;
  color: #94a3b8 !important;
  font-size: 11px !important;
  font-weight: 600 !important;
  border-right: 1px solid #f1f5f9 !important;
}

/* 행 */
:deep(.b-grid-row) {
  border-bottom: 1px solid #f1f5f9 !important;
}

:deep(.b-grid-row:hover .b-grid-cell) {
  background: #f8fafc !important;
}

:deep(.b-grid-row.b-selected .b-grid-cell) {
  background: #eff6ff !important;
}

/* 셀 */
:deep(.b-grid-cell) {
  font-size: 12.5px !important;
  color: #374151 !important;
  border-right: 1px solid #f1f5f9 !important;
  padding: 0 10px !important;
}

/* 트리 아이템 */
:deep(.b-tree-cell-value) {
  font-size: 12.5px !important;
  font-weight: 500 !important;
}

/* 타임라인 헤더 */
:deep(.b-sch-header-timeaxis-cell) {
  font-size: 11px !important;
  font-weight: 500 !important;
  color: #94a3b8 !important;
  background: #f8fafc !important;
  border-bottom: 1px solid #e2e8f0 !important;
}

/* 타임라인 배경 줄무늬 */
:deep(.b-sch-column-line) {
  border-left: 1px solid #f1f5f9 !important;
}

/* 오늘 라인 */
:deep(.b-sch-current-time) {
  border-left: 2px solid #f97316 !important;
  opacity: 0.8;
}

:deep(.b-sch-current-time-indicator) {
  background: #f97316 !important;
  border-radius: 2px !important;
}

/* 태스크 바 */
:deep(.b-gantt-task) {
  border-radius: 6px !important;
  font-size: 11px !important;
  border: none !important;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12) !important;
}

:deep(.b-gantt-task-wrap) {
  padding: 3px 0 !important;
}

/* 부모 태스크 (롤업 바) */
:deep(.b-gantt-task.b-milestone) {
  background: #334155 !important;
}

/* 스크롤바 */
:deep(.b-virtual-scroller::-webkit-scrollbar) {
  height: 6px !important;
  width: 6px !important;
}
:deep(.b-virtual-scroller::-webkit-scrollbar-thumb) {
  background: #cbd5e1 !important;
  border-radius: 3px !important;
}
:deep(.b-virtual-scroller::-webkit-scrollbar-track) {
  background: transparent !important;
}

/* 분할선 (그리드 | 차트) */
:deep(.b-grid-splitter) {
  background: #e2e8f0 !important;
  width: 4px !important;
}
</style>
