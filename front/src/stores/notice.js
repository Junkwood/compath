import { defineStore } from "pinia";
import axios from "axios";

export const useNoticeStore = defineStore("notice", {
  state: () => ({
    registeredNotice: {},
    noticeInfo: {},
  }),
  getters: {},
  actions: {
    // 공지사항 등록
    async registerNotice(obj) {
      console.log("전송전 데이터: ", obj);

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
  },
});
