const noticeRoutes = [
  {
    // 공지사항 목록
    path: "/project/:projectId/:subProjectId?/noticeList",
    name: "noticeList",
    component: () => import("../notice/NoticeList.vue"),
  },
  {
    // 공지사항 생성
    path: "/project/:projectId/:subProjectId?/noticeRegister/:noticeId?",
    name: "noticeRegister",
    component: () => import("../notice/NoticeRegister.vue"),
  },
  {
    // 공지사항 상세
    path: "/project/:projectId/:subProjectId?/noticeDetail/:noticeId",
    name: "noticeDetail",
    component: () => import("../notice/NoticeDetail.vue"),
  },
];
export default noticeRoutes;
