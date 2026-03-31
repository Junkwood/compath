<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { Gantt } from "@bryntum/gantt/gantt.module.js";
import "@bryntum/gantt/gantt.css";

const router = useRouter();
const sidebarOpen = ref(false);
const ganttContainer = ref(null);
let ganttInstance = null;

const activeView = ref("weekAndDay");
const viewOptions = [
  { label: "일별", value: "weekAndDay" },
  { label: "주별", value: "weekAndMonth" },
  { label: "월별", value: "monthAndYear" },
];

const changeView = (preset) => {
  activeView.value = preset;
  if (ganttInstance) ganttInstance.viewPreset = preset;
};

const getPriorityColor = (priority) => {
  const map = { 상: "#dc2626", 중: "#f59e0b", 하: "#16a34a" };
  return map[priority] || "#94a3b8";
};

const getTaskColor = (percentDone) => {
  if (percentDone >= 100) return "#93c5fd";
  if (percentDone >= 60) return "#60a5fa";
  if (percentDone >= 30) return "#7dd3fc";
  return "#bfdbfe";
};

const initGantt = async () => {
  if (ganttInstance) ganttInstance.destroy();

  // 두 API 동시 호출
  const [taskRes, projectRes] = await Promise.all([
    fetch("/task/gantt"),
    fetch("/projectList"),
  ]);
  const rawTasks = await taskRes.json();
  const rawProjects = await projectRes.json();

  const projectNameMap = new Map(
    rawProjects.map((p) => [String(p.projectId), p.projectName]),
  );

  // 프로젝트별
  const projectMap = new Map();
  rawTasks.forEach((task) => {
    const pid = String(task.projectId);
    if (!projectMap.has(pid)) {
      projectMap.set(pid, {
        id: `p_${pid}`,
        name: projectNameMap.get(pid) ?? `프로젝트 ${pid}`,
        expanded: true,
        children: [],
      });
    }
    projectMap.get(pid).children.push({
      id: task.taskId,
      name: task.title,
      startDate: task.estStartDate ?? task.startDate,
      endDate: task.estEndDate ?? task.dueDate,
      assignee: task.assigneeName,
      priority: task.priorityCode,
      percentDone: task.progressRate ?? 0,
    });
  });

  const tasksData = Array.from(projectMap.values());

  ganttInstance = new Gantt({
    appendTo: ganttContainer.value,
    startDate: "2026-01-01",
    endDate: "2026-12-31",
    viewPreset: activeView.value,
    tbar: null,

    features: {
      timeRanges: {
        showCurrentTimeLine: true,
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
          const color = getPriorityColor(record.priority);
          return `<span style="color:${color}; font-weight:600;">${record.priority ?? "-"}</span>`;
        },
      },
      { text: "진척도", type: "percentdone", width: 90, showValue: true },
      {
        text: "추가",
        width: 50,
        htmlEncode: false,
        renderer({ record }) {
          return `<button
            onclick="window.__ganttAdd('${record.id}')"
            style="width:22px;height:22px;border-radius:50%;border:1px solid #e2e8f0;
                   background:#f8fafc;color:#475569;font-size:15px;line-height:1;
                   cursor:pointer;display:flex;align-items:center;justify-content:center;">
            +
          </button>`;
        },
      },
    ],

    taskRenderer({ taskRecord, renderData }) {
      renderData.style = `
        background-color: ${getTaskColor(taskRecord.percentDone)};
        border-radius: 6px;
        color: #1e293b;
        font-weight: 500;
      `;
      return taskRecord.name;
    },

    project: { tasksData },
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
