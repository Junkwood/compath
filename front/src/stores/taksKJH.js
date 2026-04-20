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
    modifyResult: 0,
    projectName: [],
    taskLog: [],
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

    // 업무 비활성화
    async modifyTaskStatus(id) {
      await api //
        .put("/tasks/modifyStatus/" + id)
        .then((res) => {
          this.modifyResult = res.data;
        });
    },

    // 업무 담당자만 지정
    async modifyTaskUser(obj) {
      await api //
        .put("/tasks/modifyUser", obj)
        .then((res) => {
          this.modifyTaskInfo = res.data;
        });
    },

    // 업무 작업 이력
    async getActivityLogsByTaskId(id) {
      await api //
        .get("/tasks/activityLogs/" + id)
        .then((res) => {
          let result = res.data;
          console.log(result);
          result.forEach((task) => {
            if (task.actionType == "T0") {
              task.message = `${task.userName}님이 "${task.afterValue}"를 생성하셨습니다.`;
            } else if (task.actionType == "T4") {
              task.message = `${task.userName}님이 진척도를 ${task.beforeValue}%에서 ${task.afterValue}%로 변경하셨습니다.`;
            } else if (task.actionType == "T2" && task.beforeValue == "Q1") {
              task.message = `${task.userName}님이 업무의 삭제를 해제하셨습니다.`;
            } else if (task.actionType == "T2" && task.beforeValue == "Q2") {
              task.message = `${task.userName}님이 업무를 삭제하셨습니다.`;
            } else if (task.actionType == "T1") {
              const PRIORITY_LABEL_MAP = {
                H1: "긴급",
                H2: "상",
                H3: "중",
                H4: "하",
              };
              task.beforeValue =
                PRIORITY_LABEL_MAP[task.beforeValue] || task.beforeValue;

              task.afterValue =
                PRIORITY_LABEL_MAP[task.afterValue] || task.afterValue;
              task.message = `${task.userName}님이 업무정보를 "${task.beforeValue}"에서 "${task.afterValue}"로 변경하셨습니다.`;
            } else if (task.actionType == "T5") {
              task.message = `${task.userName}님이 소요시간을 ${task.beforeValue}시간에서 ${task.afterValue}시간으로 변경하셨습니다.`;
            } else if (task.actionType == "T6") {
              const PRIORITY_LABEL_MAP = {
                H1: "긴급",
                H2: "상",
                H3: "중",
                H4: "하",
              };
              task.beforeValue =
                PRIORITY_LABEL_MAP[task.beforeValue] || task.beforeValue;

              task.afterValue =
                PRIORITY_LABEL_MAP[task.afterValue] || task.afterValue;

              task.message = `${task.userName}님이 우선순위을 "${task.beforeValue}"에서 "${task.afterValue}"로 변경하셨습니다.`;
            } else if (task.actionType == "T3") {
              const TASK_STATUS_LABEL_MAP = {
                10: "시작 전",
                11: "진행중",
                12: "개발완료",
                13: "반려",
                14: "완료",
              };

              task.beforeValue =
                TASK_STATUS_LABEL_MAP[task.beforeValue] || task.beforeValue;

              task.afterValue =
                TASK_STATUS_LABEL_MAP[task.afterValue] || task.afterValue;
              task.message = `${task.userName}님이 업무상태를 "${task.beforeValue}"에서 "${task.afterValue}"(으)로 변경하셨습니다.`;
            }
          });
          this.taskLog = result;
        });
    },
  },
});
