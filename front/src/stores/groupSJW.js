import { defineStore } from "pinia";
import api from "../utils/api";

export const useGroupStore = defineStore("group", {
  state: () => ({
    activeGroupList: [],
    groupList: [],
    group: {},
  }),
  getters: {},
  actions: {
    async getActiveGroupList() {
      await api.get("/admin/group/active").then((response) => {
        this.activeGroupList = response.data;
      });
    },
    async getGroupList() {
      const response = await api.get("/admin/group");
      this.groupList = response.data;
      return this.groupList;
    },
    async changeStatus(id, isActive, editorUserId) {
      let value;
      if (isActive == "Y") {
        value = "O1";
      } else {
        value = "O2";
      }
      console.log(id, value, editorUserId);
      const group = {
        groupId: id,
        isActive: value,
        editorUserId: editorUserId,
      };
      const response = await api.put(`/admin/group/${id}`, group);
      const result = response.data;
      if (result.isActive == null) {
        return false;
      } else {
        this.getGroupList();
        return true;
      }
    },
    async getGroupInfo(id) {
      const response = await api.get("/admin/group/" + id);
      const result = response.data;
      this.group = result;
      return this.group;
    },
  },
});
