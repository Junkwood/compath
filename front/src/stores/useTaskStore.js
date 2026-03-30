import { defineStore } from "pinia";
import { ref, computed, watch } from "vue";
import axios from "axios";

export const useTaskStore = defineStore("task", () => {
  //  공통 코드 목록
  const taskTypeList = ref([]);
  const priorityList = ref([]);
  const statusList = ref([]);
  const milestoneList = ref([]);
  const userList = ref([]);

  // 마일스톤 존재 여부
  const hasMilestone = computed(() => milestoneList.value.length > 0);

  // 모달 상태
  const userModal = ref(false);
  const milestoneModal = ref(false);

  //  소요시간 (수정 전용)
  const actualHours = ref("");

  //진척도
  const progressOptions = [0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100];

  //  Form
  const initialForm = {
    projectId: "",
    subProjectId: "",
    projectName: "",
    subProjectName: "",
    taskTypeId: "",
    title: "",
    assigneeUserId: "",
    assigneeName: "",
    content: "",
    taskStatusId: "",
    priorityCode: "",
    milestoneId: "",
    milestone: "",
    startDate: "",
    dueDate: "",
    estStartDate: "",
    estEndDate: "",
    estTime: "",
    progressRate: "",
  };

  const form = ref({ ...initialForm });

  //  초기화 (등록용)
  const initCreate = async (projectId) => {
    resetForm();
    await loadCommonCodes();

    statusList.value = statusList.value.filter(
      (s) => s.codeName === "시작 전" || s.codeName === "진행중",
    );

    if (projectId) {
      const res = await axios.get(`/api/projectDetail/${projectId}`);
      const d = res.data;
      form.value.projectName = d.displayProjectName;
      form.value.subProjectName = d.displaySubProjectName;
      form.value.projectId = d.parentProjectId || d.projectId;
      form.value.subProjectId = d.parentProjectId ? d.projectId : null;
      // 마일스톤은 항상 최상위 프로젝트 기준으로 조회
      await fetchMilestones(form.value.projectId);

      form.value.startDate = "";
      form.value.dueDate = "";
    }

    if (statusList.value.length > 0)
      form.value.taskStatusId = statusList.value[0].codeValue;
    if (taskTypeList.value.length > 0)
      form.value.taskTypeId = taskTypeList.value[0].taskTypeId;
  };

  // 초기화 (수정용)
  const initEdit = async (taskId) => {
    resetForm();
    await loadCommonCodes();

    const res = await axios.get(`/api/task/${taskId}`);
    const d = res.data;

    const projectRes = await axios.get(`/api/projectDetail/${d.projectId}`);
    const pd = projectRes.data;

    // 마일스톤은 항상 최상위 프로젝트 기준으로 조회
    await fetchMilestones(pd.parentProjectId || pd.projectId);

    form.value = {
      ...form.value,
      ...d,
      projectName: pd.displayProjectName,
      subProjectName: pd.displaySubProjectName,
      projectId: pd.parentProjectId || pd.projectId,
      subProjectId: pd.parentProjectId ? pd.projectId : null,
      estStartDate: d.estStartDate ? d.estStartDate.split("T")[0] : "",
      estEndDate: d.estEndDate ? d.estEndDate.split("T")[0] : "",
      startDate: d.startDate ? d.startDate.split("T")[0] : "",
      dueDate: d.dueDate ? d.dueDate.split("T")[0] : "",
      estTime: d.estimatedHours ? `${d.estimatedHours}시간` : "0시간",
      actualHours: [3, 6].includes(Number(d.taskStatusId))
        ? d.actualHours
        : null,
      milestone:
        milestoneList.value.find((m) => m.value === d.milestoneId)?.name ||
        "마일스톤 없음",
    };
    actualHours.value = form.value.actualHours
      ? `${form.value.actualHours}시간`
      : "";
  };

  //  공통 코드 로드
  const loadCommonCodes = async () => {
    const [typeRes, codeRes] = await Promise.all([
      axios.get("/api/taskType"),
      axios.get("/api/code", { params: { groupValue: ["0H", "0G"] } }),
    ]);
    taskTypeList.value = typeRes.data;
    priorityList.value = codeRes.data.c0H;
    statusList.value = codeRes.data.c0G;
  };

  //  담당자
  const openUserModal = async () => {
    const res = await axios.get("/api/taskUser");
    userList.value = res.data.map((u) => ({
      ...u,
      name: u.userName || u.user_name,
      value: u.userId || u.user_id,
    }));
    userModal.value = true;
  };

  const selectUser = (val) => {
    const selectedName =
      typeof val === "object" ? val.name || val.userName : val;

    const found = userList.value.find(
      (u) => u.userName === selectedName || u.name === selectedName,
    );
    if (found) {
      form.value.assigneeName = found.userName || found.name;
      form.value.assigneeUserId = found.userId || found.value;
    }
  };
  // 마일스톤
  const fetchMilestones = async (pId) => {
    if (!pId) return;
    const res = await axios.get("/api/taskMileStone", {
      params: { projectId: pId },
    });
    milestoneList.value = res.data.map((m) => ({
      name: m.milestoneName,
      value: m.milestoneId,
    }));

    if (milestoneList.value.length > 0) {
      const current = milestoneList.value.find(
        (m) => m.value === Number(form.value.milestoneId),
      );
      form.value.milestone = current?.name ?? milestoneList.value[0].name;
      if (!current) form.value.milestoneId = milestoneList.value[0].value;
    } else {
      form.value.milestone = "등록된 마일스톤 없음";
      form.value.milestoneId = "";
    }
  };

  const selectMilestone = (val) => {
    form.value.milestone = val.name;
    form.value.milestoneId = val.value;
  };

  // 우선순위
  const onPriorityChange = () => {
    const val = form.value.priorityCode;
    const today = new Date();

    if (val.includes("상") || val === "H1") {
      today.setDate(today.getDate() + 3);
    } else if (val.includes("중") || val === "H2") {
      today.setDate(today.getDate() + 7);
    } else {
      today.setDate(today.getDate() + 14);
    }

    const dateStr = today.toISOString().split("T")[0];

    form.value.estEndDate = dateStr; // 등록용
    form.value.dueDate = dateStr; // 수정용

    calcEstTime();
  };
  //추정시간
  const calcEstTime = () => {
    if (
      form.value.taskId &&
      form.value.estTime &&
      form.value.estTime !== "0시간"
    ) {
      return;
    }

    const sDate = form.value.startDate || form.value.estStartDate;
    const eDate = form.value.dueDate || form.value.estEndDate;

    if (sDate && eDate) {
      const start = new Date(sDate);
      const end = new Date(eDate);
      const diffTime = end - start;
      const days = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;

      if (days > 0) {
        form.value.estTime = `${days * 8}시간`;
      }
    }
  }; // 업무상태 → 소요시간 자동계산 (수정 전용)
  watch(
    () => form.value.taskStatusId,
    (newVal, oldVal) => {
      if (!oldVal) return;

      const status = Number(newVal);
      const isFinished = [3, 6].includes(status);

      if (isFinished) {
        const sDate = form.value.startDate;
        const eDate = form.value.dueDate || form.value.endDate; // 마감일 또는 실제종료일

        if (sDate && eDate) {
          const start = new Date(sDate);
          const end = new Date(eDate);

          start.setHours(0, 0, 0, 0);
          end.setHours(0, 0, 0, 0);

          const diffTime = end - start;
          const diffDays = Math.round(diffTime / (1000 * 60 * 60 * 24)) + 1; // 당일 포함

          const resultDays = diffDays <= 0 ? 1 : diffDays;
          actualHours.value = `${resultDays * 8}시간`;
          form.value.progressRate = 100;
        } else {
          actualHours.value = "날짜를 확인해주세요";
        }
      } else {
        actualHours.value = "";
      }
    },
  );
  // ───────────── 폼 초기화 (프로젝트 정보 유지) ─────────────
  const resetForm = () => {
    const saved = {
      projectId: form.value.projectId,
      subProjectId: form.value.subProjectId,
      projectName: form.value.projectName,
      subProjectName: form.value.subProjectName,
    };
    form.value = { ...initialForm, ...saved };
    actualHours.value = "";
  };

  // ───────────── payload 공통 빌더 ─────────────
  const buildPayload = () => {
    const status = Number(
      String(form.value.taskStatusId).replace(/[^0-9]/g, ""),
    );
    const isFinished = [3, 6].includes(status);

    const payload = {
      ...form.value,
      taskStatusId: status,
      projectId: form.value.subProjectId
        ? Number(form.value.subProjectId)
        : Number(form.value.projectId),
      estStartDate: form.value.estStartDate,
      estEndDate: form.value.estEndDate,
      startDate: form.value.taskId ? form.value.startDate || null : null,
      dueDate: form.value.taskId ? form.value.dueDate || null : null,
      estimatedHours:
        parseInt(String(form.value.estTime).replace(/[^0-9]/g, "")) || 0,
    };

    if (isFinished) {
      payload.actualHours = actualHours.value
        ? parseInt(String(actualHours.value).replace(/[^0-9]/g, ""))
        : null;
      payload.progressRate = 100;
    } else {
      payload.actualHours = null;
      payload.actual_hours = null;

      payload.progressRate = Number(form.value.progressRate) || 0;
      form.value.actualHours = null;
      form.value.actual_hours = null;

      actualHours.value = "";
    }
    return payload;
  };
  // ───────────── 등록 전 유효성 검사 ─────────────
  const validateForm = () => {
    if (hasMilestone.value && !form.value.milestoneId) {
      throw new Error("마일스톤을 선택해 주세요.");
    }
    if (!form.value.taskTypeId) throw new Error("업무 유형을 선택해 주세요.");
    if (!form.value.title?.trim()) throw new Error("업무명을 입력해 주세요.");
    if (!form.value.taskStatusId) throw new Error("업무 상태를 선택해 주세요.");
    if (!form.value.priorityCode) throw new Error("우선순위를 선택해 주세요.");
  };

  // ───────────── 등록 ─────────────
  const createTask = async () => {
    validateForm();
    const payload = buildPayload();
    await axios.post("/api/tasks", payload);
  };

  // ───────────── 수정 ─────────────
  const updateTask = async (taskId) => {
    const status = Number(form.value.taskStatusId);

    if ([3, 6].includes(status)) {
      if (
        !confirm(
          "업무를 '종료' 상태로 저장하시겠습니까?\n저장 후에는 진행 상태나 소요 시간을 임의로 변경할 수 없습니다.",
        )
      ) {
        return false;
      }
    }

    validateForm();
    await axios.put(`/api/task/${taskId}`, {
      ...buildPayload(),
      taskId: Number(taskId),
    });
    return true;
  };
  return {
    form,
    actualHours,
    hasMilestone,
    taskTypeList,
    priorityList,
    statusList,
    milestoneList,
    userList,
    userModal,
    milestoneModal,
    initCreate,
    initEdit,
    openUserModal,
    selectUser,
    fetchMilestones,
    selectMilestone,
    onPriorityChange,
    calcEstTime,
    validateForm,
    resetForm,
    createTask,
    updateTask,
  };
});
