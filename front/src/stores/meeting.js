import { defineStore } from "pinia";
import api from "../utils/api";

export const useMeetingStore = defineStore("meeting", {
  state: () => ({
    meetingType: [],
    registeredMeeting: {},
    meetingDetail: {},
    filterList: [],
    pagingList: [],
    geminiContent: [],
    connectTaskList: [],
    detailConnectList: [],
  }),
  getters: {},
  actions: {
    // 회의록 유형 조회
    async getMeetingType() {
      await api //
        .get("/meeting/register/typeList")
        .then((res) => {
          this.meetingType = res.data;
        });
    },

    // 회의록 등록
    async registerMeeting(formData) {
      await api //
        .post("/meeting/register", formData, {
          headers: {},
        })
        .then((res) => {
          this.registeredMeeting = res.data;
          console.log("회의록 조회완료", this.registeredMeeting);
        });
    },

    // 회의록 수정
    async modifyMeeting(formData) {
      await api //
        .put("/meeting/modify", formData, {
          headers: {},
        })
        .then((res) => {
          this.meetingDetail = res.data;
        });
    },

    // 알림 등록
    async registerMeetingAlarm(arr) {
      console.log("생성값", arr);
      await api //
        .post("/meeting/alarm/register", arr)
        .then((res) => {
          console.log("알림발송 및 등록 완료", res.data);
        });
    },

    // 회의록 단건조회(상세페이지)
    async getMeetingById(id) {
      await api //
        .get("/meeting/detail/" + id)
        .then((res) => {
          this.meetingDetail = res.data;
        });
    },

    // 필터링 조건
    async getFilterList(obj) {
      await api //
        .get("/meeting/list", {
          params: obj,
        })
        .then((res) => {
          this.filterList = res.data;
        });
    },

    // 페이징목록 조회
    async getPagingList(obj) {
      console.log(obj);
      await api //
        .get("/meeting/paging", {
          params: obj,
        })
        .then((res) => {
          this.pagingList = res.data;
        });
    },

    //  Gemini API 사용해서 작성내용 요약
    async getContentByGemmini(obj) {
      await api //
        .post("/gemini/simple", obj, {
          headers: {},
        })
        .then((res) => {
          this.geminiContent = res.data;
          console.log(this.geminiContent);
        });
    },

    // 연결업무 연결 해제（등록）
    async removeConnectTask(obj) {
      await api //
        .delete("/meeting/removeConnect", {
          data: obj,
        })
        .then((res) => {
          this.connectTaskList = res.data;
        });
    },

    // 회의록상세 업무 연결 등록
    async registerDetailConnect(obj) {
      await api //
        .post("/tasks/registerDetailConnect", obj)
        .then((res) => {
          this.detailConnectList = res.data;
        });
    },

    // 연결업무 연결 해제（상세）
    async removeConnectTask(obj) {
      await api //
        .delete("/meeting/removeDetailConnect", {
          data: obj,
        })
        .then((res) => {
          this.connectTaskList = res.data;
        });
    },
  },
});
