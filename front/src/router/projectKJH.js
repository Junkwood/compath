const projectRoutes1 = [
  {
    path: "/",
    name: "dashboard",
    component: () => import("../pages/Dashboard.vue"),
  },

  {
    // 프로젝트 설정
    path: "/project/:projectId/setting",
    name: "projectSetting",
    component: () => import("../project/ProjectSettingKJH.vue"),
    props: true,
  },
];
export default projectRoutes1;
