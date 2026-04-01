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
    path: "/project/milestoneDetail/:projectId/:milestoneId",
    name: "milestoneDetail",
    component: () => import("../milestone/MilestoneDetail.vue"),
  },
];
export default projectRoutes;
