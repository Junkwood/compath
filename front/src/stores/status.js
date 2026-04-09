import { defineStore } from "pinia";
import { ref } from "vue";
import admin from "../utils/admin";
import { useAuthStore } from "./auth";

export const useStatusStore = defineStore("status", () => {
  const statusList = ref([]);
  const activeStatusList = ref([]);
  const authStore = useAuthStore();
  // ── 목록 조회 ──
  async function getStatusList() {
    try {
      const res = await admin.get("/task/status");
      statusList.value = res.data;
    } catch (err) {
      console.error("상태 목록 조회 실패:", err);
      throw err;
    }
  }
  // ── 활성 상태 목록 조회 ──
  async function getActiveStatusList() {
    try {
      const res = await admin.get("/task/status/active");
      activeStatusList.value = res.data;
    } catch (err) {
      console.error("상태 목록 조회 실패:", err);
      throw err;
    }
  }
  // ── 단건 조회 ──
  async function getStatus(taskStatusId) {
    try {
      const res = await admin.get(`/task/status/${taskStatusId}`);
      return res.data;
    } catch (err) {
      console.error("상태 조회 실패:", err);
      throw err;
    }
  }

  // ── 등록 ──
  async function createStatus(form) {
    try {
      form.userId = authStore.user.userId;
      const res = await admin.post("/task/status", form);
      return res.data;
    } catch (err) {
      console.error("상태 등록 실패:", err);
      throw err;
    }
  }

  // ── 수정 ──
  async function updateStatus(form) {
    try {
      form.userId = authStore.user.userId;
      const res = await admin.put(
        `/task/status/modify/${form.taskStatusId}`,
        form,
      );
      return res.data;
    } catch (err) {
      console.error("상태 수정 실패:", err);
      throw err;
    }
  }

  // ── 활성화 상태 토글 ──
  async function changeStatus(row) {
    try {
      const res = await admin.put(`/task/status/modify/isActive`, {
        taskStatusId: row.taskStatusId,
        isActive: row.isActive,
        userId: authStore.user.userId,
      });
      // 로컬 상태도 즉시 반영
      const target = statusList.value.find(
        (s) => s.taskStatusId === row.taskStatusId,
      );
      return res.data;
    } catch (err) {
      console.error("상태 변경 실패:", err);
      throw err;
    }
  }
  // ── 활성화 상태 토글 ──
  async function changeFinal(row) {
    try {
      const res = await admin.put(`/task/status/modify/isFinal`, {
        taskStatusId: row.taskStatusId,
        isFinal: row.isFinal,
        userId: authStore.user.userId,
      });
      // 로컬 상태도 즉시 반영
      const target = statusList.value.find(
        (s) => s.taskStatusId === row.taskStatusId,
      );
      return res.data;
    } catch (err) {
      console.error("상태 변경 실패:", err);
      throw err;
    }
  }
  // 상태명 중복 체크
  async function checkDuplicate(statusName) {
    try {
      const res = await admin.get(`/task/status/checkDup/${statusName}`);
      const result = res.data;
      return result;
    } catch (e) {
      console.error(`중복 확인 실패 : ${e}`);
      throw e;
    }
  }

  return {
    statusList,
    activeStatusList,
    getStatusList,
    getActiveStatusList,
    getStatus,
    createStatus,
    updateStatus,
    changeStatus,
    changeFinal,
    checkDuplicate,
  };
});
