<template>
  <div class="dashboard-page flex h-screen overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden bg-gray-50"
    >
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="sub-header">
          <div class="breadcrumb">
            <span class="bc-home">홈</span>
            <span class="bc-sep">›</span>
            <span v-for="info in taskPjList" :key="info">{{ info }} › </span>
            <span class="bc-cur"> 공지사항 상세</span>
          </div>
        </div>

        <div class="page-container">
          <!-- 상단 프로젝트 정보 -->
          <div class="pg-row">
            <div class="pg-left">
              <div class="proj-meta">
                <span class="proj-name"> {{ noticeInfo.projectName }}</span>
                <span class="proj-period">
                  {{ noticeInfo.startDate }} ~ {{ noticeInfo.endDate }}
                </span>
              </div>
            </div>

            <div class="notice-action-wrap">
              <button
                v-if="noticeInfo.isDeleted === 'Q2' && isAssignee"
                @click="modifyNotice"
                class="btn-edit"
              >
                수정
              </button>

              <button v-if="isAssignee" @click="lockNotice" class="btn-lock">
                <el-icon><Lock /></el-icon>
                <span>{{
                  noticeInfo.isDeleted === "Q1" ? "비활성 해제" : "비활성"
                }}</span>
              </button>
              <button @click="goBack" type="button" class="btn-back-top">
                목록으로
              </button>
            </div>
          </div>
          <el-alert
            v-if="noticeInfo.isDeleted === 'Q1' && isAssignee"
            title="비활성화된 게시글입니다."
            type="warning"
            description="관리자만 열람 가능하며 일반 사용자에게는 노출되지 않습니다."
            show-icon
            :closable="false"
            class="top-alert"
          />

          <!-- 공지 본문 -->
          <div class="panel notice-panel">
            <div class="notice-article">
              <div class="notice-top-row">
                <div class="notice-badge-wrap">
                  <span
                    v-if="noticeInfo.isPinned === 'B1'"
                    class="notice-badge badge-emergency"
                  >
                    긴급
                  </span>
                  <span class="notice-badge badge-category">
                    {{ noticeInfo.roleName || "일반" }}
                  </span>
                  <span
                    v-if="noticeInfo.isDeleted === 'Q1'"
                    class="notice-badge badge-disabled"
                  >
                    비활성
                  </span>
                </div>
              </div>

              <h2 class="notice-title">{{ noticeInfo.title }}</h2>

              <div class="notice-meta">
                <span class="meta-item">
                  <span class="meta-label">작성자</span>
                  <span class="meta-value">{{
                    noticeInfo.userName || "-"
                  }}</span>
                </span>
                <span class="meta-divider"></span>
                <span class="meta-item">
                  <span class="meta-label">등록일</span>
                  <span class="meta-value">{{
                    noticeInfo.createdAt || "-"
                  }}</span>
                </span>
              </div>

              <div class="notice-divider"></div>

              <div class="notice-content-wrap">
                <div class="notice-content">
                  {{ noticeInfo.content || "내용이 없습니다." }}
                </div>
              </div>

              <!-- 첨부파일 -->
              <div
                class="mt-8 p-6 bg-white rounded-2xl border border-gray-200 shadow-sm"
              >
                <!-- 헤더 -->
                <div class="flex items-center gap-2 mb-5">
                  <div
                    class="w-6 h-6 flex items-center justify-center bg-blue-50 rounded-md"
                  >
                    <svg
                      class="w-4 h-4 text-blue-500"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13"
                      />
                    </svg>
                  </div>

                  <label class="text-[15px] font-semibold text-gray-800">
                    첨부파일
                    <span class="text-blue-600 ml-1 font-bold">
                      {{ attachmentList != null ? attachmentList.length : 0 }}
                    </span>
                  </label>
                </div>

                <!-- 파일 리스트 -->
                <div class="space-y-3">
                  <div
                    v-for="(file, index) in attachmentList"
                    :key="index"
                    v-if="
                      attachmentList != null &&
                      attachmentList != undefined &&
                      attachmentList.length > 0
                    "
                    class="flex items-center justify-between bg-gray-50 px-4 py-3 rounded-xl border border-transparent hover:border-blue-200 hover:bg-blue-50/40 hover:shadow-sm transition-all duration-200 group cursor-pointer"
                    @click="attachmentDownload(file)"
                  >
                    <!-- 왼쪽 -->
                    <div class="flex items-center gap-3 flex-1">
                      <div
                        class="w-9 h-9 bg-white rounded-lg flex items-center justify-center border border-gray-200 group-hover:border-blue-200 group-hover:bg-blue-100 transition"
                      >
                        <svg
                          class="w-4 h-4 text-gray-400 group-hover:text-blue-600"
                          fill="none"
                          stroke="currentColor"
                          viewBox="0 0 24 24"
                        >
                          <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            stroke-width="2"
                            d="M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z"
                          />
                        </svg>
                      </div>

                      <div class="flex flex-col">
                        <span
                          class="text-[14px] font-medium text-gray-800 group-hover:text-blue-600 transition"
                        >
                          {{ file.fileName }}
                        </span>
                        <span class="text-[11px] text-gray-400">1.2 MB</span>
                      </div>
                    </div>

                    <!-- 다운로드 버튼 -->
                    <button
                      class="flex items-center gap-1 px-3 py-1.5 text-[12px] font-medium text-gray-500 bg-white border border-gray-200 rounded-md group-hover:bg-blue-600 group-hover:text-white group-hover:border-blue-600 transition-all duration-200 cursor-pointer"
                    >
                      다운로드
                      <svg
                        class="w-3 h-3"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M4 16v1a2 2 0 002 2h12a2 2 0 002-2v-1m-4-4l-4 4m0 0l-4-4m4 4V4"
                        />
                      </svg>
                    </button>
                  </div>

                  <!-- empty -->
                  <div
                    v-else
                    class="py-10 flex flex-col items-center justify-center bg-gray-50 rounded-xl border border-dashed border-gray-200"
                  >
                    <div
                      class="w-12 h-12 flex items-center justify-center bg-white rounded-full mb-3 border border-gray-200"
                    >
                      <svg
                        class="w-6 h-6 text-gray-300"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M9 13h6m-3-3v6m5 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                        />
                      </svg>
                    </div>

                    <span class="text-gray-400 text-sm">
                      첨부된 파일이 없습니다.
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { onBeforeMount, ref, watch, computed } from "vue";
import { useProjectKJHStore } from "../stores/projectKJH";
import { useNoticeStore } from "../stores/notice";
import { usetaskKJHStore } from "../stores/taksKJH";
import { useAttachmentStore } from "../stores/attachment";
import { useAuthStore } from "../stores/auth";
import { useRoute, useRouter } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { Lock } from "@element-plus/icons-vue";
import Swal from "sweetalert2";

