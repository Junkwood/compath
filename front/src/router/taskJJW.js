const taskRoutes1 = [
  {
    path: "/",
    name: "dashboard",
    component: () => import("../pages/Dashboard.vue"),
  },

  {
    path: "/task/TaskRegister",
    name: "taskRegister",
    component: () => import("../task/TaskRegister.vue"),
  },

  {
    path: "/taskModify",
    name: "taskModify",
    component: () => import("../task/TaskModify.vue"),
  },
];
export default taskRoutes1;
