const empRoutes = [
  {
    path: "/login",
    name: "login",
    component: () => import("../pages/login.vue"),
  },
];
export default empRoutes;
