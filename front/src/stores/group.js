import { defineStore } from "pinia";
import axios from "axios";

export const useGroupStore = defineStore("group", {
  state: () => ({
    activeGroupList: [],
  }),
  getters: {},
  actions: {
    async getActiveGroupList() {
      await axios.get("/api/group/list/active").then((response) => {
        this.activeGroupList = response.data;
      });
    },
  },
});
