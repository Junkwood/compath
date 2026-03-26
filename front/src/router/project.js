const projectRoutes = [
  {
    path: "/",
    name: "dashboard",
    component: () => import("../pages/Dashboard.vue"),
  },

  {
    path: "/project/ProjectCreateModal.vue",
    name: "projectCreateModal",
    component: () => import("../project/ProjectCreateModal.vue"),
  },
];
export default projectRoutes;
