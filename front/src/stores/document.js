import { defineStore } from "pinia";
import axios from "axios";

export const useDocumentStore = defineStore("document", {
  state: () => ({
    registeredDocument: {},
    documentDetail: {},
    filterList: [],
    pagingList: [],
  }),
  getters: {},
  actions: {
    // 문서 등록
    async registerDocument(obj) {
      console.log("등록전 데이터: ", obj);
      await axios //
        .post("/api/documents/register", obj)
        .then((res) => {
          this.registeredDocument = res.data;
          console.log("문서 등록완료: ", this.registeredDocument);
        });
    },

    // 문서 단건 조회(상세페이지)
    async getDocumentById(id) {
      await axios //
        .get("/api/documents/Detail/" + id)
        .then((res) => {
          this.documentDetail = res.data;
          console.log("문서 단건 조회 완료: ", this.documentDetail);
        });
    },

    // 문서 수정
    async modifyDocument(obj) {
      console.log("수정전 데이터: ", obj);

      await axios //
        .put("/api/docuemnts/update", obj)
        .then((res) => {
          this.registeredDocument = res.data;
          console.log("수정완료", this.registeredDocument);
        });
    },

    // 문서 목록 필터링 조건들
    async getFilterList(obj) {
      await axios //
        .get("/api/docuemnts/listFilter", {
          params: obj,
        })
        .then((res) => {
          this.filterList = res.data;
        });
    },

    // 문서 목록 페이지네이션
    async getPagingList(obj) {
      await axios //
        .get("/api/docuemnts/listPaging", {
          params: obj,
        })
        .then((res) => {
          this.pagingList = res.data;
        });
    },
  },
});
