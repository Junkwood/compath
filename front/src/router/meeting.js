const meetingRoutes = [
  {
    path: "/",
    name: "dashboard",
    component: () => import("../pages/Dashboard.vue"),
  },
];
export default meetingRoutes;
