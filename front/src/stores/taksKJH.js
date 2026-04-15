import { defineStore } from "pinia";
import api from "../utils/api";

export const usetaskKJHStore = defineStore("taskKJH", {
  state: () => ({
    taskAllList: [],
    filterInfo: [],
    taskDetail: {},
    timeEntriesList: [],
    activityList: [],
    plPmList: [],
  }),
  getters: {},
  actions: {
    // 프로젝트 이름
    async getProjectName(id) {
      this.projectName = (await api.get("/tasks/projectname/" + id)).data;
    },

    // 프로젝트별 전체 업무 목록
    async getAllTask(obj) {
      console.log(obj);
      this.taskAllList = (
        await api.get("/tasks", {
          params: obj,
        })
      ).data;

      console.log("조회 성공: ", this.taskAllList);
    },

    // 필터링 조건들 호출(PL/SQL)
    async getAllFilterInfo(id) {
      this.filterInfo = (await api.get("/tasksFilters/" + id)).data;
    },

    // 업무 상세 정보
    async getTaskById(id) {
      let result = await api.get("/tasks/detail/" + id);
      this.taskDetail = result.data;
      console.log("업무상세정보: ", this.taskDetail);
    },

    // 소요시간 등록
    async registerTimeEntries(obj) {
      console.log("전송데이터: ", obj);
      let result = await api //
        .post("/tasks/timelog", obj);

      this.timeEntriesList = result.data;
      console.log("소요시간목록: ", this.timeEntriesList);
    },

    // 소요시간 등록 목록 조회
    async getTimeEntries(id) {
      let result = await api.get("/tasks/timelog/" + id);
      this.timeEntriesList = result.data;
    },

    // 작업내역 목록 조회
    async getActivityLogs(id) {
      let result = await api.get("/tasks/activityLog/" + id);
      this.activityList = result.data;
    },

    // 프로젝트 내 pm, pl 인원 조회
    async getProjectRole(obj) {
      let result = await api.get("/role/roleList", {
        params: obj,
      });
      this.plPmList = result.data;
    },
  },
});
