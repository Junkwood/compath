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
        <!-- 서브 헤더 / 브레드크럼 -->
        <div class="sub-header">
          <div class="breadcrumb">
            <span class="bc-home">홈</span>
            <span class="bc-sep">›</span>
            <span v-for="info in taskPjList" :key="info">{{ info }} › </span>
            <span class="bc-cur"> 문서 상세</span>
          </div>
        </div>

        <div class="page-container">
          <!-- 상단 프로젝트 정보 -->
          <div class="pg-row">
            <div class="pg-left">
              <div class="proj-meta">
                <span class="proj-name">{{ name }}</span>
                <span class="proj-period">
                  {{ projectStartDate }} ~ {{ projectendDate }}
                </span>
              </div>
            </div>

            <div class="notice-action-wrap">
              <button
                v-if="isAssignee"
                @click="modifyDocument"
                class="btn-edit"
              >
                수정
              </button>
              <button
                v-if="isAssignee && commentList.length == 0"
                class="btn-lock"
                @click="delDocument"
              >
                삭제
              </button>
              <button @click="goBack" type="button" class="btn-back-top">
                목록으로
              </button>
            </div>
          </div>

          <!-- 문서 본문 패널 -->
          <div class="panel notice-panel">
            <div class="notice-article">
              <div class="flex flex-row gap-2">
                <!-- 제목 -->
                <h2 class="notice-title">{{ documentInfo.title }}</h2>
                <div class="flex items-center">
                  <span
                    v-if="documentInfo.isPinned === 'O1'"
                    class="fixed-badge"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="10"
                      height="10"
                      viewBox="0 0 24 24"
                      fill="currentColor"
                      style="
                        display: inline;
                        vertical-align: -1px;
                        margin-right: 2px;
                      "
                    >
                      <path
                        d="M16 12V4h1V2H7v2h1v8l-2 2v2h5.2v6h1.6v-6H18v-2l-2-2z"
                      />
                    </svg>
                    중요
                  </span>
                </div>
              </div>

              <!-- 메타 정보 -->
              <div class="notice-meta">
                <span class="meta-item">
                  <span class="meta-label">카테고리</span>
                  <span class="notice-badge badge-category">{{
                    documentInfo.typeName == null
                      ? "전체"
                      : documentInfo.typeName
                  }}</span>
                </span>
                <span class="meta-item">
                  <span class="meta-label">작성자</span>
                  <span class="meta-value">{{
                    documentInfo.userName || "-"
                  }}</span>
                </span>
                <span class="meta-divider"></span>
                <span class="meta-item">
                  <span class="meta-label">등록일</span>
                  <span class="meta-value">{{
                    documentInfo.createdAt || "-"
                  }}</span>
                </span>
              </div>

              <div class="notice-divider"></div>

              <!-- 문서 설명 -->
              <div class="notice-content-wrap">
                <div class="notice-content">
                  <Editor
                    class="w-full"
                    :modelValue="documentInfo.content"
                    :isRead="isRead"
                  />
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
                    <button
                      type="button"
                      @click="attachmentDownload(file)"
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
                    <span class="text-gray-400 text-sm"
                      >첨부된 파일이 없습니다.</span
                    >
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 댓글 섹션 -->
          <div
            v-if="documentInfo.isComment == 'O1'"
            class="panel comment-panel"
          >
            <div class="comment-panel-header">
              <div class="comment-panel-title">
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
                    d="M8 10h.01M12 10h.01M16 10h.01M21 16c0 1.1-.9 2-2 2H5l-4 4V6c0-1.1.9-2 2-2h16c0 1.1.9 2 2 2v10z"
                  />
                </svg>
                댓글
                <span class="comment-count">{{ commentList.length }}</span>
              </div>
            </div>

            <!-- 댓글 입력 -->
            <div class="comment-input-wrap">
              <textarea
                v-model="comment"
                placeholder="댓글을 입력해주세요."
                class="comment-textarea"
                @keyup.ctrl.enter="registerComment()"
                rows="3"
              />
              <div class="comment-input-footer">
                <div></div>
                <button
                  type="button"
                  @click="registerComment()"
                  class="btn-comment-submit"
                >
                  등록
                </button>
              </div>
            </div>

            <!-- 댓글 목록 -->
            <div class="comment-list-wrap">
              <div
                class="comment-item"
                v-for="commentItem in commentList"
                :key="commentItem.documentCommentId"
              >
                <!-- 일반 표시 -->
                <div v-if="!commentItem.modifyOpen" class="comment-view">
                  <div class="comment-meta-row">
                    <span class="comment-author">{{
                      commentItem.userName
                    }}</span>
                    <span class="comment-date">{{
                      commentItem.createdAt
                    }}</span>
                  </div>
                  <p class="comment-text">{{ commentItem.content }}</p>
                  <div
                    v-if="commentItem.userId == authStore.user.userId"
                    class="comment-actions"
                  >
                    <button
                      class="btn-comment-action"
                      @click="openModifyComment(commentItem)"
                    >
                      수정
                    </button>
                    <span class="action-divider"></span>
                    <button
                      class="btn-comment-action btn-comment-danger"
                      @click="removeComment(commentItem)"
                    >
                      삭제
                    </button>
                  </div>
                </div>

                <!-- 수정 모드 -->
                <div v-if="commentItem.modifyOpen" class="comment-edit-wrap">
                  <div class="comment-edit-label">댓글 수정 중...</div>
                  <div class="comment-meta-row">
                    <span class="comment-author">{{
                      commentItem.userName
                    }}</span>
                    <span class="comment-date">{{
                      commentItem.createdAt
                    }}</span>
                  </div>
                  <el-input
                    type="textarea"
                    :autosize="{ minRows: 2 }"
                    v-model="commentItem.content"
                    class="edit-textarea"
                  />
                  <div class="comment-edit-actions">
                    <el-button size="small" @click="cancelModify(commentItem)"
                      >취소</el-button
                    >
                    <el-button
                      type="primary"
                      size="small"
                      @click="modifyComment(commentItem)"
                      >수정완료</el-button
                    >
                  </div>
                </div>
              </div>

              <!-- 댓글 없음 -->
              <div v-if="commentList.length === 0" class="comment-empty">
                <svg
                  class="w-8 h-8 text-gray-300 mb-2"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="1.5"
                    d="M8 10h.01M12 10h.01M16 10h.01M21 16c0 1.1-.9 2-2 2H5l-4 4V6c0-1.1.9-2 2-2h16c0 1.1.9 2 2 2v10z"
                  />
                </svg>
                <span>첫 번째 댓글을 남겨보세요.</span>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { onBeforeMount, ref, computed } from "vue";
