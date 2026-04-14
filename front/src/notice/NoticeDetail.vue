<template>
  <div class="dashboard-page flex min-h-screen overflow-hidden">
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
            <span> {{ noticeInfo.projectName }}</span>
            <span class="bc-sep">›</span>
            <span class="bc-cur"> 공지사항 상세</span>
          </div>
        </div>

        <div class="page-container">
          <el-alert
            v-if="noticeInfo.isDeleted === 'Q1'"
            title="비활성화된 게시글입니다."
            type="warning"
            description="관리자만 열람 가능하며 일반 사용자에게는 노출되지 않습니다."
            show-icon
            :closable="false"
            class="top-alert"
          />

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

            <button @click="goBack" type="button" class="btn-back-top">
              목록으로
            </button>
          </div>

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

                <div class="notice-action-wrap">
                  <button
                    v-if="noticeInfo.isDeleted === 'Q2'"
                    @click="modifyNotice"
                    class="btn-edit"
                  >
                    수정
                  </button>

                  <button @click="lockNotice" class="btn-lock">
                    <el-icon><Lock /></el-icon>
                    <span>{{
                      noticeInfo.isDeleted === "Q1" ? "비활성 해제" : "비활성"
                    }}</span>
                  </button>
                </div>
              </div>

              <h2 class="notice-title">{{ noticeInfo.title }}</h2>

              <div class="notice-meta">
                <span class="meta-item">
                  <span class="meta-label">작성자</span>
                  <span class="meta-value">{{ noticeInfo.userName || "-" }}</span>
                </span>
                <span class="meta-divider"></span>
                <span class="meta-item">
                  <span class="meta-label">등록일</span>
                  <span class="meta-value">{{ noticeInfo.createdAt || "-" }}</span>
                </span>
              </div>

              <div class="notice-divider"></div>

              <div class="notice-content-wrap">
                <div class="notice-content">
                  {{ noticeInfo.content || "내용이 없습니다." }}
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
import { useProjectKJHStore } from "../stores/projectKJH";
import { useNoticeStore } from "../stores/notice";
import { useRoute, useRouter } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";
import { Lock } from "@element-plus/icons-vue";
import Swal from "sweetalert2";

const route = useRoute();
const router = useRouter();
const noticeStore = useNoticeStore();
const projectStore = useProjectKJHStore();

const noticeInfo = ref({});
const noticeId = route.params.noticeId;
const projectId = route.params.projectId;

const memberList = ref([]); // 구성원 테이블

// 목록으로
const goBack = () => {
  router.back();
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

onBeforeMount(async () => {
  // 공지사항 및 프로젝트 정보
  await noticeStore.getNoticeById(noticeId);
  noticeInfo.value = noticeStore.noticeInfo;
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
  padding: 12px 24px;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 10;
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
  padding: 24px;
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
  align-items: flex-start;
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
