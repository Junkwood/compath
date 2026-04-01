import { defineStore } from "pinia";
import { ref, computed, watch } from "vue";
import axios from "axios";
import Swal from "sweetalert2";

export const useTaskStore = defineStore("task", () => {
  //  공통 코드 목록
  const taskTypeList = ref([]);
  const priorityList = ref([]);
  const statusList = ref([]);
  const milestoneList = ref([]);
  const userList = ref([]);
  const rejectReason = ref("");

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

    // 프로시저 한 번 호출로 전부 조회
    const res = await axios.get("/api/task-total-info", {
      params: { taskId },
    });

    const {
      taskDetail,
      projectList,
      userList,
      taskTypeList: rawTypeList,
      milestoneList,
    } = res.data;

    const d = taskDetail[0]; // 상세는 단건
    const pd = projectList;

    // 공통 코드는 별도 로드 (우선순위는 프로시저에 없으므로)
    const codeRes = await axios.get("/api/code", {
      params: { groupValue: ["0H", "0G"] },
    });
    priorityList.value = codeRes.data.c0H;
    statusList.value = codeRes.data.c0G;

    taskTypeList.value = rawTypeList;
    userList.value = userList.map((u) => ({
      name: u.userName,
      value: u.userId,
    }));
    milestoneList.value = milestoneList.map((m) => ({
      name: m.milestoneName,
      value: m.milestoneId,
    }));

    // 프로젝트 정보 세팅 (projectList에서 parent/child 구분)
    const parentProject = pd.find((p) => !p.parentProjectId) ?? pd[0];
    const subProject = pd.find((p) => p.parentProjectId);

    form.value = {
      ...form.value,
      ...d,
      projectName: parentProject?.projectName,
      subProjectName: subProject?.projectName ?? null,
      projectId: parentProject?.projectId,
      subProjectId: subProject?.projectId ?? null,
      estStartDate: d.estStartDate?.split("T")[0] ?? "",
      estEndDate: d.estEndDate?.split("T")[0] ?? "",
      startDate: d.startDate?.split("T")[0] ?? "",
      dueDate: d.dueDate?.split("T")[0] ?? "",
      estTime: d.estimatedHours ? `${d.estimatedHours}시간` : "0시간",
      milestone:
        milestoneList.value.find((m) => m.value === d.milestoneId)?.name ??
        "마일스톤 없음",
    };
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
    async (newVal, oldVal) => {
      if (!oldVal) return;
      const status = Number(newVal);

      //반려 상태일 때 모달
      if (status === 4) {
        const { value: text, isConfirmed } = await Swal.fire({
          title: "업무 반려",
          input: "textarea",
          inputLabel: "반려 사유를 입력해주세요.",
          inputPlaceholder: "사유를 입력하세요...",
          showCancelButton: true,
          confirmButtonText: "입력 완료",
          cancelButtonText: "취소",
          allowOutsideClick: false,
          inputValidator: (value) => {
            if (!value) return "반려 사유는 필수입니다!";
          },
        });

        if (isConfirmed && text) {
          rejectReason.value = text;
          Swal.fire(
            "사유 입력됨",
            "저장 버튼을 누르면 최종 반영됩니다.",
            "success",
          );
        } else {
          form.value.taskStatusId = oldVal;
          rejectReason.value = "";
        }
        return;
      }
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

    if ([6].includes(status)) {
      const result = await Swal.fire({
        title: "업무를 종료하시겠습니까?",
        text: "종료 후에는 상태나 소요 시간을 변경할 수 없습니다.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "네, 저장합니다!",
        cancelButtonText: "취소",
      });

      if (!result.isConfirmed) return false; // 취소 시 중단
    }

    validateForm();
    await axios.put(`/api/task/${taskId}`, {
      ...buildPayload(),
      taskId: Number(taskId),
    });
    return true;
  };

  // ───────────── 반려 처리 ─────────────
  const rejectTask = async (taskId, editorUserId) => {
    const payload = {
      taskId: Number(taskId),
      rejectionReason: rejectReason.value,
      rejectedBy: editorUserId, // 현재 수정하는 사람의 ID
    };
    await axios.post("/api/reject", payload);
  };

  return {
    rejectReason,
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
    rejectTask,
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
