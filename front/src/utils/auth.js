import axios from "axios";
import router from "../router/router.js"; // 라우터 파일 경로에 맞게 수정

// 1. 커스텀 Axios 인스턴스 생성
const auth = axios.create({
  baseURL: "http://13.209.66.174:8080/auth", // 백엔드 기본 URL (env 변수로 빼는 것 추천!)
  // timeout: 10000, // 10초 이상 응답 없으면 에러
  withCredentials: true, // 세션 쿠키를 백엔드와 주고받으려면 무조건 true
});

// 2. 요청(Request) 인터셉터 (옵션: JWT 토큰 등을 보낼 때 사용)
auth.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => Promise.reject(error),
);

// 3. 응답(Response) 인터셉터
auth.interceptors.response.use(
  (response) => {
    // 정상 응답(2xx)은 그대로 통과
    return response;
  },
  (error) => {
    // 백엔드에서 에러를 뱉었을 때
    if (error.response) {
      const status = error.response.status;

      if (status === 401) {
        // 반드시 에러가 발생한 이 '시점(함수 내부)'에서 스토어를 호출해야 합니다.
        // 비동기 처리: 확인 버튼을 누른 뒤에 로그아웃 및 라우팅.
        Swal.fire({
          icon: "warning",
          title: "세션 만료",
          text: "세션이 만료되었거나 로그인이 필요합니다.",
          confirmButtonText: "확인",
        }).then(() => {
          authStore.logout();
          router.push("/login");
        });
      } else if (status === 403) {
        Swal.fire({
          icon: "error",
          title: "접근 거부",
          text: "접근 권한이 없습니다.",
          confirmButtonText: "확인",
        }).then(() => {
          router.back(); // 확인 버튼을 누른 뒤 뒤로가기
        });
      } else if (status === 404) {
        Swal.fire({
          icon: "question",
          title: "페이지 없음",
          text: "요청하신 페이지나 데이터를 찾을 수 없습니다.",
          confirmButtonText: "확인",
        });
      } else if (status === 500) {
        Swal.fire({
          icon: "error",
          title: "서버 오류",
          text: "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
          confirmButtonText: "확인",
        });
      } else {
        const serverMessage = error.response.data?.message;
        Swal.fire({
          icon: "error",
          title: "요청 오류",
          text: serverMessage || "데이터 요청 중 오류가 발생했습니다.",
          confirmButtonText: "확인",
        });
      }
    } else {
      // 서버가 죽었거나 인터넷이 끊겼을 때
      Swal.fire({
        icon: "error",
        title: "네트워크 오류",
        text: "서버와 통신할 수 없습니다. 네트워크 상태를 확인해주세요.",
        confirmButtonText: "확인",
      });
    }

    // 컴포넌트 쪽으로 에러를 넘겨줌 (컴포넌트의 catch 블록이 실행되도록)
    return Promise.reject(error);
  },
);

// 4. 세팅이 끝난 api 객체를 내보냄
export default auth;
