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
  {
    path: "/resetPassword",
    component: () => import("../pages/ResetPassword.vue"),
    meta: { requiresAuth: false },
  },
];
export default empRoutes;
