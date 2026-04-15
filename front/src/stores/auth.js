import jwtDecode from "jwt-decode";
import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    user: (() => {
      try {
        const saved = localStorage.getItem("user");
        return saved && saved !== "undefined" ? JSON.parse(saved) : null;
      } catch {
        localStorage.removeItem("user");
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
      const refreshToken = userData.refreshToken;
      if (remember) {
        // 로그인 유지 체크 시: localStorage 사용
        localStorage.setItem("keepLogin", "true");
        localStorage.setItem("ACCESS_TOKEN", token);
        if (refreshToken) localStorage.setItem("REFRESH_TOKEN", refreshToken);
      } else {
        // 로그인 유지 미체크 시: sessionStorage 사용
        sessionStorage.setItem("ACCESS_TOKEN", token);
        if (refreshToken) sessionStorage.setItem("REFRESH_TOKEN", refreshToken);
      }
      const decoded = jwtDecode(token);
      this.user = userData;
      this.user.userType = decoded.userType;
      this.user.name = decoded.userName;
      console.log("디코딩된 토큰 정보:", decoded);
      localStorage.setItem("user", JSON.stringify(this.user));
    },
    logout() {
      this.user = null;
      console.log("authStore.logout");
      localStorage.removeItem("user");
      localStorage.removeItem("keepLogin");

      localStorage.removeItem("ACCESS_TOKEN");
      localStorage.removeItem("REFRESH_TOKEN");

      sessionStorage.removeItem("ACCESS_TOKEN");
      sessionStorage.removeItem("REFRESH_TOKEN");
    },
    updateAccessToken(newToken) {
      const isKeepLogin = localStorage.getItem("keepLogin") === "true";

      if (isKeepLogin) {
        localStorage.setItem("ACCESS_TOKEN", newToken);
      } else {
        sessionStorage.setItem("ACCESS_TOKEN", newToken);
      }
      console.log("Token 갱신됨.");
    },
  },
});
