const roleRoutes = [
  {
    path: "/",
    name: "dashboard",
    component: () => import("../pages/Dashboard.vue"),
  },
  {
    path: "/admin/role",
    name: "role",
    component: () => import("../role/RoleLIst.vue"),
  },
];
export default roleRoutes;
