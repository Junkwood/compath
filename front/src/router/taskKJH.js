const taskRoutes = [
  {
    // 업무 목록
    path: "/project/:projectId/:subProjectId?/tasklist",
    name: "taskList",
    component: () => import("../task/TaskListKJH.vue"),
  },
  {
    // 업무 상세페이지
    path: "/project/:projectId/:subProjectId?/tasklist/:taskId",
    name: "taskDetail",
    component: () => import("../task/TaskDetail.vue"),
  },
];
export default taskRoutes;
