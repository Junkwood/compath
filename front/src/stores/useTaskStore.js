import { defineStore } from "pinia";
import { ref, computed, watch } from "vue";
import api from "../utils/api";
import Swal from "sweetalert2";

export const useTaskStore = defineStore("task", () => {
  // ───────────── 상태 ─────────────
  const taskTypeList = ref([]);
  const priorityList = ref([]);
  const statusList = ref([]);
  const milestoneList = ref([]);
  const userList = ref([]);
  const rejectReason = ref("");
  const originalForm = ref(null);
  const isOriginallyTerminated = ref(false);
  const actualHours = ref("");
  const userModal = ref(false);
  const milestoneModal = ref(false);
  const projectList = ref([]);
  const recommandTask = ref([]);
  const deletedAttachmentIds = ref([]);

  // ───────────── computed ─────────────
  // 마일스톤 존재 여부
  const hasMilestone = computed(() => milestoneList.value.length > 0);

  //상위 프로젝트에서 하위 프로젝트 선택시
  const subProjectList = computed(() =>
    projectList.value.filter((p) => p.parentProjectId),
  );

  // 종료 상태 ID 목록 (IS_FINAL = 'O1')
  const finishedIds = computed(() =>
    (statusList.value ?? [])
      .filter((s) => s.isFinal === "O1" || s.statusName === "개발완료")
      .map((s) => s.taskStatusId),
  );

  // 등록 시 시작전/진행중만, 수정 시 전체
  const filteredStatusList = computed(() => {
    if (form.value.taskId) return statusList.value;
    return statusList.value.filter((s) => [10, 11].includes(s.taskStatusId));
  });

  // 소요시간/진척도 표시 여부
  const isCompletedStatus = computed(
    () =>
      finishedIds.value.includes(Number(form.value.taskStatusId)) ||
      Number(form.value.taskStatusId) === 12,
  );

  // ───────────── Form ─────────────
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
    parentTaskId: "",
    meetingLogId: "",
  };

  const form = ref({ ...initialForm });
  // ───────────── 초기화 (등록용) ─────────────
  const initCreate = async (projectId, parentTaskId = null) => {
    resetForm();
    const res = await api.get("/task-total-info", { params: { projectId } });
    const {
      userList: uList,
      taskTypeList: tList,
      milestoneList: mList,
      projectList: pList,
      statusList: rawStatusList,
    } = res.data;

    userList.value = uList.map((u) => ({
      name: u.userName,
      value: u.userId,
      // userType: u.userType,
      userType: u.userType,
    }));

    taskTypeList.value = tList;
    statusList.value = rawStatusList;
    projectList.value = pList; //하위 프로젝트 가져오기

    milestoneList.value = mList
      .filter((m) => m.isFinal !== "E3")
      .map((m) => ({
        name: m.milestoneName,
        value: m.milestoneId,
      }));
    if (milestoneList.value.length > 0) {
      form.value.milestone = "";
      form.value.milestoneId = "";
    } else {
      form.value.milestone = "등록된 마일스톤 없음";
      form.value.milestoneId = "";
    }
    if (pList && pList.length > 0) {
      const currentProj = pList.find((proj) => proj.projectId == projectId);
      if (currentProj) {
        if (currentProj.parentProjectId) {
          const parentProj = pList.find(
            (p) => p.projectId == currentProj.parentProjectId,
          );
          form.value.projectName =
            parentProj?.projectName ?? "상위 프로젝트 없음";
          form.value.projectId = currentProj.parentProjectId;
          form.value.subProjectName = currentProj.projectName;
          form.value.subProjectId = currentProj.projectId;
        } else {
          form.value.projectName = currentProj.projectName;
          form.value.projectId = currentProj.projectId;
          form.value.subProjectName = "";
          form.value.subProjectId = "";
        }
      }
    }

    // 우선순위만 공통코드에서
    const codeRes = await api.get("/code", { params: { groupValue: ["0H"] } });
    priorityList.value = codeRes.data.c0H;

    if (parentTaskId) {
      const parentRes = await api.get("/task-total-info", {
        params: { taskId: parentTaskId },
      });
      const parent = parentRes.data.taskDetail[0];
      form.value.taskTypeId = parent.taskTypeId;
      form.value.assigneeUserId = parent.assigneeUserId;
      form.value.assigneeName = parent.assigneeName;
      form.value.parentTaskId = Number(parentTaskId);
      form.value.milestoneId = parent.milestoneId;
      form.value.milestone =
        milestoneList.value.find((m) => m.value === parent.milestoneId)?.name ??
        form.value.milestone;
    }
  };

  // ───────────── 초기화 (수정용) ─────────────
  const attachmentList = ref([]);
  const initEdit = async (taskId) => {
    deletedAttachmentIds.value = [];
    resetForm();

    const res = await api.get("/task-total-info", { params: { taskId } });
    const {
      taskDetail,
      projectList,
      userList: uList,
      taskTypeList: rawTypeList,
      milestoneList: rawMilestoneList,
      statusList: rawStatusList,
      attachmentList: rawAttachmentList,
    } = res.data;
    attachmentList.value = (rawAttachmentList ?? []).map((f) => ({
      ...f,
      fileName: f.fileName || f.filename,
      attachmentId: f.attachmentId || f.id,
    }));
    const d = taskDetail[0];
    const pd = projectList;

    // 우선순위만 공통코드에서
    const codeRes = await api.get("/code", { params: { groupValue: ["0H"] } });
    priorityList.value = codeRes.data.c0H;

    // 업무상태는 프로시저에서
    statusList.value = rawStatusList;
    taskTypeList.value = rawTypeList;
    userList.value = uList.map((u) => ({
      name: u.userName,
      value: u.userId,
      userType: u.userType,
    }));
    milestoneList.value = rawMilestoneList.map((m) => ({
      name: m.milestoneName,
      value: m.milestoneId,
      statusCode: m.statusCode,
    }));

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
        rawMilestoneList.find((m) => m.milestoneId === d.milestoneId)
          ?.milestoneName ?? "마일스톤 없음",

      displayActualHours:
        d.totalTimeEntries > 0 ? d.totalTimeEntries : (d.actualHours ?? 0),
    };

    originalForm.value = { ...form.value };
    isOriginallyTerminated.value =
      (rawStatusList ?? []).find(
        (s) => s.taskStatusId === Number(d.taskStatusId),
      )?.isFinal === "O1";

    calcEstTime();
  };

  // ───────────── 담당자 ─────────────
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
      userModal.value = false;
    }
  };

  // ───────────── 마일스톤 ─────────────
  const fetchMilestones = async (pId) => {
    if (!pId) return;
    const res = await api.get("/taskMileStone", { params: { projectId: pId } });
    milestoneList.value = res.data.map((m) => ({
      name: m.milestoneName,
      value: m.milestoneId,
    }));
    const exists = milestoneList.value.find(
      (m) => m.value === form.value.milestoneId,
    );

    if (!exists) {
      form.value.milestoneId = "";
      form.value.milestone = "";
    }

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

  // ───────────── 우선순위 ─────────────
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
    form.value.estEndDate = dateStr;
    form.value.dueDate = dateStr;
    calcEstTime(true);
  };

  // ───────────── 추정시간 ─────────────
  const calcEstTime = () => {
    const estStart = form.value.estStartDate?.trim();
    const estEnd = form.value.estEndDate?.trim();
    const realStart = form.value.startDate?.trim();
    const realEnd = form.value.dueDate?.trim();

    let start = null;
    let end = null;

    if (estStart && estEnd) {
      // 예정 시작일 + 예정 종료일 둘 다 있을 때만 예정일 기준
      start = estStart;
      end = estEnd;
    } else if (realStart && realEnd) {
      // 하나라도 비어 있으면 실제 시작/마감일로 fallback
      start = realStart;
      end = realEnd;
    } else {
      return;
    }

    const workdays = countWorkdays(new Date(start), new Date(end));
    const hours = Math.max(1, workdays) * 8;
    form.value.estTime = `${hours}시간`;
  };
  // ───────────── 업무상태 변경 시 소요시간 자동계산 ─────────────
  const countWorkdays = (start, end) => {
    let count = 0;
    const cur = new Date(start);
    while (cur <= end) {
      const day = cur.getDay();
      if (day !== 0 && day !== 6) count++;
      cur.setDate(cur.getDate() + 1);
    }
    return count;
  };

  watch(
    () => form.value.taskStatusId,
    (newVal) => {
      const status = Number(newVal);
      const isFinished = finishedIds.value.includes(status);

      if (isFinished) {
        if (form.value.displayActualHours > 0) {
          actualHours.value = `${form.value.displayActualHours}시간`;
          form.value.progressRate = 100;
          return;
        }
        const { startDate, dueDate } = form.value;
        if (startDate && dueDate) {
          const workdays = countWorkdays(
            new Date(startDate),
            new Date(dueDate),
          );
          actualHours.value = `${Math.max(1, workdays) * 8}시간`;
          form.value.progressRate = 100;
        }
      } else {
        actualHours.value = "";
      }
    },
  );

  watch(
    () => ({
      estStartDate: form.value.estStartDate,
      estEndDate: form.value.estEndDate,
      startDate: form.value.startDate,
      dueDate: form.value.dueDate,
    }),
    () => calcEstTime(),
    { deep: true },
  );
  // watch(
  //   () => form.value.startDate,
  //   (v) => {
  //     if (!form.value.estStartDate) {
  //       form.value.estStartDate = v;
  //     }
  //   },
  // );

  // watch(
  //   () => form.value.dueDate,
  //   (v) => {
  //     if (!form.value.estEndDate) {
  //       form.value.estEndDate = v;
  //     }
  //   },
  // );
  // ───────────── 폼 초기화 ─────────────
  const resetForm = (mode = "create") => {
    if (mode === "edit" && originalForm.value) {
      form.value = { ...originalForm.value };
      actualHours.value = "";
      return;
    }
    const saved = {
      projectId: form.value.projectId,
      subProjectId: form.value.subProjectId,
      projectName: form.value.projectName,
      subProjectName: form.value.subProjectName,
      milestoneId: form.value.milestoneId,
      milestone: form.value.milestone,
      ...(form.value.parentTaskId
        ? {
            parentTaskId: form.value.parentTaskId,
            taskTypeId: form.value.taskTypeId,
            assigneeUserId: form.value.assigneeUserId,
            assigneeName: form.value.assigneeName,
          }
        : {}),
    };
    form.value = { ...initialForm, ...saved };
    actualHours.value = "";
  };

  // ───────────── payload 빌더 ─────────────
  const buildPayload = () => {
    const status = Number(
      String(form.value.taskStatusId).replace(/[^0-9]/g, ""),
    );
    const isFinished = finishedIds.value.includes(status);
    const estStartDate =
      form.value.estStartDate && form.value.estStartDate.trim() !== ""
        ? form.value.estStartDate
        : form.value.startDate;

    const estEndDate =
      form.value.estEndDate && form.value.estEndDate.trim() !== ""
        ? form.value.estEndDate
        : form.value.dueDate;
    const payload = {
      ...form.value,
      taskStatusId: status,
      projectId: form.value.subProjectId
        ? Number(form.value.subProjectId)
        : Number(form.value.projectId),
      estStartDate,
      estEndDate,
      startDate: form.value.taskId ? form.value.startDate || null : null,
      dueDate: form.value.taskId ? form.value.dueDate || null : null,
      estimatedHours:
        parseInt(String(form.value.estTime).replace(/[^0-9]/g, "")) || 0,
    };

    if (isFinished) {
      const detailSum = parseInt(
        String(form.value.displayActualHours || 0).replace(/[^0-9]/g, ""),
      );

      if (detailSum > 0) {
        payload.actualHours = detailSum;
      } else {
        const { startDate, dueDate } = form.value;
        if (startDate && dueDate) {
          const workdays = countWorkdays(
            new Date(startDate),
            new Date(dueDate),
          );
          payload.actualHours = Math.max(1, workdays) * 8;
        } else {
          payload.actualHours = 0;
        }
      }
      payload.progressRate = 100;
    } else {
      payload.actualHours = null;
      payload.progressRate = Number(form.value.progressRate) || 0;
    }
    payload.deletedAttachmentIds = deletedAttachmentIds.value;
    return payload;
  };

  // ───────────── 유효성 검사 ─────────────
  const validateForm = () => {
    if (hasMilestone.value && !form.value.milestoneId)
      throw new Error("마일스톤을 선택해 주세요.");
    if (!form.value.taskTypeId) throw new Error("업무 유형을 선택해 주세요.");
    if (!form.value.assigneeUserId) throw new Error("담당자를 선택해 주세요.");
    if (!form.value.title?.trim()) throw new Error("업무명을 입력해 주세요.");
    if (!form.value.taskStatusId) throw new Error("업무 상태를 선택해 주세요.");
    if (!form.value.priorityCode) throw new Error("우선순위를 선택해 주세요.");
  };

  // ───────────── 등록 ─────────────
  const createTask = async (createdBy, files = []) => {
    validateForm();
    let obj = buildPayload();

    const formData = new FormData();
    formData.append(
      "dto",
      new Blob([JSON.stringify({ ...obj, createdBy })], {
        type: "application/json",
      }),
    );

    if (files && files.length > 0) {
      files.forEach((file) => {
        formData.append("files", file.raw);
      });
    }

    await api.post("/tasks", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  };

  // ───────────── 수정 ─────────────
  const updateTask = async (taskId, editorUserId, files = []) => {
    calcEstTime();
    const status = Number(form.value.taskStatusId);

    // 현재 상태 이름 가져오기
    const statusName = statusList.value.find(
      (s) => s.taskStatusId === status,
    )?.statusName;
    // 반려 처리
    if (statusName === "반려") {
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
      rejectReason.value = text;
      await rejectTask(taskId, editorUserId);
      await Swal.fire("완료", "업무가 반려 처리되었습니다.", "success");
      return true;
    }

    // 종료 확인
    if (statusName === "완료") {
      const result = await Swal.fire({
        title: "업무를 종료하시겠습니까?",
        text: "종료 후에는 상태를 변경할 수 없습니다.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "종료 저장",
      });
      if (!result.isConfirmed) return false;
    }

    try {
      validateForm();

      const payload = {
        ...buildPayload(),
        taskId: Number(taskId),
        rejectionReason: rejectReason.value,
        editorUserId: editorUserId,
      };

      const formData = new FormData();
      formData.append(
        "dto",
        new Blob([JSON.stringify(payload)], { type: "application/json" }),
      );
      if (files && files.length > 0) {
        files.forEach((file) => formData.append("files", file.raw));
      }

      await api.put(`/task/${taskId}`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
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
    await api.post("/reject", {
      taskId: Number(taskId),
      rejectionReason: rejectReason.value,
      rejectedBy: editorUserId,
    });
  };

  // ───────────── 첨부파일 삭제 ─────────────
  const removeExistingFile = (attachmentId) => {
    attachmentList.value = attachmentList.value.filter(
      (f) => f.attachmentId !== attachmentId,
    );
    deletedAttachmentIds.value.push(attachmentId);
  };

  return {
    form,
    actualHours,
    hasMilestone,
    finishedIds,
    filteredStatusList,
    isCompletedStatus,
    taskTypeList,
    priorityList,
    statusList,
    milestoneList,
    userList,
    userModal,
    milestoneModal,
    originalForm,
    isOriginallyTerminated,
    rejectReason,
    subProjectList,
    attachmentList,
    deletedAttachmentIds,
    removeExistingFile,
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
    rejectTask,
    recommandTask,
  };
});
