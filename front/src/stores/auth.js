import jwtDecode from "jwt-decode";
import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    user: (() => {
      try {
        const saved = localStorage.getItem("user");
        return saved && saved !== "undefined" ? JSON.parse(saved) : null;
      } catch {
        localStorage.removeItem("user"); // 깨진 데이터 자동 정리
        return null;
      }
    })(),
  }),
  getters: {
    isLoggedIn: (state) => !!state.user,
    isAdmin: (state) => state.user?.userType === "ADMIN",
    userName: (state) => state.user?.name || "",
  },
  actions: {
    login(userData, remember = false) {
      const token = userData.token;
      if (remember) {
        // 로그인 유지 체크 시: localStorage 사용
        localStorage.setItem("keepLogin", "true");
        localStorage.setItem("ACCESS_TOKEN", token);
      } else {
        // 로그인 유지 미체크 시: sessionStorage 사용
        sessionStorage.setItem("ACCESS_TOKEN", token);
      }
      const decoded = jwtDecode(token);
      this.user = userData;
      this.user.userType = decoded.userType;
      this.user.name = decoded.userName;
      console.log(decoded);
      localStorage.setItem("user", JSON.stringify(userData));
    },
    logout() {
      this.user = null;
      console.log("authStore.logout");
      localStorage.removeItem("user");
      localStorage.removeItem("keepLogin");
      localStorage.removeItem("ACCESS_TOKEN");
    },
  },
});
