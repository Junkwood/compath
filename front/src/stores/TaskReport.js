import { defineStore } from "pinia";
import api from "../utils/api";

export const useTaskReport = defineStore("TaskReport", {
  state: () => ({ reportList: [], totalCount: 0 }),
  getters: {},
  actions: {
    async fetchReportList(params) {
      try {
        const response = await api.get("/taskReport", { params });
        this.reportList = response.data;
        this.totalCount = response.data.length;
        return response.data;
      } catch (error) {
        console.error("보고서 로딩 실패:", error);
      }
    },
  },
});