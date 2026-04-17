import { defineStore } from "pinia";
import admin from "../utils/admin";
import api from "../utils/api";
export const useRoleStore = defineStore("role", {
  state: () => ({
    roleList: [],
    activeRoleList: [],
    permissionList: [],
  }),
  getters: {},
  actions: {
    async getRoleList() {
      const response = await admin.get("/role");
      this.roleList = response.data;
      return this.roleList;
    },
    async getActiveRoleList() {
      const response = await api.get("/role/active");
      this.activeRoleList = response.data;
      return this.roleList;
    },
    async getPermissionList() {
      const response = await admin.get("/permission");
      this.permissionList = response.data;
      return this.permissionList;
    },
    async changeRoleStatus(roleId, isActive) {
      const response = await admin.put(`/role/${roleId}/activation`, {
        isActive: isActive,
      });
      const result = response.data;
      return result;
    },
  },
});
