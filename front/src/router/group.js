const groupRoutes = [
  {
    path: "/",
    name: "dashboard",
    component: () => import("../pages/Dashboard.vue"),
  },
  {
    path: "/admin/group",
    name: "group",
    component: () => import("../group/GroupList.vue"),
  },
  {
    path: "/admin/group/register",
    name: "groupRegister",
    component: () => import("../group/GroupCreate.vue"),
  },
  {
    path: "/admin/group/Info/:id",
    name: "groupInfo",
    component: () => import("../group/GroupInfo.vue"),
    props: true,
  },
  {
    path: "/admin/group/modify/:groupId",
    component: () => import("../group/GroupModify.vue"),
    meta: { requiresAuth: true },
  },
];
export default groupRoutes;
