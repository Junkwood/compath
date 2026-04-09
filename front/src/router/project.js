const projectRoutes = [
  {
    path: "/",
    name: "dashboard",
    component: () => import("../pages/Dashboard.vue"),
  },

  {
    path: "/project/dashboard/:projectId",
    name: "projectDash",
    component: () => import("../project/ProjectDashboard.vue"),
    props: true,
  },
  {
    path: "/project/memo/:projectId",
    name: "projectMemo",
    component: () => import("../project/ProjectMemoModal.vue"),
  },
  {
    path: "/project/milestone/:projectId",
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
    path: "/project/workHistory",
    name: "projectWorkHistory",
    component: () => import("../project/ProjectWorkHistory.vue"),
  },
];
export default projectRoutes;
