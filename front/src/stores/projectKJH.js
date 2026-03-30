import { defineStore } from "pinia";
import axios from "axios";

export const useProjectKJHStore = defineStore("projectKJH", {
  state: () => ({
    projectInfo: {},
    modifiedInfo: {},
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
  },
});
