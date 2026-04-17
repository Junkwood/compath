import axios from "axios";
import router from "../router/router.js"; // 라우터 파일 경로에 맞게 수정
import { useAuthStore } from "../stores/auth"; // 스토어 파일 경로에 맞게 수정
import Swal from "sweetalert2";

// 1. 커스텀 Axios 인스턴스 생성
const api = axios.create({
  baseURL: "http://localhost:8080/api", // 백엔드 기본 URL
  withCredentials: true, // 세션 쿠키를 백엔드와 주고받으려면 무조건 true
});

let isTokenRefreshing = false; // 현재 토큰 갱신 중인지 여부
let refreshSubscribers = []; // 토큰 갱신을 기다리는 대기열(Queue)

// 💡 알림창 중복 방지를 위한 자물쇠 변수들
let isForbiddenAlertOpen = false; // 403 에러용 자물쇠
let isErrorAlertOpen = false; // 404, 500, 기타 에러용 공통 자물쇠

// 대기열에 있는 요청들에게 새 토큰을 쥐여주고 실행시키는 함수
const onTokenRefreshed = (accessToken) => {
  refreshSubscribers.map((callback) => callback(accessToken));
  refreshSubscribers = []; // 다 실행했으면 대기열 비우기
};

// 대기열에 요청을 추가하는 함수
const addRefreshSubscriber = (callback) => {
  refreshSubscribers.push(callback);
};

// 2. 요청(Request) 인터셉터
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore();
    const token =
      localStorage.getItem("ACCESS_TOKEN") ||
      sessionStorage.getItem("ACCESS_TOKEN");
    const currentProjectId = sessionStorage.getItem("CURRENT_PROJECT_ID");

    // 1. JWT 토큰 세팅
    if (!token) {
      authStore.logout();
      router.push("/login");
      const cancelError = new Error("토큰이 없어 요청을 취소합니다.");
      cancelError.isLocalCancel = true;
      return Promise.reject(cancelError);
    }

    config.headers.Authorization = `Bearer ${token}`;

    if (currentProjectId) {
      config.headers["X-Project-Id"] = currentProjectId;
    }

    if (authStore.user && authStore.user.primaryGroupId) {
      config.headers["X-Primary-Group-Id"] = authStore.user.primaryGroupId;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// 3. 응답(Response) 인터셉터
api.interceptors.response.use(
  (response) => {
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

            isTokenRefreshing = false;
            onTokenRefreshed(newAccessToken);

            originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
            return api(originalRequest);
          } catch (refreshError) {
            isTokenRefreshing = false;
            refreshSubscribers = [];

            console.error("🚨 리프레시 중 에러 발생:", refreshError);

            if (
              refreshError.response &&
              (refreshError.response.status === 401 ||
                refreshError.response.status === 400)
            ) {
              // 💡 await를 써서 알림창이 닫힐 때까지 코드 멈춤!
              await Swal.fire({
                icon: "warning",
                title: "세션 만료",
                text: "로그인 시간이 만료되었습니다. 다시 로그인해주세요.",
                allowOutsideClick: false,
              });
              authStore.logout();
              router.push("/login");
              return Promise.reject(refreshError);
            } else {
              return Promise.reject(refreshError);
            }
          }
        }
        // 누군가 이미 리프레시 중이라면 대기방으로
        else {
          return new Promise((resolve) => {
            addRefreshSubscriber((token) => {
              originalRequest.headers.Authorization = `Bearer ${token}`;
              resolve(api(originalRequest));
            });
          });
        }
      }
      // 💡 403 권한 없음
      else if (status === 403) {
        if (!isForbiddenAlertOpen) {
          isForbiddenAlertOpen = true;
          await Swal.fire({
            icon: "error",
            title: "접근 거부",
            text: "접근 권한이 없습니다.",
            confirmButtonText: "확인",
            allowOutsideClick: false,
          });
          isForbiddenAlertOpen = false; // 자물쇠 풀기
          if (router.currentRoute.value.path !== "/") {
            router.back();
          }
        }
      }
      // 💡 404 페이지 없음
      else if (status === 404) {
        if (!isErrorAlertOpen) {
          isErrorAlertOpen = true;
          await Swal.fire({
            icon: "question",
            title: "페이지 없음",
            text: "요청하신 페이지나 데이터를 찾을 수 없습니다.",
            confirmButtonText: "확인",
          });
          isErrorAlertOpen = false;
          if (router.currentRoute.value.path !== "/") {
            router.back();
          }
        }
      }
      // 💡 500 서버 오류
      else if (status === 500) {
        if (!isErrorAlertOpen) {
          isErrorAlertOpen = true;
          await Swal.fire({
            icon: "error",
            title: "서버 오류",
            text: "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
            confirmButtonText: "확인",
          });
          isErrorAlertOpen = false;
          if (router.currentRoute.value.path !== "/") {
            router.back();
          }
        }
      }
      // 💡 기타 요류
      else {
        if (!isErrorAlertOpen) {
          isErrorAlertOpen = true;
          const serverMessage = error.response.data?.message;
          await Swal.fire({
            icon: "error",
            title: "요청 오류",
            text: serverMessage || "데이터 요청 중 오류가 발생했습니다.",
            confirmButtonText: "확인",
          });
          isErrorAlertOpen = false;
          if (router.currentRoute.value.path !== "/") {
            router.back();
          }
        }
      }
    }
    // 백엔드에서 응답조차 없을 때 (네트워크 오류 등)
    else {
      if (!isErrorAlertOpen) {
        isErrorAlertOpen = true;
        await Swal.fire({
          icon: "error",
          title: "네트워크 오류",
          text: "서버와 통신할 수 없습니다. 네트워크 상태를 확인해주세요.",
          confirmButtonText: "확인",
        });
        isErrorAlertOpen = false;
        router.push("/");
      }
    }

    // 💡 모든 Swal 대기가 끝난 후, 비로소 컴포넌트 쪽으로 에러를 넘겨줌!
    return Promise.reject(error);
  },
);

export default api;
