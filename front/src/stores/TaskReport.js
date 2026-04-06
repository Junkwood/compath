import { defineStore } from "pinia";
import api from "../utils/api";

export const useTaskReport = defineStore("TaskReport", {
  state: () => ({
    initTitleList: [], 
    allData: [],
    filterList: [],
    listLength: 0,
    workPageSize: 0,
    titleList: [],
    assigneeList: [],
    taskTypeList: [],
  }),

  actions: {
    slicePage(page, listNum) {
      const start = (page - 1) * listNum;
      const end = start + listNum;
      this.filterList = this.allData.slice(start, end);
    },

    async fetchReportList(params, page = 1, listNum = 10,isInit = false) {
      try {
        const response = await api.get("/taskReport", { params });
        const data = response.data;

        this.allData = data;
        this.listLength = data.length;
        this.workPageSize = data.length;
        this.slicePage(page, listNum);

         if (isInit) {
        this.titleList = [...new Set(data.map((t) => t.title).filter(Boolean))];

        this.assigneeList = [
          ...new Map(
            data.map((t) => [
              t.assigneeUserId,
              { assigneeUserId: t.assigneeUserId, userName: t.userName },
            ])
          ).values(),
        ];
      
        this.taskTypeList = [
          ...new Map(
            data
              .filter((t) => t.typeName != null)
              .map((t) => [t.typeName, { typeName: t.typeName }])
          ).values(),
        ];
      }
        return data;
      } catch (error) {
        console.error("보고서 로딩 실패:", error);
      }
    },
  },
});