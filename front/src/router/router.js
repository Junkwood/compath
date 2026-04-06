import { createRouter, createWebHistory } from "vue-router";
import bulletinRoutes from "./bulletin.js";
import adminRoutes from "./admin.js";
import meetingRoutes from "./meeting.js";
import projectRoutes from "./project.js";
import reportRoutes from "./report.js";
import taskRoutes1 from "./taskJJW.js";
import taskRoutes from "./taskKJH.js";
import { useAuthStore } from "../stores/auth.js";
import projectRoutes1 from "./projectKJH.js";
/**
 * 라우터 등록 규칙
 * - path      : 사이드바 링크와 동일하게 맞출 것
 * - component : src/pages/ 하위에 파일 생성 후 import
 * - name      : 카멜케이스로 작성 ex) projectTasks
 *
 * [경로 규칙]
 * 메인            /
 * 전체 목록       /projects
 * 프로젝트 하위   /project/:기능명
 *
 * [파일 위치 규칙]
 * 최상위 페이지        → src/pages/페이지명.vue           ex) Dashboard.vue
 * 프로젝트 하위 페이지  → src/pages/project/페이지명.vue   ex) ProjectTasks.vue
 */

const routerHistory = createWebHistory();

const router = createRouter({
  history: routerHistory,
  routes: [
    // 예시) 아래 형식으로 추가하기
    // {
    //   path: '/project/tasks',
    //   name: 'projectTasks',
    //   component: () => import('./pages/project/ProjectTasks.vue'),
    // },
    {
      path: "/",
      name: "dashboard",
      component: () => import("../pages/Dashboard.vue"),
    },

    ...bulletinRoutes,
    ...adminRoutes,
    ...meetingRoutes,
    ...projectRoutes,
    ...reportRoutes,
    ...taskRoutes,
    ...taskRoutes1,
    ...projectRoutes1,
  ],
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const alive = sessionStorage.getItem("alive");
  const keepLogin = localStorage.getItem("keepLogin");

  if (authStore.user && !keepLogin && !alive) {
    authStore.logout();
    return next("/login");
  }

  if (to.meta.requiresAuth && !authStore.user) {
    return next("/login");
  }
  if (to.path == "/resetPassword") {
    next();
  } else if (to.path !== "/login" && !authStore.user) {
    next("/login");
  } else if (to.path.startsWith("/admin") && !useAuthStore().isAdmin) {
    next("/");
  } else {
    next();
  }
});

export default router;
