import { defineStore } from "pinia";
import api from "../utils/api";

export const useNoticeStore = defineStore("notice", {
  state: () => ({
    registeredNotice: {},
    noticeInfo: {},
    filterList: [],
    pagingList: [],
    taskType: [],
  }),
  getters: {},
  actions: {
    // 공지사항 등록
    async registerNotice(formData) {
      await api //
        .post("/notice/register", formData, {
          headers: {},
        }) //
        .then((res) => {
          this.registeredNotice = res.data;
        });
    },

    // 공지사항 단건 조회
    async getNoticeById(id) {
      await api //
        .get("/notice/Detail/" + id) //
        .then((res) => {
          this.noticeInfo = res.data;
        });
    },

    // 공지사항 수정
    async modifyNotice(formData) {
      await api //
        .put("/notice/update", formData, {
          headers: {},
        }) //
        .then((res) => {
          this.registeredNotice = res.data;
        });
    },

    // 공지사항 목록 필터링 조건들
    async getFilterList(obj) {
      await api //
        .get("/notice/listFilter", {
          params: obj,
        })
        .then((res) => {
          this.filterList = res.data;
        });
    },

    // 공지사항 목록 페이지네이션
    async getPagingList(obj) {
      await api //
        .get("/notice/listPaging", {
          params: obj,
        })
        .then((res) => {
          this.pagingList = res.data;
        });
    },

    // 공지사항 활성화/ 비활성화
    async modifyNoticeLock(id, lock) {
      await api //
        .put("/notice/updateLock/" + id + "/" + lock) //
        .then((res) => {
          this.registeredNotice = res.data;
        });
    },

    // 공지사항 수정/생성시 프로젝트 구서원에 있는 역할
    async getProjectType() {
      await api //
        .get("/tasks/typeList") //
        .then((res) => {
          this.taskType = res.data;
        });
    },
  },
});
