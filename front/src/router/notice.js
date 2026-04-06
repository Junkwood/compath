const noticeRoutes = [
  {
    // 공지사항 목록
    path: "/project/:projectId/noticeList",
    name: "noticeList",
    component: () => import("../notice/NoticeList.vue"),
  },
  {
    // 공지사항 생성
    path: "/project/:projectId/noticeRegister/:noticeId?",
    name: "noticeRegister",
    component: () => import("../notice/NoticeRegister.vue"),
  },
  {
    // 공지사항 상세
    path: "/project/:projectId/noticeDetail/:noticeId",
    name: "noticeDetail",
    component: () => import("../notice/NoticeDetail.vue"),
  },
];
export default noticeRoutes;
