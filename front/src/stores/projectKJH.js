import { defineStore } from "pinia";
import axios from "axios";

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
  }),
  getters: {},
  actions: {
    // 프로젝트 구성원
    async getProjectInfo(id) {
      this.projectInfo = (await axios.get("/api/ProjectInfo/" + id)).data;
    },

    // 프로젝트 수정
    async modifyProject(payload) {
      console.log(payload);
      try {
        let id = payload.projectId;

        console.log("수정 프로젝트 번호", id);

        let result = await axios //
          .put("/api/projects/info/" + id, payload);

        if (result.status == 200) {
          this.modifiedInfo = result.data;
        }
      } catch (err) {
        console.error("프로젝트 등록 실패:", err);
      }
    },

    // 멤버 정보
    async getUsersById(id) {
      let result = await axios //
        .get("/api/emp/users/" + id);
      this.userInfo = result.data;
    },

    // 그룹 전체 정보
    async getAllGroups() {
      let result = await axios //
        .get("/api/group/list");

      this.groupList = result.data;
    },

    // 그룹 하나 조회
    async getGroupsById(id) {
      let result = await axios //
        .get("/api/group/info/" + id);

      this.groupInfo = result.data;
    },

    // 프로젝트 구성원 등록
    async registerProjectMem(arr) {
      console.log(arr);
      let result = await axios.post("/api/projects/registerMember", arr);

      console.log("구성원 등록 ", result.data);
      this.insertedList = result.data;
    },

    // 프로젝트 구성원 조회
    async getAllMembers(id) {
      let result = await axios //
        .get("/api/projects/getMembers/" + id);

      this.memberList = result.data;
    },

    // 역할 전체 조회
    async getAllRoles() {
      let result = await axios //
        .get("/api/role/list");
      this.roleList = result.data;
    },

    // 구성원 삭제
    async removeMem(obj) {
      console.log("구성원 삭제전", obj);
      let result = await axios //
        .delete("/api/projects/delMembers", {
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
      let result = await axios.get("/api/projectgroup/members");
      console.log(result);
      this.projectGroupMem = result.data;
    },
  },
});
