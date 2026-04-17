import { defineStore } from "pinia";
import { ref, computed } from "vue";
import api from "../utils/api";

export const useActivityLogsStore = defineStore("activityLogs", () => {
  const logs = ref([]);

  const searchKeyword = ref("");
  const selectedActivityType = ref("");
  const selectedTargetType = ref("");
  const sortOrder = ref("DESC");

  const selectedQuickRange = ref("ALL");
  const startDate = ref("");
  const endDate = ref("");

  const quickRangeOptions = [
    { label: "전체", value: "ALL" },
    { label: "오늘", value: "TODAY" },
    { label: "최근 7일", value: "7D" },
    { label: "최근 30일", value: "30D" },
    { label: "직접선택", value: "CUSTOM" },
  ];

  const ACTION_TYPE_MAP = {
    J0: "생성",
    J1: "수정",
    J2: "삭제",
    J3: "상태변경",

    M0: "생성",
    M1: "수정",
    M2: "삭제",
    M3: "상태변경",

    T0: "생성",
    T1: "수정",
    T2: "삭제",
    T3: "상태변경",
    T4: "진척도변경",
    T5: "소요시간변경",
    T6: "우선순위변경",
  };

  const TARGET_TYPE_MAP = {
    projects: "프로젝트",
    tasks: "업무",
    milestones: "마일스톤",
    members: "구성원",
    memos: "메모",
    time_entries: "소요시간",
  };

  const TASK_STATUS_LABEL_MAP = {
    1: "시작 전",
    2: "진행중",
    3: "개발완료",
    4: "반려",
    5: "종료",
  };

  const PRIORITY_LABEL_MAP = {
    H1: "긴급",
    H2: "상",
    H3: "중",
    H4: "하",
  };

  function startOfDay(date) {
    const d = new Date(date);
    d.setHours(0, 0, 0, 0);
    return d;
  }

  function endOfDay(date) {
    const d = new Date(date);
    d.setHours(23, 59, 59, 999);
    return d;
  }

  function applyQuickRange(type) {
    selectedQuickRange.value = type;
    if (type !== "CUSTOM") {
      startDate.value = "";
      endDate.value = "";
    }
  }

  function getActivityTypeLabel(type) {
    return ACTION_TYPE_MAP[type] || type || "-";
  }

  function getTargetTypeLabel(type) {
    return TARGET_TYPE_MAP[type] || type || "-";
  }

  function badgeClass(type) {
    if (["J0", "M0", "T0"].includes(type)) return "create";
    if (["J2", "M2", "T2"].includes(type)) return "delete";
    if (["J1", "J3", "M1", "M3", "T1", "T3", "T4", "T5", "T6"].includes(type)) {
      return "update";
    }
    return "";
  }

  function formatDateTitle(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}.${month}.${day}`;
  }

  function formatTime(dateTimeString) {
    const timePart = dateTimeString.split(" ")[1];
    return timePart ? timePart.slice(0, 5) : "";
  }

  function getTaskStatusLabel(value) {
    if (value === null || value === undefined || value === "") return "-";
    return TASK_STATUS_LABEL_MAP[Number(value)] || value;
  }

  function isPriorityCode(value) {
    if (value === null || value === undefined || value === "") return false;
    return Object.prototype.hasOwnProperty.call(
      PRIORITY_LABEL_MAP,
      String(value).trim(),
    );
  }

  function getPriorityLabel(value) {
    if (value === null || value === undefined || value === "") return "-";
    const code = String(value).trim();
    return PRIORITY_LABEL_MAP[code] || code;
  }

  function buildMessage(item) {
    const targetLabel = getTargetTypeLabel(item.targetType);
    const beforeValue = item.beforeValue?.trim() || "";
    const afterValue = item.afterValue?.trim() || "";

    if (item.targetType === "projects") {
      switch (item.actionType) {
        case "J0":
          return afterValue
            ? `프로젝트 "${afterValue}"가 생성되었습니다.`
            : "프로젝트가 생성되었습니다.";
        case "J1":
          return "프로젝트 정보가 수정되었습니다.";
        case "J2":
          return beforeValue
            ? `프로젝트 "${beforeValue}"가 삭제되었습니다.`
            : "프로젝트가 삭제되었습니다.";
        case "J3":
          return "프로젝트 상태가 변경되었습니다.";
        default:
          return "프로젝트 작업내역이 기록되었습니다.";
      }
    }

    if (item.targetType === "milestones") {
      switch (item.actionType) {
        case "M0":
          return afterValue
            ? `마일스톤 "${afterValue}"이 생성되었습니다.`
            : "마일스톤이 생성되었습니다.";
        case "M1":
          return "마일스톤 정보가 수정되었습니다.";
        case "M2":
          return beforeValue
            ? `마일스톤 "${beforeValue}"이 삭제되었습니다.`
            : "마일스톤이 삭제되었습니다.";
        case "M3":
          return "마일스톤 상태가 변경되었습니다.";
        default:
          return "마일스톤 작업내역이 기록되었습니다.";
      }
    }

    if (item.targetType === "tasks") {
      switch (item.actionType) {
        case "T0":
          return afterValue
            ? `업무 "${afterValue}"가 생성되었습니다.`
            : "업무가 생성되었습니다.";
        case "T1":
          if (isPriorityCode(beforeValue) || isPriorityCode(afterValue)) {
            return "업무 우선순위가 변경되었습니다.";
          }
          return "업무 정보가 수정되었습니다.";
        case "T2":
          return beforeValue
            ? `업무 "${beforeValue}"가 삭제되었습니다.`
            : "업무가 삭제되었습니다.";
        case "T3":
          if (Number(afterValue) === 4) return "업무가 반려되었습니다.";
          return "업무 상태가 변경되었습니다.";
        case "T4":
          return "업무 진척도가 변경되었습니다.";
        case "T5":
          return "업무 소요시간이 변경되었습니다.";
        case "T6":
          return "업무 우선순위가 변경되었습니다.";
        default:
          return "업무 작업내역이 기록되었습니다.";
      }
    }

    if (item.targetType === "time_entries") {
      return "소요시간이 등록되었습니다.";
    }

    switch (item.actionType) {
      case "J0":
      case "M0":
      case "T0":
        return `${targetLabel}가 생성되었습니다.`;
      case "J1":
      case "M1":
      case "T1":
        return `${targetLabel}가 수정되었습니다.`;
      case "J2":
      case "M2":
      case "T2":
        return `${targetLabel}가 삭제되었습니다.`;
      case "J3":
      case "M3":
      case "T3":
        return `${targetLabel}의 상태가 변경되었습니다.`;
      default:
        return "작업내역이 기록되었습니다.";
    }
  }

  function buildDetail(item) {
    const beforeValue = item.beforeValue?.trim() || "";
    const afterValue = item.afterValue?.trim() || "";

    if (item.targetType === "projects") {
      switch (item.actionType) {
        case "J0":
          return afterValue ? `생성값: ${afterValue}` : "";
        case "J1":
          if (!beforeValue && !afterValue) return "";
          return `기존값: ${beforeValue || "-"} → 변경값: ${afterValue || "-"}`;
        case "J2":
          return beforeValue ? `삭제 전 값: ${beforeValue}` : "";
        case "J3":
          if (!beforeValue && !afterValue) return "";
          return `상태 변경: ${beforeValue || "-"} → ${afterValue || "-"}`;
        default:
          return "";
      }
    }

    if (item.targetType === "milestones") {
      switch (item.actionType) {
        case "M0":
          return afterValue ? `생성값: ${afterValue}` : "";
        case "M1":
          if (!beforeValue && !afterValue) return "";
          return `기존값: ${beforeValue || "-"} → 변경값: ${afterValue || "-"}`;
        case "M2":
          return beforeValue ? `삭제 전 값: ${beforeValue}` : "";
        case "M3":
          if (!beforeValue && !afterValue) return "";
          return `상태 변경: ${beforeValue || "-"} → ${afterValue || "-"}`;
        default:
          return "";
      }
    }

    if (item.targetType === "tasks") {
      switch (item.actionType) {
        case "T0":
          return afterValue ? `생성값: ${afterValue}` : "";
        case "T1":
          if (!beforeValue && !afterValue) return "";
          if (isPriorityCode(beforeValue) || isPriorityCode(afterValue)) {
            return `우선순위 변경: ${getPriorityLabel(beforeValue)} → ${getPriorityLabel(afterValue)}`;
          }
          return `기존값: ${beforeValue || "-"} → 변경값: ${afterValue || "-"}`;
        case "T2":
          return beforeValue ? `삭제 전 값: ${beforeValue}` : "";
        case "T3":
          if (!beforeValue && !afterValue) return "";
          return `상태 변경: ${getTaskStatusLabel(beforeValue)} → ${getTaskStatusLabel(afterValue)}`;
        case "T4":
          if (!beforeValue && !afterValue) return "";
          return `진척도 변경: ${beforeValue || "0"}% → ${afterValue || "0"}%`;
        case "T5":
          if (!beforeValue && !afterValue) return "";
          return `소요시간 변경: ${beforeValue || "0"}시간 → ${afterValue || "0"}시간`;
        case "T6":
          if (!beforeValue && !afterValue) return "";
          return `우선순위 변경: ${getPriorityLabel(beforeValue)} → ${getPriorityLabel(afterValue)}`;
        default:
          return "";
      }
    }

    if (item.targetType === "time_entries") {
      if (!beforeValue && !afterValue) return "";
      return `기존값: ${beforeValue || "-"} → 변경값: ${afterValue || "-"}`;
    }

    return "";
  }

  function mapLogItem(item) {
    return {
      id: item.activityLogId,
      createdAt: item.createdAt,
      userName: item.userName || "알 수 없음",
      activityType: item.actionType,
      actionType: item.actionType,
      targetType: item.targetType,
      targetId: item.targetId || "",
      beforeValue: item.beforeValue || "",
      afterValue: item.afterValue || "",
      targetName: item.targetName || item.targetId || "",
      message: buildMessage(item),
      detail: buildDetail(item),
      projectPath: `프로젝트 ID: ${item.projectId}`,
    };
  }

  async function fetchActivityLogs(projectId) {
    try {
      if (!projectId) {
        logs.value = [];
        return;
      }

      const res = await api.get(`/activityLogs/${projectId}`);
      logs.value = Array.isArray(res.data) ? res.data.map(mapLogItem) : [];
    } catch (err) {
      console.error("작업내역 조회 실패:", err);
      logs.value = [];
    }
  }

  const filteredLogs = computed(() => {
    let result = [...logs.value];

    if (selectedActivityType.value) {
      result = result.filter(
        (item) => item.activityType === selectedActivityType.value,
      );
    }

    if (selectedTargetType.value) {
      result = result.filter(
        (item) => item.targetType === selectedTargetType.value,
      );
    }

    if (searchKeyword.value.trim()) {
      const keyword = searchKeyword.value.trim().toLowerCase();
      result = result.filter((item) => {
        return (
          (item.userName || "").toLowerCase().includes(keyword) ||
          (item.targetName || "").toLowerCase().includes(keyword) ||
          (item.message || "").toLowerCase().includes(keyword) ||
          (item.detail || "").toLowerCase().includes(keyword) ||
          (item.projectPath || "").toLowerCase().includes(keyword)
        );
      });
    }

    if (
      selectedQuickRange.value === "CUSTOM" &&
      startDate.value &&
      endDate.value
    ) {
      result = result.filter((item) => {
        const itemDate = item.createdAt.split(" ")[0];
        return itemDate >= startDate.value && itemDate <= endDate.value;
      });
    }

    if (selectedQuickRange.value === "TODAY") {
      const today = new Date().toISOString().slice(0, 10);
      result = result.filter((item) => item.createdAt.startsWith(today));
    }

    if (selectedQuickRange.value === "7D") {
      const baseDate = new Date();
      const fromDate = new Date(baseDate);
      fromDate.setDate(baseDate.getDate() - 6);

      result = result.filter((item) => {
        const itemDate = new Date(item.createdAt.replace(" ", "T"));
        return (
          itemDate >= startOfDay(fromDate) && itemDate <= endOfDay(baseDate)
        );
      });
    }

    if (selectedQuickRange.value === "30D") {
      const baseDate = new Date();
      const fromDate = new Date(baseDate);
      fromDate.setDate(baseDate.getDate() - 29);

      result = result.filter((item) => {
        const itemDate = new Date(item.createdAt.replace(" ", "T"));
        return (
          itemDate >= startOfDay(fromDate) && itemDate <= endOfDay(baseDate)
        );
      });
    }

    result.sort((a, b) => {
      const aTime = new Date(a.createdAt.replace(" ", "T")).getTime();
      const bTime = new Date(b.createdAt.replace(" ", "T")).getTime();
      return sortOrder.value === "DESC" ? bTime - aTime : aTime - bTime;
    });

    return result;
  });

  const groupedLogs = computed(() => {
    const groupMap = {};

    filteredLogs.value.forEach((log) => {
      const date = log.createdAt.split(" ")[0];
      if (!groupMap[date]) groupMap[date] = [];
      groupMap[date].push(log);
    });

    return Object.keys(groupMap).map((date) => ({
      date,
      items: groupMap[date],
    }));
  });

  return {
    logs,

    searchKeyword,
    selectedActivityType,
    selectedTargetType,
    sortOrder,
    selectedQuickRange,
    startDate,
    endDate,
    quickRangeOptions,

    filteredLogs,
    groupedLogs,

    fetchActivityLogs,
    applyQuickRange,
    getActivityTypeLabel,
    getTargetTypeLabel,
    badgeClass,
    formatDateTitle,
    formatTime,
  };
});
