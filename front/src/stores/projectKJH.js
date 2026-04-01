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

    // 그룹 멤버 조회
    async getAllGroupMem(id, name) {
      let result = await axios //
        .get("/api/group/members", {
          params: {
            id: id,
            name: name,
          },
        });

      this.groupMem = result.data;
    },

    // 그룹 하나 조회
    async getGroupsById(id) {
      let result = await axios //
        .get("/api/group/info/" + id);

      this.groupInfo = result.data;
    },

    // 프로젝트 구성원 등록
    async registerProjectMem(uId, pId, rId) {
      let result = await axios.post("/api/projects/registerMember", {
        userId: uId,
        projectId: pId,
        roleId: rId,
      });

      console.log("구성원 등록 ", result.data);
      this.insertedList = result.data;
    },

    // 프로젝트 구성원 조회
    async getAllMembers(id) {
      let result = await axios //
        .get("/api/projects/getMembers/" + id);

      this.memberList = result.data;
    },
  },
});
