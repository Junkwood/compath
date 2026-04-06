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
    async getPermissionList() {
      return await api.get("/permission/list");
    },
    async changeRoleStatus(roleId, isActive) {
      console.log(roleId, isActive);
      const response = await api.put("/role/activation", {
        roleId: roleId,
        isActive: isActive,
      });
      const result = response.data;
      return result;
    },
  },
});
