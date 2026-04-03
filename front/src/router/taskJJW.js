const taskRoutes1 = [
  {
    path: "/",
    name: "dashboard",
    component: () => import("../pages/Dashboard.vue"),
  },

  {
    path: "/task/TaskRegister/:projectId",
    name: "taskRegister",
    component: () => import("../task/TaskRegister.vue"),
  },

  {
    path: "/taskModify/:taskId",
    name: "taskModify",
    component: () => import("../task/TaskModify.vue"),
  },
  {
    path: "/task/gantt/:projectId",
    name: "GanttView",
    component: () => import("../report/GanttView.vue"),
  },
  {
    path: "/task/report/:projectId",
    name: "TaskReport",
    component: () => import("../report/TaskReport.vue"),
  },
];
export default taskRoutes1;
