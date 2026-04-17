import RoleForm from "../role/RoleForm.vue";
const adminRoutes = [
  {
    // 업무 상태 목록
    path: "/admin/task/status",
    name: "taskStatusList",
    component: () => import("../task/TaskStatusListSJW.vue"),
  },
  {
    // 업무 유형 목록
    path: "/admin/task/type",
    name: "taskTypeList",
    component: () => import("../task/TaskTypeListSJW.vue"),
  },
  {
    //역할 목록
    path: "/admin/role",
    name: "roleList",
    component: () => import("../role/RoleList.vue"),
  },
  {
    //역할 등록
    path: "/admin/role/register",
    name: "roleRegister",
    component: RoleForm,
  },
  {
    //역할 수정
    path: "/admin/role/modify/:id",
    name: "roleModify",
    component: RoleForm,
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
    component: () => import("../group/GroupModify.vue"),
  },
  {
    //그룹 단건조회 페이지
    path: "/admin/group/info/:id",
    name: "groupInfo",
    component: () => import("../group/GroupInfo.vue"),
    props: true,
  },
  {
    //그룹 수정 페이지
    path: "/admin/group/modify/:id",
    component: () => import("../group/GroupModify.vue"),
    meta: { requiresAuth: true },
  },
  {
    //그룹목록페이지
    path: "/group",
    name: "groupPM",
    component: () => import("../group/GroupList.vue"),
  },
  {
    //그룹 등록페이지
    path: "/group/register",
    name: "groupRegisterPM",
    component: () => import("../group/GroupModify.vue"),
  },
  {
    //그룹 단건조회 페이지
    path: "/group/info/:id",
    name: "groupInfoPM",
    component: () => import("../group/GroupInfo.vue"),
    props: true,
  },
  {
    //그룹 수정 페이지
    path: "/group/modify/:id",
    component: () => import("../group/GroupModify.vue"),
    meta: { requiresAuth: true },
  },
];
export default adminRoutes;
