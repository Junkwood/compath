<template>
  <div class="flex h-screen overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden"
    >
      <!-- Header -->
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <div class="col-span-full xl:col-span-8 mx-8 mt-3 mb-0">
        <header class="pt-4 border-b border-gray-100 dark:border-gray-700/60">
          <h1 class="text-2xl font-bold text-gray-800 dark:text-gray-100 mb-8">
            설정 <span class="text-base"> [{{ name }}]</span>
          </h1>
        </header>
      </div>
      <div class="grid grid-cols-12 gap-4 mx-8 mb-2">
        <div
          class="text-lg font-bold text-gray-800 dark:text-gray-100 col-start-1 col-end-4"
        >
          <h2>기본 설정</h2>
        </div>
      </div>
      <div
        class="col-span-full xl:col-span-8 bg-white dark:bg-gray-800 shadow-xs rounded-xl mx-8 mb-0 p-6"
      >
        <!-- 예정시작일 / 예정종료일 / 추정시간 -->
        <div class="grid grid-cols-3 gap-6 mb-8">
          <div class="flex flex-row gap-10">
            <label class="block text-base font-semibold mb-1"
              >프로젝트 이름</label
            >
            <span>{{ projectInfo.projectName }}</span>
          </div>
          <div class="flex flex-row gap-10">
            <label class="block text-base font-semibold mb-1">시작일</label>
            <span>{{ projectInfo.startDate }}</span>
          </div>
          <div class="flex flex-row gap-10">
            <label class="block text-base font-semibold mb-1">종료일</label>
            <span>{{ projectInfo.endDate }}</span>
          </div>
        </div>

        <!-- 예정시작일 / 예정종료일 / 추정시간 -->
        <div class="grid grid-cols-3 gap-6 mb-8">
          <div class="flex flex-row gap-10">
            <label class="block text-base font-semibold mb-1 ml-11"
              >식별자</label
            >
            <span>{{ projectInfo.identifier }}</span>
          </div>
          <div class="flex flex-row gap-10">
            <label class="block text-base font-semibold mb-1 ml-5">PM</label>
            <span>{{ projectInfo.pmUserId }}</span>
          </div>
          <div class="flex flex-row gap-10">
            <label class="block text-base font-semibold mb-1">총괄 PL</label>
            <span>{{ projectInfo.plUserId }}</span>
          </div>
        </div>
        <div class="mb-6">
          <label class="block text-base font-semibold mb-1"
            >프로젝트 설명</label
          >
          <textarea
            rows="5"
            class="input w-full"
            :value="projectInfo.description"
            disabled
          />
        </div>
        <div class="flex flex-row mb-8">
          <div class="flex flex-row gap-10">
            <label class="block text-base font-semibold mb-1 ml-8"
              >마일스톤</label
            >
            <span>{{ projectInfo.useMilestone }}</span>
          </div>
        </div>
        <div class="flex flex-row justify-between">
          <div class="flex flex-row gap-10">
            <label class="block text-base font-semibold mb-1"
              >공개 프로젝트</label
            >
            <span>{{ projectInfo.isPublic }}</span>
          </div>
          <div>
            <button @click="openModfyModal" class="btn-green">수정</button>
          </div>
        </div>
      </div>
      <div class="flex flex-row justify-between gap-4 mx-8 mt-4">
        <div
          class="text-lg font-bold text-gray-800 dark:text-gray-100 col-start-1 col-end-4 pt-3"
        >
          <h2>구성원</h2>
        </div>
        <div>
          <button
            @click="openMemberModal"
            class="btn bg-violet-500 hover:bg-violet-600 text-white"
          >
            구성원 추가
          </button>
        </div>
      </div>
      <div class="card main-col mx-8 mt-4">
        <!-- Table -->

        <el-table
          :data="memberList"
          style="width: 100%"
          :header-cell-style="headerStyle"
          :cell-style="cellStyle"
        >
          <el-table-column
            prop="name"
            label="이름"
            width="300"
            align="center"
          />
          <el-table-column
            prop="userId"
            label="사번"
            width="300"
            align="center"
          />
          <el-table-column
            prop="email"
            label="이메일"
            width="500"
            align="center"
          />
          <el-table-column
            prop="userRoleName"
            label="역할"
            width="300"
            align="center"
          />
          <el-table-column
            fixed="right"
            label="삭제"
            width="200"
            align="center"
          >
            <template #default="scope">
              <el-button
                size="small"
                type="danger"
                @click="handleDelete(scope.$index, scope.row)"
              >
                Delete
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>

  <ProjectModifyModal
    v-model="ModifyProjectModalOpen"
    :originInfo="projectInfo"
    @handle-cancel="closeModifyMdoal"
    @modify-Info="modifyProject"
  />
  <ProjectMemberModal
    v-model="MemberModalOpen"
    :groupList="groupList"
    @member-cancel="closeMemberMdoal"
    @member-insert="memberInsert"
  />
