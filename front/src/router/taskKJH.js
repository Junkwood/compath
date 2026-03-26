const taskRoutes = [
  {
    path: "/tasklist/:id",
    name: "taskList",
    component: () => import("../task/TaskListKJH.vue"),
  },
];
export default taskRoutes;
