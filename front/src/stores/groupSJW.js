import { defineStore } from "pinia";
import axios from "axios";

export const useGroupStore = defineStore("group", {
  state: () => ({
    activeGroupList: [],
    groupList: [],
    group: {},
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
    async changeStatus(id) {
      const response = await axios.get("/api/group/deacitvate/" + id);
      this.getGroupList();
      return response.data;
    },
    async getGroupInfo(id) {
      const response = await axios.get("/api/group/info/" + id);
      const result = response.data;
      this.group = result;
      return this.group;
    },
  },
});
