import { defineStore } from "pinia";
import axios from "axios";

export const useProjectKJHStore = defineStore("projectKJH", {
  state: () => ({
    projectInfo: {},
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
        let result = await axios //
          .put("/api/projects/info/" + payload.projectId, payload);

        console.log(result);
      } catch (err) {
        console.error("프로젝트 등록 실패:", err);
      }
    },
  },
});
