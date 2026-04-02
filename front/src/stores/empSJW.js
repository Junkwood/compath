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
      await api.get("/emp/list").then((response) => {
        this.empList = response.data;
      });
    },
    async getEmpList4Group() {
      await api.get("/emp/list/group").then((response) => {
        this.empList4Group = response.data;
      });
    },
    async getEmp(id) {
      const response = await api.get(`/emp/info/${id}`);
      return response.data;
    },
    async chageStatus(id) {
      const response = await api.get(`/emp/status/${id}`);
      return response.data;
    },
    async registerEmp(data) {
      const response = await api.post("/emp", data);
      return response;
    },
    async updateEmp(data) {
      const response = await api.put("/emp", data);
      if (response) {
        console.log("업뎃 성공", data);
      } else {
        console.error("업뎃 실패", data);
      }
    },
  },
});
