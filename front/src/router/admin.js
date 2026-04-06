const adminRoutes = [
  {
    // 업무 상태 목록
    path: "/admin/task/status",
    name: "taskStatusList",
    component: () => import("../task/TaskStatusListSJW.vue"),
  },
  {
    //역할 목록
    path: "/admin/role",
    name: "role",
    component: () => import("../role/RoleLIst.vue"),
  },
  {
    //로그인페이지
    path: "/login",
    name: "login",
    component: () => import("../pages/Login.vue"),
  },
  {
    //계정목록페이지
    path: "/admin/emp",
    name: "emp",
    component: () => import("../emp/EmpList.vue"),
  },
  {
    //비번 재설정페이지
    path: "/resetPassword",
    component: () => import("../pages/ResetPassword.vue"),
    meta: { requiresAuth: false },
  },
  {
    //그룹목록페이지
    path: "/admin/group",
    name: "group",
    component: () => import("../group/GroupList.vue"),
  },
  {
    //그룹 등록페이지
    path: "/admin/group/register",
    name: "groupRegister",
    component: () => import("../group/GroupCreate.vue"),
  },
  {
    //그룹 단건조회 페이지
    path: "/admin/group/Info/:id",
    name: "groupInfo",
    component: () => import("../group/GroupInfo.vue"),
    props: true,
  },
  {
    //그룹 수정 페이지
    path: "/admin/group/modify/:groupId",
    component: () => import("../group/GroupModify.vue"),
    meta: { requiresAuth: true },
  },
];
export default adminRoutes;
