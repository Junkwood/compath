const taskRoutes = [
  {
    path: "/project/:projectId/tasklist",
    name: "taskList",
    component: () => import("../task/TaskListKJH.vue"),
  },
];
export default taskRoutes;
