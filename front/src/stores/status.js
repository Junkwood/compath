import { defineStore } from "pinia";
import { ref } from "vue";
import api from "../utils/api";
import { useAuthStore } from "./auth";

export const useStatusStore = defineStore("status", () => {
  const statusList = ref([]);
  const authStore = useAuthStore();
  // ── 목록 조회 ──
  async function getStatusList() {
    try {
      const res = await api.get("/taskStatus");
      console.log(res);
      statusList.value = res.data;
    } catch (err) {
      console.error("상태 목록 조회 실패:", err);
      throw err;
    }
  }

  // ── 단건 조회 ──
  async function getStatus(taskStatusId) {
    try {
      const res = await api.get(`/taskStatus/${taskStatusId}`);
      return res.data;
    } catch (err) {
      console.error("상태 조회 실패:", err);
      throw err;
    }
  }

  // ── 등록 ──
  async function createStatus(form) {
    try {
      const res = await api.post("/taskStatus", form);
      return res.data;
    } catch (err) {
      console.error("상태 등록 실패:", err);
      throw err;
    }
  }

  // ── 수정 ──
  async function updateStatus(form) {
    try {
      const res = await api.put(`/taskStatus/modifiy`, form);
      return res.data;
    } catch (err) {
      console.error("상태 수정 실패:", err);
      throw err;
    }
  }

  // ── 활성화 상태 토글 ──
  async function changeStatus(row) {
    try {
      console.log(authStore.user.userId);
      const res = await api.put(`/taskStatus/modify/isActive`, {
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
      console.log(authStore.user.userId);
      const res = await api.put(`/taskStatus/modify/isFinal`, {
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

  return {
    statusList,
    getStatusList,
    getStatus,
    createStatus,
    updateStatus,
    changeStatus,
    changeFinal,
  };
});