import { useDocumentStore } from "../stores/document";
import { useAuthStore } from "../stores/auth";
import { usetaskKJHStore } from "../stores/taksKJH";
import { useAttachmentStore } from "../stores/attachment";
import { useRoute, useRouter } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import Swal from "sweetalert2";
import Editor from "../components/Editor.vue";
import { downloadFile } from "../utils/commonFunc";

const isRead = true;
const route = useRoute();
const router = useRouter();
const documentStore = useDocumentStore();
const authStore = useAuthStore();
const taskStore = usetaskKJHStore();
const attachmentStore = useAttachmentStore();
const sidebarOpen = ref(false);

const documentInfo = ref({});
const documentId = route.params.documentId;
const projectId = route.params.projectId;
const subId = route.params.subProjectId;
let taskPjList = ref([]);
const attachmentList = ref([]);

const commentList = ref([]);
const comment = ref();
const modifyOpen = ref(false);

let name = ref();
let projectStartDate = ref();
let projectendDate = ref();

const goBack = () => {
  router.push({
    name: "documentList",
    params: { projectId: projectId, subProjectId: subId },
  });
};

const modifyDocument = () => {
  router.push({
    name: "documentRegister",
    params: {
      projectId: projectId,
      subProjectId: subId,
      documentId: documentId,
    },
  });
};

