import { defineStore } from "pinia";
import axios from "axios";

export const useprojectKJHStore = defineStore("projectKJH", {
  state: () => ({}),
  getters: {},
  actions: {
    // 프로젝트 구성원
    async getProjectName(id) {
      this.projectName = (await axios.get("/api/tasks/projectname/" + id)).data;
    },
  },
});
