import { createRouter, createWebHistory } from "vue-router";
import Swal from "sweetalert2"; // 🚨 Swal 임포트 추가
import bulletinRoutes from "./bulletin.js";
import adminRoutes from "./admin.js";
import meetingRoutes from "./meeting.js";
import projectRoutes from "./project.js";
import reportRoutes from "./report.js";
import taskRoutes1 from "./taskJJW.js";
import taskRoutes from "./taskKJH.js";
import { useAuthStore } from "../stores/auth.js";
import projectRoutes1 from "./projectKJH.js";
import noticeRoutes from "./notice.js";
import documentRoutes from "./document.js";

/**
 * 라우터 등록 규칙
 * - path      : 사이드바 링크와 동일하게 맞출 것
 * - component : src/pages/ 하위에 파일 생성 후 import
 * - name      : 카멜케이스로 작성 ex) projectTasks
 *
 * [경로 규칙]
 * 메인          /
 * 전체 목록       /projects
 * 프로젝트 하위   /project/:기능명
 *
 * [파일 위치 규칙]
 * 최상위 페이지        → src/pages/페이지명.vue          ex) Dashboard.vue
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
    {
      path: "/myPage",
      name: "MyPage",
      component: () => import("../pages/MyPage.vue"),
    },

    ...bulletinRoutes,
    ...adminRoutes,
    ...meetingRoutes,
    ...projectRoutes,
    ...reportRoutes,
    ...taskRoutes,
    ...taskRoutes1,
    ...projectRoutes1,
    ...noticeRoutes,
    ...documentRoutes,
  ],
});

// 🚨 async 추가 (Swal 대기용)
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  const extractedProjectId = to.params.projectId || to.params.rootProjectId;

  if (extractedProjectId) {
    // 2. 주소에서 프로젝트 ID를 찾았다면 세션 스토리지에 덮어씁니다!
    sessionStorage.setItem("CURRENT_PROJECT_ID", extractedProjectId);
  } else {
    // 3. 프로젝트 ID가 없는 주소(예: 메인 대시보드 / )로 갈 때의 처리
    sessionStorage.removeItem("CURRENT_PROJECT_ID");
  }

  // 1. 비밀번호 재설정 페이지는 로그인 여부 상관없이 무조건 허용
  if (to.path === "/resetPassword") {
    return next();
  }

  // 2. 관리자 전용 페이지 접근 제어 (/admin으로 시작하는 경로)
  if (to.path.startsWith("/admin") && !authStore.isAdmin) {
    await Swal.fire({
      icon: "error",
      title: "접근 거부",
      text: "해당 페이지는 관리자만 접근할 수 있습니다.",
      confirmButtonColor: "#2563eb",
    }).then(() => {
      next(false);
      router.back();
      return;
    });
  }

  // 3. 로그인이 필요한데 유저 정보가 없는 경우 (새로고침 시 세션이 날아갔거나, 원래 비로그인 상태)
  // to.meta.requiresAuth가 설정된 라우트이거나, 로그인 페이지가 아닌 다른 모든 페이지로 갈 때
  if (to.path !== "/login" && !authStore.user) {
    return next("/login");
  }

  // 4. 위 조건들을 다 통과했다면 정상적으로 이동 허용
  next();
});

export default router;