const delDocument = async () => {
  const result = await Swal.fire({
    title: "정말 삭제하시겠습니까?",
    text: "삭제된 문서는 목록에서 보이지 않습니다.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "삭제",
    cancelButtonText: "취소",
    reverseButtons: true,
  });
  if (!result.isConfirmed) return;

  let obj = {
    documentId: documentId,
    isDeleted: "Q1",
    isEditorUserId: authStore.user.userId,
  };

  const formData = new FormData();

  formData.append(
    "obj",
    new Blob([JSON.stringify(obj)], {
      type: "application/json",
    }),
  );

  await documentStore.modifyDocument(formData);

  await Swal.fire({
    title: "삭제를 완료했습니다.",
    icon: "success",
    confirmButtonText: "확인",
  });
  router.push({
    name: "documentList",
    params: { projectId: projectId, subProjectId: subId },
  });
};

const registerComment = async () => {
  if (!comment.value || comment.value == " ") {
    await Swal.fire({
      title: "댓글을 작성해주세요",
      icon: "error",
      confirmButtonText: "확인",
    });
    return;
  }
  const result = await Swal.fire({
    title: "댓글을 등록하시겠습니까?",
    icon: "question",
    showCancelButton: true,
    confirmButtonText: "확인",
    cancelButtonText: "취소",
    reverseButtons: true,
  });
  if (!result.isConfirmed) return;

  let user = authStore.user;
  let commentInfo = {
    documentId: documentId,
    userId: user.userId,
    content: comment.value,
  };
  await documentStore.registerComment(commentInfo);
  commentList.value = documentStore.registeredComment;
  comment.value = null;
};

const openModifyComment = (comment) => {
  comment.modifyOpen = true;
};

const modifyComment = async (comment) => {
  const result = await Swal.fire({
    title: "댓글을 수정하시겠습니까?",
    icon: "question",
    showCancelButton: true,
    confirmButtonText: "확인",
    cancelButtonText: "취소",
    reverseButtons: true,
  });
  if (!result.isConfirmed) return;

  let obj = {
    documentId: comment.documentId,
    documentCommentId: comment.documentCommentId,
    content: comment.content,
    editorUserId: authStore.user.userId,
  };
  modifyOpen.value = false;
  await documentStore.modifyComment(obj);
  commentList.value = documentStore.registeredComment;
};

const cancelModify = (comment) => {
  comment.modifyOpen = false;
};

const removeComment = async (comment) => {
  const result = await Swal.fire({
    title: "댓글을 삭제하시겠습니까?",
    icon: "question",
    showCancelButton: true,
    confirmButtonText: "확인",
    cancelButtonText: "취소",
    reverseButtons: true,
  });
  if (!result.isConfirmed) return;

  let obj = {
    documentId: comment.documentId,
    documentCommentId: comment.documentCommentId,
    editorUserId: authStore.user.userId,
    isDeleted: "O1",
  };
  await documentStore.modifyComment(obj);
  commentList.value = documentStore.registeredComment;
};

const attachmentDownload = async (file) => {
  await attachmentStore.downloadFile(file);
};

onBeforeMount(async () => {
  await documentStore.getDocumentById(documentId);
  documentInfo.value = documentStore.documentDetail.documentInfo.documentInfo;
  commentList.value = documentStore.documentDetail.documentInfo.commentInfo;
  attachmentList.value = documentStore.documentDetail.attachmentList;

  commentList.value.forEach((comment) => {
    comment.modifyOpen = false;
  });

  let id = subId ? subId : projectId;
  await taskStore.getProjectName(id);

  let roleObj = { projectId: projectId, subProjectId: subId };
  await taskStore.getProjectRole(roleObj);

  let projectInfo = taskStore.projectName;
  if (projectInfo.parentProjectName != null) {
    taskPjList.value = [projectInfo.parentProjectName, projectInfo.projectName];
  } else {
    taskPjList.value = [projectInfo.projectName];
  }

  name.value = projectInfo.projectName;
  projectStartDate.value = projectInfo.startDate;
  projectendDate.value = projectInfo.endDate;
});

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
</script>

<style scoped>
/* ── 공통 레이아웃 (notice 동일) ── */
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
  padding: 24px 30px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 상단 카드 */
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

