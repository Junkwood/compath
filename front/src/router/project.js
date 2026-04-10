const projectRoutes = [
  {
    path: "/",
    name: "dashboard",
    component: () => import("../pages/Dashboard.vue"),
  },

  {
    path: "/project/:projectId/dashboard",
    name: "projectDash",
    component: () => import("../project/ProjectDashboard.vue"),
    props: true,
  },
  {
    path: "/project/:projectId/memo",
    name: "projectMemo",
    component: () => import("../project/ProjectMemoModal.vue"),
  },
  {
    path: "/project/:projectId/milestone",
    name: "milestoneDashboard",
    component: () => import("../milestone/MilestoneDashboard.vue"),
  },
  {
    path: "/project/:projectId/milestoneDetail/:milestoneId",
    name: "milestoneDetail",
    component: () => import("../milestone/MilestoneDetail.vue"),
  },
  {
    path: "/project/:projectId/sub/:subProjectId",
    name: "subProjectDashboard",
    component: () => import("../project/ProjectSubDashboard.vue"),
  },
  {
    path: "/project/listAll",
    name: "projectListAll",
    component: () => import("../project/ProjectListAll.vue"),
  },
  {
    path: "/project/:projectId/workHistory",
    name: "projectWorkHistory",
    component: () => import("../project/ProjectWorkHistory.vue"),
  },
];
export default projectRoutes;
