const taskRoutes = [
  {
    path: "/task/list/:id",
    name: "taskList",
    component: () => import("../pages/task/TaskListKJH.vue"),
  },
];
export default taskRoutes;
