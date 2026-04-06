import { defineStore } from "pinia";
import { ref, computed, watch } from "vue";
import api from "../utils/api";
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
    const res = await api.get("/task-total-info", {
      params: { projectId },
    });

    const {
      userList: uList,
      taskTypeList: tList,
      milestoneList: mList,
      projectList: pList,
    } = res.data;

    // 기초 리스트
    userList.value = uList.map((u) => ({ name: u.userName, value: u.userId }));
    taskTypeList.value = tList;

    // 마일스톤 리스트 매핑 및 초기값
    milestoneList.value = mList.map((m) => ({
      name: m.milestoneName,
      value: m.milestoneId,
    }));

    if (milestoneList.value.length > 0) {
      form.value.milestone = milestoneList.value[0].name;
      form.value.milestoneId = milestoneList.value[0].value;
    } else {
      form.value.milestone = "등록된 마일스톤 없음";
    }

    // 2. 프로젝트 정보
    if (pList && pList.length > 0) {
      const currentProj = pList.find((proj) => proj.projectId == projectId);

      if (currentProj) {
        // 만약 현재 프로젝트가 하위 프로젝트일때
        if (currentProj.parentProjectId) {
          const parentProj = pList.find(
            (p) => p.projectId == currentProj.parentProjectId,
          );
          form.value.projectName = parentProj
            ? parentProj.projectName
            : "상위 프로젝트 없음";
          form.value.projectId = currentProj.parentProjectId;

          form.value.subProjectName = currentProj.projectName;
          form.value.subProjectId = currentProj.projectId;
        } else {
          // 현재 프로젝트가 상위 프로젝트일때
          form.value.projectName = currentProj.projectName;
          form.value.projectId = currentProj.projectId;
          form.value.subProjectName = "";
          form.value.subProjectId = "";
        }
      }
    }

    await loadCommonCodes();
  };
  // 초기화 (수정용)
  const initEdit = async (taskId) => {
    resetForm();

    // 프로시저 한 번 호출로 전부 조회
    const res = await api.get("/task-total-info", {
      params: { taskId },
    });

    const {
      taskDetail,
      projectList,
      userList: uList,
      taskTypeList: rawTypeList,
      milestoneList,
    } = res.data;

    const d = taskDetail[0];
    const pd = projectList;

    const codeRes = await api.get("/code", {
      params: { groupValue: ["0H", "0G"] },
    });
    priorityList.value = codeRes.data.c0H;
    statusList.value = codeRes.data.c0G;

    taskTypeList.value = rawTypeList;
    userList.value = uList.map((u) => ({
      name: u.userName,
      value: u.userId,
    }));
    milestoneList.value = milestoneList.map((m) => ({
      name: m.milestoneName,
      value: m.milestoneId,
    }));

    // 프로젝트 정보 세팅
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

  //  공통 코드 로드(우선순위, 상태)
  const loadCommonCodes = async () => {
    const codeRes = await api.get("/code", {
      params: { groupValue: ["0H", "0G"] },
    });

    priorityList.value = codeRes.data.c0H;
    statusList.value = codeRes.data.c0G;
  };

  //  담당자
  const openUserModal = () => {
    userModal.value = true;
  };

  const selectUser = (val) => {
    const selectedName =
      typeof val === "object" ? val.name || val.userName : val;

    const found = userList.value.find(
      (u) => u.name === selectedName || u.userName === selectedName,
    );

    if (found) {
      form.value.assigneeName = found.name || found.userName;
      form.value.assigneeUserId = found.value || found.userId;

      // 선택 후 모달 닫기
      userModal.value = false;
    }
  }; // 마일스톤
  const fetchMilestones = async (pId) => {
    if (!pId) return;
    const res = await api.get("/taskMileStone", {
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
    (newVal) => {
      const status = Number(newVal);
      const isFinished = [3, 6].includes(status); // 완료(3) 또는 종료(6)

      if (isFinished) {
        const sDate = form.value.startDate;
        const eDate = form.value.dueDate;

        if (sDate && eDate) {
          const start = new Date(sDate);
          const end = new Date(eDate);
          const diffDays =
            Math.round((end - start) / (1000 * 60 * 60 * 24)) + 1;
          actualHours.value = `${Math.max(1, diffDays) * 8}시간`;
          form.value.progressRate = 100;
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
    await api.post("/tasks", payload);
  };

  // ───────────── 수정 ─────────────
  const updateTask = async (taskId, editorUserId) => {
    const status = Number(form.value.taskStatusId);

    // 1. 반려 처리 (상태 4)
    if (status === 4) {
      const { value: text, isConfirmed } = await Swal.fire({
        title: "업무 반려",
        input: "textarea",
        inputLabel: "반려 사유를 입력해주세요.",
        showCancelButton: true,
        confirmButtonText: "반려 확정",
        cancelButtonText: "취소",
        inputValidator: (value) => !value && "반려 사유는 필수입니다!",
      });

      if (!isConfirmed) return false;

      // 반려 사유 서버 전송 및 업무 수정 병행
      rejectReason.value = text;
      await rejectTask(taskId, editorUserId);
    }

    // 2. 종료 확인 (상태 6)
    if (status === 6) {
      const result = await Swal.fire({
        title: "업무를 종료하시겠습니까?",
        text: "종료 후에는 상태를 변경할 수 없습니다.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "종료 저장",
      });

      if (!result.isConfirmed) return false;
    }

    // 3. 최종 유효성 검사 및 저장
    try {
      validateForm();
      await api.put(`/task/${taskId}`, {
        ...buildPayload(),
        taskId: Number(taskId),
        rejectionReason: rejectReason.value, // 반려 사유 포함
      });

      await Swal.fire(
        "저장 완료",
        "업무 정보가 업데이트되었습니다.",
        "success",
      );
      return true;
    } catch (error) {
      Swal.fire("오류", error.message, "error");
      return false;
    }
  };
  // ───────────── 반려 처리 ─────────────
  const rejectTask = async (taskId, editorUserId) => {
    const payload = {
      taskId: Number(taskId),
      rejectionReason: rejectReason.value,
      rejectedBy: editorUserId, // 현재 수정하는 사람의 ID
    };
    await api.post("/reject", payload);
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
