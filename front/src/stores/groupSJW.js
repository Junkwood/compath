import { defineStore } from "pinia";
import admin from "../utils/admin";

export const useGroupStore = defineStore("group", {
  state: () => ({
    activeGroupList: [],
    groupList: [],
    group: {},
  }),
  getters: {},
  actions: {
    async getActiveGroupList() {
      await admin.get("/group/active").then((response) => {
        this.activeGroupList = response.data;
      });
    },
    async getGroupList() {
      const response = await admin.get("/group");
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
      const response = await admin.put(`/group/${id}`, group);
      const result = response.data;
      if (result.isActive == null) {
        return false;
      } else {
        this.getGroupList();
        return true;
      }
    },
    async getGroupInfo(id) {
      const response = await admin.get("/group/" + id);
      const result = response.data;
      this.group = result;
      return this.group;
    },
  },
});
