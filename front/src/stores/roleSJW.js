import { defineStore } from "pinia";
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
      const response = await api.get("/admin/role");
      this.roleList = response.data;
      return this.roleList;
    },
    async getActiveRoleList() {
      const response = await api.get("/admin/role/active");
      this.activeRoleList = response.data;
      console.log("activeRoles");
      console.log();
      return this.roleList;
    },
    async getPermissionList() {
      const response = await api.get("/admin/permission");
      this.permissionList = response.data;
      return this.permissionList;
    },
    async changeRoleStatus(roleId, isActive) {
      console.log(roleId, isActive);
      const response = await api.put(`/admin/role/${roleId}/activation`, {
        isActive: isActive,
      });
      const result = response.data;
      return result;
    },
  },
});
