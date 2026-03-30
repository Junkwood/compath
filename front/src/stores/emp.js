import { defineStore } from "pinia";
import axios from "axios";

export const useEmpStore = defineStore("emp", {
  state: () => ({
    empList: [],
  }),
  getters: {},
  actions: {
    async getEmpList() {
      await axios.get("/api/emp/list").then((response) => {
        this.empList = response.data;
      });
    },
    async getEmp(id) {
      const response = await axios.get(`/api/emp/info/${id}`);
      return response.data;
    },
    async chageStatus(id) {
      const response = await axios.get(`/api/emp/status/${id}`);
      return response.data;
    },
    async registerEmp(data) {
      const response = await axios.post("/api/emp", data);
      return response;
    },
    async updateEmp(data) {
      const response = await axios.put("/api/emp", data);
      if (response) {
        console.log("업뎃 성공", data);
      } else {
        console.error("업뎃 실패", data);
      }
    },
  },
});
