import { defineStore } from "pinia";
import api from "../utils/api";
export const useRoleStore = defineStore("role", {
  state: () => ({
    roleList: [],
  }),
  getters: {},
  actions: {
    async getRoleList() {
      const response = await api.get("/role/list");
      this.roleList = response.data;
      return this.roleList;
    },
  },
});
