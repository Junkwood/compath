import { defineStore } from "pinia";
import axios from "axios";

export const useEmpStore = defineStore("emp", {
  state: () => ({
    empList: [],
  }),
  getters: {},
  actions: {
    login(userData, token = null) {
      this.user = userData;
      this.token = token;
      localStorage.setItem("user", JSON.stringify(userData));
      if (token) {
        localStorage.setItem("token", token);
      }
    },
    async getEmpList() {
      await axios.get("/api/emp/list").then((response) => {
        this.empList = response.data;
      });
    },
    async chageStatus(id) {
      const response = await axios.get(`/api/emp/status/${id}`);
      return response.data;
    },
    async registerEmp(data) {
      const response = await axios.post("/api/emp", data);
      return response;
    },
  },
});
