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
];
export default taskRoutes1;
