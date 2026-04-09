const meetingRoutes = [
  {
    // 회의록 목록
    path: "/project/:projectId/meetingList",
    name: "meetingList",
    component: () => import("../meeting/meetingList.vue"),
  },
  {
    // 회의록 생성
    path: "/project/:projectId/meetingRegister/:meetingId?",
    name: "meetingRegister",
    component: () => import("../meeting/meetingRegister.vue"),
  },
  {
    // 회의록 상세
    path: "/project/:projectId/meetingDetail/:meetingId",
    name: "meetingDetail",
    component: () => import("../meeting/meetingDetail.vue"),
  },
];
export default meetingRoutes;
