const taskRoutes1 = [
  {
    path: "/",
    name: "dashboard",
    component: () => import("../pages/Dashboard.vue"),
  },

  {
    path: "/project/:projectId/task/TaskRegister",
    name: "taskRegister",
    component: () => import("../task/TaskRegister.vue"),
  },

  {
    path: "/project/:projectId/taskModify/:taskId",
    name: "taskModify",
    component: () => import("../task/TaskModify.vue"),
  },
  {
    path: "/project/:projectId/task/gantt",
    name: "GanttView",
    component: () => import("../report/GanttView.vue"),
  },
  {
    path: "/project/:projectId/task/report",
    name: "TaskReport",
    component: () => import("../report/TaskReport.vue"),
  },
];
export default taskRoutes1;
