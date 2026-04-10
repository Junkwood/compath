<template>
  <div class="flex min-h-screen overflow-hidden">
    <Sidebar :sidebarOpen="sidebarOpen" @close-sidebar="sidebarOpen = false" />

    <div
      class="relative flex flex-col flex-1 overflow-y-auto overflow-x-hidden"
    >
      <!-- Header -->
      <Header
        :sidebarOpen="sidebarOpen"
        @toggle-sidebar="sidebarOpen = !sidebarOpen"
      />

      <main class="grow">
        <div class="px-4 sm:px-6 lg:px-8 py-8 w-full max-w-9xl mx-auto">
          <el-alert
            v-if="documentInfo.isDeleted === 'O1'"
            title="비활성화된 게시글입니다."
            type="warning"
            description="관리자만 열람 가능하며 일반 사용자에게는 노출되지 않습니다."
            show-icon
            :closable="false"
            class="mb-4"
          />
          <!-- projectDashboard.vue와 동일한 제목 영역 -->
          <div class="mb-6 proj-title-row flex justify-between">
            <div class="proj-title-left">
              <h2
                class="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-bold"
              >
                문서 상세
              </h2>

              <div class="proj-name-row">
                <span class="proj-name"
                  >【 {{ documentInfo.projectName }} 】</span
                >
                <span class="proj-period">
                  {{ documentInfo.startDate }} ~ {{ documentInfo.endDate }}
                </span>
              </div>
            </div>
          </div>

          <div
            class="col-span-full xl:col-span-8 bg-white dark:bg-gray-800 shadow-xs rounded-xl mb-0 p-6"
          >
            <div
              class="text-xl md:text-2xl text-gray-800 dark:text-gray-100 font-bold mb-8"
            >
              <h4>{{ documentInfo.title }}</h4>
            </div>
            <div class="grid grid-cols-3 gap-6 mb-8">
              <div class="flex flex-row gap-10">
                <label class="block text-base font-semibold mb-1"
                  >카테고리</label
                >
                <span>{{
                  documentInfo.roleName == null ? "전체" : documentInfo.roleName
                }}</span>
              </div>
              <div class="flex flex-row gap-10">
                <label class="block text-base font-semibold mb-1">작성자</label>
                <span>{{ documentInfo.userName }}</span>
              </div>
              <div class="flex flex-row gap-10">
                <label class="block text-base font-semibold mb-1">등록일</label>
                <span>{{ documentInfo.createdAt }}</span>
              </div>
            </div>

            <div class="mb-6">
              <label class="block text-base font-semibold mb-1"
                >문서 설명</label
              >
              <textarea
                rows="5"
                class="input w-full"
                :value="documentInfo.content"
                disabled
              />
            </div>

            <div class="flex flex-row justify-between">
              <div class="flex flex-row gap-10">
                <button @click="goBack" type="button" class="btn-navy">
                  ← 목록으로
                </button>
              </div>
              <div class="flex flex-row gap-2">
                <button @click="modifyDocument" class="btn-green">수정</button>
                <button
                  @click="delDocument"
                  class="btn-red"
                  v-if="commentList.length == 0"
                >
                  삭제
                </button>
              </div>
            </div>
          </div>
          <div
            v-if="documentInfo.isComment == 'O1'"
            class="filter-card mt-4 mb-0"
          >
            <!-- 검색어 -->
            <div class="filter-item filter-item--wide mt-3">
              <div class="search-wrap">
                <input
                  v-model="comment"
                  type="text"
                  placeholder="댓글을 입력해주세요."
                  class="search-input"
                  @keyup.enter="registerComment()"
                />
                <div class="filter-actions">
                  <button
                    type="button"
                    @click="registerComment()"
                    class="btn-search"
                  >
                    등록
                  </button>
                </div>
              </div>
              <!-- 버튼 -->
            </div>
            <div class="comment-list" v-for="comment in commentList">
              <div class="flex gap-4 pt-4 pb-2 border-b-2 border-gray-100">
                <div class="flex-1">
                  <div v-if="!comment.modifyOpen">
                    <div class="flex items-center gap-2 mb-1">
                      <el-text size="large" strong>{{
                        comment.userName
                      }}</el-text>
                      <el-text size="small" type="info">{{
                        comment.createdAt
                      }}</el-text>
                    </div>
                    <el-text class="block leading-relaxed">{{
                      comment.content
                    }}</el-text>

                    <div class="flex flex-row-reverse mt-2">
                      <el-button
                        link
                        type="danger"
                        size="small"
                        @click="removeComment(comment)"
                        >삭제</el-button
                      >
                      <el-button
                        type="button"
                        @click="openModifyComment(comment)"
                        link
                        size="small"
                        >수정</el-button
                      >
                    </div>
                  </div>
                  <div
                    class="p-4 bg-indigo-50/50 border-2 border-indigo-200 rounded-lg transition-all"
                    v-if="comment.modifyOpen"
                  >
                    <div
                      class="flex items-center gap-2 mb-2 text-indigo-600 font-bold text-xs"
                    >
                      <i class="el-icon-edit"></i> 댓글 수정 중...
                    </div>

                    <div class="flex-1">
                      <div class="flex items-center gap-2 mb-1">
                        <el-text size="large" strong>{{
                          comment.userName
                        }}</el-text>
                        <el-text size="small" type="info">{{
                          comment.createdAt
                        }}</el-text>
                      </div>
                      <el-input
                        type="textarea"
                        :autosize="{ minRows: 2 }"
                        v-model="comment.content"
                        class="edit-textarea"
                      />
                      <div class="flex flex-row-reverse mt-2 gap-1">
                        <el-button size="small" @click="cancelModify(comment)"
                          >취소</el-button
                        >
                        <el-button
                          type="primary"
                          size="small"
                          @click="modifyComment(comment)"
                          >수정완료</el-button
                        >
                      </div>
                    </div>
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
import { onBeforeMount, ref, watch } from "vue";
import { useDocumentStore } from "../stores/document";
import { useAuthStore } from "../stores/auth";
import { useRoute, useRouter } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import Swal from "sweetalert2";

