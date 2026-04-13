const documentRoutes = [
  {
    // 문서 목록
    path: "/project/:projectId/:subProjectId?/documentList",
    name: "documentList",
    component: () => import("../documents/DocumentList.vue"),
  },
  {
    // 문서 생성
    path: "/project/:projectId/:subProjectId?/documentRegister/:documentId?",
    name: "documentRegister",
    component: () => import("../documents/DocumentRegister.vue"),
  },
  {
    // 문서 상세
    path: "/project/:projectId/:subProjectId?/documentDetail/:documentId",
    name: "documentDetail",
    component: () => import("../documents/DocumentDetail.vue"),
  },
];
export default documentRoutes;
