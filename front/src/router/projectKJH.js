const projectRoutes1 = [
  {
    path: "/",
    name: "dashboard",
    component: () => import("../pages/Dashboard.vue"),
  },

  {
    path: "/project/setting/:id",
    name: "projectSetting",
    component: () => import("../project/ProjectSettingKJH.vue"),
    props: true,
  },
];
export default projectRoutes1;
