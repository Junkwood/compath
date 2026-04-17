// src/stores/projectDashboard.js
import { defineStore } from "pinia";
import { computed, ref } from "vue";
import api from "../utils/api";

export const useProjectDashboardStore = defineStore("projectDashboard", () => {
  const projectInfo = ref({
    projectId: null,
    projectName: "",
    identifier: "",
    description: "",
    startDate: "",
    endDate: "",
    useMilestone: "",
  });

  const milestoneList = ref([]);
  const taskSummaryData = ref([]);
  const noticeList = ref([]);
  const projectMembers = ref([]);
  const memoList = ref([]);
  const subProjects = ref([]);

  const normalizeMilestoneValue = (value) =>
    String(value ?? "").trim().toUpperCase();

  const isMilestoneUsed = computed(
    () => normalizeMilestoneValue(projectInfo.value.useMilestone) === "O1",
  );

  const milestoneLabel = computed(() =>
    isMilestoneUsed.value ? "마일스톤 사용" : "마일스톤 미사용",
  );

  const isMilestoneEnabled = computed(
    () =>
      isMilestoneUsed.value ||
      subProjects.value.some((item) => item.milestoneId != null),
  );

  const milestoneTabs = computed(() => {
    const map = new Map();

    // 1. 먼저 마일스톤 목록 기준으로 탭을 만들고
    milestoneList.value.forEach((item) => {
      const key = item.milestoneId;

      if (!map.has(key)) {
        map.set(key, {
          milestoneId: item.milestoneId,
          milestoneName: item.milestoneName || "마일스톤명 없음",
          projects: [],
        });
      }
    });

    // 2. 그 다음 하위프로젝트를 해당 마일스톤 탭에 넣는다
    subProjects.value.forEach((item) => {
      const key = item.milestoneId;

      // 혹시 milestoneList에 없는데 subProjects에는 있는 데이터가 오면 보정
      if (!map.has(key)) {
        map.set(key, {
          milestoneId: item.milestoneId,
          milestoneName: item.milestoneName || "미분류",
          projects: [],
        });
      }

      if (item.projectId) {
        map.get(key).projects.push({
          projectId: item.projectId,
          projectName: item.projectName,
          identifier: item.identifier,
          userName: item.userName,
          startDate: item.startDate || "",
          endDate: item.endDate || "",
        });
      }
    });

    return Array.from(map.values()).sort((a, b) => {
      if (a.milestoneId == null) return 1;
      if (b.milestoneId == null) return -1;
      return Number(a.milestoneId) - Number(b.milestoneId);
    });
  });

  const normalizeRoleName = (roleName) => {
    if (!roleName || !String(roleName).trim()) return "구성원";
    const value = String(roleName).trim();
    if (value.includes("PM")) return "PM";
    if (value.includes("PL")) return "PL";
    if (value.includes("QA")) return "QA";
    if (value.includes("관리")) return "관리자";
    if (value.includes("개발")) return "개발";
    return value;
  };

  const roleOrderMap = {
    PM: 1,
    PL: 2,
    관리자: 3,
    개발: 4,
    QA: 5,
    구성원: 99,
  };

  const groupedMembers = computed(() => {
    const map = new Map();

    projectMembers.value.forEach((member) => {
      const groupName = normalizeRoleName(member.roleName);
      if (!map.has(groupName)) {
        map.set(groupName, {
          key: groupName,
          label: groupName,
          members: [],
        });
      }
      map.get(groupName).members.push(member);
    });

    return Array.from(map.values())
      .map((group) => ({
        ...group,
        members: [...group.members].sort((a, b) =>
          String(a.userName || "").localeCompare(String(b.userName || ""), "ko"),
        ),
      }))
      .sort((a, b) => {
        const orderA = roleOrderMap[a.label] ?? 50;
        const orderB = roleOrderMap[b.label] ?? 50;
        if (orderA !== orderB) return orderA - orderB;
        return a.label.localeCompare(b.label, "ko");
      });
  });

  const formatDate = (value) => {
    if (!value) return "";
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return String(value).slice(0, 10);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
  };

  const isWithin7Days = (value) => {
    if (!value) return false;
    const target = new Date(value);
    if (Number.isNaN(target.getTime())) return false;
    const diff =
      (new Date().getTime() - target.getTime()) / (1000 * 60 * 60 * 24);
    return diff >= 0 && diff <= 7;
  };

  const fetchProjectDetail = async (projectId) => {
    try {
      const res = await api.get(`/ProjectDetail/${projectId}`);
      projectInfo.value = {
        projectId: res.data?.projectId ?? null,
        projectName: res.data?.projectName ?? "",
        identifier: res.data?.identifier ?? "",
        description: res.data?.description ?? "",
        startDate: res.data?.startDate ?? "",
        endDate: res.data?.endDate ?? "",
        useMilestone: res.data?.useMilestone ?? "",
      };
    } catch (err) {
      console.error("프로젝트 상세 조회 실패:", err);
      projectInfo.value = {
        projectId: null,
        projectName: "",
        identifier: "",
        description: "",
        startDate: "",
        endDate: "",
        useMilestone: "",
      };
    }
  };

  // 하단 마일스톤 탭
  const fetchMilestoneList = async (projectId) => {
    if (!isMilestoneUsed.value) {
      milestoneList.value = [];
      return;
    }

    try {
      const res = await api.get(`/MilestoneTab/${projectId}`);
      milestoneList.value = Array.isArray(res.data) ? res.data : [];
    } catch (err) {
      console.error("마일스톤 목록 조회 실패:", err);
      milestoneList.value = [];
    }
  };

  // 우상단 내 업무현황 조회
  const fetchTaskSummary = async (projectId) => {
    try {
      const res = await api.get(`/TaskSummary/${projectId}`);
      taskSummaryData.value = res.data || [];
    } catch (err) {
      console.error("업무 현황 조회 실패:", err);
      taskSummaryData.value = [];
    }
  };

  // 공지사항 리스트
  const fetchNoticeList = async (projectId) => {
    try {
      const res = await api.get(`/notices/getNoticeLists/${Number(projectId)}`);
      const rawList = Array.isArray(res.data) ? res.data : [];

      noticeList.value = rawList
        .map((item) => ({
          noticeId: item.noticeId,
          title: item.title,
          createdAt: formatDate(item.createdAt),
          rawCreatedAt: item.createdAt,
          pinStatusCode: item.pinStatusCode ?? "B2",
          isNew: isWithin7Days(item.createdAt),
        }))
        .sort((a, b) => {
          const diff =
            (b.pinStatusCode === "B1" ? 1 : 0) -
            (a.pinStatusCode === "B1" ? 1 : 0);
          return diff !== 0
            ? diff
            : new Date(b.rawCreatedAt) - new Date(a.rawCreatedAt);
        })
        .slice(0, 7);
    } catch (err) {
      console.error("공지사항 목록 조회 실패:", err);
      noticeList.value = [];
    }
  };

  const fetchPmemList = async (projectId) => {
    try {
      const res = await api.get(`/GroupMemList/${projectId}`);
      projectMembers.value = res.data || [];
    } catch (err) {
      console.error("구성원 목록 조회 실패:", err);
      projectMembers.value = [];
    }
  };

  // 메모리스트
  const fetchMemoList = async (projectId, userId) => {
    try {
      const res = await api.get(`/MemoList/${projectId}`, {
        params: { userId },
      });
      memoList.value = res.data || [];
    } catch (err) {
      console.error("메모 목록 조회 실패:", err);
      memoList.value = [];
    }
  };

  // 메모저장
  const saveMemo = async ({
    projectId,
    userId,
    memoId = null,
    memoContent,
    isEditMode = false,
  }) => {
    if (!userId) return;

    if (isEditMode) {
      await api.post("/MemoContentUpdate", {
        memoId,
        projectId,
        userId,
        memoContent,
      });
    } else {
      await api.post("/MemoRegister", {
        projectId,
        userId,
        memoContent,
      });
    }

    await fetchMemoList(projectId, userId);
  };

  // 메모삭제
  const deleteMemo = async (memoId, userId, projectId) => {
    await api.post("/MemoStatUpdate", {
      memoId,
      userId,
    });

    await fetchMemoList(projectId, userId);
  };

  const fetchSubProject = async (projectId) => {
    try {
      const res = await api.get(`/ProjectSubList/${projectId}`);
      subProjects.value = res.data || [];
    } catch (err) {
      console.error("하위프로젝트 조회 실패:", err);
      subProjects.value = [];
    }
  };

  const fetchAll = async (projectId, userId) => {
    await fetchProjectDetail(projectId);
    await fetchMilestoneList(projectId);
    await fetchSubProject(projectId);
    await fetchTaskSummary(projectId);
    await fetchNoticeList(projectId);
    await fetchPmemList(projectId);
    await fetchMemoList(projectId, userId);
  };

  return {
    projectInfo,
    milestoneList,
    taskSummaryData,
    noticeList,
    projectMembers,
    memoList,
    subProjects,

    isMilestoneUsed,
    milestoneLabel,
    isMilestoneEnabled,
    milestoneTabs,
    groupedMembers,

    fetchProjectDetail,
    fetchMilestoneList,
    fetchTaskSummary,
    fetchNoticeList,
    fetchPmemList,
    fetchMemoList,
    saveMemo,
    deleteMemo,
    fetchSubProject,
    fetchAll,
  };
});