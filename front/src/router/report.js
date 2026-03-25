const reportRoutes = [
  {
    path: "/",
    name: "dashboard",
    component: () => import("../pages/Dashboard.vue"),
  },
];
export default reportRoutes;
