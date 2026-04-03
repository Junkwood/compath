import { defineStore } from "pinia";
import axios from "axios";

export const usetaskKJHStore = defineStore("taskKJH", {
  state: () => ({
    projectName: null,
    taskAllList: [],
    taskDetail: {},
    timeEntriesList: [],
    activityList: [],
  }),
  getters: {},
  actions: {
    // 프로젝트 이름
    async getProjectName(id) {
      this.projectName = (await axios.get("/api/tasks/projectname/" + id)).data;
    },

    // 프로젝트별 전체 업무 목록
    async getAllTask(obj) {
      console.log(obj);
      this.taskAllList = (
        await axios.get("/api/tasks", {
          params: obj,
        })
      ).data;
    },

    // 업무 상세 정보
    async getTaskById(id) {
      let result = await axios.get("/api/tasks/detail/" + id);
      this.taskDetail = result.data;
      console.log("업무상세정보: ", this.taskDetail);
    },

    // 소요시간 등록
    async registerTimeEntries(obj) {
      console.log("전송데이터: ", obj);
      let result = await axios //
        .post("/api/tasks/timelog", obj);

      this.timeEntriesList = result.data;
      console.log("소요시간목록: ", this.timeEntriesList);
    },

    // 소요시간 등록 목록 조회
    async getTimeEntries(id) {
      let result = await axios.get("/api/tasks/timelog/" + id);
      this.timeEntriesList = result.data;
    },

    // 작업내역 목록 조회
    async getActivityLogs(id) {
      let result = await axios.get("/api/tasks/activityLog/" + id);
      this.activityList = result.data;
    },
  },
});
