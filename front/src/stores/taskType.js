import { defineStore } from "pinia";
import { ref } from "vue";
import api from "../utils/api";
import { useAuthStore } from "./auth";

export const useTypeStore = defineStore("taskType", () => {
  const typeList = ref([]);
  const authStore = useAuthStore();
  // ── 목록 조회 ──
  async function getTypeList() {
    try {
      const res = await api.get("/admin/task/type");
      console.log(res);
      typeList.value = res.data;
    } catch (err) {
      console.error("상태 목록 조회 실패:", err);
      throw err;
    }
  }

  // ── 단건 조회 ──
  async function getType(taskTypeId) {
    try {
      const res = await api.get(`/admin/task/type/${taskTypeId}`);
      return res.data;
    } catch (err) {
      console.error("상태 조회 실패:", err);
      throw err;
    }
  }

  // ── 등록 ──
  async function createType(form) {
    try {
      form.userId = authStore.user.userId;
      const res = await api.post("/admin/task/type", form);
      return res.data;
    } catch (err) {
      console.error("상태 등록 실패:", err);
      throw err;
    }
  }

  // ── 수정 ──
  async function updateType(form) {
    try {
      // const payload = { userId: authStore.user.userId, ...form };
      form.userId = authStore.user.userId;
      // console.log(payload, form);
      const res = await api.put(
        `/admin/task/type/modify/${form.taskTypeId}`,
        form,
      );
      return res.data;
    } catch (err) {
      console.error("상태 수정 실패:", err);
      throw err;
    }
  }

  // ── 활성화 상태 토글 ──
  async function toggleActive(row) {
    try {
      console.log(authStore.user.userId);
      const res = await api.put(`/admin/task/type/modify/isActive`, {
        taskTypeId: row.taskTypeId,
        isActive: row.isActive,
        userId: authStore.user.userId,
      });
      // 로컬 상태도 즉시 반영
      const target = typeList.value.find(
        (s) => s.taskTypeId === row.taskTypeId,
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
      const res = await api.put(`/admin/task/type/modify/isFinal`, {
        taskTypeId: row.taskTypeId,
        isFinal: row.isFinal,
        userId: authStore.user.userId,
      });
      // 로컬 상태도 즉시 반영
      const target = typeList.value.find(
        (s) => s.taskTypeId === row.taskTypeId,
      );
      return res.data;
    } catch (err) {
      console.error("상태 변경 실패:", err);
      throw err;
    }
  }
  // 상태명 중복 체크
  async function checkDuplicate(typeName) {
    try {
      const res = await api.get(`/admin/task/type/checkDup/${typeName}`);
      const result = res.data;
      return result;
    } catch (e) {
      console.log(`중복 확인 실패 : ${e}`);
      throw e;
    }
  }

  return {
    typeList,
    getTypeList,
    getType,
    createType,
    updateType,
    toggleActive,
    changeFinal,
    checkDuplicate,
  };
});
