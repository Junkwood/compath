const empRoutes = [
  {
    path: "/login",
    name: "login",
    component: () => import("../pages/Login.vue"),
  },
  {
    path: "/admin/emp",
    name: "emp",
    component: () => import("../emp/EmpList.vue"),
  },
];
export default empRoutes;
