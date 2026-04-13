import { defineStore } from "pinia";
import api from "../utils/api";

export const useAttachmentStore = defineStore("attachment", {
  state: () => ({}),

  actions: {
    // 파일 다운로드
    async downloadFile(file) {
      await api
        .get("attachment/download/resp", {
          params: {
            fullPath: file.filePath,
          },
          responseType: "blob", // 반드시 blob으로 받아야 데이터가 안 깨집니다!
        })
        .then((res) => {
          const contentType = res.data.type;
          console.log("파일 타입:", contentType);
          console.log(res.data);

          // 2. Blob 객체 생성 시 타입을 명시해주면 더 정확합니다.
          const blob = new Blob([res.data], { type: contentType });
          const newUrl = window.URL.createObjectURL(blob);

          // 4. 공통 다운로드 로직
          const a = document.createElement("a");
          a.href = newUrl;

          // 파일명도 서버에서 넘겨준 이름이 있다면 그걸 쓰는게 좋습니다.
          a.download = file.fileName;

          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          window.URL.revokeObjectURL(newUrl);
        });
    },
  },
});
