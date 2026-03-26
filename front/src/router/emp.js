const empRoutes = [
  {
    path: "/login",
    name: "login",
    component: () => import("../pages/login.vue"),
  },{
  path: "/admin/emp",
    name: "emp",
    component: () => import("../emp/emp.vue"),
  }
];
export default empRoutes;
