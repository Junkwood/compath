import { defineStore } from "pinia";
import api from "../utils/api";

export const useEmpStore = defineStore("emp", {
  state: () => ({
    empList: [],
    empList4Group: [],
  }),
  getters: {},
  actions: {
    async getEmpList() {
      await api.get("/admin/emp").then((response) => {
        this.empList = response.data;
      });
    },
    async getEmpList4Group() {
      await api.get("/admin/emp/group").then((response) => {
        this.empList4Group = response.data;
      });
    },
    async getEmp(id) {
      const response = await api.get(`/admin/emp/${id}`);
      return response.data;
    },
    async chageStatus(id) {
      const response = await api.get(`/admin/emp/${id}/status`);
      return response.data;
    },
    async registerEmp(data) {
      const response = await api.post("/admin/emp", data);
      return response;
    },
    async updateEmp(data) {
      console.log(data);
      const response = await api.put(`/admin/emp/${data.userId}`, data);
      if (response) {
        console.log("업뎃 성공", data);
      } else {
        console.error("업뎃 실패", data);
      }
    },
  },
});
