import { defineStore } from "pinia";
import axios from "axios";

export const useProjectKJHStore = defineStore("projectKJH", {
  state: () => ({
    projectInfo: {},
    modifiedInfo: {},
    userInfo: {},
    groupList: [],
    groupMem: [],
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
        .get("/api/emp/info/" + id);

      this.userInfo = result.data;
    },

    // 그룹 정보
    async getAllGroups() {
      let result = await axios //
        .get("/api/group/list");

      this.groupList = result.data;
    },

    // 그룹 멤버 조회
    async getAllGroupMem(id) {
      let result = await axios //
        .get("/api/group/members/" + id);

      this.groupMem = result.data;
    },
  },
});
