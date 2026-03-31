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
          <!-- 페이지 헤더 -->
          <div class="flex items-center justify-between mb-6">
            <h1 class="text-2xl font-bold text-gray-800 dark:text-gray-100">
              간트 차트
            </h1>
            <div class="flex items-center gap-3">
              <!-- 뷰 전환 버튼 -->
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

          <!-- 간트 차트 -->
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

// 우선순위 색상
const getPriorityColor = (priority) => {
  const map = { 상: "#dc2626", 중: "#f59e0b", 하: "#16a34a" };
  return map[priority] || "#94a3b8";
};

// 진척도에 따른 태스크 바 색상
const getTaskColor = (percentDone) => {
  if (percentDone >= 100) return "#93c5fd";
  if (percentDone >= 60) return "#60a5fa";
  if (percentDone >= 30) return "#7dd3fc";
  return "#bfdbfe";
};

const initGantt = async () => {
  if (ganttInstance) ganttInstance.destroy();

  // 실제 API 호출 위치
  // const res = await fetch("/api/tasks/gantt");
  // const tasksData = await res.json();

  // 샘플 데이터 (API 연동 전)
  const tasksData = [
    {
      id: 1,
      name: "발명정보인...",
      expanded: true,
      children: [
        {
          id: 2,
          name: "로그인 구현",
          startDate: "2026-01-13",
          endDate: "2026-01-15",
          assignee: "일개발",
          priority: "중",
          percentDone: 30,
        },
        {
          id: 3,
          name: "회원가입 구현",
          startDate: "2026-01-16",
          endDate: "2026-01-17",
          assignee: "이개발",
          priority: "상",
          percentDone: 40,
        },
      ],
    },
    {
      id: 10,
      name: "네더버 수정",
      expanded: true,
      children: [
        {
          id: 11,
          name: "기여 제제",
          startDate: "2026-02-05",
          endDate: "2026-02-15",
          assignee: "강강왕",
          priority: "중",
          percentDone: 10,
        },
        {
          id: 12,
          name: "초과진 처리...",
          startDate: "2026-02-15",
          endDate: "2026-02-25",
          assignee: "김한왕",
          priority: "중",
          percentDone: 20,
        },
        {
          id: 13,
          name: "보조거가 산정",
          startDate: "2026-02-18",
          endDate: "2026-02-27",
          assignee: "강한왕",
          priority: "강",
          percentDone: 20,
        },
        {
          id: 14,
          name: "태형 수정",
          startDate: "2026-02-24",
          endDate: "2026-02-34",
          assignee: "강강왕",
          priority: "상",
          percentDone: 20,
        },
        {
          id: 15,
          name: "복지 새형 동록",
          startDate: "2026-02-25",
          endDate: "2026-02-05",
          assignee: "이개발",
          priority: "중",
          percentDone: 10,
        },
        {
          id: 16,
          name: "재료",
          startDate: "2026-02-25",
          endDate: "2026-02-09",
          assignee: "김한왕",
          priority: "중",
          percentDone: 30,
        },
        {
          id: 17,
          name: "달성 제류",
          startDate: "2026-02-18",
          endDate: "2026-02-09",
          assignee: "아카셀",
          priority: "하",
          percentDone: 40,
        },
      ],
    },
  ];

  ganttInstance = new Gantt({
    appendTo: ganttContainer.value,
    startDate: "2026-01-28",
    endDate: "2026-03-15",
    viewPreset: activeView.value,

    // 툴바 제거
    tbar: null,

    // Today line
    features: {
      timeRanges: {
        showCurrentTimeLine: true,
      },
    },

    columns: [
      {
        type: "name",
        text: "작업명",
        width: 180,
        htmlEncode: false,
      },
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
      {
        text: "담당자",
        field: "assignee",
        width: 90,
      },
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
      {
        text: "진척도",
        type: "percentdone",
        width: 90,
        showValue: true,
      },
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

  // + 버튼 핸들러 (전역 등록)
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

/* 뷰 전환 토글 */
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
  background: #fff;
  color: #1e293b;
  font-weight: 600;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  border: 1px solid #e2e8f0;
}

/* Bryntum 오버라이드 */
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
  border-radius: 6px !important;
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
