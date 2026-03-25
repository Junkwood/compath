const bulletinRoutes = [
  {
    path: "/",
    name: "dashboard",
    component: () => import("../pages/Dashboard.vue"),
  },
];
export default bulletinRoutes;
