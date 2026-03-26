const taskRoutes = [
  {
    path: "/tasklist/:id",
    name: "taskList",
    component: () => import("../pages/task/TaskListKJH.vue"),
  },
];
export default taskRoutes;
