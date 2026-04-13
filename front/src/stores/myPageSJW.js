import { defineStore } from "pinia";
import api from "../utils/api";

export const userMypageStore = defineStore("myPage", {
  state: () => ({
    adminSummary: {},
    taskList: [],
    taskSummary: {},
  }),
  getters: {},
  actions: {
    async getAdminSummary() {
      const res = await api.get("/mypage/adminSummary");
      this.adminSummary = res.data;
    },
    async getTaskSummary(userId) {
      const res = await api.get(`/mypage/taskSummary/${userId}`);
      this.taskSummary = res.data[0];
    },
    async getTaskList(userId) {
      const res = await api.get(`/mypage/taskList/${userId}`);
      this.taskList = res.data;
    },
  },
});