</template>

<script setup>
import { onBeforeMount, ref } from "vue";
import { usetaskKJHStore } from "../stores/taksKJH";
import { useProjectKJHStore } from "../stores/projectKJH";
import { useRoute } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import ProjectModifyModal from "./ProjectModifyModal.vue";
import ProjectMemberModal from "./ProjectMemberModal.vue";

const route = useRoute();
const taskStore = usetaskKJHStore();
const projectStore = useProjectKJHStore();

const name = ref(); // 프로젝트 이름
const projectInfo = ref({
  createdAt: null,
  createdBy: null,
  description: null,
  editorUserId: null,
  endDate: null,
  identifier: null,
  isPublic: null,
  parentProjectId: null,
  plUserId: null,
  pmUserId: null,
  projectId: null,
  projectName: null,
  startDate: null,
  status: null,
  updatedAt: null,
  useMilestone: null,
});
const id = route.params.id;

const ModifyProjectModalOpen = ref(false); // 수정 모달
const MemberModalOpen = ref(false); // 구성원 추가 모달
const memberList = ref([]); // 구성원 테이블
const groupList = ref([]);

onBeforeMount(async () => {
  // 프로젝트명
  await taskStore.getProjectName(id);
  name.value = taskStore.projectName.projectName;

  // 프로젝트 정보
  await projectStore.getProjectInfo(id);
  projectInfo.value = projectStore.projectInfo;
  console.log("프로젝트 정보: ", projectInfo.value);

  // 사용자 정보 조회
  await projectStore.getUsersById(projectInfo.value.pmUserNum);
  memberList.value.push(projectStore.userInfo);
  memberList.value[0].userRoleName = "PM";
  await projectStore.getUsersById(projectInfo.value.plUserNum);
  memberList.value.push(projectStore.userInfo);
  memberList.value[1].userRoleName = "PL";

  // 그룹정보
  await projectStore.getAllGroups();
  groupList.value = projectStore.groupList;
});

// 수정버튼
const openModfyModal = () => {
  ModifyProjectModalOpen.value = true;
};

// 설정 모달 취소버튼
const closeModifyMdoal = () => {
  ModifyProjectModalOpen.value = false;
};

// 설정 모달창 수정 버튼
const modifyProject = async (form) => {
  const payload = {
    projectId: form.projectId,
    projectName: form.projectName,
    identifier: form.identifier,
    plUserId: form.plUserId,
    startDate: form.startDate,
    endDate: form.endDate,
    description: form.description,
    useMilestone: form.useMilestone ? "O1" : "O2",
    isPublic: form.isPublic ? "P1" : "P2",
  };

  console.log("전송데이터", payload);

  await projectStore.modifyProject(payload);

  projectInfo.value = projectStore.modifiedInfo;
  await closeModifyMdoal();
};

// 구성원 추가 버튼
const openMemberModal = () => {
  MemberModalOpen.value = true;
};

// 구성원 모달 취소 버튼
const closeMemberMdoal = () => {
  MemberModalOpen.value = false;
};

// 구성원 모달 추가 버튼
const memberInsert = () => {
  console.log("구성원 추가 완료");
  closeMemberMdoal();
};
</script>
<style scoped>
/* 인풋 전체 라운드 */
:deep(.input) {
  border-radius: 10px !important;
  border: 1px solid #e2e8f0 !important;
  background: #f8fafc !important;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
  font-size: 13px;
}
:deep(.input:focus) {
  border-color: #94a3b8 !important;
  box-shadow: 0 0 0 3px rgba(148, 163, 184, 0.15) !important;
  background: #fff !important;
  outline: none;
}
:deep(.input:disabled) {
  background: #f1f5f9 !important;
  color: #94a3b8 !important;
}
:deep(select.input) {
  border-radius: 10px !important;
  appearance: auto !important;
  -webkit-appearance: auto !important;
  padding-right: 28px !important;
}
:deep(textarea.input) {
  border-radius: 10px !important;
}
:deep(.input:disabled) {
  background: #f1f5f9 !important;
  color: #475569 !important; /* #94a3b8 → #475569 으로 변경! */
}

/* 수정버튼 */
.btn-green {
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  background: #1882c9;
  color: #fff;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(22, 163, 74, 0.25);
  letter-spacing: 0.01em;
}
.btn-green:hover {
  background: #60aee2;
  box-shadow: 0 4px 10px rgba(22, 163, 74, 0.3);
  transform: translateY(-1px);
}
/* ── 카드 공통 ── */
.card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}
.card-header {
  padding: 14px 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.card-title {
  font-weight: 600;
  font-size: 14px;
  color: #1a1a2e;
}
</style>
