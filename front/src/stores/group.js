import { defineStore } from "pinia";
import axios from "axios";

export const useGroupStore = defineStore("group", {
  state: () => ({
    activeGroupList: [],
    groupList: [],
  }),
  getters: {},
  actions: {
    async getActiveGroupList() {
      await axios.get("/api/group/list/active").then((response) => {
        this.activeGroupList = response.data;
      });
    },
    async getGroupList() {
      const response = await axios.get("/api/group/list");
      this.groupList = response.data;
      return this.groupList;
    },
  },
});