const route = useRoute();
const router = useRouter();
const noticeStore = useNoticeStore();
const projectStore = useProjectKJHStore();
const taskStore = usetaskKJHStore();
const authStore = useAuthStore();
const attachmentStore = useAttachmentStore();

const noticeInfo = ref({});
const noticeId = route.params.noticeId;
const projectId = route.params.projectId;
const subProjectId = ref(route.params.subProjectId);
let taskPjList = ref([]);
const attachmentList = ref([]);

const memberList = ref([]); // 구성원 테이블

// 목록으로
const goBack = () => {
  router.push({
    name: "noticeList",
    params: { projectId: projectId, subProjectId: subProjectId.value },
  });
};

// 수정 버튼
const modifyNotice = () => {
  router.push({
    name: "noticeRegister",
    params: { projectId: projectId, noticeId: noticeId },
  });
};

// 비활성 및 해제 버튼
const lockNotice = async () => {
  let lock = noticeInfo.value.isDeleted == "Q2" ? true : false;
  let isDeleted = noticeInfo.value.isDeleted == "Q2" ? "Q1" : "Q2";

  if (lock) {
    const result = await Swal.fire({
      title: "정말 비활성하시겠습니까?",
      text: "비활성한 공지사항은 목록에서 보이지 않습니다.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "비활성",
      cancelButtonText: "취소",
      reverseButtons: true,
    });

    if (!result.isConfirmed) return;
  } else {
    const result = await Swal.fire({
      title: "정말 비활성화를 해제하시겠습니까?",
      text: "활성화된 공지사항은 목록에서 다시 보이게됩니다.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "활성화",
      cancelButtonText: "취소",
      reverseButtons: true,
    });

    if (!result.isConfirmed) return;
  }
  await noticeStore.modifyNoticeLock(noticeId, isDeleted);
  noticeInfo.value = noticeStore.registeredNotice;
};

// 파일 다운로드
const attachmentDownload = async (file) => {
  console.log(file);
  await attachmentStore.downloadFile(file);
};

onBeforeMount(async () => {
  console.log(noticeId);
  // 공지사항 및 프로젝트 정보
  await noticeStore.getNoticeById(noticeId);
  noticeInfo.value = noticeStore.noticeInfo.noticeInfo;
  attachmentList.value = noticeStore.noticeInfo.attachmentList;

  console.log(subProjectId.value);
  let id =
    subProjectId.value != null &&
    subProjectId.value != undefined &&
    subProjectId.value != ""
      ? subProjectId.value
      : projectId;
  await taskStore.getProjectName(id);
  let projectInfo = taskStore.projectName;

  let roleObj = { projectId: projectId, subProjectId: subProjectId.value };
  await taskStore.getProjectRole(roleObj);

  if (projectInfo.parentProjectName != null) {
    taskPjList.value = [projectInfo.parentProjectName, projectInfo.projectName];
  } else {
    taskPjList.value = [projectInfo.projectName];
  }
});

