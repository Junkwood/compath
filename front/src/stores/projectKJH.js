import { defineStore } from "pinia";
import api from "../utils/api";

export const useProjectKJHStore = defineStore("projectKJH", {
  state: () => ({
    projectInfo: {},
    modifiedInfo: {},
    userInfo: {},
    groupList: [],
    groupMem: [],
    groupInfo: [],
    memberList: [],
    insertedList: [],
    roleList: [],
    remainMem: [],
    projectGroupMem: [],
    generalGroupMem: [],
  }),
  getters: {},
  actions: {
    // 프로젝트 구성원
    async getProjectInfo(id) {
      this.projectInfo = (await api.get("/ProjectInfo/" + id)).data;
    },

    // 프로젝트 수정
    async modifyProject(payload) {
      try {
        let id = payload.projectId;

        let result = await api //
          .put("/projects/info/" + id, payload);

        if (result.status == 200) {
          this.modifiedInfo = result.data;
        }
      } catch (err) {
        console.error("프로젝트 등록 실패:", err);
      }
    },

    // 멤버 정보
    async getUsersById(id) {
      let result = await api //
        .get("/emp/users/" + id);
      this.userInfo = result.data;
    },

    // 그룹 전체 정보
    async getAllGroups() {
      let result = await api //
        .get("/group/list");

      this.groupList = result.data;
    },

    // 그룹 하나 조회
    async getGroupsById(id) {
      let result = await api //
        .get("/group/info/" + id);

      this.groupInfo = result.data;
    },

    // 프로젝트 구성원 등록
    async registerProjectMem(arr) {
      let result = await api.post("/projects/registerMember", arr);

      this.insertedList = result.data;
    },

    // 프로젝트 구성원 조회
    async getAllMembers(id) {
      let result = await api //
        .get("/projects/getMembers/" + id);

      this.memberList = result.data;
    },

    // 역할 전체 조회
    async getAllRoles() {
      let result = await api //
        .get("/role/list");
      this.roleList = result.data;
    },

    // 구성원 삭제
    async removeMem(obj) {
      let result = await api //
        .delete("/projects/delMembers", {
          data: {
            projectId: obj.projectId,
            projectMemberId: obj.projectMemberId,
            projectMemberRoleId: obj.projectMemberRoleId,
          },
        });
      this.remainMem = result.data;
    },

    // 프로젝트 그룹 및 멤버 조회
    async getProjectGroupMem() {
      let result = await api.get("/projectgroup/members");

      this.projectGroupMem = result.data;
    },

    // 직군 그룹 및 멤버 조회
    async getGeneralGroupMem() {
      let result = await api.get("/group/members");
      this.generalGroupMem = result.data;
    },
  },
});
