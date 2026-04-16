import axios from "axios";
import router from "../router/router.js"; // 라우터 파일 경로에 맞게 수정
import { useAuthStore } from "../stores/auth.js";
import Swal from "sweetalert2";
// 1. 커스텀 Axios 인스턴스 생성
const admin = axios.create({
  baseURL: "http://localhost:8080/admin", // 백엔드 기본 URL (env 변수로 빼는 것 추천!)
  // timeout: 10000, // 10초 이상 응답 없으면 에러
  withCredentials: true, //  세션 쿠키를 백엔드와 주고받으려면 무조건 true
});
let isTokenRefreshing = false; // 현재 토큰 갱신 중인지 여부
let refreshSubscribers = []; // 토큰 갱신을 기다리는 대기열(Queue)
let isForbiddenAlertOpen = false;
// 대기열에 있는 요청들에게 새 토큰을 쥐여주고 실행시키는 함수
const onTokenRefreshed = (accessToken) => {
  refreshSubscribers.map((callback) => callback(accessToken));
  refreshSubscribers = []; // 다 실행했으면 대기열 비우기
};
// 대기열에 요청을 추가하는 함수
const addRefreshSubscriber = (callback) => {
  refreshSubscribers.push(callback);
};
// 2. 요청(Request) 인터셉터 (옵션: JWT 토큰 등을 보낼 때 사용)
admin.interceptors.request.use(
  (config) => {
    //헤더에 토큰 추가(세션스토리지도 체크)
    const token =
      localStorage.getItem("ACCESS_TOKEN") ||
      sessionStorage.getItem("ACCESS_TOKEN");
    config.headers.Authorization = `Bearer ${token}`;
    return config;
  },
  (error) => Promise.reject(error),
);

// 3. 응답(Response) 인터셉터
admin.interceptors.response.use(
  (response) => {
    // 정상 응답(2xx)은 그대로 통과
    return response;
  },
  async (error) => {
    if (error.isLocalCancel) {
      return Promise.reject(error);
    }
    // 백엔드에서 에러를 뱉었을 때
    if (error.response) {
      const status = error.response.status;
      const authStore = useAuthStore();
      const originalRequest = error.config;
      if (status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;

        const refreshToken =
          localStorage.getItem("REFRESH_TOKEN") ||
          sessionStorage.getItem("REFRESH_TOKEN");

        if (!refreshToken) {
          authStore.logout();
          router.push("/login");
          return Promise.reject(error);
        }

        // 누군가 이미 리프레시 중이 아니라면 백엔드 다녀오기
        if (!isTokenRefreshing) {
          isTokenRefreshing = true;

          try {
            const refreshRes = await axios.post(
              "http://localhost:8080/auth/refresh",
              {
                refreshToken: refreshToken,
              },
            );

            const newAccessToken = refreshRes.data.token;
            authStore.updateAccessToken(newAccessToken);

            // 토큰 발급 완료! 상태 변경
            isTokenRefreshing = false;

            // 대기방에 있던 다른 요청들(예: 그룹 목록)에게 새 토큰 쏴주기
            onTokenRefreshed(newAccessToken);

            // 총대 멨던 원래 요청(예: 알림 목록)도 마저 실행
            originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
            return admin(originalRequest);
          } catch (refreshError) {
            isTokenRefreshing = false;
            refreshSubscribers = []; // 대기열 폭파

            // 💡 콘솔에 진짜 에러가 뭔지 찍어봅니다! (여기서 JS 오타를 잡을 수 있습니다)
            console.error("🚨 리프레시 중 에러 발생:", refreshError);

            // 💡 프론트엔드 JS 에러가 아니라, 진짜 백엔드에서 에러(401, 400 등)를 뱉었을 때만 로그아웃!
            if (
              refreshError.response &&
              (refreshError.response.status === 401 ||
                refreshError.response.status === 400)
            ) {
              Swal.fire({
                icon: "warning",
                title: "세션 만료",
                text: "로그인 시간이 만료되었습니다. 다시 로그인해주세요.",
                allowOutsideClick: false,
              }).then(() => {
                authStore.logout();
                router.push("/login");
              });
            } else {
              // 네트워크가 끊겼거나 프론트엔드 코드(오타 등) 에러인 경우 조용히 에러만 넘김
              return Promise.reject(refreshError);
            }
          }
        }
        // 💡 누군가 이미 리프레시 중이라면? -> 대기방(Queue)으로 들어가서 기다리기!
        else {
          return new Promise((resolve) => {
            addRefreshSubscriber((token) => {
              originalRequest.headers.Authorization = `Bearer ${token}`;
              resolve(admin(originalRequest));
            });
          });
        }
      } else if (status === 403) {
        if (!isForbiddenAlertOpen) {
          isForbiddenAlertOpen = true; // 자물쇠 잠금
          Swal.fire({
            icon: "error",
            title: "접근 거부",
            text: "접근 권한이 없습니다.",
            confirmButtonText: "확인",
            allowOutsideClick: false,
          }).then(() => {
            isForbiddenAlertOpen = false;
            router.back(); // 확인 버튼을 누른 뒤 뒤로가기
          });
        }
      } else if (status === 404) {
        Swal.fire({
          icon: "question",
          title: "페이지 없음",
          text: "요청하신 페이지나 데이터를 찾을 수 없습니다.",
          confirmButtonText: "확인",
        }).then(() => router.back());
      } else if (status === 500) {
        Swal.fire({
          icon: "error",
          title: "서버 오류",
          text: "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
          confirmButtonText: "확인",
        }).then(() => router.back());
      } else {
        const serverMessage = error.response.data?.message;
        Swal.fire({
          icon: "error",
          title: "요청 오류",
          text: serverMessage || "데이터 요청 중 오류가 발생했습니다.",
          confirmButtonText: "확인",
        }).then(() => router.back());
      }
    } else {
      // 서버가 죽었거나 인터넷이 끊겼을 때
      Swal.fire({
        icon: "error",
        title: "네트워크 오류",
        text: "서버와 통신할 수 없습니다. 네트워크 상태를 확인해주세요.",
        confirmButtonText: "확인",
      }).then(() => router.push("/"));
    }

    // 컴포넌트 쪽으로 에러를 넘겨줌 (컴포넌트의 catch 블록이 실행되도록)
    return Promise.reject(error);
  },
);

// 4. 세팅이 끝난 api 객체를 내보냄
export default admin;
