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
            v-if="meetingInfo.isDeleted === 'O1'"
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
                회의록 상세
              </h2>

              <div class="proj-name-row">
                <span class="proj-name"
                  >【 {{ meetingInfo.projectName }} 】</span
                >
                <span class="proj-period">
                  {{ meetingInfo.startDate }} ~ {{ meetingInfo.endDate }}
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
              <h4>{{ meetingInfo.title }}</h4>
            </div>
            <div class="grid grid-cols-3 gap-6 mb-8">
              <div class="flex flex-row gap-10">
                <label class="block text-base font-semibold mb-1"
                  >카테고리</label
                >
                <span>{{
                  meetingInfo.roleName == null ? "전체" : meetingInfo.roleName
                }}</span>
              </div>
              <div class="flex flex-row gap-10">
                <label class="block text-base font-semibold mb-1">작성자</label>
                <span>{{ meetingInfo.userName }}</span>
              </div>
              <div class="flex flex-row gap-10">
                <label class="block text-base font-semibold mb-1">등록일</label>
                <span>{{ meetingInfo.createdAt }}</span>
              </div>
            </div>

            <div class="mb-6">
              <label class="block text-base font-semibold mb-1"
                >문서 설명</label
              >
              <textarea
                rows="5"
                class="input w-full"
                :value="meetingInfo.content"
                disabled
              />
            </div>
            <div class="my-6">
              <label class="block text-base font-semibold mb-1">첨부파일</label>

              <div
                class="border-t border-b border-gray-200 divide-y divide-gray-100"
              >
                <div
                  v-for="(file, index) in attachmentList"
                  :key="index"
                  class="py-2 flex items-center justify-between group"
                >
                  <div class="flex items-center gap-2 flex-1">
                    <svg
                      class="w-5 h-5 text-gray-500 shrink-0"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z"
                      ></path>
                    </svg>

                    <div
                      class="flex items-center gap-1 cursor-pointer"
                      @click="attachmentDownload(file)"
                    >
                      <span
                        class="text-[14px] font-medium text-gray-700 group-hover:text-blue-600 transition-colors my-1"
                      >
                        {{ file.fileName }}
                      </span>
                    </div>
                  </div>

                  <div class="flex items-center gap-6">
                    <button
                      @click="handleDownload(file)"
                      class="text-[13px] text-gray-600 hover:text-blue-600 flex items-center gap-1"
                    >
                      다운로드 <span class="text-[10px] text-gray-400">〉</span>
                    </button>
                  </div>
                </div>

                <div
                  v-if="!attachmentList || attachmentList.length === 0"
                  class="py-4 text-center text-sm text-gray-400"
                >
                  등록된 서류가 없습니다.
                </div>
              </div>
            </div>

            <div class="flex flex-row justify-between">
              <div class="flex flex-row gap-10">
                <button @click="goBack" type="button" class="btn-navy">
                  ← 목록으로
                </button>
              </div>
              <div class="flex flex-row gap-2">
                <button @click="modifyDocument" class="btn-green">수정</button>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { onBeforeMount, ref } from "vue";
import { useMeetingStore } from "../stores/meeting";
import { useAttachmentStore } from "../stores/attachment";
import { useRoute, useRouter } from "vue-router";
import Sidebar from "../partials/Sidebar.vue";
import Header from "../partials/Header.vue";

const route = useRoute();
const router = useRouter();
const meetingStore = useMeetingStore();
const attachmentStore = useAttachmentStore();
const sidebarOpen = ref(false);

const meetingInfo = ref({});
const attachmentList = ref([]);
const meetingId = route.params.meetingId;
const projectId = route.params.projectId;

// 목록으로
const goBack = () => {
  console.log(projectId);
  router.push({
    name: "meetingList",
    params: { projectId: projectId },
  });
};

// 수정 버튼
const modifyDocument = () => {
  router.push({
    name: "meetingRegister",
    params: { projectId: projectId, meetingId: meetingId },
  });
};

// 파일 다운로드
const attachmentDownload = async (file) => {
  console.log(file);
  await attachmentStore.downloadFile(file);
};

onBeforeMount(async () => {
  // 문서 및 프로젝트 정보
  await meetingStore.getMeetingById(meetingId);
  meetingInfo.value = meetingStore.meetingDetail.meetingList;
  attachmentList.value = meetingStore.meetingDetail.attachmentList;
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

/* 1. 파일 목록 전체 감싸는 영역 (위아래 선) */
.attachment-container {
  border-top: 1px solid #e5e7eb;
  border-bottom: 1px solid #e5e7eb;
  margin-top: 1rem;
}

/* 2. 각 파일 행 (간격 좁게) */
.file-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0; /* 사진처럼 촘촘한 간격 */
  border-bottom: 1px solid #f3f4f6;
}

.file-row:last-child {
  border-bottom: none;
}

/* 3. 파일 이름 & 메타 정보 (왼쪽 정렬) */
.file-link-group {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex: 1; /* 왼쪽으로 바짝 붙게 함 */
}

.file-name-text {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.file-meta-text {
  font-size: 13px;
  color: #9ca3af; /* 사진 속 회색 느낌 */
}

/* 4. 우측 액션 버튼 (다운로드, 바로보기) */
.action-group {
  display: flex;
  gap: 20px; /* 버튼 사이 간격 */
}

.action-btn {
  font-size: 13px;
  color: #4b5563;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.2s;
}

.action-btn:hover {
  color: #2563eb; /* 호버 시 파란색 */
}

.arrow-icon {
  font-size: 10px;
  color: #d1d5db; /* 화살표는 연하게 */
  font-family: sans-serif;
}
</style>
