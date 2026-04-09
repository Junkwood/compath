import { defineStore } from "pinia";
import axios from "axios";

export const useMeetingStore = defineStore("meeting", {
  state: () => ({
    meetingType: [],
  }),
  getters: {},
  actions: {
    // 회의록 유형
    async getMeetingType() {
      await axios //
        .get("/api/meeting/register/typeList")
        .then((res) => {
          this.meetingType = res.data;
        });
    },
  },
});
