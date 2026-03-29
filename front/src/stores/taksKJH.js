import { defineStore } from "pinia";
import axios from "axios";

export const usetaskKJHStore = defineStore("taskKJH", {
  state: () => ({
    projectName: null,
    taskAllList: [],
  }),
  getters: {},
  actions: {
    // 프로젝트 이름
    async getProjectName(id) {
      this.projectName = (await axios.get("/api/tasks/projectname/" + id)).data;
    },

    // 프로젝트별 전체 업무 목록
    async getAllTask(id) {
      this.taskAllList = (await axios.get("/api/tasks/" + id)).data;
    },
  },
});
