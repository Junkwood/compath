import { defineStore } from "pinia";
import api from "../utils/api";

export const useMeetingStore = defineStore("meeting", {
  state: () => ({
    meetingType: [],
    registeredMeeting: {},
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
    async registerMeeting(obj) {
      console.log("회의록 등록전", obj);
      await api //
        .post("/meeting/register", obj)
        .then((res) => {
          this.registeredMeeting = res.data;
          console.log("회의록 조회완료", this.registeredMeeting);
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
  },
});
