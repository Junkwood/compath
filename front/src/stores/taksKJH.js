import { defineStore } from "pinia";
import axios from "axios";

export const usetaskKJHStore = defineStore("taskKJH", {
  state: () => ({
    projectName: null,
    taskAllList: [],
    taskDetail: {},
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

    // 업무 상세 정보
    async getTaskById(id) {
      let result = await axios.get("/api/tasks/detail/" + id);
      this.taskDetail = result.data;
      console.log("업무상세정보: ", this.taskDetail);
    },
  },
});
