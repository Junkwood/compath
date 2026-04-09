import axios from "axios";
import router from "../router/router.js"; // 라우터 파일 경로에 맞게 수정
import { useAuthStore } from "../stores/auth"; // 스토어 파일 경로에 맞게 수정

// 1. 커스텀 Axios 인스턴스 생성
const api = axios.create({
  baseURL: "http://localhost:8080/api", // 백엔드 기본 URL (env 변수로 빼는 것 추천!)
  // timeout: 10000, // 10초 이상 응답 없으면 에러
  withCredentials: true, // ⭐️ 세션 쿠키를 백엔드와 주고받으려면 무조건 true!
});

// 2. 요청(Request) 인터셉터 (옵션: JWT 토큰 등을 보낼 때 사용)
api.interceptors.request.use(
  (config) => {
    // 토큰을 헤더에 집어넣기
    const token = localStorage.getItem("ACCESS_TOKEN");
    // const currentProjectId = localStorage.getItem("CURRENT_PROJECT_ID");
    // 1. JWT 토큰 세팅
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    // if (currentProjectId) {
    //   config.headers["X-Project-Id"] = currentProjectId;
    // }
    return config;
  },
  (error) => Promise.reject(error),
);

// 3. 응답(Response) 인터셉터 (⭐ 401 자동 로그아웃의 핵심!)
api.interceptors.response.use(
  (response) => {
    // 정상 응답(2xx)은 그대로 통과
    return response;
  },
  (error) => {
    // 백엔드에서 에러를 뱉었을 때
    if (error.response) {
      const status = error.response.status;

      if (status === 401) {
        // 🚨 반드시 에러가 발생한 이 '시점(함수 내부)'에서 스토어를 호출해야 합니다!
        const authStore = useAuthStore();

        alert("세션이 만료되었거나 로그인이 필요합니다.");

        authStore.logout(); // 피니아 데이터 초기화
        router.push("/login"); // 로그인 화면으로 강제 이동
      } else if (status === 403) {
        alert("접근 권한이 없습니다.");
      } else if (status === 404) {
        alert("요청하신 페이지나 데이터를 찾을 수 없습니다.");
      } else if (status === 500) {
        alert("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
      } else {
        const serverMessage = error.response.data?.message;
        alert(serverMessage || "데이터 요청 중 오류가 발생했습니다.");
      }
    } else {
      alert("서버와 통신할 수 없습니다. 네트워크 상태를 확인해주세요.");
    }

    // 컴포넌트 쪽으로 에러를 넘겨줌 (컴포넌트의 catch 블록이 실행되도록)
    return Promise.reject(error);
  },
);

// 4. 세팅이 끝난 api 객체를 내보냄
export default api;
