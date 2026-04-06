import { defineStore } from "pinia";
import axios from "axios";

export const useNoticeStore = defineStore("notice", {
  state: () => ({
    registeredNotice: {},
    noticeInfo: {},
    filterList: [],
    pagingList: [],
  }),
  getters: {},
  actions: {
    // 공지사항 등록
    async registerNotice(obj) {
      console.log("등록전 데이터: ", obj);

      await axios //
        .post("/api/notice/register", obj) //
        .then((res) => {
          this.registeredNotice = res.data;
          console.log("등록완료", this.registeredNotice);
        });
    },

    // 공지사항 단건 조회
    async getNoticeById(id) {
      await axios //
        .get("/api/notice/Detail/" + id) //
        .then((res) => {
          this.noticeInfo = res.data;
          console.log("조회완료", this.noticeInfo);
        });
    },

    // 공지사항 수정
    async modifyNotice(obj) {
      console.log("수정전 데이터: ", obj);

      await axios //
        .put("/api/notice/update", obj) //
        .then((res) => {
          this.registeredNotice = res.data;
          console.log("수정완료", this.registeredNotice);
        });
    },

    // 공지사항 목록 필터링 조건들
    async getFilterList(obj) {
      await axios //
        .get("/api/notice/listFilter", {
          params: obj,
        })
        .then((res) => {
          this.filterList = res.data;
        });
    },

    // 공지사항 목록 페이지네이션
    async getPagingList(obj) {
      await axios //
        .get("/api/notice/listPaging", {
          params: obj,
        })
        .then((res) => {
          this.pagingList = res.data;
        });
    },
  },
});
