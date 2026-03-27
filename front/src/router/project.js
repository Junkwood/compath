const projectRoutes = [
  {
    path: "/",
    name: "dashboard",
    component: () => import("../pages/Dashboard.vue"),
  },

  {
    path: "/project/dashboard/:projectId",
    name: "projectDash",
    component: () => import("../project/ProjectDashboard.vue"),
    props: true,
  },
];
export default projectRoutes;
