import { defineStore } from "pinia";
import axios from "axios";

export const useRoleStore = defineStore("role", {
  state: () => ({
    roleList: [],
  }),
  getters: {},
  actions: {
    async getRoleList() {
      const response = await axios.get("/api/role/list");
      this.roleList = response.data;
      return this.roleList;
    },
  },
});