// 권한 파악
const isAssignee = computed(() => {
  const currentUserId = authStore.user?.userId || authStore.user?.id;
  if (!currentUserId) return false;

  const isPmPl = (taskStore.plPmList?.projectRoleList || []).some(
    (item) => Number(item.userId) === Number(currentUserId),
  );
  const isManager = (taskStore.plPmList?.empList || []).some(
    (item) => Number(item.userId) === Number(currentUserId),
  );
  return isPmPl || isManager;
});

watch(
  () => projectStore.insertedList,
  () => {
    memberList.value = projectStore.insertedList;
  },
);
</script>

<style scoped>
.dashboard-page {
  font-family: "Pretendard", sans-serif;
  background-color: #f3f4f6;
}

.sub-header {
  background: #fff;
  padding: 15px 24px;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 30;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.bc-home {
  color: #9ca3af;
}

.bc-sep {
  color: #d1d5db;
}

.bc-cur {
  color: #111827;
  font-weight: 600;
}

.page-container {
  padding: 24px 30px 24px 30px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.top-alert {
  margin-bottom: -8px;
}

/* 상단 프로젝트 카드 */
.pg-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 20px 24px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.pg-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.pg-title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #111827;
}

.proj-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.proj-name {
  font-size: 15px;
  font-weight: 700;
  color: #1b5c9c;
}

.proj-period {
  font-size: 13px;
  color: #6b7280;
}

/* 상단 돌아가기 버튼 */
.btn-back-top {
  height: 40px;
  padding: 0 18px;
  border-radius: 10px;
  border: 1px solid #dbe4f0;
  background: #fff;
  color: #1b5c9c;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.btn-back-top:hover {
  background: #eff6ff;
  border-color: #bfd3f6;
}

/* 공통 패널 */
.panel {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

/* 공지 본문 */
.notice-panel {
  overflow: hidden;
}

.notice-article {
  padding: 28px 32px 32px;
}

.notice-top-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 18px;
  flex-wrap: wrap;
}

.notice-badge-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.notice-badge {
  display: inline-flex;
  align-items: center;
  height: 30px;
  padding: 0 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 800;
}

.badge-emergency {
  background: #fee2e2;
  color: #b91c1c;
}

.badge-category {
  background: #eff6ff;
  color: #1d4ed8;
}

.badge-disabled {
  background: #f3f4f6;
  color: #4b5563;
}

.notice-action-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.notice-title {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
  color: #111827;
  line-height: 1.45;
  letter-spacing: -0.02em;
}

.notice-meta {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
  margin-top: 14px;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.meta-label {
  font-size: 13px;
  font-weight: 700;
  color: #6b7280;
}

.meta-value {
  font-size: 14px;
  color: #334155;
  font-weight: 500;
}

.meta-divider {
  width: 1px;
  height: 14px;
  background: #d1d5db;
}

.notice-divider {
  height: 1px;
  background: #e5e7eb;
  margin: 22px 0 24px;
}

.notice-content-wrap {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.content-label {
  font-size: 14px;
  font-weight: 700;
  color: #111827;
}

.notice-content {
  min-height: 260px;
  padding: 0;
  color: #334155;
  font-size: 15px;
  line-height: 1.95;
  white-space: pre-line;
  word-break: break-word;
}

/* 버튼 */
.btn-edit,
.btn-lock {
  height: 38px;
  padding: 0 16px;
  border-radius: 10px;
  border: none;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-edit {
  background: linear-gradient(135deg, #1b5c9c 0%, #144677 100%);
  color: #fff;
  box-shadow: 0 4px 12px rgba(27, 92, 156, 0.22);
}

.btn-edit:hover {
  transform: translateY(-1px);
  filter: brightness(1.05);
}

.btn-lock {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: #ef4444;
  color: #fff;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.2);
}

.btn-lock:hover {
  transform: translateY(-1px);
  background: #dc2626;
}

@media (max-width: 768px) {
  .sub-header {
    padding: 12px 16px;
  }

  .page-container {
    padding: 16px;
    gap: 16px;
  }

  .pg-row {
    padding: 16px;
    flex-direction: column;
    align-items: flex-start;
  }

  .pg-title {
    font-size: 20px;
  }

  .notice-article {
    padding: 18px 16px 20px;
  }

  .notice-top-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .notice-title {
    font-size: 21px;
    line-height: 1.5;
  }

  .notice-meta {
    gap: 10px;
  }

  .meta-divider {
    display: none;
  }

  .notice-action-wrap {
    width: 100%;
  }

  .btn-edit,
  .btn-lock {
    flex: 1;
    justify-content: center;
  }

  .btn-back-top {
    width: 100%;
  }
}
</style>
