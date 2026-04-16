import { defineStore } from "pinia";
import api from "../utils/api";
export const useDocumentStore = defineStore("document", {
  state: () => ({
    registeredDocument: {}, // 문서 등록
    documentDetail: {}, // 문서 단건 조회
    filterList: [], // 필터링 조건들
    pagingList: [], // 페이징된 데이터
    registeredComment: [], // 댓글 목록
  }),
  getters: {},
  actions: {
    // 문서 등록
    async registerDocument(formData) {
      console.log("등록전 데이터: ", formData);
      await api //
        .post("/documents/register", formData, {
          headers: {},
        })
        .then((res) => {
          this.registeredDocument = res.data;
          console.log("문서 등록완료: ", this.registeredDocument);
        });
    },

    // 문서 단건 조회(상세페이지)
    async getDocumentById(id) {
      await api //
        .get("/documents/Detail/" + id)
        .then((res) => {
          this.documentDetail = res.data;
          console.log("문서 단건 조회 완료: ", this.documentDetail);
        });
    },

    // 문서 수정
    async modifyDocument(obj) {
      console.log("수정전 데이터: ", obj);

      await api //
        .put("/docuemnts/update", obj)
        .then((res) => {
          this.registeredDocument = res.data;
          console.log("수정완료", this.registeredDocument);
        });
    },

    // 문서 목록 필터링 조건들
    async getFilterList(obj) {
      await api //
        .get("/docuemnts/listFilter", {
          params: obj,
        })
        .then((res) => {
          this.filterList = res.data;
        });
    },

    // 문서 목록 페이지네이션
    async getPagingList(obj) {
      await api //
        .get("/docuemnts/listPaging", {
          params: obj,
        })
        .then((res) => {
          this.pagingList = res.data;
        });
    },

    // 문서 댓글 등록
    async registerComment(obj) {
      console.log("등록전 데이터: ", obj);
      await api //
        .post("/documents/comments/register", obj)
        .then((res) => {
          this.registeredComment = res.data;
          console.log("댓글 등록완료: ", this.registeredComment);
        });
    },

    // 문서 댓글 수정
    async modifyComment(obj) {
      console.log("수정전 데이터: ", obj);

      await api //
        .put("/docuemnts/comments/update", obj)
        .then((res) => {
          this.registeredComment = res.data;
          console.log("수정완료", this.registeredComment);
        });
    },

    // 알림 등록
    async registerDocumentAlarm(arr) {
      console.log("생성값", arr);
      await api //
        .post("/docuemnts/alarm/register", arr)
        .then((res) => {
          console.log("알림발송 및 등록 완료", res.data);
        });
    },
  },
});
