import { defineStore } from "pinia";
import admin from "../utils/admin";
import api from "../utils/api";

export const useEmpStore = defineStore("emp", {
  state: () => ({
    empList: [],
    empList4Group: [],
    user: {},
  }),
  getters: {},
  actions: {
    async getEmpList() {
      await admin.get("/emp").then((response) => {
        this.empList = response.data;
      });
    },
    async getEmpList4Group() {
      await admin.get("/emp/group").then((response) => {
        this.empList4Group = response.data;
      });
    },
    async getEmp(id) {
      const response = await admin.get(`/emp/${id}`);
      return response.data;
    },
    async chageStatus(id) {
      const response = await admin.put(`/emp/${id}/status`);
      return response.data;
    },
    async registerEmp(data) {
      const response = await admin.post("/emp", data);
      return response;
    },
    async updateEmp(data) {
      console.log(data);
      const response = await admin.put(`/emp/${data.userId}`, data);
      if (response) {
        console.log("업뎃 성공", data);
      } else {
        console.error("업뎃 실패", data);
      }
    },
    async getUser(id) {
      const response = await api.get(`/emp/${id}`);
      this.user = response.data;
      return this.user;
    },
  },
});
