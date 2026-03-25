import { createRouter, createWebHistory } from "vue-router";
import bulletinRoutes from "./bulletin.js";
import empRoutes from "./emp.js";
import groupRoutes from "./group.js";
import meetingRoutes from "./meeting.js";
import milstoneRoutes from "./milstone.js";
import projectRoutes from "./project.js";
import reportRoutes from "./report.js";
import roleRoutes from "./role.js";
import taskRoutes from "./task.js";
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
    ...empRoutes,
    ...groupRoutes,
    ...meetingRoutes,
    ...milstoneRoutes,
    ...projectRoutes,
    ...reportRoutes,
    ...roleRoutes,
    ...taskRoutes,
  ],
});

export default router;