const route = useRoute();
const router = useRouter();
const documentStore = useDocumentStore();
const authStore = useAuthStore();
const sidebarOpen = ref(false);

const documentInfo = ref({});
const documentId = route.params.documentId;
const projectId = route.params.projectId;

const commentList = ref([]); // 댓글목록
const comment = ref(); // 댓글
const modifyOpen = ref(false);

// 목록으로
const goBack = () => {
  console.log(projectId);
  router.push({
    name: "documentList",
    params: { projectId: projectId },
  });
};

// 수정 버튼
const modifyDocument = () => {
  router.push({
    name: "documentRegister",
    params: { projectId: projectId, documentId: documentId },
  });
};

// 삭제 버튼
const delDocument = async () => {
  const result = await Swal.fire({
    title: "정말 삭제하시겠습니까?",
    text: "비활성한 문서는 목록에서 보이지 않습니다.",
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

  await documentStore.modifyDocument(obj);

  await Swal.fire({
    title: "삭제를 완료했습니다.",
    icon: "success",
    confirmButtonText: "확인",
    reverseButtons: true,
  });

  router.push({
    name: "documentList",
    params: { projectId: projectId },
  });
};

// 댓글 등록
const registerComment = async () => {
  // 작성한 내용 없을 경우 알림창
  if (!comment.value || comment.value == " ") {
    const result = await Swal.fire({
      title: "댓글을 작성해주세요",
      icon: "error",
      confirmButtonText: "확인",
      reverseButtons: true,
    });

    if (!result.isConfirmed) return;
    return;
  }

  const result = await Swal.fire({
    title: "댓글을 등록하시겠습니까??",
    icon: "question",
    showCancelButton: true,
    confirmButtonText: "확인",
    cancelButtonText: "취소",
    reverseButtons: true,
  });

  if (!result.isConfirmed) return;

  let user = authStore.user; // 로그인한 사람 정보

  let commentInfo = {
    // 백으로 보낼 정보
    documentId: documentId,
    userId: user.userId,
    content: comment.value,
  };

  await documentStore.registerComment(commentInfo);
  commentList.value = documentStore.registeredComment;
  comment.value = null;
};

// 댓글 수정
const openModifyComment = (comment) => {
  console.log(comment);
  comment.modifyOpen = true;
};

const modifyComment = async (comment) => {
  const result = await Swal.fire({
    title: "댓글을 수정하시겠습니까??",
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

// 댓글 수정 취소
const cancelModify = (comment) => {
  comment.modifyOpen = false;
};

// 댓글 삭제
const removeComment = async (comment) => {
  const result = await Swal.fire({
    title: "댓글을 삭제하시겠습니까??",
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

onBeforeMount(async () => {
  // 문서 및 프로젝트 정보
  await documentStore.getDocumentById(documentId);
  documentInfo.value = documentStore.documentDetail.documentInfo;
  commentList.value = documentStore.documentDetail.commentInfo;

  commentList.value.forEach((comment) => {
    comment.modifyOpen = false;
  });
});
</script>
<style scoped>
/* 상단 */
.proj-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}

.proj-title-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.proj-name-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.proj-name {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
  letter-spacing: -0.02em;
}

.proj-period {
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
}
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
/* 목록으로 */
.btn-navy {
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  background: #1e3a5f;
  color: #fff;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(30, 58, 95, 0.25);
}
.btn-navy:hover {
  background: #162d4a;
  box-shadow: 0 4px 10px rgba(30, 58, 95, 0.3);
  transform: translateY(-1px);
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

/* 비활성 버튼 */
.btn-red {
  height: 38px;
  padding: 0 20px;
  font-size: 13px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  border: none;
  background: #dc2626;
  color: #fff;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(220, 38, 38, 0.25);
  letter-spacing: 0.01em;
}
.btn-red:hover {
  background: #b91c1c;
  box-shadow: 0 4px 10px rgba(220, 38, 38, 0.3);
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
.filter-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.filter-row {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
  min-width: 120px;
  flex: 1;
}

.filter-item--wide {
  flex: 2;
  min-width: 180px;
}

.filter-label {
  font-size: 0.72rem;
  font-weight: 600;
  color: #6b7280;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

/* ── 검색어 ── */
.search-wrap {
  position: relative;
  display: flex;
  align-items: center;
}
.search-input {
  width: 100%;
  height: 100px;
  padding: 8px 10px 70px 8px;
  margin-right: 3px;
  border: 1px solid #d1d5db;
  border-radius: 7px;
  font-size: 0.85rem;
  color: #374151;
  background: #f9fafb;
  outline: none;
  transition:
    border-color 0.15s,
    box-shadow 0.15s;
}
.search-input:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
  background: #fff;
}

/* ── 버튼 ── */
.filter-actions {
  display: flex;
  gap: 8px;
  padding-bottom: 1px;
}
.btn-search {
  flex: 1;
  padding: 8px 20px;
  background: #334155;
  color: #fff;
  font-size: 0.85rem;
  font-weight: 600;
  border-radius: 7px;
  border: none;
  cursor: pointer;
  transition: background 0.15s;
  white-space: nowrap;
  height: 100px;
}
.btn-search:hover {
  background: #1e293b;
}

:deep(.el-button + .el-button) {
  margin-left: 0px;
}
</style>
