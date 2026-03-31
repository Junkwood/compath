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
      this.user = userData;
      localStorage.setItem("user", JSON.stringify(userData));

      if (remember) {
        localStorage.setItem("keepLogin", "true");
      } else {
        localStorage.removeItem("keepLogin");
        sessionStorage.setItem("alive", "true");
      }
    },
    logout() {
      this.user = null;
      localStorage.removeItem("user");
      localStorage.removeItem("keepLogin");
      sessionStorage.removeItem("alive");
    },
  },
});