/* 액션 버튼 묶음 */
.notice-action-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

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

.btn-edit {
  height: 38px;
  padding: 0 16px;
  border-radius: 10px;
  border: none;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
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
  height: 38px;
  padding: 0 16px;
  border-radius: 10px;
  border: none;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  background: #ef4444;
  color: #fff;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.2);
}
.btn-lock:hover {
  transform: translateY(-1px);
  background: #dc2626;
}

/* 공통 패널 */
.panel {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
  overflow: hidden;
}

/* ── 문서 본문 (notice-article 동일) ── */
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
.badge-category {
  background: #eff6ff;
  color: #1d4ed8;
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
  min-height: 120px;
  padding: 16px;
  background: #fff;
  border-radius: 10px;
  color: #334155;
  font-size: 15px;
  line-height: 1.95;
  word-break: break-word;
}

.doc-description {
  margin: 0;
  white-space: pre-line;
}

/* ── 댓글 패널 ── */
.comment-panel {
  overflow: hidden;
}

.comment-panel-header {
  padding: 18px 24px 16px;
  border-bottom: 1px solid #f0f2f5;
}
.comment-panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 700;
  color: #111827;
}
.comment-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 22px;
  height: 22px;
  padding: 0 6px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 12px;
  font-weight: 800;
}

/* 댓글 입력 */
.comment-input-wrap {
  padding: 20px 24px 16px;
  border-bottom: 1px solid #f0f2f5;
}
.comment-textarea {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  background: #f8fafc;
  font-size: 14px;
  color: #374151;
  resize: vertical;
  outline: none;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
  font-family: inherit;
  box-sizing: border-box;
}
.comment-textarea:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
  background: #fff;
}
.comment-input-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.comment-hint {
  font-size: 12px;
  color: #9ca3af;
}

.btn-comment-submit {
  height: 36px;
  padding: 0 20px;
  border-radius: 8px;
  border: none;
  background: #334155;
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-comment-submit:hover {
  background: #1e293b;
}

/* 댓글 목록 */
.comment-list-wrap {
  padding: 8px 24px 20px;
}

.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f2f5;
}
.comment-item:last-child {
  border-bottom: none;
}

.comment-view {
}
.comment-meta-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}
.comment-author {
  font-size: 14px;
  font-weight: 700;
  color: #111827;
}
.comment-date {
  font-size: 12px;
  color: #9ca3af;
}
.comment-text {
  margin: 0;
  font-size: 14px;
  color: #374151;
  line-height: 1.7;
}

.comment-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 6px;
  margin-top: 8px;
}
.btn-comment-action {
  background: none;
  border: none;
  font-size: 12px;
  color: #6b7280;
  cursor: pointer;
  padding: 2px 4px;
  transition: color 0.15s;
}
.btn-comment-action:hover {
  color: #111827;
}
.btn-comment-danger:hover {
  color: #ef4444;
}
.action-divider {
  width: 1px;
  height: 10px;
  background: #e5e7eb;
}

/* 댓글 수정 모드 */
.comment-edit-wrap {
  padding: 16px;
  background: #eff6ff;
  border: 1px solid #bfdbfe;
  border-radius: 10px;
}
.comment-edit-label {
  font-size: 11px;
  font-weight: 700;
  color: #1d4ed8;
  margin-bottom: 10px;
}
.comment-edit-actions {
  display: flex;
  justify-content: flex-end;
  gap: 6px;
  margin-top: 10px;
}
:deep(.el-button + .el-button) {
  margin-left: 0;
}

/* 댓글 없음 */
.comment-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #9ca3af;
  font-size: 14px;
}

/* ── 반응형 ── */
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
  .comment-panel-header,
  .comment-input-wrap,
  .comment-list-wrap {
    padding-left: 16px;
    padding-right: 16px;
  }
}

.fixed-badge {
  display: inline-flex;
  align-items: center;
  height: 22px;
  padding: 0 8px;
  border-radius: 999px;
  background: #fff1f2;
  border: 1px solid #fecdd3;
  color: #be123c;
  font-size: 11px;
  font-weight: 700;
}
</style>
